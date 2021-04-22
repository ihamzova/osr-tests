package com.tsystems.tm.acc.ta.team.upiter.ontcommissioning;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.OntOltOrchestratorRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_12_0.client.model.AccessLineStatus;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_12_0.client.model.PortType;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_12_0.client.model.ProfileState;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_10_0.client.model.PortAndHomeIdDto;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static org.testng.AssertJUnit.assertEquals;

@ServiceLog({
        ONT_OLT_ORCHESTRATOR_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        DECOUPLING_MS,
        GATEWAY_ROUTE_MS
})
public class FTTBCommissioning extends GigabitTest {

  private AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
  private OntOltOrchestratorRobot ontOltOrchestratorRobot = new OntOltOrchestratorRobot();
  private AccessLine accessLine;
  private UpiterTestContext context = UpiterTestContext.get();

  @BeforeClass
  public void init() throws InterruptedException {
    accessLineRiRobot.clearDatabase();
    Thread.sleep(1000);
    accessLineRiRobot.fillDatabaseForOltCommissioning();
    Thread.sleep(1000);
    accessLineRiRobot.fillDatabaseAddFttbLinesToOltDevice();
    Thread.sleep(1000);
  }

  @AfterClass
  public void clearData() {
    accessLineRiRobot.clearDatabase();
  }


  @Test
  @TmsLink("DIGIHUB-72719")
  @Description("Assign a FTTB line")
  public void FTTBLineReservation() {
    accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.FTTBCommissioning);
    accessLine.setHomeId(accessLineRiRobot.getHomeIdByPort(accessLine));
    //Start access line registration
    PortAndHomeIdDto portAndHomeIdDto = new PortAndHomeIdDto()
            .vpSz(accessLine.getDpuDevice().getVpsz())
            .fachSz(accessLine.getDpuDevice().getFsz())
            .portNumber(accessLine.getDpuPortNumber())
            .portType(PortAndHomeIdDto.PortTypeEnum.valueOf(accessLine.getDpuPortType()))
            .homeId(accessLine.getHomeId());
    String lineId = ontOltOrchestratorRobot.reserveAccessLineByPortAndHomeId(portAndHomeIdDto);
    accessLine.setLineId(lineId);


    String actualHomeId = accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getHomeId();
    assertEquals(actualHomeId, accessLine.getHomeId());
    ProfileState actualStateMosaic = accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getFttbNeProfile().getStateMosaic();
    ProfileState actualStateOlt = accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getFttbNeProfile().getStateOlt();
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId()),
            AccessLineStatus.ASSIGNED);
    assertEquals(actualStateMosaic, ProfileState.ACTIVE);
    assertEquals(actualStateOlt, ProfileState.ACTIVE);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getDpuReference().getPortType(),PortType.GFAST);
  }

  @Test(dependsOnMethods = {"FTTBLineReservation"})
  @TmsLink("DIGIHUB-75824")
  @Description("Terminate FTTB AccessLine: general case")
  public void FTTBLineDecommissioning() {
    ontOltOrchestratorRobot.decommissionOnt(accessLine);
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId()),
            AccessLineStatus.WALLED_GARDEN);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getFttbNeProfile().getStateMosaic(),ProfileState.ACTIVE);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getHomeId(),
        accessLine.getHomeId());
  }
}