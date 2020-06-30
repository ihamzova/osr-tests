package com.tsystems.tm.acc.ta.team.morpheus.dpucommissioning;

import com.tsystems.tm.acc.ta.robot.osr.ETCDRobot;
import org.testng.annotations.Test;

public class ETCDTest {
    ETCDRobot etcdRobot = new ETCDRobot();

    @Test
    public void testEtcd() {
        //etcdRobot.checkIfEtcdKeyContainsValue("/DISCOVERY/DISCOVERY/79af56af-0afa-4d68-ab45-a107d9b70b23", "http://olt-commissioning-app/callback/discovery");
        //etcdRobot.checkIfEtcdDirContainsKey("/DISCOVERY/DISCOVERY", "/79af56af-0afa-4d68-ab45-a107d9b70b23");

        //etcdRobot.checkIfEtcdKeyContainsValue("/dpu-commissioning/14b2a08d-73f8-4a9e-a1d5-392a4552e160", "{\\\"source\\\":\\\"dpu-commissioning\\\",\\\"type\\\":\\\"info\\\",\\\"message\\\":\\\"EXECUTE [Read DPU device data] ProcessInstanceId [1643e7cf-bab4-11ea-a2ab-0a58c0a8cff7] BusinessKey [dpu-commissioning/14b2a08d-73f8-4a9e-a1d5-392a4552e160]\\\"}");

        etcdRobot.checkEtcdValue("dpu-commissioning/bbf4c2c6-f987-4cdc-9882-48ce020993dc", "EXECUTE [Read DPU device data]");
    }
}
