package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.osr.mappers.PreProvisioningMapper;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.invoker.JSON;

import javax.ws.rs.HttpMethod;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_CREATED_201;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_INTERNAL_SERVER_ERROR_500;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_MS;

public class PreProvisioningStub extends AbstractStubMapping {

    public static final String ACCESS_LINE_URL = "/resource-order-resource-inventory/v1/a4/accessLines";
    private static final String RETRY = "retry";
    private static final String RESILIENCE_TEST = "ResilienceTest";
    private static final String POST_ACCESS_LINE = "postAccessLine";

    public MappingBuilder postPreProvAccessLineWithNspCreation(int httpCode) {
        final String NETWORK_SERVICE_PROFILE_URL = new GigabitUrlBuilder(A4_RESOURCE_INVENTORY_MS).buildUri()
                + "/resource-order-resource-inventory/v1/a4NetworkServiceProfilesFtthAccess/" + UUID.randomUUID();

        return post(urlPathEqualTo(ACCESS_LINE_URL))
                .inScenario(RESILIENCE_TEST)
                .willSetStateTo("success")
                .withName(POST_ACCESS_LINE + httpCode)
                .willReturn(aDefaultResponseWithBody(null, httpCode))
                .withPostServeAction("webhook",
                        aDefaultWebhookWithBody(serialize(new PreProvisioningMapper().getNetworkServiceProfile())).withUrl(NETWORK_SERVICE_PROFILE_URL).withMethod(HttpMethod.PUT));
    }

    public MappingBuilder getAccessLine201() {
        return postPreProvAccessLineWithNspCreation(HTTP_CODE_CREATED_201);
    }

    public MappingBuilder postPreProvAccessLineFirstTime(int httpCode) {
        return post(urlPathEqualTo(ACCESS_LINE_URL))
                .inScenario(RESILIENCE_TEST)
                .whenScenarioStateIs(STARTED)
                .willSetStateTo(RETRY)
                .withName(POST_ACCESS_LINE + httpCode)
                .willReturn(aDefaultResponseWithBody(null, httpCode));
    }

    public MappingBuilder getAccessLine500() {
        return postPreProvAccessLineFirstTime(HTTP_CODE_INTERNAL_SERVER_ERROR_500);
    }

    public MappingBuilder postPreProvAccessLineSecondTimeWithNspCreation(int httpCode) {
        final String NETWORK_SERVICE_PROFILE_URL = new GigabitUrlBuilder(A4_RESOURCE_INVENTORY_MS).buildUri()
                + "/resource-order-resource-inventory/v1/a4NetworkServiceProfilesFtthAccess/" + UUID.randomUUID();

        return post(urlPathEqualTo(ACCESS_LINE_URL))
                .inScenario(RESILIENCE_TEST)
                .whenScenarioStateIs(RETRY)
                .willSetStateTo("success")
                .withName(POST_ACCESS_LINE + httpCode)
                .willReturn(aDefaultResponseWithBody(null, httpCode))
                .withPostServeAction("webhook",
                        aDefaultWebhookWithBody(serialize(new PreProvisioningMapper().getNetworkServiceProfile())).withUrl(NETWORK_SERVICE_PROFILE_URL).withMethod(HttpMethod.PUT));
    }

    private String serialize(Object obj) {
        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }
}
