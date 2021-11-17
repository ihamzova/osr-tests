package com.tsystems.tm.acc.ta.robot.osr;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import io.qameta.allure.Step;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.DeProvisioningStub.DEPROV_ACCESS_LINE_URL;
import static com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.PreProvisioningStub.ACCESS_LINE_URL;

public class WgA4ProvisioningWiremockRobot {

    @Step("Check if POST request to a4-preprovisioning wiremock has happened")
    public void checkPostToPreprovisioningWiremock() {
        WireMockFactory.get()
                .retrieve(
                        newRequestPattern(
                                RequestMethod.POST,
                                urlPathEqualTo(ACCESS_LINE_URL)));
    }

    @Step("Check if POST request to a4-deprovisioning wiremock has happened")
    public String checkPostToDeprovisioningWiremock(int count) {
        List<LoggedRequest> requestList = WireMockFactory.get()
                .retrieve(
                        exactly(count),
                        newRequestPattern(
                                RequestMethod.POST,
                                urlPathEqualTo(DEPROV_ACCESS_LINE_URL)));

        if (!requestList.isEmpty()) {
            return new String(requestList.get(0).getBody());
        } else
            return null;
    }

}
