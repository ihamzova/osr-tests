package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.osr.mappers.PreProvisioningMapper;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
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
    private final String RETRY = "retry";

    public MappingBuilder getAccessLine201() {
        final String NETWORK_SERVICE_PROFILE_URL = new OCUrlBuilder(A4_RESOURCE_INVENTORY_MS).buildUri().toString()
                + "/networkServiceProfilesFtthAccess/" + UUID.randomUUID().toString();

        return post(urlPathEqualTo(ACCESS_LINE_URL))
                .inScenario("ResilienceTest")
                .whenScenarioStateIs(RETRY)
                .willSetStateTo("success")
                .withName("getAccessLine201")
                .willReturn(aDefaultResponseWithBody(null, HTTP_CODE_CREATED_201))
                .withPostServeAction("webhook",
                        aDefaultWebhookWithBody(serialize(new PreProvisioningMapper().getNetworkServiceProfile())).withUrl(NETWORK_SERVICE_PROFILE_URL).withMethod(HttpMethod.PUT));
    }

    public MappingBuilder getAccessLine500() {
        return post(urlPathEqualTo(ACCESS_LINE_URL))
                .inScenario("ResilienceTest")
                .whenScenarioStateIs(STARTED)
                .willSetStateTo(RETRY)
                .withName("getAccessLine500")
                .willReturn(aDefaultResponseWithBody(null, HTTP_CODE_INTERNAL_SERVER_ERROR_500));
    }

    private String serialize(Object obj) {
        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }
}
