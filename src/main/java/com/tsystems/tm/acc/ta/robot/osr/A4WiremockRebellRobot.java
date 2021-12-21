package com.tsystems.tm.acc.ta.robot.osr;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.RebellStub.REBELL_UEWEG_URL;

public class A4WiremockRebellRobot {

    public void checkSyncRequestToRebellWiremock(String endSz, String method, int count) {
        final String endSzUnderline = endSz.replace("/", "_");

        WireMockFactory.get()
                .retrieve(
                        exactly(count),
                        newRequestPattern(
                                RequestMethod.fromString(method),
                                urlPathEqualTo(REBELL_UEWEG_URL ))
                                .withQueryParam("endsz", equalTo(endSzUnderline)));
    }

}
