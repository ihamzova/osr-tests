package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import io.qameta.allure.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.attachEventsToAllureReport;

/*@ServiceLog(A4_RESOURCE_INVENTORY_MS)
@ServiceLog(A4_NEMO_UPDATER_MS)*/
@Epic("OS&R domain")
@Feature("Sending update calls to NEMO")
@TmsLink("DIGIHUB-xxxxx")
public class A4NemoUpdateTest extends ApiTest {

    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();
    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();

    // Initialize with dummy wiremock so that cleanUp() call within init() doesn't run into nullpointer
    private WireMockMappingsContext wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "")).build();

    private A4NetworkElementGroup negData;

    @BeforeClass
    public void init() {
        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);

        // Ensure that no old test data is in the way
        cleanup();
    }

    @BeforeMethod
    public void setup() {
        a4Inventory.createNetworkElementGroup(negData);

        wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "A4NemoUpdateTest"))
                .addWgA4ProvisioningMock()
                .addNemoMock()
                .build();
        wiremock.publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());
    }

    @AfterMethod
    public void cleanup() {
        wiremock.close();
        wiremock
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());

        a4Inventory.deleteA4TestDataRecursively(negData);
    }

    @Test(description = "DIGIHUB-xxxxx Trigger an update call (PUT) to NEMO for existing network element group")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Trigger an update call to NEMO for existing network element group")
    public void testNemoUpdateWithNeg() {
        // WHEN / Action
        a4NemoUpdater.triggerNemoUpdate(negData.getUuid());

        // THEN / Assert
        a4NemoUpdater.checkLogicalResourcePutRequestToNemoWiremock(negData.getUuid());
    }

    @Test(description = "DIGIHUB-xxxxx Trigger an update call (DELETE) to NEMO for non-existing entity type element")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Trigger an update call to NEMO for non-existing entity type element")
    public void testNemoUpdateForNonexistingEntity() {
        // GIVEN / Arrange
        String uuid = UUID.randomUUID().toString();

        // WHEN / Action
        a4NemoUpdater.triggerNemoUpdate(uuid);

        // THEN / Assert
        a4NemoUpdater.checkLogicalResourceDeleteRequestToNemoWiremock(uuid);
    }

}
