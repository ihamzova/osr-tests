package com.tsystems.tm.acc.ta.team.upiter.ontcommissioning;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.data.upiter.models.ont.OntCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.Ont;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.helpers.upiter.MapperFunctions;
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
    accessLineRiRobot.clearDatabaseByOlt("49/89/8000/76H2");
    accessLineRiRobot.clearDatabaseByOlt("49/911/1100/76H1");
    accessLineRiRobot.clearDatabaseByOlt("49/911/1100/76H3");
    accessLineRiRobot.clearDatabaseByOlt("49/911/1100/76H5");
    accessLineRiRobot.clearDatabaseByOlt("49/30/179/76H1");
    accessLineRiRobot.clearDatabaseByOlt("49/30/179/76G3");
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
    assertNull("Callback returned an error", callback.getError());
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertNotNull(callback.getResponse().getLineId(), "Callback didn't return a LineId");
    assertEquals("HomeId returned in the callbac is incorrect",
            accessLineForCommissioning.getHomeId(), callback.getResponse().getHomeId());

    // check alri
    accessLineForCommissioning.setLineId(callback.getResponse().getLineId());
    assertEquals("", AccessLineStatus.ASSIGNED, accessLineRiRobot.getAccessLineStateByLineId(accessLineForCommissioning.getLineId()));

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
    assertNull("Callback returned an error", callback.getError());
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertEquals("LineId returned in the callback is incorrect", accessLineForCommissioning.getLineId(), callback.getResponse().getLineId());
    assertEquals("ONT S/N returned in the callback is incorrect", ontSerialNumber.getSerialNumber(), callback.getResponse().getSerialNumber());

    // check alri
    SubscriberNeProfileDto subscriberNEProfile = accessLineRiRobot.getSubscriberNEProfile(accessLineForCommissioning.getLineId());
    assertNotNull(subscriberNEProfile, "");
    assertEquals("ONT S/N on the AccessLine is incorrect", subscriberNEProfile.getOntSerialNumber(), ontSerialNumber.getSerialNumber());
    assertEquals("SubscriberNProfile state is incorrect", subscriberNEProfile.getState(), ProfileState.ACTIVE);
    assertEquals("SubscriberNProfile ONT state is incorrect", subscriberNEProfile.getOntState(), OntState.UNKNOWN);
  }

  @Test(dependsOnMethods = {"accessLineReservationByPortAndHomeIdTest", "ontRegistrationTest"})
  @TmsLink("DIGIHUB-33938")
  @Description("ONT Connectivity test")
  public void ontTest() {
    OperationResultOntTestDto callback = ontOltOrchestratorRobot.testOnt(accessLineForCommissioning.getLineId());

    // check callback
    assertNull("Callback returned an error", callback.getError());
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertNotNull(callback.getResponse().getLastDownCause(), "Callback didn't return LastDownCause");
    assertNotNull(callback.getResponse().getLastDownTime(), "Callback didn't return LastDownTime");
    assertNotNull(callback.getResponse().getLastUpTime(), "Callback didn't return LastUpTime");
    assertEquals("ActualRunState returned in the callback is incorrect",
            com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.OntState.ONLINE, callback.getResponse().getActualRunState());
    //todo add check for onuid

    ontOltOrchestratorRobot.updateOntState(accessLineForCommissioning);

    // check alri
    SubscriberNeProfileDto subscriberNEProfile = accessLineRiRobot.getSubscriberNEProfile(accessLineForCommissioning.getLineId());
    assertNotNull(subscriberNEProfile, "SubscriberNeProfile is not present on the AccessLine");
    assertEquals("SubscriberNeProfile ONT State is incorrect", subscriberNEProfile.getOntState(), OntState.ONLINE);
  }

  @Test(dependsOnMethods = {"accessLineReservationByPortAndHomeIdTest", "ontRegistrationTest", "ontTest"})
  @TmsLink("DIGIHUB-53891")
  @Description("ONT Change")
  public void ontChangeTest() {
    assertEquals("ONT S/N on the AccessLine is incorrect", accessLineRiRobot.getSubscriberNEProfile(accessLineForCommissioning.getLineId()).getOntSerialNumber(),
            ontSerialNumber.getSerialNumber());
    OperationResultLineIdSerialNumberDto callback = ontOltOrchestratorRobot.changeOntSerialNumber(accessLineForCommissioning.getLineId(), ontSerialNumber.getNewSerialNumber());

    // check callback
    assertNull("Callback returned an error", callback.getError());
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertEquals("LineId returned in the callback is incorrect", accessLineForCommissioning.getLineId(), callback.getResponse().getLineId());
    assertEquals("ONT S/N returned in the callback is incorrect", ontSerialNumber.getNewSerialNumber(), callback.getResponse().getSerialNumber());

    // check alri
    assertEquals("NewSerialNumber on the AccessLine is incorrect",
            accessLineRiRobot.getSubscriberNEProfile(accessLineForCommissioning.getLineId()).getOntSerialNumber(),
            ontSerialNumber.getNewSerialNumber());
  }

  @Test(dependsOnMethods = {"accessLineReservationByPortAndHomeIdTest", "ontRegistrationTest", "ontTest", "ontChangeTest"})
  @TmsLink("DIGIHUB-53292")
  @Description("ONT Decommissioning, rollback to reservation = empty")
  public void ontDecommissioningTest() {
    OperationResultVoid callback = ontOltOrchestratorRobot.decommissionOnt(accessLineForCommissioning);

    // check callback
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertNull("Callback returned an error", callback.getError());
    assertNull("Callback returned a response body", callback.getResponse());

    // check alri
    assertNull("SubscriberNeProfile wansn't deleted", accessLineRiRobot.getSubscriberNEProfile(accessLineForCommissioning.getLineId()));
    assertEquals("AccessLine state is incorrect", accessLineRiRobot.getAccessLineStateByLineId(accessLineForCommissioning.getLineId()),
            AccessLineStatus.WALLED_GARDEN);
    assertEquals("HomeId on the AccessLine is incorrect", accessLineRiRobot.getAccessLinesByLineId(accessLineForCommissioning.getLineId()).get(0).getHomeId(),
            accessLineForCommissioning.getHomeId());
    assertEquals("DefaultNeProfile state is incorrect",
            accessLineRiRobot.getAccessLinesByLineId(accessLineForCommissioning.getLineId()).get(0).getDefaultNeProfile().getState(),
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
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertNull("Callback returned an error", callback.getError());
    assertNull("Callback returned a response body", callback.getResponse());

    // check alri
    assertNull("SubscriberNeProfile wasn't deleted", accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()));
    assertEquals("AccessLine state is incorrect", accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId()),
            AccessLineStatus.ASSIGNED);
    assertEquals("HomeId on the AccessLine is incorrect", accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getHomeId(),
            accessLine.getHomeId());
    assertEquals("DefaultNeProfile state is incorrect", accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getDefaultNeProfile().getState(),
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
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertNull("Callback returned an error", callback.getError());
    assertNull("Callback returned a response body", callback.getResponse());

    // check alri
    assertNull("SubscriberNeProfile wasn't deleted", accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()));
    assertEquals("AccessLine state is incorrect", accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId()),
            AccessLineStatus.WALLED_GARDEN);
    assertEquals("HomeId on the AccessLine is incorrect", accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getHomeId(),
            accessLine.getHomeId());
    assertEquals("DefaultNeProfile state is incorrect", accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getDefaultNeProfile().getState(),
            ProfileState.ACTIVE);
  }

  @Test
  @TmsLink("DIGIHUB-136262")
  @Description("Deprovisioning of the 33d AccessLine after termination, feature toggle is off ")
  @Owner("DL_T-Magic.U-Piter@t-systems.com")
  public void ontDecommissioning33LineToggleOffTest() throws InterruptedException {
    wgAccessProvisioningRobot.changeFeatureToogleEnable64PonSplittingState(false);

   // Thread.sleep(1000);

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
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertNull("Callback returned an error", callback.getError());
    assertNull("Callback returned a response body", callback.getResponse());

    // check alri
    assertEquals("State of the AccessLine that wasn't decommissioned is incorrect", accessLineRiRobot.getAccessLineStateByLineId(accessLineFor33LineCaseOld.getLineId()),
            AccessLineStatus.ASSIGNED);
    assertEquals("HomeId on the AccessLine that wasn't decommissioned is incorrect",
            accessLineRiRobot.getAccessLinesByLineId(accessLineFor33LineCaseOld.getLineId()).get(0).getHomeId(),
            accessLineFor33LineCaseOld.getHomeId());
    Thread.sleep(5000);

    assertTrue(accessLineRiRobot.getAccessLinesByLineId(accessLineFor33LineCaseNew.getLineId()).isEmpty(), "Decommissioned AccessLine wasn't deleted");
  }

  @Test()
  @TmsLink("DIGIHUB-136259")
  @Description("Deprovisioning of the 33d AccessLine after termination, feature toggle is on ")
  @Owner("DL_T-Magic.U-Piter@t-systems.com")
  public void ontDecommissioning33LineToggleOnTest() throws InterruptedException {
    wgAccessProvisioningRobot.changeFeatureToogleEnable64PonSplittingState(true);

    Thread.sleep(5000);

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
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertNull("Callback returned an error", callback.getError());
    assertNull("Callback returned a response body", callback.getResponse());

    // check alri
    assertEquals("AccessLine that was decommissioned was deleted although it shouldn't have",
            accessLineRiRobot.getAccessLinesByLineId(accessLineFor33LineCaseNew.getLineId()).size(), 1);
    assertEquals("State of the AccessLine that was decommissioned is incorrect", accessLineRiRobot.getAccessLineStateByLineId(accessLineFor33LineCaseNew.getLineId()),
            AccessLineStatus.WALLED_GARDEN);
    assertEquals("HomeId on the AccessLine that was decommissioned is incorrect",
            accessLineRiRobot.getAccessLinesByLineId(accessLineFor33LineCaseNew.getLineId()).get(0).getHomeId(), accessLineFor33LineCaseNew.getHomeId());
    assertNull("SubscriberNeProfile wasn't deleted after decommissioning",
            accessLineRiRobot.getAccessLinesByLineId(accessLineFor33LineCaseNew.getLineId()).get(0).getDefaultNeProfile().getSubscriberNeProfile());

    assertEquals("State of the AccessLine that wasn't decommissioned is incorrect",
            accessLineRiRobot.getAccessLineStateByLineId(accessLineFor33LineCaseOld.getLineId()),
            AccessLineStatus.ASSIGNED);
    assertEquals("Home Id on the AccessLine that wasn't decommissioned is incorrect",
            accessLineRiRobot.getAccessLinesByLineId(accessLineFor33LineCaseOld.getLineId()).get(0).getHomeId(),
            accessLineFor33LineCaseOld.getHomeId());
  }

  @Test
  @TmsLink("DIGIHUB-63811")
  @Description("ONT Change, newSerialNumber = DEFAULT (Anbieterwechsel)")
  public void anbieterwechselTest() {

    accessLine.setLineId(accessLineRiRobot.getAccessLinesByTypeV2(AccessLineProductionPlatform.OLT_BNG,
            AccessLineTechnology.GPON, AccessLineStatus.ASSIGNED, ProfileState.ACTIVE, ProfileState.ACTIVE).get(0).getLineId());

    assertNotNull(accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()).getOntSerialNumber(),
            "ONT S/Т is not present");
    assertEquals("ONT State is incorrect", accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()).getOntState(), OntState.ONLINE);

    OperationResultLineIdSerialNumberDto callback = ontOltOrchestratorRobot.changeOntSerialNumber(accessLine.getLineId(), "DEFAULT");

    // check callback
    assertNull("Callback returned an error", callback.getError());
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertEquals("LineId returned in the callback is incorrect", accessLine.getLineId(), callback.getResponse().getLineId());
    assertEquals("ONT S/N returned in the callback is incorrect", "DEFAULT", callback.getResponse().getSerialNumber());

    // check alri
    assertEquals("AccessLine state is incorrect", accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId()), AccessLineStatus.ASSIGNED);
    assertEquals("SubscriberNeProfile state is incorrect", accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()).getState(), ProfileState.INACTIVE);
    assertNotNull(accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()).getOntSerialNumber(), "ONT S/N is not present");
    assertEquals("DefaultNeProfile state is incorrect",
            accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getDefaultNeProfile().getState(),
            ProfileState.ACTIVE);
    assertEquals("ONT State is incorrect", accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()).getOntState(), OntState.UNKNOWN);
    assertEquals("SubscriberNLProfile state is incorrect", accessLineRiRobot.getSubscriberNLProfile(accessLine.getLineId()).getState(), ProfileState.ACTIVE);
  }

  @Test
  @TmsLink("DIGIHUB-53284")
  @Description("Get ONT State by LineID, AccessLine has subscriber_ne_profile")
  public void getOntStateByLineIdTest() {
    accessLine.setLineId(accessLineRiRobot.getFtthAccessLinesWithOnt(port).get(0).getLineId());
    OntStateDto ontState = ontOltOrchestratorRobot.getOntState(accessLine);
    assertEquals("ONT State returned in the callback is incorrect",
            accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()).getOntState().toString(), ontState.getOntState());
    assertEquals("ONT S/N returned in the callback is incorrect",
            accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()).getOntSerialNumber(), ontState.getSerialNumber());
  }

  @Test
  @TmsLink("DIGIHUB-150111")
  @Description("Get ONT State by LineID, WG AccessLine")
  public void getOntStateWgAccessLine() {
    String lineId = accessLineRiRobot.getAccessLinesByTypeV2(AccessLineProductionPlatform.OLT_BNG,
            AccessLineTechnology.GPON,
            AccessLineStatus.WALLED_GARDEN,
            null,
            null).get(0).getLineId();
    accessLine.setLineId(lineId);
    OntStateDto ontState = ontOltOrchestratorRobot.getOntState(accessLine);
    assertEquals("ONT State returned in the callback is incorrect",
            OntState.UNKNOWN.getValue(), ontState.getOntState());
    assertNull("ONT S/N returned in the callback is incorrect", ontState.getSerialNumber());
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
    assertTrue(operationResultEmsEventCallback.getSuccess(), "Callback returned an error");
    assertNull("Callback returned an error", operationResultEmsEventCallback.getError());
    assertEquals("ONT S/N returned in the callback is incorrect", ontSerialNumber, operationResultEmsEventCallback.getResponse().getSerialNumber());
    assertEquals("EventMessage returned in the callback is incorrect", "LastEvent", operationResultEmsEventCallback.getResponse().getEventMessage());
    assertNotNull((operationResultEmsEventCallback.getResponse().getTimestamp()), "Timestamp returned in the callback is incorrect");
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
    assertTrue(operationResultEmsEventCallback.getSuccess(), "Callback returned an error");
    assertNull("Callback returned an error", operationResultEmsEventCallback.getError());
    assertEquals("EndSz returned in the callback is incorrect", operationResultEmsEventCallback.getResponse().getEndSz(), portDetectedInA4.getEndSz());
    assertNull("Callback returned EventMessage although it shouldn't have", operationResultEmsEventCallback.getResponse().getSlotNumber());
    assertEquals("PortNumber returned in the callback is incorrect", operationResultEmsEventCallback.getResponse().getPortNumber(), portDetectedInA4.getPortNumber());
    assertEquals("ONT S/N returned in the callback is incorrect", operationResultEmsEventCallback.getResponse().getSerialNumber(), ontSerialNumber);
    assertNull("Callback returned Timestamp although it shouldn't have", operationResultEmsEventCallback.getResponse().getTimestamp());
    assertNull("Callback returned OnuId although it shouldn't have", operationResultEmsEventCallback.getResponse().getOnuId());
    assertNull("Callback returned EventMessage although it shouldn't have", operationResultEmsEventCallback.getResponse().getEventMessage());
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
    assertTrue(operationResultEmsEventCallback.getSuccess(), "Callback returned an error");
    assertNull("Callback returned an error", operationResultEmsEventCallback.getError());
  }

  @Test
  @TmsLink("DIGIHUB-116324")
  @Description("Get attenuation measurement for an FTTH AccessLine")
  public void ontAttenuationMeasurementTest() {
    accessLine.setLineId(accessLineRiRobot.getFtthAccessLinesWithOnt(port).get(0).getLineId());
    AttenuationMeasurementsDto attenuationMeasurementsCallback = ontOltOrchestratorRobot.getOntAttenuationMeasurement(accessLine.getLineId());
    assertTrue(attenuationMeasurementsCallback.getSuccess(), "Callback returned an error");
    assertNull("Callback returned an error", attenuationMeasurementsCallback.getError());
    assertEquals("ONT State returned in the callback is incorrect", attenuationMeasurementsCallback.getResponse().getOntState(), EmsMeasurementsDto.OntStateEnum.ONLINE);
    assertNotNull(attenuationMeasurementsCallback.getResponse().getReceivedPowerAtOltFromRequestedOnu(),
            "Callback didn't return ReceivedPowerAtOltFromRequestedOnu");
    assertNotNull(attenuationMeasurementsCallback.getResponse().getReceivedPowerOnu(),
            "Callback didn't return ReceivedPowerOnu");
    assertNotNull(attenuationMeasurementsCallback.getResponse().getTransmittedPowerAtOltToAllOnus(),
            "Callback didn't return TransmittedPowerAtOltToAllOnus");
    assertNotNull(attenuationMeasurementsCallback.getResponse().getTransmittedPowerOnu(),
            "Callback didn't return TransmittedPowerOnu");
  }

  @Test
  @TmsLink("DIGIHUB-64806")
  @Description("Search Access Line information by criteria in OOO")
  public void getAccessLineInformation() {
    AccessLineDto accessLine = accessLineRiRobot.getAccessLinesByTypeV2(AccessLineProductionPlatform.OLT_BNG,
                    AccessLineTechnology.GPON, AccessLineStatus.ASSIGNED, ProfileState.ACTIVE, ProfileState.ACTIVE)
            .get(0);
    AccessLineInformationDto  expectedInfo = new AccessLineInformationDto();
    expectedInfo.setHomeId(accessLine.getHomeId());
    expectedInfo.setOntSerialNumber(accessLine.getDefaultNeProfile().getSubscriberNeProfile().getOntSerialNumber());
    expectedInfo.setSubscriberNeProfileState(MapperFunctions.statusMapper().apply(accessLine.getDefaultNeProfile().getSubscriberNeProfile().getState()));
    expectedInfo.setPortNumber(accessLine.getReference().getPortNumber());
    expectedInfo.setSlotNumber(accessLine.getReference().getSlotNumber());
    expectedInfo.setEndSz(accessLine.getReference().getEndSz());
    expectedInfo.setLineId(accessLine.getLineId());

    List<com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.AccessLineInformationDto> actualInfo = ontOltOrchestratorRobot.getAccessLineInformation(accessLine);
    assertTrue(actualInfo.contains(expectedInfo));
  }
}