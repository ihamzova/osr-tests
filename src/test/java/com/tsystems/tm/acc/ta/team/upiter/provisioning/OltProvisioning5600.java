package com.tsystems.tm.acc.ta.team.upiter.provisioning;

import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_14_0.client.model.AccessLineDto;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.Card;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.Device;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;

@ServiceLog({
        WG_ACCESS_PROVISIONING_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        ACCESS_LINE_MANAGEMENT_MS,
        NETWORK_LINE_PROFILE_MANAGEMENT_MS,
        DECOUPLING_MS,
        GATEWAY_ROUTE_MS
})
public class OltProvisioning5600 extends GigabitTest {

  private static final Integer LATENCY_FOR_PORT_PROVISIONING = 100_000;
  private static final Integer LATENCY_FOR_DEVICE_PROVISIONING = 15 * LATENCY_FOR_PORT_PROVISIONING;

  private AccessLineRiRobot accessLineRiRobot;
  private WgAccessProvisioningRobot wgAccessProvisioningRobot;
  private PortProvisioning portEmpty;
  private PortProvisioning portProvisioningPartly;
  private PortProvisioning portProvisioningFully;
  private PortProvisioning portWithInActiveLines;
  private UpiterTestContext context = UpiterTestContext.get();

  @BeforeMethod
  public void prepareData() throws InterruptedException {
    accessLineRiRobot.clearDatabase();
    Thread.sleep(1000);
    accessLineRiRobot.fillDatabaseForOltCommissioning();
  }

  @AfterMethod
  public void clearData() {
    accessLineRiRobot.clearDatabase();
  }

  @BeforeClass
  public void init() {
    accessLineRiRobot = new AccessLineRiRobot();
    wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
    portEmpty = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portEmpty5600);
    portProvisioningPartly = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portPartlyOccupied);
    portProvisioningFully = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portFullyOccupied);
    portWithInActiveLines = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portWithInActiveLines);
  }

  @Test
  @TmsLink("DIGIHUB-29664")
  @Description("Port provisioning case when port completely free")
  public void portProvisioningEmpty() {
    List<AccessLineDto> accessLinesBeforeProvisioning = accessLineRiRobot.getAccessLinesByPort(portEmpty);
    Assert.assertEquals(accessLinesBeforeProvisioning.size(), 0);
    wgAccessProvisioningRobot.startPortProvisioning(portEmpty);
    accessLineRiRobot.checkProvisioningResults(portEmpty);
  }

  @Test
  @TmsLink("DIGIHUB-32288")
  @Description("Port provisioning case when port partly occupied")
  public void portProvisioningPartly() {
    List<AccessLineDto> accessLinesBeforeProvisioning = accessLineRiRobot.getAccessLinesByPort(portProvisioningPartly);
    Assert.assertEquals(accessLinesBeforeProvisioning.size(), 8);
    wgAccessProvisioningRobot.startPortProvisioning(portProvisioningPartly);
    accessLineRiRobot.checkProvisioningResults(portProvisioningPartly);
  }

  @Test
  @TmsLink("DIGIHUB-40631")
  @Description("Port provisioning case when port completely occupied")
  public void portProvisioningFully() {
    List<AccessLineDto> accessLinesBeforeProvisioning = accessLineRiRobot.getAccessLinesByPort(portProvisioningFully);
    Assert.assertEquals(accessLinesBeforeProvisioning.size(), portProvisioningFully.getAccessLinesCount().intValue());
    wgAccessProvisioningRobot.startPortProvisioning(portProvisioningFully);
    accessLineRiRobot.checkProvisioningResults(portProvisioningFully);
  }

  @Test
  @TmsLink("DIGIHUB-32026")
  @Description("Port provisioning case when port has InActive Lines")
  public void portProvisioningWithInactiveLines() {
    wgAccessProvisioningRobot.startPortProvisioning(portWithInActiveLines);
    accessLineRiRobot.checkProvisioningResults(portWithInActiveLines);
  }

  @Test
  @TmsLink("DIGIHUB-29666")
  @Description("Card provisioning case with 1 empty port")
  public void cardProvisioning() {
    Card cardBeforeProvisioning = wgAccessProvisioningRobot.getCard(portEmpty);
    wgAccessProvisioningRobot.startCardProvisioning(portEmpty);
    Assert.assertNotNull(cardBeforeProvisioning);
    Assert.assertEquals(cardBeforeProvisioning.getPorts().size(), 3);
    Assert.assertEquals(accessLineRiRobot.getAccessLinesByPort(portEmpty).size(), 0);
    accessLineRiRobot.checkProvisioningResults(portEmpty);
  }

  @Test
  @TmsLink("DIGIHUB-83085")
  @Description("Card provisioning case with 1 card")
  public void oneCardProvisioning() {
    Card cardBeforeProvisioning = wgAccessProvisioningRobot.getCard(portEmpty);
    wgAccessProvisioningRobot.startCardProvisioningV2(portEmpty);
    Assert.assertNotNull(cardBeforeProvisioning);
    Assert.assertEquals(cardBeforeProvisioning.getPorts().size(), 3);
    Assert.assertEquals(accessLineRiRobot.getAccessLinesByPort(portEmpty).size(), 0);
    accessLineRiRobot.checkProvisioningResults(portEmpty);
  }

  @Test
  @TmsLink("DIGIHUB-29667")
  @Description("Device provisioning case")
  public void deviceProvisioning() throws InterruptedException {
    Device deviceBeforeProvisioning = wgAccessProvisioningRobot.getDevice(portEmpty);
    Assert.assertNotNull(deviceBeforeProvisioning);
    Assert.assertEquals(deviceBeforeProvisioning.getEmsNbiName(), "MA5600T");
    Assert.assertEquals(deviceBeforeProvisioning.getEquipmentHolders().get(0).getCard().getPorts().size(), 3);

    wgAccessProvisioningRobot.startDeviceProvisioning(portEmpty);
    Thread.sleep(LATENCY_FOR_DEVICE_PROVISIONING);

    Device deviceAfterProvisioning = wgAccessProvisioningRobot.getDevice(portEmpty);
    PortProvisioning port = wgAccessProvisioningRobot.getPortProvisioning(portEmpty.getEndSz(),
            deviceAfterProvisioning.getEquipmentHolders().get(0).getSlotNumber(),
            deviceAfterProvisioning.getEquipmentHolders().get(0).getCard().getPorts().get(0).getPortNumber(), portEmpty);
    accessLineRiRobot.checkProvisioningResults(port);
  }
}
