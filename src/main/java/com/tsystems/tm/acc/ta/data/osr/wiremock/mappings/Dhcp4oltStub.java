package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.osr.mappers.Dhcp4oltMapper;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.seal.external.v1_2_01.client.invoker.JSON;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;

public class Dhcp4oltStub extends AbstractStubMapping {

    public static final String DHCP_CONFIGURATION_PATTERN =  "/((resource-order-resource-inventory/v1/oltDhcpConfiguration/)?)";

    public MappingBuilder getOlt200OltNotFound(OltDevice oltDevice) {
        return get(urlPathMatching(DHCP_CONFIGURATION_PATTERN + "olt"))
                .withName("getOlt200")
                .atPriority(3)
                .willReturn(aDefaultResponseWithBody(serialize(new Dhcp4oltMapper().getOLTGetResponseOltNotFound()), HTTP_CODE_OK_200))
                .withQueryParam("endsz_olt", equalTo((oltDevice.getEndsz()).replace("/", "_")));
    }

    public MappingBuilder getOlt200(OltDevice oltDevice) {
        return get(urlPathMatching(DHCP_CONFIGURATION_PATTERN + "olt"))
                .withName("getOlt200")
                .willReturn(aDefaultResponseWithBody(serialize(new Dhcp4oltMapper().getOLTGetResponse(oltDevice)), HTTP_CODE_OK_200))
                .withQueryParam("endsz_olt", equalTo((oltDevice.getEndsz()).replace("/", "_")));
    }

    public MappingBuilder getBng200(OltDevice oltDevice) {
        return get(urlPathMatching(DHCP_CONFIGURATION_PATTERN + "bng"))
                .withName("getBng200")
                .willReturn(aDefaultResponseWithBody(serialize(new Dhcp4oltMapper().getBNGGetResponse(oltDevice)), HTTP_CODE_OK_200))
                .withQueryParam("endsz", equalTo((oltDevice.getBngEndsz()).replace("/", "_")));
    }

    private String serialize(Object obj) {
        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }
}
