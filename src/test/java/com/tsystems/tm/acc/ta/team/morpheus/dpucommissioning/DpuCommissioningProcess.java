package com.tsystems.tm.acc.ta.team.morpheus.dpucommissioning;

import com.tsystems.tm.acc.data.models.stable.OltDevice;
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

public class DpuCommissioningProcess extends BaseTest {
    private OsrTestContext osrTestContext = OsrTestContext.get();
    private DpuCommissioningRobot dpuCommissioningRobot;
    private boolean isAsyncScenario = false;

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
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DefaultPositive);
        dpuCommissioningRobot.setUpWiremock(olt,dpu,isAsyncScenario);

        String oltEndsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        List<String> onuidCheckValues = new ArrayList<>();
        onuidCheckValues.add(dpu.getEndSz());

        List<String> backhaulidCheckValues = new ArrayList<>();
        backhaulidCheckValues.add(oltEndsz);
        backhaulidCheckValues.add(olt.getOltSlot());
        backhaulidCheckValues.add(olt.getOltPort());

        List<String> deprovisionPortCheckValues = new ArrayList<>();
        deprovisionPortCheckValues.add(oltEndsz);

        List<String> dpuAtOltCheckValues = new ArrayList<>();
        dpuAtOltCheckValues.add(dpu.getEndSz());

        List<String> dpuSealAtOltCheckValues = new ArrayList<>();
        dpuSealAtOltCheckValues.add(dpu.getEndSz().replace("/","_"));

        List<String> dpuEmsValues = new ArrayList<>();
        dpuAtOltCheckValues.add(dpu.getEndSz());

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
        dpuCommissioningRobot.checkGetDpuAtOltConfigCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkPostDpuAtOltConfigCalled(timeOfExecution, dpuAtOltCheckValues);
        dpuCommissioningRobot.checkPostSEALDpuAtOltConfigCalled(timeOfExecution, dpuSealAtOltCheckValues);
        dpuCommissioningRobot.checkPutDpuAtOltConfigCalled(timeOfExecution, dpuAtOltCheckValues);
        dpuCommissioningRobot.checkGetDpuEmsConfigCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkPostDpuEmsConfigCalled(timeOfExecution, dpuEmsValues);
        dpuCommissioningRobot.checkPostSEALDpuEmsConfigCalled(timeOfExecution, dpuAtOltCheckValues);
        //TODO : OLT-RI.PUT.DpuEmsConf called
    }

    @Test(description = "Positive case. DPU-commisioning without errors")
    @Description("Use case: DpuAtOltConfiguration exists")
    public void dpuCommissioningDpuAtOltConfigurationExists(){
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DpuAtOltConfigurationExists);
        dpuCommissioningRobot.setUpWiremock(olt,dpu,isAsyncScenario);

        String oltEndsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        List<String> onuidCheckValues = new ArrayList<>();
        onuidCheckValues.add(dpu.getEndSz());

        List<String> backhaulidCheckValues = new ArrayList<>();
        backhaulidCheckValues.add(oltEndsz);
        backhaulidCheckValues.add(olt.getOltSlot());
        backhaulidCheckValues.add(olt.getOltPort());

        List<String> deprovisionPortCheckValues = new ArrayList<>();
        deprovisionPortCheckValues.add(oltEndsz);

        List<String> dpuAtOltCheckValues = new ArrayList<>();
        dpuAtOltCheckValues.add(dpu.getEndSz());

        List<String> dpuSealAtOltCheckValues = new ArrayList<>();
        dpuSealAtOltCheckValues.add(dpu.getEndSz().replace("/","_"));

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
        dpuCommissioningRobot.checkGetDpuAtOltConfigCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkPostSEALDpuAtOltConfigNotCalled(timeOfExecution, dpuSealAtOltCheckValues);
        dpuCommissioningRobot.checkPostDpuAtOltConfigNotCalled(timeOfExecution, dpuAtOltCheckValues);
        dpuCommissioningRobot.checkPutDpuAtOltConfigNotCalled(timeOfExecution, dpuAtOltCheckValues);

    }

    @Test(description = "Negative case. GET oltResourceInventory returned 400")
    @Description("Negative case. GET oltResourceInventory returned 400")
    public void dpuCommissioningGetDevice400(){
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.GetDevice400);
        dpuCommissioningRobot.setUpWiremock(olt,dpu,isAsyncScenario);

        Long timeOfExecution = System.currentTimeMillis();

        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkGetDeviceDPUCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetDpuPonConnNotCalled(timeOfExecution, dpu.getEndSz());
    }

    @Test(description = "Negative case. GET DpuPonConn returned 400")
    @Description("Negative case. GET DpuPonConn returned 400")
    public void dpuCommissioningGetDpuPonConn400(){
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.GetPonConn400);
        dpuCommissioningRobot.setUpWiremock(olt,dpu,isAsyncScenario);

        String oltEndsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();

        Long timeOfExecution = System.currentTimeMillis();

        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkGetDpuPonConnCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetEthernetLinkNotCalled(timeOfExecution, oltEndsz);

    }

    @Test(description = "Negative case. GET EthernetLink returned 400")
    @Description("Negative case. GET EthernetLink returned 400")
    public void dpuCommissioningGetEthLink400(){
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.FindEthLink400);
        dpuCommissioningRobot.setUpWiremock(olt,dpu,isAsyncScenario);

        String oltEndsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        List<String> onuidCheckValues = new ArrayList<>();
        onuidCheckValues.add(dpu.getEndSz());

        Long timeOfExecution = System.currentTimeMillis();

        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkGetEthernetLinkCalled(timeOfExecution, oltEndsz);
        dpuCommissioningRobot.checkPostOnuIdNotCalled(timeOfExecution, onuidCheckValues);
    }

    @Test(description = "Negative case. GET OnuId returned 400")
    @Description("Negative case. GET OnuId returned 400")
    public void dpuCommissioningGetOnuId400(){
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.GetOnuId400);
        dpuCommissioningRobot.setUpWiremock(olt,dpu,isAsyncScenario);

        String oltEndsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        List<String> onuidCheckValues = new ArrayList<>();
        onuidCheckValues.add(dpu.getEndSz());

        List<String> backhaulidCheckValues = new ArrayList<>();
        backhaulidCheckValues.add(oltEndsz);
        backhaulidCheckValues.add(olt.getOltSlot());
        backhaulidCheckValues.add(olt.getOltPort());

        Long timeOfExecution = System.currentTimeMillis();

        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkPostOnuIdCalled(timeOfExecution,onuidCheckValues);
        dpuCommissioningRobot.checkPostBackhaulidNotCalled(timeOfExecution, backhaulidCheckValues);

    }

    @Test
    @Description("Negative case. GET BackhaulId 400")
    public void dpuCommissioningGetBackhaul400(){
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.GetBackhaul400);

        Long timeOfExecution = System.currentTimeMillis();
        String oltEndsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        List<String> backhaulidCheckValues = new ArrayList<>();
        backhaulidCheckValues.add(oltEndsz);
        backhaulidCheckValues.add(olt.getOltSlot());
        backhaulidCheckValues.add(olt.getOltPort());

        List<String> deprovisionPonPortValues = new ArrayList<>();
        deprovisionPonPortValues.add(oltEndsz);

        dpuCommissioningRobot.setUpWiremock(olt,dpu,isAsyncScenario);
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkPostBackhaulidCalled(timeOfExecution,backhaulidCheckValues);
        dpuCommissioningRobot.checkPostDeprovisioningPortNotCalled(timeOfExecution,deprovisionPonPortValues);
    }

    @Test(description = "Negative case. POST DeprovisionOltPort returned 400")
    @Description("Negative case. POST DeprovisionOltPort returned 400")
    public void dpuCommissioningPostDeprovision400(){
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.PostDeprovisionOltPort400);
        Long timeOfExecution = System.currentTimeMillis();
        String oltEndsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        List<String> deprovisionCheckValues = new ArrayList<>();
        deprovisionCheckValues.add(oltEndsz);

        List<String> configureAncpCheckValues = new ArrayList<>();
        configureAncpCheckValues.add(dpu.getEndSz());
        dpuCommissioningRobot.setUpWiremock(olt,dpu,isAsyncScenario);
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkPostDeprovisioningPortCalled(timeOfExecution,deprovisionCheckValues);
        dpuCommissioningRobot.checkPostConfigAncpNotCalled(timeOfExecution, dpu.getEndSz());
    }

    @Test(description = "Negative case. POST ConfigureANCP returned 400")
    @Description("Negative case. POST ConfigureANCP returned 400")
    public void dpuCommissioningConfigureAncp400(){
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.ConfigureAncp400);
        Long timeOfExecution = System.currentTimeMillis();

        dpuCommissioningRobot.setUpWiremock(olt,dpu,isAsyncScenario);
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkPostConfigAncpCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetAncpSessionNotCalled(timeOfExecution,dpu.getEndSz());
    }

    @Test(description = "Negative case. GET ANCPSession returned 400")
    @Description("Negative case. GET ANCPSession returned 400")
    public void dpuCommissioningGetAncp400(){
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.GetAncpSession400);
        Long timeOfExecution = System.currentTimeMillis();

        dpuCommissioningRobot.setUpWiremock(olt,dpu,isAsyncScenario);
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkGetAncpSessionCalled(timeOfExecution,dpu.getEndSz());
        dpuCommissioningRobot.checkGetDpuAtOltConfigNotCalled(timeOfExecution,dpu.getEndSz());
    }

    @Test(description = "Negative case. POST DeprovisionOltPort returned error in callback")
    @Description("Negative case. POST DeprovisionOltPort returned error in callback")
    public void dpuCommissioningPostDeprovisionCallbackError(){
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.PostDeprovisionCallbackError);
        Long timeOfExecution = System.currentTimeMillis();
        isAsyncScenario = true;

        String oltEndsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        List<String> deprovisionCheckValues = new ArrayList<>();
        deprovisionCheckValues.add(oltEndsz);

        dpuCommissioningRobot.setUpWiremock(olt,dpu,isAsyncScenario);
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkPostDeprovisioningPortCalled(timeOfExecution,deprovisionCheckValues);
        dpuCommissioningRobot.checkPostConfigAncpNotCalled(timeOfExecution,dpu.getEndSz());
    }

    @Test(description = "Negative case. POST ConfigureANCP returned error in callback")
    @Description("Negative case. POST ConfigureANCP returned error in callback")
    public void dpuCommissioningPostConfigureANCPCallbackError(){
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.PostConfigureANCPCallbackError);
        Long timeOfExecution = System.currentTimeMillis();
        isAsyncScenario = true;

        dpuCommissioningRobot.setUpWiremock(olt,dpu,isAsyncScenario);
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkPostConfigAncpCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetAncpSessionNotCalled(timeOfExecution,dpu.getEndSz());
    }

    @Test(description = "Negative case. SEAL POST.DpuAtOltConf returned error in callback")
    @Description("Negative case. SEAL POST.DpuAtOltConf returned error in callback")
    public void dpuCommissioningPostSealDpuAtOltConfigCallbackError(){
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.PostSealDpuAtOltConfigCallbackError);
        Long timeOfExecution = System.currentTimeMillis();
        isAsyncScenario = true;
        List<String> dpuSealAtOltCheckValues = new ArrayList<>();
        dpuSealAtOltCheckValues.add(dpu.getEndSz().replace("/","_"));
        List<String> dpuAtOltCheckValues = new ArrayList<>();
        dpuAtOltCheckValues.add(dpu.getEndSz());

        dpuCommissioningRobot.setUpWiremock(olt,dpu,isAsyncScenario);
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkPostSEALDpuAtOltConfigCalled(timeOfExecution, dpuSealAtOltCheckValues);
        dpuCommissioningRobot.checkPutDpuAtOltConfigNotCalled(timeOfExecution, dpuAtOltCheckValues);
    }

    @Test(description = "Negative case. SEAL POST.DpuEmsConf returned error in callback")
    @Description("Negative case. SEAL POST.DpuEmsConf returned error in callback")
    public void dpuCommissioningPostSealDpuEmsConfigCallbackError(){
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.PostSealDpuEmsConfigCallbackError);
        Long timeOfExecution = System.currentTimeMillis();
        isAsyncScenario = true;
        List<String> dpuSealEmsCheckValues = new ArrayList<>();
        dpuSealEmsCheckValues.add(dpu.getEndSz());

        dpuCommissioningRobot.setUpWiremock(olt,dpu,isAsyncScenario);
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkPostSEALDpuEmsConfigCalled(timeOfExecution, dpuSealEmsCheckValues);
        //TODO :
        //dpuCommissioningRobot.checkPutDpuEmsConfigNotCalled(timeOfExecution, dpuSealEmsCheckValues);
    }
}
