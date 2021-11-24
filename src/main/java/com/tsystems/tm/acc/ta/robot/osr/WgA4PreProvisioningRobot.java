package com.tsystems.tm.acc.ta.robot.osr;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.A4CommissioningClient;
import com.tsystems.tm.acc.ta.api.osr.WgA4ProvisioningClient;
import com.tsystems.tm.acc.ta.data.upiter.UpiterConstants;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.tests.osr.a4.commissioning.client.model.DeprovisioningResponseHolder;
import com.tsystems.tm.acc.tests.osr.resource.inventory.adapter.external.client.invoker.JSON;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_9_0.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_9_0.client.model.A4AccessLineRequestDto;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_9_0.client.model.TpRefDto;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.*;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_CREATED_201;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.wiremock.ExtendedWireMock.CONSUMER_ENDPOINT;
import static org.testng.Assert.assertTrue;


@Slf4j
public class WgA4PreProvisioningRobot {

    private ApiClient wgA4ProvisioningClient = new WgA4ProvisioningClient(authTokenProvider).getClient();
    private com.tsystems.tm.acc.tests.osr.a4.commissioning.client.invoker.ApiClient a4CommissioningClient = new A4CommissioningClient(authTokenProvider).getClient();
    private static final AuthTokenProvider authTokenProvider = new RhssoClientFlowAuthTokenProvider("wiremock-acc", RhssoHelper.getSecretOfGigabitHub("wiremock-acc"));
    private static String CORRELATION_ID;

    @Step("Start preprovisioning process")
    public void startPreProvisioning(TpRefDto tpRefDto) {
        wgA4ProvisioningClient
                .preProvisioningProcessExternal()
                .startAccessLinePreProvisioning()
                .body(tpRefDto)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

    }

    public void startAccessLineDeprovisioning(String lineId) {
        CORRELATION_ID = UUID.randomUUID().toString();
        wgA4ProvisioningClient
                .deprovisioningProcess()
                .startA4AccessLineDeprovisioning()
                .xCallbackCorrelationIdHeader(CORRELATION_ID)
                .xCallbackUrlHeader(new OCUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .xCallbackErrorUrlHeader(new OCUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .body(new A4AccessLineRequestDto()
                .lineId(lineId))
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        log.info("Received xCallbackCorrelationId: " + CORRELATION_ID);
        new JSON().deserialize(getCallbackWiremock(CORRELATION_ID).get(0).getBodyAsString(), DeprovisioningResponseHolder.class);
    }

    @Step("Check callback in Wiremock")
    public List<LoggedRequest> getCallbackWiremock(String uuid) {
        List<LoggedRequest> requests = WireMockFactory.get().retrieve(
                exactly(1),
                newRequestPattern(RequestMethod.POST, urlPathEqualTo(CONSUMER_ENDPOINT))
                        .withHeader("X-Callback-Correlation-Id", equalTo(uuid)),
                120_000);
        log.info("Callback: " + requests);
        assertTrue(requests.size() >= 1, "Callback is found");
        return requests;
    }

    public void startCallBackA4AccessLineDeprovisioning(String tpuuid) {
        a4CommissioningClient
                .callback()
                .callbackDeprovisioning()
                .xCallbackCorrelationIdHeader(tpuuid)
                .body(new DeprovisioningResponseHolder())

                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        log.info("Received xCallbackCorrelationId: " + CORRELATION_ID);
    }

    public Response startCallBackA4AccessLineDeprovisioningWithoutResponse(String tpuuid) {
        return a4CommissioningClient
                .callback()
                .callbackDeprovisioning()
                .xCallbackCorrelationIdHeader(tpuuid)
                .body(new DeprovisioningResponseHolder())

                .execute(voidCheck());
    }

}
