package com.tsystems.tm.acc.ta.team.morpheus.sealdatamanagement;

import com.tsystems.tm.acc.ta.robot.osr.WiremockRobot;
import org.junit.Ignore;
import org.junit.Test;

public class SealTestData {

    private WiremockRobot wiremockRobot = new WiremockRobot();

    @Test

    public void TestMock() {

        wiremockRobot.initializeWiremock("/team.morpheus/wiremock");
    }

}
