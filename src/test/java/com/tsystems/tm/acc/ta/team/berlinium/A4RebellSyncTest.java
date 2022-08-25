package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementlink.A4NetworkElementLinkCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.uewegdata.UewegDataCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4InventoryImporterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Epic;
import org.testng.annotations.*;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_INVENTORY_IMPORTER_MS;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_MS;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.attachEventsToAllureReport;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.saveEventsToDefaultDir;

@ServiceLog({A4_RESOURCE_INVENTORY_MS,A4_INVENTORY_IMPORTER_MS})
@Epic("OS&R domain")

public class A4RebellSyncTest extends GigabitTest {

    private final String wiremockScenarioName = "RebellSync";

    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();
    private final A4InventoryImporterRobot a4Importer = new A4InventoryImporterRobot();
    private WireMockMappingsContext mappingsContext = new OsrWireMockMappingsContextBuilder(
            new WireMockMappingsContext(WireMockFactory.get(), wiremockScenarioName)).build();

    private A4NetworkElementGroup negData;
    private A4NetworkElement ne1Data;
    private A4NetworkElement ne2Data;
    private A4NetworkElementPort nep1Data;
    private A4NetworkElementPort nep2Data;
    private A4NetworkElementLink nelData;
    private UewegData uewegData;

    @BeforeClass
    public void init() {
        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        ne1Data = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementOperatingBor02);
        ne2Data = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementRetiringPodServer01);
        nep1Data = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_1G_002);
        nep2Data = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_10G_001);
        nelData = osrTestContext.getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.defaultNetworkElementLink);

        // Ensure that no old test data is in the way
        cleanup();
    }

    @BeforeMethod
    public void setup() {
        a4Inventory.createNetworkElementGroup(negData);
        a4Inventory.createNetworkElement(ne1Data, negData);
        a4Inventory.createNetworkElement(ne2Data, negData);
        a4Inventory.createNetworkElementPort(nep1Data, ne1Data);
        a4Inventory.createNetworkElementPort(nep2Data, ne2Data);
    }

    @AfterMethod
    public void cleanup() {
        mappingsContext.close();
        mappingsContext
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());

        // Delete all A4 data which might provoke problems because of unique constraints
        a4Inventory.deleteA4NetworkElementGroupsRecursively(negData);
        a4Inventory.deleteA4NetworkElementsRecursively(ne1Data);
        a4Inventory.deleteA4NetworkElementsRecursively(ne2Data);
        a4Inventory.deleteA4NetworkElementPortsRecursively(nep1Data, ne1Data);
        a4Inventory.deleteA4NetworkElementPortsRecursively(nep2Data, ne2Data);
    }

    @Test
    public void testHorizonEventEndSzFoundRebelSync() {
        // GIVEN / ARRANGE
        uewegData = osrTestContext.getData().getUewegDataDataProvider()
                .get(UewegDataCase.defaultUeweg);
        System.out.println("+++ uewegData: "+uewegData);
        // +++ uewegData: UewegData(uewegId=764026, 299152, vendorPortNameA=ge-0/0/1, vendorPortNameB=PCI-1/0)
        System.out.println("+++ ne1Data: "+ne1Data);
        // +++ ne1Data: A4NetworkElement(uuid=93db293f-9b40-4b76-a2fd-64b2c26018f9, vpsz=49/4917/0, fsz=7KC1,
        // klsId=17056514, category=BOR, operationalState=WORKING, lifecycleState=OPERATING, type=A4-BOR-v1,
        // plannedMatNr=40318601, planningDeviceName=dmst.bor.2, ztpIdent=null)
        System.out.println("+++ ne2Data: "+ne2Data);
        // +++ ne2Data: A4NetworkElement(uuid=141a2562-96a4-4ddd-9dcb-a2bbb96eb4dc, vpsz=49/1343/0, fsz=7KD1,
        // klsId=1234567, category=POD_SERVER, operationalState=WORKING, lifecycleState=RETIRING, type=A4-POD-SERVER-v1,
        // plannedMatNr=40770140, planningDeviceName=dmst.server.1, ztpIdent=null)

        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(),
                wiremockScenarioName))
                .addRebellMock(uewegData, ne1Data, ne2Data)
                .build().publish();

        // send notification to Importer
        //      uewegId: I545253020
        //      uewegStatus: InBetrieb
        //  	endszA: 49/4917/0/7KC1
        //  	endszB: 49/1343/0/7KD1

        a4Importer.sendNotification(ne1Data);  // Funktion bauen, Übergabe Event


        // WHEN / ACT
        // a4Importer.doRebellSync(ne1Data);  // das soll jetzt der echte a4-Importer tun

        // THEN / ASSERT
       // a4Inventory.checkNetworkElementLinkConnectedToNePortExists(uewegData, nep1Data.getUuid(), nep2Data.getUuid());
    }

    @Test
    public void testRebelSyncLinkCreated() {
        // GIVEN / ARRANGE
        uewegData = osrTestContext.getData().getUewegDataDataProvider()
                .get(UewegDataCase.defaultUeweg);
        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(),
                wiremockScenarioName))
                .addRebellMock(uewegData, ne1Data, ne2Data)
                .build().publish();

        // WHEN / ACT
        a4Importer.doRebellSync(ne1Data);

        // THEN / ASSERT
        a4Inventory.checkNetworkElementLinkConnectedToNePortExists(uewegData, nep1Data.getUuid(), nep2Data.getUuid());
    }

    @Test
    public void testRebelSyncLinkAlreadyExists() {
        // GIVEN / ARRANGE
        uewegData = osrTestContext.getData().getUewegDataDataProvider()
                .get(UewegDataCase.defaultUeweg);
        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(),
                wiremockScenarioName))
                .addRebellMock(uewegData, ne1Data, ne2Data)
                .build().publish();
        a4Inventory.createNetworkElementLink(nelData, nep1Data, nep2Data, ne1Data, ne2Data, uewegData);

        // WHEN / ACT
        a4Importer.doRebellSync(ne1Data);

        // THEN / ASSERT
        a4Inventory.checkNetworkElementLinkConnectedToNePortExists(uewegData, nep1Data.getUuid(), nep2Data.getUuid());
        a4Inventory.getExistingNetworkElementLink(nelData.getUuid());
    }

    @Test
    public void testRebelSyncRebellFewerLinksWhichIsPlanning() {
        // GIVEN / ARRANGE
        nelData.setLifecycleState("PLANNING");
        a4Inventory.createNetworkElementLink(nelData, nep1Data, nep2Data, ne1Data, ne2Data);
        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(),
                wiremockScenarioName))
                .addRebellMockEmpty(ne1Data)
                .build().publish();

        // WHEN / ACT
        a4Importer.doRebellSync(ne1Data);

        // THEN / ASSERT
        a4Inventory.checkNetworkElementLinkIsDeleted(nelData.getUuid());
    }

    @DataProvider(name = "lifecycleStatesWithoutPlanning")
    public static Object[][] lifecycleStatesWithoutPlanning() {
        return new Object[][]{{"INSTALLING"}, {"OPERATING"}, {"RETIRING"}};
    }

    @Test(dataProvider = "lifecycleStatesWithoutPlanning",
            description = "DIGIHUB-xxxxx REBELL sync: NE with lifecycle state != PLANNING should not be deleted")
    public void testRebelSyncRebellFewerLinksWhichIsNotPlanning(String lifecycleState) {
        // GIVEN / ARRANGE
        nelData.setLifecycleState(lifecycleState);
        a4Inventory.createNetworkElementLink(nelData, nep1Data, nep2Data, ne1Data, ne2Data);
        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(),
                wiremockScenarioName))
                .addRebellMockEmpty(ne1Data)
                .build().publish();

        // WHEN / ACT
        a4Importer.doRebellSync(ne1Data);

        // THEN / ASSERT
        a4Inventory.getExistingNetworkElementLink(nelData.getUuid());
    }

}
