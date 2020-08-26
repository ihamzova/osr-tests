package com.tsystems.tm.acc.ta.team.morpheus.dpucommissioning;

import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.tsystems.tm.acc.data.osr.models.dpu.DpuCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.data.morpheus.wiremock.MorpeusWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.DpuCommissioningRobot;
import com.tsystems.tm.acc.ta.robot.osr.ETCDRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import io.qameta.allure.Description;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static com.github.tomakehurst.wiremock.client.WireMock.containing;


public class DpuDecommissioningProcess extends BaseTest {

    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private DpuCommissioningRobot dpuCommissioningRobot;
    private ETCDRobot etcdRobot;

    @BeforeClass
    public void init() {
        dpuCommissioningRobot = new DpuCommissioningRobot();
        etcdRobot = new ETCDRobot();
        WireMockFactory.get().resetToDefaultMappings();
    }

    @BeforeMethod
    public void reset() {
        WireMockFactory.get().resetRequests();
    }

    @Test(description = "Positive case. DPU-decommisioning without errors")
    @Description("Positive case. DPU-decommisioning without errors")
    public void dpuDecommissioningPositive() {

        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DpuDecommissioningDefaultPositive);

        try(WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuDecommissioningPositive")){
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addDpuDecommissioningSuccess(olt, dpu)
                    .build()
                    .publish();

            List<Consumer<RequestPatternBuilder>> checkFirstPatchValues = Collections.singletonList(
                    bodyContains("RETIRING"));
            List<Consumer<RequestPatternBuilder>> checkSecondPatchValues = Collections.singletonList(
                    bodyContains("NOT_OPERATING"));

            List<Consumer<RequestPatternBuilder>> dpuEmsCheckValuesPut = Arrays.asList(
                    bodyContains(dpu.getEndSz()),
                    bodyContains("\"configurationState\":\"INACTIVE\""));

            List<Consumer<RequestPatternBuilder>> dpuSealAtEMSCheckValuesDpu = Collections.singletonList(
                    bodyContains(dpu.getEndSz().replace("/", "_")));

            List<Consumer<RequestPatternBuilder>> releaseOnuIdTaskValues = Arrays.asList(
                    bodyContains(olt.getEndsz()),
                    bodyContains(olt.getOltSlot()),
                    bodyContains(olt.getOltPort()),
                    bodyContains("onuId"));

            dpuCommissioningRobot.startDecomissioningProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkGetDeviceDPUCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkPatchDeviceCalled(checkFirstPatchValues);
            dpuCommissioningRobot.checkPatchPortCalled(checkFirstPatchValues);
            dpuCommissioningRobot.checkPostDeviceDeprovisioningCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkGetDpuEmsConfigCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkPutDpuEmsConfigCalled(dpuEmsCheckValuesPut);
            dpuCommissioningRobot.checkPostSEALDpuEmsDEConfigCalled(dpuSealAtEMSCheckValuesDpu);
            dpuCommissioningRobot.checkDeleteDpuEmsConfigurationCalled();
            dpuCommissioningRobot.checkGetDpuAtOltConfigCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkPutDpuAtOltConfigCalled(dpuEmsCheckValuesPut);
            dpuCommissioningRobot.checkPostSEALDpuOltDEConfigCalled(dpuSealAtEMSCheckValuesDpu);
            dpuCommissioningRobot.checkPostReleaseOnuIdTaskCalled(releaseOnuIdTaskValues);
            dpuCommissioningRobot.checkDeleteDpuOltConfigurationCalled();
            dpuCommissioningRobot.checkGetDpuAncpSessionCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkDeleteAncpConfigCalled();
            dpuCommissioningRobot.checkPatchDeviceCalled(checkSecondPatchValues);
            dpuCommissioningRobot.checkPatchPortCalled(checkSecondPatchValues);
        }
    }

    @Test(description = "Positive case. Uplink.LifecycleState = RETIRING, DPU.Linfecyclestate = RETIRING")
    @Description("Positive case. Uplink.LifecycleState = RETIRING, DPU.Linfecyclestate = RETIRING")
    public void dpuDecommissioningDeviceAndUplinkLifeCycleRetiring() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.LifecycleStateDeviceUplinkRetiring);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningDeviceAndUplinkLifeCycleInstalling")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addDpuDecommissioningSuccess(olt, dpu)
                    .build()
                    .publish();

            List<Consumer<RequestPatternBuilder>> checkFirstPatchValues = Collections.singletonList(
                    bodyContains("RETIRING"));

            List<Consumer<RequestPatternBuilder>> checkSecondPatchValues = Collections.singletonList(
                    bodyContains("NOT_OPERATING"));

            dpuCommissioningRobot.startDecomissioningProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkPatchDeviceNotCalled(checkFirstPatchValues);
            dpuCommissioningRobot.checkPatchPortNotCalled(checkFirstPatchValues);
            dpuCommissioningRobot.checkPostDeviceDeprovisioningCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkPatchDeviceCalled(checkSecondPatchValues);
            dpuCommissioningRobot.checkPatchPortCalled(checkSecondPatchValues);
        }
    }

    @Test(description = "Negative case. POST.WgFTTBDeviceDeprovisioning returned error in callback")
    @Description("Negative case. POST.WgFTTBDeviceDeprovisioning returned error in callback")
    public void dpuCommissioningPostDeviceProvisioningCallbackError() throws InterruptedException {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.PostWgFTTBDeviceDeprovisioningCallbackError);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningPostDeviceDeprovisioningCallbackError")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllForPostDeviceProvisioningCallbackError(olt, dpu)
                    .build()
                    .publish();

            List<Consumer<RequestPatternBuilder>> dpuSealAtEMSCheckValuesDpu = Collections.singletonList(
                    bodyContains(dpu.getEndSz().replace("/", "_")));

            dpuCommissioningRobot.startDecomissioningProcess(dpu.getEndSz());
            Thread.sleep(4000);

            dpuCommissioningRobot.checkPostDeviceDeprovisioningCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkPostSEALDpuEmsDEConfigNotCalled(dpuSealAtEMSCheckValuesDpu);
        }
    }

    @Test(description = "Negative case. deconfigure DPU in EMS for SEAL return error in Callback")
    @Description("Negative case. deconfigure DPU in EMS for SEAL return error in Callback")
    public void dpuDecommissioningPostSealDpuEmsConfigCallbackError(){

        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.PostSealDpuEmsDeconfigCallbackError);

        try(WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuDecommissioningPostSealDpuEmsConfigCallbackError")){
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllForPostSealDpuEmsDeconfigCallbackError(olt, dpu)
                    .build()
                    .publish();

            List<Consumer<RequestPatternBuilder>> dpuSealAtEMSCheckValuesDpu = Collections.singletonList(
                    bodyContains(dpu.getEndSz().replace("/", "_")));

            dpuCommissioningRobot.startDecomissioningProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkPostSEALDpuEmsDEConfigCalled(dpuSealAtEMSCheckValuesDpu);
            dpuCommissioningRobot.checkDeleteDpuEmsConfigurationNotCalled();
        }
    }

    @Test(description = "Negative case. deconfigure DPU at OLT for SEAL return error in Callback")
    @Description("Negative case. deconfigure DPU at OLT for SEAL return error in Callback")
    public void dpuDecommissioningPostSealDpuOltConfigCallbackError(){

        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.PostSealDpuAtOltDeconfigCallbackError);

        try(WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuDecommissioningPostSealDpuOltConfigCallbackError")){
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllForPostSealDpuOltDeconfigCallbackError(olt, dpu)
                    .build()
                    .publish();

            List<Consumer<RequestPatternBuilder>> dpuSealAtEMSCheckValuesDpu = Collections.singletonList(
                    bodyContains(dpu.getEndSz().replace("/", "_")));

            List<Consumer<RequestPatternBuilder>> releaseOnuIdTaskValues = Arrays.asList(
                    bodyContains(olt.getEndsz()),
                    bodyContains(olt.getOltSlot()),
                    bodyContains(olt.getOltPort()),
                    bodyContains("onuId"));

            dpuCommissioningRobot.startDecomissioningProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkPostSEALDpuOltDEConfigCalled(dpuSealAtEMSCheckValuesDpu);
            dpuCommissioningRobot.checkPostReleaseOnuIdTaskNotCalled(releaseOnuIdTaskValues);
        }
    }

    @Test(description = "Positive case. DPU-decommisioning without errors, DpuEmsConfiguration exists")
    @Description("Use case: DpuEmsConfiguration exists")
    public void dpuDecommissioningDpuEmsConfigDoesntExist() {

        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DpuDecommissioningDefaultPositive);

        try(WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "addDpuDecommissioningDpuEmsConfigDoesntExist")){
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addDpuDecommissioningDpuEmsConfigDoesntExist(olt, dpu)
                    .build()
                    .publish();

            List<Consumer<RequestPatternBuilder>> dpuEmsCheckValuesPut = Arrays.asList(
                    bodyContains(dpu.getEndSz()),
                    bodyContains("\"configurationState\":\"INACTIVE\""));

            List<Consumer<RequestPatternBuilder>> dpuSealAtEMSCheckValuesDpu = Collections.singletonList(
                    bodyContains(dpu.getEndSz().replace("/", "_")));

            dpuCommissioningRobot.startDecomissioningProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkGetDpuEmsConfigCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkPutDpuEmsConfigNotCalled(dpuEmsCheckValuesPut);
            dpuCommissioningRobot.checkPostSEALDpuEmsDEConfigNotCalled(dpuSealAtEMSCheckValuesDpu);
            dpuCommissioningRobot.checkDeleteDpuEmsConfigurationNotCalled();
        }
    }

    @Test(description = "Positive case. DPU-decommisioning without errors, DpuOltConfiguration exists")
    @Description("Use case: DpuOltConfiguration exists")
    public void dpuDecommissioningDpuOltConfigDoesntExist() {

        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DpuDecommissioningDefaultPositive);

        try(WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "addDpuDecommissioningDpuOltConfigDoesntExist")){
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addDpuDecommissioningDpuOltConfigDoesntExist(olt, dpu)
                    .build()
                    .publish();

            List<Consumer<RequestPatternBuilder>> dpuEmsCheckValuesPut = Arrays.asList(
                    bodyContains(dpu.getEndSz()),
                    bodyContains("\"configurationState\":\"INACTIVE\""));

            List<Consumer<RequestPatternBuilder>> dpuSealAtOLTCheckValuesDpu = Collections.singletonList(
                    bodyContains(dpu.getEndSz().replace("/", "_")));

            List<Consumer<RequestPatternBuilder>> releaseOnuIdTaskValues = Arrays.asList(
                    bodyContains(olt.getEndsz()),
                    bodyContains(olt.getOltSlot()),
                    bodyContains(olt.getOltPort()),
                    bodyContains("onuId"));

            dpuCommissioningRobot.startDecomissioningProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkGetDpuAtOltConfigCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkPutDpuAtOltConfigNotCalled(dpuEmsCheckValuesPut);
            dpuCommissioningRobot.checkPostSEALDpuOltDEConfigNotCalled(dpuSealAtOLTCheckValuesDpu);
            dpuCommissioningRobot.checkPostReleaseOnuIdTaskNotCalled(releaseOnuIdTaskValues);
        }
    }

    @Test(description = "Negative case. Post ReleaseOnuIdTask returned 400")
    @Description("Negative case. Post ReleaseOnuIdTask returned 400")
    public void dpuDecommissioningReleaseOnuIdTask400() {

        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DpuDecommissioningDefaultPositive);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "addDpuDecommissioningReleaseOnuIdTask400")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addDpuDecommissioningReleaseOnuIdTask400(olt, dpu)
                    .build()
                    .publish();

            List<Consumer<RequestPatternBuilder>> releaseOnuIdTaskValues = Arrays.asList(
                    bodyContains(olt.getEndsz()),
                    bodyContains(olt.getOltSlot()),
                    bodyContains(olt.getOltPort()),
                    bodyContains("onuId"));

            dpuCommissioningRobot.startDecomissioningProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkPostReleaseOnuIdTaskCalled(releaseOnuIdTaskValues);
            dpuCommissioningRobot.checkDeleteDpuOltConfigurationNotCalled();
        }
    }

    @Test(description = "Positive case. DPU-decommisioning, AncpSession doesn't exist")
    @Description("Positive case. DPU-decommisioning, AncpSession doesn't exist")
    public void dpuDecommissioningPositiveAncpSessionDoesntExist() {

        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DpuDecommissioningDefaultPositive);

        try(WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuDecommissioningPositiveAncpSessionDoesntExist")){
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addDpuDecommissioningAncpSessionDoesntExist(olt, dpu)
                    .build()
                    .publish();

            dpuCommissioningRobot.startDecomissioningProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkGetDpuAncpSessionCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkDeleteAncpConfigNotCalled();
        }
    }

    @Test(description = "Positive case. DPU-decommisioning, AncpSession doesn't exist")
    @Description("Positive case. DPU-decommisioning, AncpSession doesn't exist")
    public void dpuDecommissioningDeleteAncpErrorCallback() {

        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DpuDecommissioningDefaultPositive);

        try(WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuDecommissioningDeleteAncpErrorCallback")){
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addDpuDecommissioningDeleteAncpErrorCallback(olt, dpu)
                    .build()
                    .publish();

            dpuCommissioningRobot.startDecomissioningProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkGetDpuAncpSessionCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkDeleteAncpConfigCalled();
            dpuCommissioningRobot.checkGetDpuPonConnNotCalled(dpu.getEndSz());
        }
    }

    private Consumer<RequestPatternBuilder> bodyContains(String str) {
        return requestPatternBuilder -> requestPatternBuilder.withRequestBody(containing(str));
    }
}

