package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;

import javax.ws.rs.HttpMethod;

import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;

public class DeProvisioningStub extends AbstractStubMapping {

    private static final String DEPROV = "DPUDeprovisioning";
    private static final String RETRY = "retry";
    private static final String SUCCESS = "success";
    private static final String WEBHOOK = "webhook";

    // TODO get correct callback URL
    private static final String A4_DPU_DEPROV_CALLBACK_URL = new GigabitUrlBuilder(A4_COMMISSIONING_MS).buildUri()
            + RORI_V1_PATH + "a4DpuDeprovCallbackUrlTodo";
    private static final String A4_NSP_DELETE_URL = new GigabitUrlBuilder(A4_RESOURCE_INVENTORY_MS).buildUri()
            + RORI_V1_PATH + "a4NetworkServiceProfilesFtthAccess/";
    public static final String DEPROV_ACCESS_LINE_URL = RORI_V1_PATH + "a4/deprovisioning/accessLine";

    public MappingBuilder postDeProvAccessLineWithCallback(int httpCode) {
        return post(urlPathEqualTo(DEPROV_ACCESS_LINE_URL))
                .inScenario(DEPROV)
                .withName("postDeProvAccessLineWithCallback")
                .willReturn(aDefaultResponseWithBody(null, httpCode))
                .withPostServeAction(WEBHOOK,
                        aDefaultWebhookWithBody(null).withUrl(A4_DPU_DEPROV_CALLBACK_URL).withMethod(HttpMethod.POST));
    }

    public MappingBuilder postDeProvAccessLineWithNspDeletion(int httpCode, String uuid) {
        return post(urlPathEqualTo(DEPROV_ACCESS_LINE_URL))
                .inScenario(DEPROV)
                .withName("postDeProvAccessLineWithNspDeletion")
                .willReturn(aDefaultResponseWithBody(null, httpCode))
                .withPostServeAction(WEBHOOK,
                        aDefaultWebhookWithBody(null).withUrl(A4_NSP_DELETE_URL + uuid).withMethod(HttpMethod.DELETE));
    }

    public MappingBuilder postDeProvAccessLine(int httpCode) {
        return post(urlPathEqualTo(DEPROV_ACCESS_LINE_URL))
                .inScenario(DEPROV)
                .withName("postDeProvAccessLine")
                .willReturn(aDefaultResponseWithBody(null, httpCode));
    }

    public MappingBuilder postDeProvAccessLineFirstTime(int httpCodeFirst) {
        return post(urlPathEqualTo(DEPROV_ACCESS_LINE_URL))
                .inScenario(DEPROV)
                .whenScenarioStateIs(STARTED)
                .willSetStateTo(RETRY)
                .withName("postDeProvAccessLineRetry")
                .willReturn(aDefaultResponseWithBody(null, httpCodeFirst));
    }

    public MappingBuilder postDeProvAccessLineSecondTimeWithNspDeletion(int httpCodeSecond, String nspUuid) {
        return post(urlPathEqualTo(DEPROV_ACCESS_LINE_URL))
                .inScenario(DEPROV)
                .whenScenarioStateIs(RETRY)
                .willSetStateTo(SUCCESS)
                .withName("postDeProvAccessLineRetry")
                .willReturn(aDefaultResponseWithBody(null, httpCodeSecond))
                .withPostServeAction(WEBHOOK,
                        aDefaultWebhookWithBody(null).withUrl(A4_NSP_DELETE_URL + nspUuid).withMethod(HttpMethod.DELETE));
    }

}
