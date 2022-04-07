package com.tsystems.tm.acc.ta.team.upiter.ontcommissioning;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.data.upiter.models.dpudevice.DpuDeviceCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.HomeIdManagementRobot;
import com.tsystems.tm.acc.ta.robot.osr.OntOltOrchestratorRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_35_0.client.model.AccessLineStatus;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_35_0.client.model.AccessTransmissionMedium;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_35_0.client.model.PortType;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_35_0.client.model.ProfileState;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.OperationResultLineIdDto;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.OperationResultVoid;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.PortAndHomeIdDto;
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
        GATEWAY_ROUTE_MS
})

@Epic("ONT Processes FTTB")
public class FTTBCommissioning extends GigabitTest {

  private AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
  private OntOltOrchestratorRobot ontOltOrchestratorRobot = new OntOltOrchestratorRobot();
  private HomeIdManagementRobot homeIdManagementRobot = new HomeIdManagementRobot();
  private AccessLine accessLineTwistedPair1;
  private AccessLine accessLineCoax1;
  private AccessLine accessLineTwistedPair2;
  private AccessLine accessLineCoax2;
  private UpiterTestContext context = UpiterTestContext.get();
  private PortProvisioning oltDeviceFttbProvisioningTwistedPair;
  private PortProvisioning oltDeviceFttbProvisioningCoax;
  private DpuDevice dpuDeviceFttbProvisioningTwistedPair;
  private DpuDevice dpuDeviceFttbProvisioningCoax;

  @BeforeClass
  public void init() throws InterruptedException {
    dpuDeviceFttbProvisioningTwistedPair = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.dpuDeviceForFttbProvisioningTwistedPair);
    dpuDeviceFttbProvisioningCoax = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.dpuDeviceForFttbProvisioningCoax);
    oltDeviceFttbProvisioningTwistedPair = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.oltDeviceForFttbProvisioningTwistedPair);
    oltDeviceFttbProvisioningCoax = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.oltDeviceForFttbProvisioningCoax);

    accessLineRiRobot.clearDatabase();
    Thread.sleep(1000);
    accessLineRiRobot.fillDatabaseForOltCommissioningWithDpu(true, AccessTransmissionMedium.TWISTED_PAIR, 1, 1,
            oltDeviceFttbProvisioningTwistedPair.getEndSz(),
            dpuDeviceFttbProvisioningTwistedPair.getEndsz(),
            oltDeviceFttbProvisioningTwistedPair.getSlotNumber(),
            oltDeviceFttbProvisioningTwistedPair.getPortNumber());

    accessLineRiRobot.fillDatabaseForOltCommissioningWithDpu(true, AccessTransmissionMedium.COAX, 10000, 10000,
            oltDeviceFttbProvisioningCoax.getEndSz(),
            dpuDeviceFttbProvisioningCoax.getEndsz(),
            oltDeviceFttbProvisioningCoax.getSlotNumber(),
            oltDeviceFttbProvisioningCoax.getPortNumber());

    accessLineTwistedPair1 = context.getData().getAccessLineDataProvider().get(AccessLineCase.AccessLineTpPort1);
    accessLineCoax1 = context.getData().getAccessLineDataProvider().get(AccessLineCase.AccessLineCoaxPort1);
    accessLineTwistedPair2 = context.getData().getAccessLineDataProvider().get(AccessLineCase.AccessLineTpPort2);
    accessLineCoax2 = context.getData().getAccessLineDataProvider().get(AccessLineCase.AccessLineCoaxPort2);
  }

  @Test
  @TmsLink("DIGIHUB-72719")
  @Description("Assign a FTTB Twisted Pair AccessLine")
  public void FttbLineReservationTwistedPair() {
    accessLineTwistedPair1.setHomeId(homeIdManagementRobot.generateHomeid().getHomeId());
    //Start access line registration
    PortAndHomeIdDto portAndHomeIdDto = new PortAndHomeIdDto()
            .vpSz(accessLineTwistedPair1.getDpuDevice().getVpsz())
            .fachSz(accessLineTwistedPair1.getDpuDevice().getFsz())
            .portNumber(accessLineTwistedPair1.getDpuPortNumber())
            .portType(PortAndHomeIdDto.PortTypeEnum.valueOf(accessLineTwistedPair1.getDpuPortType()))
            .homeId(accessLineTwistedPair1.getHomeId());
    OperationResultLineIdDto callback = ontOltOrchestratorRobot.reserveAccessLineByPortAndHomeId(portAndHomeIdDto);

    // check callback
    assertNull("Callback returned an error", callback.getError());
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertNotNull(callback.getResponse().getLineId(), "Callback didn't return a LineId");
    assertEquals("HomeId in the callback is incorrect", accessLineTwistedPair1.getHomeId(), callback.getResponse().getHomeId());
    accessLineTwistedPair1.setLineId(callback.getResponse().getLineId());

    // check alri
    accessLineTwistedPair1.setLineId(callback.getResponse().getLineId());
    String actualHomeId = accessLineRiRobot.getAccessLinesByLineId(accessLineTwistedPair1.getLineId()).get(0).getHomeId();
    assertEquals("HomeId on the AccessLine is incorrect", accessLineTwistedPair1.getHomeId(), actualHomeId);
    ProfileState actualStateMosaic = accessLineRiRobot.getAccessLinesByLineId(accessLineTwistedPair1.getLineId()).get(0).getFttbNeProfile().getStateMosaic();
    ProfileState actualStateOlt = accessLineRiRobot.getAccessLinesByLineId(accessLineTwistedPair1.getLineId()).get(0).getFttbNeProfile().getStateOlt();

    assertEquals("AccessLine state is incorrect", AccessLineStatus.ASSIGNED,
            accessLineRiRobot.getAccessLineStateByLineId(accessLineTwistedPair1.getLineId()));
    assertEquals("StateMosaic is incorrect", ProfileState.ACTIVE, actualStateMosaic);
    assertEquals("StateOlt is incorrect", ProfileState.ACTIVE, actualStateOlt);
    assertEquals("PortType is incorrect", PortType.GFAST,
            accessLineRiRobot.getAccessLinesByLineId(accessLineTwistedPair1.getLineId()).get(0).getDpuReference().getPortType());
  }

  @Test(dependsOnMethods = {"FttbLineReservationTwistedPair"})
  @TmsLink("DIGIHUB-75824")
  @Description("Terminate FTTB Twisted Pair AccessLine: general case, rollbackToREservation is not filled")
  public void FttbLineDecommissioningTwistedPair() {
    OperationResultVoid callback = ontOltOrchestratorRobot.decommissionOnt(accessLineTwistedPair1);

    // check callback
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertNull("Callback returned an error", callback.getError());
    assertNull("Callback returned a response body", callback.getResponse());

    //check alri
    assertEquals("AccessLine state is incorrect", AccessLineStatus.WALLED_GARDEN,
            accessLineRiRobot.getAccessLineStateByLineId(accessLineTwistedPair1.getLineId()));
    assertEquals("StateMosaic is incorrect", ProfileState.ACTIVE,
            accessLineRiRobot.getAccessLinesByLineId(accessLineTwistedPair1.getLineId()).get(0).getFttbNeProfile().getStateMosaic());
    assertEquals("StateOlt is incorrect", ProfileState.ACTIVE,
            accessLineRiRobot.getAccessLinesByLineId(accessLineTwistedPair1.getLineId()).get(0).getFttbNeProfile().getStateOlt());
    assertEquals("HomeId on the AccessLine is incorrect", accessLineTwistedPair1.getHomeId(),
            accessLineRiRobot.getAccessLinesByLineId(accessLineTwistedPair1.getLineId()).get(0).getHomeId());
  }

  @Test
  @TmsLink("DIGIHUB-72719")
  @Description("Assign a FTTB Coax AccessLine")
  public void FttbLineReservationCoax() {
    accessLineCoax1.setHomeId(homeIdManagementRobot.generateHomeid().getHomeId());
    //Start access line registration
    PortAndHomeIdDto portAndHomeIdDto = new PortAndHomeIdDto()
            .vpSz(accessLineCoax1.getDpuDevice().getVpsz())
            .fachSz(accessLineCoax1.getDpuDevice().getFsz())
            .portNumber(accessLineCoax1.getDpuPortNumber())
            .portType(PortAndHomeIdDto.PortTypeEnum.valueOf(accessLineCoax1.getDpuPortType()))
            .homeId(accessLineCoax1.getHomeId());
    OperationResultLineIdDto callback = ontOltOrchestratorRobot.reserveAccessLineByPortAndHomeId(portAndHomeIdDto);

    // check callback
    assertNull("Callback returned an error", callback.getError());
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertNotNull(callback.getResponse().getLineId(), "Callback didn't return a LineId");
    assertEquals("HomeId returned in the callback is incorrect", accessLineCoax1.getHomeId(), callback.getResponse().getHomeId());
    accessLineCoax1.setLineId(callback.getResponse().getLineId());

    // check alri
    accessLineCoax1.setLineId(callback.getResponse().getLineId());
    String actualHomeId = accessLineRiRobot.getAccessLinesByLineId(accessLineCoax1.getLineId()).get(0).getHomeId();
    assertEquals(actualHomeId, accessLineCoax1.getHomeId());
    ProfileState actualStateMosaic = accessLineRiRobot.getAccessLinesByLineId(accessLineCoax1.getLineId()).get(0).getFttbNeProfile().getStateMosaic();
    ProfileState actualStateOlt = accessLineRiRobot.getAccessLinesByLineId(accessLineCoax1.getLineId()).get(0).getFttbNeProfile().getStateOlt();
    assertEquals("AccessLine state is incorrect", AccessLineStatus.ASSIGNED,
            accessLineRiRobot.getAccessLineStateByLineId(accessLineCoax1.getLineId()));
    assertEquals("StateMosaic is incorrect", ProfileState.ACTIVE, actualStateMosaic);
    assertEquals("StateOlt is incorrect", ProfileState.ACTIVE, actualStateOlt);
    assertEquals("PortType is incorrect", PortType.GFAST,
            accessLineRiRobot.getAccessLinesByLineId(accessLineCoax1.getLineId()).get(0).getDpuReference().getPortType());
  }

  @Test(dependsOnMethods = {"FttbLineReservationCoax"})
  @TmsLink("DIGIHUB-75824")
  @Description("Terminate FTTB Coax AccessLine: general case, rollbackToReservation is not filled")
  public void FttbLineDecommissioningCoax() {
    OperationResultVoid callback = ontOltOrchestratorRobot.decommissionOnt(accessLineCoax1);

    // check callback
    assertTrue(callback.getSuccess(), "Callback returned an error");
    assertNull("Callback returned an error", callback.getError());
    assertNull("Callback returned a response body", callback.getResponse());

    // check alri
    assertEquals("AccessLine state is incorrect", AccessLineStatus.WALLED_GARDEN,
            accessLineRiRobot.getAccessLineStateByLineId(accessLineCoax1.getLineId()));
    assertEquals("StateMosaic is incorrect", ProfileState.ACTIVE,
            accessLineRiRobot.getAccessLinesByLineId(accessLineCoax1.getLineId()).get(0).getFttbNeProfile().getStateMosaic());
    assertEquals("StateOlt is incorrect", ProfileState.ACTIVE,
            accessLineRiRobot.getAccessLinesByLineId(accessLineCoax1.getLineId()).get(0).getFttbNeProfile().getStateOlt());
    assertEquals("HomeId on the AccessLine is incorrect", accessLineCoax1.getHomeId(),
            accessLineRiRobot.getAccessLinesByLineId(accessLineCoax1.getLineId()).get(0).getHomeId());
  }

  @Test
  @TmsLink("DIGIHUB-146275")
  @Description("Terminate FTTB Twisted Pair AccessLine, rollbackToReservation = true")
  public void FttbLineDecommissioningRollbackTrue() {
    accessLineTwistedPair2.setHomeId(homeIdManagementRobot.generateHomeid().getHomeId());
    //Start access line registration
    PortAndHomeIdDto portAndHomeIdDto = new PortAndHomeIdDto()
            .vpSz(accessLineTwistedPair2.getDpuDevice().getVpsz())
            .fachSz(accessLineTwistedPair2.getDpuDevice().getFsz())
            .portNumber(accessLineTwistedPair2.getDpuPortNumber())
            .portType(PortAndHomeIdDto.PortTypeEnum.valueOf(accessLineTwistedPair2.getDpuPortType()))
            .homeId(accessLineTwistedPair2.getHomeId());
    OperationResultLineIdDto reservationCallback = ontOltOrchestratorRobot.reserveAccessLineByPortAndHomeId(portAndHomeIdDto);

    // check callback
    assertNull("Callback returned an error", reservationCallback.getError());
    assertTrue(reservationCallback.getSuccess(), "Callback returned an error");
    assertNotNull(reservationCallback.getResponse().getLineId(), "Callback didn't return a LineId");
    assertEquals("HomeId returned in the callback is incorrect", accessLineTwistedPair2.getHomeId(), reservationCallback.getResponse().getHomeId());
    accessLineTwistedPair2.setLineId(reservationCallback.getResponse().getLineId());


    OperationResultVoid terminationCallback = ontOltOrchestratorRobot.decommissionOntWithRollback(accessLineTwistedPair2, true);

    // check callback
    assertTrue(terminationCallback.getSuccess(), "Callback returned an error");
    assertNull("Callback returned an error", terminationCallback.getError());
    assertNull("Callback returned a response body", terminationCallback.getResponse());

    // check alri
    assertEquals("AccessLine state is incorrect", AccessLineStatus.ASSIGNED,
            accessLineRiRobot.getAccessLineStateByLineId(accessLineTwistedPair2.getLineId()));
    assertEquals("StateMosaic is incorrect", ProfileState.ACTIVE,
            accessLineRiRobot.getAccessLinesByLineId(accessLineTwistedPair2.getLineId()).get(0).getFttbNeProfile().getStateMosaic());
    assertEquals("StateOlt is incorrect", ProfileState.ACTIVE,
            accessLineRiRobot.getAccessLinesByLineId(accessLineTwistedPair2.getLineId()).get(0).getFttbNeProfile().getStateOlt());
    assertEquals("HomeId on the AccessLine is incorrect", accessLineTwistedPair2.getHomeId(),
            accessLineRiRobot.getAccessLinesByLineId(accessLineTwistedPair2.getLineId()).get(0).getHomeId());
  }

  @Test
  @TmsLink("DIGIHUB-146276")
  @Description("Terminate FTTB Coax AccessLine, rollbackToReservation = false")
  public void FttbLineDecommissioningRollbackFalse() {
    accessLineCoax2.setHomeId(homeIdManagementRobot.generateHomeid().getHomeId());
    //Start access line registration
    PortAndHomeIdDto portAndHomeIdDto = new PortAndHomeIdDto()
            .vpSz(accessLineCoax2.getDpuDevice().getVpsz())
            .fachSz(accessLineCoax2.getDpuDevice().getFsz())
            .portNumber(accessLineCoax2.getDpuPortNumber())
            .portType(PortAndHomeIdDto.PortTypeEnum.valueOf(accessLineCoax2.getDpuPortType()))
            .homeId(accessLineCoax2.getHomeId());
    OperationResultLineIdDto reservationCallback = ontOltOrchestratorRobot.reserveAccessLineByPortAndHomeId(portAndHomeIdDto);

    // check callback
    assertNull("Callback returned an error", reservationCallback.getError());
    assertTrue(reservationCallback.getSuccess(), "Callback returned an error");
    assertNotNull(reservationCallback.getResponse().getLineId(), "Callback didn't return a LineId");
    assertEquals("HomeId returned in the callback is incorrect", accessLineCoax2.getHomeId(), reservationCallback.getResponse().getHomeId());
    accessLineCoax2.setLineId(reservationCallback.getResponse().getLineId());


    OperationResultVoid terminationCallback = ontOltOrchestratorRobot.decommissionOntWithRollback(accessLineCoax2, false);

    // check callback
    assertTrue(terminationCallback.getSuccess(), "Callback returned an error");
    assertNull("Callback returned an error", terminationCallback.getError());
    assertNull("Callback returned a response body", terminationCallback.getResponse());

    // check alri
    assertEquals("AccessLine state is incorrect", AccessLineStatus.WALLED_GARDEN, accessLineRiRobot.getAccessLineStateByLineId(accessLineCoax2.getLineId()));
    assertEquals("StateMosaic is incorrect", ProfileState.ACTIVE,
            accessLineRiRobot.getAccessLinesByLineId(accessLineCoax2.getLineId()).get(0).getFttbNeProfile().getStateMosaic());
    assertEquals("StateOlt is incorrect", ProfileState.ACTIVE,
            accessLineRiRobot.getAccessLinesByLineId(accessLineCoax2.getLineId()).get(0).getFttbNeProfile().getStateOlt());
    assertEquals("HomeId on the AccessLine is incorrect", accessLineCoax2.getHomeId(),
            accessLineRiRobot.getAccessLinesByLineId(accessLineCoax2.getLineId()).get(0).getHomeId());
  }
}