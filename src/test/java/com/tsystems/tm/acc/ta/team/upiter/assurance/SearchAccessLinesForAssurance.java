package com.tsystems.tm.acc.ta.team.upiter.assurance;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.AccessLineStatus;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.AccessTransmissionMedium;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.ACCESS_LINE_RESOURCE_INVENTORY_MS;
import static org.testng.Assert.assertEquals;

@ServiceLog(ACCESS_LINE_RESOURCE_INVENTORY_MS)

@Epic("Assurance")
public class SearchAccessLinesForAssurance extends GigabitTest {

  private AccessLineRiRobot accessLineRiRobot;
  private UpiterTestContext context = UpiterTestContext.get();
  private AccessLine accessLineForSearchByOlt;
  private AccessLine accessLineForSearchByDpu;

  @BeforeClass
  public void init() throws InterruptedException {
    accessLineRiRobot = new AccessLineRiRobot();
    accessLineRiRobot.clearDatabase();
    Thread.sleep(1000);
    accessLineForSearchByOlt = context.getData().getAccessLineDataProvider().get(AccessLineCase.AccessLineForSearchByOlt);
    accessLineForSearchByDpu = context.getData().getAccessLineDataProvider().get(AccessLineCase.AccessLineForSearchByDpu);
    accessLineRiRobot.fillDatabaseForOltCommissioningWithDpu(true, AccessTransmissionMedium.TWISTED_PAIR, 1, 1,
            "49/89/8000/76H2", "49/812/179/71G0", "1", "0");
  }

  @AfterClass
  public void clearData() {
    accessLineRiRobot.clearDatabase();
  }

  @Test
  @TmsLink("DIGIHUB-104136")
  @Description("Search AccessLine entities by Lineid")
  public void searchByLineId() {
    List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.AccessLine> response = accessLineRiRobot.getAccessLineEntitiesByLineId(accessLineForSearchByOlt.getLineId());
    assertEquals(response.get(0).getLineId(), accessLineForSearchByOlt.getLineId());
    assertEquals(response.get(0).getStatus(), AccessLineStatus.ASSIGNED);
    assertEquals(response.get(0).getPortReferences().getOltDownlinkPortReference().getEndSZ(), accessLineForSearchByOlt.getEndSz());
    assertEquals(response.get(0).getPortReferences().getOltDownlinkPortReference().getSlotName(), accessLineForSearchByOlt.getSlotNumber());
    assertEquals(response.get(0).getPortReferences().getOltDownlinkPortReference().getPortName(), accessLineForSearchByOlt.getPortNumber());
  }

  @Test
  @TmsLink("DIGIHUB-104137")
  @Description("Search AccessLine entities by OltEndSZ, slot, port and limit")
  public void searchByOltEndSZ() {
    List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.AccessLine> response = accessLineRiRobot
            .getAccessLineEntitiesByOlt(accessLineForSearchByOlt.getLimit(), accessLineForSearchByOlt.getEndSz(), accessLineForSearchByOlt.getSlotNumber(), accessLineForSearchByOlt.getPortNumber());
    assertEquals(response.get(0).getPortReferences().getOltDownlinkPortReference().getEndSZ(), accessLineForSearchByOlt.getEndSz());
    assertEquals(response.get(0).getPortReferences().getOltDownlinkPortReference().getSlotName(), accessLineForSearchByOlt.getSlotNumber());
    assertEquals(response.get(0).getPortReferences().getOltDownlinkPortReference().getPortName(), accessLineForSearchByOlt.getPortNumber());
    assertEquals(response.size(), accessLineForSearchByOlt.getLimit());
  }

  @Test
  @TmsLink("DIGIHUB-104138")
  @Description("Search AccessLine entities by DpuEndSZ and port")
  public void searchByDpuEndSZ() {
    List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.AccessLine> response = accessLineRiRobot
            .getAccessLineEntitiesByDpu(accessLineForSearchByDpu.getDpuEndSz(), accessLineForSearchByDpu.getDpuPortNumber());
    assertEquals(response.get(0).getPortReferences().getDpuDownlinkPortReference().getEndSZ(), accessLineForSearchByDpu.getDpuEndSz());
    assertEquals(response.get(0).getPortReferences().getDpuDownlinkPortReference().getPortName(), accessLineForSearchByDpu.getDpuPortNumber());
  }
}