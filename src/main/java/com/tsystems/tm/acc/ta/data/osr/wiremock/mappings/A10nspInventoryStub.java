package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.osr.mappers.A10nspA4DtoMapper;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.rebell.client.invoker.JSON;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;

public class A10nspInventoryStub extends AbstractStubMapping {

    public static final String A10NSP_A4CONTROLLER_URL = "/resource-order-resource-inventory/v1/a10nspA4";

    public MappingBuilder postA10nspA4200(String carrierBsaReference, String rahmenvertragsNummer) {
        return get(urlPathEqualTo(A10NSP_A4CONTROLLER_URL))
                .withName("postA10nspA4200")
                .willReturn(aDefaultResponseWithBody(serialize(new A10nspA4DtoMapper().getA10nspA4Dto(carrierBsaReference, rahmenvertragsNummer)), HTTP_CODE_OK_200))
                .withQueryParam("rahmenvertragsnummer", equalTo(rahmenvertragsNummer))
                .withQueryParam("carrierBsaReference", equalTo(carrierBsaReference))
                .atPriority(9);
    }

    private String serialize(Object obj) {
        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }

}
