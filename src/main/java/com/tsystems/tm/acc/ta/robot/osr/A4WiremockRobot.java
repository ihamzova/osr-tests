package com.tsystems.tm.acc.ta.robot.osr;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;

public class A4WiremockRobot {

    public void checkSyncRequest(String url, String method, int count) {
        WireMockFactory.get().retrieve(
                exactly(count),
                newRequestPattern(
                        RequestMethod.fromString(method),
                        urlPathEqualTo(url)), 1000);
    }
}
