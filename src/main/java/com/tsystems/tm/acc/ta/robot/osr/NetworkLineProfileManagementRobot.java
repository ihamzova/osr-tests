package com.tsystems.tm.acc.ta.robot.osr;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.tsystems.tm.acc.ta.api.osr.NetworkLineProfileManagementClient;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.NetworkLineProfileData;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.tests.osr.ifclient.network.profile.client.model.AsyncResponseNotification;
import com.tsystems.tm.acc.tests.osr.ifclient.network.profile.client.model.ResourceCharacteristicCallback;
import com.tsystems.tm.acc.tests.osr.network.line.profile.management.v1_5_0.client.model.ResourceCharacteristic;
import com.tsystems.tm.acc.tests.osr.network.line.profile.management.v1_5_0.client.model.ResourceOrder;
import com.tsystems.tm.acc.tests.osr.resource.inventory.adapter.external.client.invoker.JSON;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_ACCEPTED_202;
import static com.tsystems.tm.acc.ta.wiremock.ExtendedWireMock.CONSUMER_ENDPOINT;
import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;
import static org.testng.Assert.*;

@Slf4j
public class NetworkLineProfileManagementRobot {

    private static String CORRELATION_ID;
    private final NetworkLineProfileManagementClient networkLineProfileManagementClient = new NetworkLineProfileManagementClient();


    @Step("Performs actions with subscriberNetworklineProfile or l2SsaNspReference")
    public void createResourceOrderRequest(ResourceOrder resourceOrder, AccessLine accessLine) {
        CORRELATION_ID = UUID.randomUUID().toString();
        networkLineProfileManagementClient
                .getClient()
                .networkLineProfile()
                .updateLineProfile()
                .xCallbackCorrelationIdHeader(String.valueOf(CORRELATION_ID))
                .xCallbackUrlHeader(new GigabitUrlBuilder("wiremock-acc")
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .xCallbackErrorUrlHeader(new GigabitUrlBuilder("wiremock-acc")
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .body(resourceOrder)
                .execute(checkStatus(HTTP_CODE_ACCEPTED_202));

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

    public NetworkLineProfileData setResourceOrderData(NetworkLineProfileData resourceOrderRequest, AccessLine accessline, ResourceCharacteristic calId) {
        calId.setValue(accessline.getLineId());
        resourceOrderRequest
                .getResourceOrder().getResourceOrderItems().get(0).getResource().getResourceCharacteristics()
                .removeIf(resourceCharacteristic -> resourceCharacteristic.getName().equals(ResourceCharacteristic.NameEnum.CALID));
        resourceOrderRequest
                .getResourceOrder().getResourceOrderItems().get(0).getResource().getResourceCharacteristics()
                .add(calId);
        return resourceOrderRequest;
    }

    @Step("Check callback in Wiremock")
    public List<LoggedRequest> getCallbackWiremock(String uuid) {
        List<LoggedRequest> requests = WireMockFactory.get().retrieve(
                exactly(1),
                newRequestPattern(RequestMethod.POST, urlPathEqualTo(CONSUMER_ENDPOINT))
                        .withHeader("X-Callback-Correlation-Id", equalTo(uuid)),
                500_000);
        assertTrue(requests.size() >= 1, "Callback is found");
        return requests;
    }
}
