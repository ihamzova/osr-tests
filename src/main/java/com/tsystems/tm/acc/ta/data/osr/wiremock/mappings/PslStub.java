package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.tsystems.tm.acc.ta.data.osr.mappers.PslMapper;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.EquipmentData;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.helpers.mercury.JsonToXmlConverter;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.psl.adapter.client.invoker.JSON;
import com.tsystems.tm.acc.wiremock.webhook.WebhookPostServeAction;
import com.tsystems.tm.acc.wiremock.webhook.WebhookPostServeActionDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_ACCEPTED_202;

public class PslStub extends AbstractStubMapping {

    public static final String READ_EQUIPMENT_URL = "/v1/psl/read-equipment";
    public static final String READ_EQUIPMENT_XML_URL = "/soabp/rpc/etum2/de.telekom.digioss.architecture.DigiOSS/Default/SI_ReadEquipmentRequestOut/pp";
    public static final String READ_EQUIPMENT_3SCALE_URL = "/resource-order-resource-inventory/v1/psl/read-equipment/";
    public static final String READ_EQUIPMENT_UNIVERSAL_URL = String.format("(%s|%s)/?", READ_EQUIPMENT_URL, READ_EQUIPMENT_3SCALE_URL);

    protected WebhookPostServeActionDefinition aXmlWebhookWithBody(String body) {
        return WebhookPostServeAction.webhook()
                .withUrl("http://psl-transformer-app/api/pslTransformer/v1/callback")
                .withHeader("Authorization", new String[]{"Bearer {{oauth}}"})
                .withHeader("Content-Type", new String[]{"text/xml"})
                .withHeader("X-Callback-Correlation-Id", new String[]{"{{eq request.headers.X-Callback-Correlation-Id null yes=(randomValue type='UUID') no=request.headers.X-Callback-Correlation-Id}}"})
                .withHeader("X-B3-TraceId", new String[]{"{{eq request.headers.X-B3-TraceId null yes=(randomValue type='UUID') no=request.headers.X-B3-TraceId}}"})
                .withHeader("X-B3-SpanId", new String[]{"{{eq request.headers.X-B3-SpanId null yes=(randomValue type='UUID') no=request.headers.X-B3-SpanId}}"})
                .withHeader("X-B3-ParentSpanId", new String[]{"{{eq request.headers.X-B3-ParentSpanId null yes=(randomValue type='UUID') no=request.headers.X-B3-ParentSpanId}}"})
                .withMethod("POST")
                .withFixedDelayMilliseconds(2000)
                .withBody(body);
    }

    public MappingBuilder postReadEquipment202(OltDevice oltDevice) {
        return post(urlPathMatching(READ_EQUIPMENT_UNIVERSAL_URL))
                .withName("postReadEquipment202_" + oltDevice.getEndsz().replace("/", "_"))
                .willReturn(aDefaultResponseWithBody("", HTTP_CODE_ACCEPTED_202))
                .atPriority(1)
                .withRequestBody(matchingJsonPath("$.requestData.requestEquipment[0].endsz", equalTo(oltDevice.getEndsz())))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new PslMapper().getReadEquipmentResponseHolder(oltDevice))));
    }

    public MappingBuilder postReadEquipmentXml202(OltDevice oltDevice) throws Exception {
        return post(urlPathMatching(READ_EQUIPMENT_XML_URL))
                .atPriority(1)
                .withRequestBody(matchingXPath("//data/MT_EQDATA_REQ/EQUI/SORTIERFELD/text()", equalTo(oltDevice.getEndsz())))
                .withName("postReadEquipmentXml202_" + oltDevice.getEndsz().replace("/", "_"))
                .willReturn(WireMock.aResponse().withStatus(HTTP_CODE_ACCEPTED_202))
                .withPostServeAction(
                        WebhookPostServeAction.NAME,
                        aXmlWebhookWithBody(
                                JsonToXmlConverter
                                        .convertPslJsonToXml(null, serialize(new PslMapper().getReadEquipmentResponseHolder(oltDevice)))));
    }

    public MappingBuilder postReadEquipment202(EquipmentData equipmentData, A4NetworkElement networkElement) {
        return post(urlPathMatching(READ_EQUIPMENT_3SCALE_URL))
                .withName("postReadEquipment202_" + (networkElement.getVpsz() + "/" + networkElement.getFsz()).replace("/", "_"))
                .willReturn(aDefaultResponseWithBody("", HTTP_CODE_ACCEPTED_202))
                .atPriority(1)
                //.withRequestBody(matchingJsonPath("$.requestData.requestEquipment[0].endsz", equalTo((networkElement.getVpsz() + "/" + networkElement.getFsz()).replace("/", "/"))))

                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new PslMapper().getReadEquipmentResponseHolder(equipmentData, networkElement))));
    }

    private String serialize(Object obj) {
        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }
}
