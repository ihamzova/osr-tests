package com.tsystems.tm.acc.ta.team.morpheus.sealdatamanagement;

import com.tsystems.tm.acc.ta.robot.osr.WiremockRobot;
import org.testng.annotations.Test;

public class SealTestData {

    private WiremockRobot wiremockRobot = new WiremockRobot();

    @Test(enabled = false)
    public void TestMock() {

        wiremockRobot.initializeWiremock("/team.morpheus/wiremock");
    }

}
