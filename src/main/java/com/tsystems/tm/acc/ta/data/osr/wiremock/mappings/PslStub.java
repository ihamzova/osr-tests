package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.osr.mappers.PslMapper;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.EquipmentData;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.psl.adapter.client.invoker.JSON;
import com.tsystems.tm.acc.wiremock.webhook.WebhookPostServeAction;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class PslStub extends AbstractStubMapping {
    public static final String READ_EQUIPMENT_URL = "/v1/psl/read-equipment";
    public static final String READ_EQUIPMENT_3SCALE_URL = "/resource-order-resource-inventory/v1/psl/read-equipment";
    public static final String READ_EQUIPMENT_UNIVERSAL_URL = String.format("(%s|%s)/?", READ_EQUIPMENT_URL, READ_EQUIPMENT_3SCALE_URL);

    public MappingBuilder postReadEquipment202(OltDevice oltDevice) {
        return post(urlPathMatching(READ_EQUIPMENT_UNIVERSAL_URL))
                .withName("postReadEquipment202_" + oltDevice.getEndsz().replace("/", "_"))
                .willReturn(aDefaultResponseWithBody("", 202))
                .withRequestBody(matchingJsonPath("$.requestData.requestEquipment[0].endsz", equalTo(oltDevice.getEndsz())))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new PslMapper().getReadEquipmentResponseHolder(oltDevice))));
    }

    public MappingBuilder postReadEquipment202(EquipmentData equipmentData, A4NetworkElement networkElement) {
        return post(urlPathMatching(READ_EQUIPMENT_UNIVERSAL_URL))
                .withName("postReadEquipment202_" + (networkElement.getVpsz() + "/" + networkElement.getFsz()).replace("/", "_"))
                .willReturn(aDefaultResponseWithBody("", 202))
                .withRequestBody(matchingJsonPath("$.requestData.requestEquipment[0].endsz", equalTo((networkElement.getVpsz() + "/" + networkElement.getFsz()).replace("/", "_"))))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new PslMapper().getReadEquipmentResponseHolder(equipmentData, networkElement))));
    }

    private String serialize(Object obj) {
        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }
}