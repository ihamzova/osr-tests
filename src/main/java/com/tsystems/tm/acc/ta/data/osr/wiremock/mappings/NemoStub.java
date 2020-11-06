package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.rebell.client.invoker.JSON;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class NemoStub extends AbstractStubMapping {
    public static final String NEMO_URL = "/resource-order-resource-inventory/v1/updateNemoTask";

    public MappingBuilder postNemoUpdate200(String uuid) {
        return get(urlPathEqualTo(NEMO_URL))
                .withName("postNemoUpdate200")
                .willReturn(aDefaultResponseWithBody("", 202))
                .atPriority(1)
//                .withQueryParam("endsz", equalTo((neA.getVpsz() + "/" + neA.getFsz()).replace("/", "_")))
                .withRequestBody(matchingJsonPath("$.uuid", equalTo(uuid)))
                ;
    }

    private String serialize(Object obj) {
        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }
}
