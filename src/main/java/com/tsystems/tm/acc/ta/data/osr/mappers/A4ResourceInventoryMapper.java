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
    public static final String TP_UUID = "tpUuid";

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
                .networkElementGroupUuid("negUuid") // has to be set existing NEG in calling method
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
                .networkElementUuid("neUuid") // has to be set existing NE in calling method
                .networkElementEndsz("neEndsz") // has to be set existing NE in calling method
                .href("/networkElements/neUuid")
                .operationalState(INSTALLING)
                .administrativeState(WORKING)
                .portNumber(getPortNumberByFunctionalPortLabel(funcLabel))
                .accessNetworkOperator("AccessNetworkOperator")
                .type("GPON")
                .creationTime(OffsetDateTime.now().minusDays(1))
                .lastUpdateTime(OffsetDateTime.now())
                .lastSuccessfulSyncTime(OffsetDateTime.now())
                .specificationVersion("14.1")
                .description("NetworkElementPortDto");
    }

    public NetworkElementLinkDto getDefaultNetworkElementLinkData() {
        final String endsz1 = "endszA";
        final String endsz2 = "endszB";
        final String lsz = NEL_LSZ;
        final String orderNo = NEL_ORDER_NUMBER;

        return new NetworkElementLinkDto()
                .uuid(UUID.randomUUID().toString()) // Unique constraint for NELs
                .networkElementPortAUuid("nepA") // has to be set existing NEP in calling method
                .endszA(endsz1) // has to be set existing NE in calling method
                .networkElementPortBUuid("nepB") // has to be set existing NEP in calling method
                .endszB(endsz2) // has to be set existing NE in calling method
                .lbz(getLbzByEndsz(lsz, orderNo, endsz1, endsz2)) // has to be linked existing NEs in calling method
                .description("NEL for integration test")
                .lsz(lsz)
                .lifecycleState(INSTALLING)
                .operationalState(INSTALLING)
                .orderNumber(orderNo)
                .pluralId("2")
                .ueWegId("uewegId")
                .creationTime(OffsetDateTime.now())
                .lastUpdateTime(OffsetDateTime.now())
                .lastSuccessfulSyncTime(OffsetDateTime.now());
    }

    public String getLbzByEndsz(String lsz, String orderNo, String endsz1, String endsz2) {
        return lsz + "/" + orderNo + "-" + endsz1 + "-" + endsz2;
    }

    public TerminationPointDto getDefaultTerminationPointData() {
        return new TerminationPointDto()
                .uuid(UUID.randomUUID().toString()) // Unique constraint for TPs
                .parentUuid("parentUuid") // has to be set to existing NEP or NEG in calling method
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
                .lineId("LINEID-" + getRandomDigits(6))
                .ontSerialNumber("ONTSERIALNUMBER-" + getRandomDigits(6))
                .terminationPointFtthAccessUuid(TP_UUID) // has to be set to existing TP in calling method
                .oltPortOntLastRegisteredOn("nepUuid") // has to be set to existing NEP in calling method
                .href("HREF?")
                .specificationVersion("3")
                .virtualServiceProvider("ein Virtual Service Provider")
                .administrativeMode(ACTIVATED)
                .operationalState(INSTALLING)
                .lifecycleState(INSTALLING)
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
                .terminationPointA10NspUuid(TP_UUID) // has to be set to existing TP in calling method
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
                .terminationPointL2BsaUuid(TP_UUID) // has to be set to existing TP in calling method
                .href("HREF")
                .specificationVersion("1")
                .virtualServiceProvider("a Virtual Service Provider")
                .administrativeMode("admMode") // neu im Model
                .operationalState("opState")
                .lifecycleState("lcState")
                .lineId("lineId")
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

        if (isNullOrEmpty(negData.getUuid()))
            negData.setUuid(neg.getUuid());

        if (isNullOrEmpty(negData.getName()))
            negData.setName(neg.getName()); // satisfy unique constraints

        neg.setUuid(negData.getUuid());
        neg.setName(negData.getName());
        neg.setOperationalState(negData.getOperationalState());
        neg.setLifecycleState(negData.getLifecycleState());

        return neg;
    }

    public NetworkElementDto getNetworkElementDto(A4NetworkElement neData, String negUuid) {
        NetworkElementDto ne = getNetworkElementDto(negUuid);

        if (isNullOrEmpty(neData.getUuid()))
            neData.setUuid(ne.getUuid());

        if (isNullOrEmpty(neData.getFsz()))
            neData.setFsz(ne.getFsz()); // satisfy unique constraints

        ne.setUuid(neData.getUuid());
        ne.setOperationalState(neData.getOperationalState());
        ne.setLifecycleState(neData.getLifecycleState());
        ne.setCategory(neData.getCategory());
        ne.setType(neData.getType());
        ne.setVpsz(neData.getVpsz());
        ne.setFsz(neData.getFsz());
        ne.setKlsId(neData.getKlsId());
        ne.setPlanningDeviceName(neData.getPlanningDeviceName());
        ne.setPlannedMatNumber(neData.getPlannedMatNr());

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

        if (isNullOrEmpty(nepData.getUuid()))
            nepData.setUuid(nep.getUuid());

        if (isNullOrEmpty(nepData.getFunctionalPortLabel()))
            nepData.setFunctionalPortLabel(nep.getLogicalLabel());

        if (isNullOrEmpty(nepData.getType()))
            nepData.setType(nep.getType());

        if (isNullOrEmpty(nepData.getDescription()))
            nepData.setDescription(nep.getDescription());

        if(isNullOrEmpty(nepData.getOperationalState()))
            nepData.setOperationalState(nep.getOperationalState());

        nep.setUuid(nepData.getUuid());
        nep.setDescription(nepData.getDescription());
        nep.setLogicalLabel(nepData.getFunctionalPortLabel());
        nep.setPortNumber(getPortNumberByFunctionalPortLabel(nepData.getFunctionalPortLabel()));
        nep.setOperationalState(nepData.getOperationalState());
        nep.setType(nepData.getType());

        return nep;
    }

    public NetworkElementLinkDto getNetworkElementLinkDto(A4NetworkElementLink nelData, A4NetworkElementPort nepDataA, A4NetworkElementPort nepDataB) {
        A4NetworkElement neDataA = new A4NetworkElement();
        A4NetworkElement neDataB = new A4NetworkElement();
        UewegData uewegData = new UewegData();

        neDataA.setVpsz(getRandomDigits(4));
        neDataA.setFsz(getRandomDigits(4));

        neDataB.setVpsz(getRandomDigits(4));
        neDataB.setFsz(getRandomDigits(4));

        uewegData.setUewegId(nelData.getUeWegId());

        return getNetworkElementLinkDto(nelData, nepDataA, nepDataB, neDataA, neDataB, uewegData);
    }

    public NetworkElementLinkDto getNetworkElementLinkDto(A4NetworkElementLink nelData, A4NetworkElementPort nepDataA, A4NetworkElementPort nepDataB, A4NetworkElement neDataA, A4NetworkElement neDataB) {
        UewegData uewegData = new UewegData();

        if (isNullOrEmpty(nelData.getUeWegId()))
            uewegData.setUewegId(UUID.randomUUID().toString());
        else
            uewegData.setUewegId(nelData.getUeWegId());

        return getNetworkElementLinkDto(nelData, nepDataA, nepDataB, neDataA, neDataB, uewegData);
    }

    public NetworkElementLinkDto getNetworkElementLinkDto(A4NetworkElementLink nelData, A4NetworkElementPort nepDataA, A4NetworkElementPort nepDataB, A4NetworkElement neDataA, A4NetworkElement neDataB, UewegData uewegData) {
        NetworkElementLinkDto nel = getNetworkElementLinkDto(nepDataA.getUuid(), nepDataB.getUuid(), neDataA.getVpsz(), neDataA.getFsz(), neDataB.getVpsz(), neDataB.getFsz());

        if (isNullOrEmpty(nelData.getUuid()))
            nelData.setUuid(nel.getUuid());

        if (isNullOrEmpty(uewegData.getUewegId()))
            uewegData.setUewegId(nel.getUeWegId());

        if (isNullOrEmpty(nelData.getLsz()))
            nelData.setLsz(nel.getLsz());

        if (isNullOrEmpty(nelData.getLbz()))
            nelData.setLbz(nel.getLbz());

        nel.setUuid(nelData.getUuid());
        nel.setLifecycleState(nelData.getLifecycleState());
        nel.setOperationalState(nelData.getOperationalState());
        nel.setUeWegId(uewegData.getUewegId());
        nel.setLsz(nelData.getLsz());
        nel.setLbz(nelData.getLbz());

        return nel;
    }

    public NetworkElementLinkDto getNetworkElementLinkDto(String nepUuid1, String nepUuid2, String neVpsz1, String neFsz1, String neVpsz2, String neFsz2) {
        final String endsz1 = getEndsz(neVpsz1, neFsz1);
        final String endsz2 = getEndsz(neVpsz2, neFsz2);

        NetworkElementLinkDto nel = getDefaultNetworkElementLinkData();
        nel.setNetworkElementPortAUuid(nepUuid1);
        nel.setNetworkElementPortBUuid(nepUuid2);
        nel.setEndszA(endsz1);
        nel.setEndszB(endsz2);
        nel.setLbz(getLbzByEndsz(nel.getLsz(), nel.getOrderNumber(), endsz1, endsz2)); // LBZ is unique constraint!

        return nel;
    }

    public TerminationPointDto getTerminationPointDto(A4TerminationPoint tpData, String uuid) {
        TerminationPointDto tp = getTerminationPointDto(uuid);

        if (isNullOrEmpty(tpData.getUuid()))
            tpData.setUuid(tp.getUuid());

        tp.setUuid(tpData.getUuid());
        tp.setCarrierBsaReference(tpData.getCarrierBsaReference());
        tp.setType(tpData.getSubType());

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

        if (isNullOrEmpty(nspData.getUuid()))
            nspData.setUuid(nspFtth.getUuid());

        if (isNullOrEmpty(nspData.getLineId()))
            nspData.setLineId(nspFtth.getLineId()); // satisfy unique constraints

        if (isNullOrEmpty(nspData.getOntSerialNumber()))
            nspData.setOntSerialNumber(nspFtth.getOntSerialNumber()); // satisfy unique constraints

        nspFtth.setUuid(nspData.getUuid());
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

        if (isNullOrEmpty(nspData.getUuid()))
            nspData.setUuid(nspA10.getUuid());

        nspA10.setUuid(nspData.getUuid());
        nspA10.setOperationalState(nspData.getOperationalState());
        nspA10.setLifecycleState(nspData.getLifecycleState());
        nspA10.setNumberOfAssociatedNsps(nspData.getNumberOfAssociatedNsps());

        return nspA10;
    }

    public NetworkServiceProfileA10NspDto getNetworkServiceProfileA10NspDto(String tpUuid) {
        NetworkServiceProfileA10NspDto nspA10 = getDefaultNetworkServiceProfileA10NspData();
        nspA10.setTerminationPointA10NspUuid(tpUuid);

        return nspA10;
    }

    public NetworkServiceProfileL2BsaDto getNetworkServiceProfileL2BsaDto(A4NetworkServiceProfileL2Bsa nspData, A4TerminationPoint tpData) {
        NetworkServiceProfileL2BsaDto nspL2 = getNetworkServiceProfileL2BsaDto(tpData.getUuid());

        if (isNullOrEmpty(nspData.getUuid()))
            nspData.setUuid(nspL2.getUuid());

        nspL2.setUuid(nspData.getUuid());
        nspL2.setAdministrativeMode(nspData.getAdministrativeMode());// neu im Model
        nspL2.setOperationalState(nspData.getOperationalState());
        nspL2.setLifecycleState(nspData.getLifecycleState());
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
