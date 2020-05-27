package com.tsystems.tm.acc.ta.team.morpheus.dpucommissioning;

import com.tsystems.tm.acc.data.models.OltDevice;
import com.tsystems.tm.acc.data.osr.models.dpu.DpuCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.DpuCommissioningRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class DpuCommissioningOneCase extends BaseTest {
    private OsrTestContext osrTestContext = OsrTestContext.get();
    private DpuCommissioningRobot dpuCommissioningRobot;

    @BeforeClass
    public void init(){
        dpuCommissioningRobot = new DpuCommissioningRobot();
    }
    @AfterMethod
    public void cleanup(){
        dpuCommissioningRobot.cleanup();
    }

    @Test(description = "Positive case. DPU-commisioning without errors")
    @TmsLink("DIGIHUB-62083")
    @Description("Positive case. DPU-commisioning without errors")
    public void dpuCommissioningPositive(){
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DpuCommissioningPositive);
        dpuCommissioningRobot.setUpWiremock(olt,dpu);

        String oltEndsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        List<String> onuidCheckValues = new ArrayList<>();
        onuidCheckValues.add(dpu.getEndSz());

        List<String> backhaulidCheckValues = new ArrayList<>();
        backhaulidCheckValues.add(oltEndsz);
        backhaulidCheckValues.add(olt.getOltSlot());
        backhaulidCheckValues.add(olt.getOltPort());

        List<String> deprovisionPortCheckValues = new ArrayList<>();
        deprovisionPortCheckValues.add(oltEndsz);

        Long timeOfExecution = System.currentTimeMillis();

        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkGetDeviceDPUCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetDpuPonConnCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetEthernetLinkCalled(timeOfExecution, oltEndsz);
        dpuCommissioningRobot.checkPostOnuIdCalled(timeOfExecution,onuidCheckValues);
        dpuCommissioningRobot.checkPostBackhaulidCalled(timeOfExecution, backhaulidCheckValues);
        dpuCommissioningRobot.checkPostDeprovisioningPortCalled(timeOfExecution,deprovisionPortCheckValues);
        dpuCommissioningRobot.checkPostConfigAncpCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetAncpSessionCalled(timeOfExecution, dpu.getEndSz());

    }
}
