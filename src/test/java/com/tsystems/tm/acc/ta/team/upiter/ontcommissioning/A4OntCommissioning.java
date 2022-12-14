package com.tsystems.tm.acc.ta.team.upiter.ontcommissioning;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.data.upiter.models.ont.OntCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.Ont;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.HomeIdManagementRobot;
import com.tsystems.tm.acc.ta.robot.osr.OntOltOrchestratorRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgA4PreProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLineDto;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLineStatus;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLineTechnology;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.*;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_11_0.client.model.TpRefDto;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_BAD_REQUEST_400;
import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.attachStubsToAllureReport;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.savePublishedToDefaultDir;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
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

@Epic("ONT Processes A4")
public class A4OntCommissioning extends GigabitTest {

  private AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
  private OntOltOrchestratorRobot ontOltOrchestratorRobot = new OntOltOrchestratorRobot();
  private WgA4PreProvisioningRobot wgA4PreProvisioningRobot = new WgA4PreProvisioningRobot();
  private HomeIdManagementRobot homeIdManagementRobot = new HomeIdManagementRobot();
  private PortProvisioning a4port;
  private AccessLine accessLineForCommissioning;
  private AccessLine accessLine;
  private PortProvisioning portDetectedInA4;
  private Ont ontSerialNumber;
  private TpRefDto tfRef;
  private UpiterTestContext context = UpiterTestContext.get();
  private WireMockMappingsContext mappingsContext;

  @BeforeClass
  public void init() throws InterruptedException {
    accessLineRiRobot.clearDatabase();
    Thread.sleep(1000);
    accessLineRiRobot.fillDatabaseForOltCommissioningV2(1, 1);
    a4port = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.A4PortForReservation);
    accessLineForCommissioning = context.getData().getAccessLineDataProvider().get(AccessLineCase.A4ontAccessLine);
    accessLine = new AccessLine();
    portDetectedInA4 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.PortDetectedInA4);
    ontSerialNumber = context.getData().getOntDataProvider().get(OntCase.A4ontSerialNumber);
    tfRef = new TpRefDto().endSz(accessLineForCommissioning.getOltDevice().getEndsz())
            .slotNumber(accessLineForCommissioning.getSlotNumber())
            .portNumber(accessLineForCommissioning.getPortNumber())
            .klsId(ontSerialNumber.getKlsId())
            .tpRef(UUID.randomUUID().toString())
            .partyId((long) accessLineForCommissioning.getPartyId());
  }

  @Test
  @TmsLink("DIGIHUB-58640")
  @Description("A4 Reserve AccessLine")
  public void a4ReservationTest() {
    wgA4PreProvisioningRobot.startPreProvisioning(tfRef);
    accessLineRiRobot.checkA4LineParameters(a4port, tfRef.getTpRef());
    accessLineForCommissioning.setHomeId(homeIdManagementRobot.generateHomeid().getHomeId());
    PortAndHomeIdDto portAndHomeIdDto = new PortAndHomeIdDto()
            .vpSz(accessLineForCommissioning.getOltDevice().getVpsz())
            .fachSz(accessLineForCommissioning.getOltDevice().getFsz())
            .slotNumber(accessLineForCommissioning.getSlotNumber())
            .portNumber(accessLineForCommissioning.getPortNumber())
            .homeId(accessLineForCommissioning.getHomeId());
    OperationResultLineIdDto callback = ontOltOrchestratorRobot.reserveAccessLineByPortAndHomeId(portAndHomeIdDto);

    //check callback
    assertNull("Callback returned an error", callback.getError());
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertNotNull(callback.getResponse().getLineId(), "Callback didn't return a LineId");
    assertEquals("HomeId returned in the callback is incorrect", accessLineForCommissioning.getHomeId(), callback.getResponse().getHomeId());

    // check alri
    accessLineForCommissioning.setLineId(callback.getResponse().getLineId());
    assertEquals("AccessLine state is incorrect",
            accessLineRiRobot.getAccessLineStateByLineId(accessLineForCommissioning.getLineId()), AccessLineStatus.ASSIGNED);
    assertNull("NspOntSerialNumber is present on the AccessLine although it shouldn't be",
            accessLineRiRobot.getAccessLinesByPort(a4port).get(0).getNetworkServiceProfileReference().getNspOntSerialNumber());
  }

  @Test(dependsOnMethods = {"a4ReservationTest"})
  @TmsLink("DIGIHUB-58640")
  @Description("A4 Register ONT resource")
  public void a4OntRegistrationTest() {
    OperationResultLineIdSerialNumberDto callback = ontOltOrchestratorRobot.registerOnt(accessLineForCommissioning, ontSerialNumber);

    // check callback
    assertNull("Callback returned an error", callback.getError());
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertEquals("LineId returned in the callback is incorrect", accessLineForCommissioning.getLineId(), callback.getResponse().getLineId());
    assertEquals("ONT S/N returned in the callback is incorrect", ontSerialNumber.getSerialNumber(), callback.getResponse().getSerialNumber());

    // check alri
    assertEquals("NspOntSerialNumber on the AccessLine is incorrect", ontSerialNumber.getSerialNumber(),
            accessLineRiRobot.getAccessLinesByPort(a4port).get(0).getNetworkServiceProfileReference().getNspOntSerialNumber());
  }

  @Test(dependsOnMethods = {"a4ReservationTest", "a4OntRegistrationTest"})
  @TmsLink("DIGIHUB-58673")
  @Description("A4 ONT Connectivity test, OntState = Online")
  public void a4OntTestOnline() {
    mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "A4ConnectivityTest"))
            .addA4NetworkElementPort(accessLineForCommissioning.getOltDevice().getEndsz(), accessLineForCommissioning.getPortNumber())
            .build()
            .publish()
            .publishedHook(savePublishedToDefaultDir())
            .publishedHook(attachStubsToAllureReport());

    OperationResultOntTestDto callback = ontOltOrchestratorRobot.testOnt(accessLineForCommissioning.getLineId());

    mappingsContext.deleteStubs();
    // check callback
    assertNull("Callback returned an error", callback.getError());
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertNotNull(callback.getResponse().getLastUpTime(), "Callback didn't return LastUpTime");
    assertEquals("ActualRunState returned in the callback is incorrect",
            OntState.ONLINE, callback.getResponse().getActualRunState());

    ontOltOrchestratorRobot.updateOntState(accessLineForCommissioning);

    // check alri
    assertNotNull(accessLineRiRobot.getAccessLinesByPort(a4port).get(0).getHomeId(), "HomeId is null");
    assertEquals("NspOntSerialNumber on the AccessLine is incorrect",
            ontSerialNumber.getSerialNumber(), accessLineRiRobot.getAccessLinesByPort(a4port).get(0).getNetworkServiceProfileReference().getNspOntSerialNumber());
  }

  @Test(dependsOnMethods = {"a4ReservationTest", "a4OntRegistrationTest", "a4OntTestOnline"})
  @TmsLink("DIGIHUB-58725")
  @Description("A4 ONT Change test")
  public void a4OntChangeTest() {
    assertEquals("NspOntSerialNumber on the AccessLine is incorrect", ontSerialNumber.getSerialNumber(),
            accessLineRiRobot.getAccessLinesByPort(a4port).get(0).getNetworkServiceProfileReference().getNspOntSerialNumber());
    OperationResultLineIdSerialNumberDto callback = ontOltOrchestratorRobot.changeOntSerialNumber(accessLineForCommissioning.getLineId(), ontSerialNumber.getNewSerialNumber());

    // check callback
    assertNull("Callback returned an error", callback.getError());
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertEquals("LineId in the callbac is incorrect", accessLineForCommissioning.getLineId(), callback.getResponse().getLineId());
    assertEquals("ONT S/N returned in the callback is incorrect", ontSerialNumber.getNewSerialNumber(), callback.getResponse().getSerialNumber());

    // check alri
    assertEquals("NspOntSerialNumber on the AccessLine is incorrect",
            ontSerialNumber.getNewSerialNumber(), accessLineRiRobot.getAccessLinesByPort(a4port).get(0).getNetworkServiceProfileReference().getNspOntSerialNumber());
  }

  @Test(dependsOnMethods = {"a4ReservationTest", "a4OntRegistrationTest", "a4OntTestOnline", "a4OntChangeTest"})
  @TmsLink("DIGIHUB-117787")
  @Description("ONT Pon Detection for A4, NSP is found in A4RI")
  public void onePonDetectTest() {
    OffsetDateTime timestamp = OffsetDateTime.now();
    OperationResultEmsEventDto operationResultEmsEventCallback = ontOltOrchestratorRobot.getEmsEvents(new OntConnectivityInfoDto()
            .endSz(accessLineForCommissioning.getOltDevice().getEndsz())
            .serialNumber(ontSerialNumber.getNewSerialNumber())
            .timestamp(timestamp));

    // check callback
    assertTrue(operationResultEmsEventCallback.getSuccess(), "Callback returned an error");
    assertNull("Callback returned an error", operationResultEmsEventCallback.getError());
    assertEquals("EndSz returned in the callback is incorrect",
            operationResultEmsEventCallback.getResponse().getEndSz(), portDetectedInA4.getEndSz());
    assertNull("SlotNumber was returned in the callback although it shouldn't have",
            operationResultEmsEventCallback.getResponse().getSlotNumber());
    assertEquals("PortNumber returned in the callback is incorrect",
            operationResultEmsEventCallback.getResponse().getPortNumber(), portDetectedInA4.getPortNumber());
    assertEquals("ONT S/N returned in the callback is incorrect",
            operationResultEmsEventCallback.getResponse().getSerialNumber(), ontSerialNumber.getNewSerialNumber());
    assertNull("Timestamp was returned in the callback although it shouldn't have",
            operationResultEmsEventCallback.getResponse().getTimestamp());
    assertNull("OnuId was returned in the callback although it shouldn't have",
            operationResultEmsEventCallback.getResponse().getOnuId());
    assertNull("EventMessage was returned in the callback although it shouldn't have",
            operationResultEmsEventCallback.getResponse().getEventMessage());
  }

  @Test(dependsOnMethods = {"a4ReservationTest", "a4OntRegistrationTest", "a4OntTestOnline"})
  @TmsLink("DIGIHUB-58674")
  @Description("A4 Postprovisioning test(negative)")
  public void a4PostprovisioningTest() {
    PortAndHomeIdDto portAndHomeIdDto = new PortAndHomeIdDto()
            .vpSz(accessLineForCommissioning.getOltDevice().getVpsz())
            .fachSz(accessLineForCommissioning.getOltDevice().getFsz())
            .slotNumber(accessLineForCommissioning.getSlotNumber())
            .portNumber(accessLineForCommissioning.getPortNumber())
            .homeId("00B1Q82");

    OperationResultLineIdDto callback = ontOltOrchestratorRobot.reserveAccessLineByPortAndHomeId(portAndHomeIdDto);

    // check callback
    assertEquals("Walled Garden access line not found", callback.getError().getMessage());
    assertEquals("Callback returned a wrong Error Status", 404, callback.getError().getStatus().intValue());
  }

  @Test(dependsOnMethods = {"a4ReservationTest", "a4OntRegistrationTest", "a4OntTestOnline", "a4OntChangeTest", "onePonDetectTest"})
  @TmsLink("DIGIHUB-59626")
  @Description("Decommissioning case A4")
  public void a4DecommissioningTest() {
    OperationResultVoid callback = ontOltOrchestratorRobot.decommissionOnt(accessLineForCommissioning);

    // check callback
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertNull("Callback returned an error", callback.getError());

    // check alri
    assertEquals("AccessLine state in incorrect",
            accessLineRiRobot.getAccessLineStateByLineId(accessLineForCommissioning.getLineId()), AccessLineStatus.WALLED_GARDEN);
    assertNull("NspOntSerialNumber was not deleted from the AccessLine",
            accessLineRiRobot.getAccessLinesByLineId(accessLineForCommissioning.getLineId()).get(0).getNetworkServiceProfileReference().getNspOntSerialNumber());
    assertEquals("HomeId on the AccessLine is incorrect",
            accessLineRiRobot.getAccessLinesByLineId(accessLineForCommissioning.getLineId()).get(0).getHomeId(), accessLineForCommissioning.getHomeId());
  }

  @Test
  @TmsLink("DIGIHUB-116326")
  @Description("Get attenuation measurement for an A4 AccessLine")
  public void ontAttenuationMeasurementTest() {
    accessLine.setLineId(accessLineRiRobot.getA4AccessLinesWithOnt(AccessLineTechnology.GPON).get(0).getLineId());
    AttenuationMeasurementsDto attenuationMeasurementsCallback = ontOltOrchestratorRobot.getOntAttenuationMeasurement(accessLine.getLineId());
    // check callback
    assertNotNull(attenuationMeasurementsCallback.getError(), "Callback didn't return an error");
    assertFalse("Callback didn't return an error", attenuationMeasurementsCallback.getSuccess());
    assertNull("Callback returned a response body", attenuationMeasurementsCallback.getResponse());
    assertEquals("Callback returned a wrong Error Message", "Measurement is not supported for access 4.0 platform", attenuationMeasurementsCallback.getError().getMessage());
    assertEquals("Callback returned a wrong Error Status", HTTP_CODE_BAD_REQUEST_400, attenuationMeasurementsCallback.getError().getStatus());
    assertEquals("Callback returned a wrong Error Code", "3", attenuationMeasurementsCallback.getError().getCode());
  }

  @Test
  @TmsLink("DIGIHUB-58673")
  @Description("A4 ONT Connectivity test, OntState = Offline (address mismatch)")
  public void a4OntTestOffline() {
    AccessLineDto a4AccessLine = accessLineRiRobot.getA4AccessLinesWithOnt(AccessLineTechnology.GPON).get(0);
    accessLine.setLineId(a4AccessLine.getLineId());
    accessLine.setEndSz(a4AccessLine.getReference().getEndSz());
    accessLine.setPortNumber(a4AccessLine.getReference().getPortNumber());

    OperationResultOntTestDto callback = ontOltOrchestratorRobot.testOnt(accessLine.getLineId());

    // check callback
    assertNull("Callback returned an error", callback.getError());
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertNotNull(callback.getResponse().getLastUpTime(), "Callback didn't return LastUpTime");
    assertEquals("ActualRunState in the callback is incorrect", OntState.OFFLINE, callback.getResponse().getActualRunState());
  }

  @Test
  @TmsLink("DIGIHUB-123223")
  @Description("A4 Connectivity Test for: A4RI returns no OntLastRegisteredOn, OperationalState = Working")
  public void a4OntTestNoOntLastRegisteredOnTest() {
    AccessLineDto a4AccessLine = accessLineRiRobot.getA4AccessLinesWithOnt(AccessLineTechnology.GPON).get(0);
    accessLine.setLineId(a4AccessLine.getLineId());

    mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "A4Verschaltungsfehler"))
            .addA4NspByUuidWithoutOntLastRegisteredOnWorking()
            .build()
            .publish()
            .publishedHook(savePublishedToDefaultDir())
            .publishedHook(attachStubsToAllureReport());

    OperationResultOntTestDto callback = ontOltOrchestratorRobot.testOnt(accessLine.getLineId());

    mappingsContext.deleteStubs();
    // check callback
    assertNull("Callback returned an error", callback.getError());
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertNotNull(callback.getResponse().getLastUpTime(), "Callback didn't return LastUpTime");
    assertEquals("ActualRunState in the callback is incorrect", OntState.ONLINE, callback.getResponse().getActualRunState());
  }

  @Test
  @TmsLink("DIGIHUB-123224")
  @Description("OntPonDetection for an A4 AccessLine: A4RI returns no OntLastRegisteredOn, SEAL returns Events, Slot + Port do not coincide with initial data")
  public void ontPonDetectNoOntLastRegisteredOnTest() {
    AccessLineDto a4AccessLine = accessLineRiRobot.getA4AccessLinesWithOnt(AccessLineTechnology.GPON).get(0);
    accessLine.setLineId(a4AccessLine.getLineId());
    accessLine.setEndSz(a4AccessLine.getReference().getEndSz());
    String ontSerialNumber = a4AccessLine.getNetworkServiceProfileReference().getNspOntSerialNumber();
    OffsetDateTime timestamp = OffsetDateTime.now();

    mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "A4Verschaltungsfehler"))
            .addA4NspProfileBySnMockWithoutOntLastRegisteredOn()
            .build()
            .publish()
            .publishedHook(savePublishedToDefaultDir())
            .publishedHook(attachStubsToAllureReport());

    OperationResultEmsEventDto operationResultEmsEventCallback = ontOltOrchestratorRobot.getEmsEvents(new OntConnectivityInfoDto()
            .endSz(accessLine.getEndSz())
            .serialNumber(ontSerialNumber)
            .timestamp(timestamp));

    mappingsContext.deleteStubs();

    // check callback
    assertTrue(operationResultEmsEventCallback.getSuccess(), "Callback returned an error");
    assertNull("Callback returned an error", operationResultEmsEventCallback.getError());
    assertEquals("EndSz in the callback is incorrect", accessLine.getEndSz(), operationResultEmsEventCallback.getResponse().getEndSz());
    assertEquals("SlotNumber in the callback is incorrect", "0", operationResultEmsEventCallback.getResponse().getSlotNumber());
    assertEquals("PortNumber in the callback is incorrect", "0", operationResultEmsEventCallback.getResponse().getPortNumber());
    assertEquals("ONT S/N in the callback is incorrect", ontSerialNumber, operationResultEmsEventCallback.getResponse().getSerialNumber());
    assertEquals("EventMessage in the callback is incorrect", "LastEvent", operationResultEmsEventCallback.getResponse().getEventMessage());
    assertNotNull(operationResultEmsEventCallback.getResponse().getTimestamp(), "Callback didn't return Timestamp");
    assertNotNull(operationResultEmsEventCallback.getResponse().getOnuId(), "Callback didn't return OnuId");
  }

  @Test
  @TmsLink("DIGIHUB-117788")
  @Description("OntPonDetection for an A4 AccessLine: no NSP in A4RI, SEAL returns Events, Slot + Port do not coincide with initial data")
  public void ontPonDetectNoNspInA4Test() {
    AccessLineDto a4AccessLine = accessLineRiRobot.getA4AccessLinesWithOnt(AccessLineTechnology.GPON).get(0);
    accessLine.setLineId(a4AccessLine.getLineId());
    accessLine.setEndSz(a4AccessLine.getReference().getEndSz());
    String ontSerialNumber = a4AccessLine.getNetworkServiceProfileReference().getNspOntSerialNumber();
    OffsetDateTime timestamp = OffsetDateTime.now();

    mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "A4Verschaltungsfehler"))
            .addA4NspBySnMockEmpty()
            .build()
            .publish()
            .publishedHook(savePublishedToDefaultDir())
            .publishedHook(attachStubsToAllureReport());

    OperationResultEmsEventDto operationResultEmsEventCallback = ontOltOrchestratorRobot.getEmsEvents(new OntConnectivityInfoDto()
            .endSz(accessLine.getEndSz())
            .serialNumber(ontSerialNumber)
            .timestamp(timestamp));

    mappingsContext.deleteStubs();

    // check callback
    assertTrue(operationResultEmsEventCallback.getSuccess(), "Callback returned an error");
    assertNull("Callback returned an error", operationResultEmsEventCallback.getError());
    assertEquals("EndSz in the callback is incorrect", accessLine.getEndSz(), operationResultEmsEventCallback.getResponse().getEndSz());
    assertEquals("SlotNumber in the callback is incorrect", "0", operationResultEmsEventCallback.getResponse().getSlotNumber());
    assertEquals("PortNumber in the callback is incorrect", "0", operationResultEmsEventCallback.getResponse().getPortNumber());
    assertEquals("ONT S/N in the callback is incorrect", ontSerialNumber, operationResultEmsEventCallback.getResponse().getSerialNumber());
    assertEquals("EventMessage in the callback is incorrect", "LastEvent", operationResultEmsEventCallback.getResponse().getEventMessage());
    assertNotNull(operationResultEmsEventCallback.getResponse().getTimestamp(), "Callback didn't return Timestamp");
    assertNotNull(operationResultEmsEventCallback.getResponse().getOnuId(), "Callback didn't return OnuId");
  }

  @Test
  @TmsLink("DIGIHUB-123849")
  @Description("OntPonDetection for an OLT_BNG AccessLine: no NSP in A4RI, no NSP in SEAL")
  public void ontPonDetectNoNspInA4andSealTest() {

    AccessLineDto a4AccessLine = accessLineRiRobot.getA4AccessLinesWithOnt(AccessLineTechnology.GPON).get(0);
    accessLine.setLineId(a4AccessLine.getLineId());
    accessLine.setEndSz(a4AccessLine.getReference().getEndSz());
    String ontSerialNumber = a4AccessLine.getNetworkServiceProfileReference().getNspOntSerialNumber();
    OffsetDateTime timestamp = OffsetDateTime.now();

    mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "Verschaltungsfehler"))
            .addA4NspBySnMockEmpty()
            .addSealNoEmsEventsMock()
            .build()
            .publish()
            .publishedHook(savePublishedToDefaultDir())
            .publishedHook(attachStubsToAllureReport());

    OperationResultEmsEventDto operationResultEmsEventCallback = ontOltOrchestratorRobot.getEmsEvents(new OntConnectivityInfoDto()
            .endSz(accessLine.getEndSz())
            .serialNumber(ontSerialNumber)
            .timestamp(timestamp));
    mappingsContext.deleteStubs();

    // check callback
    assertTrue(operationResultEmsEventCallback.getSuccess(), "Callback returned an error");
    assertNull("Callback returned an error", operationResultEmsEventCallback.getError());
  }

  @Test
  @TmsLink("DIGIHUB-128158")
  @Description("ONT Change, newSerialNumber = DEFAULT (Anbieterwechsel)")
  public void anbieterwechselTest() {
    AccessLineDto a4AccessLine = accessLineRiRobot.getA4AccessLinesWithOnt(AccessLineTechnology.GPON).get(0);
    accessLine.setLineId(a4AccessLine.getLineId());

    OperationResultLineIdSerialNumberDto callback = ontOltOrchestratorRobot.changeOntSerialNumber(accessLine.getLineId(), "DEFAULT");

    // check callback
    assertNull("Callback returned an error", callback.getError());
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertEquals("LineId returned in the callback is incorrect", accessLine.getLineId(), callback.getResponse().getLineId());
    assertEquals("ONT S/N returned in the callback is incorrect", "DEFAULT", callback.getResponse().getSerialNumber());

    // check alri
    assertEquals("AccessLine state is incorrect", AccessLineStatus.ASSIGNED, accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId()));
    assertEquals("NspOntSerialNumber on the AccessLine is incorrect",
            a4AccessLine.getNetworkServiceProfileReference().getNspOntSerialNumber(),
            accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getNetworkServiceProfileReference().getNspOntSerialNumber());
  }
}
