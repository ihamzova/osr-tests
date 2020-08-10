package com.tsystems.tm.acc.ta.robot.osr;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.tsystems.tm.acc.ta.api.osr.OntOltOrchestratorClient;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.internal.client.model.HomeIdDto;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.internal.client.model.PortAndHomeIdDto;
import com.tsystems.tm.acc.tests.osr.resource.inventory.adapter.external.client.invoker.JSON;
import com.tsystems.tm.acc.tests.osr.resource.inventory.adapter.external.client.model.ReserveLineByHomeIdResultV2;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.helpers.WiremockHelper.CONSUMER_ENDPOINT;

@Slf4j
public class OntOltOrchestratorRobot {
    private static final int HTTP_CODE_CREATED_202 = 202;
    private static final UUID uuid = UUID.randomUUID();
    private OntOltOrchestratorClient ontOltOrchestratorClient = new OntOltOrchestratorClient();

    @Step("Reserving new access line by port and homeId")
    public String reserveAccessLineByPortAndHomeId(PortAndHomeIdDto portAndHomeIdDto) {
        ontOltOrchestratorClient
                .getClient()
                .ontOltOrchestratorV2()
                .reserveAccessLineWithHomeIdV2()
                .xCallbackCorrelationIdHeader(String.valueOf(uuid))
                .xCallbackUrlHeader(new OCUrlBuilder("wiremock-acc")
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .xCallbackErrorUrlHeader(new OCUrlBuilder("wiremock-acc")
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .body(portAndHomeIdDto)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_202)));
        log.info("Received xCallbackCorrelationId: " + uuid.toString());

        List<LoggedRequest> requests = WireMockFactory.get().retrieve(
                exactly(1),
                newRequestPattern(RequestMethod.POST, urlPathEqualTo(CONSUMER_ENDPOINT))
                        .withHeader("X-Callback-Correlation-Id", equalTo(uuid.toString())),
                30_000);

        log.info("Callback: " + requests);
        Assert.assertEquals(requests.size(), 1);

        // TODO: Change type to the valid one, if swagger found
        ReserveLineByHomeIdResultV2 result = new JSON().deserialize(requests.get(0).getBodyAsString(), ReserveLineByHomeIdResultV2.class);
        // TODO: add error check
        return result.getResponse().getLineId();
    }

    @Step("Reserving new access line by homeId")
    public void reserveAccessLineTask(HomeIdDto homeIdDto) {
        ontOltOrchestratorClient
                .getClient()
                .ontOltOrchestratorV2()
                .reserveAccessLineByHomeIdV2()
                .xCallbackCorrelationIdHeader(String.valueOf(uuid))
                .xCallbackUrlHeader(new OCUrlBuilder("wiremock-acc")
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .xCallbackErrorUrlHeader(new OCUrlBuilder("wiremock-acc")
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .body(homeIdDto)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_202)));
    }
}
