package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkElementGroupDto;
import io.restassured.response.Response;
import io.qameta.allure.Owner;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;

public class A4ResourceInventoryServiceTest {

    private A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();

    @Test
    @Owner("bela.kovac@t-systems.com")
    public void testCreateNeg_checkLogicalResource_deleteNeg() {

        String uuid = UUID.randomUUID().toString();
        NetworkElementGroupDto networkElementGroup = new NetworkElementGroupDto()
                .uuid(uuid)
                .type("POD")
                .specificationVersion("1")
                .operationalState("INSTALLING")
                .name(UUID.randomUUID().toString().substring(0, 6))
                .lifeCycleState("UNINSTALLING")
                .lastUpdateTime(OffsetDateTime.now())
                .description("NEG created during osr-test integration test")
                .creationTime(OffsetDateTime.now())
                .centralOfficeNetworkOperator("neg_centOffNetOp_for_integration_test");

        a4ResourceInventoryRobot.createNetworkElementGroup(networkElementGroup);
        Response response = a4ResourceInventoryRobot.getNegAsLogicalResourceWithA4ResourceInventoryServiceApi(uuid);

        response.then().assertThat()
                .body("id[0]", equalTo(uuid));

        a4ResourceInventoryRobot.deleteNegWithA4ResourceInventoryApi(uuid);

    }

}
