package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.osr.mappers.A10nspA4DtoMapper;
import com.tsystems.tm.acc.ta.data.osr.models.UewegData;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.rebell.client.invoker.JSON;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;

public class A10nspInventoryStub extends AbstractStubMapping {

    public static final String A10NSP_A4CONTROLLER_URL = "/api/a10nspInventory/v1/a10nspA4";   // "/resource-order-resource-inventory/v1/a10nspA4";

    public MappingBuilder postA10nspA4200(String carrierBsaReference, String rahmenvertragsnummer) {
        return get(urlPathEqualTo(A10NSP_A4CONTROLLER_URL))
                .withName("postA10nspA4200")
                .willReturn(aDefaultResponseWithBody(serialize(new A10nspA4DtoMapper().getA10nspA4Dto(carrierBsaReference, rahmenvertragsnummer)), HTTP_CODE_OK_200))
                .withQueryParam("rahmenvertragsnummer", equalTo((rahmenvertragsnummer)))
                .withQueryParam("carrierBsaReference", equalTo(carrierBsaReference));
    }

    // Curl
    //
    //curl -X POST "http://localhost:8198/api/a10nspInventory/v1/a10nspA4?carrierBsaReference=carrier123&rahmenvertragsnummer=rahm123"

    private String serialize(Object obj) {
        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }
}
