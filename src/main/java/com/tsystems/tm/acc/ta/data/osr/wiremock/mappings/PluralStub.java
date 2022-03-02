package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.osr.mappers.PreProvisioningMapper;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;

import javax.ws.rs.HttpMethod;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_ACCEPTED_202;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_MS;

public class PluralStub extends AbstractStubMapping {

    // Endpoint: /upstream-partner/tardis/resource-order-resource-inventory/networkElementPlanning/v2/Ueweg/Rsrv1KnotenLesenD3
    public static final String PLURAL_URL = "/upstream-partner/tardis/resource-order-resource-inventory/networkElementPlanning/v2/Ueweg/Rsrv1KnotenLesenD3";

    public MappingBuilder postPluralCallbackResponce202() {
        return post(urlPathMatching(PLURAL_URL))
                 .willReturn(aDefaultResponseWithBody(null, HTTP_CODE_ACCEPTED_202))
                .atPriority(9);
    }



}
