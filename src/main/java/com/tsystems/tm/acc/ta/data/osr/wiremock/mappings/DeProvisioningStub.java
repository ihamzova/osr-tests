package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;

import javax.ws.rs.HttpMethod;

import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_MS;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.RORI_V1_PATH;

public class DeProvisioningStub extends AbstractStubMapping {

    private static final String DEPROV = "DPUDeprovisioning";
    private static final String RETRY = "Retry";
    private static final String SUCCESS = "success";
    private static final String WEBHOOK = "webhook";
    private static final String POST_DEPROV_ACCESSLINE = "postDeprovAccessLine";

    private static final String A4_NSP_DELETE_URL = new GigabitUrlBuilder(A4_RESOURCE_INVENTORY_MS).buildUri()
            + RORI_V1_PATH + "a4NetworkServiceProfilesFtthAccess/";
    public static final String DEPROV_ACCESS_LINE_URL = RORI_V1_PATH + "a4/deprovisioning/accessLine";

    public MappingBuilder postDeProvAccessLineWithNspDeletion(int httpCode, String uuid) {
        return post(urlPathEqualTo(DEPROV_ACCESS_LINE_URL))
                .inScenario(DEPROV)
                .withName(POST_DEPROV_ACCESSLINE + "WithNspDeletion")
                .willReturn(aDefaultResponseWithBody(null, httpCode))
                .withPostServeAction(WEBHOOK,
                        aDefaultWebhookWithBody(null).withUrl(A4_NSP_DELETE_URL + uuid).withMethod(HttpMethod.DELETE));
    }

    public MappingBuilder postDeProvAccessLine(int httpCode) {
        return post(urlPathEqualTo(DEPROV_ACCESS_LINE_URL))
                .inScenario(DEPROV)
                .withName(POST_DEPROV_ACCESSLINE)
                .willReturn(aDefaultResponseWithBody(null, httpCode));
    }

    public MappingBuilder postDeProvAccessLineFirstTime(int httpCodeFirst) {
        return post(urlPathEqualTo(DEPROV_ACCESS_LINE_URL))
                .inScenario(DEPROV)
                .whenScenarioStateIs(STARTED)
                .willSetStateTo(RETRY + 1)
                .withName(POST_DEPROV_ACCESSLINE + RETRY)
                .willReturn(aDefaultResponseWithBody(null, httpCodeFirst));
    }

    public MappingBuilder postDeProvAccessLineRetry(int httpCodeFirst, int retryNumber) {
        return post(urlPathEqualTo(DEPROV_ACCESS_LINE_URL))
                .inScenario(DEPROV)
                .whenScenarioStateIs(RETRY + retryNumber)
                .willSetStateTo(RETRY + (retryNumber + 1))
                .withName(POST_DEPROV_ACCESSLINE + RETRY)
                .willReturn(aDefaultResponseWithBody(null, httpCodeFirst));
    }

    public MappingBuilder postDeProvAccessLineWithNspDeletion(int httpCodeSecond, String nspUuid, int retryNumber) {
        return post(urlPathEqualTo(DEPROV_ACCESS_LINE_URL))
                .inScenario(DEPROV)
                .whenScenarioStateIs(RETRY + (retryNumber - 1))
                .willSetStateTo(SUCCESS)
                .withName(POST_DEPROV_ACCESSLINE + RETRY)
                .willReturn(aDefaultResponseWithBody(null, httpCodeSecond))
                .withPostServeAction(WEBHOOK,
                        aDefaultWebhookWithBody(null).withUrl(A4_NSP_DELETE_URL + nspUuid).withMethod(HttpMethod.DELETE));
    }

}
