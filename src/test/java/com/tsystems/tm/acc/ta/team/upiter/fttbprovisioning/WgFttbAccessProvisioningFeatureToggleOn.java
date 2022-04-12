package com.tsystems.tm.acc.ta.team.upiter.fttbprovisioning;

import com.tsystems.tm.acc.data.upiter.models.defaultnetworklineprofile.DefaultNetworkLineProfileCase;
import com.tsystems.tm.acc.data.upiter.models.dpudemand.DpuDemandCase;
import com.tsystems.tm.acc.data.upiter.models.dpudevice.DpuDeviceCase;
import com.tsystems.tm.acc.data.upiter.models.fttbneprofile.FttbNeProfileCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgFttbAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;

@ServiceLog({
        WG_FTTB_ACCESS_PROVISIONING_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        NETWORK_LINE_PROFILE_MANAGEMENT_MS,
        ACCESS_LINE_MANAGEMENT_MS,
        ACCESS_LINE_PROFILE_CATALOG_MS,
        DECOUPLING_MS,
        GATEWAY_ROUTE_MS
})

@Epic("WG FTTB Provisioning")
public class WgFttbAccessProvisioningFeatureToggleOn extends GigabitTest {

    private WgFttbAccessProvisioningRobot wgFttbAccessProvisioningRobot;
    private AccessLineRiRobot accessLineRiRobot;
    private PortProvisioning oltDeviceFttbProvisioningTwistedPair;
    private PortProvisioning adtranDeviceFttbProvisioningCoax;
    private DpuDevice dpuDeviceFttbProvisioningTwistedPair;
    private DpuDevice dpuDeviceFttbProvisioningOnAdtranCoax;
    private DpuDevice standardDpuDeviceData;
    private DpuDemand dpuDemand;
    private FttbNeProfile fttbNeProfileTp;
    private FttbNeProfile fttbNeProfileCoax;
    private DefaultNetworkLineProfile defaultNlProfileFttbTp;
    private DefaultNetworkLineProfile defaultNlProfileFttbCoax;
    private int numberOfAccessLinesForProvisioning;

    @BeforeClass
    public void init() throws InterruptedException {
        wgFttbAccessProvisioningRobot = new WgFttbAccessProvisioningRobot();
        wgFttbAccessProvisioningRobot.changeFeatureToogleDpuDemandState(true);

        accessLineRiRobot = new AccessLineRiRobot();
        accessLineRiRobot.clearDatabaseByOlt("49/89/8000/76H2");
        accessLineRiRobot.clearDatabaseByOlt("49/911/1100/76H1");
        accessLineRiRobot.clearDatabaseByOlt("49/911/1100/76H3");
        accessLineRiRobot.clearDatabaseByOlt("49/911/1100/76H5");
        Thread.sleep(1000);
        UpiterTestContext context = UpiterTestContext.get();

        dpuDeviceFttbProvisioningTwistedPair = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.dpuDeviceForFttbProvisioningTwistedPair);
        dpuDeviceFttbProvisioningOnAdtranCoax = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.dpuDeviceForFttbProvisioningOnAdtranCoax);

        oltDeviceFttbProvisioningTwistedPair = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.oltDeviceForFttbProvisioningTwistedPair);
        adtranDeviceFttbProvisioningCoax = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.adtranDeviceForFttbProvisioningCoax);

        defaultNlProfileFttbTp = context.getData().getDefaultNetworkLineProfileDataProvider().get(DefaultNetworkLineProfileCase.defaultNLProfileFttbTP);
        defaultNlProfileFttbCoax = context.getData().getDefaultNetworkLineProfileDataProvider().get(DefaultNetworkLineProfileCase.defaultNLProfileFttbCoax);
        fttbNeProfileTp = context.getData().getFttbNeProfileDataProvider().get(FttbNeProfileCase.fttbNeProfileTwistedPair);
        fttbNeProfileCoax = context.getData().getFttbNeProfileDataProvider().get(FttbNeProfileCase.fttbNeProfileCoax);

        standardDpuDeviceData = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.standardDpuDeviceData);
        dpuDemand = context.getData().getDpuDemandDataProvider().get(DpuDemandCase.dpuDemand);

        if (wgFttbAccessProvisioningRobot.getFeatureToggleDpuDemandState()) {
            numberOfAccessLinesForProvisioning = Integer.parseInt(dpuDemand.getNumberOfNeededDpuPorts());
        } else {
            numberOfAccessLinesForProvisioning = standardDpuDeviceData.getNumberOfAccessLines();
        }

        if (numberOfAccessLinesForProvisioning > 16) {
            numberOfAccessLinesForProvisioning = 16;
        }

        accessLineRiRobot.fillDatabaseForDpuPreprovisioningV2(1, 1, dpuDeviceFttbProvisioningTwistedPair, oltDeviceFttbProvisioningTwistedPair);
        accessLineRiRobot.fillDatabaseForDpuPreprovisioningV2(3000, 3000, dpuDeviceFttbProvisioningOnAdtranCoax, adtranDeviceFttbProvisioningCoax);

        // sleep to let the ms get the new value of the feature toggle
        Thread.sleep(3000);
    }

    @Test
    @TmsLink("DIGIHUB-123654")
    @Description("FTTB Provisioning for a Huawei Device, Twisted Pair, Feature Toggle On")
    public void fttbDeviceProvisioningTwistedPairTest() {
        wgFttbAccessProvisioningRobot.startWgFttbAccessProvisioningForDevice(dpuDeviceFttbProvisioningTwistedPair.getEndsz());
        accessLineRiRobot.checkFttbLineParameters(oltDeviceFttbProvisioningTwistedPair, numberOfAccessLinesForProvisioning);
        accessLineRiRobot.checkAccessTransmissionMedium(dpuDeviceFttbProvisioningTwistedPair, numberOfAccessLinesForProvisioning);
        accessLineRiRobot.checkDefaultNetworkLineProfiles(oltDeviceFttbProvisioningTwistedPair, defaultNlProfileFttbTp, numberOfAccessLinesForProvisioning);
        accessLineRiRobot.checkFttbNeProfiles(oltDeviceFttbProvisioningTwistedPair, fttbNeProfileTp, numberOfAccessLinesForProvisioning);
        accessLineRiRobot.checkPhysicalResourceRefCountFttb(dpuDeviceFttbProvisioningTwistedPair,
                oltDeviceFttbProvisioningTwistedPair,
                numberOfAccessLinesForProvisioning,
                1, 1);
        accessLineRiRobot.checkHomeIdsCount(oltDeviceFttbProvisioningTwistedPair);
    }

    @Test(dependsOnMethods = "fttbDeviceProvisioningTwistedPairTest")
    @TmsLink("DIGIHUB-123654")
    @Description("FTTB Deprovisioning for a Huawei Device, Twisted Pair, Feature Toggle On")
    public void fttbDeviceDeprovisioningTwistedPairTest() {
        wgFttbAccessProvisioningRobot.startWgFttbAccessDeprovisioningForDevice(dpuDeviceFttbProvisioningTwistedPair.getEndsz());
        accessLineRiRobot.checkFttbLineParameters(oltDeviceFttbProvisioningTwistedPair, 0);
        accessLineRiRobot.checkPhysicalResourceRefCountFttb(dpuDeviceFttbProvisioningTwistedPair,
                oltDeviceFttbProvisioningTwistedPair,
                0,
                1, 1);
        accessLineRiRobot.checkHomeIdsCount(oltDeviceFttbProvisioningTwistedPair);
    }

    @Test
    @TmsLink("DIGIHUB-129112")
    @Description("FTTB Provisioning for a Device on Adtran, Coax, Feature Toggle On")
    public void fttbDeviceProvisioningOnAdtranCoaxTest() {
        wgFttbAccessProvisioningRobot.startWgFttbAccessProvisioningForDevice(dpuDeviceFttbProvisioningOnAdtranCoax.getEndsz());
        accessLineRiRobot.checkFttbLineParameters(adtranDeviceFttbProvisioningCoax, numberOfAccessLinesForProvisioning);
        accessLineRiRobot.checkAccessTransmissionMedium(dpuDeviceFttbProvisioningOnAdtranCoax, numberOfAccessLinesForProvisioning);
        accessLineRiRobot.checkDefaultNetworkLineProfiles(adtranDeviceFttbProvisioningCoax, defaultNlProfileFttbCoax, numberOfAccessLinesForProvisioning);
        accessLineRiRobot.checkFttbNeProfiles(adtranDeviceFttbProvisioningCoax, fttbNeProfileCoax, numberOfAccessLinesForProvisioning);
        accessLineRiRobot.checkPhysicalResourceRefCountFttb(dpuDeviceFttbProvisioningOnAdtranCoax,
                adtranDeviceFttbProvisioningCoax,
                numberOfAccessLinesForProvisioning,
                1, 1);
        accessLineRiRobot.checkHomeIdsCount(adtranDeviceFttbProvisioningCoax);
    }

    @Test(dependsOnMethods = "fttbDeviceProvisioningOnAdtranCoaxTest")
    @TmsLink("DIGIHUB-129113")
    @Description("FTTB Deprovisioning for a Device on Adtran, Coax, Feature Toggle On")
    public void fttbDeviceDeprovisioningOnAdtranCoaxTest() {
        wgFttbAccessProvisioningRobot.startWgFttbAccessDeprovisioningForDevice(dpuDeviceFttbProvisioningOnAdtranCoax.getEndsz());
        accessLineRiRobot.checkFttbLineParameters(adtranDeviceFttbProvisioningCoax, 0);
        accessLineRiRobot.checkPhysicalResourceRefCountFttb(dpuDeviceFttbProvisioningOnAdtranCoax,
                adtranDeviceFttbProvisioningCoax,
                0,
                1, 1);
        accessLineRiRobot.checkHomeIdsCount(adtranDeviceFttbProvisioningCoax);
    }
}
