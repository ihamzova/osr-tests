package com.tsystems.tm.acc.ta.team.upiter.ontcommissioning;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.data.upiter.models.businessinformation.BusinessInformationCase;
import com.tsystems.tm.acc.data.upiter.models.ont.OntCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.BusinessInformation;
import com.tsystems.tm.acc.ta.data.osr.models.Ont;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.OntOltOrchestratorRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_19_0.client.model.*;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.OntConnectivityInfoDto;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.OntStateDto;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.OperationResultEmsEventDto;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.PortAndHomeIdDto;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
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
public class OntCommissioning extends GigabitTest {

  private AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
  private OntOltOrchestratorRobot ontOltOrchestratorRobot = new OntOltOrchestratorRobot();
  private AccessLine accessLine;
  private AccessLine accessLineForOntState;
  private AccessLine accessLineAnbieterwechsel;
  private AccessLine accessLineForDeprovisioningNew;
  private AccessLine accessLineForDeprovisioningOld;
  private AccessLine accessLineForDeprovisioningTrue;
  private AccessLine accessLineForDeprovisioningFalse;
  private BusinessInformation postprovisioningStart;
  private BusinessInformation postprovisioningEnd;
  private AccessLine accessLineForPonDetection;
  private Ont ontSerialNumber;
  private Ont defaultSerialNumber;
  private Ont ontForPonDetection;
  private PortProvisioning port;
  private UpiterTestContext context = UpiterTestContext.get();

  @BeforeClass
  public void init() throws InterruptedException {
    accessLineRiRobot.clearDatabase();
    Thread.sleep(1000);
    accessLineRiRobot.fillDatabaseForOltCommissioning();
    port = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.PortFor33LineCase);
    accessLineForDeprovisioningNew = context.getData().getAccessLineDataProvider().get(AccessLineCase.ForDeprovisioningOntRegistrationAccessLine1);
    accessLineForDeprovisioningOld = context.getData().getAccessLineDataProvider().get(AccessLineCase.ForDeprovisioningOntRegistrationAccessLine2);
    accessLineForDeprovisioningTrue = context.getData().getAccessLineDataProvider().get(AccessLineCase.ForDeprovisioningTrue);
    accessLineForDeprovisioningFalse = context.getData().getAccessLineDataProvider().get(AccessLineCase.ForDeprovisioningFalse);
    accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.OntRegistrationAccessLine);
    accessLineForOntState = context.getData().getAccessLineDataProvider().get(AccessLineCase.OntGetStateAccessLine);
    accessLineAnbieterwechsel = context.getData().getAccessLineDataProvider().get(AccessLineCase.AccessLineAnbieterwechsel);
    defaultSerialNumber = context.getData().getOntDataProvider().get(OntCase.OntDefaultSerialNumber);
    postprovisioningStart = context.getData().getBusinessInformationDataProvider().get(BusinessInformationCase.PostprovisioningStartEvent);
    postprovisioningEnd = context.getData().getBusinessInformationDataProvider().get(BusinessInformationCase.PostprovisioningEndEvent);
    accessLineForPonDetection = context.getData().getAccessLineDataProvider().get(AccessLineCase.EndSzForOntDetection);
    ontForPonDetection = context.getData().getOntDataProvider().get(OntCase.SerialNumberForOntDetection);
  }

  @AfterClass
  public void clearData() {
    accessLineRiRobot.clearDatabase();
  }

  @Test
  @TmsLink("DIGIHUB-71918")
  @Description("ONT Access Line Reservation by HomeID")
  public void accessLineReservationByPortAndHomeIdTest() {
    accessLine.setHomeId(accessLineRiRobot.getHomeIdByPort(accessLine));
    PortAndHomeIdDto portAndHomeIdDto = new PortAndHomeIdDto()
            .vpSz(accessLine.getOltDevice().getVpsz())
            .fachSz(accessLine.getOltDevice().getFsz())
            .slotNumber(accessLine.getSlotNumber())
            .portNumber(accessLine.getPortNumber())
            .homeId(accessLine.getHomeId());
    String lineId = ontOltOrchestratorRobot.reserveAccessLineByPortAndHomeId(portAndHomeIdDto);
    accessLine.setLineId(lineId);
    AccessLineStatus lineIdState = accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId());
    assertEquals(AccessLineStatus.ASSIGNED, lineIdState);

/*        //Create temp List to check business data
        List<BusinessInformation> businessInformationList = new ArrayList<>();
        businessInformationList.add(postprovisioningStart);
        businessInformationList.add(postprovisioningEnd);

        List<BusinessInformation> businessInformationLogCollector = wgAccessProvisioningRobot.getBusinessInformation();
        Assert.assertTrue(businessInformationLogCollector.containsAll(businessInformationList), "Business information is not found");*/
  }

  @Test(dependsOnMethods = "accessLineReservationByPortAndHomeId")
  @TmsLink("DIGIHUB-47257")
  @Description("Register ONT resource")
  public void ontRegistrationTest() {
    ontSerialNumber = context.getData().getOntDataProvider().get(OntCase.OntSerialNumber);
    ontOltOrchestratorRobot.registerOnt(accessLine, ontSerialNumber);
    SubscriberNeProfileDto subscriberNEProfile = accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId());
    assertNotNull(subscriberNEProfile);
    assertEquals(subscriberNEProfile.getOntSerialNumber(), ontSerialNumber.getSerialNumber());
    assertEquals(subscriberNEProfile.getState(), ProfileState.ACTIVE);
    assertEquals(subscriberNEProfile.getOntState(), OntState.UNKNOWN);
  }

  @Test(dependsOnMethods = {"accessLineReservationByPortAndHomeId", "ontRegistration"})
  @TmsLink("DIGIHUB-33938")
  @Description("ONT Connectivity test")
  public void ontTest() {
    ontOltOrchestratorRobot.testOnt(accessLine.getLineId());
    ontOltOrchestratorRobot.updateOntState(accessLine);
    SubscriberNeProfileDto subscriberNEProfile2 = accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId());
    assertNotNull(subscriberNEProfile2);
    assertEquals(subscriberNEProfile2.getOntState(), OntState.ONLINE);
  }

  @Test(dependsOnMethods = {"accessLineReservationByPortAndHomeId", "ontRegistration", "ontTest"})
  @TmsLink("DIGIHUB-53891")
  @Description("ONT Change")
  public void ontChangeTest() {
    assertEquals(accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()).getOntSerialNumber(),
            ontSerialNumber.getSerialNumber());
    ontOltOrchestratorRobot.changeOntSerialNumber(accessLine, ontSerialNumber.getNewSerialNumber());
    assertEquals(accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()).getOntSerialNumber(),
            ontSerialNumber.getNewSerialNumber());
  }

  @Test(dependsOnMethods = {"accessLineReservationByPortAndHomeId", "ontRegistration", "ontTest", "ontChangeTest"})
  @TmsLink("DIGIHUB-53292")
  @Description("ONT Decommissioning, rollback to reservation = empty")
  public void ontDecommissioningTest() {
    ontOltOrchestratorRobot.decommissionOnt(accessLine);
    assertNull(accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()));
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId()),
            AccessLineStatus.WALLED_GARDEN);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getHomeId(),
            accessLine.getHomeId());
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getDefaultNeProfile().getState(),
            ProfileState.ACTIVE);
  }

  @Test
  @TmsLink("DIGIHUB-38181")
  @Description("ONT Decommissioning, rollback to reservation = true")
  public void ontDecommissioningWithRollbackTrueTest() {
    ontOltOrchestratorRobot.decommissionOntWithRollback(accessLineForDeprovisioningTrue, true);
    assertNull(accessLineRiRobot.getSubscriberNEProfile(accessLineForDeprovisioningTrue.getLineId()));
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLineForDeprovisioningTrue.getLineId()),
            AccessLineStatus.ASSIGNED);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineForDeprovisioningTrue.getLineId()).get(0).getHomeId(),
            accessLineForDeprovisioningTrue.getHomeId());
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineForDeprovisioningTrue.getLineId()).get(0).getDefaultNeProfile().getState(),
            ProfileState.ACTIVE);
  }

  @Test
  @TmsLink("DIGIHUB-38182")
  @Description("ONT Decommissioning, rollback to reservation = false")
  public void ontDecommissioningWithRollbackFalseTest() {
    ontOltOrchestratorRobot.decommissionOntWithRollback(accessLineForDeprovisioningFalse, false);
    assertNull(accessLineRiRobot.getSubscriberNEProfile(accessLineForDeprovisioningFalse.getLineId()));
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLineForDeprovisioningFalse.getLineId()),
            AccessLineStatus.WALLED_GARDEN);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineForDeprovisioningFalse.getLineId()).get(0).getHomeId(),
            accessLineForDeprovisioningFalse.getHomeId());
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineForDeprovisioningFalse.getLineId()).get(0).getDefaultNeProfile().getState(),
            ProfileState.ACTIVE);
  }

  @Test()
  @TmsLink("DIGIHUB-42230")
  @Description("Deprovisioning of the 33d AccessLine after termination")
  @Owner("DL_T-Magic.U-Piter@t-systems.com")
  public void ontDecommissioning33LineTest() {
    ontOltOrchestratorRobot.decommissionOnt(accessLineForDeprovisioningNew);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineForDeprovisioningNew.getLineId()).isEmpty(),
            true);
    assertEquals(accessLineRiRobot.getLineIdPool(port).stream().filter(lineIdDto ->
            lineIdDto.getLineId().equals(accessLineForDeprovisioningNew.getLineId())).filter(lineIdDto ->
            lineIdDto.getStatus().equals(LineIdStatus.FREE)).collect(Collectors.toList()).size(), 1);
    assertEquals(accessLineRiRobot.getLineIdStateByLineId(accessLineForDeprovisioningNew.getLineId()),
            LineIdStatus.FREE);
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLineForDeprovisioningOld.getLineId()),
            AccessLineStatus.ASSIGNED);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineForDeprovisioningOld.getLineId()).get(0).getHomeId(),
            accessLineForDeprovisioningOld.getHomeId());
  }

  @Test
  @TmsLink("DIGIHUB-63811")
  @Description("ONT Change,newSerialNumber = DEFAULT")
  public void ontDefaultChangeTest() {
    assertNotNull(accessLineRiRobot.getSubscriberNEProfile(accessLineAnbieterwechsel.getLineId()).getOntSerialNumber());
    assertEquals(accessLineRiRobot.getSubscriberNEProfile(accessLineAnbieterwechsel.getLineId()).getOntState(), OntState.ONLINE);
    ontOltOrchestratorRobot.changeOntSerialNumber(accessLineAnbieterwechsel, defaultSerialNumber.getNewSerialNumber());
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
    OntStateDto ontState = ontOltOrchestratorRobot.getOntState(accessLineForOntState);
    assertEquals(accessLineRiRobot.getSubscriberNEProfile(accessLineForOntState.getLineId()).getOntState().toString(), ontState.getOntState());
    assertEquals(accessLineRiRobot.getSubscriberNEProfile(accessLineForOntState.getLineId()).getOntSerialNumber(), ontState.getSerialNumber());
  }

  @Test
  @TmsLink("DIGIHUB-109220")
  @Description("Ont Detection")
  public void getOntInformationTest() {
    OperationResultEmsEventDto OperationResultEmsEventCallback = ontOltOrchestratorRobot.getEmsEvents( new OntConnectivityInfoDto()
            .endSz(accessLineForPonDetection.getEndSz())
            .serialNumber(ontForPonDetection.getSerialNumber())
            .timestamp(OffsetDateTime.now()));
    assertTrue(OperationResultEmsEventCallback.getSuccess());
    assertNull(OperationResultEmsEventCallback.getError());
    assertEquals(OperationResultEmsEventCallback.getResponse().getSerialNumber(), ontForPonDetection.getSerialNumber());
    assertEquals(OperationResultEmsEventCallback.getResponse().getEventMessage(), "LastEvent");
    assertNotNull((OperationResultEmsEventCallback.getResponse().getTimestamp()));
  }
}