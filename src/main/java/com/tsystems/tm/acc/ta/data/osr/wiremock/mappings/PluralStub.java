package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.osr.mappers.PluralTnpMapper;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.data.osr.models.PluralTnpData;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.rebell.client.invoker.JSON;

import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_CREATED_201;

public class PluralStub extends AbstractStubMapping {

    // Plural-Endpoint: /upstream-partner/tardis/resource-order-resource-inventory/networkElementPlanning/v2/Ueweg/Rsrv1KnotenLesenD3
    // UI-Endpoint: /pluralAlignment
    public static final String PLURAL_URL = "/upstream-partner/tardis/resource-order-resource-inventory/networkElementPlanning/v2/Ueweg/Rsrv1KnotenLesenD3";

    public MappingBuilder postPluralResponce201(PluralTnpData pluralTnpData) {
        System.out.println("+++ PluralStub Mock antwortet mit 201");
        System.out.println("+++ pluralTnpData: "+pluralTnpData);
        return post(urlPathMatching(PLURAL_URL))
                .withName("getPluralTnp201")
                .willReturn(aDefaultResponseWithBody(serialize(new PluralTnpMapper().getJsonIRsrv1KnotenLesenD3Response(pluralTnpData)), HTTP_CODE_CREATED_201));
    }


    public MappingBuilder postPluralResponce201(A4ImportCsvData csvData) {
        System.out.println("+++ PluralStub Mock antwortet mit 201");
        System.out.println("+++ csvData: "+csvData);
        return post(urlPathMatching(PLURAL_URL))
                .withName("getPluralTnp201")
                .willReturn(aDefaultResponseWithBody(serialize(new PluralTnpMapper().getJsonIRsrv1KnotenLesenD3Response(csvData)), HTTP_CODE_CREATED_201));
    }
/*
    public MappingBuilder postPluralResponce() {
        System.out.println("+++ PluralStub Mock antwortet mit 201, Daten werden nicht übergeben");
        return post(urlPathMatching(PLURAL_URL))
                .willReturn(aDefaultResponseWithBody(serialize(new PluralTnpMapper().getJsonIRsrv1KnotenLesenD3ResponseEmpty()), HTTP_CODE_CREATED_201))
                .atPriority(9);
    }

 */

    private String serialize(Object obj) {
        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }




}
