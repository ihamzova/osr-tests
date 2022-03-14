package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;

import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_ACCEPTED_202;

public class PluralStub extends AbstractStubMapping {

    // Endpoint: /upstream-partner/tardis/resource-order-resource-inventory/networkElementPlanning/v2/Ueweg/Rsrv1KnotenLesenD3
    public static final String PLURAL_URL = "/upstream-partner/tardis/resource-order-resource-inventory/networkElementPlanning/v2/Ueweg/Rsrv1KnotenLesenD3";

    public MappingBuilder postPluralCallbackResponce202() {
        return post(urlPathMatching(PLURAL_URL))
                 .willReturn(aDefaultResponseWithBody(null, HTTP_CODE_ACCEPTED_202))
                .atPriority(9);
    }

    public MappingBuilder postPluralCallbackResponce() {

        //aResponse().build().getBody().

        return post(urlPathMatching(PLURAL_URL))
                .willReturn(aDefaultResponseWithBody(null, HTTP_CODE_ACCEPTED_202))
                .atPriority(9);
    }







}
