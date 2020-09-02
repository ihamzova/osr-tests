package com.tsystems.tm.acc.ta.data.morpheus.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.morpheus.mappers.AncpConfigurationMapper;
import com.tsystems.tm.acc.ta.data.morpheus.mappers.WgAccessProvisioningMapper;
import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.ancp.configuration.external.v2_1_0.client.invoker.JSON;
import com.tsystems.tm.acc.wiremock.webhook.WebhookPostServeAction;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class WgAccessProvisioningStub extends AbstractStubMapping {
    public static final String PORT_DEPROVISIONING_URL = "/resource-order-resource-inventory/v1/deprovisioning/port";
    public static final String PORT_PREPROVISIONING_URL = "/resource-order-resource-inventory/v1/provisioning/port";

    public MappingBuilder postPortDeprovisioning201(OltDevice olt, Dpu dpu) {
        return post(urlPathEqualTo(PORT_DEPROVISIONING_URL))
                .withName("postPortDeprovisioning201")
                .willReturn(aDefaultResponseWithBody(serialize(new WgAccessProvisioningMapper().getProcessDto()), 201))
                .withQueryParam("businessKey", matching(".*"))
                .withQueryParam("deprovisioningForDpu", matching("true|false"))
                .withRequestBody(matchingJsonPath(String.format("$.[?(@.endSz=='%s')]", olt.getEndsz())))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new WgAccessProvisioningMapper().getDeprovisioningResponseHolder(true))));
    }

    public MappingBuilder postPortDeprovisioning400(OltDevice olt, Dpu dpu) {
        return post(urlPathEqualTo(PORT_DEPROVISIONING_URL))
                .withName("postPortDeprovisioning400")
                .willReturn(aDefaultResponseWithBody(serialize(new WgAccessProvisioningMapper().getProcessDto()), 400))
                .withQueryParam("businessKey", matching(".*"))
                .withQueryParam("deprovisioningForDpu", matching("true|false"))
                .withRequestBody(matchingJsonPath(String.format("$.[?(@.endSz=='%s')]", olt.getEndsz())));
    }

    public MappingBuilder postPortDeprovisioning201CallbackError(OltDevice olt, Dpu dpu) {
        return post(urlPathEqualTo(PORT_DEPROVISIONING_URL))
                .withName("postPortDeprovisioning201")
                .willReturn(aDefaultResponseWithBody(serialize(new WgAccessProvisioningMapper().getProcessDto()), 201))
                .withQueryParam("businessKey", matching(".*"))
                .withQueryParam("deprovisioningForDpu", matching("true|false"))
                .withRequestBody(matchingJsonPath(String.format("$.[?(@.endSz=='%s')]", olt.getEndsz())))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new WgAccessProvisioningMapper().getDeprovisioningResponseHolder(false))));
    }

    public MappingBuilder postPortProvisioning400(OltDevice olt, Dpu dpu) {
        return post(urlPathEqualTo(PORT_DEPROVISIONING_URL))
                .withName("postPortProvisioning400")
                .willReturn(aDefaultResponseWithBody(serialize(new WgAccessProvisioningMapper().getProcessDto()), 400))
                .withQueryParam("businessKey", matching(".*"))
                .withRequestBody(matchingJsonPath(String.format("$.[?(@.endSz=='%s')]", olt.getEndsz())));
    }

    public MappingBuilder postPortProvisioning201CallbackError(OltDevice olt, Dpu dpu) {
        return post(urlPathEqualTo(PORT_PREPROVISIONING_URL))
                .withName("postPortProvisioning201")
                .willReturn(aDefaultResponseWithBody(serialize(new WgAccessProvisioningMapper().getProcessDto()), 201))
                .withQueryParam("businessKey", matching(".*"))
                .withRequestBody(matchingJsonPath(String.format("$.[?(@.endSz=='%s')]", olt.getEndsz())))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new WgAccessProvisioningMapper().getDeprovisioningResponseHolder(false))));
    }

    public MappingBuilder postPortProvisioning201(OltDevice olt, Dpu dpu) {
        return post(urlPathEqualTo(PORT_PREPROVISIONING_URL))
                .withName("postPortProvisioning201")
                .willReturn(aDefaultResponseWithBody(serialize(new WgAccessProvisioningMapper().getProcessDto()), 201))
                .withQueryParam("businessKey", matching(".*"))
                .withRequestBody(matchingJsonPath(String.format("$.[?(@.endSz=='%s')]", olt.getEndsz())))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new WgAccessProvisioningMapper().getDeprovisioningResponseHolder(true))));
    }

    private String serialize(Object obj) {
        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }
}
