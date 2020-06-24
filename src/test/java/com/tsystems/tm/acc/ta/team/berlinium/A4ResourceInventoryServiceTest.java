package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceRobot;
import io.qameta.allure.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Epic("OS&R domain")
@Feature("Accessing entries in a4-resource-inventory via the a4-resource-inventory-service as logical resource objects")
@TmsLink("DIGIHUB-57771")
public class A4ResourceInventoryServiceTest extends ApiTest {
    private OsrTestContext osrTestContext = OsrTestContext.get();
    private A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();
    private A4ResourceInventoryServiceRobot a4Nemo = new A4ResourceInventoryServiceRobot();

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
        // nothing to do
    }

    @AfterMethod
    public void cleanup() {
        a4Inventory.deleteNetworkElementGroups(negData);
    }

    @Test(description = "DIGIHUB-57774 Create new network element in inventory and read it as logical resource")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-57774")
    @Description("Create new network element in inventory and read it as logical resource")
    public void testCreateNeg_checkLogicalResource_deleteNeg() {
        // GIVEN / Arrange
        // nothing to do

        // WHEN / Action
        a4Inventory.createNetworkElementGroup(negData);

        // THEN / Assert
        a4Nemo.checkLogicalResourceIsNetworkElementGroup(negData);

        // AFTER / Clean-up
        // nothing to do
    }
}
