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
public class OltProvisioning5600 extends GigabitTest {

  private AccessLineRiRobot accessLineRiRobot;
  private WgAccessProvisioningRobot wgAccessProvisioningRobot;
  private PortProvisioning device5600;
  private PortProvisioning card5600v2;
  private PortProvisioning port5600v1;
  private PortProvisioning port5600v2;
  private PortProvisioning portGfnw;
  private PortProvisioning portGfps;
  private DefaultNeProfile defaultNeProfile;
  private DefaultNetworkLineProfile defaultNetworkLineProfile;
  private DefaultNetworkLineProfile defaultNetworkLineProfileV2;
  private UpiterTestContext context = UpiterTestContext.get();

  @BeforeClass
  public void init() {
    accessLineRiRobot = new AccessLineRiRobot();
    wgAccessProvisioningRobot = new WgAccessProvisioningRobot();

    //accessLineRiRobot.clearDatabaseByOlt("49/30/179/76H1");
    accessLineRiRobot.clearDatabaseByOlt("49/30/179/76H2");
    accessLineRiRobot.clearDatabaseByOlt("49/30/179/76H3");
    accessLineRiRobot.clearDatabaseByOlt("49/30/179/76H4");
    accessLineRiRobot.clearDatabaseByOlt("49/8571/0/76Z7");

    device5600 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.device5600);
    card5600v2 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.card5600v2);
    port5600v1 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.port5600v1);
    port5600v2 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.port5600v2);
    portGfnw = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.deviceGfnw);
    portGfps = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.deviceGfps);
    defaultNeProfile = context.getData().getDefaultNeProfileDataProvider().get(DefaultNeProfileCase.defaultNeProfile);
    defaultNetworkLineProfile = context.getData().getDefaultNetworkLineProfileDataProvider().get(DefaultNetworkLineProfileCase.defaultNLProfileFtth);
    defaultNetworkLineProfileV2 = context.getData().getDefaultNetworkLineProfileDataProvider().get(DefaultNetworkLineProfileCase.defaultNLProfileFtthV2);
  }

  @Test
  @TmsLink("DIGIHUB-151327")
  @Description("Port provisioning case when port is completely free, enable-64-pon-splitting toggle off")
  public void portProvisioningToggleOff() throws InterruptedException {
    wgAccessProvisioningRobot.changeFeatureToogleEnable64PonSplittingState(false);
    Thread.sleep(5000);

    List<AccessLineDto> accessLinesBeforeProvisioning = accessLineRiRobot.getAccessLinesByPort(port5600v1);
    assertEquals(accessLinesBeforeProvisioning.size(), 0);
    wgAccessProvisioningRobot.startPortProvisioning(port5600v1);
    accessLineRiRobot.checkFtthPortParameters(port5600v1);
    accessLineRiRobot.checkDefaultNeProfiles(port5600v1, defaultNeProfile, port5600v1.getAccessLinesCount());
    accessLineRiRobot.checkDefaultNetworkLineProfiles(port5600v1, defaultNetworkLineProfile, port5600v1.getAccessLinesCount());
    accessLineRiRobot.checkPhysicalResourceRefCountFtth(port5600v1, 1, 1);
    accessLineRiRobot.checkPartyId(port5600v1, 10001L);
    accessLineRiRobot.checkLineIdPrefix(port5600v1, "DTAG");
  }

  @Test
  @TmsLink("DIGIHUB-151326")
  @Description("Port provisioning case when port is completely free, enable-64-pon-splitting toggle on")
  public void portProvisioningToggleOn() throws InterruptedException {
    wgAccessProvisioningRobot.changeFeatureToogleEnable64PonSplittingState(true);
    Thread.sleep(5000);

    List<AccessLineDto> accessLinesBeforeProvisioning = accessLineRiRobot.getAccessLinesByPort(port5600v2);
    assertEquals(accessLinesBeforeProvisioning.size(), 0);
    wgAccessProvisioningRobot.startPortProvisioning(port5600v2);
    accessLineRiRobot.checkFtthPortParameters(port5600v2);
    accessLineRiRobot.checkDefaultNeProfiles(port5600v2, defaultNeProfile, port5600v2.getAccessLinesCount());
    accessLineRiRobot.checkDefaultNetworkLineProfiles(port5600v2, defaultNetworkLineProfileV2, port5600v2.getAccessLinesCount());
    accessLineRiRobot.checkPhysicalResourceRefCountFtth(port5600v2, 1, 1);
    accessLineRiRobot.checkPartyId(port5600v2, 10001L);
    accessLineRiRobot.checkLineIdPrefix(port5600v2, "DTAG");
  }

  @Test(priority = 1)
  @TmsLink("DIGIHUB-113901")
  @Description("Card provisioning case with 1 card")
  public void oneCardProvisioning() throws InterruptedException {
    wgAccessProvisioningRobot.changeFeatureToogleEnable64PonSplittingState(false);
    Thread.sleep(5000);

    Card cardBeforeProvisioning = wgAccessProvisioningRobot.getCard(card5600v2);
    PortProvisioning port = wgAccessProvisioningRobot.getPortProvisioning(card5600v2.getEndSz(),
            card5600v2.getSlotNumber(),
            cardBeforeProvisioning.getContainsPortsRefOrValue().get(1).getPortName(), card5600v2);

    assertNotNull(cardBeforeProvisioning);
    assertEquals(cardBeforeProvisioning.getContainsPortsRefOrValue().size(), 3);
    assertEquals(accessLineRiRobot.getAccessLinesByPort(card5600v2).size(), 0);

    wgAccessProvisioningRobot.startCardProvisioningV2(card5600v2);

    accessLineRiRobot.checkFtthPortParameters(port);
    accessLineRiRobot.checkDefaultNeProfiles(port, defaultNeProfile, card5600v2.getAccessLinesCount());
    accessLineRiRobot.checkDefaultNetworkLineProfiles(port, defaultNetworkLineProfile, card5600v2.getAccessLinesCount());
    accessLineRiRobot.checkPhysicalResourceRefCountFtth(port, 1, 1);
    accessLineRiRobot.checkPartyId(port, 10001L);
    accessLineRiRobot.checkLineIdPrefix(port, "DTAG");
  }

  @Test(priority = 1)
  @TmsLink("DIGIHUB-29667")
  @Description("Device provisioning case")
  public void deviceProvisioning() throws InterruptedException {
    wgAccessProvisioningRobot.changeFeatureToogleEnable64PonSplittingState(false);
    Thread.sleep(5000);

    Device device = wgAccessProvisioningRobot.getDevice(device5600);
    PortProvisioning port = wgAccessProvisioningRobot.getPortProvisioning(device5600.getEndSz(),
            device.getEquipmentHolders().get(1).getSlotNumber(),
            device.getEquipmentHolders().get(1).getCard().getPorts().get(0).getPortNumber(), device5600);

    assertNotNull(device);
    assertEquals(device.getEmsNbiName(), "MA5600T");
    assertEquals(device.getEquipmentHolders().get(0).getCard().getPorts().size(), 3);
    assertEquals(accessLineRiRobot.getAccessLinesByPort(port).size(), 0);

    wgAccessProvisioningRobot.startDeviceProvisioning(device5600);

    accessLineRiRobot.checkFtthPortParameters(port);
    accessLineRiRobot.checkDefaultNeProfiles(port, defaultNeProfile, device5600.getAccessLinesCount());
    accessLineRiRobot.checkDefaultNetworkLineProfiles(port, defaultNetworkLineProfile, device5600.getAccessLinesCount());
    accessLineRiRobot.checkPhysicalResourceRefCountFtth(port, 1, 1);
    accessLineRiRobot.checkPartyId(port, 10001L);
    accessLineRiRobot.checkLineIdPrefix(port, "DTAG");
  }

  @Test
  @TmsLink("DIGIHUB-74001")
  @Description("Port provisioning case when port is completely free, Topas (GFNW)")
  public void portProvisioningGfnw() throws InterruptedException {
    wgAccessProvisioningRobot.changeFeatureToogleEnable64PonSplittingState(false);
    Thread.sleep(5000);

    List<AccessLineDto> accessLinesBeforeProvisioning = accessLineRiRobot.getAccessLinesByPort(portGfnw);
    assertEquals(accessLinesBeforeProvisioning.size(), 0);
    wgAccessProvisioningRobot.startPortProvisioning(portGfnw);
    accessLineRiRobot.checkFtthPortParameters(portGfnw);
    accessLineRiRobot.checkDefaultNeProfiles(portGfnw, defaultNeProfile, portGfnw.getAccessLinesCount());
    accessLineRiRobot.checkDefaultNetworkLineProfiles(portGfnw, defaultNetworkLineProfile, portGfnw.getAccessLinesCount());
    accessLineRiRobot.checkPhysicalResourceRefCountFtth(portGfnw, 1, 1);
    accessLineRiRobot.checkPartyId(portGfnw, 10000L);
    accessLineRiRobot.checkLineIdPrefix(portGfnw, "GFNW");
  }

  @Test
  @TmsLink("DIGIHUB-*****")
  @Description("Port provisioning case when port is completely free, GF+ (GFPS)")
  public void portProvisioningGfps() throws InterruptedException {
    wgAccessProvisioningRobot.changeFeatureToogleEnable64PonSplittingState(false);
    Thread.sleep(5000);

    List<AccessLineDto> accessLinesBeforeProvisioning = accessLineRiRobot.getAccessLinesByPort(portGfps);
    assertEquals(accessLinesBeforeProvisioning.size(), 0);
    wgAccessProvisioningRobot.startPortProvisioning(portGfps);
    accessLineRiRobot.checkFtthPortParameters(portGfps);
    accessLineRiRobot.checkDefaultNeProfiles(portGfps, defaultNeProfile, portGfps.getAccessLinesCount());
    accessLineRiRobot.checkDefaultNetworkLineProfiles(portGfps, defaultNetworkLineProfile, portGfps.getAccessLinesCount());
    accessLineRiRobot.checkPhysicalResourceRefCountFtth(portGfps, 1, 1);
    accessLineRiRobot.checkPartyId(portGfps, 10257L);
    accessLineRiRobot.checkLineIdPrefix(portGfps, "GFPS");
  }
}