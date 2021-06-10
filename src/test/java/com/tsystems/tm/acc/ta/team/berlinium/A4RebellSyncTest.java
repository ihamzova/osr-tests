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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.attachEventsToAllureReport;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.saveEventsToDefaultDir;

public class A4RebellSyncTest extends GigabitTest {

    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();
    private final A4InventoryImporterRobot a4Importer = new A4InventoryImporterRobot();
    private WireMockMappingsContext mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "RebellSync")).build();

    private A4NetworkElementGroup negData;
    private A4NetworkElement ne1Data;
    private A4NetworkElement ne2Data;
    private A4NetworkElementPort nep1Data;
    private A4NetworkElementPort nep2Data;
    private A4NetworkElementLink nel1Data;
    private A4NetworkElementLink nel2Data;
    private UewegData uewegDataA;

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
        nel1Data = osrTestContext.getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.defaultNetworkElementLink);
        nel2Data = osrTestContext.getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.networkElementLinkLcsInstalling);

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

        a4Inventory.deleteA4TestDataRecursively(negData);
    }

    @Test
    public void testRebelSyncLinkCreated() {
        // GIVEN / ARRANGE
        uewegDataA = osrTestContext.getData().getUewegDataDataProvider()
                .get(UewegDataCase.defaultUeweg);
        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "RebellSync"))
                .addRebellMock(uewegDataA, ne1Data, ne2Data)
                .build().publish();

        // WHEN / ACT
        a4Importer.doRebellSync(ne1Data.getVpsz(), ne1Data.getFsz());

        // THEN / ASSERT
        a4Inventory.checkNetworkElementLinkConnectedToNePortExists(uewegDataA, nep1Data.getUuid(), nep2Data.getUuid());
    }

    @Test
    public void testRebelSyncLinkAlreadyExists() {
        // GIVEN / ARRANGE
        uewegDataA = osrTestContext.getData().getUewegDataDataProvider()
                .get(UewegDataCase.defaultUeweg);
        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "RebellSync"))
                .addRebellMock(uewegDataA, ne1Data, ne2Data)
                .build().publish();
        a4Inventory.createNetworkElementLink(nel1Data, nep1Data, nep2Data, ne1Data, ne2Data, uewegDataA);

        // WHEN / ACT
        a4Importer.doRebellSync(ne1Data.getVpsz(), ne1Data.getFsz());

        // THEN / ASSERT
        a4Inventory.checkNetworkElementLinkConnectedToNePortExists(uewegDataA, nep1Data.getUuid(), nep2Data.getUuid());
        a4Inventory.getExistingNetworkElementLink(nel1Data.getUuid());
    }

    @Test
    public void testRebelSyncRebellFewerLinksWhichIsPlanning() {
        // GIVEN / ARRANGE
        a4Inventory.createNetworkElementLink(nel1Data, nep1Data, nep2Data, ne1Data, ne2Data);
        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "RebellSync"))
                .addRebellMockEmpty(ne1Data)
                .build().publish();

        // WHEN / ACT
        a4Importer.doRebellSync(ne1Data.getVpsz(), ne1Data.getFsz());

        // THEN / ASSERT
        a4Inventory.checkNetworkElementLinkIsDeleted(nel1Data.getUuid());
    }

    @Test
    public void testRebelSyncRebellFewerLinksWhichIsNotPlanning() {
        // GIVEN / ARRANGE
        a4Inventory.createNetworkElementLink(nel2Data, nep1Data, nep2Data, ne1Data, ne2Data);
        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "RebellSync"))
                .addRebellMockEmpty(ne1Data)
                .build().publish();

        // WHEN / ACT
        a4Importer.doRebellSync(ne1Data.getVpsz(), ne1Data.getFsz());

        // THEN / ASSERT
        a4Inventory.getExistingNetworkElementLink(nel2Data.getUuid());
    }

}
