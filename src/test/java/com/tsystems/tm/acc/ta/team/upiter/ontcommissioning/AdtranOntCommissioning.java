package com.tsystems.tm.acc.ta.team.upiter.ontcommissioning;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.data.upiter.models.ont.OntCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.Ont;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.HomeIdManagementRobot;
import com.tsystems.tm.acc.ta.robot.osr.OntOltOrchestratorRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_35_0.client.model.AccessLineStatus;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_35_0.client.model.OntState;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_35_0.client.model.ProfileState;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_35_0.client.model.SubscriberNeProfileDto;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.*;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

@ServiceLog({
        ONT_OLT_ORCHESTRATOR_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        DECOUPLING_MS,
        APIGW_MS
})
@Epic("ONT Processes on Adtran")
public class AdtranOntCommissioning extends GigabitTest {

  private final AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
  private final OntOltOrchestratorRobot ontOltOrchestratorRobot = new OntOltOrchestratorRobot();
  private final HomeIdManagementRobot homeIdManagementRobot = new HomeIdManagementRobot();
  private final UpiterTestContext context = UpiterTestContext.get();
  private AccessLine accessLine;
  private Ont ontSerialNumber;

  @BeforeClass
  public void loadContext() throws InterruptedException {
    accessLineRiRobot.clearDatabase();
    Thread.sleep(1000);
    accessLineRiRobot.fillDatabaseForAdtranOltCommissioning();
    accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.adtranOntAccessLine);
    ontSerialNumber = context.getData().getOntDataProvider().get(OntCase.adtranOntSerialNumber);
  }

  @Test
  @TmsLink("DIGIHUB-91170")
  @Description("Adtran Reservation ONT resource")
  public void adtranOntReservation() {
    //Get 1 Free HomeId from pool
    accessLine.setHomeId(homeIdManagementRobot.generateHomeid().getHomeId());

    //Start access line registration
    PortAndHomeIdDto portAndHomeIdDto = new PortAndHomeIdDto()
            .vpSz(accessLine.getOltDevice().getVpsz())
            .fachSz(accessLine.getOltDevice().getFsz())
            .portNumber(accessLine.getPortNumber())
            .homeId(accessLine.getHomeId());
    OperationResultLineIdDto callback = ontOltOrchestratorRobot.reserveAccessLineByPortAndHomeId(portAndHomeIdDto);

    // check callback
    assertNull("Callback returned an error", callback.getError());
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertNotNull(callback.getResponse().getLineId(), "Callback didn't return a LineId");
    assertEquals("HomeId returned in the callback is incorrect", callback.getResponse().getHomeId(), accessLine.getHomeId());

    // check alri
    accessLine.setLineId(callback.getResponse().getLineId());
    assertEquals("AccessLine state is incorrect", AccessLineStatus.ASSIGNED, accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId()));
  }

  @Test(dependsOnMethods = "adtranOntReservation")
  @TmsLink("DIGIHUB-91173")
  @Description("Adtran Registeration ONT resource")
  public void adtranOntRegistration() {
    OperationResultLineIdSerialNumberDto callback = ontOltOrchestratorRobot.registerOnt(accessLine, ontSerialNumber);

    // check callback
    assertNull("Callback returned an error", callback.getError());
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertEquals("LineId returned in the callback is incorrect", accessLine.getLineId(), callback.getResponse().getLineId());
    assertEquals("ONT S/N returned in the callback is incorrect", ontSerialNumber.getSerialNumber(), callback.getResponse().getSerialNumber());

    // check alri
    SubscriberNeProfileDto subscriberNEProfile = accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId());
    assertNotNull(subscriberNEProfile, "");
    assertEquals("ONT S/N on the AccessLine is incorrect", subscriberNEProfile.getOntSerialNumber(), ontSerialNumber.getSerialNumber());
    assertEquals("SubscriberNeProfile state is incorrect", subscriberNEProfile.getState(), ProfileState.ACTIVE);
    assertEquals("SubscriberNeProfile ONT state is incorrect", subscriberNEProfile.getOntState(), OntState.UNKNOWN);
  }

  @Test(dependsOnMethods = {"adtranOntReservation", "adtranOntRegistration"})
  @TmsLink("DIGIHUB-91174")
  @Description("Adtran ONT Connectivity test")
  public void adtranOntTest() {
    //test Ont
    OperationResultOntTestDto callback = ontOltOrchestratorRobot.testOnt(accessLine.getLineId());

    // check callback
    assertNull("Callback returned an error", callback.getError());
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertNotNull(callback.getResponse().getLastDownCause(), "Callback didn't return LastDownCause");
    assertNotNull(callback.getResponse().getLastDownTime(), "Callback didn't return LastDownTime");
    assertNotNull(callback.getResponse().getLastUpTime(), "Callback didn't return LastUpTime");
    assertEquals("ActualRunState returned in the callback is incorrect",
            com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.OntState.ONLINE, callback.getResponse().getActualRunState());
    //todo add check for onuid

    ontOltOrchestratorRobot.updateOntState(accessLine);

    // check alri
    SubscriberNeProfileDto subscriberNEProfile = accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId());
    assertNotNull(subscriberNEProfile, "SubscriberNeProfile is not present");
    assertEquals("Ont State on the AccessLine is incorrect", subscriberNEProfile.getOntState(), OntState.ONLINE);
  }

  @Test(dependsOnMethods = {"adtranOntReservation", "adtranOntRegistration", "adtranOntTest"})
  @TmsLink("DIGIHUB-91178")
  @Description("Adtran Change ONT serial number")
  public void adtranOntChange() {
    //check SN
    assertEquals("ONT S/N on the AccessLine is incorrect",
            ontSerialNumber.getSerialNumber(),
            accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()).getOntSerialNumber());
    OperationResultLineIdSerialNumberDto callback = ontOltOrchestratorRobot.changeOntSerialNumber(accessLine.getLineId(), ontSerialNumber.getNewSerialNumber());

    // check callback
    assertNull("Callback returned an error", callback.getError());
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertEquals("LineId returned in the callback is incorrect", accessLine.getLineId(), callback.getResponse().getLineId());
    assertEquals("ONT S/N returned in the callback is incorrect", ontSerialNumber.getNewSerialNumber(), callback.getResponse().getSerialNumber());

    // check alri
    assertEquals("New ONT S/N on the AccessLine is incorrect",
            ontSerialNumber.getNewSerialNumber(), accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()).getOntSerialNumber());

  }

  @Test(dependsOnMethods = {"adtranOntReservation", "adtranOntRegistration", "adtranOntTest", "adtranOntChange"})
  @TmsLink("DIGIHUB-91179")
  @Description("Adtran ONT Termination rollback to reservation = false")
  public void adtranOntTermination() {
    OperationResultVoid callback = ontOltOrchestratorRobot.decommissionOnt(accessLine);

    // check callback
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertNull("Callback returned an error", callback.getError());
    assertNull("Callback returned a response body", callback.getResponse());

    // check alri
    assertEquals("AccessLine state in incorrect", accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId()), AccessLineStatus.WALLED_GARDEN);
    assertEquals("HomeId on the AccessLine is incorrect", accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getHomeId(),
            accessLine.getHomeId());
    assertEquals("DefaultNeProfile state is incorrect", accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getDefaultNeProfile().getState(),
            ProfileState.ACTIVE);
    SubscriberNeProfileDto subscriberNEProfile = accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId());
    assertNull("SubscriberNeProfile was not deleted", subscriberNEProfile);
  }
}
