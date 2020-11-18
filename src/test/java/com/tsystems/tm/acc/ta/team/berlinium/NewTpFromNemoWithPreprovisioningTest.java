package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilea10nsp.A4NetworkServiceProfileA10NspCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilel2bsa.A4NetworkServiceProfileL2BsaCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4PreProvisioningRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceRobot;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.*;

import java.util.concurrent.TimeUnit;

import static com.tsystems.tm.acc.ta.data.berlinium.BerliniumConstants.*;

@ServiceLog(A4_RESOURCE_INVENTORY)
@ServiceLog(A4_RESOURCE_INVENTORY_SERVICE)
public class NewTpFromNemoWithPreprovisioningTest extends ApiTest {

    private final long SLEEP_TIMER = 5; // in seconds

    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceInventoryServiceRobot a4ResourceInventoryService = new A4ResourceInventoryServiceRobot();
    private final A4PreProvisioningRobot a4PreProvisioning = new A4PreProvisioningRobot();
    private final A4ResourceInventoryRobot a4ResourceInventory = new A4ResourceInventoryRobot();
    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData;
    private A4NetworkElementPort nepData;
    private A4TerminationPoint tpFtthData;
    private A4TerminationPoint tpA10Data;
    private A4TerminationPoint tpL2Data;

    // Initialize with dummy wiremock so that cleanUp() call within init() doesn't run into nullpointer
    private WireMockMappingsContext wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "")).build();

    @BeforeClass
    public void init() {
        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        neData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        nepData = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
        tpFtthData = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointFtthAccess);
        tpA10Data = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointA10Nsp);
        tpL2Data = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointL2Bsa);

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
        wiremock.publish();
    }

    @AfterMethod
    public void cleanup() {
        wiremock.deleteAll();

        a4ResourceInventory.deleteA4TestData(negData, neData);
    }

    @Test(description = "DIGIHUB-xxxxx NEMO creates new Termination Point with FTTH Accesss Preprovisioning")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("NEMO creates new Termination Point with FTTH Accesss Preprovisioning")
    public void newTpWithFtthAccessPreprovisioning() throws InterruptedException {
        // WHEN / Action
        a4ResourceInventoryService.createTerminationPoint(tpFtthData, nepData);
        TimeUnit.SECONDS.sleep(SLEEP_TIMER); // Wait a bit because queues might need some time to process all events

        // THEN
        a4PreProvisioning.checkPostToPreprovisioningWiremock();
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
                .get(A4NetworkServiceProfileA10NspCase.defaultNetworkServiceProfileA10Nsp);
        a4ResourceInventory.createTerminationPoint(tpA10Data,nepData);
        a4ResourceInventory.createNetworkServiceProfileA10Nsp(nspA10Data, tpA10Data);

        // WHEN / Action
        a4ResourceInventoryService.createTerminationPoint(tpA10Data, nepData);
        TimeUnit.SECONDS.sleep(SLEEP_TIMER); // Wait a bit because queues might need some time to process all events

        // THEN
        a4ResourceInventory.checkNetworkServiceProfileA10NspConnectedToTerminationPointExists(tpA10Data.getUuid(), 1);
        a4NemoUpdater.checkNetworkServiceProfileA10NspPutRequestToNemoWiremockDidntHappen(tpA10Data.getUuid());
    }

    @Test(description = "DIGIHUB-xxxxx NEMO creates new Termination Point (L2BSA) with TP and NSP already existing in inventory")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("NEMO creates new Termination Point (L2BSA) with TP and NSP already existing in inventory")
    public void newTpWithL2BsaPreprovisioningWithExistingTerminationPointAndNsp() throws InterruptedException {
        // GIVEN /Arrange
        A4NetworkServiceProfileL2Bsa nspL2Data = osrTestContext.getData().getA4NetworkServiceProfileL2BsaDataProvider()
                .get(A4NetworkServiceProfileL2BsaCase.defaultNetworkServiceProfileL2Bsa);
        a4ResourceInventory.createTerminationPoint(tpL2Data,nepData);
        a4ResourceInventory.createNetworkServiceProfileL2Bsa(nspL2Data, tpL2Data);

        // WHEN / Action
        a4ResourceInventoryService.createTerminationPoint(tpL2Data, nepData);
        TimeUnit.SECONDS.sleep(SLEEP_TIMER); // Wait a bit because queues might need some time to process all events

        // THEN
        a4ResourceInventory.checkNetworkServiceProfileL2BsaConnectedToTerminationPointExists(tpL2Data.getUuid(), 1);
        a4NemoUpdater.checkNetworkServiceProfileL2BsaPutRequestToNemoWiremockDidntHappen(tpL2Data.getUuid());
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
