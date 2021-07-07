package com.tsystems.tm.acc.ta.team.upiter.provisioning;

import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.WgAccessProvisioningClient;
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
public class OltProvisioning5800 extends GigabitTest {

  private AccessLineRiRobot accessLineRiRobot;
  private WgAccessProvisioningRobot wgAccessProvisioningRobot;
  private WgAccessProvisioningClient wgAccessProvisioningClient;
  private AccessLineResourceInventoryClient accessLineResourceInventoryClient;
  private PortProvisioning portEmptyV1;
  private PortProvisioning portEmptyV2;
  private UpiterTestContext context = UpiterTestContext.get();

  @BeforeClass
  public void init() {
    accessLineRiRobot = new AccessLineRiRobot();
    wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
    accessLineResourceInventoryClient = new AccessLineResourceInventoryClient();
    wgAccessProvisioningClient = new WgAccessProvisioningClient();
    portEmptyV1 = context.getData()
            .getPortProvisioningDataProvider()
            .get(PortProvisioningCase.portEmpty5800v1);
    portEmptyV2 = context.getData()
            .getPortProvisioningDataProvider()
            .get(PortProvisioningCase.portEmpty5800v2);
  }

  @BeforeMethod
  public void prepareData() {
    accessLineRiRobot.clearDatabase();
  }

  @AfterMethod
  public void clearData() {
    accessLineRiRobot.clearDatabase();
  }

  @Test
  @TmsLink("DIGIHUB-30877")
  @Description("Port Provisioning with 32 WG Lines")
  public void portProvisioning() {
    List<AccessLineDto> accessLinesBeforeProvisioning = accessLineRiRobot.getAccessLinesByPort(portEmptyV1);
    Assert.assertEquals(accessLinesBeforeProvisioning.size(), 0);

    wgAccessProvisioningRobot.startPortProvisioning(portEmptyV1);
    accessLineRiRobot.checkProvisioningResults(portEmptyV1);
  }

  @Test
  @TmsLink("DIGIHUB-30870")
  @Description("Card Provisioning with 1 port")
  public void cardProvisioning() {

    Card cardBeforeProvisioning = wgAccessProvisioningRobot.getCard(portEmptyV1);
    PortProvisioning port = wgAccessProvisioningRobot.getPortProvisioning(portEmptyV1.getEndSz(),
            portEmptyV1.getSlotNumber(),
            cardBeforeProvisioning.getPorts().get(0).getPortNumber(), portEmptyV1);

    Assert.assertNotNull(cardBeforeProvisioning);
    Assert.assertEquals(cardBeforeProvisioning.getPorts().size(), 16);
    Assert.assertEquals(accessLineRiRobot.getAccessLinesByPort(port).size(), 0);

    wgAccessProvisioningRobot.startCardProvisioning(portEmptyV1);
    accessLineRiRobot.checkProvisioningResults(port);
  }

  @Test
  @TmsLink("DIGIHUB-83085")
  @Description("Card Provisioning with 1 card")
  public void oneCardProvisioning() {
    Card cardBeforeProvisioning = wgAccessProvisioningRobot.getCard(portEmptyV2);
    PortProvisioning port = wgAccessProvisioningRobot.getPortProvisioning(portEmptyV2.getEndSz(),
            portEmptyV2.getSlotNumber(),
            cardBeforeProvisioning.getPorts().get(0).getPortNumber(), portEmptyV2);

    Assert.assertEquals(cardBeforeProvisioning.getPorts().size(), 16);
    Assert.assertEquals(accessLineRiRobot.getAccessLinesByPort(port).size(), 0);

    wgAccessProvisioningRobot.startCardProvisioningV2(portEmptyV2);
    accessLineRiRobot.checkProvisioningResults(port);
  }


  @Test
  @TmsLink("DIGIHUB-30824")
  @Description("Device Provisioning with 1 card and 1 port")
  public void deviceProvisioning() {

    Device deviceBeforeProvisioning = wgAccessProvisioningRobot.getDevice(portEmptyV1);

    PortProvisioning port = wgAccessProvisioningRobot.getPortProvisioning(portEmptyV1.getEndSz(),
            deviceBeforeProvisioning.getEquipmentHolders().get(0).getSlotNumber(),
            deviceBeforeProvisioning.getEquipmentHolders().get(0).getCard().getPorts().get(0).getPortNumber(), portEmptyV1);

    Assert.assertNotNull(deviceBeforeProvisioning);
    Assert.assertEquals(deviceBeforeProvisioning.getEmsNbiName(), "MA5800-X7");
    Assert.assertEquals(deviceBeforeProvisioning.getEquipmentHolders().get(0).getCard().getPorts().size(), 16);
    Assert.assertEquals(accessLineRiRobot.getAccessLinesByPort(port).size(), 0);

    wgAccessProvisioningRobot.startDeviceProvisioning(portEmptyV1);
    accessLineRiRobot.checkProvisioningResults(port);
  }
}
