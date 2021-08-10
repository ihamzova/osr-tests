package com.tsystems.tm.acc.ta.team.upiter.ontcommissioning;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.data.upiter.models.ont.OntCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.Ont;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.OntOltOrchestratorRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgA4PreProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_14_0.client.model.AccessLineStatus;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_10_0.client.model.PortAndHomeIdDto;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_6_0.client.model.TpRefDto;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

@ServiceLog({
        ONT_OLT_ORCHESTRATOR_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        ACCESS_LINE_MANAGEMENT_MS,
        NETWORK_LINE_PROFILE_MANAGEMENT_MS,
        WG_A4_PROVISIONING_MS,
        DECOUPLING_MS,
        APIGW_MS
})
public class A4OntCommissioning extends GigabitTest {

  private AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
  private OntOltOrchestratorRobot ontOltOrchestratorRobot = new OntOltOrchestratorRobot();
  private WgA4PreProvisioningRobot wgA4PreProvisioningRobot = new WgA4PreProvisioningRobot();
  private PortProvisioning a4port;
  private AccessLine accessLine;
  private Ont ontSerialNumber;
  private TpRefDto tfRef;
  private UpiterTestContext context = UpiterTestContext.get();

  @AfterClass
  public void clearData() {
    accessLineRiRobot.clearDatabase();
  }

  @BeforeClass
  public void loadContext() {
    a4port = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.A4PortForReservation);
    accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.A4ontAccessLine);
    ontSerialNumber = context.getData().getOntDataProvider().get(OntCase.A4ontSerialNumber);
    tfRef = new TpRefDto().endSz(accessLine.getOltDevice().getEndsz())
            .slotNumber(accessLine.getSlotNumber())
            .portNumber(accessLine.getPortNumber())
            .klsId(ontSerialNumber.getKlsId())
            .tpRef(UUID.randomUUID().toString())
            .partyId((long) accessLine.getPartyId());
  }

  @Test
  @TmsLink("DIGIHUB-58640")
  @Description("A4 Register ONT resource")
  public void a4ontRegistration() {
    wgA4PreProvisioningRobot.startPreProvisioning(tfRef);
    accessLineRiRobot.checkA4LineParameters(a4port, tfRef.getTpRef());
    accessLine.setHomeId(accessLineRiRobot.getHomeIdByPort(accessLine));
    PortAndHomeIdDto portAndHomeIdDto = new PortAndHomeIdDto()
            .vpSz(accessLine.getOltDevice().getVpsz())
            .fachSz(accessLine.getOltDevice().getFsz())
            .slotNumber(accessLine.getSlotNumber())
            .portNumber(accessLine.getPortNumber())
            .homeId(accessLine.getHomeId());
    String lineId = ontOltOrchestratorRobot.reserveAccessLineByPortAndHomeId(portAndHomeIdDto);
    accessLine.setLineId(lineId);
    Assert.assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId()).toString(), AccessLineStatus.ASSIGNED.toString());
    Assert.assertNull(accessLineRiRobot.getAccessLinesByPort(a4port).get(0).getNetworkServiceProfileReference().getNspOntSerialNumber(), "Serial number is not null");
    ontOltOrchestratorRobot.registerOnt(accessLine, ontSerialNumber);
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId()).toString(), AccessLineStatus.ASSIGNED.toString());
    assertEquals(ontSerialNumber.getSerialNumber(), accessLineRiRobot.getAccessLinesByPort(a4port).get(0).getNetworkServiceProfileReference().getNspOntSerialNumber());
  }

  @Test(dependsOnMethods = {"a4ontRegistration"})
  @TmsLink("DIGIHUB-58673")
  @Description("A4 ONT Connectivity test")
  public void a4ontTest() {
    ontOltOrchestratorRobot.testOnt(accessLine.getLineId());
    ontOltOrchestratorRobot.updateOntState(accessLine);
    Assert.assertNotNull(accessLineRiRobot.getAccessLinesByPort(a4port).get(0).getHomeId(), "HomeId is null");
    Assert.assertEquals(ontSerialNumber.getSerialNumber(), accessLineRiRobot.getAccessLinesByPort(a4port).get(0).getNetworkServiceProfileReference().getNspOntSerialNumber());
  }

  @Test(dependsOnMethods = {"a4ontTest"})
  @TmsLink("DIGIHUB-58725")
  @Description("A4 ONT Change test")
  public void a4ontChangeTest() {
    Assert.assertEquals(ontSerialNumber.getSerialNumber(), accessLineRiRobot.getAccessLinesByPort(a4port).get(0).getNetworkServiceProfileReference().getNspOntSerialNumber());
    ontOltOrchestratorRobot.changeOntSerialNumber(accessLine, ontSerialNumber.getNewSerialNumber());
    Assert.assertEquals(ontSerialNumber.getNewSerialNumber(), accessLineRiRobot.getAccessLinesByPort(a4port).get(0).getNetworkServiceProfileReference().getNspOntSerialNumber());
  }

  @Test(dependsOnMethods = {"a4ontTest"})
  @TmsLink("DIGIHUB-58674")
  @Description("A4 Postprovisioning test(negative)")
  public void a4PostprovisioningTest() {
    PortAndHomeIdDto portAndHomeIdDto = new PortAndHomeIdDto()
            .vpSz(accessLine.getOltDevice().getVpsz())
            .fachSz(accessLine.getOltDevice().getFsz())
            .slotNumber(accessLine.getSlotNumber())
            .portNumber(accessLine.getPortNumber())
            .homeId(accessLineRiRobot.getHomeIdByPort(accessLine));
    String response = ontOltOrchestratorRobot.reserveAccessLineByPortAndHomeId(portAndHomeIdDto);
    assertEquals(response, "Walled Garden access line not found");
  }

  @Test(dependsOnMethods = {"a4ontChangeTest"})
  @TmsLink("DIGIHUB-59626")
  @Description("Decommissioning case A4")
  public void a4Decommissioning() {
    ontOltOrchestratorRobot.decommissionOnt(accessLine);
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId()).toString(), AccessLineStatus.WALLED_GARDEN.toString());
    assertNull(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getNetworkServiceProfileReference().getNspOntSerialNumber());
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getHomeId(), accessLine.getHomeId());
  }


}
