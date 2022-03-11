package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.A4CommissioningClient;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_11_0.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_11_0.client.model.DeprovisioningResponseHolder;
import io.restassured.response.Response;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.voidCheck;

public class A4CommissioningRobot {

    private static final AuthTokenProvider authTokenProvider = new RhssoClientFlowAuthTokenProvider("wiremock-acc", RhssoHelper.getSecretOfGigabitHub("wiremock-acc"));
    private final ApiClient a4CommissioningClient = new A4CommissioningClient(authTokenProvider).getClient();

    public Response startCallBackA4AccessLineDeprovisioningWithoutChecks(String tpUuid) {
        return a4CommissioningClient
                .callback()
                .callbackDeprovisioningWithUuid()
                .uuidPath(tpUuid)
                .xCallbackCorrelationIdHeader(tpUuid)
                .body(new DeprovisioningResponseHolder())
                .execute(voidCheck());
    }

}
