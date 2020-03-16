package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.data.models.portprovisioning.PortProvisioning;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkElementGroupDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.TerminationPointDto;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.internal.client.model.TpRefDto;
import io.qameta.allure.Step;

public class A4PreProvisioningRobot {
    private A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
    private WgA4PreProvisioningRobot wgA4PreProvisioningRobot = new WgA4PreProvisioningRobot();

    @Step("Fill A4 RI db and clear AL RI db")
    public void prepareData(NetworkElementGroupDto networkElementGroup, TerminationPointDto terminationPoint) {
        a4ResourceInventoryRobot.createNetworkElementGroup(networkElementGroup);
        a4ResourceInventoryRobot.createTerminationPoint(terminationPoint);

        accessLineRiRobot.clearDatabase();
    }

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

    @Step("Delete network element and termination point, clear AL RI db")
    public void clearData(String networkElementUuid, String terminationPointUuid) {
        a4ResourceInventoryRobot.deleteNetworkElementGroup(networkElementUuid);
        a4ResourceInventoryRobot.deleteTerminationPoint(terminationPointUuid);

        accessLineRiRobot.clearDatabase();
    }
}
