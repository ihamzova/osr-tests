package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.*;

import java.time.OffsetDateTime;
import java.util.UUID;

import static com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceV4Robot.getPortNumberByFunctionalPortLabel;

public class A4ResourceInventoryMapper {

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
                .lifecycleState("WORKING")
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
                .type("type")
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
                .productionScheme("ein Production Scheme")
                .administrativeMode("ACTIVATED")
                .operationalState("WORKING")
                .lifecycleState(nspData.getLifecycleState())
                .terminationPointFtthAccessUuid(tpData.getUuid())
                .lastUpdateTime(OffsetDateTime.now())
                .description("NSP created during osr-test integration test")
                .creationTime(OffsetDateTime.now());
    }

    private String getEndszFromVpszAndFsz(String Vpsz, String Fsz) {
        return Vpsz.concat("/").concat(Fsz);
    }

}
