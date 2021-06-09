package com.tsystems.tm.acc.ta.robot.osr;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import io.qameta.allure.Step;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.PreProvisioningStub.ACCESS_LINE_URL;

public class WgA4PreProvisioningWiremockRobot {

    @Step("Check if POST request to a4-preprovisioning wiremock has happened")
    public void checkPostToPreprovisioningWiremock() {
        WireMockFactory.get()
                .retrieve(
                        newRequestPattern(
                                RequestMethod.POST,
                                urlPathEqualTo(ACCESS_LINE_URL)));
    }

}
