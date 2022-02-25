package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceV4Robot.getPortNumberByFunctionalPortLabel;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.*;

public class A4ResourceInventoryMapper {

    public static final String NEL_LSZ = "4N4";
    public static final String NEL_ORDER_NUMBER = "1004";
    public static final String ACTIVATED = "ACTIVATED";
    public static final String INSTALLING = "INSTALLING";
    public static final String WORKING = "WORKING";
    public static final String DATE_TIME = "2020-07-14T13:59:18+02:00";

    public NetworkElementDto getNetworkElementDto(A4NetworkElement neData, A4NetworkElementGroup negData) {
        if (isNullOrEmpty(neData.getUuid()))
            neData.setUuid(UUID.randomUUID().toString());

        if (isNullOrEmpty(neData.getFsz()))
            neData.setFsz(getRandomDigits(4)); // satisfy unique constraints

        return new NetworkElementDto()
                .uuid(neData.getUuid())
                .networkElementGroupUuid(negData.getUuid())
                .description("NE for integration test")
                .address("address")
                .administrativeState(ACTIVATED)
                .lifecycleState(neData.getLifecycleState())
                .operationalState(neData.getOperationalState())
                .category(neData.getCategory())
                .fsz(neData.getFsz())
                .vpsz(neData.getVpsz())
                .klsId(neData.getKlsId())
                .plannedRackId("rackid")
                .plannedRackPosition("rackpos")
                .planningDeviceName(neData.getPlanningDeviceName())
                .roles("role")
                .type(neData.getType())
                .creationTime(OffsetDateTime.now())
                .plannedMatNumber(neData.getPlannedMatNr())
                .lastUpdateTime(OffsetDateTime.now())
                .lastSuccessfulSyncTime(OffsetDateTime.now());
    }

    public NetworkElementGroupDto getNetworkElementGroupDto(A4NetworkElementGroup negData) {
        if (isNullOrEmpty(negData.getUuid()))
            negData.setUuid(UUID.randomUUID().toString());

        if (isNullOrEmpty(negData.getName()))
            negData.setName("NEG-" + getRandomDigits(6)); // satisfy unique constraints

        return new NetworkElementGroupDto()
                .uuid(negData.getUuid())
                .type("POD")
                .specificationVersion("1")
                .operationalState(negData.getOperationalState())
                .name(negData.getName())
                .lifecycleState(negData.getLifecycleState())
                .lastUpdateTime(OffsetDateTime.now())
                .lastSuccessfulSyncTime(OffsetDateTime.now())
                .description("NEG created during osr-test integration test")
                .creationTime(OffsetDateTime.now())
                .centralOfficeNetworkOperator("neg_centOffNetOp_for_integration_test");
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
        if (isNullOrEmpty(nelData.getUuid()))
            nelData.setUuid(UUID.randomUUID().toString());

        if (isNullOrEmpty(uewegData.getUewegId()))
            uewegData.setUewegId(UUID.randomUUID().toString());

        nelData.setLbz(NEL_LSZ + "/" + NEL_ORDER_NUMBER + "-" + getEndsz(neDataA) + "-" + getEndsz(neDataB)); // LBZ is unique constraint!

        return new NetworkElementLinkDto()
                .uuid(nelData.getUuid())
                .networkElementPortAUuid(nepDataA.getUuid())
                .endszA(neDataA.getVpsz() + "/" + neDataA.getFsz())
                .networkElementPortBUuid(nepDataB.getUuid())
                .endszB(neDataB.getVpsz() + "/" + neDataB.getFsz())
                .description("NEL for integration test")
                .creationTime(OffsetDateTime.now())
                .lastUpdateTime(OffsetDateTime.now())
                .lastSuccessfulSyncTime(OffsetDateTime.now())
                .lsz(NEL_LSZ)
                .lifecycleState(nelData.getLifecycleState())
                .operationalState(nelData.getOperationalState())
                .orderNumber(NEL_ORDER_NUMBER)
                .pluralId("2")
                .ueWegId(uewegData.getUewegId())
                .lbz(nelData.getLbz());
    }

    public NetworkElementPortDto getNetworkElementPortDto(A4NetworkElementPort nepData, A4NetworkElement neData) {
        if (isNullOrEmpty(nepData.getUuid()))
            nepData.setUuid(UUID.randomUUID().toString());

        if (isNullOrEmpty(nepData.getFunctionalPortLabel()))
            nepData.setFunctionalPortLabel("GPON_" + getRandomDigits(4));

        if (isNullOrEmpty(nepData.getType()))
            nepData.setType("GPON");

        if (isNullOrEmpty(nepData.getDescription()))
            nepData.setDescription("NEP for integration test");

        return new NetworkElementPortDto()
                .uuid(nepData.getUuid())
                .networkElementUuid(neData.getUuid())
                .networkElementEndsz(this.getEndszFromVpszAndFsz(neData.getVpsz(), neData.getFsz()))
                .logicalLabel(nepData.getFunctionalPortLabel())
                .portNumber(getPortNumberByFunctionalPortLabel(nepData.getFunctionalPortLabel()))
                .accessNetworkOperator("NetOp")
                .administrativeState(ACTIVATED)
                .operationalState(nepData.getOperationalState())
                .type(nepData.getType())
                .creationTime(OffsetDateTime.now())
                .lastUpdateTime(OffsetDateTime.now())
                .lastSuccessfulSyncTime(OffsetDateTime.now());
    }

    public TerminationPointDto getTerminationPointDto(A4TerminationPoint tpData, String uuid) {
        if (isNullOrEmpty(tpData.getUuid()))
            tpData.setUuid(UUID.randomUUID().toString());

        return new TerminationPointDto()
                .uuid(tpData.getUuid())
                .parentUuid(uuid)
                .description("TP for integration test")
                .lockedForNspUsage(true)
                .state("state")
                .carrierBsaReference(tpData.getCarrierBsaReference())
                .supportedDiagnosesName("supportedDiagnoseName")
                .supportedDiagnosesSpecificationVersion("supportedDiagnoseVerison")
                .type(tpData.getSubType())
                .creationTime(OffsetDateTime.now())
                .lastUpdateTime(OffsetDateTime.now());
    }

    public NetworkServiceProfileFtthAccessDto getNetworkServiceProfileFtthAccessDto(A4NetworkServiceProfileFtthAccess nspData, A4TerminationPoint tpData) {
        if (isNullOrEmpty(nspData.getUuid()))
            nspData.setUuid(UUID.randomUUID().toString());

        if (isNullOrEmpty(nspData.getLineId()))
            nspData.setLineId("LINEID-" + UUID.randomUUID().toString().substring(0, 6)); // satisfy unique constraints

        if (isNullOrEmpty(nspData.getOntSerialNumber()))
            nspData.setOntSerialNumber("ONTSERIALNUMBER-" + getRandomDigits(6)); // satisfy unique constraints

        return new NetworkServiceProfileFtthAccessDto()
                .uuid(nspData.getUuid())
                .href("HREF?")
                .ontSerialNumber(nspData.getOntSerialNumber())
                .lineId(nspData.getLineId())
                .specificationVersion("3")
                .virtualServiceProvider("ein Virtual Service Provider")
                .administrativeMode(ACTIVATED)
                .operationalState(nspData.getOperationalState())
                .lifecycleState(nspData.getLifecycleState())
                .terminationPointFtthAccessUuid(tpData.getUuid())
                .lastUpdateTime(OffsetDateTime.now())
                .lastSuccessfulSyncTime(OffsetDateTime.now())
                .description("NSP FTTH Access created during osr-test integration test")
                .creationTime(OffsetDateTime.now());
    }

    public NetworkServiceProfileFtthAccessDto getNetworkServiceProfileFtthAccessDto(
            A4NetworkServiceProfileFtthAccess nspData,
            A4TerminationPoint tpData,
            A4NetworkElementPort nepData) {

        if (isNullOrEmpty(nspData.getUuid()))
            nspData.setUuid(UUID.randomUUID().toString());

        if (isNullOrEmpty(nspData.getLineId()))
            nspData.setLineId("LINEID-" + UUID.randomUUID().toString().substring(0, 6)); // satisfy unique constraints

        if (isNullOrEmpty(nspData.getOntSerialNumber()))
            nspData.setOntSerialNumber("ONTSERIALNUMBER-" + getRandomDigits(6)); // satisfy unique constraints

        return new NetworkServiceProfileFtthAccessDto()
                .uuid(nspData.getUuid())
                .href("HREF?")
                .ontSerialNumber(nspData.getOntSerialNumber())
                .lineId(nspData.getLineId())
                .specificationVersion("3")
                .virtualServiceProvider("ein Virtual Service Provider")
                .administrativeMode(ACTIVATED)
                .operationalState(nspData.getOperationalState())
                .lifecycleState(nspData.getLifecycleState())
                .terminationPointFtthAccessUuid(tpData.getUuid())
                .oltPortOntLastRegisteredOn(nepData.getUuid())
                .lastUpdateTime(OffsetDateTime.now())
                .lastSuccessfulSyncTime(OffsetDateTime.now())
                .description("NSP FTTH Access created during osr-test integration test")
                .creationTime(OffsetDateTime.now());
    }

    public NetworkServiceProfileA10NspDto getNetworkServiceProfileA10NspDto(A4NetworkServiceProfileA10Nsp nspData, A4TerminationPoint tpData) {
        if (isNullOrEmpty(nspData.getUuid()))
            nspData.setUuid(UUID.randomUUID().toString());

        VlanRangeDto vrDto = new VlanRangeDto();
        final String UNDEFINED = "undefined";
        vrDto.setVlanRangeLower(UNDEFINED);
        vrDto.setVlanRangeUpper(UNDEFINED);

        A10NspQosDto a10NspQosDto = new A10NspQosDto();
        a10NspQosDto.setQosBandwidthDown(UNDEFINED);
        a10NspQosDto.setQosBandwidthUp(UNDEFINED);
        a10NspQosDto.setQosPriority(UNDEFINED);

        return new NetworkServiceProfileA10NspDto()
                .uuid(nspData.getUuid())
                .href("HREF")
                .specificationVersion("1")
                .virtualServiceProvider("a Virtual Service Provider")
                .administrativeMode(ACTIVATED)
                .operationalState(nspData.getOperationalState())
                .lifecycleState(nspData.getLifecycleState())
                .terminationPointA10NspUuid(tpData.getUuid())
                .lastUpdateTime(OffsetDateTime.now())
                .lastSuccessfulSyncTime(OffsetDateTime.now())
                .description("NSP A10NSP created during osr-test integration test")
                .creationTime(OffsetDateTime.now())
                .mtuSize("1590")
                .etherType("0x88a8")
                .lacpActive(true)
                .minActiveLagLinks("1")
                .qosMode("TOLERANT")
                .carrierBsaReference("CarrierBsaReference")
                .numberOfAssociatedNsps(nspData.getNumberOfAssociatedNsps())
                .itAccountingKey(UNDEFINED)
                .lacpMode(UNDEFINED)
                .dataRate(UNDEFINED)
                .numberOfAssociatedNsps(UNDEFINED)
                .sVlanRange(Collections.singletonList(vrDto))
                .qosClasses(Collections.singletonList(a10NspQosDto));
    }

    public NetworkServiceProfileL2BsaDto getNetworkServiceProfileL2BsaDto(A4NetworkServiceProfileL2Bsa nspData, A4TerminationPoint tpData) {
        if (isNullOrEmpty(nspData.getUuid()))
            nspData.setUuid(UUID.randomUUID().toString());

        ServiceBandwidthDto serviceBandwidthDto = new ServiceBandwidthDto();
        serviceBandwidthDto.setDataRateUp("150000");
        serviceBandwidthDto.setDataRateDown("300000");

        List<ServiceBandwidthDto> serviceBandwidthDtoList = new ArrayList<>();
        serviceBandwidthDtoList.add(serviceBandwidthDto);

        return new NetworkServiceProfileL2BsaDto()
                .uuid(nspData.getUuid())
                .href("HREF")
                .specificationVersion("1")
                .virtualServiceProvider("a Virtual Service Provider")
                .administrativeMode(nspData.getAdministrativeMode()) // neu im Model
                .operationalState(nspData.getOperationalState())
                .lifecycleState(nspData.getLifecycleState())
                .lineId(nspData.getLineId())
                .l2CcId(nspData.getL2CcId())
                .terminationPointL2BsaUuid(tpData.getUuid())
                .lastUpdateTime(OffsetDateTime.now())
                .lastSuccessfulSyncTime(OffsetDateTime.now())
                .description("NSP L2BSA created during osr-test integration test")
                .creationTime(OffsetDateTime.now())
//                .nspAccess("123") // skip this because optional
                .serviceBandwidth(serviceBandwidthDtoList);
    }

    private String getEndszFromVpszAndFsz(String vpsz, String fsz) {
        return vpsz.concat("/").concat(fsz);
    }

    public List<NetworkServiceProfileFtthAccessDto> getListOfNspWithoutOntLastRegisteredOn() {
        List<NetworkServiceProfileFtthAccessDto> networkServiceProfileFtthAccessDtos = new ArrayList<>();
        networkServiceProfileFtthAccessDtos.add(getNspWithoutOntLastRegisteredOn());
        return networkServiceProfileFtthAccessDtos;
    }

    public NetworkServiceProfileFtthAccessDto getNspWithoutOntLastRegisteredOn() {
        NetworkServiceProfileFtthAccessDto networkServiceProfileFtthAccessDto = new NetworkServiceProfileFtthAccessDto();
        networkServiceProfileFtthAccessDto.setUuid(UUID.randomUUID().toString());
        networkServiceProfileFtthAccessDto.setLifecycleState(INSTALLING);
        networkServiceProfileFtthAccessDto.setOperationalState(WORKING);
        networkServiceProfileFtthAccessDto.setDescription("A4 Stub without oltPortOntLastRegisteredOn");
        networkServiceProfileFtthAccessDto.setAdministrativeMode("adm mode");
        networkServiceProfileFtthAccessDto.setVirtualServiceProvider("virt serv prov");
        networkServiceProfileFtthAccessDto.setSpecificationVersion("14.1");
        networkServiceProfileFtthAccessDto.setLineId("line id");
        networkServiceProfileFtthAccessDto.setOntSerialNumber("serial no");
        networkServiceProfileFtthAccessDto.setLastUpdateTime(OffsetDateTime.now());
        networkServiceProfileFtthAccessDto.setCreationTime(OffsetDateTime.now().minusDays(1));
        networkServiceProfileFtthAccessDto.setTerminationPointFtthAccessUuid(UUID.randomUUID().toString());
        networkServiceProfileFtthAccessDto.setHref("href");
        return networkServiceProfileFtthAccessDto;
    }

    public NetworkElementPortDto getNetworkElementPortDto(String endSz, String port) {
        NetworkElementPortDto networkElementPortDto = new NetworkElementPortDto();
        networkElementPortDto.setOperationalState(INSTALLING);
        networkElementPortDto.setAdministrativeState(WORKING);
        networkElementPortDto.setLogicalLabel("998");
        networkElementPortDto.setAccessNetworkOperator("AccessNetworkOperator");
        networkElementPortDto.setType("type");
        networkElementPortDto.setPortNumber(port);
        networkElementPortDto.setUuid(UUID.randomUUID().toString());
        networkElementPortDto.setCreationTime(OffsetDateTime.now().minusDays(1));
        networkElementPortDto.setLastUpdateTime(OffsetDateTime.now());
        networkElementPortDto.setSpecificationVersion("14.1");
        networkElementPortDto.setDescription("A4 mock for getNetworkElementPortDto");
        networkElementPortDto.setNetworkElementUuid(UUID.randomUUID().toString());
        networkElementPortDto.setNetworkElementEndsz(endSz);
        networkElementPortDto.setHref("/networkElements/3e2fece2-5b18-440c-89ef-4441d0320cea");
        return networkElementPortDto;
    }

    public NetworkElementDto getNetworkElementDto() {
        NetworkElementDto networkElementDto = new NetworkElementDto();
        networkElementDto.setUuid("444");
        networkElementDto.setCreationTime(OffsetDateTime.parse(DATE_TIME));
        networkElementDto.setDescription("string");
        networkElementDto.setLastUpdateTime(OffsetDateTime.parse(DATE_TIME));
        networkElementDto.lastSuccessfulSyncTime(OffsetDateTime.parse(DATE_TIME));
        networkElementDto.setSpecificationVersion("string");
        networkElementDto.setAddress("Berlin");
        networkElementDto.setAdministrativeState(ACTIVATED);
        networkElementDto.setFsz("7KH4");
        networkElementDto.setVpsz("49/30/179");
        networkElementDto.setKlsId("123456");
        networkElementDto.setLifecycleState(INSTALLING);
        networkElementDto.setOperationalState("NOT_WORKING");
        networkElementDto.setPlannedMatNumber("40958960");
        networkElementDto.setPlannedRackId("000031-000000-001-004-002-021");
        networkElementDto.setPlannedRackPosition("1 / 2 / 3 / 4");
        networkElementDto.setPlanningDeviceName("dmst.olt.1");
        networkElementDto.setRoles("SE");
        networkElementDto.setType("A4-OLT-v1");
        networkElementDto.setCategory("OLT");
        networkElementDto.setZtpIdent("1234567890123456");
        networkElementDto.setFiberOnLocationId("100000005");
        networkElementDto.setNetworkElementGroupUuid("672f71da-15cc-499e-85ee-fbadee9c97be");
        networkElementDto.setPartyId("10001");
        networkElementDto.setHref("/networkElementGroups/672f71da-15cc-499e-85ee-fbadee9c97be");
        return networkElementDto;
    }

}
