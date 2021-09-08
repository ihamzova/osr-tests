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
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_19_0.client.model.AccessLineStatus;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.AttenuationMeasurementsDto;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.OntConnectivityInfoDto;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.OperationResultEmsEventDto;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.PortAndHomeIdDto;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_6_0.client.model.TpRefDto;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_BAD_REQUEST_400;
import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.*;

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
  private PortProvisioning portDetectedInA4;
  private AccessLine accessLineForAttenuationMeasurement;
  private Ont ontSerialNumber;
  private TpRefDto tfRef;
  private UpiterTestContext context = UpiterTestContext.get();

  @AfterClass
  public void clearData() {
    accessLineRiRobot.clearDatabase();
  }

  @BeforeClass
  public void init() throws InterruptedException {
    accessLineRiRobot.clearDatabase();
    Thread.sleep(1000);
    accessLineRiRobot.fillDatabaseForOltCommissioning();
    a4port = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.A4PortForReservation);
    accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.A4ontAccessLine);
    portDetectedInA4 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.PortDetectedInA4);
    accessLineForAttenuationMeasurement = context.getData().getAccessLineDataProvider().get(AccessLineCase.A4AccessLineForAttenuationMeasurement);
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
  public void a4ontRegistrationTest() {
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
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId()), AccessLineStatus.ASSIGNED);
    Assert.assertNull(accessLineRiRobot.getAccessLinesByPort(a4port).get(0).getNetworkServiceProfileReference().getNspOntSerialNumber(),
            "Serial number is not null");
    ontOltOrchestratorRobot.registerOnt(accessLine, ontSerialNumber);
    assertEquals(ontSerialNumber.getSerialNumber(), accessLineRiRobot.getAccessLinesByPort(a4port).get(0).getNetworkServiceProfileReference().getNspOntSerialNumber());
  }

  @Test(dependsOnMethods = {"a4ontRegistrationTest"})
  @TmsLink("DIGIHUB-58673")
  @Description("A4 ONT Connectivity test")
  public void a4ontTest() {
    ontOltOrchestratorRobot.testOnt(accessLine.getLineId());
    ontOltOrchestratorRobot.updateOntState(accessLine);
    assertNotNull(accessLineRiRobot.getAccessLinesByPort(a4port).get(0).getHomeId(), "HomeId is null");
    assertEquals(ontSerialNumber.getSerialNumber(), accessLineRiRobot.getAccessLinesByPort(a4port).get(0).getNetworkServiceProfileReference().getNspOntSerialNumber());
  }

  @Test(dependsOnMethods = {"a4ontRegistrationTest", "a4ontTest"})
  @TmsLink("DIGIHUB-58725")
  @Description("A4 ONT Change test")
  public void a4ontChangeTest() {
    assertEquals(ontSerialNumber.getSerialNumber(), accessLineRiRobot.getAccessLinesByPort(a4port).get(0).getNetworkServiceProfileReference().getNspOntSerialNumber());
    ontOltOrchestratorRobot.changeOntSerialNumber(accessLine, ontSerialNumber.getNewSerialNumber());
    assertEquals(ontSerialNumber.getNewSerialNumber(), accessLineRiRobot.getAccessLinesByPort(a4port).get(0).getNetworkServiceProfileReference().getNspOntSerialNumber());
  }

  @Test(dependsOnMethods = {"a4ontRegistrationTest", "a4ontTest", "a4ontChangeTest"})
  @TmsLink("DIGIHUB-117787")
  @Description("ONT Pon Detection for A4, NSP is found in A4RI")
  public void onePonDetectTest() {
      OffsetDateTime timestamp = OffsetDateTime.now();
      OperationResultEmsEventDto operationResultEmsEventCallback = ontOltOrchestratorRobot.getEmsEvents( new OntConnectivityInfoDto()
            .endSz(accessLine.getOltDevice().getEndsz())
            .serialNumber(ontSerialNumber.getNewSerialNumber())
            .timestamp(timestamp));
    assertTrue(operationResultEmsEventCallback.getSuccess());
    assertNull(operationResultEmsEventCallback.getError());
    assertEquals(operationResultEmsEventCallback.getResponse().getEndSz(), portDetectedInA4.getEndSz());
    assertNull(operationResultEmsEventCallback.getResponse().getSlotNumber());
    assertEquals(operationResultEmsEventCallback.getResponse().getPortNumber(), portDetectedInA4.getPortNumber());
    assertEquals(operationResultEmsEventCallback.getResponse().getSerialNumber(), ontSerialNumber.getNewSerialNumber());
    assertNull(operationResultEmsEventCallback.getResponse().getTimestamp());
    assertNull(operationResultEmsEventCallback.getResponse().getOnuId());
    assertNull(operationResultEmsEventCallback.getResponse().getEventMessage());
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
  public void a4DecommissioningTest() {
    ontOltOrchestratorRobot.decommissionOnt(accessLine);
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId()).toString(), AccessLineStatus.WALLED_GARDEN.toString());
    assertNull(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getNetworkServiceProfileReference().getNspOntSerialNumber());
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getHomeId(), accessLine.getHomeId());
  }

  @Test
  @TmsLink("DIGIHUB-116326")
  @Description("Get attenuation measurement for an A4 AccessLine")
  public void ontAttenuationMeasurementTest() {
    AttenuationMeasurementsDto attenuationMeasurementsCallback = ontOltOrchestratorRobot.getOntAttenuationMeasurement(accessLineForAttenuationMeasurement);
    assertNotNull(attenuationMeasurementsCallback.getError());
    assertFalse(attenuationMeasurementsCallback.getSuccess());
    assertNull(attenuationMeasurementsCallback.getResponse());
    assertEquals("Measurement is not supported for access 4.0 platform", attenuationMeasurementsCallback.getError().getMessage());
    assertEquals(HTTP_CODE_BAD_REQUEST_400, attenuationMeasurementsCallback.getError().getStatus());
    assertEquals("3", attenuationMeasurementsCallback.getError().getCode());
  }
}
