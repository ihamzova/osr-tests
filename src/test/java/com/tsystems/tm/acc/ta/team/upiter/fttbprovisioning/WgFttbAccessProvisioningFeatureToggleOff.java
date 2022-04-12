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
public class WgFttbAccessProvisioningFeatureToggleOff extends GigabitTest {

    private WgFttbAccessProvisioningRobot wgFttbAccessProvisioningRobot;
    private AccessLineRiRobot accessLineRiRobot;
    private PortProvisioning oltDeviceFttbProvisioningCoax;
    private PortProvisioning adtranDeviceFttbProvisioningTwistedPair;
    private DpuDevice dpuDeviceFttbProvisioningCoax;
    private DpuDevice dpuDeviceFttbProvisioningonOnAdtranTwistedPair;
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
        wgFttbAccessProvisioningRobot.changeFeatureToogleDpuDemandState(false);

        accessLineRiRobot = new AccessLineRiRobot();
        accessLineRiRobot.clearDatabaseByOlt("49/89/8000/76H2");
        accessLineRiRobot.clearDatabaseByOlt("49/911/1100/76H1");
        accessLineRiRobot.clearDatabaseByOlt("49/911/1100/76H3");
        accessLineRiRobot.clearDatabaseByOlt("49/911/1100/76H5");
        UpiterTestContext context = UpiterTestContext.get();

        dpuDeviceFttbProvisioningCoax = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.dpuDeviceForFttbProvisioningCoax);
        dpuDeviceFttbProvisioningonOnAdtranTwistedPair = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.dpuDeviceForFttbProvisioningOnAdtranTwistedPair);

        oltDeviceFttbProvisioningCoax = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.oltDeviceForFttbProvisioningCoax);
        adtranDeviceFttbProvisioningTwistedPair = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.adtranDeviceForFttbProvisioningTwistedPair);

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

        accessLineRiRobot.fillDatabaseForDpuPreprovisioningV2(20000, 20000, dpuDeviceFttbProvisioningCoax, oltDeviceFttbProvisioningCoax);
        accessLineRiRobot.fillDatabaseForDpuPreprovisioningV2(30000, 30000, dpuDeviceFttbProvisioningonOnAdtranTwistedPair, adtranDeviceFttbProvisioningTwistedPair);

        // sleep to let the ms get the new value of the feature toggle
        Thread.sleep(3000);
    }

    @Test
    @TmsLink("DIGIHUB-123653")
    @Description("FTTB Provisioning for a Huawei Device, Coax, Feature Toggle Off")
    public void fttbDeviceProvisioningCoaxTest() {
        wgFttbAccessProvisioningRobot.startWgFttbAccessProvisioningForDevice(dpuDeviceFttbProvisioningCoax.getEndsz());
        accessLineRiRobot.checkFttbLineParameters(oltDeviceFttbProvisioningCoax, numberOfAccessLinesForProvisioning);
        accessLineRiRobot.checkAccessTransmissionMedium(dpuDeviceFttbProvisioningCoax, numberOfAccessLinesForProvisioning);
        accessLineRiRobot.checkDefaultNetworkLineProfiles(oltDeviceFttbProvisioningCoax, defaultNlProfileFttbCoax, numberOfAccessLinesForProvisioning);
        accessLineRiRobot.checkFttbNeProfiles(oltDeviceFttbProvisioningCoax, fttbNeProfileCoax, numberOfAccessLinesForProvisioning);
        accessLineRiRobot.checkPhysicalResourceRefCountFttb(dpuDeviceFttbProvisioningCoax,
                oltDeviceFttbProvisioningCoax,
                numberOfAccessLinesForProvisioning,
                1, 1);
        accessLineRiRobot.checkHomeIdsCount(oltDeviceFttbProvisioningCoax);
    }

    @Test(dependsOnMethods = "fttbDeviceProvisioningCoaxTest")
    @TmsLink("DIGIHUB-123653")
    @Description("FTTB Deprovisioning for a Huawei Device, Coax, Feature Toggle Off")
    public void fttbDeviceDeprovisioningCoaxTest() {
        wgFttbAccessProvisioningRobot.startWgFttbAccessDeprovisioningForDevice(dpuDeviceFttbProvisioningCoax.getEndsz());
        accessLineRiRobot.checkFttbLineParameters(oltDeviceFttbProvisioningCoax, 0);
        accessLineRiRobot.checkPhysicalResourceRefCountFttb(dpuDeviceFttbProvisioningCoax,
                oltDeviceFttbProvisioningCoax,
                0,
                1, 1);
        accessLineRiRobot.checkHomeIdsCount(oltDeviceFttbProvisioningCoax);
    }

    @Test
    @TmsLink("DIGIHUB-124170")
    @Description("FTTB Provisioning for a Device on Adtran, Twisted Pair, Feature Toggle Off")
    public void fttbDeviceProvisioningOnAdtranTpTest() {
        wgFttbAccessProvisioningRobot.startWgFttbAccessProvisioningForDevice(dpuDeviceFttbProvisioningonOnAdtranTwistedPair.getEndsz());
        accessLineRiRobot.checkFttbLineParameters(adtranDeviceFttbProvisioningTwistedPair, numberOfAccessLinesForProvisioning);
        accessLineRiRobot.checkAccessTransmissionMedium(dpuDeviceFttbProvisioningonOnAdtranTwistedPair, numberOfAccessLinesForProvisioning);
        accessLineRiRobot.checkDefaultNetworkLineProfiles(adtranDeviceFttbProvisioningTwistedPair, defaultNlProfileFttbTp, numberOfAccessLinesForProvisioning);
        accessLineRiRobot.checkFttbNeProfiles(adtranDeviceFttbProvisioningTwistedPair, fttbNeProfileTp, numberOfAccessLinesForProvisioning);
        accessLineRiRobot.checkPhysicalResourceRefCountFttb(dpuDeviceFttbProvisioningonOnAdtranTwistedPair,
                adtranDeviceFttbProvisioningTwistedPair,
                numberOfAccessLinesForProvisioning,
                1, 1);
        accessLineRiRobot.checkHomeIdsCount(adtranDeviceFttbProvisioningTwistedPair);
    }

    @Test(dependsOnMethods = "fttbDeviceProvisioningOnAdtranTpTest")
    @TmsLink("DIGIHUB-124171")
    @Description("FTTB Deprovisioning for a Device on Adtran, Twisted Pair, Feature Toggle Off")
    public void fttbDeviceDeprovisioningOnAdtranTpTest() {
        wgFttbAccessProvisioningRobot.startWgFttbAccessDeprovisioningForDevice(dpuDeviceFttbProvisioningonOnAdtranTwistedPair.getEndsz());
        accessLineRiRobot.checkFttbLineParameters(adtranDeviceFttbProvisioningTwistedPair, 0);
        accessLineRiRobot.checkPhysicalResourceRefCountFttb(dpuDeviceFttbProvisioningonOnAdtranTwistedPair,
                adtranDeviceFttbProvisioningTwistedPair,
                0,
                1, 1);
        accessLineRiRobot.checkHomeIdsCount(adtranDeviceFttbProvisioningTwistedPair);
    }
}
