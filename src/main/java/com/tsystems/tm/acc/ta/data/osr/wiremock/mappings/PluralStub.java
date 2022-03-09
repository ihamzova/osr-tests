package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.osr.mappers.PluralTnpMapper;
import com.tsystems.tm.acc.ta.data.osr.models.PluralTnpData;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.rebell.client.invoker.JSON;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;

public class PluralStub extends AbstractStubMapping {

    // Endpoint: /upstream-partner/tardis/resource-order-resource-inventory/networkElementPlanning/v2/Ueweg/Rsrv1KnotenLesenD3
    public static final String PLURAL_URL = "/upstream-partner/tardis/resource-order-resource-inventory/networkElementPlanning/v2/Ueweg/Rsrv1KnotenLesenD3";

    public MappingBuilder postPluralResponce200(PluralTnpData pluralTnpData) {
        System.out.println("+++ PluralStub Mock antwortet mit 200");
        System.out.println("+++ pluralTnpData: "+pluralTnpData);
        return post(urlPathMatching(PLURAL_URL))
                .withName("getPluralTnp200")
                .willReturn(aDefaultResponseWithBody(serialize(new PluralTnpMapper().getJsonIRsrv1KnotenLesenD3Response(pluralTnpData)), HTTP_CODE_OK_200))
                .withQueryParam("negName", equalTo(pluralTnpData.getNegName()) );
    }

    public MappingBuilder postPluralResponce() {
        System.out.println("+++ PluralStub Mock antwortet mit 200, Daten werden nicht übergeben");
        //aResponse().build().getBody().

        return post(urlPathMatching(PLURAL_URL))
                .willReturn(aDefaultResponseWithBody(serialize(new PluralTnpMapper().getJsonIRsrv1KnotenLesenD3ResponseEmpty()), HTTP_CODE_OK_200))
                .atPriority(9);
    }

    public MappingBuilder postPluralCallbackResponce(String negName) {
        //aResponse().build().getBody().
        return post(urlPathMatching(PLURAL_URL))
                .willReturn(aDefaultResponseWithBody(null, HTTP_CODE_OK_200))
                .atPriority(9);
    }



    private String serialize(Object obj) {
        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }




}
