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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DpuCommissioningNew extends BaseTest {
    private OsrTestContext osrTestContext = OsrTestContext.get();
    private DpuCommissioningRobot dpuCommissioningRobot;

    @BeforeClass
    public void init(){
        dpuCommissioningRobot = new DpuCommissioningRobot();
    }
//    @AfterClass
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
        Long timeOfExecution = System.currentTimeMillis();
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkGetDeviceDPU(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetDpuPonConn(timeOfExecution, dpu.getEndSz(), dpu.getPonPortNumber());
    }

    @Test(description = "Negative case. GET oltResourceInventory returned 400")
    @Description("Negative case. GET oltResourceInventory returned 400")
    public void dpuCommissioningGetDevice400(){
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DpuCommissioningGetDevice400);
        dpuCommissioningRobot.setUpWiremock(olt,dpu);
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.verifyDpu(dpu);
    }

    @Test(description = "Negative case. GET DpuPonConn returned 400")
    @Description("Negative case. GET DpuPonConn returned 400")
    public void dpuCommissioningGetDpuPonConn400(){
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DpuCommissioningGetPonConn400);
        dpuCommissioningRobot.setUpWiremock(olt,dpu);
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.verifyDpu(dpu);
    }

    @Test(description = "Negative case. GET EthernetLink returned 400")
    @Description("Negative case. GET EthernetLink returned 400")
    public void dpuCommissioningGetEthLink400(){
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DpuCommissioningFindEthLink400);
        dpuCommissioningRobot.setUpWiremock(olt,dpu);
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.verifyDpu(dpu);
    }

    @Test(description = "Negative case. GET OnuId returned 400")
    @Description("Negative case. GET OnuId returned 400")
    public void dpuCommissioningGetOnuId400(){
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DpuCommissioningGetOnuId400);
        dpuCommissioningRobot.setUpWiremock(olt,dpu);
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.verifyDpu(dpu);
    }

    @Test(description = "Negative case. GET BackhaulId returned 400")
    @Description("Negative case. GET BackhaulId 400")
    public void dpuCommissioningGetBackhaul400(){
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DpuCommissioningGetBackhaul400);
        dpuCommissioningRobot.setUpWiremock(olt,dpu);
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.verifyDpu(dpu);
    }

    @Test(description = "Negative case. POST DeprovisionOltPort returned 400")
    @Description("Negative case. POST DeprovisionOltPort returned 400 400")
    public void dpuCommissioningPostDeprovision400(){
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DpuCommissioningPostDeprovisionOltPort400);
        dpuCommissioningRobot.setUpWiremock(olt,dpu);
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.verifyDpu(dpu);
    }

    @Test(description = "Negative case. GET ANCPSession returned 400")
    @Description("Negative case. GET ANCPSession returned 400 400")
    public void dpuCommissioningGetAncp400(){
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DpuCommissioningGetAncpSession400);
        dpuCommissioningRobot.setUpWiremock(olt,dpu);
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.verifyDpu(dpu);
    }
}
