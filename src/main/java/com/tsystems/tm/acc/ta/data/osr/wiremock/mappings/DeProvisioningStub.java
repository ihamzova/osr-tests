package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;

import javax.ws.rs.HttpMethod;

import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_COMMISSIONING_MS;

public class DeProvisioningStub extends AbstractStubMapping {

    public static final String DEPROV_ACCESS_LINE_URL = "/resource-order-resource-inventory/v1/a4/deprovisioning/accessLine";
    private static final String RETRY = "retry";

    public MappingBuilder postDeProvAccessLineWithCallback(int httpCode) {
        // TODO get correct callback URL
        final String A4_DPU_DEPROV_CALLBACK_URL = new GigabitUrlBuilder(A4_COMMISSIONING_MS).buildUri()
                + "/resource-order-resource-inventory/v1/a4DpuDeprovCallbackUrlTodo";

        return post(urlPathEqualTo(DEPROV_ACCESS_LINE_URL))
                .inScenario("DPUDeprovisioning")
                .whenScenarioStateIs(RETRY)
                .willSetStateTo("success")
                .withName("postDeProvAccessLineWithCallback")
                .willReturn(aDefaultResponseWithBody(null, httpCode))
                .withPostServeAction("webhook",
                        aDefaultWebhookWithBody(null).withUrl(A4_DPU_DEPROV_CALLBACK_URL).withMethod(HttpMethod.POST));
    }

    public MappingBuilder postDeProvAccessLine(int httpCode) {
        return post(urlPathEqualTo(DEPROV_ACCESS_LINE_URL))
                .inScenario("DPUDeprovisioning")
                .whenScenarioStateIs(STARTED)
                .willSetStateTo(RETRY)
                .withName("postDeProvAccessLine")
                .willReturn(aDefaultResponseWithBody(null, httpCode));
    }

}
