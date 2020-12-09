package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_CREATED_201;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_NO_CONTENT_204;

public class NemoStub extends AbstractStubMapping {

    public static final String NEMO_URL = "/api/v1/planning/logicalResource";

    public MappingBuilder putNemoUpdate201() {
        return put(urlPathMatching(NEMO_URL + "/.*"))
                .withName("putNemoUpdate201")
                .willReturn(aDefaultResponseWithBody("{{{request.body}}}", HTTP_CODE_CREATED_201))
                .atPriority(1);
    }

    public MappingBuilder deleteNemoUpdate204() {
        return delete(urlPathMatching(NEMO_URL + "/.*"))
                .withName("deleteNemoUpdate204")
                .willReturn(aDefaultResponseWithBody(null, HTTP_CODE_NO_CONTENT_204))
                .atPriority(1);
    }

}
