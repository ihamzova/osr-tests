package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.tsystems.tm.acc.ta.data.HttpConstants.*;

public class MerlinStub extends AbstractStubMapping {

    public MappingBuilder postMerlinCallbackResponse202() {
        return post(urlPathMatching("/test_url"))
                 .willReturn(aDefaultResponseWithBody(null, HTTP_CODE_ACCEPTED_202))
                .atPriority(9);
    }

    public MappingBuilder postMercuryResponse409() {
//        return post(urlPathMatching("/resource-order-resource-inventory/v1/a10nspA4"))
//        return post(urlPathMatching("/api/a10nspInventory/v1/a10nspA4"))
        return post(urlPathMatching(".*/v1/a10nspA4.*"))
                .willReturn(aDefaultResponseWithBody(null, HTTP_CODE_CONFLICT_409))
                .atPriority(9);
    }

}
