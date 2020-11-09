package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class NemoStub extends AbstractStubMapping {

    public static final String NEMO_URL = "/resource-order-resource-inventory/v1/updateNemoTask";

    public MappingBuilder postNemoUpdate201() {
        return get(urlPathEqualTo(NEMO_URL))
                .withName("postNemoUpdate201")
                .willReturn(aDefaultResponseWithBody(null, 201))
                .atPriority(1);
    }

}
