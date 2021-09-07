package com.tsystems.tm.acc.ta.robot.osr;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.NetworkLineProfileManagementClient;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.tests.osr.ifclient.network.profile.client.model.AsyncResponseNotification;
import com.tsystems.tm.acc.tests.osr.ifclient.network.profile.client.model.ResourceCharacteristicCallback;
import com.tsystems.tm.acc.tests.osr.network.line.profile.management.v1_5_0.client.model.ResourceOrder;
import com.tsystems.tm.acc.tests.osr.resource.inventory.adapter.external.client.invoker.JSON;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_ACCEPTED_202;
import static com.tsystems.tm.acc.ta.wiremock.ExtendedWireMock.CONSUMER_ENDPOINT;
import static org.testng.Assert.*;

@Slf4j
public class NetworkLineProfileManagementRobot {

    private static String CORRELATION_ID;
    private NetworkLineProfileManagementClient networkLineProfileManagementClient = new NetworkLineProfileManagementClient(authTokenProvider );
    private static final AuthTokenProvider authTokenProvider = new RhssoClientFlowAuthTokenProvider("wg-access-provisioning", RhssoHelper.getSecretOfGigabitHub("wg-access-provisioning"));


    @Step("Performs actions with subscriberNetworklineProfile or l2SsaNspReference")
    public void createResourceOrderRequest(ResourceOrder resourceOrder, AccessLine accessLine){
        CORRELATION_ID = UUID.randomUUID().toString();
        networkLineProfileManagementClient
                .getClient()
                .networkLineProfile()
                .updateLineProfile()
                .xCallbackCorrelationIdHeader(String.valueOf(CORRELATION_ID))
                .xCallbackUrlHeader(new OCUrlBuilder("wiremock-acc")
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .xCallbackErrorUrlHeader(new OCUrlBuilder("wiremock-acc")
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .body(resourceOrder)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
        log.info("Received xCallbackCorrelationId: " + CORRELATION_ID);

        AsyncResponseNotification notification = new JSON()
                .deserialize(getCallbackWiremock(CORRELATION_ID).get(0).getBodyAsString(), AsyncResponseNotification.class);
       assertNotNull(notification.getResponse(), "Cannot get callback");
        assertEquals(notification.getResponse()
                .getEvent().getResourceOrder()
                .getResourceOrderItems().get(0)
                .getResource()
                .getResourceCharacteristics().stream()
                .filter(name -> name.getName().equals(ResourceCharacteristicCallback.NameEnum.CALID))
                .collect(Collectors.toList()).get(0)
                .getValue(), accessLine.getLineId(), "Subscriber profile was not created");

    }

    @Step("Check callback in Wiremock")
    public List<LoggedRequest> getCallbackWiremock(String uuid) {
        List<LoggedRequest> requests = WireMockFactory.get().retrieve(
                exactly(1),
                newRequestPattern(RequestMethod.POST, urlPathEqualTo(CONSUMER_ENDPOINT))
                        .withHeader("X-Callback-Correlation-Id", equalTo(uuid)),
                400_000);
        log.info("Callback: " + requests);
        assertTrue(requests.size() >= 1, "Callback is found");
        return requests;
    }
}
