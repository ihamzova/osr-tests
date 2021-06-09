package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_ACCEPTED_202;

public class MerlinStub extends AbstractStubMapping {



    public MappingBuilder postMerlinCallbackResponce202() {
        return post(urlPathMatching("/test_url"))
                 .willReturn(aDefaultResponseWithBody(null, HTTP_CODE_ACCEPTED_202))
                .atPriority(9);
    }
}
