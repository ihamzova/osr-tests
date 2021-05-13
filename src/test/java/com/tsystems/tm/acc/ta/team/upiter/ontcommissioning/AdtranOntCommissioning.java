package com.tsystems.tm.acc.ta.team.upiter.ontcommissioning;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.data.upiter.models.ont.OntCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.Ont;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.OntOltOrchestratorRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_14_0.client.model.*;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_10_0.client.model.PortAndHomeIdDto;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static org.testng.Assert.*;

@ServiceLog({
        ONT_OLT_ORCHESTRATOR_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        DECOUPLING_MS,
        APIGW_MS
})
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
    String lineId = ontOltOrchestratorRobot.reserveAccessLineByPortAndHomeId(portAndHomeIdDto);
    accessLine.setLineId(lineId);

    //Get Access line state
    AccessLineStatus lineIdState = accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId());

    //Check that access line became assigned
    Assert.assertEquals(AccessLineStatus.ASSIGNED, lineIdState);
  }

  @Test(dependsOnMethods = "adtranOntReservation")
  @TmsLink("DIGIHUB-91173")
  @Description("Adtran Registeration ONT resource")
  public void adtranOntRegistration() {
    //Register ONT
    ontOltOrchestratorRobot.registerOnt(accessLine, ontSerialNumber);

    //Check subscriberNEProfile
    SubscriberNeProfileDto subscriberNEProfile = accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId());
    assertNotNull(subscriberNEProfile);
    Assert.assertEquals(subscriberNEProfile.getOntSerialNumber(), ontSerialNumber.getSerialNumber());
    Assert.assertEquals(subscriberNEProfile.getState(), ProfileState.ACTIVE);
    Assert.assertEquals(subscriberNEProfile.getOntState(), OntState.UNKNOWN);
  }

  @Test(dependsOnMethods = {"adtranOntRegistration"})
  @TmsLink("DIGIHUB-91174")
  @Description("Adtran ONT Connectivity test")
  public void adtranOntTest() {
    //test Ont
    ontOltOrchestratorRobot.testOnt(accessLine.getLineId());

    //update Ont state
    ontOltOrchestratorRobot.updateOntState(accessLine);
    SubscriberNeProfileDto subscriberNEProfile = accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId());
    assertNotNull(subscriberNEProfile);
    assertEquals(subscriberNEProfile.getOntState(), OntState.ONLINE);
  }

  @Test(dependsOnMethods = {"adtranOntTest"})
  @TmsLink("DIGIHUB-91178")
  @Description("Adtran Change ONT serial number")
  public void adtranOntChange() {
    //check SN
    Assert.assertEquals(ontSerialNumber.getSerialNumber(), accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()).getOntSerialNumber());
    ontOltOrchestratorRobot.changeOntSerialNumber(accessLine, ontSerialNumber.getNewSerialNumber());
    Assert.assertEquals(ontSerialNumber.getNewSerialNumber(), accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()).getOntSerialNumber());

  }

  @Test(dependsOnMethods = {"adtranOntChange"})
  @TmsLink("DIGIHUB-91179")
  @Description("Adtran ONT Termination rollback to reservation = false")
  public void adtranOntTermination() {
    ontOltOrchestratorRobot.decommissionOnt(accessLine);
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId()), AccessLineStatus.WALLED_GARDEN);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getHomeId(),
            accessLine.getHomeId());
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getDefaultNeProfile().getState(),
            ProfileState.ACTIVE);
    SubscriberNeProfileDto subscriberNEProfile = accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId());
    assertNull(subscriberNEProfile);
  }
}
