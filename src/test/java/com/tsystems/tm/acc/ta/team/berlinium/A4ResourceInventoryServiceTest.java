package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkElementGroupDto;
import io.qameta.allure.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

@Epic("OS&R domain")
@Feature("Accessing entries in a4-resource-inventory via the a4-resource-inventory-service as logical resource objects")
@TmsLink("DIGIHUB-57771")
public class A4ResourceInventoryServiceTest extends ApiTest {

    private A4ResourceInventoryRobot a4ResourceInventoryRobot;
    private A4ResourceInventoryServiceRobot a4ResourceInventoryServiceRobot;
    private NetworkElementGroupDto networkElementGroup;

    @BeforeClass
    public void init() {
        a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
        a4ResourceInventoryServiceRobot = new A4ResourceInventoryServiceRobot();
    }

    @BeforeMethod
    public void prepareData() {
        networkElementGroup = new NetworkElementGroupDto()
                .uuid(UUID.randomUUID().toString())
                .type("POD")
                .specificationVersion("1")
                .operationalState("INSTALLING")
                .name("NEG-" + UUID.randomUUID().toString().substring(0, 6)) // random NEG name to fulfill unique constraint
                .lifeCycleState("UNINSTALLING")
                .lastUpdateTime(OffsetDateTime.now())
                .description("NEG created during osr-test integration test")
                .creationTime(OffsetDateTime.now())
                .centralOfficeNetworkOperator("neg_centOffNetOp_for_integration_test");
    }

    @AfterMethod
    public void clearData() {
        a4ResourceInventoryRobot.deleteNetworkElementGroup(networkElementGroup.getUuid());
    }

    @Test(description = "DIGIHUB-57774 Create new network element in inventory and read it as logical resource")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-57774")
    @Description("Create new network element in inventory and read it as logical resource")
    public void testCreateNeg_checkLogicalResource_deleteNeg() {
        // GIVEN / Arrange
        // Already done in prepareData method

        // WHEN / Action
        a4ResourceInventoryRobot.createNetworkElementGroup(networkElementGroup);

        // THEN / Assert
        a4ResourceInventoryServiceRobot.checkLogicalResourceIsNetworkElementGroup(networkElementGroup.getUuid());

        // AFTER / Clean-up
        // Taken care of in clearData method
    }
}
