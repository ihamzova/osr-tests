package com.tsystems.tm.acc.ta.team.upiter.assurance;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLineStatus;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLineTechnology;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessTransmissionMedium;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.ACCESS_LINE_RESOURCE_INVENTORY_MS;
import static org.testng.Assert.*;

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
    accessLineRiRobot.clearDatabaseByOlt("49/89/8000/76H2");
    accessLineRiRobot.clearDatabaseByOlt("49/911/1100/76H1");
    accessLineRiRobot.clearDatabaseByOlt("49/911/1100/76H3");
    accessLineRiRobot.clearDatabaseByOlt("49/911/1100/76H5");
    accessLineRiRobot.clearDatabaseByOlt("49/30/179/76H1");
    accessLineRiRobot.clearDatabaseByOlt("49/30/179/76G3");
    Thread.sleep(1000);
    accessLineForSearchByOlt = context.getData().getAccessLineDataProvider().get(AccessLineCase.AccessLineForSearchByOlt);
    accessLineForSearchByDpu = context.getData().getAccessLineDataProvider().get(AccessLineCase.AccessLineForSearchByDpu);
    accessLineRiRobot.fillDatabaseForOltCommissioningWithDpu(true, AccessTransmissionMedium.TWISTED_PAIR, 1, 1,
            "49/89/8000/76H2", "49/812/179/71G0", "1", "0");
  }

  @Test
  @TmsLink("DIGIHUB-104136")
  @Description("Search AccessLine entities by Lineid")
  public void searchByLineId() {
    List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> response = accessLineRiRobot.getAccessLineEntitiesByLineId(accessLineForSearchByOlt.getLineId());
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
    List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> response = accessLineRiRobot
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
    List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> response = accessLineRiRobot
            .getAccessLineEntitiesByDpu(accessLineForSearchByDpu.getDpuEndSz(), accessLineForSearchByDpu.getDpuPortNumber());
    assertEquals(response.get(0).getPortReferences().getDpuDownlinkPortReference().getEndSZ(), accessLineForSearchByDpu.getDpuEndSz());
    assertEquals(response.get(0).getPortReferences().getDpuDownlinkPortReference().getPortName(), accessLineForSearchByDpu.getDpuPortNumber());
  }

  @Test
  @TmsLink("DIGIHUB-139281")
  @Description("Search AccessLine entities by HomeId")
  public void searchByHomeId() {
    com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine expectedAccessLine = accessLineRiRobot
            .getAllAccessLineEntities().stream().filter(accessLine -> accessLine.getHomeId() != null).collect(Collectors.toList()).get(0);

    List <com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> actualAccessLines =
            accessLineRiRobot.getAccessLineEntitiesByHomeId(expectedAccessLine.getHomeId());

    assertTrue(actualAccessLines.size() == 1);
    assertEquals(actualAccessLines.get(0), expectedAccessLine);
  }

  @Test
  @TmsLink("DIGIHUB-139582")
  @Description("Search AccessLine entities by OntSerialNumber")
  public void searchByOntSerialNumber() {
    com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine expectedAccessLine = accessLineRiRobot
            .getAllAccessLineEntities().stream()
            .filter(accessLine -> accessLine.getProfiles().getFtthNeProfile()!=null)
            .filter(accessLine -> accessLine.getProfiles().getFtthNeProfile().getSubscriberNetworkElementProfile() != null)
            .collect(Collectors.toList()).get(0);

    List <com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> actualAccessLines =
            accessLineRiRobot.getAccessLineEntitiesByOntSerialNumber(expectedAccessLine.getProfiles().getFtthNeProfile()
                    .getSubscriberNetworkElementProfile().getOntSerialNumber());

    assertTrue(actualAccessLines.size() == 1);
    assertEquals(actualAccessLines.get(0), expectedAccessLine);
  }

  @Test
  @TmsLink("DIGIHUB-139281")
  @Description("Search AccessLine entities by Status")
  public void searchByStatus() {
    List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> response = accessLineRiRobot
            .getAccessLineEntitiesByStatus(AccessLineStatus.ASSIGNED);
    assertTrue(response.stream().allMatch(accessLine -> accessLine.getStatus().equals(AccessLineStatus.ASSIGNED)));
  }

  @Test
  @TmsLink("DIGIHUB-139282")
  @Description("Search AccessLine entities by Technology")
  public void searchByTechnology() {
    List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> response = accessLineRiRobot
            .getAccessLineEntitiesByTechnology(AccessLineTechnology.GPON);
    assertTrue(response.stream().allMatch(accessLine -> accessLine.getTechnology().equals(AccessLineTechnology.GPON)));
  }

  @Test
  @TmsLink("DIGIHUB-139283")
  @Description("Search AccessLine entities by modification date (greater than)")
  public void searchByModificationDateGreaterThan() {
    List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> accessLinesBeforeFiltering = accessLineRiRobot
            .getAllAccessLineEntities();

    com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine filteredAccessLine = accessLinesBeforeFiltering.stream()
            .sorted(Comparator.comparing(com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine::getModificationDate))
            .collect(Collectors.toList())
            .get(0);

    OffsetDateTime offsetDateTime = filteredAccessLine.getModificationDate();

    List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> accessLinesAfterFiltering
            = accessLineRiRobot.getAccessLineEntitiesByModificationDateGt(offsetDateTime);

    assertEquals(accessLinesAfterFiltering.size(), accessLinesBeforeFiltering.size()-1);
    assertFalse(accessLinesAfterFiltering.contains(filteredAccessLine));
  }

  @Test
  @TmsLink("DIGIHUB-139284")
  @Description("Search AccessLine entities by modification date (less than)")
  public void searchByModificationDateLessThan() {
    List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> accessLinesBeforeFiltering = accessLineRiRobot
            .getAllAccessLineEntities();

    com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine filteredAccessLine = accessLinesBeforeFiltering.stream()
            .sorted(Comparator.comparing(com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine::getModificationDate)
                    .reversed())
            .collect(Collectors.toList())
            .get(0);

    OffsetDateTime offsetDateTime = filteredAccessLine.getModificationDate();

    List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> accessLinesAfterFiltering =
            accessLineRiRobot.getAccessLineEntitiesByModificationDateLt(offsetDateTime);

    assertEquals(accessLinesAfterFiltering.size(), accessLinesBeforeFiltering.size()-1);
    assertFalse(accessLinesAfterFiltering.contains(filteredAccessLine));
  }

  @Test
  @TmsLink("DIGIHUB-139285")
  @Description("Search AccessLine entities with offset")
  public void searchWithOffset() {
    int offset = 1 + (int) (Math.random() * 64);

    List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> allAccessLines =
            accessLineRiRobot.getAllAccessLineEntities();

    List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> excludedAccessLines =
            allAccessLines.subList(0, offset);

    List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> filteredAccessLines =
            accessLineRiRobot.getAccessLineEntitiesWithOffset(offset);

    assertEquals(filteredAccessLines.size(), allAccessLines.size()-offset);
    assertFalse(filteredAccessLines.contains(excludedAccessLines));
  }

  @Test
  @TmsLink("DIGIHUB-139286")
  @Description("Search AccessLine by id")
  public void searchById() {
    com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine expectedAccessline = accessLineRiRobot
            .getAllAccessLineEntities().get(0);
    com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine actualAccessline =
            accessLineRiRobot.getAccessLineEntitiesbyId(expectedAccessline.getId());

    assertEquals(actualAccessline.getLineId(), expectedAccessline.getLineId());
  }
}