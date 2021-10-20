package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilea10nsp.A4NetworkServiceProfileA10NspCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilel2bsa.A4NetworkServiceProfileL2BsaCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.*;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_MS;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_SERVICE_MS;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;

@ServiceLog({A4_RESOURCE_INVENTORY_MS,A4_RESOURCE_INVENTORY_SERVICE_MS,A4_CARRIER_MANAGEMENT_MS,A4_NEMO_UPDATER_MS})
public class NewTpFromNemoWithPreprovisioningTest extends GigabitTest {

    private final long SLEEP_TIMER = 5; // in seconds

    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceInventoryServiceRobot a4ResourceInventoryService = new A4ResourceInventoryServiceRobot();
    private final WgA4PreProvisioningWiremockRobot a4PreProvisioning = new WgA4PreProvisioningWiremockRobot();
    private final A4ResourceInventoryRobot a4ResourceInventory = new A4ResourceInventoryRobot();
    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();
    private final A4ResilienceRobot a4Resilience = new A4ResilienceRobot();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData;
    private A4NetworkElementPort nepData;
    private A4TerminationPoint tpFtthData;
    private A4TerminationPoint tpA10Data;
    private A4TerminationPoint tpL2Data;
    private String routeName;

    // Initialize with dummy wiremock so that cleanUp() call within init() doesn't run into nullpointer
    private WireMockMappingsContext wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "")).build();

    @BeforeClass
    public void init() throws IOException {
        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        neData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        nepData = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
        tpFtthData = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.terminationPointFtthAccessPrePro);
        tpA10Data = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.terminationPointA10NspPrePro);
        tpL2Data = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.terminationPointL2BsaPrePro);
        routeName = "resource-order-resource-inventory.v1.a4TerminationPoints";

        // Ensure that no old test data is in the way
        cleanup();
    }

    @BeforeMethod
    public void setup() {
        a4ResourceInventory.createNetworkElementGroup(negData);
        a4ResourceInventory.createNetworkElement(neData, negData);
        a4ResourceInventory.createNetworkElementPort(nepData, neData);

        wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "NewTpFromNemoWithPreprovisioningTest"))
                .addWgA4ProvisioningMock()
                .addNemoMock()
                .build();
        wiremock.publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());
    }

    @AfterMethod
    public void cleanup() throws IOException {
        wiremock.close();
        wiremock
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());

        a4ResourceInventory.deleteA4TestDataRecursively(negData);

        a4Resilience.changeRouteToA4ResourceInventoryService(routeName);
    }

    @Test(description = "DIGIHUB-xxxxx NEMO creates new Termination Point with failed-and-retried FTTH Accesss Preprovisioning")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("NEMO creates new Termination Point with failed-and-retried FTTH Accesss Preprovisioning")
    public void newTpWithFailedAndRetriedFtthAccessPreprovisioning() throws InterruptedException, IOException {
        // GIVEN / Arrange
        final long REDELIVERY_DELAY = a4Resilience.getRedeliveryDelayCarrierManagement();

        // WHEN / Action
        a4ResourceInventoryService.createTerminationPoint(tpFtthData, nepData);

        // THEN / Assert
        a4PreProvisioning.checkPostToPreprovisioningWiremock();

        // because the wiremock answers with 500, nsp should not be created
        a4ResourceInventory.checkNetworkServiceProfileFtthAccessConnectedToTerminationPointExists(tpFtthData.getUuid(), 0);

        // after a little time it is trying to redeliver to wiremock it should answer with 201...

        TimeUnit.MILLISECONDS.sleep(REDELIVERY_DELAY + 15000);

        // ... and create nsp
        a4ResourceInventory.checkNetworkServiceProfileFtthAccessConnectedToTerminationPointExists(tpFtthData.getUuid(), 1);

        // Deactivated this check, because wiremock currently supports only one webhook callback. We'd need a 2nd one for this check to be successful
//        a4NemoUpdater.checkNetworkServiceProfileFtthAccessPutRequestToNemoWiremock(tpFtthData.getUuid());
    }

    @Test(description = "DIGIHUB-xxxxx NEMO creates new Termination Point with A10NSP Preprovisioning")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("NEMO creates new Termination Point with A10NSP Preprovisioning")
    public void newTpWithA10NspPreprovisioning() throws InterruptedException {
        // WHEN / Action
        a4ResourceInventoryService.createTerminationPoint(tpA10Data, nepData);
        TimeUnit.SECONDS.sleep(SLEEP_TIMER); // Wait a bit because queues might need some time to process all events

        // THEN
        a4ResourceInventory.checkNetworkServiceProfileA10NspConnectedToTerminationPointExists(tpA10Data.getUuid(), 1);
        a4NemoUpdater.checkNetworkServiceProfileA10NspPutRequestToNemoWiremock(tpA10Data.getUuid());
    }

    @Test(description = "DIGIHUB-xxxxx NEMO creates new Termination Point with A10NSP Preprovisioning, but resource-inventory is not reachable")
    @Owner("thea.john@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("NEMO creates new Termination Point with A10NSP Preprovisioning")
    public void newTpWithA10NspPreprovisioningRedelivery() throws InterruptedException, IOException {
        // what to do before this test:
        //   edit yaml of service apigw
        //   add ports:
        //      - name: apigw-admin
        //      port: 81
        //      protocol: TCP
        //      targetPort: 8001
        //   then create new route called apigw-admin
        //   and uses new port mapping (81 -> 8001)

        String queue = "jms.queue.a10NspTP";
        String dlq = "jms.dlq.a10NspTP";
        a4Resilience.removeAllMessagesInQueue(dlq);
        a4Resilience.removeAllMessagesInQueue(queue);
        //BEFORE
        a4ResourceInventory.createTerminationPoint(tpA10Data, nepData);
            // change kong route so wiremock is used for TerminationPoint, because that is the first request for preprovisioning
        a4Resilience.changeRouteToWiremock(routeName);
            // make wiremock return 500 for findTerminationPoint
            //and return 201 for put TerminationPoint
        wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "NewTpFromNemoWithPreprovisioningTest"))
                .addA4ResourceInventoryMock500()
                .addA4ResourceInventoryMock201()
                .build();
        wiremock.publish();

        // WHEN / Action
            // start preprovisioning by creating TP and wait a little bit
        TimeUnit.SECONDS.sleep(SLEEP_TIMER);
        a4ResourceInventoryService.createTerminationPoint(tpA10Data, nepData);
        TimeUnit.SECONDS.sleep(SLEEP_TIMER); // Wait a bit because queues might need some time to process all events

        // THEN
            // check if message is still waiting in queue
        a4Resilience.checkMessagesInQueue(queue, 1);
        int old = a4Resilience.countMessagesInQueue(dlq);

        //AFTER
            // change route back over kong to a4-resource-inventory
        a4Resilience.changeRouteToA4ResourceInventoryService(routeName);

            // wait time of redelivery and check if message it out of queue and
        long sleepTime = a4Resilience.getRedeliveryDelayCarrierManagement();
        TimeUnit.MILLISECONDS.sleep(sleepTime);

        a4Resilience.checkMessagesInQueue(queue, 0);
        a4Resilience.checkMessagesInQueue(dlq, old);
    }

    @Test(description = "DIGIHUB-xxxxx NEMO creates new Termination Point with L2BSA Preprovisioning")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("NEMO creates new Termination Point with L2BSA Preprovisioning")
    public void newTpWithL2BsaPreprovisioning() throws InterruptedException {
        // WHEN / Action
        a4ResourceInventoryService.createTerminationPoint(tpL2Data, nepData);
        TimeUnit.SECONDS.sleep(SLEEP_TIMER); // Wait a bit because queues might need some time to process all events

        // THEN
        a4ResourceInventory.checkNetworkServiceProfileL2BsaConnectedToTerminationPointExists(tpL2Data.getUuid(), 1);
        a4NemoUpdater.checkNetworkServiceProfileL2BsaPutRequestToNemoWiremock(tpL2Data.getUuid());
    }

    @Test(description = "DIGIHUB-xxxxx NEMO creates new Termination Point (A10NSP) with TP and NSP already existing in inventory")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("NEMO creates new Termination Point (A10NSP) with TP and NSP already existing in inventory")
    public void newTpWithA10NspPreprovisioningWithExistingTerminationPointAndNsp() throws InterruptedException {
        // GIVEN /Arrange
        A4NetworkServiceProfileA10Nsp nspA10Data = osrTestContext.getData().getA4NetworkServiceProfileA10NspDataProvider()
                .get(A4NetworkServiceProfileA10NspCase.networkServiceProfileA10NspPrePro);
        a4ResourceInventory.createTerminationPoint(tpA10Data,nepData);
        a4ResourceInventory.createNetworkServiceProfileA10Nsp(nspA10Data, tpA10Data);

        // WHEN / Action
        a4ResourceInventoryService.createTerminationPoint(tpA10Data, nepData);
        TimeUnit.SECONDS.sleep(SLEEP_TIMER); // Wait a bit because queues might need some time to process all events

        // THEN
        a4ResourceInventory.checkNetworkServiceProfileA10NspConnectedToTerminationPointExists(tpA10Data.getUuid(), 1);
//        a4NemoUpdater.checkNetworkServiceProfileA10NspPutRequestToNemoWiremockDidntHappen(tpA10Data.getUuid());
        a4NemoUpdater.checkNetworkServiceProfileA10NspPutRequestToNemoWiremock(tpA10Data.getUuid());
    }

    @Test(description = "DIGIHUB-xxxxx NEMO creates new Termination Point (L2BSA) with TP and NSP already existing in inventory")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("NEMO creates new Termination Point (L2BSA) with TP and NSP already existing in inventory")
    public void newTpWithL2BsaPreprovisioningWithExistingTerminationPointAndNsp() throws InterruptedException {
        // GIVEN /Arrange
        A4NetworkServiceProfileL2Bsa nspL2Data = osrTestContext.getData().getA4NetworkServiceProfileL2BsaDataProvider()
                .get(A4NetworkServiceProfileL2BsaCase.networkServiceProfileL2BsaPrePro);
        a4ResourceInventory.createTerminationPoint(tpL2Data,nepData);
        a4ResourceInventory.createNetworkServiceProfileL2Bsa(nspL2Data, tpL2Data);

        // WHEN / Action
        a4ResourceInventoryService.createTerminationPoint(tpL2Data, nepData);
        TimeUnit.SECONDS.sleep(SLEEP_TIMER); // Wait a bit because queues might need some time to process all events

        // THEN
        a4ResourceInventory.checkNetworkServiceProfileL2BsaConnectedToTerminationPointExists(tpL2Data.getUuid(), 1);
//        a4NemoUpdater.checkNetworkServiceProfileL2BsaPutRequestToNemoWiremockDidntHappen(tpL2Data.getUuid());
        a4NemoUpdater.checkNetworkServiceProfileL2BsaPutRequestToNemoWiremock(tpL2Data.getUuid());
    }

    @Test(description = "DIGIHUB-xxxxx NEMO creates new Termination Point (A10NSP) with TP already existing in inventory, NSP not existing")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("NEMO creates new Termination Point (A10NSP) with TP already existing in inventory, NSP not existing")
    public void newTpWithA10NspPreprovisioningWithExistingTerminationPoint() throws InterruptedException {
        // GIVEN /Arrange
        a4ResourceInventory.createTerminationPoint(tpA10Data,nepData);

        // WHEN / Action
        a4ResourceInventoryService.createTerminationPoint(tpA10Data, nepData);
        TimeUnit.SECONDS.sleep(SLEEP_TIMER); // Wait a bit because queues might need some time to process all events

        // THEN
        a4ResourceInventory.checkNetworkServiceProfileA10NspConnectedToTerminationPointExists(tpA10Data.getUuid(), 1);
        a4NemoUpdater.checkNetworkServiceProfileA10NspPutRequestToNemoWiremock(tpA10Data.getUuid());
    }

    @Test(description = "DIGIHUB-xxxxx NEMO creates new Termination Point (L2BSA) with TP already existing in inventory, NSP not existing")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("NEMO creates new Termination Point (L2BSA) with TP already existing in inventory, NSP not existing")
    public void newTpWithL2BsaPreprovisioningWithExistingTerminationPoint() throws InterruptedException {
        // GIVEN /Arrange
        a4ResourceInventory.createTerminationPoint(tpL2Data,nepData);

        // WHEN / Action
        a4ResourceInventoryService.createTerminationPoint(tpL2Data, nepData);
        TimeUnit.SECONDS.sleep(SLEEP_TIMER); // Wait a bit because queues might need some time to process all events

        // THEN
        a4ResourceInventory.checkNetworkServiceProfileL2BsaConnectedToTerminationPointExists(tpL2Data.getUuid(), 1);
        a4NemoUpdater.checkNetworkServiceProfileL2BsaPutRequestToNemoWiremock(tpL2Data.getUuid());
    }

}
