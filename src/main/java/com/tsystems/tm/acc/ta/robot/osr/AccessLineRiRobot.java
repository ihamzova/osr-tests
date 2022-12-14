package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryFillDbClient;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.helpers.osr.logs.TimeoutBlock;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.*;
import io.qameta.allure.Owner;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.STATUS_WALLED_GARDEN;
import static com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.BackhaulStatus.CONFIGURED;
import static com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.PortType.*;
import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;
import static org.testng.Assert.*;

public class AccessLineRiRobot {
    private static final Integer LATENCY_FOR_PORT_PROVISIONING = 500_000;
    private static final Integer LATENCY_FOR_ACCESSLINE_DEPROVISIONING = 30_000;
    private static final Integer LATENCY_FOR_RECONFIGURATION = 80_000;

    private final AccessLineResourceInventoryClient accessLineResourceInventory = new AccessLineResourceInventoryClient();
    private final AccessLineResourceInventoryFillDbClient accessLineResourceInventoryFillDbClient = new AccessLineResourceInventoryFillDbClient();

    @Step("Clear Al RI db")
    public void clearDatabase() {
        accessLineResourceInventoryFillDbClient
                .getClient()
                .fillDatabase()
                .truncateDatabase()
                .execute(checkStatus(HTTP_CODE_OK_200));
    }

    public void clearDatabaseByOlt(String endSz) {
        accessLineResourceInventoryFillDbClient
                .getClient()
                .fillDatabase()
                .removeOlt()
                .END_SZQuery(endSz)
                .execute(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Fill database with test data as a part of OLT Commissioning process emulation, v2, default values")
    public void fillDatabaseForOltCommissioningV2(int HOME_ID_SEQ, int LINE_ID_SEQ) {
        accessLineResourceInventoryFillDbClient.getClient().fillDatabase()
                .fillDatabaseForOltCommissioningWithDpu()
                .HOME_ID_SEQQuery(HOME_ID_SEQ)
                .LINE_ID_SEQQuery(LINE_ID_SEQ)
                .execute(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Fill database with test data as a part of OLT Commissioning process emulation, v2")
    public void fillDatabaseForOltCommissioningV2WithOlt(int HOME_ID_SEQ, int LINE_ID_SEQ, String oltEndSz, String oltSlot) {
        accessLineResourceInventoryFillDbClient.getClient().fillDatabase()
                .fillDatabaseForOltCommissioningWithDpu()
                .HOME_ID_SEQQuery(HOME_ID_SEQ)
                .LINE_ID_SEQQuery(LINE_ID_SEQ)
                .SLOT_NUMBER1Query(oltSlot)
                .END_SZQuery(oltEndSz)
                .execute(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Fill database with test data as a part of OLT Commissioning process emulation, v2")
    public void fillDatabaseForOltCommissioningWithDpu(Boolean addDpu, AccessTransmissionMedium accessTransmissionMedium, int HOME_ID_SEQ, int LINE_ID_SEQ,
                                                       String oltEndSz, String dpuEndSz, String oltSlotWithDpu, String oltPortWithDpu) {
        accessLineResourceInventoryFillDbClient.getClient().fillDatabase()
                .fillDatabaseForOltCommissioningWithDpu()
                .HOME_ID_SEQQuery(HOME_ID_SEQ)
                .LINE_ID_SEQQuery(LINE_ID_SEQ)
                .ADD_DPUQuery(addDpu)
                .ACCESS_TRANSMISSION_MEDIUMQuery(accessTransmissionMedium)
                .END_SZQuery(oltEndSz)
                .DPU_ENDSZQuery(dpuEndSz)
                .OLT_SLOT_WITH_DPUQuery(oltSlotWithDpu)
                .OLT_PORT_WITH_DPUQuery(oltPortWithDpu)
                .execute(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Fill database with test data as a part of Adtran OLT Commissioning process emulation")
    public void fillDatabaseForAdtranOltCommissioning() {
        accessLineResourceInventoryFillDbClient.getClient().fillDatabase().fillDatabaseWithAdtranOlt()
                .HOME_ID_SEQQuery(1)
                .LINE_ID_SEQQuery(1)
                .execute(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Fill database with test data for Network Switching")
    public void fillDatabaseForNetworkSwitching(PortProvisioning sourcePort, PortProvisioning targetPort) {
        accessLineResourceInventoryFillDbClient.getClient().fillDatabase().fillDatabaseForSwitchingPreparation()
                .HOME_ID_SEQQuery(1)
                .LINE_ID_SEQQuery(1)
                .END_SZQuery(sourcePort.getEndSz())
                .SLOT_NUMBER1Query(sourcePort.getSlotNumber())
                .END_SZ_2Query(targetPort.getEndSz())
                .SLOT_NUMBER2Query(targetPort.getSlotNumber())
                .execute(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Fill database with test data as a part of DPU Preprovisioning process emulation, v2")
    public void fillDatabaseForDpuPreprovisioningV2(int HOME_ID_SEQ, int LINE_ID_SEQ, DpuDevice dpuDevice, PortProvisioning oltDevice) {
        if (oltDevice.getSlotNumber() == null) oltDevice.setSlotNumber("null");
        accessLineResourceInventoryFillDbClient.
                getClient()
                .fillDatabase()
                .fillDatabaseForDpuPreprovisioningV4()
                .HOME_ID_SEQQuery(HOME_ID_SEQ)
                .LINE_ID_SEQQuery(LINE_ID_SEQ)
                .OLT_END_SZQuery(oltDevice.getEndSz())
                .OLT_SLOT_WITH_DPUQuery(oltDevice.getSlotNumber())
                .OLT_PORT_WITH_DPUQuery(oltDevice.getPortNumber())
                .DPU_END_SZQuery(dpuDevice.getEndsz())
                .execute(checkStatus(HTTP_CODE_OK_200));
        if (oltDevice.getSlotNumber().equals("null")) oltDevice.setSlotNumber(null);
    }

    @Step("Check results after (de)provisioning: AccessLines, default ne profiles, default nl profiles")
    public void checkFtthPortParameters(PortProvisioning port) {
        try {
            TimeoutBlock timeoutBlock = new TimeoutBlock(LATENCY_FOR_PORT_PROVISIONING); //set timeout in milliseconds
            timeoutBlock.setTimeoutInterval(15000);
            Supplier<Boolean> checkProvisioning = () ->
                    getAccessLinesByPort(port).stream().filter(accessLine ->
                                    accessLine.getStatus().equals(AccessLineStatus.WALLED_GARDEN))
                            .collect(Collectors.toList()).size()
                            == port.getAccessLinesWG();
            timeoutBlock.addBlock(checkProvisioning); // execute the runnable precondition
        } catch (Exception e) {
            //catch the exception here . Which is block didn't execute within the time limit
        }

        List<AccessLineDto> accessLinesAfterProvisioning = getAccessLinesByPort(port);
        long countAccessLinesWG = accessLinesAfterProvisioning.stream().filter(Objects::nonNull)
                .filter(accessLine -> Objects.requireNonNull(accessLine.getStatus()).getValue()
                        .equals(STATUS_WALLED_GARDEN))
                .count();

        assertEquals(countAccessLinesWG, port.getAccessLinesWG().intValue());
        checkIdPools(port);
    }

    @Step("Check AccessLines after FTTB Provisioning")
    public void checkAccessLinesAfterFttbProvisioning(PortProvisioning oltPort,
                                                      DpuDevice dpuDevice,
                                                      FttbNeProfile expectedFttbNeProfile,
                                                      DefaultNetworkLineProfile expectedDefaultNetworkLineProfile,
                                                      int numberOfAccessLines) {

        checkFttbLineParameters(oltPort, numberOfAccessLines);
        checkAccessTransmissionMedium(dpuDevice, numberOfAccessLines);
        checkDefaultNetworkLineProfiles(oltPort, expectedDefaultNetworkLineProfile, numberOfAccessLines);
        checkFttbNeProfiles(oltPort, expectedFttbNeProfile, numberOfAccessLines);
        checkHomeIdsCount(oltPort);
    }

    @Step("Check pools")
    public void checkIdPools(PortProvisioning port) {
        checkHomeIdsCount(port);
        checkBackhaulIdCount(port);
    }

    @Step("Check PhysicalResourceRefs for FTTH")
    public void checkPhysicalResourceRefCountFtth(PortProvisioning port, int expectedPonPortsCount, int expectedEthernetPortsCount) {
        try {
            TimeoutBlock timeoutBlock = new TimeoutBlock(LATENCY_FOR_PORT_PROVISIONING); //set timeout in milliseconds
            timeoutBlock.setTimeoutInterval(5000);
            Supplier<Boolean> checkEthernetPorts = () -> getEthernetPorts(port).size() == expectedEthernetPortsCount;
            timeoutBlock.addBlock(checkEthernetPorts); // execute the runnable precondition
        } catch (Exception e) {
            //catch the exception here . Which is block didn't execute within the time limit
        }

        assertEquals(getPonPorts(port).size(), expectedPonPortsCount);
        assertEquals(getEthernetPorts(port).size(), expectedEthernetPortsCount);
    }

    @Step("Check PhysicalResourceRefs for A4 FTTH")
    public void checkPhysicalResourceRefCountA4Ftth(PortProvisioning port, int expectedGponPortsCount) {

        try {
            TimeoutBlock timeoutBlock = new TimeoutBlock(LATENCY_FOR_PORT_PROVISIONING); //set timeout in milliseconds
            timeoutBlock.setTimeoutInterval(5000);
            Supplier<Boolean> checkGponPorts = () -> getGponPorts(port).size() == expectedGponPortsCount;
            timeoutBlock.addBlock(checkGponPorts); // execute the runnable precondition
        } catch (Exception e) {
            //catch the exception here . Which is block didn't execute within the time limit
        }

        assertEquals(getGponPorts(port).size(), expectedGponPortsCount);
        assertEquals(getPonPorts(port).size(), 0);
        assertEquals(getEthernetPorts(port).size(), 0);
    }

    @Step("Check PhysicalResourceRefs for A4 FTTB")
    public void checkPhysicalResourceRefCountA4Fttb(DpuDevice dpuDevice, PortProvisioning port, int expectedGfastPortsCount) {

        try {
            TimeoutBlock timeoutBlock = new TimeoutBlock(LATENCY_FOR_PORT_PROVISIONING); //set timeout in milliseconds
            timeoutBlock.setTimeoutInterval(5000);
            Supplier<Boolean> checkGfastPorts = () -> getGfastPorts(dpuDevice).size() == expectedGfastPortsCount;
            timeoutBlock.addBlock(checkGfastPorts); // execute the runnable precondition
        } catch (Exception e) {
            //catch the exception here . Which is block didn't execute within the time limit
        }

        assertEquals(getGfastPorts(dpuDevice).size(), expectedGfastPortsCount);
        assertEquals(getPonPorts(port).size(), 0);
        assertEquals(getEthernetPorts(port).size(), 0);
    }

    @Step("Check PhysicalResourceRefs for FTTB")
    public void checkPhysicalResourceRefCountFttb(DpuDevice dpuDevice,
                                                  PortProvisioning oltPort,
                                                  int expectedGfastPortsCount,
                                                  int expectedEthernetPortsCount,
                                                  int expectedPonPortsCount) {
        try {
            TimeoutBlock timeoutBlock = new TimeoutBlock(LATENCY_FOR_PORT_PROVISIONING); //set timeout in milliseconds
            timeoutBlock.setTimeoutInterval(5000);
            Supplier<Boolean> checkEthernetPorts = () -> getEthernetPorts(oltPort).size() == expectedEthernetPortsCount;
            timeoutBlock.addBlock(checkEthernetPorts); // execute the runnable precondition
        } catch (Exception e) {
            //catch the exception here . Which is block didn't execute within the time limit
        }

        assertEquals(getGfastPorts(dpuDevice).size(), expectedGfastPortsCount);
        assertEquals(getEthernetPorts(oltPort).size(), expectedEthernetPortsCount);
        assertEquals(getPonPorts(oltPort).size(), expectedPonPortsCount);
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

    @Step("Check A4 OLT_BNG specific parameters (NSP ref and phys ref exist, A4 prod platform")
    public void checkA4LineParameters(PortProvisioning port, String tpRefUuid) {
        try {
            TimeoutBlock timeoutBlock = new TimeoutBlock(LATENCY_FOR_PORT_PROVISIONING); //set timeout in milliseconds
            Supplier<Boolean> checkA4PreProvisioning = () -> getAccessLinesByPort(port).size() == 1;
            timeoutBlock.addBlock(checkA4PreProvisioning); // execute the runnable precondition
        } catch (Exception e) {
            //catch the exception here . Which is block didn't execute within the time limit
        }

        List<AccessLineDto> accessLines = getAccessLinesByPort(port);
        assertEquals(accessLines.size(), port.getAccessLinesCount().intValue(), "Access lines count");

        AccessLineDto accessLine = accessLines.get(0);

        assertEquals(accessLine.getStatus(), AccessLineStatus.WALLED_GARDEN, "AccessLine status");
        assertNotNull(accessLine.getNetworkServiceProfileReference(), "There is no NSP reference");
        assertEquals(accessLine.getNetworkServiceProfileReference().getTpRef(), tpRefUuid);
        assertNotNull(accessLine.getReference(), "Reference");
        assertEquals(accessLine.getReference().getEndSz(), port.getEndSz());
        assertEquals(accessLine.getReference().getSlotNumber(), port.getSlotNumber());
        assertEquals(accessLine.getReference().getPortNumber(), port.getPortNumber());
        assertEquals(accessLine.getReference().getPortType(), GPON);
        assertEquals(accessLine.getProductionPlatform(), AccessLineProductionPlatform.A4, "Production platform");
    }

    @Step("Check A4 FTTB specific parameters")
    public void checkA4FttbLineParameters(PortProvisioning port, String tpRefUuid) {
        try {
            TimeoutBlock timeoutBlock = new TimeoutBlock(LATENCY_FOR_PORT_PROVISIONING); //set timeout in milliseconds
            Supplier<Boolean> checkA4FttbPreProvisioning = () -> getAccessLinesByGfastPort(port).stream().
                    filter(accessLine -> accessLine.getStatus().getValue().equals(STATUS_WALLED_GARDEN)).count() == 1;
            timeoutBlock.addBlock(checkA4FttbPreProvisioning); // execute the runnable precondition
        } catch (Exception e) {
            //catch the exception here . Which is block didn't execute within the time limit
        }

        List<AccessLineDto> accessLines = getAccessLinesByGfastPort(port);
        assertEquals(accessLines.size(), port.getAccessLinesCount().intValue(), "Access lines count");

        AccessLineDto accessLine = accessLines.get(0);

        assertEquals(accessLine.getStatus(), AccessLineStatus.WALLED_GARDEN, "AccessLine status");
        assertNotNull(accessLine.getNetworkServiceProfileReference(), "There is no NSP reference");
        assertEquals(accessLine.getNetworkServiceProfileReference().getTpRef(), tpRefUuid);
        assertNotNull(accessLine.getDpuReference(), "Reference");
        assertEquals(accessLine.getDpuReference().getEndSz(), port.getEndSz());
        assertEquals(accessLine.getDpuReference().getSlotNumber(), port.getSlotNumber());
        assertEquals(accessLine.getDpuReference().getPortNumber(), port.getPortNumber());
        assertEquals(accessLine.getDpuReference().getPortType(), GFAST);
        assertEquals(accessLine.getProductionPlatform(), AccessLineProductionPlatform.A4, "Production platform");
    }

    @Step("Check FTTB AccessLines (FTTB_NE_Profile, Default_NetworkLine_Profile")
    public void checkFttbLineParameters(PortProvisioning port, int numberOfAccessLinesForProvisioning) {
        try {
            TimeoutBlock timeoutBlock = new TimeoutBlock(LATENCY_FOR_PORT_PROVISIONING); //set timeout in milliseconds
            Supplier<Boolean> checkFttbProvisioning = () -> getAccessLinesByPort(port).stream()
                    .filter(accessLine -> accessLine.getStatus().getValue().equals(STATUS_WALLED_GARDEN)).count() == numberOfAccessLinesForProvisioning;
            timeoutBlock.addBlock(checkFttbProvisioning); // execute the runnable precondition
        } catch (Exception e) {
            //catch the exception here . Which is block didn't execute within the time limit
        }

        List<AccessLineDto> accessLines = getAccessLinesByPort(port);
        assertEquals(accessLines.size(), numberOfAccessLinesForProvisioning, "AccessLines count");

        long countAccessLinesWG = accessLines.stream()
                .filter(accessLine -> accessLine.getStatus().getValue().equals(STATUS_WALLED_GARDEN)).count();

        List<Integer> expectedOnuIdsList = IntStream.rangeClosed(1, numberOfAccessLinesForProvisioning)
                .boxed().collect(Collectors.toList());

        List<Integer> onuAccessIds = accessLines.stream().map(AccessLineDto::getFttbNeProfile).map(FttbNeProfileDto::getOnuAccessId).
                map(OnuAccessIdDto::getOnuAccessId).sorted().collect(Collectors.toList());

        assertEquals(countAccessLinesWG, numberOfAccessLinesForProvisioning,
                "WG AccessLines count is incorrect");
        assertEquals(onuAccessIds, expectedOnuIdsList,
                "OnuAccessIds are incorrect");
    }

    @Step("Check Default NE Profiles")
    public void checkDefaultNeProfiles(PortProvisioning port, DefaultNeProfile expectedDefaultNeProfile, int numberOfAccessLinesForProvisioning) {
        List<DefaultNeProfileDto> actualDefaultNeProfileDtos = getAccessLinesByPort(port)
                .stream().map(AccessLineDto::getDefaultNeProfile).collect(Collectors.toList());
        assertEquals(actualDefaultNeProfileDtos.size(), numberOfAccessLinesForProvisioning, "Default NE Profiles count is incorrect");
        List<DefaultNeProfile> actualDefaultNeProfiles =
                actualDefaultNeProfileDtos.stream().map(AccessLineRiRobot::mapToDefaultNeProfile).collect(Collectors.toList());
        assertTrue(actualDefaultNeProfiles.stream().allMatch(defaultNeProfile -> defaultNeProfile.equals(expectedDefaultNeProfile)),
                "Default NE Profiles are incorrect");
    }

    @Step("Check Default NetworkLine Profiles")
    public void checkDefaultNetworkLineProfiles(PortProvisioning port, DefaultNetworkLineProfile expectedDefaultNLineProfile, int numberOfAccessLinesForProvisioning) {
        List<DefaultNetworkLineProfileDto> actualDefaultNLProfileDtos = getAccessLinesByPort(port)
                .stream().map(AccessLineDto::getDefaultNetworkLineProfile).collect(Collectors.toList());
        assertEquals(actualDefaultNLProfileDtos.size(), numberOfAccessLinesForProvisioning, "Default NetworkLine Profiles count is incorrect");
        List<DefaultNetworkLineProfile> actualDefaultNLProfiles =
                actualDefaultNLProfileDtos.stream().map(AccessLineRiRobot::mapToNLProfile).collect(Collectors.toList());
        System.out.println("actualDefaultNLProfiles = " + actualDefaultNLProfiles);
        System.out.println("expectedDefaultNLineProfile = " + expectedDefaultNLineProfile);
        assertTrue(actualDefaultNLProfiles.stream().allMatch(defaultNetworkLineProfile -> defaultNetworkLineProfile.equals(expectedDefaultNLineProfile)),
                "Default NetworkLine Profiles are incorrect");
    }

    @Step("Check Default NetworkLine Profiles")
    public void checkDefaultNetworkLineProfiles(AccessLine accessLine, DefaultNetworkLineProfile expectedDefaultNLineProfile) {
        List<DefaultNetworkLineProfileDto> actualDefaultNLProfileDtos = getAccessLinesByLineId(accessLine.getLineId())
                .stream().map(AccessLineDto::getDefaultNetworkLineProfile).collect(Collectors.toList());
        List<DefaultNetworkLineProfile> actualDefaultNLProfiles =
                actualDefaultNLProfileDtos.stream().map(AccessLineRiRobot::mapToNLProfile).collect(Collectors.toList());
        assertTrue(actualDefaultNLProfiles.stream().allMatch(defaultNetworkLineProfile -> defaultNetworkLineProfile.equals(expectedDefaultNLineProfile)),
                "Default NetworkLine Profiles are incorrect");
    }

    @Step("Check Subscriber NetworkLine Profiles")
    public void checkSubscriberNetworkLineProfiles(AccessLine accessLine, SubscriberNetworkLineProfile expectedSubscriberNetworklineProfile) {
        List<SubscriberNetworkLineProfileDto> actualSubscriberProfileDtos = getAccessLinesByLineId(accessLine.getLineId())
                .stream().map(AccessLineDto::getSubscriberNetworkLineProfile).collect(Collectors.toList());
        List<SubscriberNetworkLineProfile> actualSubscriberProfiles =
                actualSubscriberProfileDtos.stream().map(AccessLineRiRobot::mapToSubscriberNetworkLineProfile).collect(Collectors.toList());
        assertTrue(actualSubscriberProfiles.stream().allMatch(subscriberNetworkLineProfile -> subscriberNetworkLineProfile.equals(expectedSubscriberNetworklineProfile)),
                "Subscriber NetworkLine Profiles are incorrect");
    }

    @Step("Check FTTB NE Profiles")
    public void checkFttbNeProfiles(PortProvisioning port, FttbNeProfile expectedFttbNeProfile, int numberOfAccessLinesForProvisioning) {
        List<FttbNeProfileDto> actualFttbNeProfileDtos = getAccessLinesByPort(port)
                .stream().map(AccessLineDto::getFttbNeProfile).collect(Collectors.toList());
        assertEquals(actualFttbNeProfileDtos.size(), numberOfAccessLinesForProvisioning, "FTTB NE Profiles count is incorrect");
        List<FttbNeProfile> actualFttbNeProfiles =
                actualFttbNeProfileDtos.stream().map(AccessLineRiRobot::mapToFttbNeProfile).collect(Collectors.toList());
        assertTrue(actualFttbNeProfiles.stream().allMatch(fttbNeProfile -> fttbNeProfile.equals(expectedFttbNeProfile)), "FTTB NE Profiles are incorrect");
    }

    @Step("Check L2BSA NSP Reference")
    public void checkL2bsaNspReference(PortProvisioning port, L2BsaNspReference expectedL2bsaNspReference) {
        List<L2BsaNspReferenceDto> actualL2bsaNspReferenceDtos = getAccessLinesByPort(port)
                .stream().map(AccessLineDto::getL2BsaNspReference).collect(Collectors.toList());
        assertEquals(actualL2bsaNspReferenceDtos.size(), 1, "L2bsaNspReferences count is incorrect");
        List<L2BsaNspReference> actualL2bsaNspReferences =
                actualL2bsaNspReferenceDtos.stream().map(AccessLineRiRobot::mapToL2BsaNspReference).collect(Collectors.toList());
        assertTrue(actualL2bsaNspReferences.stream().allMatch(l2BsaNspReference -> l2BsaNspReference.equals(expectedL2bsaNspReference)), "L2BsaNSPReference is incorrect");
    }

    @Step("Check L2BSA NSP Reference")
    public void checkL2bsaNspReference(AccessLine accessLine, L2BsaNspReference expectedL2bsaNspReference) {
        List<L2BsaNspReferenceDto> actualL2bsaNspReferenceDtos = getAccessLinesByLineId(accessLine.getLineId())
                .stream().map(AccessLineDto::getL2BsaNspReference).collect(Collectors.toList());
        List<L2BsaNspReference> actualL2bsaNspReferences =
                actualL2bsaNspReferenceDtos.stream().map(AccessLineRiRobot::mapToL2BsaNspReference).collect(Collectors.toList());
        assertTrue(actualL2bsaNspReferences.stream().allMatch(l2BsaNspReference -> l2BsaNspReference.equals(expectedL2bsaNspReference)), "L2BsaNSPReference is incorrect");
    }

    @Step("Check syncStatus after reconfiguration")
    public void checkReconfigurationResult(String lineId) {
        try {
            TimeoutBlock timeoutBlock = new TimeoutBlock(LATENCY_FOR_RECONFIGURATION); //set timeout in milliseconds
            timeoutBlock.setTimeoutInterval(2000);
            Supplier<Boolean> checkReconfigurationResult = () ->
                    getAccessLinesByLineId(lineId).get(0).getDefaultNetworkLineProfile().getSyncStatus() == null
                            && getAccessLinesByLineId(lineId).get(0).getSubscriberNetworkLineProfile().getSyncStatus() == null
                            && getAccessLinesByLineId(lineId).get(0).getDefaultNeProfile().getSyncStatus() == null
                            && getAccessLinesByLineId(lineId).get(0).getDefaultNeProfile().getSubscriberNeProfile().getSyncStatus() == null;

            timeoutBlock.addBlock(checkReconfigurationResult); // execute the runnable precondition
        } catch (Exception e) {
            //catch the exception here . Which is block didn't execute within the time limit
        }

        assertNull(getAccessLinesByLineId(lineId).get(0).getDefaultNeProfile().getSyncStatus(),
                "DefaultNeProfile syncStatus is incorrect");
        assertNull(getAccessLinesByLineId(lineId).get(0).getDefaultNetworkLineProfile().getSyncStatus(),
                "DefaultNetworkLineProfile syncStatus is incorrect");

        if (getAccessLinesByLineId(lineId).get(0).getDefaultNeProfile().getSubscriberNeProfile() != null) {
            assertNull(getAccessLinesByLineId(lineId).get(0).getDefaultNeProfile().getSubscriberNeProfile().getSyncStatus(),
                    "SubscriberNeProfile syncStatus is incorrect");
        }

        if (getAccessLinesByLineId(lineId).get(0).getSubscriberNetworkLineProfile() != null) {
            assertNull(getAccessLinesByLineId(lineId).get(0).getSubscriberNetworkLineProfile().getSyncStatus(),
                    "SubscriberNetworkLineProfile syncStatus is incorrect");
        }
    }

    @Step("Check partyId")
    public void checkPartyId(PortProvisioning port, Long expectedPartyId) {
        List<Long> actualPartyIds = getAccessLinesByPort(port)
                .stream().map(AccessLineDto::getResourceAssociation)
                .map(ResourceAssociationDto::getPartyId).collect(Collectors.toList());
        assertTrue(actualPartyIds.stream().allMatch(partyId -> partyId.equals(expectedPartyId)), "PartyId is incorrect");
    }

    @Step("Check LineId Prefix")
    public void checkLineIdPrefix(PortProvisioning port, String expectedPrefix) {
        List<String> actualPrefixes =
                getLineIdsByPort(port).stream().map(AccessLineRiRobot::getLineIdPrefix).collect(Collectors.toList());
        assertTrue(actualPrefixes.stream().allMatch(prefix -> prefix.equals(expectedPrefix)), "LineId prefixes are incorrect");
    }

    @Step("Remove lines with id > 1008, change some port refs")
    public void prepareTestDataToDeprovisioning(PortProvisioning port) {
        // delete extra lines
        List<AccessLineDto> accessLineIds = new ArrayList<AccessLineDto>();
        getAccessLinesByPort(port).stream()
                .filter(line -> line.getStatus() == AccessLineStatus.ASSIGNED)
                .forEach(line -> {
                    accessLineResourceInventory.getClient().accessLineController()
                            .delete()
                            .lineIdQuery(line.getLineId())
                            .execute(checkStatus(HTTP_CODE_OK_200));
                    accessLineIds.add(line);
                });
//        for (int i = 0; i < 8; i++) {
//            getAllocatedOnuIds(port, String.valueOf(i)).stream()
//                    .filter(onuId -> onuId.getId() > 1008).forEach(onu -> {
//                        accessLineResourceInventory.getClient().allocatedOnuIdController()
//                                .deleteAllocatedOnuId()
//                                .idQuery(onu.getId())
//                                .execute(checkStatus(HTTP_CODE_OK_200));
//                    });
//        }

        List<Integer> ontIds = accessLineIds.stream().map(accessLine -> accessLineResourceInventory.getClient().allocatedOnuIdController()
                        .searchAllocatedOnuId()
                        .body(new SearchAllocatedOnuIdDto()
                                .oltEndSz(accessLine.getReference().getEndSz())
                                .slotNumber(accessLine.getReference().getSlotNumber())
                                .portNumber(accessLine.getReference().getPortNumber())
                                .lineId(accessLine.getLineId()))
                        .executeAs(checkStatus(HTTP_CODE_OK_200)))
                .flatMap(List::stream).collect(Collectors.toList());

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

    @Step("Check PhysicalResourceRef after manual OLT Decommissioning")
    public void checkPhysicalResourceRefAfterManualOltDecommissioning(OltDevice olt) {
        List<ReferenceDto> physicalResourceRefs = accessLineResourceInventory.getClient().physicalResourceReferenceInternalController().searchPhysicalResourceReference()
                .body(new SearchPhysicalResourceReferenceDto().endSz(olt.getEndsz()))
                .executeAs(checkStatus(HTTP_CODE_OK_200))
                .stream().collect(Collectors.toList());

        long numberOfPhysicalResourceRefs = physicalResourceRefs.stream().map(ReferenceDto::getPortType)
                .filter(PortType -> !(PortType.getValue()).equals(ETHERNET.toString())).count();

        assertEquals(numberOfPhysicalResourceRefs, 0, "There are PhysicalResourceRefs left");
    }

    @Step("Check PhysicalResourceRef after Auto OLT Decommissioning")
    public void checkPhysicalResourceRefAfterAutoOltDecommissioning(OltDevice olt) {
        long physicalResourceRefs = accessLineResourceInventory.getClient().physicalResourceReferenceInternalController().searchPhysicalResourceReference()
                .body(new SearchPhysicalResourceReferenceDto().endSz(olt.getEndsz()))
                .executeAs(checkStatus(HTTP_CODE_OK_200))
                .stream().collect(Collectors.toList()).size();

        assertEquals(physicalResourceRefs, 0, "There are PhysicalResourceRefs left");
    }

    @Step("Check home id count for port")
    public void checkHomeIdsCount(PortProvisioning port) {
        assertEquals(getHomeIdPool(port).size(), port.getHomeIdPool().intValue(), "Home ids count");
    }

    @Step("Check BackhaulId count per port")
    public void checkBackhaulIdCount(PortProvisioning port) {
        assertEquals(getBackHaulId(port).stream().filter(backhaulId -> backhaulId.getStatus().getValue().equals(CONFIGURED.toString())).collect(Collectors.toList()).size(),
                port.getBackhaulId().intValue(), "Backhauld count is incorrect");
    }

    @Step("Check accessTransmissionMedium parameters")
    public void checkAccessTransmissionMedium(DpuDevice dpuDevice, int numberOfAccessLinesForProvisioning) {
        long accessTransmissionMedia = getGfastPorts(dpuDevice).stream().map(ReferenceDto::getAccessTransmissionMedium)
                .filter(accessTransmissionMedium -> accessTransmissionMedium.getValue()
                        .equals(dpuDevice.getAccessTransmissionMedium()))
                .count();
        assertEquals(accessTransmissionMedia, numberOfAccessLinesForProvisioning);
    }

    @Step("Get Pon Ports")
    public List<ReferenceDto> getPonPorts(PortProvisioning port) {
        List<ReferenceDto> ponPorts = accessLineResourceInventory.getClient()
                .physicalResourceReferenceInternalController()
                .searchPhysicalResourceReference()
                .body(new SearchPhysicalResourceReferenceDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber())
                        .portNumber(port.getPortNumber()))
                .executeAs(checkStatus(HTTP_CODE_OK_200));
        return ponPorts.stream().filter(ponPort -> ponPort.getPortType().getValue().equals(PON.toString())).collect(Collectors.toList());
    }

    @Step("Get Ethernet Ports")
    public List<ReferenceDto> getEthernetPorts(PortProvisioning port) {
        List<ReferenceDto> ponPorts = accessLineResourceInventory.getClient().physicalResourceReferenceInternalController()
                .searchPhysicalResourceReference()
                .body(new SearchPhysicalResourceReferenceDto()
                        .endSz(port.getEndSz()))
                .executeAs(checkStatus(HTTP_CODE_OK_200));
        return ponPorts.stream().filter(ponPort -> ponPort.getPortType().getValue().equals(ETHERNET.toString())).collect(Collectors.toList());
    }

    @Step("Get Gfast Ports")
    public List<ReferenceDto> getGfastPorts(DpuDevice dpuDevice) {
        List<ReferenceDto> gfastPorts = accessLineResourceInventory.getClient().physicalResourceReferenceInternalController()
                .searchPhysicalResourceReference()
                .body(new SearchPhysicalResourceReferenceDto()
                        .endSz(dpuDevice.getEndsz()))
                .executeAs(checkStatus(HTTP_CODE_OK_200));
        return gfastPorts.stream().filter(gfastPort -> gfastPort.getPortType().getValue().equals(GFAST.toString())).collect(Collectors.toList());
    }

    @Step("Get Gpon Ports")
    public List<ReferenceDto> getGponPorts(PortProvisioning port) {
        List<ReferenceDto> gponPorts = accessLineResourceInventory.getClient().physicalResourceReferenceInternalController()
                .searchPhysicalResourceReference()
                .body(new SearchPhysicalResourceReferenceDto()
                        .endSz(port.getEndSz()))
                .executeAs(checkStatus(HTTP_CODE_OK_200));
        return gponPorts.stream().filter(gponPort -> gponPort.getPortType().getValue().equals(GPON.toString())).collect(Collectors.toList());
    }

    @Step("Get BackhaulId by Port")
    public List<BackhaulIdDto> getBackHaulId(PortProvisioning port) {
        List<BackhaulIdDto> backhaulIds = accessLineResourceInventory.getClient()
                .backhaulIdController()
                .searchBackhaulIds()
                .body(new SearchBackhaulIdDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber())
                        .portNumber(port.getPortNumber()))
                .executeAs(checkStatus(HTTP_CODE_OK_200));
        return backhaulIds;
    }

    @Step("Get list of AccessLines on the specified port")
    public List<AccessLineDto> getAccessLinesByPort(PortProvisioning port) {
        return accessLineResourceInventory.getClient()
                .accessLineController()
                .searchAccessLines()
                .body(new SearchAccessLineDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber())
                        .portNumber(port.getPortNumber()))
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Get list of AccessLines on the specified port")
    public List<AccessLineDto> getAccessLinesByGfastPort(PortProvisioning port) {
        return accessLineResourceInventory.getClient()
                .accessLineController()
                .searchAccessLines()
                .body(new SearchAccessLineDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber())
                        .portNumber(port.getPortNumber())
                        .referenceType(ReferenceType.DPU))
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Get AccessLine by lineId")
    public List<AccessLineDto> getAccessLinesByLineId(String lineId) {
        return accessLineResourceInventory.getClient()
                .accessLineController()
                .searchAccessLines()
                .body(new SearchAccessLineDto()
                        .lineId(lineId))
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Get OLT_BNG AccessLines with ONT")
    public List<AccessLineDto> getAccessLinesByType(AccessLineProductionPlatform productionPlatform, AccessLineTechnology technology, AccessLineStatus accessLineStatus) {
        List<AccessLineDto> accessLines = accessLineResourceInventory.getClient()
                .accessLineController()
                .searchAccessLines()
                .body(new SearchAccessLineDto()
                        .productionPlatform(productionPlatform)
                        .technology(technology)
                        .status(accessLineStatus))
                .executeAs(checkStatus(HTTP_CODE_OK_200));
        assertTrue(accessLines.size() != 0, "There are no AccessLines with required parameters");
        return accessLines;
    }

    @Step("Get AccessLines by parameters")
    public List<AccessLineDto> getAccessLinesByTypeV2(AccessLineProductionPlatform productionPlatform,
                                                      AccessLineTechnology technology,
                                                      AccessLineStatus accessLineStatus,
                                                      ProfileState subscriberNeProfileState,
                                                      ProfileState subscriberNlProfileState) {
        List<AccessLineDto> accessLines = accessLineResourceInventory.getClient()
                .accessLineController()
                .searchAccessLines()
                .body(new SearchAccessLineDto()
                        .productionPlatform(productionPlatform)
                        .technology(technology)
                        .status(accessLineStatus))
                .executeAs(checkStatus(HTTP_CODE_OK_200));

        List<AccessLineDto> accessLinesFilteredByNeStatus;
        List<AccessLineDto> accessLinesFilteredByNlStatus;

        if (subscriberNeProfileState == null) {
            accessLinesFilteredByNeStatus = accessLines.stream()
                    .filter(accessLineDto -> accessLineDto.getDefaultNeProfile().getSubscriberNeProfile() == null)
                    .collect(Collectors.toList());
        } else {
            accessLinesFilteredByNeStatus = accessLines.stream()
                    .filter(accessLineDto -> accessLineDto.getDefaultNeProfile().getSubscriberNeProfile() != null
                            && accessLineDto.getDefaultNeProfile().getSubscriberNeProfile().getState().equals(subscriberNeProfileState))
                    .collect(Collectors.toList());
        }

        if (subscriberNlProfileState == null) {
            accessLinesFilteredByNlStatus = accessLinesFilteredByNeStatus.stream()
                    .filter(accessLineDto -> accessLineDto.getSubscriberNetworkLineProfile() == null)
                    .collect(Collectors.toList());
        } else {
            accessLinesFilteredByNlStatus = accessLinesFilteredByNeStatus.stream()
                    .filter(accessLineDto -> accessLineDto.getSubscriberNetworkLineProfile() != null
                            && accessLineDto.getSubscriberNetworkLineProfile().getState().equals(subscriberNlProfileState))
                    .collect(Collectors.toList());
        }

        assertTrue(accessLinesFilteredByNlStatus.size() != 0, "There are no AccessLines with required parameters");
        return accessLinesFilteredByNlStatus;
    }

    @Step("Get OLT_BNG AccessLines with ONT")
    public List<AccessLineDto> getFtthAccessLinesWithOnt(PortProvisioning port) {
        List<AccessLineDto> accessLines = accessLineResourceInventory.getClient()
                .accessLineController()
                .searchAccessLines()
                .body(new SearchAccessLineDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber())
                        .portNumber(port.getPortNumber()))
                .executeAs(checkStatus(HTTP_CODE_OK_200));
        List<AccessLineDto> accessLinesWithOnt = accessLines.stream()
                .filter(accessLineDto -> accessLineDto.getStatus().equals(AccessLineStatus.ASSIGNED)
                        && accessLineDto.getDefaultNeProfile().getSubscriberNeProfile() != null
                        && accessLineDto.getDefaultNeProfile().getSubscriberNeProfile().getState().equals(ProfileState.ACTIVE)
                        && accessLineDto.getSubscriberNetworkLineProfile() == null)
                .collect(Collectors.toList());
        assertTrue(accessLinesWithOnt.size() != 0, "There are no AccessLines with ONT");
        return accessLinesWithOnt;
    }

    @Step("Get A4 AccessLines with ONT")
    public List<AccessLineDto> getA4AccessLinesWithOnt(AccessLineTechnology technology) {
        List<AccessLineDto> accessLines = accessLineResourceInventory.getClient()
                .accessLineController()
                .searchAccessLines()
                .body(new SearchAccessLineDto()
                        .productionPlatform(AccessLineProductionPlatform.A4)
                        .technology(technology))
                .executeAs(checkStatus(HTTP_CODE_OK_200));
        List<AccessLineDto> accessLinesWithOnt = accessLines.stream()
                .filter(accessLineDto -> accessLineDto.getStatus().equals(AccessLineStatus.ASSIGNED)
                        && accessLineDto.getNetworkServiceProfileReference().getNspOntSerialNumber() != null)
                .collect(Collectors.toList());
        return accessLinesWithOnt;
    }

    @Step("Get FTTB AccessLines")
    public List<AccessLineDto> getFttbAccessLines(AccessTransmissionMedium accessTransmissionMedium, AccessLineStatus accessLineStatus, AccessLineProductionPlatform productionPlatform) {
        List<AccessLineDto> fttbAccessLines = accessLineResourceInventory.getClient()
                .accessLineController()
                .searchAccessLines()
                .body(new SearchAccessLineDto()
                        .technology(AccessLineTechnology.GFAST)
                        .productionPlatform(productionPlatform))
                .executeAs(checkStatus(HTTP_CODE_OK_200));
        List<AccessLineDto> fttbAccessLinesFiltered = fttbAccessLines.stream()
                .filter(accessLineDto -> accessLineDto.getDpuReference().getAccessTransmissionMedium().equals(accessTransmissionMedium)
                        && accessLineDto.getStatus().equals(accessLineStatus))
                .collect(Collectors.toList());
        assertTrue(fttbAccessLinesFiltered.size() != 0, "There are no FTTB AccessLines with required parameters");
        return fttbAccessLinesFiltered;
    }

    @Step("Get AccessLines with HomeIds")
    public List<AccessLineDto> getAccessLinesWithHomeId(PortProvisioning port) {
        List<AccessLineDto> accessLinesWithHomeIds = getAccessLinesByPort(port).stream()
                .filter(accessLineDto -> (accessLineDto.getHomeId() != null)).collect(Collectors.toList());

        if (accessLinesWithHomeIds.size() != 0) {
            return accessLinesWithHomeIds;
        } else {
            throw new RuntimeException("There are no AccessLines with HomeIds on this port");
        }
    }

    public List<AccessLineDto> getAccessLinesByHomeIds(List<String> homeIds) {
        return homeIds.stream().map(homeId -> accessLineResourceInventory.getClient().accessLineController()
                        .searchAccessLines()
                        .body(new SearchAccessLineDto()
                                .homeId(homeId))
                        .executeAs(checkStatus(HTTP_CODE_OK_200)))
                .flatMap(List::stream).collect(Collectors.toList());
    }

    @Step("Get AccessLines with NetworkSwitching Profiles")
    public List<AccessLineDto> getAccessLinesWithSwitchingProfile(PortProvisioning port) {
        List<AccessLineDto> accessLinesWithSwitchingProfile = getAccessLinesByPort(port).stream()
                .filter(accessLineDto -> (accessLineDto.getNetworkSwitchingProfile() != null)).collect(Collectors.toList());

        if (accessLinesWithSwitchingProfile.size() != 0) {
            return accessLinesWithSwitchingProfile;
        } else {
            throw new RuntimeException("There are no AccessLines with Switching Profiles");
        }
    }

    @Step("Get AccessLines without NetworkSwitching Profiles")
    public List<AccessLineDto> getAccessLinesWithoutSwitchingProfile(PortProvisioning port) {
        List<AccessLineDto> accessLinesWithoutSwitchingProfile = getAccessLinesByPort(port).stream()
                .filter(accessLineDto -> (accessLineDto.getNetworkSwitchingProfile() == null)).collect(Collectors.toList());

        if (accessLinesWithoutSwitchingProfile.size() != 0) {
            return accessLinesWithoutSwitchingProfile;
        } else {
            throw new RuntimeException("There are no AccessLines without Switching Profiles");
        }
    }

    @Step("Get LineID Pool by Port")
    public List<LineIdDto> getLineIdPool(PortProvisioning port) {
        return accessLineResourceInventory.getClient().lineIdController().searchLineIds().body(
                        new SearchLineIdDto()
                                .endSz(port.getEndSz())
                                .slotNumber(port.getSlotNumber())
                                .portNumber(port.getPortNumber()))
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Get HomeID Pool by Port")
    public List<HomeIdDto> getHomeIdPool(PortProvisioning port) {
        return accessLineResourceInventory.getClient().homeIdController().searchHomeIds().body(
                        new SearchHomeIdDto()
                                .endSz(port.getEndSz())
                                .slotNumber(port.getSlotNumber())
                                .portNumber(port.getPortNumber()))
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Get LineIds by Port")
    public List<String> getLineIdsByPort(PortProvisioning port) {
        return getAccessLinesByPort(port).stream().map(accessLineDto -> accessLineDto.getLineId()).collect(Collectors.toList());
    }

    @Step("Get LineId Prefix")
    public static String getLineIdPrefix(String lineId) {
        String[] parts = lineId.split("\\.");
        return parts[1];
    }

    @Step("Get AllocatedOnuIds by port")
    private List<AllocatedOnuIdDto> getAllocatedOnuIds(PortProvisioning port, String portNumber) {
        List<Integer> onuIds = accessLineResourceInventory.getClient().allocatedOnuIdController().searchAllocatedOnuId()
                .body(new SearchAllocatedOnuIdDto()
                        .oltEndSz(port.getEndSz())
                        .portNumber(portNumber)
                        .slotNumber(port.getSlotNumber()))
                .executeAs(checkStatus(HTTP_CODE_OK_200));

        List<AllocatedOnuIdDto> onuIdDtos = onuIds.stream().map(onuId -> accessLineResourceInventory.getClient().allocatedOnuIdController().findFirstAllocatedOnuId()
                        .body(new SearchAllocatedOnuIdDto()
                                .onuId(onuId)
                                .oltEndSz(port.getEndSz())
                                .portNumber(portNumber)
                                .slotNumber(port.getSlotNumber()))
                        .executeAs(checkStatus(HTTP_CODE_OK_200)))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return onuIdDtos;
    }

    @Step("Get AllocatedOnuIds by Device and LineId")
    public List<Integer> getAllocatedOnuIdByDeviceAndLineId(PortProvisioning port, String lineId) {
        return accessLineResourceInventory.getClient().allocatedOnuIdController()
                .searchAllocatedOnuId()
                .body(new SearchAllocatedOnuIdDto()
                        .lineId(lineId)
                        .oltEndSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber())
                        .portNumber(port.getPortNumber()))
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Get AllocatedOnuIds by AccessLines")
    public List<Integer> getAllocatedOnuIdsFromAccessLines(PortProvisioning port, List<AccessLineDto> accessLinesList) {
        return accessLinesList.stream().map(accessLine -> accessLineResourceInventory.getClient().allocatedOnuIdController()
                        .searchAllocatedOnuId()
                        .body(new SearchAllocatedOnuIdDto()
                                .oltEndSz(port.getEndSz())
                                .slotNumber(port.getSlotNumber())
                                .portNumber(port.getPortNumber())
                                .lineId(accessLine.getLineId()))
                        .executeAs(checkStatus(HTTP_CODE_OK_200)))
                .flatMap(List::stream).collect(Collectors.toList());
    }

    @Step("Get AllocatedOnuId by AccessLine")
    public List<Integer> getAllocatedOnuIdFromAccessLine(AccessLineDto accessLine) {
        return accessLineResourceInventory.getClient().allocatedOnuIdController()
                .searchAllocatedOnuId()
                .body(new SearchAllocatedOnuIdDto()
                        .oltEndSz(accessLine.getReference().getEndSz())
                        .lineId(accessLine.getLineId())
                        .slotNumber(accessLine.getReference().getSlotNumber())
                        .portNumber(accessLine.getReference().getPortNumber()))
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Get AllocatedAnpTags from AccessLines")
    public List<Integer> getAllocatedAnpTags(List<AccessLineDto> accessLines) {
        return accessLines.stream().map(accessLineDto -> accessLineDto.getAnpTag().getAnpTag()).collect(Collectors.toList());
    }

    @Step("Get AllocatedAnpTags from the NetworkSwitchingProfiles")
    public List<Integer> getAllocatedAnpTagsFromNsProfile(List<AccessLineDto> accessLines) {
        return accessLines.stream().map(accessLineDto -> accessLineDto.getNetworkSwitchingProfile().getAnpTag().getAnpTag()).collect(Collectors.toList());
    }

    @Step("Get AllocatedAnpTags from the NetworkSwitchingProfiles")
    public List<AllocatedAnpTagDto> getAllocatedAnpTagsFromNsProfileV2(List<AccessLineDto> accessLines) {
        return accessLines.stream().map(accessLineDto -> accessLineDto.getNetworkSwitchingProfile().getAnpTag()).collect(Collectors.toList());
    }

    @Step("Get NetworkSwitchingProfiles")
    public List<NetworkSwitchingProfileDto> getNsProfile(List<AccessLineDto> accessLines) {
        return accessLines.stream().map(accessLineDto -> accessLineDto.getNetworkSwitchingProfile()).collect(Collectors.toList());
    }

    @Step("Get homeID state")
    public HomeIdStatus getHomeIdStateByHomeId(String homeId) {
        List<HomeIdDto> homeIdPool = accessLineResourceInventory.getClient().homeIdController()
                .searchHomeIds()
                .body(new SearchHomeIdDto()
                        .homeId(homeId))
                .executeAs(checkStatus(HTTP_CODE_OK_200));
        assertNotNull(homeIdPool.get(0), "HomeId is not found");
        return homeIdPool.get(0).getStatus();
    }

    @Step("Get access line state by LineId")
    public AccessLineStatus getAccessLineStateByLineId(String lineId) {
        List<AccessLineDto> line = accessLineResourceInventory.getClient().accessLineController()
                .searchAccessLines()
                .body(new SearchAccessLineDto()
                        .lineId(lineId))
                .executeAs(checkStatus(HTTP_CODE_OK_200));
        assertNotNull(line.get(0), "Access line is not found");
        return line.get(0).getStatus();
    }

    @Step("Get subscriber NE profile by LineId")
    public SubscriberNeProfileDto getSubscriberNEProfile(String lineId) {
        List<AccessLineDto> line = accessLineResourceInventory.getClient().accessLineController()
                .searchAccessLines()
                .body(new SearchAccessLineDto()
                        .lineId(lineId))
                .executeAs(checkStatus(HTTP_CODE_OK_200));
        assertNotNull(line.get(0).getDefaultNeProfile(), "Default NE profile is null");
        SubscriberNeProfileDto subscriberNeProfile = line.get(0).getDefaultNeProfile().getSubscriberNeProfile();
        return subscriberNeProfile;
    }

    @Step("Get subscriber network line profile by LineId")
    public SubscriberNetworkLineProfileDto getSubscriberNLProfile(String lineId) {
        List<AccessLineDto> line = accessLineResourceInventory.getClient().accessLineController()
                .searchAccessLines()
                .body(new SearchAccessLineDto()
                        .lineId(lineId))
                .executeAs(checkStatus(HTTP_CODE_OK_200));
        assertNotNull(line.get(0).getSubscriberNetworkLineProfile(), "Subscriber NL profile is null");
        return line.get(0).getSubscriberNetworkLineProfile();
    }

    @Step("Search physical resource reference by criteria")
    public List<ReferenceDto> getPhysicalResourceRef(String endsz, String port, String portType) {
        List<ReferenceDto> references = accessLineResourceInventory.getClient()
                .physicalResourceReferenceInternalController()
                .searchPhysicalResourceReference()
                .body(new SearchPhysicalResourceReferenceDto().endSz(endsz).portNumber(port).portType(PortType.valueOf(portType)))
                .executeAs(checkStatus(HTTP_CODE_OK_200));
        return references;

    }

    @Step("Get all AccessLine entities")
    public List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> getAllAccessLineEntities() {
        return accessLineResourceInventory.getClient().accessLineControllerExternal()
                .listAccessLine()
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Get AccessLine entities by LineId for CA")
    public List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> getAccessLineEntitiesByLineId(String lineId) {
        return accessLineResourceInventory.getClient().accessLineControllerExternal().listAccessLine().lineIdQuery(lineId)
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Get AccessLine entities by oltEndSz, slot, port for CA")
    public List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> getAccessLineEntitiesByOlt(int limit, String EndSz, String slot, String port) {
        return accessLineResourceInventory.getClient().accessLineControllerExternal()
                .listAccessLine()
                .limitQuery(limit)
                .portReferencesOltDownlinkPortReferenceEndSZQuery(EndSz)
                .portReferencesOltDownlinkPortReferenceSlotNameQuery(slot)
                .portReferencesOltDownlinkPortReferencePortNameQuery(port)
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Get AccessLine entities by dpuEndSz, port for CA")
    public List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> getAccessLineEntitiesByDpu(String dpuEndSz, String port) {
        return accessLineResourceInventory.getClient().accessLineControllerExternal()
                .listAccessLine()
                .portReferencesDpuDownlinkPortReferenceEndSZQuery(dpuEndSz)
                .portReferencesDpuDownlinkPortReferencePortNameQuery(port)
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Get AccessLine entities by HomeId")
    public List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> getAccessLineEntitiesByHomeId(String homeId) {
        return accessLineResourceInventory.getClient().accessLineControllerExternal()
                .listAccessLine()
                .homeIdQuery(homeId)
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Get AccessLine entities by OntSerialNumber")
    public List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> getAccessLineEntitiesByOntSerialNumber(String ontSerialNumber) {
        return accessLineResourceInventory.getClient().accessLineControllerExternal()
                .listAccessLine()
                .profilesFtthNeProfileSubscriberNetworkElementProfileOntSerialNumberQuery(ontSerialNumber)
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Get AccessLine entities by Status")
    public List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> getAccessLineEntitiesByStatus(AccessLineStatus accesslineStatus) {
        return accessLineResourceInventory.getClient().accessLineControllerExternal()
                .listAccessLine()
                .statusQuery(accesslineStatus)
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Get AccessLine entities by Technology")
    public List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> getAccessLineEntitiesByTechnology(AccessLineTechnology accessLineTechnology) {
        return accessLineResourceInventory.getClient().accessLineControllerExternal()
                .listAccessLine()
                .technologyQuery(accessLineTechnology)
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Get AccessLine entities by Modification Date greater than")
    public List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> getAccessLineEntitiesByModificationDateGt(OffsetDateTime offsetDateTime) {
        return accessLineResourceInventory.getClient().accessLineControllerExternal()
                .listAccessLine()
                .modificationDateGtQuery(offsetDateTime.toString())
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Get AccessLine entities by Modification Date less than")
    public List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> getAccessLineEntitiesByModificationDateLt(OffsetDateTime offsetDateTime) {
        return accessLineResourceInventory.getClient().accessLineControllerExternal()
                .listAccessLine()
                .modificationDateLtQuery(offsetDateTime.toString())
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Get AccessLine entities with offset")
    public List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> getAccessLineEntitiesWithOffset(int offset) {
        return accessLineResourceInventory.getClient().accessLineControllerExternal()
                .listAccessLine()
                .offsetQuery(offset)
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Get AccessLine entities with fields")
    public List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> getAccessLineEntitiesWithFields(String fields) {
        return accessLineResourceInventory.getClient().accessLineControllerExternal()
                .listAccessLine()
                .fieldsQuery(fields)
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Get AccessLine entities by id")
    public com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine getAccessLineEntitiesbyId(Long id) {
        return accessLineResourceInventory.getClient().accessLineControllerExternal()
                .retrieveAccessLine()
                .idPath(id)
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }


    @Step("Compare two Lists")
    public <T, U> boolean compareLists(List<T> actualList, List<U> expectedList) {
        return actualList.size() == expectedList.size() && actualList.containsAll(expectedList) && expectedList.containsAll(actualList);
    }

    private static DefaultNeProfile mapToDefaultNeProfile(DefaultNeProfileDto defaultNeProfile) {
        DefaultNeProfile defaultNeProfileList = new DefaultNeProfile();
        defaultNeProfileList.setProfileName(defaultNeProfile.getProfileName());
        defaultNeProfileList.setState(defaultNeProfile.getState().toString());
        defaultNeProfileList.setAncpPartitionId(defaultNeProfile.getAncpPartitionId());
        return defaultNeProfileList;
    }

    private static DefaultNetworkLineProfile mapToNLProfile(DefaultNetworkLineProfileDto defaultNlProfile) {
        DefaultNetworkLineProfile defaultNlProfileList = new DefaultNetworkLineProfile();
        defaultNlProfileList.setAccessType(defaultNlProfile.getAccessType());
        defaultNlProfileList.setMinDownBandwidth(defaultNlProfile.getMinDownBandwidth());
        defaultNlProfileList.setMinUpBandwidth(defaultNlProfile.getMinUpBandwidth());
        defaultNlProfileList.setGuaranteedDownBandwidth(defaultNlProfile.getGuaranteedDownBandwidth());
        defaultNlProfileList.setGuaranteedUpBandwidth(defaultNlProfile.getGuaranteedUpBandwidth());
        defaultNlProfileList.setMaxDownBandwidth(defaultNlProfile.getMaxDownBandwidth());
        defaultNlProfileList.setMaxUpBandwidth(defaultNlProfile.getMaxUpBandwidth());
        defaultNlProfileList.setState(defaultNlProfile.getState().toString());
        return defaultNlProfileList;
    }

    private static FttbNeProfile mapToFttbNeProfile(FttbNeProfileDto fttbNeProfile) {
        FttbNeProfile fttbNeProfileList = new FttbNeProfile();
        fttbNeProfileList.setGfastInterfaceProfile(fttbNeProfile.getGfastInterfaceProfile());
        fttbNeProfileList.setDpuLineSpectrumProfile(fttbNeProfile.getDpuLineSpectrumProfile());
        fttbNeProfileList.setNumberOfGemPorts(fttbNeProfile.getNumberOfGemPorts());
        fttbNeProfileList.setBandwidthProfile(fttbNeProfile.getBandwidthProfile());
        fttbNeProfileList.setStateMosaic(fttbNeProfile.getStateMosaic().toString());
        fttbNeProfileList.setStateOlt(fttbNeProfile.getStateOlt().toString());
        return fttbNeProfileList;
    }

    private static L2BsaNspReference mapToL2BsaNspReference(L2BsaNspReferenceDto l2BsaNspReference) {
        L2BsaNspReference l2BsaNspReferenceList = new L2BsaNspReference();
        l2BsaNspReferenceList.setL2ccid(l2BsaNspReference.getL2ccid());
        l2BsaNspReferenceList.setCarrierBsaReference(l2BsaNspReference.getCarrierBsaReference());
        l2BsaNspReferenceList.setDownBandwidth(l2BsaNspReference.getDownBandwidth());
        l2BsaNspReferenceList.setUpBandwidth(l2BsaNspReference.getUpBandwidth());
        return l2BsaNspReferenceList;
    }

    private static SubscriberNetworkLineProfile mapToSubscriberNetworkLineProfile(SubscriberNetworkLineProfileDto subscriberNetworkLineProfile) {
        SubscriberNetworkLineProfile subscriberNetworkLineProfileList = new SubscriberNetworkLineProfile();
        subscriberNetworkLineProfileList.setMaxDownBandwidth(subscriberNetworkLineProfile.getMaxDownBandwidth());
        subscriberNetworkLineProfileList.setMaxUpBandwidth(subscriberNetworkLineProfile.getMaxUpBandwidth());
        subscriberNetworkLineProfileList.setMinDownBandwidth(subscriberNetworkLineProfile.getMinDownBandwidth());
        subscriberNetworkLineProfileList.setMinUpBandwidth(subscriberNetworkLineProfile.getMinUpBandwidth());
        subscriberNetworkLineProfileList.setGuaranteedDownBandwidth(subscriberNetworkLineProfile.getGuaranteedDownBandwidth());
        subscriberNetworkLineProfileList.setGuaranteedUpBandwidth(subscriberNetworkLineProfile.getGuaranteedUpBandwidth());
        subscriberNetworkLineProfileList.setDownBandwidth(subscriberNetworkLineProfile.getDownBandwidth());
        subscriberNetworkLineProfileList.setUpBandwidth(subscriberNetworkLineProfile.getUpBandwidth());
        subscriberNetworkLineProfileList.setKlsId(Math.toIntExact(subscriberNetworkLineProfile.getKlsId()));
        subscriberNetworkLineProfileList.setState((subscriberNetworkLineProfile.getState().toString()));
        return subscriberNetworkLineProfileList;
    }

    @Owner("TMI")
    public String getLineIdByHomeId(String homeId) {
        List<com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLine> accessLines =
                accessLineResourceInventory.getClient().accessLineControllerExternal()
                        .listAccessLine()
                        .homeIdQuery(homeId)
                        .executeAs(checkStatus(HTTP_CODE_OK_200));
        assertNotNull(accessLines.get(0), "AccessLine is not found");
        assertNotNull(accessLines.get(0).getLineId(), "lineId is not found");
        return accessLines.get(0).getLineId();
    }

    @Step("Create AccessLine")
    public void postAccessLine(AccessLineDto accessLineMigrated) {
        accessLineResourceInventory.getClient().accessLineController()
                .create()
                .body(accessLineMigrated)
                .execute(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Create OnuId")
    public void postOnuId(AllocatedOnuIdDto onuIdMigrated) {
        accessLineResourceInventory.getClient().allocatedOnuIdController()
                .createAllocatedOnuId()
                .body(onuIdMigrated)
                .execute(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Create LineId")
    public void postLineId(LineIdMigrated lineIdMigrated) {
        accessLineResourceInventory.getClient()
                .lineIdController()
                .addLineIds()
                .body(lineIdMigrated.getLineIdDtoList())
                .execute(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Change HomeId Status")
    public String changeHomeIdStatus(HomeIdDto homeIdDto, HomeIdStatus homeIdStatus) {
        homeIdDto.setStatus(homeIdStatus);
        accessLineResourceInventory.getClient().homeIdController()
                .updateHomeId()
                .body(homeIdDto)
                .execute(checkStatus(HTTP_CODE_OK_200));
        return homeIdDto.getHomeId();
    }

    @Step("Update HomeId on migrated AccessLine")
    public void updateHomeIdOnAccessLine(String lineId, String homeId) {
        List<AccessLineDto> accessLineDtoList = accessLineResourceInventory.getClient().accessLineController()
                .searchAccessLines()
                .body(new SearchAccessLineDto().lineId(lineId))
                .executeAs(checkStatus(HTTP_CODE_OK_200));

        final AccessLineDto accessLineDto = accessLineDtoList.get(0);

        if (accessLineDto != null) {
            accessLineDto.setHomeId(homeId);

            accessLineResourceInventory.getClient().accessLineController()
                    .update()
                    .body(accessLineDto)
                    .executeAs(checkStatus(HTTP_CODE_OK_200));
        } else {
            throw new RuntimeException("Access Line not found with lineId: " + lineId);
        }
    }

    @Step("Wait until AccessLine is deleted")
    public void waitUntilAccessLineIsDeleted(String lineId) {
        try {
            TimeoutBlock timeoutBlock = new TimeoutBlock(LATENCY_FOR_ACCESSLINE_DEPROVISIONING); //set timeout in milliseconds
            timeoutBlock.setTimeoutInterval(2000);
            Supplier<Boolean> checkAccessLineDeletion = () ->
                    getAccessLinesByLineId(lineId).size() == 0;
            timeoutBlock.addBlock(checkAccessLineDeletion); // execute the runnable precondition
        } catch (Exception e) {
            //catch the exception here . Which is block didn't execute within the time limit
        }
    }


}

//  private void checkDevicePostConditions(PortProvisioning port) {
//    List<String> portNumbers = wgAccessProvisioningRobot.getPonPorts(port)
//            .stream()
//            .map(Port::getPortNumber)
//            .collect(Collectors.toList()); //list of ponPort numbers
//
//    List<PortProvisioning> portProvisioningList = portNumbers
//            .stream()
//            .map(portNumber -> getPortProvisioning(port, portNumber))
//            .collect(Collectors.toList()); //list of portProvisioning numbers
//
//    portProvisioningList.forEach(portAfterProvisioning -> checkFtthPortParameters(portAfterProvisioning));
//  }
//
//  private PortProvisioning getPortProvisioning(PortProvisioning portProvisioning, String portNumber) {
//    PortProvisioning port = new PortProvisioning();
//    port.setEndSz(portProvisioning.getEndSz());
//    port.setPortNumber(portNumber);
//    port.setHomeIdPool(portProvisioning.getHomeIdPool());
//    port.setDefaultNEProfilesActive(portProvisioning.getDefaultNEProfilesActive());
//    port.setDefaultNetworkLineProfilesActive(portProvisioning.getDefaultNetworkLineProfilesActive());
//    port.setAccessLinesWG(portProvisioning.getAccessLinesWG());
//    return port;
//  }
