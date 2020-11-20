package com.tsystems.tm.acc.ta.team.morpheus.mobiledpubff;

import com.tsystems.tm.acc.ta.robot.osr.MobileDpuBffRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class WorkorderDpuInstallation extends BaseTest  {
    private MobileDpuBffRobot mobileDpuBffRobot;

@BeforeClass
public void init(){
    mobileDpuBffRobot = new MobileDpuBffRobot();
}

@Test
    public void workorderDpuInstallation(){
    mobileDpuBffRobot.getWorkorder(2);
}

}
