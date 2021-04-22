package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryFillDbClient;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.helpers.osr.logs.TimeoutBlock;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_12_0.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_12_0.client.model.*;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class AccessLineRiRobot {
  private static final Integer LATENCY_FOR_PORT_PROVISIONING = 300_000;

  private ApiClient accessLineResourceInventory = new AccessLineResourceInventoryClient().getClient();
  private AccessLineResourceInventoryFillDbClient accessLineResourceInventoryFillDbClient = new AccessLineResourceInventoryFillDbClient();

  @Step("Clear Al RI db")
  public void clearDatabase() {
    accessLineResourceInventoryFillDbClient.getClient().fillDatabase().deleteDatabase().execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
  }

  @Step("Fill database with test data as a part of OLT Commissioning process emulation")
  public void fillDatabaseForOltCommissioning() {
    accessLineResourceInventoryFillDbClient.getClient().fillDatabase().fillDatabaseForOltCommissioning().execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
  }

  @Step("Fill database with test data as a part of Adtran OLT Commissioning process emulation")
  public void fillDatabaseForAdtranOltCommissioning() {
    accessLineResourceInventoryFillDbClient.getClient().fillDatabase().fillDatabaseWithAdtranOlt()
            .HOME_ID_SEQQuery(1)
            .LINE_ID_SEQQuery(1)
            .execute(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
  }

  @Step("Add FTTB access lines to olt device")
  public void fillDatabaseAddFttbLinesToOltDevice() {
    accessLineResourceInventoryFillDbClient.getClient().fillDatabase().addFttbLinesToOltDevice().execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
  }

  @Step("Fill database with test data as a part of DPU Preprovisioning process emulation")
  public void fillDatabaseForDpuPreprovisioning() {
    accessLineResourceInventoryFillDbClient.getClient().fillDatabase().fillDatabaseForDpuPreprovisioning().execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
  }

  @Step("Check home id count for port")
  public void checkHomeIdsCount(PortProvisioning port) {
    List<HomeIdDto> homeIds = accessLineResourceInventory.homeIdController().searchHomeIds().body(new SearchHomeIdDto()
            .endSz(port.getEndSz())
            .slotNumber(port.getSlotNumber())
            .portNumber(port.getPortNumber()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    assertEquals(homeIds.size(), port.getHomeIdPool().intValue(), "Home ids count");
  }

  @Step("Check line id count for port")
  public void checkLineIdsCount(PortProvisioning port) {
    List<LineIdDto> lineIds = accessLineResourceInventory.lineIdController().searchLineIds().body(new SearchLineIdDto()
            .endSz(port.getEndSz())
            .slotNumber(port.getSlotNumber())
            .portNumber(port.getPortNumber()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    assertEquals(lineIds.size(), port.getLineIdPool().intValue(), "Line ids count");
  }

  @Step("Check access lines parameters of port template (lines count and wg lines count, count of default NE and NetworkLine profiles)")
  public void checkPortParametersForLines(PortProvisioning port) {
    try {
      TimeoutBlock timeoutBlock = new TimeoutBlock(LATENCY_FOR_PORT_PROVISIONING); //set timeout in milliseconds
      Supplier<Boolean> checkFtthProvisioning = () -> getAccessLinesByPort(port).size() == port.getAccessLinesCount();
      timeoutBlock.addBlock(checkFtthProvisioning); // execute the runnable precondition
    } catch (Throwable e) {
      //catch the exception here . Which is block didn't execute within the time limit
    }

    List<AccessLineDto> accessLines = getAccessLinesByPort(port);
    assertEquals(accessLines.size(), port.getAccessLinesCount().intValue(),
            "Access lines count");

    long countDefaultNEProfileActive = accessLines.stream().map(AccessLineDto::getDefaultNeProfile)
            .filter(defaultNeProfile -> defaultNeProfile != null && defaultNeProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

    long countDefaultNetworkLineProfileActive = accessLines.stream().map(AccessLineDto::getDefaultNetworkLineProfile)
            .filter(defaultNetworkLineProfile -> defaultNetworkLineProfile != null && defaultNetworkLineProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

    long countAccessLinesWG = accessLines.stream()
            .filter(accessLine -> accessLine.getStatus().getValue().equals(STATUS_WALLED_GARDEN)).count();

    assertEquals(countDefaultNetworkLineProfileActive, port.getDefaultNetworkLineProfilesActive().intValue(),
            "Default Network Line profile count");
    assertEquals(countDefaultNEProfileActive, port.getDefaultNEProfilesActive().intValue(),
            "Default NE profile count");
    assertEquals(countAccessLinesWG, port.getAccessLinesWG().intValue(),
            "WG access lines count");
  }

  @Step("Check assigned access lines count of port template")
  public void checkPortParametersForAssignedLines(PortProvisioning port) {
    List<AccessLineDto> accessLines = getAccessLinesByPort(port);
    assertEquals(accessLines.size(), port.getAccessLinesCount().intValue(),
            "Access lines count");

    long countAssignedAccessLines = accessLines.stream()
            .filter(accessLine -> accessLine.getStatus().getValue().equals(AccessLineStatus.ASSIGNED.getValue())).count();
    assertEquals(countAssignedAccessLines,
            port.getAccessLinesCount() - port.getAccessLinesWG(),
            "Assigned access lines count");
  }

  @Step("Check A4 specific parameters (NSP ref and phys ref exist, A4 prod platform")
  public void checkA4LineParameters(PortProvisioning port, String tpRefUuid) {
    try {
      TimeoutBlock timeoutBlock = new TimeoutBlock(LATENCY_FOR_PORT_PROVISIONING); //set timeout in milliseconds
      Supplier<Boolean> checkA4PreProvisioning = () -> getAccessLinesByPort(port).size() == 1;
      timeoutBlock.addBlock(checkA4PreProvisioning); // execute the runnable precondition
    } catch (Throwable e) {
      //catch the exception here . Which is block didn't execute within the time limit
    }

    List<AccessLineDto> accessLines = getAccessLinesByPort(port);
    assertEquals(accessLines.size(), port.getAccessLinesCount().intValue(), "Access lines count");

    AccessLineDto accessLine = accessLines.get(0);

    assertEquals(accessLine.getStatus(), AccessLineStatus.WALLED_GARDEN, "AccessLine status");
    assertEquals(accessLine.getDefaultNetworkLineProfile().getState(), ProfileState.ACTIVE);
    assertNotNull(accessLine.getNetworkServiceProfileReference(), "There is no NSP reference");
    assertEquals(accessLine.getNetworkServiceProfileReference().getTpRef(), tpRefUuid);
    assertNotNull(accessLine.getReference(), "Reference");
    assertEquals(accessLine.getReference().getEndSz(), port.getEndSz());
    assertEquals(accessLine.getReference().getSlotNumber(), port.getSlotNumber());
    assertEquals(accessLine.getReference().getPortNumber(), port.getPortNumber());
    assertEquals(accessLine.getReference().getPortType(), PortType.GPON);
    assertEquals(accessLine.getProductionPlatform(), AccessLineProductionPlatform.A4, "Production platform");
  }

  @Step("Check FTTB AccessLines (FTTB_NE_Profile, Default_NetworkLine_Profile")
  public void checkFttbLineParameters(PortProvisioning port, int numberOfAccessLinesForProvisioning) {
    try {
      TimeoutBlock timeoutBlock = new TimeoutBlock(LATENCY_FOR_PORT_PROVISIONING); //set timeout in milliseconds
      Supplier<Boolean> checkFttbProvisioning = () -> getAccessLinesByPort(port).size() == numberOfAccessLinesForProvisioning;
      timeoutBlock.addBlock(checkFttbProvisioning); // execute the runnable precondition
    } catch (Throwable e) {
      //catch the exception here . Which is block didn't execute within the time limit
    }

    List<AccessLineDto> accessLines = getAccessLinesByPort(port);
    assertEquals(accessLines.size(), numberOfAccessLinesForProvisioning, "AccessLines count");

    long countFttbNeOltStateActive = accessLines.stream().map(AccessLineDto::getFttbNeProfile)
            .filter(fttbNeProfile -> fttbNeProfile != null && ProfileState.ACTIVE.equals(fttbNeProfile.getStateOlt())).count();

    long countFttbNeMosaicActive = accessLines.stream().map(AccessLineDto::getFttbNeProfile)
            .filter(fttbNeProfile -> fttbNeProfile != null && ProfileState.ACTIVE.equals(fttbNeProfile.getStateMosaic())).count();

    long countDefaultNetworkLineProfilesActive = accessLines.stream().map(AccessLineDto::getDefaultNetworkLineProfile)
            .filter(defaultNetworkLineProfile -> defaultNetworkLineProfile != null && ProfileState.ACTIVE.equals(defaultNetworkLineProfile.getState())).count();

    long countAccessLinesWG = accessLines.stream()
            .filter(accessLine -> accessLine.getStatus().getValue().equals(STATUS_WALLED_GARDEN)).count();

    List<Integer> expectedOnuIdsList = IntStream.rangeClosed(1, numberOfAccessLinesForProvisioning)
            .boxed().collect(Collectors.toList());

    List<Integer> onuAccessIds = accessLines.stream().map(AccessLineDto::getFttbNeProfile).map(FttbNeProfileDto::getOnuAccessId).
            map(OnuAccessIdDto::getOnuAccessId).sorted().collect(Collectors.toList());

    assertEquals(countFttbNeOltStateActive, numberOfAccessLinesForProvisioning,
            "FTTB NE Profiles (Olt State) count is incorrect");
    assertEquals(countFttbNeMosaicActive, numberOfAccessLinesForProvisioning,
            "FTTB NE Profiles (Mosaic State) count is incorrect");
    assertEquals(countDefaultNetworkLineProfilesActive, numberOfAccessLinesForProvisioning,
            "Default NetworkLine Profile count is incorrect");
    assertEquals(countAccessLinesWG, numberOfAccessLinesForProvisioning,
            "WG AccessLines count is incorrect");
    assertEquals(onuAccessIds, expectedOnuIdsList,
            "OnuAccessIds are incorrect");
  }

  @Step("Remove lines with id > 1008, change some port refs")
  public void prepareTestDataToDeprovisioning(PortProvisioning port) {
    // delete extra lines
    getAccessLinesByPort(port).stream()
            .filter(line -> line.getId() > 1008)
            .forEach(line -> {
              accessLineResourceInventory.accessLineController()
                      .delete()
                      .lineIdQuery(line.getLineId())
                      .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
            });
    for (int i = 0; i < 8; i++) {
      getAllocatedOnuIds(port, String.valueOf(i)).stream()
              .filter(onuId -> onuId.getId() > 1008).forEach(onu -> {
        accessLineResourceInventory.allocatedOnuIdController()
                .deleteAllocatedOnuId()
                .idQuery(onu.getId())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
      });
    }
  }

  @Step("Check absence of assigned lines, subscriber profiles")
  public void checkDecommissioningPreconditions(PortProvisioning port) {
    List<AccessLineDto> accessLines = getAccessLinesByPort(port);

    assertEquals(accessLines.stream()
            .filter(line -> line.getStatus().equals(AccessLineStatus.ASSIGNED)).count(), 0, "Assigned lines count:");
    accessLines.forEach(line -> {
      Assert.assertNull(line.getSubscriberNetworkLineProfile(), "Subscriber network line profile is not null");
      if (line.getDefaultNeProfile() != null) {
        Assert.assertNull(line.getDefaultNeProfile().getSubscriberNeProfile(), "Subscriber NE profile is not null");
      }
    });
  }

  public List<ReferenceDto> getPhysicalResourceRef(PortProvisioning port) {
    List<ReferenceDto> physicalResourceRefs = accessLineResourceInventory
            .physicalResourceReferenceInternalController()
            .searchPhysicalResourceReference()
            .body(new SearchPhysicalResourceReferenceDto()
                    .endSz(port.getEndSz())
                    .slotNumber(port.getSlotNumber())
                    .portNumber(port.getPortNumber()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    return physicalResourceRefs;
  }

  public List<BackhaulIdDto> getBackHaulId(PortProvisioning port) {
    List<BackhaulIdDto> backhaulIds = accessLineResourceInventory
            .backhaulIdController()
            .searchBackhaulIds()
            .body(new SearchBackhaulIdDto()
                    .endSz(port.getEndSz())
                    .slotNumber(port.getSlotNumber())
                    .portNumber(port.getPortNumber()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    return backhaulIds;
  }

  @Step("Check physicalResourceRef and backHaul id absence")
  public void checkPhysicalResourceRefAndBackhaulIdAbsence(PortProvisioning port) {
    try {
      TimeoutBlock timeoutBlock = new TimeoutBlock(LATENCY_FOR_PORT_PROVISIONING); //set timeout in milliseconds
      Supplier<Boolean> PhysicalResourceReferenceAbsence = () -> getPhysicalResourceRef(port).size() == 0;
      timeoutBlock.addBlock(PhysicalResourceReferenceAbsence); // execute the runnable precondition
      Supplier<Boolean> BackHaulIdAbsence = () -> getBackHaulId(port).size() == 0;
      timeoutBlock.addBlock(BackHaulIdAbsence); // execute the runnable precondition
    } catch (Throwable e) {
      //catch the exception here . Which is block didn't execute within the time limit
    }
    assertEquals(getPhysicalResourceRef(port).size(), 0, "There is physicalResourceRef left");
    assertEquals(getBackHaulId(port).size(), 0, "Backhaul ids count");
  }

  public void checkPhysicalResourceRefAfterManualOltDecommissioning(OltDevice olt) {
    List<ReferenceDto> physicalResourceRefs = accessLineResourceInventory.physicalResourceReferenceInternalController().searchPhysicalResourceReference()
            .body(new SearchPhysicalResourceReferenceDto().endSz(olt.getEndsz()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)))
            .stream().collect(Collectors.toList());

    long numberOfPhysicalResourceRefs = physicalResourceRefs.stream().map(ReferenceDto::getPortType)
            .filter(PortType -> !(PortType.getValue()).equals(PortType.ETHERNET.toString())).count();

    assertEquals(numberOfPhysicalResourceRefs, 0, "There are PhysicalResourceRefs left");
  }

  public void checkPhysicalResourceRefAfterAutoOltDecommissioning(OltDevice olt) {
    long physicalResourceRefs = accessLineResourceInventory.physicalResourceReferenceInternalController().searchPhysicalResourceReference()
            .body(new SearchPhysicalResourceReferenceDto().endSz(olt.getEndsz()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)))
            .stream().collect(Collectors.toList()).size();

    assertEquals(physicalResourceRefs, 0, "There are PhysicalResourceRefs left");
  }

  @Step("Get list of access lines on the specified port")
  public List<AccessLineDto> getAccessLinesByPort(PortProvisioning port) {
    return accessLineResourceInventory
            .accessLineController()
            .searchAccessLines()
            .body(new SearchAccessLineDto()
                    .endSz(port.getEndSz())
                    .slotNumber(port.getSlotNumber())
                    .portNumber(port.getPortNumber()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
  }

  @Step("Get access line by lineId")
  public List<AccessLineDto> getAccessLinesByLineId(String lineId) {
    return accessLineResourceInventory
            .accessLineController()
            .searchAccessLines()
            .body(new SearchAccessLineDto()
                    .lineId(lineId))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
  }

  @Step("Get list of lineIds in the pool for the specified port")
  public List<LineIdDto> getLineIdPool(PortProvisioning port) {
    return accessLineResourceInventory.lineIdController().searchLineIds().body(
            new SearchLineIdDto()
                    .endSz(port.getEndSz())
                    .slotNumber(port.getSlotNumber())
                    .portNumber(port.getPortNumber()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
  }

  @Step("Get list of homeIds in the pool for the specified port")
  public List<HomeIdDto> getHomeIdPool(PortProvisioning port) {
    return accessLineResourceInventory.homeIdController().searchHomeIds().body(
            new SearchHomeIdDto()
                    .endSz(port.getEndSz())
                    .slotNumber(port.getSlotNumber())
                    .portNumber(port.getPortNumber()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
  }

  @Step("Check results after provisioning lineId pool, homeId pool, created wg lines, default ne profiles, default nl profiles")
  public void checkProvisioningResults(PortProvisioning port) {
    try {
      TimeoutBlock timeoutBlock = new TimeoutBlock(LATENCY_FOR_PORT_PROVISIONING); //set timeout in milliseconds
      timeoutBlock.setTimeoutInterval(15000);
      Supplier<Boolean> checkProvisioning = () -> getAccessLinesByPort(port).size() == port.getAccessLinesCount();
      timeoutBlock.addBlock(checkProvisioning); // execute the runnable precondition
    } catch (Throwable e) {
      //catch the exception here . Which is block didn't execute within the time limit
    }

    List<AccessLineDto> accessLinesAfterProvisioning = getAccessLinesByPort(port);
    long countDefaultNEProfileActive = accessLinesAfterProvisioning.stream().map(AccessLineDto::getDefaultNeProfile)
            .filter(Objects::nonNull).filter(defaultNeProfile -> Objects.requireNonNull(defaultNeProfile.getState()).getValue()
                    .equals(STATUS_ACTIVE))
            .count();

    long countDefaultNetworkLineProfileActive = accessLinesAfterProvisioning.stream().map(AccessLineDto::getDefaultNetworkLineProfile)
            .filter(Objects::nonNull).filter(defaultNetworkLineProfile -> Objects.requireNonNull(defaultNetworkLineProfile
                    .getState()).getValue()
                    .equals(STATUS_ACTIVE))
            .count();

    long countAccessLinesWG = accessLinesAfterProvisioning.stream().filter(Objects::nonNull)
            .filter(accessLine -> Objects.requireNonNull(accessLine.getStatus()).getValue()
                    .equals(STATUS_WALLED_GARDEN))
            .count();

    assertEquals(getLineIdPool(port).size(), port.getLineIdPool().intValue());
    assertEquals(getHomeIdPool(port).size(), port.getHomeIdPool().intValue());
    assertEquals(countDefaultNetworkLineProfileActive, port.getDefaultNetworkLineProfilesActive().intValue());
    assertEquals(countDefaultNEProfileActive, port.getDefaultNEProfilesActive().intValue());
    assertEquals(countAccessLinesWG, port.getAccessLinesWG().intValue());
  }

  private List<AllocatedOnuIdDto> getAllocatedOnuIds(PortProvisioning port, String portNumber) {
    List<Integer> onuIds = accessLineResourceInventory.allocatedOnuIdController().searchAllocatedOnuId()
            .body(new SearchAllocatedOnuIdDto()
                    .oltEndSz(port.getEndSz())
                    .portNumber(portNumber)
                    .slotNumber(port.getSlotNumber()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

    List<AllocatedOnuIdDto> onuIdDtos = onuIds.stream().map(onuId -> accessLineResourceInventory.allocatedOnuIdController().findFirstAllocatedOnuId()
            .body(new SearchAllocatedOnuIdDto()
                    .onuId(onuId)
                    .oltEndSz(port.getEndSz())
                    .portNumber(portNumber)
                    .slotNumber(port.getSlotNumber()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200))))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    return onuIdDtos;

  }

  @Step("Get homeID from pool by port")
  public String getHomeIdByPort(AccessLine accessLine) {
    List<HomeIdDto> homeIdPool = accessLineResourceInventory.homeIdController().searchHomeIds().body(new SearchHomeIdDto()
            .endSz(accessLine.getOltDevice().getEndsz())
            .slotNumber(accessLine.getSlotNumber())
            .portNumber(accessLine.getPortNumber()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    assertEquals(homeIdPool.size(), 32, "Home ids in a pool count");
    //Assert.assertEquals(homeIdPool.get(0).getStatus(), HomeIdDto.StatusEnum.FREE);
    return homeIdPool.get(0).getHomeId();
  }

  @Step("Get homeID state")
  public HomeIdStatus getHomeIdStateByHomeId(String homeId) {
    List<HomeIdDto> homeIdPool = accessLineResourceInventory.homeIdController()
            .searchHomeIds()
            .body(new SearchHomeIdDto()
                    .homeId(homeId))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    assertNotNull(homeIdPool.get(0), "HomeId is not found");
    return homeIdPool.get(0).getStatus();
  }

  @Step("Get access line state by LineId")
  public AccessLineStatus getAccessLineStateByLineId(String lineId) {
    List<AccessLineDto> line = accessLineResourceInventory.accessLineController()
            .searchAccessLines()
            .body(new SearchAccessLineDto()
                    .lineId(lineId))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    assertNotNull(line.get(0), "Access line is not found");
    return line.get(0).getStatus();
  }

  @Step("Get lineId state by LineId")
  public LineIdStatus getLineIdStateByLineId(String lineId) {
    List<LineIdDto> lineIdPool = accessLineResourceInventory.lineIdController()
            .searchLineIds()
            .body(new SearchLineIdDto().lineId(lineId))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    assertNotNull(lineIdPool.get(0), "lineId is not found in pool");
    return lineIdPool.get(0).getStatus();
  }

  @Step("Get subscriber NE profile by LineId")
  public SubscriberNeProfileDto getSubscriberNEProfile(String lineId) {
    List<AccessLineDto> line = accessLineResourceInventory.accessLineController()
            .searchAccessLines()
            .body(new SearchAccessLineDto()
                    .lineId(lineId))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    assertNotNull(line.get(0).getDefaultNeProfile(), "Default NE profile is null");
    SubscriberNeProfileDto subscriberNeProfile = line.get(0).getDefaultNeProfile().getSubscriberNeProfile();
    return subscriberNeProfile;
  }

  @Step("Get subscriber network line profile by LineId")
  public SubscriberNetworkLineProfileDto getSubscriberNLProfile(String lineId) {
    List<AccessLineDto> line = accessLineResourceInventory.accessLineController()
            .searchAccessLines()
            .body(new SearchAccessLineDto()
                    .lineId(lineId))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    assertNotNull(line.get(0).getSubscriberNetworkLineProfile(), "Subscriber NL profile is null");
    return line.get(0).getSubscriberNetworkLineProfile();
  }

  @Step("Get AccessLine entities by LineId")
  public List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_12_0.client.model.AccessLine> getAccessLineEntitiesByLineId(String lineId) {
    return accessLineResourceInventory
            .accessLineControllerExternal().listAccessLine().lineIdQuery(lineId)
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

  }

  @Step("Get AccessLine entities by oltEndSz, slot, port")
  public List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_12_0.client.model.AccessLine> getAccessLineEntitiesByOlt(int limit, String EndSz, String slot, String port) {
    return accessLineResourceInventory
            .accessLineControllerExternal()
            .listAccessLine()
            .limitQuery(limit)
            .portReferencesOltDownlinkPortReferenceEndSZQuery(EndSz)
            .portReferencesOltDownlinkPortReferenceSlotNameQuery(slot)
            .portReferencesOltDownlinkPortReferencePortNameQuery(port)
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

  }

  @Step("Get AccessLine entities by dpuEndSz, port")
  public List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_12_0.client.model.AccessLine> getAccessLineEntitiesByDpu(String dpuEndSz, String port) {
    return accessLineResourceInventory
            .accessLineControllerExternal()
            .listAccessLine()
            .portReferencesDpuDownlinkPortReferenceEndSZQuery(dpuEndSz)
            .portReferencesDpuDownlinkPortReferencePortNameQuery(port)
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
  }
}