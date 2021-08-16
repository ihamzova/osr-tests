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
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_19_0.client.model.AccessLineDto;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.Card;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.Device;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
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
public class OltProvisioning5600 extends GigabitTest {

  private AccessLineRiRobot accessLineRiRobot;
  private WgAccessProvisioningRobot wgAccessProvisioningRobot;
  private PortProvisioning device5600;
  private PortProvisioning card5600v1;
  private PortProvisioning card5600v2;
  private PortProvisioning port5600;
  private PortProvisioning portProvisioningPartly;
  private PortProvisioning portProvisioningFully;
  private PortProvisioning portWithInActiveLines;
  private DefaultNeProfile defaultNeProfile;
  private DefaultNetworkLineProfile defaultNetworkLineProfile;
  private UpiterTestContext context = UpiterTestContext.get();

  @BeforeMethod
  public void prepareData() throws InterruptedException {
    accessLineRiRobot.clearDatabase();
    Thread.sleep(3000);
    accessLineRiRobot.fillDatabaseForOltCommissioning();
  }

  @AfterClass
  public void clearData() {
    accessLineRiRobot.clearDatabase();
  }

  @BeforeClass
  public void init() {
    accessLineRiRobot = new AccessLineRiRobot();
    wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
    device5600 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.port5600);
    card5600v1 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.card5600v1);
    card5600v2 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.card5600v2);
    port5600 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.port5600);
    portProvisioningPartly = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portPartlyOccupied);
    portProvisioningFully = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portFullyOccupied);
    portWithInActiveLines = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portWithInActiveLines);
    defaultNeProfile = context.getData().getDefaultNeProfileDataProvider().get(DefaultNeProfileCase.defaultNeProfile);
    defaultNetworkLineProfile = context.getData().getDefaultNetworkLineProfileDataProvider().get(DefaultNetworkLineProfileCase.defaultNLProfileFtth);
  }

  @Test
  @TmsLink("DIGIHUB-29664")
  @Description("Port provisioning case when port completely free")
  public void portProvisioningEmpty() {
    List<AccessLineDto> accessLinesBeforeProvisioning = accessLineRiRobot.getAccessLinesByPort(port5600);
    assertEquals(accessLinesBeforeProvisioning.size(), 0);
    wgAccessProvisioningRobot.startPortProvisioning(port5600);
    accessLineRiRobot.checkFtthPortParameters(port5600);
    accessLineRiRobot.checkDefaultNeProfiles(port5600, defaultNeProfile, port5600.getAccessLinesCount());
    accessLineRiRobot.checkDefaultNetworkLineProfiles(port5600, defaultNetworkLineProfile, port5600.getAccessLinesCount());
  }

  @Test
  @TmsLink("DIGIHUB-32288")
  @Description("Port provisioning case when port partly occupied")
  public void portProvisioningPartly() {
    List<AccessLineDto> accessLinesBeforeProvisioning = accessLineRiRobot.getAccessLinesByPort(portProvisioningPartly);
    assertEquals(accessLinesBeforeProvisioning.size(), 8);
    wgAccessProvisioningRobot.startPortProvisioning(portProvisioningPartly);
    accessLineRiRobot.checkFtthPortParameters(portProvisioningPartly);
    accessLineRiRobot.checkDefaultNeProfiles(portProvisioningPartly, defaultNeProfile, portProvisioningPartly.getAccessLinesCount());
    accessLineRiRobot.checkDefaultNetworkLineProfiles(portProvisioningPartly, defaultNetworkLineProfile, portProvisioningPartly.getAccessLinesCount());
  }

  @Test
  @TmsLink("DIGIHUB-40631")
  @Description("Port provisioning case when port completely occupied")
  public void portProvisioningFully() {
    List<AccessLineDto> accessLinesBeforeProvisioning = accessLineRiRobot.getAccessLinesByPort(portProvisioningFully);
    assertEquals(accessLinesBeforeProvisioning.size(), portProvisioningFully.getAccessLinesCount().intValue());
    wgAccessProvisioningRobot.startPortProvisioning(portProvisioningFully);
    accessLineRiRobot.checkFtthPortParameters(portProvisioningFully);
    accessLineRiRobot.checkDefaultNeProfiles(portProvisioningFully, defaultNeProfile, portProvisioningFully.getAccessLinesCount());
    accessLineRiRobot.checkDefaultNetworkLineProfiles(portProvisioningFully, defaultNetworkLineProfile, portProvisioningFully.getAccessLinesCount());
  }

  @Test
  @TmsLink("DIGIHUB-32026")
  @Description("Port provisioning case when port has InActive Lines")
  public void portProvisioningWithInactiveLines() {
    wgAccessProvisioningRobot.startPortProvisioning(portWithInActiveLines);
    accessLineRiRobot.checkFtthPortParameters(portWithInActiveLines);
    accessLineRiRobot.checkDefaultNeProfiles(portWithInActiveLines, defaultNeProfile, portWithInActiveLines.getAccessLinesCount());
    accessLineRiRobot.checkDefaultNetworkLineProfiles(portWithInActiveLines, defaultNetworkLineProfile, portWithInActiveLines.getAccessLinesCount());
  }

  @Test(priority = 1)
  @TmsLink("DIGIHUB-29666")
  @Description("Card provisioning case with 1 empty port")
  public void cardProvisioning() {
    Card cardBeforeProvisioning = wgAccessProvisioningRobot.getCard(card5600v1);
    wgAccessProvisioningRobot.startCardProvisioning(card5600v1);
    assertNotNull(cardBeforeProvisioning);
    assertEquals(cardBeforeProvisioning.getPorts().size(), 8);
    assertEquals(accessLineRiRobot.getAccessLinesByPort(card5600v1).size(), 0);
    accessLineRiRobot.checkFtthPortParameters(card5600v1);
    accessLineRiRobot.checkDefaultNeProfiles(card5600v1, defaultNeProfile, card5600v1.getAccessLinesCount());
    accessLineRiRobot.checkDefaultNetworkLineProfiles(card5600v1, defaultNetworkLineProfile, card5600v1.getAccessLinesCount());
  }

  @Test(priority = 1)
  @TmsLink("DIGIHUB-113901")
  @Description("Card provisioning case with 1 card")
  public void oneCardProvisioning() {
    Card cardBeforeProvisioning = wgAccessProvisioningRobot.getCard(card5600v2);
    assertEquals(accessLineRiRobot.getAccessLinesByPort(card5600v2).size(), 0);
    wgAccessProvisioningRobot.startCardProvisioningV2(card5600v2);
    assertNotNull(cardBeforeProvisioning);
    assertEquals(cardBeforeProvisioning.getPorts().size(), 3);
    accessLineRiRobot.checkFtthPortParameters(card5600v2);
    accessLineRiRobot.checkDefaultNeProfiles(card5600v2, defaultNeProfile, card5600v2.getAccessLinesCount());
    accessLineRiRobot.checkDefaultNetworkLineProfiles(card5600v2, defaultNetworkLineProfile, card5600v2.getAccessLinesCount());
  }

  @Test(priority = 1)
  @TmsLink("DIGIHUB-29667")
  @Description("Device provisioning case")
  public void deviceProvisioning() {
    Device deviceBeforeProvisioning = wgAccessProvisioningRobot.getDevice(device5600);
    assertNotNull(deviceBeforeProvisioning);
    assertEquals(deviceBeforeProvisioning.getEmsNbiName(), "MA5600T");
    assertEquals(deviceBeforeProvisioning.getEquipmentHolders().get(0).getCard().getPorts().size(), 3);

    wgAccessProvisioningRobot.startDeviceProvisioning(device5600);
    Device deviceAfterProvisioning = wgAccessProvisioningRobot.getDevice(device5600);
    PortProvisioning port = wgAccessProvisioningRobot.getPortProvisioning(device5600.getEndSz(),
            deviceAfterProvisioning.getEquipmentHolders().get(0).getSlotNumber(),
            deviceAfterProvisioning.getEquipmentHolders().get(0).getCard().getPorts().get(0).getPortNumber(), device5600);
    accessLineRiRobot.checkFtthPortParameters(port);
    accessLineRiRobot.checkDefaultNeProfiles(device5600, defaultNeProfile, device5600.getAccessLinesCount());
    accessLineRiRobot.checkDefaultNetworkLineProfiles(device5600, defaultNetworkLineProfile, device5600.getAccessLinesCount());
  }
}