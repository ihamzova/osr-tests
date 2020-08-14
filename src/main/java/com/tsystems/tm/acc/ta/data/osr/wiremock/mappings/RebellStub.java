package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.osr.mappers.RebellMapper;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.UewegData;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.rebell.client.invoker.JSON;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class RebellStub extends AbstractStubMapping {
    public static final String REBELL_UEWEG_URL = "/resource-order-resource-inventory/v1/uewege";

    public MappingBuilder getUeweg200(UewegData uewegData, A4NetworkElement neA, A4NetworkElement neB) {
        return get(urlPathEqualTo(REBELL_UEWEG_URL))
                .withName("getUeweg200")
                .willReturn(aDefaultResponseWithBody(serialize(new RebellMapper().getUewegList(uewegData, neA, neB)), 200))
                .withQueryParam("endsz", equalTo((neA.getVpsz() + "/" + neA.getFsz()).replace("/", "_")));
    }

    private String serialize(Object obj) {
        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }
}