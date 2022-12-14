package com.tsystems.tm.acc.ta.team.upiter.ftthprovisioning;

import com.tsystems.tm.acc.data.upiter.models.defaultneprofile.DefaultNeProfileCase;
import com.tsystems.tm.acc.data.upiter.models.defaultnetworklineprofile.DefaultNetworkLineProfileCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.DefaultNeProfile;
import com.tsystems.tm.acc.ta.data.osr.models.DefaultNetworkLineProfile;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLineDto;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.client.model.Card;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.Device;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

@ServiceLog({
        WG_ACCESS_PROVISIONING_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        ACCESS_LINE_MANAGEMENT_MS,
        NETWORK_LINE_PROFILE_MANAGEMENT_MS,
        DECOUPLING_MS,
        GATEWAY_ROUTE_MS
})

@Epic("WG Access Provisioning")
public class OltProvisioning5800 extends GigabitTest {

  private AccessLineRiRobot accessLineRiRobot;
  private WgAccessProvisioningRobot wgAccessProvisioningRobot;
  private PortProvisioning device5800;
  private PortProvisioning card5800v2;
  private PortProvisioning port5800;
  private DefaultNeProfile defaultNeProfile;
  private DefaultNetworkLineProfile defaultNetworkLineProfile;
  private UpiterTestContext context = UpiterTestContext.get();

  @BeforeClass
  public void init() throws InterruptedException {
    accessLineRiRobot = new AccessLineRiRobot();
    wgAccessProvisioningRobot = new WgAccessProvisioningRobot();

    wgAccessProvisioningRobot.changeFeatureToogleEnable64PonSplittingState(false);
    Thread.sleep(3000);

    device5800 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.device5800);
    card5800v2 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.card5800v2);
    port5800 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.port5800);
    defaultNeProfile = context.getData().getDefaultNeProfileDataProvider().get(DefaultNeProfileCase.defaultNeProfile);
    defaultNetworkLineProfile = context.getData().getDefaultNetworkLineProfileDataProvider().get(DefaultNetworkLineProfileCase.defaultNLProfileFtth);

    accessLineRiRobot.clearDatabaseByOlt("49/911/1100/76H1");
    accessLineRiRobot.clearDatabaseByOlt("49/911/1100/76H2");
    accessLineRiRobot.clearDatabaseByOlt("49/911/1100/76H4");
  }

  @Test
  @TmsLink("DIGIHUB-30877")
  @Description("Port Provisioning with 32 WG Lines")
  public void portProvisioning() {
    List<AccessLineDto> accessLinesBeforeProvisioning = accessLineRiRobot.getAccessLinesByPort(port5800);
    assertEquals(accessLinesBeforeProvisioning.size(), 0);

    wgAccessProvisioningRobot.startPortProvisioning(port5800);
    accessLineRiRobot.checkFtthPortParameters(port5800);
    accessLineRiRobot.checkDefaultNeProfiles(port5800, defaultNeProfile, port5800.getAccessLinesCount());
    accessLineRiRobot.checkDefaultNetworkLineProfiles(port5800, defaultNetworkLineProfile, port5800.getAccessLinesCount());
  }

  @Test(priority = 1)
  @TmsLink("DIGIHUB-113901")
  @Description("Card Provisioning with 1 card")
  public void oneCardProvisioning() {
    Card cardBeforeProvisioning = wgAccessProvisioningRobot.getCard(card5800v2);
    PortProvisioning port = wgAccessProvisioningRobot.getPortProvisioning(card5800v2.getEndSz(),
            card5800v2.getSlotNumber(),
            cardBeforeProvisioning.getContainsPortsRefOrValue().get(0).getPortName(), card5800v2);

    assertNotNull(cardBeforeProvisioning);
    assertEquals(cardBeforeProvisioning.getContainsPortsRefOrValue().size(), 16);
    assertEquals(accessLineRiRobot.getAccessLinesByPort(port).size(), 0);

    wgAccessProvisioningRobot.startCardProvisioning(card5800v2);
    accessLineRiRobot.checkFtthPortParameters(port);
    accessLineRiRobot.checkDefaultNeProfiles(port, defaultNeProfile, card5800v2.getAccessLinesCount());
    accessLineRiRobot.checkDefaultNetworkLineProfiles(port, defaultNetworkLineProfile, card5800v2.getAccessLinesCount());
    accessLineRiRobot.checkPhysicalResourceRefCountFtth(port, 1, 1);
  }

  @Test(priority = 1)
  @TmsLink("DIGIHUB-30824")
  @Description("Device Provisioning with 1 card and 1 port")
  public void deviceProvisioning() {
    Device device = wgAccessProvisioningRobot.getDevice(device5800);
    PortProvisioning port = wgAccessProvisioningRobot.getPortProvisioning(device5800.getEndSz(),
            device.getEquipmentHolders().get(0).getSlotNumber(),
            device.getEquipmentHolders().get(0).getCard().getPorts().get(0).getPortNumber(), device5800);

    assertNotNull(device);
    assertEquals(device.getEmsNbiName(), "MA5800-X7");
    assertEquals(device.getEquipmentHolders().get(0).getCard().getPorts().size(), 16);
    assertEquals(accessLineRiRobot.getAccessLinesByPort(port).size(), 0);

    wgAccessProvisioningRobot.startDeviceProvisioning(device5800);
    accessLineRiRobot.checkFtthPortParameters(port);
    accessLineRiRobot.checkDefaultNeProfiles(port, defaultNeProfile, device5800.getAccessLinesCount());
    accessLineRiRobot.checkDefaultNetworkLineProfiles(port, defaultNetworkLineProfile, device5800.getAccessLinesCount());
    accessLineRiRobot.checkPhysicalResourceRefCountFtth(port, 1, 1);
  }
}
