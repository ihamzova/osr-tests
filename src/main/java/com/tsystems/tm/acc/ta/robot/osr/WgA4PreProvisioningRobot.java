package com.tsystems.tm.acc.ta.robot.osr;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.WgA4ProvisioningClient;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_6_0.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_6_0.client.model.TpRefDto;
import io.qameta.allure.Step;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.PreProvisioningStub.ACCESS_LINE_URL;

public class WgA4PreProvisioningRobot {
    private static final Integer HTTP_CODE_CREATED_201 = 201;

    private ApiClient wgA4ProvisioningClient = new WgA4ProvisioningClient(authTokenProvider).getClient();
    private static final AuthTokenProvider authTokenProvider = new RhssoClientFlowAuthTokenProvider("wg-a4-provisioning", RhssoHelper.getSecretOfGigabitHub("wg-a4-provisioning"));

    @Step("Start preprovisioning process")
    public void startPreProvisioning(TpRefDto tpRefDto) {
        wgA4ProvisioningClient
                .preProvisioningProcessExternal()
                .startAccessLinePreProvisioning()
                .body(tpRefDto)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

    }

    @Step("Check if POST request to a4-preprovisioning wiremock has happened")
    public void checkPostToPreprovisioningWiremock() {
        WireMockFactory.get()
                .retrieve(
                        newRequestPattern(
                                RequestMethod.POST,
                                urlPathEqualTo(ACCESS_LINE_URL)));
    }
}