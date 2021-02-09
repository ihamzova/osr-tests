package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceV4Robot.getPortNumberByFunctionalPortLabel;

public class A4ResourceInventoryMapper {

    private final String UNDEFINED = "undefined";

    public NetworkElementDto getNetworkElementDto(A4NetworkElement neData, A4NetworkElementGroup negData) {
        if (neData.getUuid().isEmpty())
            neData.setUuid(UUID.randomUUID().toString());

        if (neData.getFsz().isEmpty())
            neData.setFsz(UUID.randomUUID().toString().substring(0, 4)); // satisfy unique constraints

        return new NetworkElementDto()
                .uuid(neData.getUuid())
                .networkElementGroupUuid(negData.getUuid())
                .description("NE for integration test")
                .address("address")
                .administrativeState("ACTIVATED")
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
                .lastUpdateTime(OffsetDateTime.now());
    }

    public NetworkElementGroupDto getNetworkElementGroupDto(A4NetworkElementGroup negData) {
        if (negData.getUuid().isEmpty())
            negData.setUuid(UUID.randomUUID().toString());

        if (negData.getName().equals(""))
            negData.setName("NEG-" + UUID.randomUUID().toString().substring(0, 6)); // satisfy unique constraints

        return new NetworkElementGroupDto()
                .uuid(negData.getUuid())
                .type("POD")
                .specificationVersion("1")
                .operationalState(negData.getOperationalState())
                .name(negData.getName())
                .lifecycleState(negData.getLifecycleState())
                .lastUpdateTime(OffsetDateTime.now())
                .description("NEG created during osr-test integration test")
                .creationTime(OffsetDateTime.now())
                .centralOfficeNetworkOperator("neg_centOffNetOp_for_integration_test");
    }

    public NetworkElementLinkDto getNetworkElementLinkDto(A4NetworkElementLink nelData, A4NetworkElementPort nepDataA, A4NetworkElementPort nepDataB) {
        if (nelData.getUuid().isEmpty())
            nelData.setUuid(UUID.randomUUID().toString());

        if (nelData.getLbz().isEmpty())
            nelData.setLbz("NEL-lbz-" + UUID.randomUUID().toString().substring(0, 6)); // satisfy unique constraints

        if (nelData.getUeWegId().isEmpty())
            nelData.setUeWegId("NEL-ueWegId-" + UUID.randomUUID().toString().substring(0, 6)); // satisfy unique constraints

        return new NetworkElementLinkDto()
                .uuid(nelData.getUuid())
                .networkElementPortAUuid(nepDataA.getUuid())
                .networkElementPortBUuid(nepDataB.getUuid())
                .description("NEL for integration test")
                .creationTime(OffsetDateTime.now())
                .lastUpdateTime(OffsetDateTime.now())
                .lsz("123")
                .lifecycleState(nelData.getLifecycleState())
                .operationalState(nelData.getOperationalState())
                .orderNumber("1")
                .pluralId("2")
                .ueWegId(nelData.getUeWegId())
                .lbz(nelData.getLbz());
    }

    public NetworkElementPortDto getNetworkElementPortDto(A4NetworkElementPort nepData, A4NetworkElement neData) {
        if (nepData.getUuid().isEmpty())
            nepData.setUuid(UUID.randomUUID().toString());

        if (nepData.getFunctionalPortLabel().isEmpty())
            nepData.setFunctionalPortLabel("GPON_" + UUID.randomUUID().toString().substring(0, 4));

        if (nepData.getType().isEmpty())
            nepData.setType("GPON");

        return new NetworkElementPortDto()
                .uuid(nepData.getUuid())
                .description("NEP for integration test")
                .networkElementUuid(neData.getUuid())
                .networkElementEndsz(this.getEndszFromVpszAndFsz(neData.getVpsz(), neData.getFsz() ))
                .logicalLabel(nepData.getFunctionalPortLabel())
                .portNumber(getPortNumberByFunctionalPortLabel(nepData.getFunctionalPortLabel()))
                .accessNetworkOperator("NetOp")
                .administrativeState("ACTIVATED")
                .operationalState(nepData.getOperationalState())
                .type(nepData.getType())
                .creationTime(OffsetDateTime.now())
                .lastUpdateTime(OffsetDateTime.now());
    }

    public TerminationPointDto getTerminationPointDto(A4TerminationPoint tpData, A4NetworkElementPort nepData) {
        if (tpData.getUuid().isEmpty())
            tpData.setUuid(UUID.randomUUID().toString());

        return new TerminationPointDto()
                .uuid(tpData.getUuid())
                .parentUuid(nepData.getUuid())
                .description("TP for integration test")
                .lockedForNspUsage(true)
                .state("state")
                .supportedDiagnosesName("supportedDiagnoseName")
                .supportedDiagnosesSpecificationVersion("supportedDiagnoseVerison")
                .type(tpData.getSubType())
                .creationTime(OffsetDateTime.now())
                .lastUpdateTime(OffsetDateTime.now());
    }

    public NetworkServiceProfileFtthAccessDto getNetworkServiceProfileFtthAccessDto(A4NetworkServiceProfileFtthAccess nspData, A4TerminationPoint tpData) {
        if (nspData.getUuid().isEmpty())
            nspData.setUuid(UUID.randomUUID().toString());

        if(nspData.getLineId().isEmpty())
            nspData.setLineId("LINEID-" + UUID.randomUUID().toString().substring(0, 6)); // satisfy unique constraints

        if(nspData.getOntSerialNumber().isEmpty())
            nspData.setOntSerialNumber("ONTSERIALNUMBER-" + UUID.randomUUID().toString().substring(0, 6)); // satisfy unique constraints

        return new NetworkServiceProfileFtthAccessDto()
                .uuid(nspData.getUuid())
                .href("HREF?")
                .ontSerialNumber(nspData.getOntSerialNumber())
                .lineId(nspData.getLineId())
                .specificationVersion("3")
                .virtualServiceProvider("ein Virtual Service Provider")
                .administrativeMode("ACTIVATED")
                .operationalState(nspData.getOperationalState())
                .lifecycleState(nspData.getLifecycleState())
                .terminationPointFtthAccessUuid(tpData.getUuid())
                .lastUpdateTime(OffsetDateTime.now())
                .description("NSP FTTH Access created during osr-test integration test")
                .creationTime(OffsetDateTime.now());
    }

    public NetworkServiceProfileA10NspDto getNetworkServiceProfileA10NspDto(A4NetworkServiceProfileA10Nsp nspData, A4TerminationPoint tpData) {
        if (nspData.getUuid().isEmpty())
            nspData.setUuid(UUID.randomUUID().toString());

        VlanRangeDto vrDto = new VlanRangeDto();
        vrDto.setVlanRangeLower(UNDEFINED);
        vrDto.setVlanRangeUpper(UNDEFINED);

        A10NspQosDto a10NspQosDto = new A10NspQosDto();
        a10NspQosDto.setQosBandwidthDown(UNDEFINED);
        a10NspQosDto.setQosBandwidthUp(UNDEFINED);
        a10NspQosDto.setQosClass(UNDEFINED);

        return new NetworkServiceProfileA10NspDto()
                .uuid(nspData.getUuid())
                .href("HREF")
                .specificationVersion("1")
                .virtualServiceProvider("a Virtual Service Provider")
                .administrativeMode("ACTIVATED")
                .operationalState(nspData.getOperationalState())
                .lifecycleState(nspData.getLifecycleState())
                .terminationPointA10NspUuid(tpData.getUuid())
                .lastUpdateTime(OffsetDateTime.now())
                .description("NSP A10NSP created during osr-test integration test")
                .creationTime(OffsetDateTime.now())
                .mtuSize("1590")
                .etherType("0x88a8")
                .lacpActive(true)
                .minActiveLagLinks("1")
                .qosMode("TOLERANT")
                .carrierBsaReference(UNDEFINED)
                .itAccountingKey(UNDEFINED)
                .lacpMode(UNDEFINED)
                .dataRate(UNDEFINED)
                .lagId(UNDEFINED)
                .sVlanRange(Collections.singletonList(vrDto))
                .qosClasses(Collections.singletonList(a10NspQosDto));
    }

    public NetworkServiceProfileL2BsaDto getNetworkServiceProfileL2BsaDto(A4NetworkServiceProfileL2Bsa nspData, A4TerminationPoint tpData) {
        if (nspData.getUuid().isEmpty())
            nspData.setUuid(UUID.randomUUID().toString());

        L2BsaQosDto l2BsaQosDto = new L2BsaQosDto();
        l2BsaQosDto.setQosBandwidthUp("666");
        l2BsaQosDto.setQosBandwidthDown("333");
        l2BsaQosDto.setQosPbit("5");

        List<L2BsaQosDto> l2BsaQosDtoList = new ArrayList<>();
        l2BsaQosDtoList.add(l2BsaQosDto);

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
                .terminationPointL2BsaUuid(tpData.getUuid())
                .lastUpdateTime(OffsetDateTime.now())
                .description("NSP L2BSA created during osr-test integration test")
                .creationTime(OffsetDateTime.now())
//                .nspAccess("123") // skip this because optional
                .activeQosClasses(l2BsaQosDtoList)
                .serviceBandwidth(serviceBandwidthDtoList);
    }

    private String getEndszFromVpszAndFsz(String Vpsz, String Fsz) {
        return Vpsz.concat("/").concat(Fsz);
    }

}
