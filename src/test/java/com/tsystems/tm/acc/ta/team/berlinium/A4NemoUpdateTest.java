package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import io.qameta.allure.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.UUID;

@Epic("OS&R domain")
@Feature("Sending update calls to NEMO")
@TmsLink("DIGIHUB-xxxxx")
public class A4NemoUpdateTest extends ApiTest {
    private OsrTestContext osrTestContext = OsrTestContext.get();
    private A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();
    private A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();

    private A4NetworkElementGroup negData;

    @BeforeClass
    public void init() {
        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);

        // Ensure that no old test data is in the way
        a4Inventory.deleteNetworkElementGroups(negData);
    }

    @BeforeMethod
    public void setup() {
        a4Inventory.createNetworkElementGroup(negData);
    }

    @AfterMethod
    public void cleanup() {
        a4Inventory.deleteNetworkElementGroups(negData);
    }

    @Test(description = "DIGIHUB-xxxxx Trigger an update call (PUT) to NEMO for existing network element group")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Trigger an update call to NEMO for existing network element group")
    public void testNemoUpdateWithNeg() {
        // GIVEN / Arrange
        // nothing to do

        // WHEN / Action
        a4NemoUpdater.triggerNemoUpdate(negData.getUuid());

        // THEN / Assert
        a4NemoUpdater.checkLogicalResourcePutRequestToNemoWiremock(negData.getUuid());

        // AFTER / Clean-up
        // nothing to do
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

        // AFTER / Clean-up
        // nothing to do
    }
}
