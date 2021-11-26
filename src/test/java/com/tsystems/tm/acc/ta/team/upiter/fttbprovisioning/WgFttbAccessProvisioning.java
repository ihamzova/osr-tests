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
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;

@ServiceLog({
        WG_FTTB_ACCESS_PROVISIONING_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        NETWORK_LINE_PROFILE_MANAGEMENT_MS,
        ACCESS_LINE_MANAGEMENT_MS,
        DECOUPLING_MS,
        GATEWAY_ROUTE_MS
})
public class WgFttbAccessProvisioning extends GigabitTest {

  private WgFttbAccessProvisioningRobot wgFttbAccessProvisioningRobot;
  private AccessLineRiRobot accessLineRiRobot;
  private PortProvisioning oltDeviceFttbProvisioningTwistedPair;
  private PortProvisioning oltDeviceFttbProvisioningCoax;
  private DpuDevice dpuDeviceFttbProvisioningTwistedPair;
  private DpuDevice dpuDeviceFttbProvisioningCoax;
  private DpuDemand dpuDemand;
  private FttbNeProfile fttbNeProfileTp;
  private FttbNeProfile fttbNeProfileCoax;
  private DefaultNetworkLineProfile defaultNlProfileFttbTp;
  private DefaultNetworkLineProfile defaultNlProfileFttbCoax;
  private int numberOfAccessLinesForProvisioningTP;
  private int numberOfAccessLinesForProvisioningCoax;

  @BeforeClass
  public void init() throws InterruptedException {
    wgFttbAccessProvisioningRobot = new WgFttbAccessProvisioningRobot();
    accessLineRiRobot = new AccessLineRiRobot();
    accessLineRiRobot.clearDatabase();
    Thread.sleep(1000);
    UpiterTestContext context = UpiterTestContext.get();
    dpuDeviceFttbProvisioningTwistedPair = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.dpuDeviceForFttbProvisioningTwistedPair);
    dpuDeviceFttbProvisioningCoax = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.dpuDeviceForFttbProvisioningCoax);
    oltDeviceFttbProvisioningTwistedPair = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.oltDeviceForFttbProvisioningTwistedPair);
    oltDeviceFttbProvisioningCoax = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.oltDeviceForFttbProvisioningCoax);
    dpuDemand = context.getData().getDpuDemandDataProvider().get(DpuDemandCase.dpuDemand);
    defaultNlProfileFttbTp = context.getData().getDefaultNetworkLineProfileDataProvider().get(DefaultNetworkLineProfileCase.defaultNLProfileFttbTP);
    defaultNlProfileFttbCoax = context.getData().getDefaultNetworkLineProfileDataProvider().get(DefaultNetworkLineProfileCase.defaultNLProfileFttbCoax);
    fttbNeProfileTp = context.getData().getFttbNeProfileDataProvider().get(FttbNeProfileCase.fttbNeProfileTwistedPair);
    fttbNeProfileCoax = context.getData().getFttbNeProfileDataProvider().get(FttbNeProfileCase.fttbNeProfileCoax);
    numberOfAccessLinesForProvisioningTP = Integer.parseInt(dpuDemand.getNumberOfNeededDpuPorts());
    if (numberOfAccessLinesForProvisioningTP > 16) {
      numberOfAccessLinesForProvisioningTP = 16;
    }
    numberOfAccessLinesForProvisioningCoax = Integer.parseInt(dpuDemand.getNumberOfNeededDpuPorts());
    if (numberOfAccessLinesForProvisioningCoax > 16) {
      numberOfAccessLinesForProvisioningCoax = 16;
    }
  }

  @AfterClass
  public void clearData() {
    accessLineRiRobot.clearDatabase();
  }

  @Test
  @TmsLink("DIGIHUB-115527")
  @Description("FTTB Provisioning for a Device, Twisted Pair")
  public void fttbDeviceProvisioningTwistedPairTest() {
    accessLineRiRobot.fillDatabaseForDpuPreprovisioning(dpuDeviceFttbProvisioningTwistedPair, oltDeviceFttbProvisioningTwistedPair);
    accessLineRiRobot.checkLineIdsCount(oltDeviceFttbProvisioningTwistedPair);
    wgFttbAccessProvisioningRobot.startWgFttbAccessProvisioningForDevice(dpuDeviceFttbProvisioningTwistedPair.getEndsz());
    accessLineRiRobot.checkFttbLineParameters(oltDeviceFttbProvisioningTwistedPair, numberOfAccessLinesForProvisioningTP);
    accessLineRiRobot.checkAccessTransmissionMedium(dpuDeviceFttbProvisioningTwistedPair, numberOfAccessLinesForProvisioningTP);
    accessLineRiRobot.checkDefaultNetworkLineProfiles(oltDeviceFttbProvisioningTwistedPair, defaultNlProfileFttbTp, numberOfAccessLinesForProvisioningTP);
    accessLineRiRobot.checkFttbNeProfiles(oltDeviceFttbProvisioningTwistedPair, fttbNeProfileTp, numberOfAccessLinesForProvisioningTP);
    accessLineRiRobot.checkPhysicalResourceRefCountFttb(dpuDeviceFttbProvisioningTwistedPair,
            oltDeviceFttbProvisioningTwistedPair,
            numberOfAccessLinesForProvisioningTP,
            1, 1);
    accessLineRiRobot.checkLineIdsCount(oltDeviceFttbProvisioningTwistedPair);
    accessLineRiRobot.checkHomeIdsCount(oltDeviceFttbProvisioningTwistedPair);
  }

  @Test(dependsOnMethods = "fttbDeviceProvisioningTwistedPairTest", priority = 1)
  @TmsLink("DIGIHUB-115622")
  @Description("FTTB Deprovisioning for a Device, Twisted Pair")
  public void fttbDeviceDeprovisioningTwistedPairTest() {
    wgFttbAccessProvisioningRobot.startWgFttbAccessDeprovisioningForDevice(dpuDeviceFttbProvisioningTwistedPair.getEndsz());
    accessLineRiRobot.checkFttbLineParameters(oltDeviceFttbProvisioningTwistedPair, 0);
    accessLineRiRobot.checkPhysicalResourceRefCountFttb(dpuDeviceFttbProvisioningTwistedPair,
            oltDeviceFttbProvisioningTwistedPair,
            0,
            1, 1);
    accessLineRiRobot.checkLineIdsCount(oltDeviceFttbProvisioningTwistedPair);
    accessLineRiRobot.checkHomeIdsCount(oltDeviceFttbProvisioningTwistedPair);
  }

  @Test(priority = 2)
  @TmsLink("DIGIHUB-115526")
  @Description("FTTB Provisioning for a Device, Coax")
  public void fttbDeviceProvisioningCoaxTest() {
    accessLineRiRobot.clearDatabase();
    accessLineRiRobot.fillDatabaseForDpuPreprovisioning(dpuDeviceFttbProvisioningCoax, oltDeviceFttbProvisioningCoax);
    accessLineRiRobot.checkLineIdsCount(oltDeviceFttbProvisioningCoax);
    wgFttbAccessProvisioningRobot.startWgFttbAccessProvisioningForDevice(dpuDeviceFttbProvisioningCoax.getEndsz());
    accessLineRiRobot.checkFttbLineParameters(oltDeviceFttbProvisioningCoax, numberOfAccessLinesForProvisioningCoax);
    accessLineRiRobot.checkAccessTransmissionMedium(dpuDeviceFttbProvisioningCoax, numberOfAccessLinesForProvisioningCoax);
    accessLineRiRobot.checkDefaultNetworkLineProfiles(oltDeviceFttbProvisioningCoax, defaultNlProfileFttbCoax, numberOfAccessLinesForProvisioningCoax);
    accessLineRiRobot.checkFttbNeProfiles(oltDeviceFttbProvisioningCoax, fttbNeProfileCoax, numberOfAccessLinesForProvisioningCoax);
    accessLineRiRobot.checkPhysicalResourceRefCountFttb(dpuDeviceFttbProvisioningCoax,
            oltDeviceFttbProvisioningCoax,
            numberOfAccessLinesForProvisioningCoax,
            1, 1);
    accessLineRiRobot.checkLineIdsCount(oltDeviceFttbProvisioningCoax);
    accessLineRiRobot.checkHomeIdsCount(oltDeviceFttbProvisioningCoax);
  }

  @Test(dependsOnMethods = "fttbDeviceProvisioningCoaxTest", priority = 2)
  @TmsLink("DIGIHUB-115623")
  @Description("FTTB Deprovisioning for a Device, Coax")
  public void fttbDeviceDeprovisioningCoaxTest() {
    wgFttbAccessProvisioningRobot.startWgFttbAccessDeprovisioningForDevice(dpuDeviceFttbProvisioningCoax.getEndsz());
    accessLineRiRobot.checkFttbLineParameters(oltDeviceFttbProvisioningCoax, 0);
    accessLineRiRobot.checkPhysicalResourceRefCountFttb(dpuDeviceFttbProvisioningCoax,
            oltDeviceFttbProvisioningCoax,
            0,
            1, 1);
    accessLineRiRobot.checkLineIdsCount(oltDeviceFttbProvisioningCoax);
    accessLineRiRobot.checkHomeIdsCount(oltDeviceFttbProvisioningCoax);
  }
}
