package com.tsystems.tm.acc.ta.robot.osr;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.tsystems.tm.acc.ta.data.osr.models.A10nspA4Dto;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.A10nspInventoryStub.A10NSP_A4CONTROLLER_URL;

public class A4WiremockA10nspA4Robot {

    public void checkSyncRequestToA10nspA4Wiremock(String carrierBsaReference, String rahmenvertragsnummer, String method, int count) {

        WireMockFactory.get()
                .retrieve(
                        exactly(count),
                        newRequestPattern(
                                RequestMethod.fromString(method),
                                urlPathEqualTo(A10NSP_A4CONTROLLER_URL))
                                .withQueryParam("rahmenvertragsnummer", equalTo(rahmenvertragsnummer))
                                .withQueryParam("carrierBsaReference", equalTo(carrierBsaReference)));
    }

    public void checkSyncRequestToA10nspA4Wiremock(A10nspA4Dto a10Nsp, String method, int count) {
        final String rvNumber = a10Nsp.getRahmenvertragsnummer();
        final String cBsaRef = a10Nsp.getCarrierBsaReference();

        WireMockFactory.get()
                .retrieve(
                        exactly(count),
                        newRequestPattern(
                                RequestMethod.fromString(method),
                                urlPathEqualTo(A10NSP_A4CONTROLLER_URL))
                                .withQueryParam("rahmenvertragsnummer", equalTo(rvNumber))
                                .withQueryParam("carrierBsaReference", equalTo(cBsaRef)));
    }

}
