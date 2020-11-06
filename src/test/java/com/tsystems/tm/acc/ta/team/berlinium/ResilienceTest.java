//package com.tsystems.tm.acc.ta.team.berlinium;
//
//import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
//import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
//import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
//import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
//import com.tsystems.tm.acc.ta.apitest.ApiTest;
//import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
//import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
//import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
//import com.tsystems.tm.acc.ta.data.osr.models.A4TerminationPoint;
//import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
//import com.tsystems.tm.acc.ta.domain.OsrTestContext;
//import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
//import com.tsystems.tm.acc.ta.robot.osr.A4PreProvisioningRobot;
//import com.tsystems.tm.acc.ta.robot.osr.A4ResilienceRobot;
//import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
//import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceRobot;
//import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
//import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
//import io.qameta.allure.Description;
//import io.qameta.allure.Owner;
//import io.qameta.allure.TmsLink;
//import lombok.extern.slf4j.Slf4j;
//import org.testng.annotations.AfterMethod;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.Test;
//
//import java.io.IOException;
//
//import static com.tsystems.tm.acc.ta.data.berlinium.BerliniumConstants.A4_RESOURCE_INVENTORY;
//import static com.tsystems.tm.acc.ta.data.berlinium.BerliniumConstants.A4_RESOURCE_INVENTORY_SERVICE;
//
//@Slf4j
//@ServiceLog(A4_RESOURCE_INVENTORY)
//@ServiceLog(A4_RESOURCE_INVENTORY_SERVICE)
//public class ResilienceTest extends ApiTest {
//    private final OsrTestContext osrTestContext = OsrTestContext.get();
//    private final A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();
//    private final A4ResourceInventoryServiceRobot a4Nemo = new A4ResourceInventoryServiceRobot();
//    private final A4PreProvisioningRobot a4PreProvisioning = new A4PreProvisioningRobot();
//    private final A4ResourceInventoryRobot a4ResourceInventory = new A4ResourceInventoryRobot();
//    private final A4ResilienceRobot a4Resilience = new A4ResilienceRobot();
//
//    private A4NetworkElementGroup negData;
//    private A4NetworkElement neData;
//    private A4NetworkElementPort nepData;
//    private A4TerminationPoint tpData;
//
//    private WireMockMappingsContext mappingsContext;
//
//    private long REDELIVERY_DELAY = 155000;
//
//    @BeforeClass
//    public void init() {
//        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
//                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
//        neData = osrTestContext.getData().getA4NetworkElementDataProvider()
//                .get(A4NetworkElementCase.defaultNetworkElement);
//        nepData = osrTestContext.getData().getA4NetworkElementPortDataProvider()
//                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
//        tpData = osrTestContext.getData().getA4TerminationPointDataProvider()
//                .get(A4TerminationPointCase.defaultTerminationPointFtthAccess);
//
//        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "ResilienceTest")).build();
//
//        // Ensure that no old test data is in the way
//        cleanup();
//    }
//
//    @BeforeMethod
//    public void setup() {
//        a4Inventory.createNetworkElementGroup(negData);
//        a4Inventory.createNetworkElement(neData, negData);
//        a4Inventory.createNetworkElementPort(nepData, neData);
//
//        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "ResilienceTest"))
//                .addPreprovisioningErrorMock()
//                .build();
//        mappingsContext.publish();
//    }
//
//    @AfterMethod
//    public void cleanup() {
//        mappingsContext.deleteAll();
//
//        a4Inventory.deleteA4NetworkElementsIncludingChildren(neData);
//        a4Inventory.deleteNetworkElementGroups(negData);
//    }
//
//    @Test(description = "DIGIHUB-xxxxx NEMO creates new Termination Point with Preprovisioning")
//    @Owner("thea.john@telekom.de")
//    @TmsLink("DIGIHUB-xxxxx")
//    @Description("NEMO creates new Termination Point with Preprovisioning. This test takes appr. 3min.")
//    public void newTpWithPreprovisioning() throws InterruptedException, IOException {
//        // GIVEN / Arrange
//        REDELIVERY_DELAY = Long.parseLong(a4Resilience.getRedeliveryDelay());
//
//        // WHEN / Action
//        a4Nemo.createTerminationPoint(tpData, nepData);
//
//        // THEN
//        a4PreProvisioning.checkPostToPreprovisioningWiremock();
//
//        //because the wiremock answers with 500, nsp should not be created
//        a4ResourceInventory.checkNetworkServiceProfileFtthAccessConnectedToTerminationPointExists(tpData.getUuid(), 0);
//
//        //next time it is trying to redeliver to wiremock it should answer with 201 and create nsp
//        mappingsContext.deleteAll();
//        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "ResilienceTest"))
//                .addWgA4ProvisioningMock()
//                .build();
//        mappingsContext.publish();
//
//        //after a little time, nsp should be existent
//        log.debug("Thread sleeps for {} seconds...", REDELIVERY_DELAY/1000);
//        Thread.sleep(REDELIVERY_DELAY + 5000);
//        a4ResourceInventory.checkNetworkServiceProfileFtthAccessConnectedToTerminationPointExists(tpData.getUuid(), 1);
//    }
//
//}
