package com.tsystems.tm.acc.ta.team.morpheus.sealdatamanagement;

import com.tsystems.tm.acc.ta.robot.osr.WiremockRobot;
import org.junit.Test;
import org.testng.annotations.BeforeClass;

public class SealTestData {

    private WiremockRobot wiremockRobot = new WiremockRobot();

    /*@BeforeClass
    public void SetMock(){
        wiremockRobot.initializeWiremock();
    }*/


    @Test
    public void TestMock(){

        wiremockRobot.initializeWiremock();
    }

}
