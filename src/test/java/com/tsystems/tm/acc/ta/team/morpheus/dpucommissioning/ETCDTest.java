package com.tsystems.tm.acc.ta.team.morpheus.dpucommissioning;

import com.tsystems.tm.acc.ta.robot.osr.ETCDRobot;
import org.testng.annotations.Test;

public class ETCDTest {
    ETCDRobot etcdRobot = new ETCDRobot();

    @Test
    public void testEtcd() {
        etcdRobot.checkIfEtcdKeyContainsValue("/DISCOVERY/DISCOVERY/79af56af-0afa-4d68-ab45-a107d9b70b23", "http://olt-commissioning-app/callback/discovery");
        etcdRobot.checkIfEtcdDirContainsKey("/DISCOVERY/DISCOVERY", "/79af56af-0afa-4d68-ab45-a107d9b70b23");
    }
}
