package com.tsystems.tm.acc.ta.team.upiter.ontcommissioning;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.data.upiter.models.businessinformation.BusinessInformationCase;
import com.tsystems.tm.acc.data.upiter.models.ont.OntCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.BusinessInformation;
import com.tsystems.tm.acc.ta.data.osr.models.Ont;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.OntOltOrchestratorRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.OntState;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.*;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.*;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.attachStubsToAllureReport;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.savePublishedToDefaultDir;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

@ServiceLog({
        ONT_OLT_ORCHESTRATOR_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        WG_ACCESS_PROVISIONING_MS,
        ACCESS_LINE_MANAGEMENT_MS,
        NETWORK_LINE_PROFILE_MANAGEMENT_MS,
        DECOUPLING_MS,
        GATEWAY_ROUTE_MS
})
@Epic("OLT_BNG FTTH ONT Processes")
public class OntCommissioning extends GigabitTest {

  private AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
  private OntOltOrchestratorRobot ontOltOrchestratorRobot = new OntOltOrchestratorRobot();
  private AccessLine accessLineForCommissioning;
  private AccessLine accessLine;
  private AccessLine accessLineAnbieterwechsel;
  private AccessLine accessLineFor33LineCaseNew;
  private AccessLine accessLineFor33LineCaseOld;
  private AccessLine accessLineForDecommissioningRollbackTrue;
  private AccessLine accessLineForDecommissioningRollbackFalse;
  private BusinessInformation postprovisioningStart;
  private BusinessInformation postprovisioningEnd;
  private Ont ontSerialNumber;
  private PortProvisioning portDetectedInA4;
  private PortProvisioning port;
  private UpiterTestContext context = UpiterTestContext.get();
  private WireMockMappingsContext mappingsContext;


  @BeforeClass
  public void init() throws InterruptedException {
    accessLineRiRobot.clearDatabase();
    Thread.sleep(1000);
    accessLineRiRobot.fillDatabaseForOltCommissioningV1();
    port = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.PortFor33LineCase);
    accessLineForCommissioning = context.getData().getAccessLineDataProvider().get(AccessLineCase.OntRegistrationAccessLine);
    accessLineFor33LineCaseNew = context.getData().getAccessLineDataProvider().get(AccessLineCase.ForDecommissioning33LineCaseAccessLine1);
    accessLineFor33LineCaseOld = context.getData().getAccessLineDataProvider().get(AccessLineCase.ForDecommissioning33LineCaseAccessLine2);
    accessLineForDecommissioningRollbackTrue = context.getData().getAccessLineDataProvider().get(AccessLineCase.ForDecommissioingRollbackTrue);
    accessLineForDecommissioningRollbackFalse = context.getData().getAccessLineDataProvider().get(AccessLineCase.ForDecommissioingRollbackFalse);
    accessLineAnbieterwechsel = context.getData().getAccessLineDataProvider().get(AccessLineCase.AccessLineAnbieterwechsel);
    postprovisioningStart = context.getData().getBusinessInformationDataProvider().get(BusinessInformationCase.PostprovisioningStartEvent);
    postprovisioningEnd = context.getData().getBusinessInformationDataProvider().get(BusinessInformationCase.PostprovisioningEndEvent);
    ontSerialNumber = context.getData().getOntDataProvider().get(OntCase.OntSerialNumber);
    accessLine = new AccessLine();
    portDetectedInA4 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.PortDetectedInA4);
  }

  @AfterClass
  public void clearData() {
    accessLineRiRobot.clearDatabase();
  }

  @Test
  @TmsLink("DIGIHUB-71918")
  @Description("ONT Access Line Reservation by HomeID")
  public void accessLineReservationByPortAndHomeIdTest() {
    accessLineForCommissioning.setHomeId(accessLineRiRobot.getHomeIdByPort(accessLineForCommissioning));
    PortAndHomeIdDto portAndHomeIdDto = new PortAndHomeIdDto()
            .vpSz(accessLineForCommissioning.getOltDevice().getVpsz())
            .fachSz(accessLineForCommissioning.getOltDevice().getFsz())
            .slotNumber(accessLineForCommissioning.getSlotNumber())
            .portNumber(accessLineForCommissioning.getPortNumber())
            .homeId(accessLineForCommissioning.getHomeId());
    OperationResultLineIdDto callback = ontOltOrchestratorRobot.reserveAccessLineByPortAndHomeId(portAndHomeIdDto);

    // check callback
    assertNull(callback.getError());
    assertTrue(callback.getSuccess());
    assertNotNull(callback.getResponse().getLineId());
    assertEquals(accessLineForCommissioning.getHomeId(), callback.getResponse().getHomeId());

    // check alri
    accessLineForCommissioning.setLineId(callback.getResponse().getLineId());
    assertEquals(AccessLineStatus.ASSIGNED, accessLineRiRobot.getAccessLineStateByLineId(accessLineForCommissioning.getLineId()));

/*        //Create temp List to check business data
        List<BusinessInformation> businessInformationList = new ArrayList<>();
        businessInformationList.add(postprovisioningStart);
        businessInformationList.add(postprovisioningEnd);

        List<BusinessInformation> businessInformationLogCollector = wgAccessProvisioningRobot.getBusinessInformation();
        Assert.assertTrue(businessInformationLogCollector.containsAll(businessInformationList), "Business information is not found");*/
  }

  @Test(dependsOnMethods = "accessLineReservationByPortAndHomeIdTest")
  @TmsLink("DIGIHUB-47257")
  @Description("Register ONT resource")
  public void ontRegistrationTest() {
    OperationResultLineIdSerialNumberDto callback = ontOltOrchestratorRobot.registerOnt(accessLineForCommissioning, ontSerialNumber);

    // check callback
    assertNull(callback.getError());
    assertTrue(callback.getSuccess());
    assertEquals(accessLineForCommissioning.getLineId(), callback.getResponse().getLineId());
    assertEquals(ontSerialNumber.getSerialNumber(), callback.getResponse().getSerialNumber());

    // check alri
    SubscriberNeProfileDto subscriberNEProfile = accessLineRiRobot.getSubscriberNEProfile(accessLineForCommissioning.getLineId());
    assertNotNull(subscriberNEProfile);
    assertEquals(subscriberNEProfile.getOntSerialNumber(), ontSerialNumber.getSerialNumber());
    assertEquals(subscriberNEProfile.getState(), ProfileState.ACTIVE);
    assertEquals(subscriberNEProfile.getOntState(), OntState.UNKNOWN);
  }

  @Test(dependsOnMethods = {"accessLineReservationByPortAndHomeIdTest", "ontRegistrationTest"})
  @TmsLink("DIGIHUB-33938")
  @Description("ONT Connectivity test")
  public void ontTest() {
    OperationResultOntTestDto callback = ontOltOrchestratorRobot.testOnt(accessLineForCommissioning.getLineId());

    // check callback
    assertNull(callback.getError());
    assertTrue(callback.getSuccess());
    assertNotNull(callback.getResponse().getLastDownCause());
    assertNotNull(callback.getResponse().getLastDownTime());
    assertNotNull(callback.getResponse().getLastUpTime());
    assertEquals(com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.OntState.ONLINE, callback.getResponse().getActualRunState());
    //todo add check for onuid

    ontOltOrchestratorRobot.updateOntState(accessLineForCommissioning);

    // check alri
    SubscriberNeProfileDto subscriberNEProfile = accessLineRiRobot.getSubscriberNEProfile(accessLineForCommissioning.getLineId());
    assertNotNull(subscriberNEProfile);
    assertEquals(subscriberNEProfile.getOntState(), OntState.ONLINE);
  }

  @Test(dependsOnMethods = {"accessLineReservationByPortAndHomeIdTest", "ontRegistrationTest", "ontTest"})
  @TmsLink("DIGIHUB-53891")
  @Description("ONT Change")
  public void ontChangeTest() {
    assertEquals(accessLineRiRobot.getSubscriberNEProfile(accessLineForCommissioning.getLineId()).getOntSerialNumber(),
            ontSerialNumber.getSerialNumber());
    OperationResultLineIdSerialNumberDto callback = ontOltOrchestratorRobot.changeOntSerialNumber(accessLineForCommissioning, ontSerialNumber.getNewSerialNumber());

    // check callback
    assertNull(callback.getError());
    assertTrue(callback.getSuccess());
    assertEquals(accessLineForCommissioning.getLineId(), callback.getResponse().getLineId());
    assertEquals(ontSerialNumber.getNewSerialNumber(), callback.getResponse().getSerialNumber());

    // check alri
    assertEquals(accessLineRiRobot.getSubscriberNEProfile(accessLineForCommissioning.getLineId()).getOntSerialNumber(),
            ontSerialNumber.getNewSerialNumber());
  }

  @Test(dependsOnMethods = {"accessLineReservationByPortAndHomeIdTest", "ontRegistrationTest", "ontTest", "ontChangeTest"})
  @TmsLink("DIGIHUB-53292")
  @Description("ONT Decommissioning, rollback to reservation = empty")
  public void ontDecommissioningTest() {
    OperationResultVoid callback = ontOltOrchestratorRobot.decommissionOnt(accessLineForCommissioning);

    // check callback
    assertTrue(callback.getSuccess());
    assertNull(callback.getError());
    assertNull(callback.getResponse());

    // check alri
    assertNull(accessLineRiRobot.getSubscriberNEProfile(accessLineForCommissioning.getLineId()));
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLineForCommissioning.getLineId()),
            AccessLineStatus.WALLED_GARDEN);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineForCommissioning.getLineId()).get(0).getHomeId(),
            accessLineForCommissioning.getHomeId());
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineForCommissioning.getLineId()).get(0).getDefaultNeProfile().getState(),
            ProfileState.ACTIVE);
  }

  @Test
  @TmsLink("DIGIHUB-38181")
  @Description("ONT Decommissioning, rollback to reservation = true")
  public void ontDecommissioningWithRollbackTrueTest() {
    OperationResultVoid callback = ontOltOrchestratorRobot.decommissionOntWithRollback(accessLineForDecommissioningRollbackTrue, true);

    // check callback
    assertTrue(callback.getSuccess());
    assertNull(callback.getError());
    assertNull(callback.getResponse());

    // check alri
    assertNull(accessLineRiRobot.getSubscriberNEProfile(accessLineForDecommissioningRollbackTrue.getLineId()));
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLineForDecommissioningRollbackTrue.getLineId()),
            AccessLineStatus.ASSIGNED);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineForDecommissioningRollbackTrue.getLineId()).get(0).getHomeId(),
            accessLineForDecommissioningRollbackTrue.getHomeId());
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineForDecommissioningRollbackTrue.getLineId()).get(0).getDefaultNeProfile().getState(),
            ProfileState.ACTIVE);
  }

  @Test
  @TmsLink("DIGIHUB-38182")
  @Description("ONT Decommissioning, rollback to reservation = false")
  public void ontDecommissioningWithRollbackFalseTest() {
    OperationResultVoid callback =
            ontOltOrchestratorRobot.decommissionOntWithRollback(accessLineForDecommissioningRollbackFalse, false);

    // check callback
    assertTrue(callback.getSuccess());
    assertNull(callback.getError());
    assertNull(callback.getResponse());

    // check alri
    assertNull(accessLineRiRobot.getSubscriberNEProfile(accessLineForDecommissioningRollbackFalse.getLineId()));
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLineForDecommissioningRollbackFalse.getLineId()),
            AccessLineStatus.WALLED_GARDEN);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineForDecommissioningRollbackFalse.getLineId()).get(0).getHomeId(),
            accessLineForDecommissioningRollbackFalse.getHomeId());
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineForDecommissioningRollbackFalse.getLineId()).get(0).getDefaultNeProfile().getState(),
            ProfileState.ACTIVE);
  }

  @Test()
  @TmsLink("DIGIHUB-42230")
  @Description("Deprovisioning of the 33d AccessLine after termination")
  @Owner("DL_T-Magic.U-Piter@t-systems.com")
  public void ontDecommissioning33LineTest() {
    OperationResultVoid callback = ontOltOrchestratorRobot.decommissionOnt(accessLineFor33LineCaseNew);

    // check callback
    assertTrue(callback.getSuccess());
    assertNull(callback.getError());
    assertNull(callback.getResponse());

    // check alri
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineFor33LineCaseNew.getLineId()).isEmpty(), true);
    assertEquals(accessLineRiRobot.getLineIdPool(port).stream().filter(lineIdDto ->
            lineIdDto.getLineId().equals(accessLineFor33LineCaseNew.getLineId())
                    && lineIdDto.getStatus().equals(LineIdStatus.FREE)).collect(Collectors.toList()).size(), 1);
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLineFor33LineCaseOld.getLineId()),
            AccessLineStatus.ASSIGNED);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineFor33LineCaseOld.getLineId()).get(0).getHomeId(),
            accessLineFor33LineCaseOld.getHomeId());
  }

  @Test
  @TmsLink("DIGIHUB-63811")
  @Description("ONT Change, newSerialNumber = DEFAULT (Anbieterwechsel)")
  public void ontDefaultChangeTest() {
    assertNotNull(accessLineRiRobot.getSubscriberNEProfile(accessLineAnbieterwechsel.getLineId()).getOntSerialNumber());
    assertEquals(accessLineRiRobot.getSubscriberNEProfile(accessLineAnbieterwechsel.getLineId()).getOntState(), OntState.ONLINE);

    OperationResultLineIdSerialNumberDto callback = ontOltOrchestratorRobot.changeOntSerialNumber(accessLineAnbieterwechsel, "DEFAULT");

    // check callback
    assertNull(callback.getError());
    assertTrue(callback.getSuccess());
    assertEquals(accessLineAnbieterwechsel.getLineId(), callback.getResponse().getLineId());
    assertEquals("DEFAULT", callback.getResponse().getSerialNumber());

    // check alri
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLineAnbieterwechsel.getLineId()), AccessLineStatus.ASSIGNED);
    assertEquals(accessLineRiRobot.getSubscriberNEProfile(accessLineAnbieterwechsel.getLineId()).getState(), ProfileState.INACTIVE);
    assertNotNull(accessLineRiRobot.getSubscriberNEProfile(accessLineAnbieterwechsel.getLineId()).getOntSerialNumber());
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineAnbieterwechsel.getLineId()).get(0).getDefaultNeProfile().getState(),
            ProfileState.ACTIVE);
    assertEquals(accessLineRiRobot.getSubscriberNEProfile(accessLineAnbieterwechsel.getLineId()).getOntState(), OntState.UNKNOWN);
    assertEquals(accessLineRiRobot.getSubscriberNLProfile(accessLineAnbieterwechsel.getLineId()).getState(), ProfileState.ACTIVE);
  }

  @Test
  @TmsLink("DIGIHUB-53284")
  @Description("Get ONT State by LineID")
  public void getOntStateByLineIdTest() {
    accessLine.setLineId(accessLineRiRobot.getFtthAccessLinesWithOnt(port).get(0).getLineId());
    OntStateDto ontState = ontOltOrchestratorRobot.getOntState(accessLine);
    assertEquals(accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()).getOntState().toString(), ontState.getOntState());
    assertEquals(accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()).getOntSerialNumber(), ontState.getSerialNumber());
  }

  @Test
  @TmsLink("DIGIHUB-109220")
  @Description("Ont Detection")
  public void getOntInformationTest() {
    String ontSerialNumber = accessLineRiRobot.getFtthAccessLinesWithOnt(port).get(0).getDefaultNeProfile().getSubscriberNeProfile().getOntSerialNumber();
    OperationResultEmsEventDto operationResultEmsEventCallback = ontOltOrchestratorRobot.getEmsEvents(new OntConnectivityInfoDto()
            .endSz(accessLineForCommissioning.getOltDevice().getEndsz())
            .serialNumber(ontSerialNumber)
            .timestamp(OffsetDateTime.now()));
    assertTrue(operationResultEmsEventCallback.getSuccess());
    assertNull(operationResultEmsEventCallback.getError());
    assertEquals(operationResultEmsEventCallback.getResponse().getSerialNumber(), ontSerialNumber);
    assertEquals(operationResultEmsEventCallback.getResponse().getEventMessage(), "LastEvent");
    assertNotNull((operationResultEmsEventCallback.getResponse().getTimestamp()));
  }

  @Test
  @TmsLink("DIGIHUB-123854")
  @Description("OntPonDetection for an OLT_BNG AccessLine: no NSP in SEAL, A4 returns Events, Slot + Port do not coincide with initial data")
  public void ontPonDetectNoNspInSealTest() {
    String ontSerialNumber = accessLineRiRobot.getFtthAccessLinesWithOnt(port).get(0).getDefaultNeProfile().getSubscriberNeProfile().getOntSerialNumber();
    mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "OLT_BNGVerschaltungsfehler"))
            .addSealNoEmsEventsMock()
            .build()
            .publish()
            .publishedHook(savePublishedToDefaultDir())
            .publishedHook(attachStubsToAllureReport());

    OperationResultEmsEventDto operationResultEmsEventCallback = ontOltOrchestratorRobot.getEmsEvents(new OntConnectivityInfoDto()
            .endSz(accessLineForCommissioning.getOltDevice().getEndsz())
            .serialNumber(ontSerialNumber)
            .timestamp(OffsetDateTime.now()));

    mappingsContext.deleteStubs();

    // check callback
    assertTrue(operationResultEmsEventCallback.getSuccess());
    assertNull(operationResultEmsEventCallback.getError());
    assertEquals(operationResultEmsEventCallback.getResponse().getEndSz(), portDetectedInA4.getEndSz());
    assertNull(operationResultEmsEventCallback.getResponse().getSlotNumber());
    assertEquals(operationResultEmsEventCallback.getResponse().getPortNumber(), portDetectedInA4.getPortNumber());
    assertEquals(operationResultEmsEventCallback.getResponse().getSerialNumber(), ontSerialNumber);
    assertNull(operationResultEmsEventCallback.getResponse().getTimestamp());
    assertNull(operationResultEmsEventCallback.getResponse().getOnuId());
    assertNull(operationResultEmsEventCallback.getResponse().getEventMessage());

  }

  @Test
  @TmsLink("DIGIHUB-123850")
  @Description("OntPonDetection for an OLT_BNG AccessLine: no NSP in SEAL, no NSP in A4RI")
  public void ontPonDetectNoNspInSealAndA4Test() {
    String ontSerialNumber = accessLineRiRobot.getFtthAccessLinesWithOnt(port).get(0).getDefaultNeProfile().getSubscriberNeProfile().getOntSerialNumber();
    mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "Verschaltungsfehler"))
            .addSealNoEmsEventsMock()
            .addA4NspBySnMockEmpty()
            .build()
            .publish()
            .publishedHook(savePublishedToDefaultDir())
            .publishedHook(attachStubsToAllureReport());

    OperationResultEmsEventDto operationResultEmsEventCallback = ontOltOrchestratorRobot.getEmsEvents(new OntConnectivityInfoDto()
            .endSz(accessLineForCommissioning.getOltDevice().getEndsz())
            .serialNumber(ontSerialNumber)
            .timestamp(OffsetDateTime.now()));

    mappingsContext.deleteStubs();

    // check callback
    assertTrue(operationResultEmsEventCallback.getSuccess());
    assertNull(operationResultEmsEventCallback.getError());
  }


  @Test
  @TmsLink("DIGIHUB-116324")
  @Description("Get attenuation measurement for an FTTH AccessLine")
  public void ontAttenuationMeasurementTest() {
    accessLine.setLineId(accessLineRiRobot.getFtthAccessLinesWithOnt(port).get(0).getLineId());
    AttenuationMeasurementsDto attenuationMeasurementsCallback = ontOltOrchestratorRobot.getOntAttenuationMeasurement(accessLine.getLineId());
    assertTrue(attenuationMeasurementsCallback.getSuccess());
    assertNull(attenuationMeasurementsCallback.getError());
    assertEquals(attenuationMeasurementsCallback.getResponse().getOntState(), EmsMeasurementsDto.OntStateEnum.ONLINE);
    assertNotNull(attenuationMeasurementsCallback.getResponse().getReceivedPowerAtOltFromRequestedOnu());
    assertNotNull(attenuationMeasurementsCallback.getResponse().getReceivedPowerOnu());
    assertNotNull(attenuationMeasurementsCallback.getResponse().getTransmittedPowerAtOltToAllOnus());
    assertNotNull(attenuationMeasurementsCallback.getResponse().getTransmittedPowerOnu());
  }
}