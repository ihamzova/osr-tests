package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class NemoStub extends AbstractStubMapping {

    public static final String NEMO_URL = "/api/v1/planning/logicalResource";

    public MappingBuilder putNemoUpdate201() {
        return put(urlPathMatching(NEMO_URL + "/.*"))
                .withName("putNemoUpdate201")
                .willReturn(aDefaultResponseWithBody("{{{request.body}}}", 201))
                .atPriority(1);
    }

    public MappingBuilder deleteNemoUpdate204() {
        return delete(urlPathMatching(NEMO_URL + "/.*"))
                .withName("deleteNemoUpdate204")
                .willReturn(aDefaultResponseWithBody(null, 204))
                .atPriority(1);
    }

}
