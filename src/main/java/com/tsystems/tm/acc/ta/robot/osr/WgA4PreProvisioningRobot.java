package com.tsystems.tm.acc.ta.robot.osr;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.tsystems.tm.acc.ta.api.osr.WgA4ProvisioningClient;
import com.tsystems.tm.acc.ta.data.upiter.UpiterConstants;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.tests.osr.resource.inventory.adapter.external.client.invoker.JSON;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_11_0.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_11_0.client.model.A4AccessLineRequestDto;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_11_0.client.model.DeprovisioningResponseHolder;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_11_0.client.model.TpRefDto;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_ACCEPTED_202;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_CREATED_201;
import static com.tsystems.tm.acc.ta.wiremock.ExtendedWireMock.CONSUMER_ENDPOINT;
import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;
import static org.testng.Assert.assertTrue;


@Slf4j
public class WgA4PreProvisioningRobot {

    private final ApiClient wgA4ProvisioningClient = new WgA4ProvisioningClient().getClient();
    private static String CORRELATION_ID;

    @Step("Start preprovisioning process")
    public void startPreProvisioning(TpRefDto tpRefDto) {
        wgA4ProvisioningClient
                .preProvisioningProcessExternal()
                .startAccessLinePreProvisioning()
                .body(tpRefDto)
                .execute(checkStatus(HTTP_CODE_CREATED_201));
    }

    @Step("Start AccessLine Deprovisioning")
    public void startAccessLineDeprovisioning(String lineId) {
        CORRELATION_ID = UUID.randomUUID().toString();
        wgA4ProvisioningClient
                .deprovisioningProcess()
                .startA4AccessLineDeprovisioning()
                .xCallbackCorrelationIdHeader(CORRELATION_ID)
                .xCallbackUrlHeader(new GigabitUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .buildUri()
                        .toString())
                .xCallbackErrorUrlHeader(new GigabitUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .buildUri()
                        .toString())
                .body(new A4AccessLineRequestDto()
                        .lineId(lineId))
                .execute(checkStatus(HTTP_CODE_ACCEPTED_202));

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

}
