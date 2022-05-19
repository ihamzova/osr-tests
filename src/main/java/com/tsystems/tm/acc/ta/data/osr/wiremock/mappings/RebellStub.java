package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.osr.mappers.RebellMapper;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.UewegData;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.rebell.client.invoker.JSON;
import com.tsystems.tm.acc.tests.osr.rebell.client.model.Ueweg;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;

public class RebellStub extends AbstractStubMapping {

    public static final String REBELL_UEWEG_URL = "/resource-order-resource-inventory/v1/uewege";
    private static final String ENDSZ = "endsz";

    public MappingBuilder getUeweg200(UewegData uewegData, A4NetworkElement neA, A4NetworkElement neB) {
        return get(urlPathEqualTo(REBELL_UEWEG_URL))
                .withName("getUeweg200")
                .willReturn(aDefaultResponseWithBody(serialize(new RebellMapper().getUewegList(uewegData, neA, neB)), HTTP_CODE_OK_200))
                .withQueryParam(ENDSZ, equalTo((neA.getVpsz() + "/" + neA.getFsz()).replace("/", "_")));
    }

    public MappingBuilder getUewegMultiple200(A4NetworkElement neA, List<UewegData> uewegData, List<A4NetworkElement> neB) {
        return get(urlPathEqualTo(REBELL_UEWEG_URL))
                .withName("getUeweg200")
                .willReturn(aDefaultResponseWithBody(serialize(new RebellMapper().getUewegListMultiple(neA, uewegData, neB)), HTTP_CODE_OK_200))
                .withQueryParam(ENDSZ, equalTo((neA.getVpsz() + "/" + neA.getFsz()).replace("/", "_")));
    }

    public MappingBuilder getUewegMultiple(int httpCode, String endsz, List<Ueweg> uewegData) {
        return get(urlPathEqualTo(REBELL_UEWEG_URL))
                .withName("getUeweg")
                .willReturn(aDefaultResponseWithBody(serialize(uewegData), httpCode))
                .withQueryParam(ENDSZ, equalTo(endsz.replace("/", "_")));
    }

    public MappingBuilder getUewegEmpty(A4NetworkElement neA) {
        return get(urlPathEqualTo(REBELL_UEWEG_URL))
                .withName("getUewegEmpty")
                .willReturn(aDefaultResponseWithBody(serialize(new RebellMapper().getUewegListEmpty()), HTTP_CODE_OK_200))
                .withQueryParam(ENDSZ, equalTo((neA.getVpsz() + "/" + neA.getFsz()).replace("/", "_")));
    }

    private String serialize(Object obj) {
        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }
}
