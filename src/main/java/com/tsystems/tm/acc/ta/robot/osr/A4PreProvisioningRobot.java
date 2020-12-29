package com.tsystems.tm.acc.ta.robot.osr;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.internal.v1_5_0.client.model.TpRefDto;
import io.qameta.allure.Step;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.PreProvisioningStub.ACCESS_LINE_URL;

public class A4PreProvisioningRobot {

    private final AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
    private final WgA4PreProvisioningRobot wgA4PreProvisioningRobot = new WgA4PreProvisioningRobot();

    @Step("Check results")
    public void checkResults(PortProvisioning port) {
        accessLineRiRobot.checkHomeIdsCount(port);
        accessLineRiRobot.checkLineIdsCount(port);
        accessLineRiRobot.checkPortParametersForLines(port);
        accessLineRiRobot.checkA4LineParameters(port);
    }

    @Step("Start preprovisioning")
    public void startA4PreProvisioning(TpRefDto tpRef) {
        wgA4PreProvisioningRobot.startPreProvisioning(tpRef);
    }

    @Step("Clear AL RI db")
    public void clearData() {
        accessLineRiRobot.clearDatabase();
    }

    @Step("Check if POST request to a4-preprovisioning wiremock has happened")
    public void checkPostToPreprovisioningWiremock() {
        WireMockFactory.get()
                .retrieve(
                        newRequestPattern(
                                RequestMethod.POST,
                                urlPathEqualTo(ACCESS_LINE_URL)));
    }

}
