package com.tsystems.tm.acc.ta.data.mercury.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;

import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;

public class DeviceDiscoveryCallbackStub extends AbstractStubMapping {

    public static final String DISCOVERY_CALLBACK_URL = "/autotestCbDiscoveryStart/";

    public MappingBuilder addDiscoveryCallbackReceiver200() {
        return post(urlPathEqualTo(DISCOVERY_CALLBACK_URL))
                .withName("addDiscoveryCallbackReceiver200")

                .willReturn(aDefaultResponseWithBody(null, HTTP_CODE_OK_200));
    }
}
