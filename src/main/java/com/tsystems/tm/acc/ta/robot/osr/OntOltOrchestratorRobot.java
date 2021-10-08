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
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.*;
import com.tsystems.tm.acc.tests.osr.resource.inventory.adapter.external.client.invoker.JSON;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_ACCEPTED_202;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.wiremock.ExtendedWireMock.CONSUMER_ENDPOINT;
import static org.testng.Assert.assertTrue;

@Slf4j
public class OntOltOrchestratorRobot {
  private static String CORRELATION_ID;
  private OntOltOrchestratorClient ontOltOrchestratorClient = new OntOltOrchestratorClient(authTokenProvider);
  private static final AuthTokenProvider authTokenProvider = new RhssoClientFlowAuthTokenProvider("wiremock-acc", RhssoHelper.getSecretOfGigabitHub("wiremock-acc"));

  @Step("Reserving new access line by port and homeId")
  public OperationResultLineIdDto reserveAccessLineByPortAndHomeId(PortAndHomeIdDto portAndHomeIdDto) {
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

    return new JSON()
            .deserialize(getCallbackWiremock(CORRELATION_ID).get(0).getBodyAsString(), OperationResultLineIdDto.class);
  }

  @Step("Send request to create ONT resource")
  public OperationResultLineIdSerialNumberDto registerOnt(AccessLine accessLine, Ont ont) {
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

    return new JSON()
            .deserialize(getCallbackWiremock(CORRELATION_ID).get(0).getBodyAsString(), OperationResultLineIdSerialNumberDto.class);
  }

  @Step("Get Ont Attenuation Measurement from SEAL")
  public AttenuationMeasurementsDto getOntAttenuationMeasurement(String lineId) {
      CORRELATION_ID = UUID.randomUUID().toString();
      ontOltOrchestratorClient
              .getClient()
              .ontOltOrchestratorV2()
              .getAttenuationMeasurements()
              .lineIdPath(lineId)
              .xCallbackCorrelationIdHeader(CORRELATION_ID)
              .xCallbackUrlHeader(new OCUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                      .withEndpoint(CONSUMER_ENDPOINT)
                      .build()
                      .toString())
              .xCallbackErrorUrlHeader(new OCUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                      .withEndpoint(CONSUMER_ENDPOINT)
                      .build()
                      .toString())
              .execute(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
      log.info("Received xCallbackCorrelationId: " + CORRELATION_ID);
    return new JSON().deserialize(getCallbackWiremock(CORRELATION_ID).get(0).getBodyAsString(), AttenuationMeasurementsDto.class);
  }

  @Step("Send request to test ONT state")
  public OperationResultOntTestDto testOnt(String lineId) {
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

    return new JSON().deserialize(getCallbackWiremock(CORRELATION_ID).get(0).getBodyAsString(), OperationResultOntTestDto.class);
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

  @Step("Gets ONT state by LineID")
  public OntStateDto getOntState(AccessLine accessLine) {
    return ontOltOrchestratorClient
            .getClient()
            .ontOltOrchestratorSyncV2()
            .getOntStateV2()
            .lineIdPath(accessLine.getLineId())
            .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)))
            .as(OntStateDto.class);
  }

  @Step("Reserving new access line by homeId")
  public OperationResultLineIdDto reserveAccessLineTask(HomeIdDto homeIdDto) {
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

    return new JSON()
            .deserialize(getCallbackWiremock(CORRELATION_ID).get(0).getBodyAsString(), OperationResultLineIdDto.class);
  }

  @Step("Get additional ONT connectivity information")
  public OperationResultEmsEventDto getEmsEvents(OntConnectivityInfoDto ontConnectivityInfoDto) {
    CORRELATION_ID = UUID.randomUUID().toString();
    ontOltOrchestratorClient
            .getClient()
            .ontOltOrchestratorV2()
            .getOntConnectivityInfoV2()
            .xCallbackCorrelationIdHeader(String.valueOf(CORRELATION_ID))
            .xCallbackUrlHeader(new OCUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                    .withEndpoint(CONSUMER_ENDPOINT)
                    .build()
                    .toString())
            .xCallbackErrorUrlHeader(new OCUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                    .withEndpoint(CONSUMER_ENDPOINT)
                    .build()
                    .toString())
            .body(ontConnectivityInfoDto)
            .execute(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
    log.info("Received xCallbackCorrelationId: " + CORRELATION_ID);
    return new JSON().deserialize(getCallbackWiremock(CORRELATION_ID).get(0).getBodyAsString(), OperationResultEmsEventDto.class);
  }

  @Step("Change ONT serial number")
  public OperationResultLineIdSerialNumberDto changeOntSerialNumber(AccessLine accessLine, String newSerialNumber) {
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

    return new JSON()
            .deserialize(getCallbackWiremock(CORRELATION_ID).get(0).getBodyAsString(), OperationResultLineIdSerialNumberDto.class);
  }

  @Step("Decommission Ont")
  public OperationResultVoid decommissionOnt(AccessLine accessline) {
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

    return new JSON()
            .deserialize(getCallbackWiremock(CORRELATION_ID).get(0).getBodyAsString(), OperationResultVoid.class);
  }

  @Step("Decommission Ont with rollback")
  public OperationResultVoid decommissionOntWithRollback(AccessLine accessline, Boolean isRollback) {
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

    return new JSON()
            .deserialize(getCallbackWiremock(CORRELATION_ID).get(0).getBodyAsString(), OperationResultVoid.class);
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
