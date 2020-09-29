package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.helpers.osr.logs.TimeoutBlock;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.internal.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.internal.client.model.*;
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

public class AccessLineRiRobot {
    private static final Integer LATENCY_FOR_PORT_PROVISIONING = 200_000;

    private ApiClient accessLineResourceInventory = new AccessLineResourceInventoryClient().getClient();

    @Step("Clear database with test data")
    public void clearDatabase() {
        accessLineResourceInventory.fillDatabase().deleteDatabase().execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Fill database with test data as a part of OLT Commissioning process emulation")
    public void fillDatabaseForOltCommissioning() {
        accessLineResourceInventory.fillDatabase().fillDatabaseForOltCommissioning().execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Fill database with test data as a part of DPU Preprovisioning process emulation")
    public void fillDatabaseForDpuPreprovisioning() {
        accessLineResourceInventory.fillDatabase().fillDatabaseForDpuPreprovisioning().execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Check home id count for port")
    public void checkHomeIdsCount(PortProvisioning port) {
        List<HomeIdDto> homeIds = accessLineResourceInventory.homeIdInternalController().searchHomeIds().body(new SearchHomeIdDto()
                .endSz(port.getEndSz())
                .slotNumber(port.getSlotNumber())
                .portNumber(port.getPortNumber()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertEquals(homeIds.size(), port.getHomeIdPool().intValue(), "Home ids count");
    }

    @Step("Check line id count for port")
    public void checkLineIdsCount(PortProvisioning port) {
        List<LineIdDto> lineIds = accessLineResourceInventory.lineIdController().searchLineIds().body(new SearchLineIdDto()
                .endSz(port.getEndSz())
                .slotNumber(port.getSlotNumber())
                .portNumber(port.getPortNumber()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertEquals(lineIds.size(), port.getLineIdPool().intValue(), "Line ids count");
    }

    @Step("Check access lines parameters of port template (lines count and wg lines count, count of default NE and NetworkLine profiles)")
    public void checkPortParametersForLines(PortProvisioning port) {
        List<AccessLineDto> accessLines = getAccessLines(port);
        Assert.assertEquals(accessLines.size(), port.getAccessLinesCount().intValue(),
                "Access lines count");

        long countDefaultNEProfileActive = accessLines.stream().map(AccessLineDto::getDefaultNeProfile)
                .filter(defaultNeProfile -> defaultNeProfile != null && defaultNeProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countDefaultNetworkLineProfileActive = accessLines.stream().map(AccessLineDto::getDefaultNetworkLineProfile)
                .filter(defaultNetworkLineProfile -> defaultNetworkLineProfile != null && defaultNetworkLineProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countAccessLinesWG = accessLines.stream()
                .filter(accessLine -> accessLine.getStatus().getValue().equals(STATUS_WALLED_GARDEN)).count();

        Assert.assertEquals(countDefaultNetworkLineProfileActive, port.getDefaultNetworkLineProfilesActive().intValue(),
                "Default Network Line profile count");
        Assert.assertEquals(countDefaultNEProfileActive, port.getDefaultNEProfilesActive().intValue(),
                "Default NE profile count");
        Assert.assertEquals(countAccessLinesWG, port.getAccessLinesWG().intValue(),
                "WG access lines count");
    }

    @Step("Check assigned access lines count of port template")
    public void checkPortParametersForAssignedLines(PortProvisioning port) {
        List<AccessLineDto> accessLines = getAccessLines(port);
        Assert.assertEquals(accessLines.size(), port.getAccessLinesCount().intValue(),
                "Access lines count");

        long countAssignedAccessLines = accessLines.stream()
                .filter(accessLine -> accessLine.getStatus().getValue().equals(AccessLineDto.StatusEnum.ASSIGNED.getValue())).count();
        Assert.assertEquals(countAssignedAccessLines,
                port.getAccessLinesCount() - port.getAccessLinesWG(),
                "Assigned access lines count");
    }

    @Step("Check A4 specific parameters (NSP ref and phys ref exist, A4 prod platform")
    public void checkA4LineParameters(PortProvisioning port) {
        List<AccessLineDto> accessLines = getAccessLines(port);
        Assert.assertEquals(accessLines.size(), port.getAccessLinesCount().intValue(), "Access lines count");

        AccessLineDto accessLine = accessLines.get(0);

        Assert.assertNotNull(accessLine.getReference(), "Reference");
        Assert.assertEquals(accessLine.getProductionPlatform(), AccessLineDto.ProductionPlatformEnum.A4, "Production platform");
        Assert.assertNotNull(accessLine.getNetworkServiceProfileReference(), "NSP ref");
    }

    @Step("Check FTTB AccessLines (FTTB_NE_Profile, Default_NetworkLine_Profile")
    public void checkFttbLineParameters(PortProvisioning port, int numberOfAccessLinesForProvisioning) {
        try {
            TimeoutBlock timeoutBlock = new TimeoutBlock(LATENCY_FOR_PORT_PROVISIONING); //set timeout in milliseconds
            Supplier<Boolean> checkFttbProvisioning = () -> getAccessLines(port).size() == port.getAccessLinesCount();
            timeoutBlock.addBlock(checkFttbProvisioning); // execute the runnable precondition
        } catch (Throwable e) {
            //catch the exception here . Which is block didn't execute within the time limit
        }

        List<AccessLineDto> accessLines = getAccessLines(port);
        Assert.assertEquals(accessLines.size(), port.getAccessLinesCount().intValue(), "AccessLines count");

        long countFttbNeOltStateActive = accessLines.stream().map(AccessLineDto::getFttbNeProfile)
                .filter(fttbNeProfile -> fttbNeProfile != null && FttbNeProfileDto.StateOltEnum.ACTIVE.equals(fttbNeProfile.getStateOlt())).count();

        long countFttbNeMosaicActive = accessLines.stream().map(AccessLineDto::getFttbNeProfile)
                .filter(fttbNeProfile -> fttbNeProfile != null && FttbNeProfileDto.StateMosaicEnum.ACTIVE.equals(fttbNeProfile.getStateMosaic())).count();

        long countDefaultNetworkLineProfilesActive = accessLines.stream().map(AccessLineDto::getDefaultNetworkLineProfile)
                .filter(defaultNetworkLineProfile -> defaultNetworkLineProfile != null && DefaultNetworkLineProfileDto.StateEnum.ACTIVE.equals(defaultNetworkLineProfile.getState())).count();

        long countAccessLinesWG = accessLines.stream()
                .filter(accessLine -> accessLine.getStatus().getValue().equals(STATUS_WALLED_GARDEN)).count();

        List<Integer> expectedOnuIdsList = IntStream.rangeClosed(1, numberOfAccessLinesForProvisioning)
                .boxed().collect(Collectors.toList());

        List<Integer> onuAccessIds = accessLines.stream().map(AccessLineDto::getFttbNeProfile).map(FttbNeProfileDto::getOnuAccessId).
                map(OnuAccessId::getOnuAccessId).sorted().collect(Collectors.toList());

        Assert.assertEquals(countFttbNeOltStateActive, port.getFttbNEProfilesActive().intValue(),
                "FTTB NE Profiles (Olt State) count is incorrect");
        Assert.assertEquals(countFttbNeMosaicActive, port.getFttbNEProfilesActive().intValue(),
                "FTTB NE Profiles (Mosaic State) count is incorrect");
        Assert.assertEquals(countDefaultNetworkLineProfilesActive, port.getDefaultNetworkLineProfilesActive().intValue(),
                "Default NetworkLine Profile count is incorrect");
        Assert.assertEquals(countAccessLinesWG, port.getAccessLinesWG().intValue(),
                "WG AccessLines count is incorrect");
        Assert.assertEquals(onuAccessIds, expectedOnuIdsList,
                "OnuAccessIds are incorrect");
    }

    @Step("Remove lines with id > 1008, change some port refs")
    public void prepareTestDataToDeprovisioning(PortProvisioning port) {
        // delete extra lines
        getAccessLines(port).stream()
                .filter(line -> line.getId() > 1008)
                .forEach(line -> {
                    accessLineResourceInventory.accessLineInternalController()
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
        List<AccessLineDto> accessLines = getAccessLines(port);

        Assert.assertEquals(accessLines.stream()
                .filter(line -> line.getStatus().equals(AccessLineDto.StatusEnum.ASSIGNED)).count(), 0, "Assigned lines count:");
        accessLines.forEach(line -> {
            Assert.assertNull(line.getSubscriberNetworkLineProfile(), "Subscriber network line profile is not null");
            if (line.getDefaultNeProfile() != null) {
                Assert.assertNull(line.getDefaultNeProfile().getSubscriberNeProfile(), "Subscriber NE profile is not null");
            }
        });
    }

    @Step("Check backHaul id absence")
    public void checkBackHaulIdAbsence(PortProvisioning port) {
        List<BackhaulIdDto> backhaulIds = accessLineResourceInventory
                .backhaulIdController()
                .searchBackhaulIds()
                .body(new SearchBackhaulIdDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber())
                        .portNumber(port.getPortNumber()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertEquals(backhaulIds.size(), 0, "Backhaul ids count");
    }

    @Step("Get list of access lines on the specified port")
    public List<AccessLineDto> getAccessLines(PortProvisioning port) {
        return accessLineResourceInventory
                .accessLineInternalController()
                .searchAccessLines()
                .body(new SearchAccessLineDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber())
                        .portNumber(port.getPortNumber()))
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
        return accessLineResourceInventory.homeIdInternalController().searchHomeIds().body(
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
            Supplier<Boolean> checkProvisioning = () -> getAccessLines(port).size() == port.getAccessLinesCount();
            timeoutBlock.addBlock(checkProvisioning); // execute the runnable precondition
        } catch (Throwable e) {
            //catch the exception here . Which is block didn't execute within the time limit
        }

        List<AccessLineDto> accessLinesAfterProvisioning = getAccessLines(port);
        long countDefaultNEProfileActive = accessLinesAfterProvisioning.stream().map(AccessLineDto::getDefaultNeProfile)
                .filter(Objects::nonNull).filter(defaultNeProfile -> defaultNeProfile.getState().getValue()
                        .equals(STATUS_ACTIVE))
                .count();

        long countDefaultNetworkLineProfileActive = accessLinesAfterProvisioning.stream().map(AccessLineDto::getDefaultNetworkLineProfile)
                .filter(Objects::nonNull).filter(defaultNetworkLineProfile -> defaultNetworkLineProfile
                        .getState().getValue()
                        .equals(STATUS_ACTIVE))
                .count();

        long countAccessLinesWG = accessLinesAfterProvisioning.stream().filter(Objects::nonNull)
                .filter(accessLine -> accessLine.getStatus().getValue()
                        .equals(STATUS_WALLED_GARDEN))
                .count();

        Assert.assertEquals(getLineIdPool(port).size(), port.getLineIdPool().intValue());
        Assert.assertEquals(getHomeIdPool(port).size(), port.getHomeIdPool().intValue());
        Assert.assertEquals(countDefaultNetworkLineProfileActive, port.getDefaultNetworkLineProfilesActive().intValue());
        Assert.assertEquals(countDefaultNEProfileActive, port.getDefaultNEProfilesActive().intValue());
        Assert.assertEquals(countAccessLinesWG, port.getAccessLinesWG().intValue());
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
        List<HomeIdDto> homeIdPool = accessLineResourceInventory.homeIdInternalController().searchHomeIds().body(new SearchHomeIdDto()
                .endSz(accessLine.getOltDevice().getEndsz())
                .slotNumber(accessLine.getSlotNumber())
                .portNumber(accessLine.getPortNumber()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertEquals(homeIdPool.size(), 32, "Home ids in a pool count");
        Assert.assertEquals(homeIdPool.get(0).getStatus(), HomeIdDto.StatusEnum.FREE);
        return homeIdPool.get(0).getHomeId();
    }

    @Step("Get homeID state")
    public HomeIdDto.StatusEnum getHomeIdStateByHomeId(String homeId) {
        List<HomeIdDto> homeIdPool = accessLineResourceInventory.homeIdInternalController()
                .searchHomeIds()
                .body(new SearchHomeIdDto()
                        .homeId(homeId))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertNotNull(homeIdPool.get(0), "HomeId is not found");
        return homeIdPool.get(0).getStatus();
    }

    @Step("Get access line state by LineId")
    public AccessLineDto.StatusEnum getAccessLineStateByLineId(String lineId) {
        List<AccessLineDto> line = accessLineResourceInventory.accessLineInternalController()
                .searchAccessLines()
                .body(new SearchAccessLineDto()
                        .lineId(lineId))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertNotNull(line.get(0), "Access line is not found");
        return line.get(0).getStatus();
    }

    @Step("Get lineId state by LineId")
    public LineIdDto.StatusEnum getLineIdStateByLineId(String lineId) {
        List<LineIdDto> lineIdPool = accessLineResourceInventory.lineIdController()
                .searchLineIds()
                .body(new SearchLineIdDto().lineId(lineId))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertNotNull(lineIdPool.get(0), "lineId is not found in pool");
        return lineIdPool.get(0).getStatus();
    }

    @Step("Get subscriber NE profile by LineId")
    public SubscriberNeProfileDto getSubscriberNEProfile(String lineId) {
        List<AccessLineDto> line = accessLineResourceInventory.accessLineInternalController()
                .searchAccessLines()
                .body(new SearchAccessLineDto()
                        .lineId(lineId))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertNotNull(line.get(0).getDefaultNeProfile(), "Default NE profile is null");
        SubscriberNeProfileDto subscriberNeProfile = line.get(0).getDefaultNeProfile().getSubscriberNeProfile();
        Assert.assertNotNull(subscriberNeProfile, "Subscriber NE profile is null");
        return subscriberNeProfile;
    }
}
