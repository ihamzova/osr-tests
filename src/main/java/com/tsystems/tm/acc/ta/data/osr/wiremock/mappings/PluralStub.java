package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.osr.mappers.PluralTnpMapper;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.rebell.client.invoker.JSON;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class PluralStub extends AbstractStubMapping {

    public static final String PLURAL_URL = "/upstream-partner/tardis/resource-order-resource-inventory/networkElementPlanning/v2/Ueweg/Rsrv1KnotenLesenD3";

    public MappingBuilder postPluralResponse(String negName, int httpCode, A4ImportCsvData csvData) {
        return post(urlPathMatching(PLURAL_URL))
                .withName("getPluralTnp")
                .willReturn(aDefaultResponseWithBody(serialize(new PluralTnpMapper().getJsonIRsrv1KnotenLesenD3Response(csvData)), httpCode))
                .withRequestBody(containing(negName));
    }

    private String serialize(Object obj) {
        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }

}
