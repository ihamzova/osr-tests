package com.tsystems.tm.acc.ta.team.upiter.ontcommissioning;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.OntOltOrchestratorRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_19_0.client.model.AccessLineStatus;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_19_0.client.model.PortType;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_19_0.client.model.ProfileState;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.OperationResultLineIdDto;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.OperationResultVoid;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.PortAndHomeIdDto;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
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
        GATEWAY_ROUTE_MS
})
public class FTTBCommissioning extends GigabitTest {

  private AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
  private OntOltOrchestratorRobot ontOltOrchestratorRobot = new OntOltOrchestratorRobot();
  private AccessLine accessLineTwistedPair;
  private AccessLine accessLineCoax;
  private UpiterTestContext context = UpiterTestContext.get();

  @BeforeClass
  public void init() throws InterruptedException {
    accessLineRiRobot.clearDatabase();
    Thread.sleep(1000);
    accessLineRiRobot.fillDatabaseForOltCommissioningV1();
    Thread.sleep(1000);
    accessLineRiRobot.fillDatabaseAddFttbLinesToOltDevice();
    Thread.sleep(1000);

    accessLineTwistedPair = context.getData().getAccessLineDataProvider().get(AccessLineCase.FTTBCommissioningTwistedPair);
    accessLineCoax = context.getData().getAccessLineDataProvider().get(AccessLineCase.FTTBCommissioningCoax);
  }

  @AfterClass
  public void clearData() {
    accessLineRiRobot.clearDatabase();
  }


  @Test
  @TmsLink("DIGIHUB-72719")
  @Description("Assign a FTTB Twisted Pair AccessLine")
  public void FTTBLineReservationTwistedPair() {
    accessLineTwistedPair.setHomeId(accessLineRiRobot.getHomeIdByPort(accessLineTwistedPair));
    //Start access line registration
    PortAndHomeIdDto portAndHomeIdDto = new PortAndHomeIdDto()
            .vpSz(accessLineTwistedPair.getDpuDevice().getVpsz())
            .fachSz(accessLineTwistedPair.getDpuDevice().getFsz())
            .portNumber(accessLineTwistedPair.getDpuPortNumber())
            .portType(PortAndHomeIdDto.PortTypeEnum.valueOf(accessLineTwistedPair.getDpuPortType()))
            .homeId(accessLineTwistedPair.getHomeId());
    OperationResultLineIdDto callback = ontOltOrchestratorRobot.reserveAccessLineByPortAndHomeId(portAndHomeIdDto);

    // check callback
    assertNull(callback.getError());
    assertTrue(callback.getSuccess());
    assertNotNull(callback.getResponse().getLineId());
    assertEquals(accessLineTwistedPair.getHomeId(), callback.getResponse().getHomeId());
    accessLineTwistedPair.setLineId(callback.getResponse().getLineId());

    // check alri
    accessLineTwistedPair.setLineId(callback.getResponse().getLineId());
    String actualHomeId = accessLineRiRobot.getAccessLinesByLineId(accessLineTwistedPair.getLineId()).get(0).getHomeId();
    assertEquals(accessLineTwistedPair.getHomeId(), actualHomeId);
    ProfileState actualStateMosaic = accessLineRiRobot.getAccessLinesByLineId(accessLineTwistedPair.getLineId()).get(0).getFttbNeProfile().getStateMosaic();
    ProfileState actualStateOlt = accessLineRiRobot.getAccessLinesByLineId(accessLineTwistedPair.getLineId()).get(0).getFttbNeProfile().getStateOlt();
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLineTwistedPair.getLineId()),
            AccessLineStatus.ASSIGNED);
    assertEquals(actualStateMosaic, ProfileState.ACTIVE);
    assertEquals(actualStateOlt, ProfileState.ACTIVE);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineTwistedPair.getLineId()).get(0).getDpuReference().getPortType(), PortType.GFAST);
  }

  @Test(dependsOnMethods = {"FTTBLineReservationTwistedPair"})
  @TmsLink("DIGIHUB-75824")
  @Description("Terminate FTTB Twisted Pair AccessLine: general case")
  public void FTTBLineDecommissioningTwistedPair() {
    OperationResultVoid callback = ontOltOrchestratorRobot.decommissionOnt(accessLineTwistedPair);

    // check callback
    assertTrue(callback.getSuccess());
    assertNull(callback.getError());
    assertNull(callback.getResponse());

    //check alri
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLineTwistedPair.getLineId()),
            AccessLineStatus.WALLED_GARDEN);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineTwistedPair.getLineId()).get(0).getFttbNeProfile().getStateMosaic(), ProfileState.ACTIVE);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineTwistedPair.getLineId()).get(0).getHomeId(),
            accessLineTwistedPair.getHomeId());
  }

  @Test
  @TmsLink("DIGIHUB-72719")
  @Description("Assign a FTTB Coax AccessLine")
  public void FTTBLineReservationCoax() {
    accessLineCoax.setHomeId(accessLineRiRobot.getHomeIdByPort(accessLineCoax));
    //Start access line registration
    PortAndHomeIdDto portAndHomeIdDto = new PortAndHomeIdDto()
            .vpSz(accessLineCoax.getDpuDevice().getVpsz())
            .fachSz(accessLineCoax.getDpuDevice().getFsz())
            .portNumber(accessLineCoax.getDpuPortNumber())
            .portType(PortAndHomeIdDto.PortTypeEnum.valueOf(accessLineCoax.getDpuPortType()))
            .homeId(accessLineCoax.getHomeId());
    OperationResultLineIdDto callback = ontOltOrchestratorRobot.reserveAccessLineByPortAndHomeId(portAndHomeIdDto);

    // check callback
    assertNull(callback.getError());
    assertTrue(callback.getSuccess());
    assertNotNull(callback.getResponse().getLineId());
    assertEquals(accessLineCoax.getHomeId(), callback.getResponse().getHomeId());
    accessLineCoax.setLineId(callback.getResponse().getLineId());

    // check alri
    accessLineCoax.setLineId(callback.getResponse().getLineId());
    String actualHomeId = accessLineRiRobot.getAccessLinesByLineId(accessLineCoax.getLineId()).get(0).getHomeId();
    assertEquals(actualHomeId, accessLineCoax.getHomeId());
    ProfileState actualStateMosaic = accessLineRiRobot.getAccessLinesByLineId(accessLineCoax.getLineId()).get(0).getFttbNeProfile().getStateMosaic();
    ProfileState actualStateOlt = accessLineRiRobot.getAccessLinesByLineId(accessLineCoax.getLineId()).get(0).getFttbNeProfile().getStateOlt();
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLineCoax.getLineId()),
            AccessLineStatus.ASSIGNED);
    assertEquals(actualStateMosaic, ProfileState.ACTIVE);
    assertEquals(actualStateOlt, ProfileState.ACTIVE);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineCoax.getLineId()).get(0).getDpuReference().getPortType(), PortType.GFAST);
  }

  @Test(dependsOnMethods = {"FTTBLineReservationCoax"})
  @TmsLink("DIGIHUB-75824")
  @Description("Terminate FTTB Coax AccessLine: general case")
  public void FTTBLineDecommissioningCoax() {
    OperationResultVoid callback = ontOltOrchestratorRobot.decommissionOnt(accessLineCoax);

    // check callback
    assertTrue(callback.getSuccess());
    assertNull(callback.getError());
    assertNull(callback.getResponse());

    // check alri
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLineCoax.getLineId()),
            AccessLineStatus.WALLED_GARDEN);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineCoax.getLineId()).get(0).getFttbNeProfile().getStateMosaic(), ProfileState.ACTIVE);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineCoax.getLineId()).get(0).getHomeId(),
            accessLineCoax.getHomeId());
  }
}