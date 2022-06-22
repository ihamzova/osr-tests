package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4CommissioningClient;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_11_0.client.model.DeprovisioningResponseHolder;
import io.restassured.response.Response;

import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.voidCheck;

public class A4CommissioningRobot {
    private final A4CommissioningClient a4CommissioningClient = new A4CommissioningClient();

    public Response startCallBackA4AccessLineDeprovisioningWithoutChecks(String tpUuid) {
        return a4CommissioningClient.getClient()
                .callback()
                .callbackDeprovisioningWithUuid()
                .uuidPath(tpUuid)
                .xCallbackCorrelationIdHeader(tpUuid)
                .body(new DeprovisioningResponseHolder())
                .execute(voidCheck());
    }

}
