package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.ta.data.osr.models.A4TerminationPoint;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
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
    private final A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();
    private final A4ResourceInventoryServiceRobot a4Nemo = new A4ResourceInventoryServiceRobot();
    private final A4PreProvisioningRobot a4PreProvisioning = new A4PreProvisioningRobot();
    private final A4ResourceInventoryRobot a4ResourceInventory = new A4ResourceInventoryRobot();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData;
    private A4NetworkElementPort nepData;
    private A4TerminationPoint tpFtthData;
    private A4TerminationPoint tpA10Data;

    private WireMockMappingsContext mappingsContext;

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

        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "ResilienceTest")).build();

        // Ensure that no old test data is in the way
        cleanup();
    }

    @BeforeMethod
    public void setup() {
        a4Inventory.createNetworkElementGroup(negData);
        a4Inventory.createNetworkElement(neData, negData);
        a4Inventory.createNetworkElementPort(nepData, neData);

        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "NewTpFromNemoWithPreprovisioningTest"))
                .addWgA4ProvisioningMock()
                .build();
        mappingsContext.publish();
    }

    @AfterMethod
    public void cleanup() {
        mappingsContext.deleteAll();
//        mappingsContext.close();

        a4Inventory.deleteA4TestData(negData, neData);
    }

    @Test(description = "DIGIHUB-xxxxx NEMO creates new Termination Point with FTTH Accesss Preprovisioning")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("NEMO creates new Termination Point with FTTH Accesss Preprovisioning")
    public void newTpWithFtthAccessPreprovisioning() throws InterruptedException {

//        mappingsContext.deleteAll();
//        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "NewTpFromNemoWithPreprovisioningTest"))
//                .addWgA4ProvisioningMock()
//                .build();
//        mappingsContext.publish();
//        TimeUnit.SECONDS.sleep(SLEEP_TIMER); // Need to wait a bit because queues might need some time to process all events

        // WHEN / Action
        a4Nemo.createTerminationPoint(tpFtthData, nepData);
        TimeUnit.SECONDS.sleep(SLEEP_TIMER); // Need to wait a bit because queues might need some time to process all events

        // THEN
        a4PreProvisioning.checkPostToPreprovisioningWiremock();
        a4ResourceInventory.checkNetworkServiceProfileFtthAccessConnectedToTerminationPointExists(tpFtthData.getUuid(), 1);

    }

    @Test(description = "DIGIHUB-xxxxx NEMO creates new Termination Point with A10NSP Preprovisioning")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("NEMO creates new Termination Point with A10NSP Preprovisioning")
    public void newTpWithA10NspPreprovisioning() throws InterruptedException {
        // WHEN / Action
        a4Nemo.createTerminationPoint(tpA10Data, nepData);
        TimeUnit.SECONDS.sleep(SLEEP_TIMER); // Need to wait a bit because queues might need some time to process all events

        // THEN
        a4ResourceInventory.checkNetworkServiceProfileA10NspConnectedToTerminationPointExists(tpA10Data.getUuid(), 1);
    }

}
