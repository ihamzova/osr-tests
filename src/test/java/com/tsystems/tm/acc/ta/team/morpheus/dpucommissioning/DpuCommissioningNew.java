package com.tsystems.tm.acc.ta.team.morpheus.dpucommissioning;

import com.tsystems.tm.acc.data.models.OltDevice;
import com.tsystems.tm.acc.data.osr.models.dpu.DpuCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.api.osr.DpuCommissioningClient;
import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.DpuCommissioningRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

public class DpuCommissioningNew extends BaseTest {
    private DpuCommissioningClient dpuCommissioningClient;
    private OsrTestContext osrTestContext = OsrTestContext.get();
    private DpuCommissioningRobot dpuCommissioningRobot;

    @BeforeClass
    public void init(){dpuCommissioningClient = new DpuCommissioningClient();
        dpuCommissioningRobot = new DpuCommissioningRobot();}
    @Test
    public void makeItWorks(){
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DpuCommissioningPositive);
        try {
            dpuCommissioningRobot.setUpWiremock(olt,dpu);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
