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
    private WireMockMappingsContext mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "")).build();

    private A4NetworkElementGroup negData;
    private A4NetworkElement ne1Data;
    private A4NetworkElement ne2Data;
    private A4NetworkElementPort nep1Data;
    private A4NetworkElementPort nep2Data;
    private A4NetworkElementPort nep3Data;
    private A4NetworkElementPort nep4Data;
    private A4NetworkElementLink nel1Data;
    private UewegData uewegDataA;

    @BeforeClass
    public void init() {
        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        ne1Data = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementOperatingBor02);// !!XX
        ne2Data = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementRetiringPodServer01);// !!XX
        nep1Data = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_1G_002);// !!XX
        nep2Data = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_10G_001);// !!XX
        nep3Data = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);// !!XX
        nep4Data = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);// !!XX
        nel1Data = osrTestContext.getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.defaultNetworkElementLink);// !!XX

        // "lbz": "LSZ/Order Number-49/1197/0/7KC1-49/1198/0/7KD1",
        nel1Data.setLbz("LSZ/Order Number-" + ne1Data.getFsz() + "/" + ne1Data.getFsz() + "/" + ne2Data.getFsz() + "/" + ne2Data.getFsz());

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
        a4Inventory.createNetworkElementPort(nep3Data, ne1Data);
        a4Inventory.createNetworkElementPort(nep4Data, ne2Data);
//        a4Inventory.createNetworkElementLink(nel1Data, nep1Data, nep2Data);
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
    public void testRebelSyncNoDifference() {
        // GIVEN / ARRANGE
        uewegDataA = osrTestContext.getData().getUewegDataDataProvider()
                .get(UewegDataCase.defaultUeweg);// !!XX
        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "RebellSync"))
                .addRebellMock(uewegDataA, ne1Data, ne2Data) // !!XX
                .build();
        mappingsContext.publish();

        // WHEN / ACT
        a4Importer.doRebellSync(ne1Data.getVpsz(), ne1Data.getFsz());

        // THEN / ASSERT
        // number of NELs is unchanged
        a4Inventory.checkNetworkElementLinkConnectedToNePortExists(uewegDataA, nep1Data.getUuid(), nep2Data.getUuid());
//        a4Inventory.getExistingNetworkElementLink(nel1Data.getUuid());
    }

    @Test
    public void testRebelSyncNoDifference_existingNel() {
        // GIVEN / ARRANGE
        a4Inventory.createNetworkElementLink(nel1Data, nep1Data, nep2Data);


        uewegDataA = osrTestContext.getData().getUewegDataDataProvider()
                .get(UewegDataCase.defaultUeweg);// !!XX
        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "RebellSync"))
                .addRebellMock(uewegDataA, ne1Data, ne2Data) // !!XX
                .build();
        mappingsContext.publish();

        // WHEN / ACT
        a4Importer.doRebellSync(ne1Data.getVpsz(), ne1Data.getFsz());

        // THEN / ASSERT
        // number of NELs is unchanged
        a4Inventory.checkNetworkElementLinkConnectedToNePortExists(uewegDataA, nep1Data.getUuid(), nep2Data.getUuid());
        a4Inventory.getExistingNetworkElementLink(nel1Data.getUuid());
    }

    @Test
    public void testRebelSyncRebellMoreLinks() {
        // GIVEN / ARRANGE
        uewegDataA = osrTestContext.getData().getUewegDataDataProvider()
                .get(UewegDataCase.defaultUeweg);// !!XX
        UewegData uewegDataB = osrTestContext.getData().getUewegDataDataProvider()
                .get(UewegDataCase.defaultUeweg);// !!XX
        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "RebellSync"))
                .addRebellMockTwoEntries(uewegDataA, uewegDataB, ne1Data, ne2Data) // !!XX
                .build();
        mappingsContext.publish();

        // WHEN / ACT
        a4Importer.doRebellSync(ne1Data.getVpsz(), ne1Data.getFsz());

        // THEN / ASSERT
        // NELs still exist, plus 1 more NEL has been created
        a4Inventory.getExistingNetworkElementLink(nel1Data.getUuid());
        a4Inventory.checkNetworkElementLinkConnectedToNePortExists(uewegDataA, nep3Data.getUuid(), nep4Data.getUuid()); // !!XX
    }

    @Test
    public void testRebelSyncRebellFewerLinksWhichIsPlanning() {
        // GIVEN / ARRANGE
        // Set LC status of NEL = PLANNING
        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "RebellSync"))
                .addRebellMockEmpty(ne1Data) // !!XX
                .build();
        mappingsContext.publish();

        // WHEN / ACT
        a4Importer.doRebellSync(ne1Data.getVpsz(), ne1Data.getFsz());

        // THEN / ASSERT
        // 1 NEL has been deleted
        a4Inventory.checkNetworkElementLinkIsDeleted(nel1Data.getUuid());
    }

    @Test
    public void testRebelSyncRebellFewerLinksWhichIsNotPlanning() {
        // GIVEN / ARRANGE
        // Set LC status of NEL != PLANNING

        A4NetworkElementLink nel2Data;
        nel2Data = osrTestContext.getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.defaultNetworkElementLink);// !!XX

        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "RebellSync"))
                .addRebellMockEmpty(ne1Data) // !!XX
                .build();
        mappingsContext.publish();

        // WHEN / ACT
        a4Importer.doRebellSync(ne1Data.getVpsz(), ne1Data.getFsz());

        // THEN / ASSERT
        // NEL which is not in REBELL response still exists
        a4Inventory.getExistingNetworkElementLink(nel1Data.getUuid());
    }

}
