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
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_35_0.client.model.AccessLineDto;
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
  private PortProvisioning card5600v1;
  private PortProvisioning card5600v2;
  private PortProvisioning port5600;
  private PortProvisioning portTopas;
  private DefaultNeProfile defaultNeProfile;
  private DefaultNetworkLineProfile defaultNetworkLineProfile;
  private UpiterTestContext context = UpiterTestContext.get();

  @BeforeClass
  public void init() {
    accessLineRiRobot = new AccessLineRiRobot();
    wgAccessProvisioningRobot = new WgAccessProvisioningRobot();

    accessLineRiRobot.clearDatabaseByOlt("49/30/179/76H1");
    accessLineRiRobot.clearDatabaseByOlt("49/30/179/76H2");
    accessLineRiRobot.clearDatabaseByOlt("49/30/179/76H3");
    accessLineRiRobot.clearDatabaseByOlt("49/30/179/76H4");
    accessLineRiRobot.clearDatabaseByOlt("49/8571/0/76Z7");

    device5600 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.device5600);
    card5600v1 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.card5600v1);
    card5600v2 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.card5600v2);
    port5600 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.port5600);
    portTopas = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.deviceTopas);
    defaultNeProfile = context.getData().getDefaultNeProfileDataProvider().get(DefaultNeProfileCase.defaultNeProfile);
    defaultNetworkLineProfile = context.getData().getDefaultNetworkLineProfileDataProvider().get(DefaultNetworkLineProfileCase.defaultNLProfileFtth);
  }

  @Test
  @TmsLink("DIGIHUB-29664")
  @Description("Port provisioning case when port is completely free")
  public void portProvisioning() {
    List<AccessLineDto> accessLinesBeforeProvisioning = accessLineRiRobot.getAccessLinesByPort(port5600);
    assertEquals(accessLinesBeforeProvisioning.size(), 0);
    wgAccessProvisioningRobot.startPortProvisioning(port5600);
    accessLineRiRobot.checkFtthPortParameters(port5600);
    accessLineRiRobot.checkDefaultNeProfiles(port5600, defaultNeProfile, port5600.getAccessLinesCount());
    accessLineRiRobot.checkDefaultNetworkLineProfiles(port5600, defaultNetworkLineProfile, port5600.getAccessLinesCount());
    accessLineRiRobot.checkPhysicalResourceRefCountFtth(port5600, 1, 1);
    accessLineRiRobot.checkPartyId(port5600, 10001L);
    accessLineRiRobot.checkLineIdPrefix(port5600, "DTAG");
  }

  @Test(priority = 1)
  @TmsLink("DIGIHUB-29666")
  @Description("Card provisioning case with 1 empty port")
  public void cardProvisioning() {
    Card cardBeforeProvisioning = wgAccessProvisioningRobot.getCard(card5600v1);
    PortProvisioning port = wgAccessProvisioningRobot.getPortProvisioning(card5600v1.getEndSz(),
            card5600v1.getSlotNumber(),
            cardBeforeProvisioning.getContainsPortsRefOrValue().get(0).getPortName(), card5600v1);

    assertNotNull(cardBeforeProvisioning);
    assertEquals(cardBeforeProvisioning.getContainsPortsRefOrValue().size(), 8);
    assertEquals(accessLineRiRobot.getAccessLinesByPort(card5600v1).size(), 0);

    wgAccessProvisioningRobot.startCardProvisioning(card5600v1);
    accessLineRiRobot.checkFtthPortParameters(port);
    accessLineRiRobot.checkDefaultNeProfiles(port, defaultNeProfile, card5600v1.getAccessLinesCount());
    accessLineRiRobot.checkDefaultNetworkLineProfiles(port, defaultNetworkLineProfile, card5600v1.getAccessLinesCount());
    accessLineRiRobot.checkPhysicalResourceRefCountFtth(port, 1, 1);
    accessLineRiRobot.checkPartyId(port, 10001L);
    accessLineRiRobot.checkLineIdPrefix(port, "DTAG");
  }

  @Test(priority = 1)
  @TmsLink("DIGIHUB-113901")
  @Description("Card provisioning case with 1 card")
  public void oneCardProvisioning() {
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
  public void deviceProvisioning() {
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
  @Description("Port provisioning case when port is completely free, Topas")
  public void portProvisioningTopas() {
    List<AccessLineDto> accessLinesBeforeProvisioning = accessLineRiRobot.getAccessLinesByPort(portTopas);
    assertEquals(accessLinesBeforeProvisioning.size(), 0);
    wgAccessProvisioningRobot.startPortProvisioning(portTopas);
    accessLineRiRobot.checkFtthPortParameters(portTopas);
    accessLineRiRobot.checkDefaultNeProfiles(portTopas, defaultNeProfile, portTopas.getAccessLinesCount());
    accessLineRiRobot.checkDefaultNetworkLineProfiles(portTopas, defaultNetworkLineProfile, portTopas.getAccessLinesCount());
    accessLineRiRobot.checkPhysicalResourceRefCountFtth(portTopas, 1, 1);
    accessLineRiRobot.checkPartyId(portTopas, 10000L);
    accessLineRiRobot.checkLineIdPrefix(portTopas, "GFNW");
  }
}