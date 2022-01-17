package com.tsystems.tm.acc.ta.team.upiter.ftthdeprovisioning;

import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.*;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static org.testng.Assert.assertEquals;

@ServiceLog({
        WG_ACCESS_PROVISIONING_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        ACCESS_LINE_MANAGEMENT_MS,
        NETWORK_LINE_PROFILE_MANAGEMENT_MS,
        DECOUPLING_MS,
        GATEWAY_ROUTE_MS
})

@Epic("WG Access Provisioning")
public class DeprovisioningTest extends GigabitTest {

  private AccessLineRiRobot accessLineRiRobot;
  private WgAccessProvisioningRobot wgAccessProvisioningRobot;
  private PortProvisioning portDepr;
  private PortProvisioning portDeprForDpu;
  private PortProvisioning cardDepr;
  private PortProvisioning deviceDepr;
  private UpiterTestContext context = UpiterTestContext.get();

  @BeforeClass
  public void init() {
    accessLineRiRobot = new AccessLineRiRobot();
    wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
    portDepr = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portDeprovisioning);
    portDeprForDpu = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portDeprovisioningForDpu);
    cardDepr = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.cardDeprovisioning);
    deviceDepr = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.deviceDepovisioning);
  }

  @BeforeMethod
  public void prepareData() {
    accessLineRiRobot.clearDatabase();
    accessLineRiRobot.fillDatabaseForOltCommissioningV2(1, 1);
  }

  @AfterClass
  public void clearData() {
    accessLineRiRobot.clearDatabase();
  }

  @Test
  @TmsLink("DIGIHUB-48518")
  @Description("Port deprovisioning case, deprovisioningForDpu = na ( = false)")
  public void portDeprovisioningTest() {
    accessLineRiRobot.checkDecommissioningPreconditions(portDepr);
    wgAccessProvisioningRobot.startPortDeprovisioning(portDepr, true);
    accessLineRiRobot.checkFtthPortParameters(portDepr);
    accessLineRiRobot.checkPhysicalResourceRefCountFtth(portDepr, 0, 1);
  }

  @Test
  @TmsLink("DIGIHUB-63668")
  @Description("Port deprovisioning case, deprovisioningForDpu = false")
  public void portDeprovisioningForDpuFalseTest() {
    accessLineRiRobot.checkDecommissioningPreconditions(portDepr);
    wgAccessProvisioningRobot.startPortDeprovisioningForDpu(portDepr, false);
    accessLineRiRobot.checkFtthPortParameters(portDepr);
    accessLineRiRobot.checkPhysicalResourceRefCountFtth(portDepr, 0, 1);
  }

  @Test
  @TmsLink("DIGIHUB-63667")
  @Description("Port deprovisioning case, deprovisioningForDpu = true")
  public void portDeprovisioningForDpuTrueTest() {
    accessLineRiRobot.checkDecommissioningPreconditions(portDeprForDpu);
    wgAccessProvisioningRobot.startPortDeprovisioningForDpu(portDeprForDpu, true);
    accessLineRiRobot.checkFtthPortParameters(portDeprForDpu);
    accessLineRiRobot.checkPhysicalResourceRefCountFtth(portDeprForDpu, 1, 1);
    List<HomeIdDto> homeIds = accessLineRiRobot.getHomeIdPool(portDeprForDpu);
    List<LineIdDto> lineIds = accessLineRiRobot.getLineIdPool(portDeprForDpu);
    long countHomeIDsFree = homeIds.stream().filter(HomeId -> HomeId.getStatus().getValue().equals(HomeIdLogicalStatus.FREE.getValue())).count();
    long countLineIDsFree = lineIds.stream().filter(LineId -> LineId.getStatus().getValue().equals(LineIdStatus.FREE.getValue())).count();
    assertEquals(accessLineRiRobot.getBackHaulId(portDeprForDpu).get(0).getStatus(), BackhaulStatus.CONFIGURED);
    assertEquals(countHomeIDsFree, portDeprForDpu.getHomeIdPool().intValue());
    assertEquals(countLineIDsFree, portDeprForDpu.getLineIdPool().intValue());
  }

  @Test
  @TmsLink("DIGIHUB-48516")
  @Description("Card deprovisioning case")
  public void cardDeprovisioningTest() {
    accessLineRiRobot.prepareTestDataToDeprovisioning(cardDepr);
    accessLineRiRobot.checkDecommissioningPreconditions(cardDepr);
    wgAccessProvisioningRobot.startCardDeprovisioning(cardDepr, true);
    accessLineRiRobot.checkFtthPortParameters(portDepr);
    accessLineRiRobot.checkPhysicalResourceRefCountFtth(portDepr, 0, 1);
  }

  @Test
  @TmsLink("DIGIHUB-113902")
  @Description("Card deprovisioning case for 1 card")
  public void oneCardDeprovisioningTest() {
    accessLineRiRobot.prepareTestDataToDeprovisioning(cardDepr);
    accessLineRiRobot.checkDecommissioningPreconditions(cardDepr);
    wgAccessProvisioningRobot.startCardDeprovisioningV2(cardDepr, false);
    accessLineRiRobot.checkFtthPortParameters(portDepr);
    accessLineRiRobot.checkPhysicalResourceRefCountFtth(portDepr, 0, 1);
  }

  @Test
  @TmsLink("DIGIHUB-48171")
  @Description("Device deprovisioning case")
  public void deviceDeprovisioningTest() {
    accessLineRiRobot.prepareTestDataToDeprovisioning(cardDepr);
    accessLineRiRobot.checkDecommissioningPreconditions(cardDepr);
    wgAccessProvisioningRobot.startDeviceDeprovisioning(deviceDepr, true);
    accessLineRiRobot.checkFtthPortParameters(portDepr);
    accessLineRiRobot.checkPhysicalResourceRefCountFtth(portDepr, 0, 0);
  }
}
