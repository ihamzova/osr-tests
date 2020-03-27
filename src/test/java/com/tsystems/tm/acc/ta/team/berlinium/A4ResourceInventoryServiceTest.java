package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkElementGroupDto;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

@Epic("OS&R domain")
@Feature("Accessing entries in a4-resource-inventory via the a4-resource-inventory-service as logical resource objects")
@TmsLink("DIGIHUB-57771")
public class A4ResourceInventoryServiceTest //extends BaseTest
{

    private A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private A4ResourceInventoryServiceRobot a4ResourceInventoryServiceRobot = new A4ResourceInventoryServiceRobot();

    @Test(description = "DIGIHUB-57774 Create new network element in inventory and read it as logical resource, afterwards clean-up")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-57774") // Jira Id for this test in Xray
    @Description("Create new network element in inventory and read it as logical resource, afterwards clean-up")
    public void testCreateNeg_checkLogicalResource_deleteNeg() {
        // GIVEN / Arrange
        String uuid = UUID.randomUUID().toString();
        NetworkElementGroupDto networkElementGroup = new NetworkElementGroupDto()
                .uuid(uuid)
                .type("POD")
                .specificationVersion("1")
                .operationalState("INSTALLING")
                .name("NEG-" + uuid.substring(0, 6)) // random NEG name to fulfill unique constraints
                .lifeCycleState("UNINSTALLING")
                .lastUpdateTime(OffsetDateTime.now())
                .description("NEG created during osr-test integration test")
                .creationTime(OffsetDateTime.now())
                .centralOfficeNetworkOperator("neg_centOffNetOp_for_integration_test");

        // WHEN / Action
        a4ResourceInventoryRobot.createNetworkElementGroup(networkElementGroup);

        // THEN / Assert
        a4ResourceInventoryServiceRobot.checkLogicalResourceIsNetworkElementGroup(networkElementGroup);

        // AFTER / Clean-up
        a4ResourceInventoryRobot.deleteNetworkElementGroup(uuid);
    }
}
