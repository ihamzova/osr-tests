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
    }

    @BeforeMethod
    public void setUp() {
        a4Inventory.createNetworkElementGroup(negData);
    }

    @AfterMethod
    public void cleanUp() {
        a4Inventory.deleteNetworkElementGroup(negData.getUuid());
    }

    @Test(description = "DIGIHUB-xxxxx Trigger an update call to NEMO for a Network Element Group")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Trigger an update call to NEMO for a Network Element Group")
    public void testNemoUpdateWithNeg() {
        // GIVEN / Arrange
        // all done in setUp() method

        // WHEN / Action
        a4NemoUpdater.triggerNemoUpdate(negData.getUuid());

        // THEN / Assert
        // No further assertions here besides return code of NEMO update call which is checked in the trigger robot above

        // AFTER / Clean-up
        // all done in cleanUp() method
    }
}
