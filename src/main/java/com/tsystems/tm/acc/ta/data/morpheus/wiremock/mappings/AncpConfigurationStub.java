package com.tsystems.tm.acc.ta.data.morpheus.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.morpheus.mappers.AncpConfigurationMapper;
import com.tsystems.tm.acc.ta.data.morpheus.mappers.DpuComissioningMapper;
import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.ancp.configuration.external.v2_1_0.client.invoker.JSON;
import com.tsystems.tm.acc.wiremock.webhook.WebhookPostServeAction;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class AncpConfigurationStub extends AbstractStubMapping {
    public static final String ANCP_CONFIGURATION_URL = "/resource-order-resource-inventory/v2/ancp/configuration";

    public MappingBuilder postCreateAncpConfiguration200(Dpu dpu) {
        return post(urlPathEqualTo(ANCP_CONFIGURATION_URL))
                .withName("postCreateAncpConfiguration200")
                .willReturn(aDefaultResponseWithBody(serialize(new AncpConfigurationMapper().getANCPResponse()), 200))
                .withQueryParam("uplinkId", matching(".*"))
                .withQueryParam("sessionType", equalTo("DPU"))
                .withQueryParam("endSz", equalTo(dpu.getEndSz()))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new DpuComissioningMapper().getConfigurationUplinkDTOResult(true))));
    }

    public MappingBuilder postCreateAncpConfiguration200CallbackError(Dpu dpu) {
        return post(urlPathEqualTo(ANCP_CONFIGURATION_URL))
                .withName("postCreateAncpConfiguration200CallbackError")
                .willReturn(aDefaultResponseWithBody(serialize(new AncpConfigurationMapper().getANCPResponse()), 200))
                .withQueryParam("uplinkId", matching(".*"))
                .withQueryParam("sessionType", equalTo("DPU"))
                .withQueryParam("endSz", equalTo(dpu.getEndSz()))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new DpuComissioningMapper().getConfigurationUplinkDTOResult(false))));
    }

    public MappingBuilder postCreateAncpConfiguration400(Dpu dpu) {
        return post(urlPathEqualTo(ANCP_CONFIGURATION_URL))
                .withName("postCreateAncpConfiguration400")
                .willReturn(aDefaultResponseWithBody(serialize(new AncpConfigurationMapper().getANCPResponse()), 400))
                .withQueryParam("uplinkId", matching(".*"))
                .withQueryParam("sessionType", equalTo("DPU"))
                .withQueryParam("endSz", equalTo(dpu.getEndSz()));
    }

    public MappingBuilder deleteAncpConfiguration200() {
        return delete(urlMatching(ANCP_CONFIGURATION_URL + "/.*"))
                .withName("deleteAncpConfiguration200")
                .willReturn(aDefaultResponseWithBody(serialize(new AncpConfigurationMapper().getANCPResponse()), 200))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new DpuComissioningMapper().getConfigurationUplinkDTOResult(true))
                ));
    }

    public MappingBuilder deleteAncpConfiguration200CallbackError() {
        return delete(urlMatching(ANCP_CONFIGURATION_URL + "/.*"))
                .withName("deleteAncpConfiguration200CallbackError")
                .willReturn(aDefaultResponseWithBody(serialize(new AncpConfigurationMapper().getANCPResponse()), 200))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new DpuComissioningMapper().getConfigurationUplinkDTOResult(false))));
    }

    private String serialize(Object obj) {
        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }
}
