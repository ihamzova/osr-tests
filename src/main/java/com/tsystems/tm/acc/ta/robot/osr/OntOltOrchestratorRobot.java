package com.tsystems.tm.acc.ta.robot.osr;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.OntOltOrchestratorClient;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.Ont;
import com.tsystems.tm.acc.ta.data.upiter.UpiterConstants;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.internal.client.model.SubscriberNeProfileDto;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_10_0.client.model.HomeIdDto;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_10_0.client.model.OntResourceV2Dto;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_10_0.client.model.OntState;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_10_0.client.model.PortAndHomeIdDto;
import com.tsystems.tm.acc.tests.osr.resource.inventory.adapter.external.client.api.CallbackControllerV2Api;
import com.tsystems.tm.acc.tests.osr.resource.inventory.adapter.external.client.invoker.JSON;
import com.tsystems.tm.acc.tests.osr.resource.inventory.adapter.external.client.model.*;
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

import static com.tsystems.tm.acc.ta.wiremock.ExtendedWireMock.CONSUMER_ENDPOINT;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Slf4j
public class OntOltOrchestratorRobot {
  private static String CORRELATION_ID;
  private OntOltOrchestratorClient ontOltOrchestratorClient = new OntOltOrchestratorClient(authTokenProvider);
  private static final AuthTokenProvider authTokenProvider = new RhssoClientFlowAuthTokenProvider("ont-olt-orchestrator", RhssoHelper.getSecretOfGigabitHub("ont-olt-orchestrator"));

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
    if (result.isSuccess() && result.getResponse() != null) {
      return result.getResponse().getLineId();
    } else
      return result.getError().getMessage();
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
                    .ontState(OntState.UNKNOWN))
            .execute(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
    log.info("Received xCallbackCorrelationId: " + CORRELATION_ID);

    CommissioningResult result = new JSON()
            .deserialize(getCallbackWiremock(CORRELATION_ID).get(0).getBodyAsString(), CommissioningResult.class);
    assertNotNull(result.getResponse().getLineId(), "Cannot get lineId from callback");
    Assert.assertEquals(accessLine.getLineId(), result.getResponse().getLineId(), "Ont wasn't registered");

    if (result.isSuccess() && result.getResponse() != null) {
      result.getResponse().getLineId();
    } else
      result.getError().getMessage();
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
            .lineIdPath(lineId)
            .execute(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
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
    assertTrue(requests.size() >= 1, "Callback is found");
    return requests;
  }

  @Step("Updates ONT state and associates access line with homeId")
  public void updateOntState(AccessLine accessLine) {
    ontOltOrchestratorClient
            .getClient()
            .ontOltOrchestratorSyncV2()
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

    if (result.isSuccess() && result.getResponse() != null) {
      return result.getResponse().getLineId();
    } else
      return result.getError().getMessage();
  }


  @Step("Change ONT serial number")
  public void changeOntSerialNumber(AccessLine accessLine, String newSerialNumber) {
    CORRELATION_ID = UUID.randomUUID().toString();
    ontOltOrchestratorClient
            .getClient()
            .ontOltOrchestratorV2()
            .changeOntSerialNumberV2()
            .xCallbackCorrelationIdHeader(String.valueOf(CORRELATION_ID))
            .xCallbackUrlHeader(new OCUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                    .withEndpoint(CONSUMER_ENDPOINT)
                    .build()
                    .toString())
            .xCallbackErrorUrlHeader(new OCUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                    .withEndpoint(CONSUMER_ENDPOINT)
                    .build()
                    .toString())
            .lineIdPath(accessLine.getLineId())
            .newSerialNumberQuery(newSerialNumber)
            .execute(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
    log.info("Received xCallbackCorrelationId: " + CORRELATION_ID);

    OntChangeResultV2 result = new JSON()
            .deserialize(getCallbackWiremock(CORRELATION_ID).get(0).getBodyAsString(), OntChangeResultV2.class);
    assertNotNull(result.getResponse().getLineId(), "Cannot get lineId from callback");
    assertNotNull(result.getResponse().getSerialNumber(), "Cannot get SerialNumber from callback");
    Assert.assertEquals(accessLine.getLineId(), result.getResponse().getLineId(), "Ont wasn't registered");
  }

  @Step("Decommission Ont")
  public void decommissionOnt(AccessLine accessline) {
    CORRELATION_ID = UUID.randomUUID().toString();
    ontOltOrchestratorClient
            .getClient()
            .ontOltOrchestratorV2()
            .decommissioningNetworkElementProfile()
            .xCallbackCorrelationIdHeader(String.valueOf(CORRELATION_ID))
            .xCallbackUrlHeader(new OCUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                    .withEndpoint(CONSUMER_ENDPOINT)
                    .build()
                    .toString())
            .xCallbackErrorUrlHeader(new OCUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                    .withEndpoint(CONSUMER_ENDPOINT)
                    .build()
                    .toString())
            .lineIdQuery(accessline.getLineId())
            .execute(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
    log.info("Received xCallbackCorrelationId: " + CORRELATION_ID);

    DecommissioningResultV2 result = new JSON()
            .deserialize(getCallbackWiremock(CORRELATION_ID).get(0).getBodyAsString(), DecommissioningResultV2.class);
    assertTrue(result.isSuccess(), "ONT failed to be decommissioned");
  }

  @Step("Decommission Ont with rollback")
  public void decommissionOntWithRollback(AccessLine accessline, Boolean isRollback) {
    CORRELATION_ID = UUID.randomUUID().toString();
    ontOltOrchestratorClient
            .getClient()
            .ontOltOrchestratorV2()
            .decommissioningNetworkElementProfile()
            .xCallbackCorrelationIdHeader(String.valueOf(CORRELATION_ID))
            .xCallbackUrlHeader(new OCUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                    .withEndpoint(CONSUMER_ENDPOINT)
                    .build()
                    .toString())
            .xCallbackErrorUrlHeader(new OCUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                    .withEndpoint(CONSUMER_ENDPOINT)
                    .build()
                    .toString())
            .lineIdQuery(accessline.getLineId())
            .rollbackToReservationQuery(isRollback)
            .execute(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
    log.info("Received xCallbackCorrelationId: " + CORRELATION_ID);

    DecommissioningResultV2 result = new JSON()
            .deserialize(getCallbackWiremock(CORRELATION_ID).get(0).getBodyAsString(), DecommissioningResultV2.class);
    assertTrue(result.isSuccess(), "ONT failed to be decommissioned");
  }
}
