package com.tsystems.tm.acc.ta.team.morpheus.dpucommissioning;

import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.data.osr.models.dpu.DpuCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import com.tsystems.tm.acc.ta.db.etcd.Node;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.DpuCommissioningRobot;
import com.tsystems.tm.acc.ta.robot.osr.ETCDRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DpuCommissioningProcess extends BaseTest {
    private OsrTestContext osrTestContext = OsrTestContext.get();
    private DpuCommissioningRobot dpuCommissioningRobot;
    private ETCDRobot etcdRobot;
    private boolean isAsyncScenario = false;

    @BeforeClass
    public void init() {
        dpuCommissioningRobot = new DpuCommissioningRobot();
        etcdRobot = new ETCDRobot();
    }

    @AfterMethod
    public void cleanup() {
        dpuCommissioningRobot.cleanup();
    }

    @Test(description = "Positive case. DPU-commisioning without errors")
    @TmsLink("DIGIHUB-62083")
    @Description("Positive case. DPU-commisioning without errors")
    public void dpuCommissioningPositive() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DefaultPositive);
        dpuCommissioningRobot.setUpWiremock(olt, dpu, isAsyncScenario);

        String oltEndsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        List<String> onuidCheckValues = new ArrayList<>();
        onuidCheckValues.add(dpu.getEndSz());

        List<String> backhaulidCheckValues = new ArrayList<>();
        backhaulidCheckValues.add(oltEndsz);
        backhaulidCheckValues.add(olt.getOltSlot());
        backhaulidCheckValues.add(olt.getOltPort());

        List<String> deprovisionPortCheckValues = new ArrayList<>();
        deprovisionPortCheckValues.add(oltEndsz);

        List<String> dpuAtOltCheckValuesPost = new ArrayList<>();
        dpuAtOltCheckValuesPost.add(dpu.getEndSz());
        dpuAtOltCheckValuesPost.add(dpu.getOnuId().toString());
        dpuAtOltCheckValuesPost.add(oltEndsz);
        dpuAtOltCheckValuesPost.add(olt.getOltSlot());
        dpuAtOltCheckValuesPost.add(olt.getOltPort());
        dpuAtOltCheckValuesPost.add("\"configurationState\":\"INACTIVE\"");

        List<String> dpuSealAtOltCheckValues = new ArrayList<>();
        dpuSealAtOltCheckValues.add(dpu.getEndSz().replace("/", "_"));

        List<String> dpuAtOltCheckValuesPut = new ArrayList<>();
        dpuAtOltCheckValuesPut.add(dpu.getEndSz());
        dpuAtOltCheckValuesPut.add(dpu.getOnuId().toString());
        dpuAtOltCheckValuesPut.add(oltEndsz);
        dpuAtOltCheckValuesPut.add(olt.getOltSlot());
        dpuAtOltCheckValuesPut.add(olt.getOltPort());
        dpuAtOltCheckValuesPut.add("\"configurationState\":\"ACTIVE\"");

        List<String> dpuEmsCheckValuesPost = new ArrayList<>();
        dpuEmsCheckValuesPost.add(dpu.getEndSz());
        dpuEmsCheckValuesPost.add("\"configurationState\":\"INACTIVE\"");

        List<String> dpuEmsCheckValuesPut = new ArrayList<>();
        dpuEmsCheckValuesPut.add(dpu.getEndSz());
        dpuEmsCheckValuesPut.add("\"configurationState\":\"ACTIVE\"");

        List<String> dpuSealAtOltCheckValuesDpu = new ArrayList<>();
        dpuSealAtOltCheckValuesDpu.add(dpu.getEndSz().replace("/", "_"));

        Long timeOfExecution = System.currentTimeMillis();

        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkGetDeviceDPUCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetDpuPonConnCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetEthernetLinkCalled(timeOfExecution, oltEndsz);
        dpuCommissioningRobot.checkPostOnuIdCalled(timeOfExecution, onuidCheckValues);
        dpuCommissioningRobot.checkPostBackhaulidCalled(timeOfExecution, backhaulidCheckValues);
        dpuCommissioningRobot.checkPostDeprovisioningPortCalled(timeOfExecution, deprovisionPortCheckValues);
        dpuCommissioningRobot.checkPostConfigAncpCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetDpuAncpSessionCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetOltAncpSessionCalled(timeOfExecution, oltEndsz);
        dpuCommissioningRobot.checkGetDpuAtOltConfigCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkPostDpuAtOltConfigCalled(timeOfExecution, dpuAtOltCheckValuesPost);
        dpuCommissioningRobot.checkPostSEALDpuAtOltConfigCalled(timeOfExecution, dpuSealAtOltCheckValues);
        dpuCommissioningRobot.checkPutDpuAtOltConfigCalled(timeOfExecution, dpuAtOltCheckValuesPut);
        dpuCommissioningRobot.checkGetDpuEmsConfigCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkPostDpuEmsConfigCalled(timeOfExecution, dpuEmsCheckValuesPost);
        dpuCommissioningRobot.checkPostSEALDpuEmsConfigCalled(timeOfExecution, dpuSealAtOltCheckValuesDpu);
        dpuCommissioningRobot.checkPutDpuEmsConfigCalled(timeOfExecution, dpuEmsCheckValuesPut);
        dpuCommissioningRobot.checkPostDeviceProvisioningCalled(timeOfExecution, dpu.getEndSz());

    }

    @Test(description = "Positive case. DPU-commisioning without errors")
    @Description("Use case: DpuAtOltConfiguration exists")
    public void dpuCommissioningDpuAtOltConfigurationExists() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DpuAtOltConfigurationExists);
        dpuCommissioningRobot.setUpWiremock(olt, dpu, isAsyncScenario);

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
        dpuSealAtOltCheckValues.add(dpu.getEndSz().replace("/", "_"));

        Long timeOfExecution = System.currentTimeMillis();

        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkGetDeviceDPUCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetDpuPonConnCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetEthernetLinkCalled(timeOfExecution, oltEndsz);
        dpuCommissioningRobot.checkPostOnuIdCalled(timeOfExecution, onuidCheckValues);
        dpuCommissioningRobot.checkPostBackhaulidCalled(timeOfExecution, backhaulidCheckValues);
        dpuCommissioningRobot.checkPostDeprovisioningPortCalled(timeOfExecution, deprovisionPortCheckValues);
        dpuCommissioningRobot.checkPostConfigAncpCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetDpuAncpSessionCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetOltAncpSessionCalled(timeOfExecution, oltEndsz);
        dpuCommissioningRobot.checkGetDpuAtOltConfigCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkPostSEALDpuAtOltConfigNotCalled(timeOfExecution, dpuSealAtOltCheckValues);
        dpuCommissioningRobot.checkPostDpuAtOltConfigNotCalled(timeOfExecution, dpuAtOltCheckValues);
        dpuCommissioningRobot.checkPutDpuAtOltConfigNotCalled(timeOfExecution, dpuAtOltCheckValues);

    }

    @Test(description = "Positive case. DPU-commisioning without errors")
    @TmsLink("DIGIHUB-62083")
    @Description("Use case: DpuEmsConfiguration exists")
    public void dpuCommissioningDpuEmsConfigurationExists() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DpuEmsConfigurationExists);
        dpuCommissioningRobot.setUpWiremock(olt, dpu, isAsyncScenario);

        String oltEndsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        List<String> onuidCheckValues = new ArrayList<>();
        onuidCheckValues.add(dpu.getEndSz());

        List<String> backhaulidCheckValues = new ArrayList<>();
        backhaulidCheckValues.add(oltEndsz);
        backhaulidCheckValues.add(olt.getOltSlot());
        backhaulidCheckValues.add(olt.getOltPort());

        List<String> deprovisionPortCheckValues = new ArrayList<>();
        deprovisionPortCheckValues.add(oltEndsz);

        List<String> dpuAtOltCheckValuesPost = new ArrayList<>();
        dpuAtOltCheckValuesPost.add(dpu.getEndSz());

        List<String> dpuSealAtOltCheckValues = new ArrayList<>();
        dpuSealAtOltCheckValues.add(dpu.getEndSz().replace("/", "_"));

        List<String> dpuAtOltCheckValuesPut = new ArrayList<>();
        dpuAtOltCheckValuesPut.add(dpu.getEndSz());

        List<String> dpuEmsCheckValuesPost = new ArrayList<>();
        dpuEmsCheckValuesPost.add(dpu.getEndSz());

        List<String> dpuEmsCheckValuesPut = new ArrayList<>();
        dpuEmsCheckValuesPut.add(dpu.getEndSz());

        List<String> dpuSealAtOltCheckValuesDpu = new ArrayList<>();
        dpuSealAtOltCheckValuesDpu.add(dpu.getEndSz().replace("/", "_"));

        Long timeOfExecution = System.currentTimeMillis();

        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkGetDeviceDPUCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetDpuPonConnCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetEthernetLinkCalled(timeOfExecution, oltEndsz);
        dpuCommissioningRobot.checkPostOnuIdCalled(timeOfExecution, onuidCheckValues);
        dpuCommissioningRobot.checkPostBackhaulidCalled(timeOfExecution, backhaulidCheckValues);
        dpuCommissioningRobot.checkPostDeprovisioningPortCalled(timeOfExecution, deprovisionPortCheckValues);
        dpuCommissioningRobot.checkPostConfigAncpCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetDpuAncpSessionCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetOltAncpSessionCalled(timeOfExecution, oltEndsz);
        dpuCommissioningRobot.checkGetDpuAtOltConfigCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkPostDpuAtOltConfigCalled(timeOfExecution, dpuAtOltCheckValuesPost);
        dpuCommissioningRobot.checkPostSEALDpuAtOltConfigCalled(timeOfExecution, dpuSealAtOltCheckValues);
        dpuCommissioningRobot.checkPutDpuAtOltConfigCalled(timeOfExecution, dpuAtOltCheckValuesPut);
        dpuCommissioningRobot.checkGetDpuEmsConfigCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkPostDpuEmsConfigNotCalled(timeOfExecution, dpuEmsCheckValuesPost);
        dpuCommissioningRobot.checkPostSEALDpuEmsConfigNotCalled(timeOfExecution, dpuSealAtOltCheckValuesDpu);
        dpuCommissioningRobot.checkPutDpuEmsConfigNotCalled(timeOfExecution, dpuEmsCheckValuesPut);

    }

    @Test(description = "Negative case. GET oltResourceInventory returned 400")
    @Description("Negative case. GET oltResourceInventory returned 400")
    public void dpuCommissioningGetDevice400() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.GetDevice400);
        dpuCommissioningRobot.setUpWiremock(olt, dpu, isAsyncScenario);

        Long timeOfExecution = System.currentTimeMillis();

        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkGetDeviceDPUCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetDpuPonConnNotCalled(timeOfExecution, dpu.getEndSz());
    }

    @Test(description = "Negative case. GET DpuPonConn returned 400")
    @Description("Negative case. GET DpuPonConn returned 400")
    public void dpuCommissioningGetDpuPonConn400() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.GetPonConn400);
        dpuCommissioningRobot.setUpWiremock(olt, dpu, isAsyncScenario);

        String oltEndsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();

        Long timeOfExecution = System.currentTimeMillis();

        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkGetDpuPonConnCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetEthernetLinkNotCalled(timeOfExecution, oltEndsz);

    }

    @Test(description = "Negative case. GET EthernetLink returned 400")
    @Description("Negative case. GET EthernetLink returned 400")
    public void dpuCommissioningGetEthLink400() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.FindEthLink400);
        dpuCommissioningRobot.setUpWiremock(olt, dpu, isAsyncScenario);

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
    public void dpuCommissioningGetOnuId400() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.GetOnuId400);
        dpuCommissioningRobot.setUpWiremock(olt, dpu, isAsyncScenario);

        String oltEndsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        List<String> onuidCheckValues = new ArrayList<>();
        onuidCheckValues.add(dpu.getEndSz());

        List<String> backhaulidCheckValues = new ArrayList<>();
        backhaulidCheckValues.add(oltEndsz);
        backhaulidCheckValues.add(olt.getOltSlot());
        backhaulidCheckValues.add(olt.getOltPort());

        Long timeOfExecution = System.currentTimeMillis();

        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkPostOnuIdCalled(timeOfExecution, onuidCheckValues);
        dpuCommissioningRobot.checkPostBackhaulidNotCalled(timeOfExecution, backhaulidCheckValues);

    }

    @Test
    @Description("Negative case. GET BackhaulId 400")
    public void dpuCommissioningGetBackhaul400() {
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

        dpuCommissioningRobot.setUpWiremock(olt, dpu, isAsyncScenario);
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkPostBackhaulidCalled(timeOfExecution, backhaulidCheckValues);
        dpuCommissioningRobot.checkPostDeprovisioningPortNotCalled(timeOfExecution, deprovisionPonPortValues);
    }

    @Test(description = "Negative case. POST DeprovisionOltPort returned 400")
    @Description("Negative case. POST DeprovisionOltPort returned 400")
    public void dpuCommissioningPostDeprovision400() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.PostDeprovisionOltPort400);
        Long timeOfExecution = System.currentTimeMillis();
        String oltEndsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        List<String> deprovisionCheckValues = new ArrayList<>();
        deprovisionCheckValues.add(oltEndsz);

        List<String> configureAncpCheckValues = new ArrayList<>();
        configureAncpCheckValues.add(dpu.getEndSz());
        dpuCommissioningRobot.setUpWiremock(olt, dpu, isAsyncScenario);
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkPostDeprovisioningPortCalled(timeOfExecution, deprovisionCheckValues);
        dpuCommissioningRobot.checkPostConfigAncpNotCalled(timeOfExecution, dpu.getEndSz());
    }

    @Test(description = "Negative case. POST ConfigureANCP returned 400")
    @Description("Negative case. POST ConfigureANCP returned 400")
    public void dpuCommissioningConfigureAncp400() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.ConfigureAncp400);
        Long timeOfExecution = System.currentTimeMillis();

        dpuCommissioningRobot.setUpWiremock(olt, dpu, isAsyncScenario);
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkPostConfigAncpCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetDpuAncpSessionNotCalled(timeOfExecution, dpu.getEndSz());
    }

    @Test(description = "Negative case. GET ANCPSession returned 400")
    @Description("Negative case. GET ANCPSession returned 400")
    public void dpuCommissioningGetAncp400() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.GetAncpSession400);
        String oltEndsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        Long timeOfExecution = System.currentTimeMillis();

        dpuCommissioningRobot.setUpWiremock(olt, dpu, isAsyncScenario);
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkGetDpuAncpSessionCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetOltAncpSessionNotCalled(timeOfExecution, oltEndsz);
    }

    @Test(description = "Negative case. POST DeprovisionOltPort returned error in callback")
    @Description("Negative case. POST DeprovisionOltPort returned error in callback")
    public void dpuCommissioningPostDeprovisionCallbackError() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.PostDeprovisionCallbackError);
        Long timeOfExecution = System.currentTimeMillis();
        isAsyncScenario = true;

        String oltEndsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        List<String> deprovisionCheckValues = new ArrayList<>();
        deprovisionCheckValues.add(oltEndsz);

        dpuCommissioningRobot.setUpWiremock(olt, dpu, isAsyncScenario);
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkPostDeprovisioningPortCalled(timeOfExecution, deprovisionCheckValues);
        dpuCommissioningRobot.checkPostConfigAncpNotCalled(timeOfExecution, dpu.getEndSz());
    }

    @Test(description = "Negative case. POST ConfigureANCP returned error in callback")
    @Description("Negative case. POST ConfigureANCP returned error in callback")
    public void dpuCommissioningPostConfigureANCPCallbackError() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.PostConfigureANCPCallbackError);
        Long timeOfExecution = System.currentTimeMillis();
        isAsyncScenario = true;

        dpuCommissioningRobot.setUpWiremock(olt, dpu, isAsyncScenario);
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkPostConfigAncpCalled(timeOfExecution, dpu.getEndSz());
        dpuCommissioningRobot.checkGetDpuAncpSessionNotCalled(timeOfExecution, dpu.getEndSz());
    }

    @Test(description = "Negative case. SEAL POST.DpuAtOltConf returned error in callback")
    @Description("Negative case. SEAL POST.DpuAtOltConf returned error in callback")
    public void dpuCommissioningPostSealDpuAtOltConfigCallbackError() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.PostSealDpuAtOltConfigCallbackError);
        Long timeOfExecution = System.currentTimeMillis();
        isAsyncScenario = true;
        List<String> dpuSealAtOltCheckValues = new ArrayList<>();
        dpuSealAtOltCheckValues.add(dpu.getEndSz().replace("/", "_"));
        List<String> dpuAtOltCheckValues = new ArrayList<>();
        dpuAtOltCheckValues.add(dpu.getEndSz());

        dpuCommissioningRobot.setUpWiremock(olt, dpu, isAsyncScenario);
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkPostSEALDpuAtOltConfigCalled(timeOfExecution, dpuSealAtOltCheckValues);
        dpuCommissioningRobot.checkPutDpuAtOltConfigNotCalled(timeOfExecution, dpuAtOltCheckValues);
    }

    @Test(description = "Negative case. SEAL POST.DpuEmsConf returned error in callback")
    @Description("Negative case. SEAL POST.DpuEmsConf returned error in callback")
    public void dpuCommissioningPostSealDpuEmsConfigCallbackError() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.PostSealDpuEmsConfigCallbackError);
        Long timeOfExecution = System.currentTimeMillis();
        isAsyncScenario = true;
        List<String> dpuSealEmsCheckValues = new ArrayList<>();
        dpuSealEmsCheckValues.add(dpu.getEndSz());

        dpuCommissioningRobot.setUpWiremock(olt, dpu, isAsyncScenario);
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        dpuCommissioningRobot.checkPostSEALDpuEmsConfigCalled(timeOfExecution, dpuSealEmsCheckValues);
        dpuCommissioningRobot.checkPutDpuEmsConfigNotCalled(timeOfExecution, dpuSealEmsCheckValues);
    }

    @Test(description = "Negative case. POST.startDeviceProvisioning returned error in callback")
    @Description("Negative case. POST.startDeviceProvisioning returned error in callback")
    public void dpuCommissioningPostDeviceProvisioningCallbackError() throws InterruptedException {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.PostWgFTTBDeviceProvisioningCallbackError);
        Long timeOfExecution = System.currentTimeMillis();
        isAsyncScenario = true;

        dpuCommissioningRobot.setUpWiremock(olt, dpu, isAsyncScenario);
        dpuCommissioningRobot.startProcess(dpu.getEndSz());
        Thread.sleep(3000);
        dpuCommissioningRobot.checkPostDeviceProvisioningCalled(timeOfExecution, dpu.getEndSz());
        //TODO :
        //dpuCommissioningRobot.checkPatchDeviceNotCalled(timeOfExecution, dpuSealEmsCheckValues);
    }

    @Test(description = "Domain level test. Positive case. DPU-commisioning without errors")
    @Description("Positive case. DPU-commisioning without errors")
    public void dpuCommissioningPositiveDomain() throws InterruptedException {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DefaultPositive);
        dpuCommissioningRobot.setUpWiremock(olt, dpu, isAsyncScenario);

        dpuCommissioningRobot.startProcess(dpu.getEndSz());

        Thread.sleep(20000);

        etcdRobot.checkEtcdValues(dpuCommissioningRobot.getBusinessKey(),
                Arrays.asList(
                        "EXECUTED Successfuly [Read DPU device data]",
                        "EXECUTED Successfuly [Read OltPonPort Data]",
                        "EXECUTED Successfuly [Read OltUpLinkPortData]",
                        "EXECUTED Successfuly [Get Unique OnuId for DPU]",
                        "EXECUTED Successfuly [Read BackhaulId]",
                        "EXECUTED Successfuly [Read BackhaulId]",
                        "EXECUTED Successfuly [Deprovision FTTH on PonPort][call]",
                        "EXECUTED Successfuly [Deprovision FTTH on PonPort][callback]",
                        "EXECUTED Successfuly [Configure ANCP on BNG][call]",
                        "EXECUTED Successfuly [Configure ANCP on BNG][callback]",
                        "EXECUTED Successfuly [Read ANCP Info]",
                        "EXECUTED Successfuly [Create DpuAtOltConfiguration If Missing]",
                        "EXECUTED Successfuly [Configure DPU at OLT][call]",
                        "EXECUTED Successfuly [Configure DPU at OLT][callback]",
                        "EXECUTED Successfuly [Set DpuAtOltConfiguration.configurationState to active]",
                        "EXECUTED Successfuly [Create DpuEmsConfiguration If Missing]",
                        "EXECUTED Successfuly [Configure DPU Ems][call]",
                        "EXECUTED Successfuly [Configure DPU Ems][callback]",
                        "EXECUTED Successfuly [Set DpuEmsConfiguration.configurationState to active]"));


    }

}
