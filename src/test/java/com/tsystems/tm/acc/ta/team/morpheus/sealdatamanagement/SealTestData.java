package com.tsystems.tm.acc.ta.team.morpheus.sealdatamanagement;

import com.tsystems.tm.acc.ta.robot.osr.WiremockRobot;
import org.testng.annotations.Test;

import java.io.File;

public class SealTestData {

    private WiremockRobot wiremockRobot = new WiremockRobot();

    @Test(enabled = false)
    public void TestMock() {
        wiremockRobot.initializeWiremock(new File(getClass().getResource("/team/morpheus/wiremock").getFile()));
    }

}
