package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.*;

public class A4ResourceInventoryMapper {

    public static final String NEL_LSZ = "4N4";
    public static final String NEL_ORDER_NUMBER = "1004";
    public static final String ACTIVATED = "ACTIVATED";
    public static final String INSTALLING = "INSTALLING";
    public static final String WORKING = "WORKING";
    public static final String UNDEFINED = "undefined";

    public NetworkElementGroupDto getDefaultNetworkElementGroupData() {
        return new NetworkElementGroupDto()
                .uuid(UUID.randomUUID().toString()) // Unique constraint for NEGs
                .name("NEG-" + getRandomDigits(6)) // Unique constraint for NEGs
                .type("POD")
                .specificationVersion("1")
                .operationalState("NOT_WORKING")
                .lifecycleState("PLANNING")
                .description("NEG created during osr-test integration test")
                .centralOfficeNetworkOperator("neg_centOffNetOp_for_integration_test")
                .lastUpdateTime(OffsetDateTime.now())
                .lastSuccessfulSyncTime(OffsetDateTime.now())
                .creationTime(OffsetDateTime.now());
    }

    public NetworkElementDto getDefaultNetworkElementData() {
        return new NetworkElementDto()
                .uuid(UUID.randomUUID().toString()) // Unique constraint for NEs
                .vpsz("49/" + getRandomDigits(4) + "/" + getRandomDigits(3)) // Unique constraint (together with FSZ) for NEs
                .fsz("7KH0") // Unique constraint (together with VPSZ) for NEs
                .ztpIdent(getRandomDigits(12)) // Unique constraint for NEs
                .networkElementGroupUuid(null) // has to be set to existing NEG in calling method
                .href("/networkElementGroups/negUuid")
                .description("NE for integration test")
                .specificationVersion("string")
                .address("Berlin")
                .administrativeState(ACTIVATED)
                .klsId("123456")
                .lifecycleState(INSTALLING)
                .operationalState("NOT_WORKING")
                .plannedMatNumber("40958960")
                .plannedRackId("000031-000000-001-004-002-021")
                .plannedRackPosition("1 / 2 / 3 / 4")
                .planningDeviceName("dmst.olt.1")
                .roles("SE")
                .type("A4-OLT-v1")
                .category("OLT")
                .fiberOnLocationId("100000005")
                .partyId("10001")
                .creationTime(OffsetDateTime.now())
                .lastUpdateTime(OffsetDateTime.now())
                .lastSuccessfulSyncTime(OffsetDateTime.now());
    }

    public NetworkElementPortDto getDefaultNetworkElementPortData() {
        final String funcLabel = "GPON_" + getRandomDigits(4);

        return new NetworkElementPortDto()
                .uuid(UUID.randomUUID().toString()) // Unique constraint for NEPs
                .logicalLabel(funcLabel) // Unique constraint (together with endsz of connected NE) for NEPs
                .networkElementUuid(null) // has to be set to existing NE in calling method
                .networkElementEndsz(null) // has to be set to existing NE in calling method
                .href("/networkElements/neUuid")
                .operationalState(INSTALLING)
                .administrativeState(WORKING)
                .portNumber(getPortNumberByFunctionalPortLabel(funcLabel))
                .accessNetworkOperator("AccessNetworkOperator")
                .type("GPON")
                .creationTime(OffsetDateTime.now())
                .lastUpdateTime(OffsetDateTime.now())
                .lastSuccessfulSyncTime(OffsetDateTime.now())
                .specificationVersion("14.1")
                .description("NetworkElementPortDto");
    }

    public NetworkElementLinkDto getDefaultNetworkElementLinkData() {
        return new NetworkElementLinkDto()
                .uuid(UUID.randomUUID().toString()) // Unique constraint for NELs
                .networkElementPortAUuid(null) // has to be set existing NEP in calling method
                .endszA(null) // has to be set existing NE in calling method
                .networkElementPortBUuid(null) // has to be set existing NEP in calling method
                .endszB(null) // has to be set existing NE in calling method
                .lbz(null) // has to be linked to existing NEs in calling method
                .ueWegId("uewegId-" + getRandomDigits(6))
                .description("NEL for integration test")
                .lsz(NEL_LSZ)
                .lifecycleState(INSTALLING)
                .operationalState(INSTALLING)
                .orderNumber(NEL_ORDER_NUMBER)
                .pluralId("2")
                .creationTime(OffsetDateTime.now())
                .lastUpdateTime(OffsetDateTime.now())
                .lastSuccessfulSyncTime(OffsetDateTime.now());
    }

    public TerminationPointDto getDefaultTerminationPointData() {
        return new TerminationPointDto()
                .uuid(UUID.randomUUID().toString()) // Unique constraint for TPs
                .parentUuid(null) // has to be set to existing NEP or NEG in calling method
                .description("TP for integration test")
                .lockedForNspUsage(true)
                .state("state")
                .carrierBsaReference("carrerBsaRef")
                .supportedDiagnosesName("supportedDiagnoseName")
                .supportedDiagnosesSpecificationVersion("supportedDiagnoseVerison")
                .type("type")
                .creationTime(OffsetDateTime.now())
                .lastUpdateTime(OffsetDateTime.now());
    }

    public NetworkServiceProfileFtthAccessDto getDefaultNetworkServiceProfileFtthAccessData() {
        return new NetworkServiceProfileFtthAccessDto()
                .uuid(UUID.randomUUID().toString()) // Unique constraint for NSPs
                .lineId("LINEID-" + getRandomDigits(6)) // Unique constraint (together with lifecycleState) for NSPs
                .ontSerialNumber("ONTSERIALNUMBER-" + getRandomDigits(6)) // Unique constraint (together with lifecycleState) for NSPs
                .terminationPointFtthAccessUuid(null) // has to be set to existing TP in calling method, also unique constraint (together with terminationPointFtthAccessUuid) for NSPs
                .oltPortOntLastRegisteredOn(null) // has to be set to existing NEP in calling method
                .lifecycleState(INSTALLING)
                .href("HREF?")
                .specificationVersion("3")
                .virtualServiceProvider("ein Virtual Service Provider")
                .administrativeMode(ACTIVATED)
                .operationalState(INSTALLING)
                .description("NSP FTTH Access created during osr-test integration test")
                .creationTime(OffsetDateTime.now())
                .lastUpdateTime(OffsetDateTime.now())
                .lastSuccessfulSyncTime(OffsetDateTime.now());
    }

    public NetworkServiceProfileA10NspDto getDefaultNetworkServiceProfileA10NspData() {
        VlanRangeDto vrDto = new VlanRangeDto()
                .vlanRangeLower(UNDEFINED)
                .vlanRangeUpper(UNDEFINED);

        A10NspQosDto a10NspQosDto = new A10NspQosDto()
                .qosBandwidthDown(UNDEFINED)
                .qosBandwidthUp(UNDEFINED)
                .qosPriority(UNDEFINED);

        return new NetworkServiceProfileA10NspDto()
                .uuid(UUID.randomUUID().toString()) // Unique constraint for TPs
                .terminationPointA10NspUuid(null) // has to be set to existing TP in calling method
                .href("HREF")
                .specificationVersion("1")
                .virtualServiceProvider("a Virtual Service Provider")
                .administrativeMode(ACTIVATED)
                .operationalState("opState")
                .lifecycleState("lcState")
                .description("NSP A10NSP created during osr-test integration test")
                .mtuSize("1590")
                .etherType("0x88a8")
                .lacpActive(true)
                .minActiveLagLinks("1")
                .qosMode("TOLERANT")
                .carrierBsaReference("CarrierBsaReference")
                .numberOfAssociatedNsps("noAssoNsps")
                .itAccountingKey(UNDEFINED)
                .lacpMode(UNDEFINED)
                .dataRate(UNDEFINED)
                .numberOfAssociatedNsps(UNDEFINED)
                .sVlanRange(Collections.singletonList(vrDto))
                .qosClasses(Collections.singletonList(a10NspQosDto))
                .creationTime(OffsetDateTime.now())
                .lastUpdateTime(OffsetDateTime.now())
                .lastSuccessfulSyncTime(OffsetDateTime.now());
    }

    public NetworkServiceProfileL2BsaDto getDefaultNetworkServiceProfileL2BsaData() {
        ServiceBandwidthDto serviceBandwidthDto = new ServiceBandwidthDto()
                .dataRateUp("150000")
                .dataRateDown("300000");

        List<ServiceBandwidthDto> serviceBandwidthDtoList = new ArrayList<>();
        serviceBandwidthDtoList.add(serviceBandwidthDto);

        return new NetworkServiceProfileL2BsaDto()
                .uuid(UUID.randomUUID().toString()) // Unique constraint for TPs
                .lineId("LINEID-" + getRandomDigits(6)) // Unique constraint (together with lifecycleState) for NSPs
                .terminationPointL2BsaUuid(null) // has to be set to existing TP in calling method
                .href("HREF")
                .specificationVersion("1")
                .virtualServiceProvider("a Virtual Service Provider")
                .administrativeMode("admMode") // neu im Model
                .operationalState("opState")
                .lifecycleState("lcState")
                .l2CcId("l2CcId")
                .description("NSP L2BSA created during osr-test integration test")
//                .nspAccess("123") // skip this because optional
                .serviceBandwidth(serviceBandwidthDtoList)
                .creationTime(OffsetDateTime.now())
                .lastUpdateTime(OffsetDateTime.now())
                .lastSuccessfulSyncTime(OffsetDateTime.now());
    }

    public NetworkElementGroupDto getNetworkElementGroupDto(A4NetworkElementGroup negData) {
        NetworkElementGroupDto neg = getDefaultNetworkElementGroupData();

        negData.setUuid(neg.getUuid());

        neg.setName(negData.getName());
        neg.setOperationalState(negData.getOperationalState());
        neg.setLifecycleState(negData.getLifecycleState());
        neg.setType(negData.getType());

        return neg;
    }

    public NetworkElementDto getNetworkElementDto(A4NetworkElement neData, String negUuid) {
        NetworkElementDto ne = getNetworkElementDto(negUuid);

        neData.setUuid(ne.getUuid());

        ne.setOperationalState(neData.getOperationalState());
        ne.setLifecycleState(neData.getLifecycleState());
        ne.setCategory(neData.getCategory());
        ne.setType(neData.getType());
        ne.setVpsz(neData.getVpsz());
        ne.setFsz(neData.getFsz());
        ne.setKlsId(neData.getKlsId());
        ne.setPlanningDeviceName(neData.getPlanningDeviceName());
        ne.setPlannedMatNumber(neData.getPlannedMatNr());
        ne.setZtpIdent(neData.getZtpIdent());

        return ne;
    }

    public NetworkElementDto getNetworkElementDto(String negUuid) {
        NetworkElementDto ne = getDefaultNetworkElementData();
        ne.setNetworkElementGroupUuid(negUuid);

        return ne;
    }

    public NetworkElementDto getNetworkElementDto(A4NetworkElement neData, A4NetworkElementGroup negData) {
        return getNetworkElementDto(neData, negData.getUuid());
    }

    public NetworkElementDto getNetworkElementDto(A4NetworkElement neData, NetworkElementGroupDto neg) {
        return getNetworkElementDto(neData, neg.getUuid());
    }

    public NetworkElementPortDto getNetworkElementPortDto(String endsz, String port) {
        NetworkElementPortDto nep = getDefaultNetworkElementPortData();
        nep.setPortNumber(port);
        nep.setNetworkElementEndsz(endsz);

        return nep;
    }

    public NetworkElementPortDto getNetworkElementPortDto(String neUuid, String neVpsz, String neFsz) {
        NetworkElementPortDto nep = getDefaultNetworkElementPortData();
        nep.setNetworkElementUuid(neUuid);
        nep.setNetworkElementEndsz(getEndsz(neVpsz, neFsz));

        return nep;
    }

    public NetworkElementPortDto getNetworkElementPortDto(A4NetworkElementPort nepData, A4NetworkElement neData) {
        NetworkElementPortDto nep = getNetworkElementPortDto(neData.getUuid(), neData.getVpsz(), neData.getFsz());

        nepData.setUuid(nep.getUuid());

        nep.setLogicalLabel(nepData.getFunctionalPortLabel());
        nep.setPortNumber(getPortNumberByFunctionalPortLabel(nepData.getFunctionalPortLabel()));
        nep.setOperationalState(nepData.getOperationalState());
        nep.setType(nepData.getType());

        return nep;
    }

    public NetworkElementLinkDto getNetworkElementLinkDto(A4NetworkElementLink nelData, A4NetworkElementPort nepDataA, A4NetworkElementPort nepDataB) {
        A4NetworkElement neDataA = new A4NetworkElement();
        A4NetworkElement neDataB = new A4NetworkElement();

        neDataA.setVpsz(getRandomDigits(4));
        neDataA.setFsz(getRandomDigits(4));

        neDataB.setVpsz(getRandomDigits(4));
        neDataB.setFsz(getRandomDigits(4));

        return getNetworkElementLinkDto(nelData, nepDataA, nepDataB, neDataA, neDataB);
    }

    public NetworkElementLinkDto getNetworkElementLinkDto(A4NetworkElementLink nelData, A4NetworkElementPort nepDataA, A4NetworkElementPort nepDataB, A4NetworkElement neDataA, A4NetworkElement neDataB) {
        NetworkElementLinkDto nel = getNetworkElementLinkDto(nepDataA.getUuid(), nepDataB.getUuid(), neDataA.getVpsz(), neDataA.getFsz(), neDataB.getVpsz(), neDataB.getFsz());

        nelData.setUuid(nel.getUuid());
        nelData.setUeWegId(nel.getUeWegId());
        nelData.setLsz(nel.getLsz());
        nelData.setLbz(nel.getLbz());

        nel.setLifecycleState(nelData.getLifecycleState());
        nel.setOperationalState(nelData.getOperationalState());

        return nel;
    }

    public NetworkElementLinkDto getNetworkElementLinkDto(A4NetworkElementLink nelData, A4NetworkElementPort nepDataA, A4NetworkElementPort nepDataB, A4NetworkElement neDataA, A4NetworkElement neDataB, UewegData uewegData) {
        NetworkElementLinkDto nel = getNetworkElementLinkDto(nepDataA.getUuid(), nepDataB.getUuid(), neDataA.getVpsz(), neDataA.getFsz(), neDataB.getVpsz(), neDataB.getFsz());

        nelData.setUuid(nel.getUuid());
        nelData.setUeWegId(uewegData.getUewegId());
        nelData.setLsz(nel.getLsz());
        nelData.setLbz(nel.getLbz());

        nel.setUeWegId(uewegData.getUewegId());
        nel.setLifecycleState(nelData.getLifecycleState());
        nel.setOperationalState(nelData.getOperationalState());

        return nel;
    }

    public NetworkElementLinkDto getNetworkElementLinkDto(String nepUuid1, String nepUuid2, String neVpsz1, String neFsz1, String neVpsz2, String neFsz2) {
        final String endszA = getEndsz(neVpsz1, neFsz1);
        final String endszB = getEndsz(neVpsz2, neFsz2);

        NetworkElementLinkDto nel = getDefaultNetworkElementLinkData();
        nel.setNetworkElementPortAUuid(nepUuid1);
        nel.setNetworkElementPortBUuid(nepUuid2);
        nel.setEndszA(endszA);
        nel.setEndszB(endszB);
        nel.setLbz(getLbzByEndsz(nel.getLsz(), nel.getOrderNumber(), endszA, endszB)); // LBZ is unique constraint!

        return nel;
    }

    public TerminationPointDto getTerminationPointDto(A4TerminationPoint tpData, String uuid) {
        TerminationPointDto tp = getTerminationPointDto(uuid);

        tpData.setUuid(tp.getUuid());

        tp.setCarrierBsaReference(tpData.getCarrierBsaReference());
        tp.setType(tpData.getSubType());
        tp.setState(tpData.getState());

        return tp;
    }

    public TerminationPointDto getTerminationPointDto(String parentUuid) {
        TerminationPointDto tp = getDefaultTerminationPointData();
        tp.setParentUuid(parentUuid);

        return tp;
    }

    public NetworkServiceProfileFtthAccessDto getNetworkServiceProfileFtthAccessDto(A4NetworkServiceProfileFtthAccess nspData, A4TerminationPoint tpData) {
        return getNetworkServiceProfileFtthAccessDto(nspData, tpData, nspData.getOltPortOntLastRegisteredOn());
    }

    public NetworkServiceProfileFtthAccessDto getNetworkServiceProfileFtthAccessDto(A4NetworkServiceProfileFtthAccess nspData, A4TerminationPoint tpData, String port) {
        NetworkServiceProfileFtthAccessDto nspFtth = getNetworkServiceProfileFtthAccessDto(tpData.getUuid());

        nspData.setUuid(nspFtth.getUuid());
        nspData.setOltPortOntLastRegisteredOn(port);

        nspFtth.setLineId(nspData.getLineId());
        nspFtth.setOntSerialNumber(nspData.getOntSerialNumber());
        nspFtth.setLineId(nspData.getLineId());
        nspFtth.setOltPortOntLastRegisteredOn(port);
        nspFtth.setOperationalState(nspData.getOperationalState());
        nspFtth.setLifecycleState(nspData.getLifecycleState());

        return nspFtth;
    }

    public NetworkServiceProfileFtthAccessDto getNetworkServiceProfileFtthAccessDto(String tpUuid) {
        NetworkServiceProfileFtthAccessDto nspFtth = getDefaultNetworkServiceProfileFtthAccessData();
        nspFtth.setTerminationPointFtthAccessUuid(tpUuid);

        return nspFtth;
    }

    public NetworkServiceProfileFtthAccessDto getNetworkServiceProfileFtthAccessDto(
            A4NetworkServiceProfileFtthAccess nspData,
            A4TerminationPoint tpData,
            A4NetworkElementPort nepData) {

        return getNetworkServiceProfileFtthAccessDto(nspData, tpData, nepData.getUuid());
    }

    public NetworkServiceProfileFtthAccessDto getNspWithoutOntLastRegisteredOn() {
        NetworkServiceProfileFtthAccessDto nspFtth = getDefaultNetworkServiceProfileFtthAccessData();
        nspFtth.setOltPortOntLastRegisteredOn(null);

        return nspFtth;
    }

    public List<NetworkServiceProfileFtthAccessDto> getListOfNspWithoutOntLastRegisteredOn() {
        List<NetworkServiceProfileFtthAccessDto> networkServiceProfileFtthAccessDtos = new ArrayList<>();
        networkServiceProfileFtthAccessDtos.add(getNspWithoutOntLastRegisteredOn());
        return networkServiceProfileFtthAccessDtos;
    }

    public NetworkServiceProfileA10NspDto getNetworkServiceProfileA10NspDto(A4NetworkServiceProfileA10Nsp nspData, A4TerminationPoint tpData) {
        NetworkServiceProfileA10NspDto nspA10 = getNetworkServiceProfileA10NspDto(tpData.getUuid());

        nspData.setUuid(nspA10.getUuid());

        nspA10.setOperationalState(nspData.getOperationalState());
        nspA10.setLifecycleState(nspData.getLifecycleState());

        return nspA10;
    }

    public NetworkServiceProfileA10NspDto getNetworkServiceProfileA10NspDto(String tpUuid) {
        NetworkServiceProfileA10NspDto nspA10 = getDefaultNetworkServiceProfileA10NspData();
        nspA10.setTerminationPointA10NspUuid(tpUuid);

        return nspA10;
    }

    public NetworkServiceProfileL2BsaDto getNetworkServiceProfileL2BsaDto(A4NetworkServiceProfileL2Bsa nspData, A4TerminationPoint tpData) {
        NetworkServiceProfileL2BsaDto nspL2 = getNetworkServiceProfileL2BsaDto(tpData.getUuid());

        nspData.setUuid(nspL2.getUuid());

        nspL2.setAdministrativeMode(nspData.getAdministrativeMode());// neu im Model
        nspL2.setOperationalState(nspData.getOperationalState());
        nspL2.setLifecycleState(nspData.getLifecycleState());
        nspL2.setDataRateUp(nspData.getDataRateUp());
        nspL2.setDataRateDown(nspData.getDataRateDown());
        nspL2.setLineId(nspData.getLineId());
        nspL2.setL2CcId(nspData.getL2CcId());

        return nspL2;
    }

    public NetworkServiceProfileL2BsaDto getNetworkServiceProfileL2BsaDto(String tpUuid) {
        NetworkServiceProfileL2BsaDto nspL2 = getDefaultNetworkServiceProfileL2BsaData();
        nspL2.setTerminationPointL2BsaUuid(tpUuid);

        return nspL2;
    }

}
