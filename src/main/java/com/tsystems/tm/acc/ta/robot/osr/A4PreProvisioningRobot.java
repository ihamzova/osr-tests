package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.data.models.stable.PortProvisioning;
import com.tsystems.tm.acc.ta.helpers.WiremockHelper;
import com.tsystems.tm.acc.ta.helpers.wiremock.WiremockRequestPatternBuilder;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.internal.client.model.TpRefDto;
import com.tsystems.tm.acc.tests.wiremock.client.model.RequestFind;
import com.tsystems.tm.acc.tests.wiremock.client.model.RequestPattern;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.util.List;

public class A4PreProvisioningRobot {
    private AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
    private WgA4PreProvisioningRobot wgA4PreProvisioningRobot = new WgA4PreProvisioningRobot();

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
        RequestPattern requestPattern = new WiremockRequestPatternBuilder()
                .withMethod("POST")
                .withUrlPathPattern(".*/v1/a4/accessLines")
                .build();
        List<RequestFind> requests = WiremockHelper.requestsFindByCustomPatternAmount(requestPattern, 1).getRequests();
        Assert.assertTrue(requests.size() >= 1);
    }
}
