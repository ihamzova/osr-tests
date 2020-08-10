package com.tsystems.tm.acc.ta.data.morpheus.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.morpheus.mappers.WgFttbAccessProvisioningMapper;
import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.wg.fttb.access.provisioning.external.v1.client.invoker.JSON;
import com.tsystems.tm.acc.wiremock.webhook.WebhookPostServeAction;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class WgFttbAccessProvisioningStub extends AbstractStubMapping {
    public static final String DEVICE_PROVISIONING_URL = "/resource-order-resource-inventory/v1/fttbProvisioning/device";

    public MappingBuilder postDeviceProvisioning202(Dpu dpu) {
        return post(urlPathEqualTo(DEVICE_PROVISIONING_URL))
                .withName("postDeviceProvisioning202")
                .willReturn(aDefaultResponseWithBody("", 202))
                .withQueryParam("endSZ", matching(dpu.getEndSz()))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new WgFttbAccessProvisioningMapper().getAsyncResponseNotification(dpu.getEndSz(), true))));
    }

    public MappingBuilder postDeviceProvisioning202CallbackError(Dpu dpu) {
        return post(urlPathEqualTo(DEVICE_PROVISIONING_URL))
                .withName("postDeviceProvisioning202CallbackError")
                .willReturn(aDefaultResponseWithBody("", 202))
                .withQueryParam("endSZ", matching(dpu.getEndSz()))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new WgFttbAccessProvisioningMapper().getAsyncResponseNotification(dpu.getEndSz(), false))));
    }

    public MappingBuilder postDeviceProvisioning400(Dpu dpu) {
        return post(urlPathEqualTo(DEVICE_PROVISIONING_URL))
                .withName("postDeviceProvisioning400")
                .willReturn(aDefaultResponseWithBody("", 400))
                .withQueryParam("endSZ", matching(dpu.getEndSz()));
    }

    private String serialize(Object obj) {
        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }
}
