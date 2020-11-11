package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class NemoStub extends AbstractStubMapping {

    public static final String NEMO_URL = "/resource-order-resource-inventory/v1/nemo/logicalResource";

    public MappingBuilder putNemoUpdate201() {
//        return put(urlPathEqualTo(NEMO_URL + "/" + "uuid"))
//        return put(urlMatching(".*" + NEMO_URL + "/.*"))
        return put(urlPathMatching(NEMO_URL + "/.*"))
                .withName("putNemoUpdate201")
                .willReturn(aDefaultResponseWithBody("{TEST: \"PONG\"}", 201))
                .atPriority(1);
    }

    public MappingBuilder deleteNemoUpdate204() {
//        return put(urlPathEqualTo(NEMO_URL + "/" + "uuid"))
        return delete(urlPathMatching(NEMO_URL + "/.*"))
                .withName("deleteNemoUpdate204")
                .willReturn(aDefaultResponseWithBody("{TEST: \"PING\"}", 204))
                .atPriority(1);
    }

}
