package com.tsystems.tm.acc.ta.robot.osr;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.tsystems.tm.acc.ta.api.osr.OntOltOrchestratorClient;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.Ont;
import com.tsystems.tm.acc.ta.data.upiter.UpiterConstants;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.internal.client.model.SubscriberNeProfileDto;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.internal.client.model.HomeIdDto;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.internal.client.model.OntResourceV2Dto;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.internal.client.model.PortAndHomeIdDto;
import com.tsystems.tm.acc.tests.osr.resource.inventory.adapter.external.client.invoker.JSON;
import com.tsystems.tm.acc.tests.osr.resource.inventory.adapter.external.client.model.CommissioningResult;
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
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_ACCEPTED_202;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.helpers.WiremockHelper.CONSUMER_ENDPOINT;

@Slf4j
public class OntOltOrchestratorRobot {
    private static String CORRELATION_ID;
    private OntOltOrchestratorClient ontOltOrchestratorClient = new OntOltOrchestratorClient();

    @Step("Reserving new access line by port and homeId")
    public String reserveAccessLineByPortAndHomeId(PortAndHomeIdDto portAndHomeIdDto) {
        CORRELATION_ID = UUID.randomUUID().toString();
        ontOltOrchestratorClient
                .getClient()
                .ontOltOrchestratorV2()
                .reserveAccessLineWithHomeIdV2()
                .xCallbackCorrelationIdHeader(CORRELATION_ID)
                .xCallbackUrlHeader(new OCUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .xCallbackErrorUrlHeader(new OCUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .body(portAndHomeIdDto)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
        log.info("Received xCallbackCorrelationId: " + CORRELATION_ID);

        // TODO: Change type to the valid one, if swagger found
        ReserveLineByHomeIdResultV2 result = new JSON()
                .deserialize(getCallbackWiremock(CORRELATION_ID).get(0).getBodyAsString(), ReserveLineByHomeIdResultV2.class);
        // TODO: add error check
        return result.getResponse().getLineId();
    }

    @Step("Send request to create ONT resource")
    public void registerOnt(AccessLine accessLine, Ont ont) {
        CORRELATION_ID = UUID.randomUUID().toString();
        ontOltOrchestratorClient
                .getClient()
                .ontOltOrchestratorV2()
                .createOntResourceV2()
                .xCallbackCorrelationIdHeader(CORRELATION_ID)
                .xCallbackUrlHeader(new OCUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .xCallbackErrorUrlHeader(new OCUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .body(new OntResourceV2Dto()
                        .homeId(accessLine.getHomeId())
                        .lineId(accessLine.getLineId())
                        .ontSerialNumber(ont.getSerialNumber())
                        .ontState(OntResourceV2Dto.OntStateEnum.UNKNOWN))
                .execute(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
        log.info("Received xCallbackCorrelationId: " + CORRELATION_ID);

        CommissioningResult result = new JSON()
                .deserialize(getCallbackWiremock(CORRELATION_ID).get(0).getBodyAsString(), CommissioningResult.class);
        Assert.assertEquals(accessLine.getLineId(), result.getResponse().getLineId());
    }

    @Step("Send request to test ONT state")
    public void testOnt(String lineId) {
        CORRELATION_ID = UUID.randomUUID().toString();
        ontOltOrchestratorClient
                .getClient()
                .ontOltOrchestratorV2()
                .testOntResourceV2()
                .xCallbackCorrelationIdHeader(CORRELATION_ID)
                .xCallbackUrlHeader(new OCUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .xCallbackErrorUrlHeader(new OCUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .lineIdPath(lineId);
        log.info("Received xCallbackCorrelationId: " + CORRELATION_ID);
    }

    @Step("Check callback in Wiremock")
    public List<LoggedRequest> getCallbackWiremock(String uuid) {
        List<LoggedRequest> requests = WireMockFactory.get().retrieve(
                exactly(1),
                newRequestPattern(RequestMethod.POST, urlPathEqualTo(CONSUMER_ENDPOINT))
                        .withHeader("X-Callback-Correlation-Id", equalTo(uuid)),
                30_000);
        log.info("Callback: " + requests);
        Assert.assertEquals(requests.size(), 1);
        return requests;
    }

    @Step("Updates ONT state and associates access line with homeId")
    public void updateOntState(AccessLine accessLine) {
        ontOltOrchestratorClient
                .getClient()
                .ontOltOrchestratorV2()
                .updateOntResourceV2()
                .homeIdQuery(accessLine.getHomeId())
                .lineIdQuery(accessLine.getLineId())
                .ontStateResultQuery(SubscriberNeProfileDto.OntStateEnum.ONLINE)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Reserving new access line by homeId")
    public String reserveAccessLineTask(HomeIdDto homeIdDto) {
        CORRELATION_ID = UUID.randomUUID().toString();
        ontOltOrchestratorClient
                .getClient()
                .ontOltOrchestratorV2()
                .reserveAccessLineByHomeIdV2()
                .xCallbackCorrelationIdHeader(String.valueOf(CORRELATION_ID))
                .xCallbackUrlHeader(new OCUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .xCallbackErrorUrlHeader(new OCUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .body(homeIdDto)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));

        ReserveLineByHomeIdResultV2 result = new JSON()
                .deserialize(getCallbackWiremock(CORRELATION_ID).get(0).getBodyAsString(), ReserveLineByHomeIdResultV2.class);

        return result.getResponse().getLineId();
    }
}
