package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.tsystems.tm.acc.ta.data.HttpConstants.*;

public class A4ResourceInventoryStub extends AbstractStubMapping {

    public MappingBuilder getTPWith500() {
        return get(urlPathMatching("/terminationPoints/.*"))
                .withName("getTP500")
                .willReturn(aDefaultResponseWithBody("", HTTP_CODE_INTERNAL_SERVER_ERROR_500))
                .atPriority(1);
    }

}
