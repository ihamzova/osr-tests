package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkElementGroupDto;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

@Epic("OS&R domain")
@Feature("Sending update calls to NEMO")
@TmsLink("DIGIHUB-xxxxx")
public class A4NemoUpdateTest {
    private A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private A4NemoUpdaterRobot a4NemoUpdaterRobot = new A4NemoUpdaterRobot();

    @Test(description = "DIGIHUB-xxxxx Trigger an update call to NEMO for a Network Element Group")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Trigger an update call to NEMO for a Network Element Group")
    public void testNemoUpdateWithNeg() {
        // GIVEN / Arrange
        NetworkElementGroupDto networkElementGroupDto = new NetworkElementGroupDto()
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
        a4ResourceInventoryRobot.createNetworkElementGroup(networkElementGroupDto);

        // WHEN / Action
        a4NemoUpdaterRobot.triggerNemoUpdate(networkElementGroupDto.getUuid());

        // THEN / Assert
        // No further assertions here besides return code of NEMO which is checked in the trigger robot above

        // AFTER / Clean-up
        a4ResourceInventoryRobot.deleteNetworkElementGroup(networkElementGroupDto.getUuid());

    }
}
