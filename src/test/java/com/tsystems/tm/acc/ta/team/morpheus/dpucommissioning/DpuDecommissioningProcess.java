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
import com.tsystems.tm.acc.tests.osr.olt.discovery.external.v1_2_0.client.model.Device;
import io.qameta.allure.Description;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;


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

            List<Consumer<RequestPatternBuilder>> dpuSealAtEMSCheckValuesDpu = Collections.singletonList(
                    bodyContains(dpu.getEndSz().replace("/", "_")));

            dpuCommissioningRobot.startDecomissioningProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkGetDeviceDPUCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkPatchDeviceCalled(checkFirstPatchValues);
            dpuCommissioningRobot.checkPatchPortCalled(checkFirstPatchValues);
            //dpuCommissioningRobot.checkPostDeprovisioningPortCalled(deprovisioningPortCheckValuesDpu);
            dpuCommissioningRobot.checkPostSEALDpuEmsDEConfigCalled(dpuSealAtEMSCheckValuesDpu);

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

    private Consumer<RequestPatternBuilder> bodyContains(String str) {
        return requestPatternBuilder -> requestPatternBuilder.withRequestBody(containing(str));
    }
}

