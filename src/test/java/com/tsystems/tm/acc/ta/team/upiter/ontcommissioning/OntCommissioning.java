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
import com.tsystems.tm.acc.ta.robot.osr.WgAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_35_0.client.model.OntState;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_35_0.client.model.*;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.*;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.List;
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
@Epic("ONT Processes OLT_BNG FTTH")
public class OntCommissioning extends GigabitTest {

  private AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
  private OntOltOrchestratorRobot ontOltOrchestratorRobot = new OntOltOrchestratorRobot();
  private WgAccessProvisioningRobot wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
  private HomeIdManagementRobot homeIdManagementRobot = new HomeIdManagementRobot();
  private AccessLine accessLineForCommissioning;
  private AccessLine accessLine;
  private AccessLine accessLineFor33LineCaseNew;
  private AccessLine accessLineFor33LineCaseOld;
//  private BusinessInformation postprovisioningStart;
//  private BusinessInformation postprovisioningEnd;
  private Ont ontSerialNumber;
  private PortProvisioning portDetectedInA4;
  private PortProvisioning port;
  private UpiterTestContext context = UpiterTestContext.get();
  private WireMockMappingsContext mappingsContext;

  @BeforeClass
  public void init() throws InterruptedException {
    accessLineRiRobot.clearDatabase();
    Thread.sleep(1000);
    accessLineRiRobot.fillDatabaseForOltCommissioningV2(1, 1);
    port = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.Port);
    portDetectedInA4 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.PortDetectedInA4);
    accessLine = new AccessLine();
    accessLineForCommissioning = context.getData().getAccessLineDataProvider().get(AccessLineCase.OntRegistrationAccessLine);
    accessLineFor33LineCaseNew = new AccessLine();
    accessLineFor33LineCaseOld = new AccessLine();
    ontSerialNumber = context.getData().getOntDataProvider().get(OntCase.OntSerialNumber);
//    postprovisioningStart = context.getData().getBusinessInformationDataProvider().get(BusinessInformationCase.PostprovisioningStartEvent);
//    postprovisioningEnd = context.getData().getBusinessInformationDataProvider().get(BusinessInformationCase.PostprovisioningEndEvent);
  }

  @Test
  @TmsLink("DIGIHUB-71918")
  @Description("ONT Access Line Reservation by HomeID")
  public void accessLineReservationByPortAndHomeIdTest() {
    accessLineForCommissioning.setHomeId(homeIdManagementRobot.generateHomeid().getHomeId());
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
    OperationResultLineIdSerialNumberDto callback = ontOltOrchestratorRobot.changeOntSerialNumber(accessLineForCommissioning.getLineId(), ontSerialNumber.getNewSerialNumber());

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
    accessLine.setLineId(accessLineRiRobot.getAccessLinesByTypeV2(AccessLineProductionPlatform.OLT_BNG,
            AccessLineTechnology.GPON, AccessLineStatus.ASSIGNED, ProfileState.ACTIVE, null).get(0).getLineId());
    accessLine.setHomeId(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getHomeId());

    OperationResultVoid callback = ontOltOrchestratorRobot.decommissionOntWithRollback(accessLine, true);

    // check callback
    assertTrue(callback.getSuccess());
    assertNull(callback.getError());
    assertNull(callback.getResponse());

    // check alri
    assertNull(accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()));
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId()),
            AccessLineStatus.ASSIGNED);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getHomeId(),
            accessLine.getHomeId());
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getDefaultNeProfile().getState(),
            ProfileState.ACTIVE);
  }

  @Test
  @TmsLink("DIGIHUB-38182")
  @Description("ONT Decommissioning, rollback to reservation = false")
  public void ontDecommissioningWithRollbackFalseTest() {
    accessLine.setLineId(accessLineRiRobot.getAccessLinesByTypeV2(AccessLineProductionPlatform.OLT_BNG,
            AccessLineTechnology.GPON, AccessLineStatus.ASSIGNED, ProfileState.ACTIVE, null).get(0).getLineId());
    accessLine.setHomeId(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getHomeId());

    OperationResultVoid callback =
            ontOltOrchestratorRobot.decommissionOntWithRollback(accessLine, false);

    // check callback
    assertTrue(callback.getSuccess());
    assertNull(callback.getError());
    assertNull(callback.getResponse());

    // check alri
    assertNull(accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()));
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId()),
            AccessLineStatus.WALLED_GARDEN);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getHomeId(),
            accessLine.getHomeId());
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getDefaultNeProfile().getState(),
            ProfileState.ACTIVE);
  }

  @Test()
  @TmsLink("DIGIHUB-136262")
  @Description("Deprovisioning of the 33d AccessLine after termination, feature toggle is off ")
  @Owner("DL_T-Magic.U-Piter@t-systems.com")
  public void ontDecommissioning33LineToggleOffTest() throws InterruptedException {
//    wgAccessProvisioningRobot.changeFeatureToogleEnable64PonSplittingState(false);

    Thread.sleep(3000);

    // prepare test data
    List<AccessLineDto> accessLines = accessLineRiRobot.getAccessLinesByPort(port).stream()
            .filter(accessLineDto -> accessLineDto.getStatus().equals(AccessLineStatus.ASSIGNED)
                    &&accessLineDto.getHomeId()!=null).collect(Collectors.toList());
    accessLineFor33LineCaseOld.setLineId(accessLines.get(0).getLineId());
    accessLineFor33LineCaseOld.setHomeId(accessLines.get(0).getHomeId());
    accessLineFor33LineCaseNew.setLineId(accessLines.stream().filter(accessLine -> !accessLine.getLineId().equals(accessLineFor33LineCaseOld.getLineId()))
            .collect(Collectors.toList()).get(0).getLineId());
    accessLineFor33LineCaseNew.setHomeId(accessLineFor33LineCaseOld.getHomeId());
    ontOltOrchestratorRobot.updateOntState(accessLineFor33LineCaseNew);

    // test
    OperationResultVoid callback = ontOltOrchestratorRobot.decommissionOnt(accessLineFor33LineCaseNew);

    // check callback
    assertTrue(callback.getSuccess());
    assertNull(callback.getError());
    assertNull(callback.getResponse());

    // check alri
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLineFor33LineCaseOld.getLineId()),
            AccessLineStatus.ASSIGNED);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineFor33LineCaseOld.getLineId()).get(0).getHomeId(),
            accessLineFor33LineCaseOld.getHomeId());
    Thread.sleep(2000);

    assertEquals(true, accessLineRiRobot.getAccessLinesByLineId(accessLineFor33LineCaseNew.getLineId()).isEmpty());
  }

  @Test()
  @TmsLink("DIGIHUB-136259")
  @Description("Deprovisioning of the 33d AccessLine after termination, feature toggle is on ")
  @Owner("DL_T-Magic.U-Piter@t-systems.com")
  public void ontDecommissioning33LineToggleOnTest() throws InterruptedException {
    wgAccessProvisioningRobot.changeFeatureToogleEnable64PonSplittingState(true);

    Thread.sleep(3000);

    // prepare test data
    List<AccessLineDto> accessLines = accessLineRiRobot.getAccessLinesByPort(port).stream()
            .filter(accessLineDto -> accessLineDto.getStatus().equals(AccessLineStatus.ASSIGNED)
                    &&accessLineDto.getHomeId()!=null).collect(Collectors.toList());
    accessLineFor33LineCaseOld.setLineId(accessLines.get(0).getLineId());
    accessLineFor33LineCaseOld.setHomeId(accessLines.get(0).getHomeId());
    accessLineFor33LineCaseNew.setLineId(accessLines.stream().filter(accessLine -> !accessLine.getLineId().equals(accessLineFor33LineCaseOld.getLineId()))
            .collect(Collectors.toList()).get(0).getLineId());
    accessLineFor33LineCaseNew.setHomeId(accessLineFor33LineCaseOld.getHomeId());
    ontOltOrchestratorRobot.updateOntState(accessLineFor33LineCaseNew);

    // test
    OperationResultVoid callback = ontOltOrchestratorRobot.decommissionOnt(accessLineFor33LineCaseNew);

    // check callback
    assertTrue(callback.getSuccess());
    assertNull(callback.getError());
    assertNull(callback.getResponse());

    // check alri
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineFor33LineCaseNew.getLineId()).size(), 1);
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLineFor33LineCaseNew.getLineId()),
            AccessLineStatus.WALLED_GARDEN);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineFor33LineCaseNew.getLineId()).get(0).getHomeId(), accessLineFor33LineCaseNew.getHomeId());
    assertNull(accessLineRiRobot.getAccessLinesByLineId(accessLineFor33LineCaseNew.getLineId()).get(0).getDefaultNeProfile().getSubscriberNeProfile());

    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLineFor33LineCaseOld.getLineId()),
            AccessLineStatus.ASSIGNED);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineFor33LineCaseOld.getLineId()).get(0).getHomeId(),
            accessLineFor33LineCaseOld.getHomeId());
  }

  @Test
  @TmsLink("DIGIHUB-63811")
  @Description("ONT Change, newSerialNumber = DEFAULT (Anbieterwechsel)")
  public void anbieterwechselTest() {

    accessLine.setLineId(accessLineRiRobot.getAccessLinesByTypeV2(AccessLineProductionPlatform.OLT_BNG,
            AccessLineTechnology.GPON, AccessLineStatus.ASSIGNED, ProfileState.ACTIVE, ProfileState.ACTIVE).get(0).getLineId());

    assertNotNull(accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()).getOntSerialNumber());
    assertEquals(accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()).getOntState(), OntState.ONLINE);

    OperationResultLineIdSerialNumberDto callback = ontOltOrchestratorRobot.changeOntSerialNumber(accessLine.getLineId(), "DEFAULT");

    // check callback
    assertNull(callback.getError());
    assertTrue(callback.getSuccess());
    assertEquals(accessLine.getLineId(), callback.getResponse().getLineId());
    assertEquals("DEFAULT", callback.getResponse().getSerialNumber());

    // check alri
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId()), AccessLineStatus.ASSIGNED);
    assertEquals(accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()).getState(), ProfileState.INACTIVE);
    assertNotNull(accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()).getOntSerialNumber());
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getDefaultNeProfile().getState(),
            ProfileState.ACTIVE);
    assertEquals(accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()).getOntState(), OntState.UNKNOWN);
    assertEquals(accessLineRiRobot.getSubscriberNLProfile(accessLine.getLineId()).getState(), ProfileState.ACTIVE);
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