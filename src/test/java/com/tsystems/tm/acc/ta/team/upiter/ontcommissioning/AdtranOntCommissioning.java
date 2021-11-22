package com.tsystems.tm.acc.ta.team.upiter.ontcommissioning;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.data.upiter.models.ont.OntCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.Ont;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.OntOltOrchestratorRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.AccessLineStatus;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.OntState;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.ProfileState;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.SubscriberNeProfileDto;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.*;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
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
  private final UpiterTestContext context = UpiterTestContext.get();
  private AccessLine accessLine;
  private Ont ontSerialNumber;

  @BeforeClass
  public void loadContext() throws InterruptedException {
    accessLineRiRobot.clearDatabase();
    Thread.sleep(1000);
    accessLineRiRobot.fillDatabaseForAdtranOltCommissioning();
    Thread.sleep(5000);
    accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.adtranOntAccessLine);
    ontSerialNumber = context.getData().getOntDataProvider().get(OntCase.adtranOntSerialNumber);
  }

  @AfterClass
  public void clearData() {
    accessLineRiRobot.clearDatabase();
  }

  @Test
  @TmsLink("DIGIHUB-91170")
  @Description("Adtran Reservation ONT resource")
  public void adtranOntReservation() {
    //Get 1 Free HomeId from pool
    accessLine.setHomeId(accessLineRiRobot.getHomeIdByPort(accessLine));

    //Start access line registration
    PortAndHomeIdDto portAndHomeIdDto = new PortAndHomeIdDto()
            .vpSz(accessLine.getOltDevice().getVpsz())
            .fachSz(accessLine.getOltDevice().getFsz())
            .portNumber(accessLine.getPortNumber())
            .homeId(accessLine.getHomeId());
    OperationResultLineIdDto callback = ontOltOrchestratorRobot.reserveAccessLineByPortAndHomeId(portAndHomeIdDto);

    // check callback
    assertNull(callback.getError());
    assertTrue(callback.getSuccess());
    assertNotNull(callback.getResponse().getLineId());
    assertEquals(callback.getResponse().getHomeId(), accessLine.getHomeId());

    // check alri
    accessLine.setLineId(callback.getResponse().getLineId());
    assertEquals(AccessLineStatus.ASSIGNED, accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId()));
  }

  @Test(dependsOnMethods = "adtranOntReservation")
  @TmsLink("DIGIHUB-91173")
  @Description("Adtran Registeration ONT resource")
  public void adtranOntRegistration() {
    OperationResultLineIdSerialNumberDto callback = ontOltOrchestratorRobot.registerOnt(accessLine, ontSerialNumber);

    // check callback
    assertNull(callback.getError());
    assertTrue(callback.getSuccess());
    assertEquals(accessLine.getLineId(), callback.getResponse().getLineId());
    assertEquals(ontSerialNumber.getSerialNumber(), callback.getResponse().getSerialNumber());

    // check alri
    SubscriberNeProfileDto subscriberNEProfile = accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId());
    assertNotNull(subscriberNEProfile);
    assertEquals(subscriberNEProfile.getOntSerialNumber(), ontSerialNumber.getSerialNumber());
    assertEquals(subscriberNEProfile.getState(), ProfileState.ACTIVE);
    assertEquals(subscriberNEProfile.getOntState(), OntState.UNKNOWN);
  }

  @Test(dependsOnMethods = {"adtranOntReservation", "adtranOntRegistration"})
  @TmsLink("DIGIHUB-91174")
  @Description("Adtran ONT Connectivity test")
  public void adtranOntTest() {
    //test Ont
    OperationResultOntTestDto callback = ontOltOrchestratorRobot.testOnt(accessLine.getLineId());

    // check callback
    assertNull(callback.getError());
    assertTrue(callback.getSuccess());
    assertNotNull(callback.getResponse().getLastDownCause());
    assertNotNull(callback.getResponse().getLastDownTime());
    assertNotNull(callback.getResponse().getLastUpTime());
    assertEquals(com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.OntState.ONLINE, callback.getResponse().getActualRunState());
    //todo add check for onuid

    ontOltOrchestratorRobot.updateOntState(accessLine);

    // check alri
    SubscriberNeProfileDto subscriberNEProfile = accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId());
    assertNotNull(subscriberNEProfile);
    assertEquals(subscriberNEProfile.getOntState(), OntState.ONLINE);
  }

  @Test(dependsOnMethods = {"adtranOntReservation", "adtranOntRegistration", "adtranOntTest"})
  @TmsLink("DIGIHUB-91178")
  @Description("Adtran Change ONT serial number")
  public void adtranOntChange() {
    //check SN
    assertEquals(ontSerialNumber.getSerialNumber(), accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()).getOntSerialNumber());
    OperationResultLineIdSerialNumberDto callback = ontOltOrchestratorRobot.changeOntSerialNumber(accessLine, ontSerialNumber.getNewSerialNumber());

    // check callback
    assertNull(callback.getError());
    assertTrue(callback.getSuccess());
    assertEquals(accessLine.getLineId(), callback.getResponse().getLineId());
    assertEquals(ontSerialNumber.getNewSerialNumber(), callback.getResponse().getSerialNumber());

    // check alri
    assertEquals(ontSerialNumber.getNewSerialNumber(), accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()).getOntSerialNumber());

  }

  @Test(dependsOnMethods = {"adtranOntReservation", "adtranOntRegistration", "adtranOntTest", "adtranOntChange"})
  @TmsLink("DIGIHUB-91179")
  @Description("Adtran ONT Termination rollback to reservation = false")
  public void adtranOntTermination() {
    OperationResultVoid callback = ontOltOrchestratorRobot.decommissionOnt(accessLine);

    // check callback
    assertTrue(callback.getSuccess());
    assertNull(callback.getError());
    assertNull(callback.getResponse());

    // check alri
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId()), AccessLineStatus.WALLED_GARDEN);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getHomeId(),
            accessLine.getHomeId());
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getDefaultNeProfile().getState(),
            ProfileState.ACTIVE);
    SubscriberNeProfileDto subscriberNEProfile = accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId());
    assertNull(subscriberNEProfile);
  }
}
