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
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.dpu.commissioning.model.DpuCommissioningResponse;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
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
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.attachStubsToAllureReport;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.savePublishedToDefaultDir;

public class DpuCommissioningProcess extends GigabitTest {
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

    @Test(description = "Positive case. DPU-commisioning without errors")
    @TmsLink("DIGIHUB-62083")
    @Description("Positive case. DPU-commisioning without errors")
    public void dpuCommissioningPositive() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DefaultOltForCommissioningPositive);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DefaultPositive);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningPositive")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllSuccess(olt, dpu)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            List<Consumer<RequestPatternBuilder>> onuidCheckValues = Collections.singletonList(
                    bodyContains(dpu.getEndSz()));

            List<Consumer<RequestPatternBuilder>> backhaulidCheckValues = Arrays.asList(
                    bodyContains(olt.getEndsz()),
                    bodyContains(olt.getOltSlot()),
                    bodyContains(olt.getOltPort()));

            List<Consumer<RequestPatternBuilder>> deprovisionPortCheckValues = Collections.singletonList(
                    bodyContains(olt.getEndsz()));

            List<Consumer<RequestPatternBuilder>> dpuAtOltCheckValuesPost = Arrays.asList(
                    bodyContains(dpu.getEndSz()),
                    bodyContains(dpu.getOnuId().toString()),
                    bodyContains(olt.getEndsz()),
                    bodyContains(olt.getOltSlot()),
                    bodyContains(olt.getOltPort()),
                    bodyContains("\"configurationState\":\"INACTIVE\""));

            List<Consumer<RequestPatternBuilder>> dpuSealAtOltCheckValues = Collections.singletonList(
                    bodyContains(dpu.getEndSz().replace("/", "_")));

            List<Consumer<RequestPatternBuilder>> dpuAtOltCheckValuesPut = Arrays.asList(
                    bodyContains(dpu.getEndSz()),
                    bodyContains(dpu.getOnuId().toString()),
                    bodyContains(olt.getEndsz()),
                    bodyContains(olt.getOltSlot()),
                    bodyContains(olt.getOltPort()),
                    bodyContains("\"configurationState\":\"ACTIVE\""));

            List<Consumer<RequestPatternBuilder>> dpuEmsCheckValuesPost = Arrays.asList(
                    bodyContains(dpu.getEndSz()),
                    bodyContains("\"configurationState\":\"INACTIVE\""));

            List<Consumer<RequestPatternBuilder>> dpuEmsCheckValuesPut = Arrays.asList(
                    bodyContains(dpu.getEndSz()),
                    bodyContains("\"configurationState\":\"ACTIVE\""));

            List<Consumer<RequestPatternBuilder>> dpuSealAtOltCheckValuesDpu = Collections.singletonList(
                    bodyContains(dpu.getEndSz().replace("/", "_")));

            List<Consumer<RequestPatternBuilder>> checkFirstPatchValues = Collections.singletonList(
                    bodyContains("INSTALLING"));

            List<Consumer<RequestPatternBuilder>> checkSecondPatchValues = Collections.singletonList(
                    bodyContains("OPERATING"));

            dpuCommissioningRobot.startProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkGetDeviceDPUCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkPatchPortCalled(checkFirstPatchValues);
            dpuCommissioningRobot.checkGetDpuPonConnCalled(dpu.getGfApFolId());
            dpuCommissioningRobot.checkGetEthernetLinkCalled(olt.getEndsz());
            dpuCommissioningRobot.checkPostOnuIdCalled(onuidCheckValues);
            dpuCommissioningRobot.checkPostBackhaulidCalled(backhaulidCheckValues);
            dpuCommissioningRobot.checkPostDeprovisioningPortCalled(deprovisionPortCheckValues);
            dpuCommissioningRobot.checkPostConfigAncpCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkGetDpuAncpSessionCalled(dpu.getEndSz().replace("/", "%2F"));
            dpuCommissioningRobot.checkGetOltAncpSessionCalled(olt.getEndsz().replace("/", "%2F"));
            dpuCommissioningRobot.checkGetDpuAtOltConfigCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkPostDpuAtOltConfigCalled(dpuAtOltCheckValuesPost);
            dpuCommissioningRobot.checkPostSEALDpuAtOltConfigCalled(dpuSealAtOltCheckValues);
            dpuCommissioningRobot.checkPutDpuAtOltConfigCalled(dpuAtOltCheckValuesPut);
            dpuCommissioningRobot.checkGetDpuEmsConfigCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkPostDpuEmsConfigCalled(dpuEmsCheckValuesPost);
            dpuCommissioningRobot.checkPostSEALDpuEmsConfigCalled(dpuSealAtOltCheckValuesDpu);
            dpuCommissioningRobot.checkPutDpuEmsConfigCalled(dpuEmsCheckValuesPut);
            dpuCommissioningRobot.checkPostDeviceProvisioningCalled(dpu.getEndSz());
        }
    }

    @Test(description = "Positive case. DPU-commissioning without errors")
    @Description("Use case: DpuAtOltConfiguration exists")
    public void dpuCommissioningDpuAtOltConfigurationExists() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DpuAtOltConfigurationExists);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningDpuAtOltConfigurationExists")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllSuccessWithDpuAtOltConfigurationExists(olt, dpu)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            List<Consumer<RequestPatternBuilder>> onuidCheckValues = Collections.singletonList(
                    bodyContains(dpu.getEndSz()));

            List<Consumer<RequestPatternBuilder>> backhaulidCheckValues = Arrays.asList(
                    bodyContains(olt.getEndsz()),
                    bodyContains(olt.getOltSlot()),
                    bodyContains(olt.getOltPort()));

            List<Consumer<RequestPatternBuilder>> deprovisionPortCheckValues = Collections.singletonList(
                    bodyContains(olt.getEndsz()));

            List<Consumer<RequestPatternBuilder>> dpuAtOltCheckValues = Collections.singletonList(
                    bodyContains(dpu.getEndSz()));

            List<Consumer<RequestPatternBuilder>> dpuSealAtOltCheckValues = Collections.singletonList(
                    bodyContains(dpu.getEndSz().replace("/", "_")));

            dpuCommissioningRobot.startProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkGetDeviceDPUCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkGetDpuPonConnCalled(dpu.getGfApFolId());
            dpuCommissioningRobot.checkGetEthernetLinkCalled(olt.getEndsz());
            dpuCommissioningRobot.checkPostOnuIdCalled(onuidCheckValues);
            dpuCommissioningRobot.checkPostBackhaulidCalled(backhaulidCheckValues);
            dpuCommissioningRobot.checkPostDeprovisioningPortCalled(deprovisionPortCheckValues);
            dpuCommissioningRobot.checkPostConfigAncpCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkGetDpuAncpSessionCalled(dpu.getEndSz().replace("/", "%2F"));
            dpuCommissioningRobot.checkGetOltAncpSessionCalled(olt.getEndsz().replace("/", "%2F"));
            dpuCommissioningRobot.checkGetDpuAtOltConfigCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkPostSEALDpuAtOltConfigNotCalled(dpuSealAtOltCheckValues);
            dpuCommissioningRobot.checkPostDpuAtOltConfigNotCalled(dpuAtOltCheckValues);
            dpuCommissioningRobot.checkPutDpuAtOltConfigNotCalled(dpuAtOltCheckValues);
        }
    }

    @Test(description = "Positive case. DPU-commisioning without errors")
    @TmsLink("DIGIHUB-62083")
    @Description("Use case: DpuEmsConfiguration exists")
    public void dpuCommissioningDpuEmsConfigurationExists() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DpuEmsConfigurationExists);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningDpuEmsConfigurationExists")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllSuccessWithDpuEmsConfigurationExists(olt, dpu)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            List<Consumer<RequestPatternBuilder>> onuidCheckValues = Collections.singletonList(
                    bodyContains(dpu.getEndSz()));

            List<Consumer<RequestPatternBuilder>> backhaulidCheckValues = Arrays.asList(
                    bodyContains(olt.getEndsz()),
                    bodyContains(olt.getOltSlot()),
                    bodyContains(olt.getOltPort()));

            List<Consumer<RequestPatternBuilder>> deprovisionPortCheckValues = Collections.singletonList(
                    bodyContains(olt.getEndsz()));

            List<Consumer<RequestPatternBuilder>> dpuAtOltCheckValuesPost = Collections.singletonList(
                    bodyContains(dpu.getEndSz()));

            List<Consumer<RequestPatternBuilder>> dpuSealAtOltCheckValues = Collections.singletonList(
                    bodyContains(dpu.getEndSz().replace("/", "_")));

            List<Consumer<RequestPatternBuilder>> dpuAtOltCheckValuesPut = Collections.singletonList(
                    bodyContains(dpu.getEndSz()));

            List<Consumer<RequestPatternBuilder>> dpuEmsCheckValuesPost = Collections.singletonList(
                    bodyContains(dpu.getEndSz()));

            List<Consumer<RequestPatternBuilder>> dpuEmsCheckValuesPut = Collections.singletonList(
                    bodyContains(dpu.getEndSz()));

            List<Consumer<RequestPatternBuilder>> dpuSealAtOltCheckValuesDpu = Collections.singletonList(
                    bodyContains(dpu.getEndSz().replace("/", "_")));

            dpuCommissioningRobot.startProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkGetDeviceDPUCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkGetDpuPonConnCalled(dpu.getGfApFolId());
            dpuCommissioningRobot.checkGetEthernetLinkCalled(olt.getEndsz());
            dpuCommissioningRobot.checkPostOnuIdCalled(onuidCheckValues);
            dpuCommissioningRobot.checkPostBackhaulidCalled(backhaulidCheckValues);
            dpuCommissioningRobot.checkPostDeprovisioningPortCalled(deprovisionPortCheckValues);
            dpuCommissioningRobot.checkPostConfigAncpCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkGetDpuAncpSessionCalled(dpu.getEndSz().replace("/", "%2F"));
            dpuCommissioningRobot.checkGetOltAncpSessionCalled(olt.getEndsz().replace("/", "%2F"));
            dpuCommissioningRobot.checkGetDpuAtOltConfigCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkPostDpuAtOltConfigCalled(dpuAtOltCheckValuesPost);
            dpuCommissioningRobot.checkPostSEALDpuAtOltConfigCalled(dpuSealAtOltCheckValues);
            dpuCommissioningRobot.checkPutDpuAtOltConfigCalled(dpuAtOltCheckValuesPut);
            dpuCommissioningRobot.checkGetDpuEmsConfigCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkPostDpuEmsConfigNotCalled(dpuEmsCheckValuesPost);
            dpuCommissioningRobot.checkPostSEALDpuEmsConfigNotCalled(dpuSealAtOltCheckValuesDpu);
            dpuCommissioningRobot.checkPutDpuEmsConfigNotCalled(dpuEmsCheckValuesPut);
        }
    }

    @Test(description = "Negative case. GET oltResourceInventory returned 400")
    @Description("Negative case. GET oltResourceInventory returned 400")
    public void dpuCommissioningGetDevice400() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.GetDevice400);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningGetDevice400")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllForGetDevice400(olt, dpu)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            List<Consumer<RequestPatternBuilder>> backhaulidCheckValues = Arrays.asList(
                    bodyContains(olt.getEndsz()),
                    bodyContains(olt.getOltSlot()),
                    bodyContains(olt.getOltPort()));

            dpuCommissioningRobot.startProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkGetDeviceDPUCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkGetDpuPonConnNotCalled(dpu.getGfApFolId());
            dpuCommissioningRobot.checkPostBackhaulidCalled(backhaulidCheckValues);
        }
    }

    @Test(description = "Negative case. GET DpuPonConn returned 400")
    @Description("Negative case. GET DpuPonConn returned 400")
    public void dpuCommissioningGetDpuPonConn400() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.GetPonConn400);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningGetDpuPonConn400")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllForDpuPonConn400(olt, dpu)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            List<Consumer<RequestPatternBuilder>> backhaulidCheckValues = Arrays.asList(
                    bodyContains(olt.getEndsz()),
                    bodyContains(olt.getOltSlot()),
                    bodyContains(olt.getOltPort()));

            dpuCommissioningRobot.startProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkGetDpuPonConnCalled(dpu.getGfApFolId());
            dpuCommissioningRobot.checkGetEthernetLinkNotCalled(olt.getEndsz());
            dpuCommissioningRobot.checkPostBackhaulidCalled(backhaulidCheckValues);
        }
    }

    @Test(description = "Negative case. GET DpuPonConn returned different ports in response")
    @Description("Negative case. Expected: get request for GetEthernetLink not sent")
    public void dpuCommissioningGetDpuPonConnReturnDifferentPorts() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DpuDifferentPortsError);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "DpuDifferentPortsError")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllForGetPonConnDiffPortsError(olt, dpu)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());


            dpuCommissioningRobot.startProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkGetDpuPonConnCalled(dpu.getGfApFolId());
            dpuCommissioningRobot.checkGetEthernetLinkNotCalled(olt.getEndsz());
        }
    }

    @Test(description = "Negative case. GET EthernetLink returned 400")
    @Description("Negative case. GET EthernetLink returned 400")
    public void dpuCommissioningGetEthLink400() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.FindEthLink400);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningGetEthLink400")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllForGetEthLink400(olt, dpu)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            List<Consumer<RequestPatternBuilder>> onuidCheckValues = Collections.singletonList(
                    bodyContains(dpu.getEndSz()));

            List<Consumer<RequestPatternBuilder>> backhaulidCheckValues = Arrays.asList(
                    bodyContains(olt.getEndsz()),
                    bodyContains(olt.getOltSlot()),
                    bodyContains(olt.getOltPort()));

            dpuCommissioningRobot.startProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkGetEthernetLinkCalled(olt.getEndsz());
            dpuCommissioningRobot.checkPostOnuIdNotCalled(onuidCheckValues);
            dpuCommissioningRobot.checkPostBackhaulidCalled(backhaulidCheckValues);
        }
    }

    @Test(description = "Negative case. GET OnuId returned 400")
    @Description("Negative case. GET OnuId returned 400")
    public void dpuCommissioningGetOnuId400() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.GetOnuId400);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningGetOnuId400")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllForGetOnuId400(olt, dpu)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            UUID traceId = dpuCommissioningRobot.startProcess(dpu.getEndSz());

            List<Consumer<RequestPatternBuilder>> onuidCheckValues = Arrays.asList(
                    bodyContains(dpu.getEndSz()));

            List<Consumer<RequestPatternBuilder>> backhaulidCheckValues = Arrays.asList(
                    bodyContains(olt.getEndsz()),
                    bodyContains(olt.getOltSlot()),
                    bodyContains(olt.getOltPort()));

            dpuCommissioningRobot.checkPostOnuIdCalled(onuidCheckValues);
            dpuCommissioningRobot.checkPostBackhaulidNotCalled(backhaulidCheckValues);
        }
    }

    @Test
    @Description("Negative case. GET BackhaulId 400")
    public void dpuCommissioningGetBackhaul400() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.GetBackhaul400);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningGetBackhaul400")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllForGetBackhaul400(olt, dpu)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            List<Consumer<RequestPatternBuilder>> backhaulidCheckValues = Arrays.asList(
                    bodyContains(olt.getEndsz()),
                    bodyContains(olt.getOltSlot()),
                    bodyContains(olt.getOltPort()));

            List<Consumer<RequestPatternBuilder>> deprovisionPonPortValues = Collections.singletonList(
                    bodyContains(olt.getEndsz()));

            dpuCommissioningRobot.startProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkPostBackhaulidCalled(backhaulidCheckValues);
            dpuCommissioningRobot.checkPostDeprovisioningPortNotCalled(deprovisionPonPortValues);
        }
    }

    @Test(description = "Negative case. POST DeprovisionOltPort returned 400")
    @Description("Negative case. POST DeprovisionOltPort returned 400")
    public void dpuCommissioningPostDeprovision400() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.PostDeprovisionOltPort400);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningPostDeprovision400")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllForPostDeprovision400(olt, dpu)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            List<Consumer<RequestPatternBuilder>> deprovisionCheckValues = Collections.singletonList(
                    bodyContains(olt.getEndsz()));

            dpuCommissioningRobot.startProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkPostDeprovisioningPortCalled(deprovisionCheckValues);
            dpuCommissioningRobot.checkPostConfigAncpNotCalled(dpu.getEndSz());
        }
    }

    @Test(description = "Negative case. POST ConfigureANCP returned 400")
    @Description("Negative case. POST ConfigureANCP returned 400")
    public void dpuCommissioningConfigureAncp400() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.ConfigureAncp400);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningConfigureAncp400")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllForConfigureAncp400(olt, dpu)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            dpuCommissioningRobot.startProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkPostConfigAncpCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkGetDpuAncpSessionNotCalled(dpu.getEndSz());
        }
    }

    @Test(description = "Negative case. GET ANCPSession returned 400")
    @Description("Negative case. GET ANCPSession returned 400")
    public void dpuCommissioningGetAncp400() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.GetAncpSession400);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningGetAncp400")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllForGetAncp400(olt, dpu)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            dpuCommissioningRobot.startProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkGetDpuAncpSessionCalled(dpu.getEndSz().replace("/", "%2F"));
            dpuCommissioningRobot.checkGetOltAncpSessionNotCalled(olt.getEndsz().replace("/", "%2F"));
        }
    }

    @Test(description = "Negative case. POST DeprovisionOltPort returned error in callback")
    @Description("Negative case. POST DeprovisionOltPort returned error in callback")
    public void dpuCommissioningPostDeprovisionCallbackError() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.PostDeprovisionCallbackError);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningPostDeprovisionCallbackError")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllForPostDeprovisionCallbackError(olt, dpu)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            List<Consumer<RequestPatternBuilder>> deprovisionCheckValues = Collections.singletonList(
                    bodyContains(olt.getEndsz()));

            dpuCommissioningRobot.startProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkPostDeprovisioningPortCalled(deprovisionCheckValues);
            dpuCommissioningRobot.checkPostConfigAncpNotCalled(dpu.getEndSz());
        }
    }

    @Test(description = "Negative case. POST ConfigureANCP returned error in callback")
    @Description("Negative case. POST ConfigureANCP returned error in callback")
    public void dpuCommissioningPostConfigureANCPCallbackError() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.PostConfigureANCPCallbackError);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningPostConfigureANCPCallbackError")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllForPostConfigureANCPCallbackError(olt, dpu)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            dpuCommissioningRobot.startProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkPostConfigAncpCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkGetDpuAncpSessionNotCalled(dpu.getEndSz().replace("/", "%2F"));
        }
    }

    @Test(description = "Negative case. SEAL POST.DpuAtOltConf returned error in callback")
    @Description("Negative case. SEAL POST.DpuAtOltConf returned error in callback")
    public void dpuCommissioningPostSealDpuAtOltConfigCallbackError() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.PostSealDpuAtOltConfigCallbackError);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningPostSealDpuAtOltConfigCallbackError")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllForPostSealDpuAtOltConfigCallbackError(olt, dpu)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            List<Consumer<RequestPatternBuilder>> dpuSealAtOltCheckValues = Collections.singletonList(
                    bodyContains(dpu.getEndSz().replace("/", "_")));
            List<Consumer<RequestPatternBuilder>> dpuAtOltCheckValues = Collections.singletonList(
                    bodyContains(dpu.getEndSz()));

            dpuCommissioningRobot.startProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkPostSEALDpuAtOltConfigCalled(dpuSealAtOltCheckValues);
            dpuCommissioningRobot.checkPutDpuAtOltConfigNotCalled(dpuAtOltCheckValues);
        }
    }

    @Test(description = "Negative case. SEAL POST.DpuEmsConf returned error in callback")
    @Description("Negative case. SEAL POST.DpuEmsConf returned error in callback")
    public void dpuCommissioningPostSealDpuEmsConfigCallbackError() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.PostSealDpuEmsConfigCallbackError);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningPostSealDpuEmsConfigCallbackError")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllForPostSealDpuEmsConfigCallbackError(olt, dpu)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            List<Consumer<RequestPatternBuilder>> dpuSealEmsCheckValues = Collections.singletonList(
                    bodyContains(dpu.getEndSz()));

            dpuCommissioningRobot.startProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkPostSEALDpuEmsConfigCalled(dpuSealEmsCheckValues);
            dpuCommissioningRobot.checkPutDpuEmsConfigNotCalled(dpuSealEmsCheckValues);
        }
    }

    @Test(description = "Negative case. POST.startDeviceProvisioning returned error in callback")
    @Description("Negative case. POST.startDeviceProvisioning returned error in callback")
    public void dpuCommissioningPostDeviceProvisioningCallbackError() throws InterruptedException {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.PostWgFTTBDeviceProvisioningCallbackError);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningPostDeviceProvisioningCallbackError")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllForPostDeviceProvisioningCallbackError(olt, dpu)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            List<Consumer<RequestPatternBuilder>> checkSecondPatchValues = Collections.singletonList(
                    bodyContains("OPERATING"));

            dpuCommissioningRobot.startProcess(dpu.getEndSz());
            Thread.sleep(4000);
            dpuCommissioningRobot.checkPostDeviceProvisioningCalled(dpu.getEndSz());
            dpuCommissioningRobot.checkPatchDeviceNotCalled(checkSecondPatchValues);
        }
    }

    @Test(description = "Positive case. Uplink.Linfecyclestate = NOT_OPERATING")
    @Description("Positive case. Uplink.Linfecyclestate = NOT_OPERATING")
    public void dpuCommissioningDpuLifeCycleInstalling() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.LifecycleStateDeviceInstalling);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningDpuLifeCycleInstalling")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllSuccess(olt, dpu)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            List<Consumer<RequestPatternBuilder>> checkFirstPatchValues = Collections.singletonList(
                    bodyContains("INSTALLING"));

            List<Consumer<RequestPatternBuilder>> checkSecondPatchValues = Collections.singletonList(
                    bodyContains("OPERATING"));

            dpuCommissioningRobot.startProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkPatchDeviceNotCalled(checkFirstPatchValues);
        }
    }

    @Test(description = "Positive case. Uplink.LifecycleState = INSTALLING")
    @Description("Positive case. Uplink.LifecycleState = INSTALLING")
    public void dpuCommissioningUplinkLifeCycleInstalling() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.LifecycleStateUplinkInstalling);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningUplinkLifeCycleInstalling")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllSuccess(olt, dpu)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            List<Consumer<RequestPatternBuilder>> checkFirstPatchValues = Collections.singletonList(
                    bodyContains("INSTALLING"));

            List<Consumer<RequestPatternBuilder>> checkSecondPatchValues = Collections.singletonList(
                    bodyContains("OPERATING"));

            dpuCommissioningRobot.startProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkPatchPortNotCalled(checkFirstPatchValues);
        }
    }

    @Test(description = "Positive case. Uplink.LifecycleState = INSTALLING")
    @Description("Positive case. Uplink.LifecycleState = INSTALLING")
    public void dpuCommissioningDeviceAndUplinkLifeCycleInstalling() {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.LifecycleStateDeviceUplinkInstalling);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningDeviceAndUplinkLifeCycleInstalling")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllSuccess(olt, dpu)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            List<Consumer<RequestPatternBuilder>> checkFirstPatchValues = Collections.singletonList(
                    bodyContains("INSTALLING"));

            List<Consumer<RequestPatternBuilder>> checkSecondPatchValues = Collections.singletonList(
                    bodyContains("OPERATING"));

            dpuCommissioningRobot.startProcess(dpu.getEndSz());
            dpuCommissioningRobot.checkPatchPortNotCalled(checkFirstPatchValues);
        }
    }

    @Test(description = "Double calls check and reject")
    @Description("Double start dpu-commissioning calls check and reject in dpu-commissioning process")
    public void doubleCallsRejectTest(){
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DefaultOltForCommissioningPositive);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DefaultPositive);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningPositive")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllSuccess(olt, dpu)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            dpuCommissioningRobot.startProcess(dpu.getEndSz());
            dpuCommissioningRobot.startProcess500(dpu.getEndSz());

        }

    }

    @Test(description = "Domain level test. Positive case. DPU-commisioning without errors")
    @Description("Positive case. DPU-commissioning without errors")
    public void dpuCommissioningPositiveDomain() throws InterruptedException {
        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DpuCommissioningOlt);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DefaultPositive);

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningPositiveDomain")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addAllSuccess(olt, dpu)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            DpuCommissioningResponse resp = dpuCommissioningRobot.startCommissioningProcess(dpu.getEndSz(), UUID.randomUUID());

            Thread.sleep(30000);

            etcdRobot.checkEtcdValues(resp.getBusinessKey(),
                    Arrays.asList(
                            "EXECUTED Successfuly [Read DPU device data]",
                            "EXECUTED Successfuly [update LifecycleStatus of DPU.uplinkPort to INSTALLING]",
                            "EXECUTED Successfuly [Read OltPonPort Data]",
                            "EXECUTED Successfuly [Read OltUpLinkPortData]",
                            "EXECUTED Successfuly [Get Unique OnuId for DPU]",
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
                            "EXECUTED Successfuly [Set DpuEmsConfiguration.configurationState to active]",
                            "EXECUTED Successfuly [Provision FTTB access provisioning on DPU][call]",
                            "EXECUTED Successfuly [Provision FTTB access provisioning on DPU][callback]"));
        }
    }

    private Consumer<RequestPatternBuilder> bodyContains(String str) {
        return requestPatternBuilder -> requestPatternBuilder.withRequestBody(containing(str));
    }

    private Consumer<RequestPatternBuilder> traceIdIs(UUID uuid) {
        return requestPatternBuilder -> requestPatternBuilder.withHeader("X-B3-TraceId", equalTo(uuid.toString()));
    }
}
