package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import io.restassured.response.Response;
import io.qameta.allure.Owner;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;

public class A4ResourceInventoryServiceTest {

    private A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();

    @Test
    @Owner("bela.kovac@t-systems.com")
    public void testCreateNeg_checkLogicalResource_deleteNeg() {

        String uuid = UUID.randomUUID().toString();

        a4ResourceInventoryRobot.createNegWithA4ResourceInventoryApi(uuid);
        Response response = a4ResourceInventoryRobot.getNegAsLogicalResourceWithA4ResourceInventoryServiceApi(uuid);

        response.then().assertThat()
                .body("id[0]", equalTo(uuid));

        a4ResourceInventoryRobot.deleteNegWithA4ResourceInventoryApi(uuid);

    }

}
