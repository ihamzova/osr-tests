package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.data.models.PortProvisioning;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.internal.client.model.TpRefDto;
import io.qameta.allure.Step;

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
}
