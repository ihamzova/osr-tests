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
import com.tsystems.tm.acc.ta.robot.osr.A4WiremockRebellRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.a4.link.event.importer.client.model.Event;
import com.tsystems.tm.acc.tests.osr.a4.link.event.importer.client.model.EventData;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Epic;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.*;

import javax.ws.rs.HttpMethod;
import java.time.OffsetDateTime;
import java.util.UUID;

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
    private final A4WiremockRebellRobot a4WmRobotRebell = new A4WiremockRebellRobot();
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

    @Test(description = "DIGIHUB-129851 Scenario 1: EndSz A is found in A4 RI")
    public void testHorizonEventBothEndSzFoundRebelSync() {
        // GIVEN / ARRANGE
        OffsetDateTime starttime = OffsetDateTime.now();   // time for check later

        uewegData = osrTestContext.getData().getUewegDataDataProvider()
                .get(UewegDataCase.defaultUeweg);
        uewegData.setVendorPortNameA("ge-0/0/1");
        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(),
                wiremockScenarioName))
                .addRebellMock(uewegData, ne1Data, ne2Data)
                .build().publish();

        Event event = createEvent();

        // WHEN / ACT
        a4Importer.sendNotification(event);   // a4-importer at berlinium-03 do rebell sync

        // THEN / ASSERT
        nelData.setUuid(a4Inventory
                .getNetworkElementLinksByNeEndSz(ne1Data.getVpsz()+"/"+ne1Data.getFsz(), ne2Data.getVpsz()+"/"+ne2Data.getFsz())
                .get(0).getUuid());

        a4Inventory.checkNetworkElementLinkIsUpdatedWithLastSuccessfulSyncTime(nelData, starttime);
        a4Inventory.checkNetworkElementLinkConnectedToNePortExists(uewegData, nep1Data.getUuid(), nep2Data.getUuid());
        a4WmRobotRebell.checkSyncRequestToRebellWiremock(ne1Data.getVpsz()+"/"+ne1Data.getFsz(), HttpMethod.GET, 1);
        a4WmRobotRebell.checkSyncRequestToRebellWiremock(ne2Data.getVpsz()+"/"+ne2Data.getFsz(), HttpMethod.GET, 0); // ne1 found, ne2 not requested
    }


    @Test(description = "DIGIHUB-129851 Scenario 1: EndSz A is found in A4 RI")
    public void testHorizonEventEndSzAFoundRebelSync() {
        // GIVEN / ARRANGE
        OffsetDateTime starttime = OffsetDateTime.now();

        a4Inventory.deleteA4NetworkElementsRecursively(ne2Data);

        uewegData = osrTestContext.getData().getUewegDataDataProvider()
                .get(UewegDataCase.defaultUeweg);

        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(),
                wiremockScenarioName))
                .addRebellMock(uewegData, ne1Data, ne2Data)     // ne2 in a4-ri unknown
                .build().publish();

        Event event = createEvent();

        // WHEN / ACT
        a4Importer.sendNotification(event);   // a4-importer at berlinium-03 do now rebell sync

        // THEN / ASSERT
        nelData.setUuid(a4Inventory
                .getNetworkElementLinksByNeEndSz(ne1Data.getVpsz()+"/"+ne1Data.getFsz(), ne2Data.getVpsz()+"/"+ne2Data.getFsz())
                .get(0).getUuid());

        a4Inventory.checkNetworkElementLinkIsUpdatedWithLastSuccessfulSyncTime(nelData, starttime);
        a4WmRobotRebell.checkSyncRequestToRebellWiremock(ne1Data.getVpsz()+"/"+ne1Data.getFsz(), HttpMethod.GET, 1);
        a4WmRobotRebell.checkSyncRequestToRebellWiremock(ne2Data.getVpsz()+"/"+ne2Data.getFsz(), HttpMethod.GET, 0); // no request to Rebell
    }

    @Test(description = "DIGIHUB-129851 Scenario 2: EndSz B is found in A4 RI")
    public void testHorizonEventEndSzBFoundRebelSync() {
        // GIVEN / ARRANGE
        OffsetDateTime starttime = OffsetDateTime.now();

        a4Inventory.deleteA4NetworkElementsRecursively(ne1Data);

        uewegData = osrTestContext.getData().getUewegDataDataProvider()
                .get(UewegDataCase.defaultUeweg);
        uewegData.setVendorPortNameA("PCI-1/0");

        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(),
                wiremockScenarioName))
                .addRebellMock(uewegData, ne2Data, ne1Data)   // ne1 unknown in a4
                .build().publish();

        Event event = createEvent();

        // WHEN / ACT
        a4Importer.sendNotification(event);    // a4-importer at berlinium-03 do now rebell sync

        // THEN / ASSERT
        nelData.setUuid(a4Inventory
                .getNetworkElementLinksByNeEndSz(ne2Data.getVpsz()+"/"+ne2Data.getFsz(), ne1Data.getVpsz()+"/"+ne1Data.getFsz())
                .get(0).getUuid());
        a4Inventory.checkNetworkElementLinkIsUpdatedWithLastSuccessfulSyncTime(nelData, starttime);
        a4WmRobotRebell.checkSyncRequestToRebellWiremock(ne1Data.getVpsz()+"/"+ne1Data.getFsz(), HttpMethod.GET, 0); // no request to Rebell
        a4WmRobotRebell.checkSyncRequestToRebellWiremock(ne2Data.getVpsz()+"/"+ne2Data.getFsz(), HttpMethod.GET, 1);
        uewegData.setVendorPortNameA("ge-0/0/1");
    }

    @Test(description = "DIGIHUB-129850 Scenario 1: EndSz are not found in A4 RI")
    public void testHorizonEventEndSzNotFoundNoSync() {
        // GIVEN / ARRANGE
        a4Inventory.deleteA4NetworkElementsRecursively(ne1Data);
        a4Inventory.deleteA4NetworkElementsRecursively(ne2Data);

        uewegData = osrTestContext.getData().getUewegDataDataProvider()
                .get(UewegDataCase.defaultUeweg);

        Event event = createEvent();

        // WHEN / ACT
        a4Importer.sendNotification(event);  // a4-importer at berlinium-03 do now rebell sync

        // THEN / ASSERT
        a4Inventory.checkNetworkElementNotExist(ne1Data.getVpsz(), ne1Data.getFsz()); // no ne, then no nel
        a4Inventory.checkNetworkElementNotExist(ne2Data.getVpsz(), ne2Data.getFsz());
        a4WmRobotRebell.checkSyncRequestToRebellWiremock(ne1Data.getVpsz()+"/"+ne1Data.getFsz(), HttpMethod.GET, 0); // no request to Rebell
        a4WmRobotRebell.checkSyncRequestToRebellWiremock(ne2Data.getVpsz()+"/"+ne2Data.getFsz(), HttpMethod.GET, 0); // no request to Rebell
    }

    @NotNull
    private Event createEvent() {
        Event event = new Event();
        event.setType("com.telekom.net4f.irtafel.v1");
        event.setId(UUID.randomUUID());
        event.setSource("http://tafel-url-tbd");
        event.setSpecversion("1.0");

        EventData eventData = new EventData();
        eventData.setEndszA(ne1Data.getVpsz()+"/"+ne1Data.getFsz());
        eventData.setEndszB(ne2Data.getVpsz()+"/"+ne2Data.getFsz());
        eventData.setUewegId(uewegData.getUewegId());
        eventData.setUewegStatus("InBetrieb");
        event.setData(eventData);
        return event;
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
