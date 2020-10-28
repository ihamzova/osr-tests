package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.LogicalResourceUpdate;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceCharacteristic;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceRef;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceRelationship;

import java.time.OffsetDateTime;
import java.util.UUID;

public class A4ResourceInventoryServiceMapper {
    // Create logicalResource representation of network element group with operational state as defined in data package
    public LogicalResourceUpdate getLogicalResourceUpdate(A4NetworkElementGroup negData) {
        return generateNegLogicalResourceUpdate(negData, negData.getOperationalState());
    }

    // Create logicalResource representation of network element group with manually set operational state
    public LogicalResourceUpdate getLogicalResourceUpdate(A4NetworkElementGroup negData, String operationalState) {
        return generateNegLogicalResourceUpdate(negData, operationalState);
    }

    // Create logicalResource representation of network element with manually set operational state
    public LogicalResourceUpdate getLogicalResourceUpdate(A4NetworkElement neData, A4NetworkElementGroup negData, String operationalState) {
        return generateNeLogicalResourceUpdate(neData, negData, operationalState);
    }

    // Create logicalResource representation of network element port with manually set operational state
    public LogicalResourceUpdate getLogicalResourceUpdate(A4NetworkElementPort nepData, A4NetworkElement neData, String operationalState) {
        return generateNepLogicalResourceUpdate(nepData, neData, operationalState);
    }

    public LogicalResourceUpdate getLogicalResourceUpdate(A4TerminationPoint tpData, A4NetworkElementPort nepData) {
        if (tpData.getUuid().isEmpty())
            tpData.setUuid(UUID.randomUUID().toString());

        return generateGenericLogicalResourceUpdate(tpData.getUuid())
                .type("TerminationPoint")
                .version("1")
                .description("TP for integration test")
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("subType")
                        .value(tpData.getSubType()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("state")
                        .value("state"))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("lockedForNspUsage")
                        .value("true"))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("supportedDiagnosesName")
                        .value("(Diagnose1,Diagnose2)"))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("supportedDiagnosesSpecificationVersion")
                        .value("(V1,V2)"))
                .addResourceRelationshipItem(new ResourceRelationship()
                        .resourceRef(new ResourceRef()
                                .id(nepData.getUuid())
                                .type("NetworkElementPort"))
                );
    }

    // Create logicalResource representation of network service profile (FTTH Access) with manually set operational state
    public LogicalResourceUpdate getLogicalResourceUpdate(A4NetworkServiceProfileFtthAccess nspFtthData, A4TerminationPoint tpData, String operationalState) {
        return generateNspFtthLogicalResourceUpdate(nspFtthData, tpData, operationalState);
    }

    // Create logicalResource representation of network element link with manually set operational state
    public LogicalResourceUpdate getLogicalResourceUpdate(A4NetworkElementLink nelData, A4NetworkElementPort nepDataA, A4NetworkElementPort nepDataB, String operationalState) {
        return generateNelLogicalResourceUpdate(nelData, nepDataA, nepDataB, operationalState);
    }

    private LogicalResourceUpdate generateNegLogicalResourceUpdate(A4NetworkElementGroup negData, String operationalState) {
        return generateGenericLogicalResourceUpdate(negData.getUuid())
                .type("NetworkElementGroup")
                .name(negData.getName())
                .description("NEG for integration test")
                .lifecycleState(negData.getLifecycleState())
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("centralOfficeNetworkOperator")
                        .value("CONO"))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("operationalState")
                        .value(operationalState))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("neTypeName")
                        .value("POD"));
    }

    private LogicalResourceUpdate generateNeLogicalResourceUpdate(A4NetworkElement neData, A4NetworkElementGroup negData, String operationalState) {
        return generateGenericLogicalResourceUpdate(neData.getUuid())
                .type("NetworkElement")
                .description("NE for integration test")
                .lifecycleState(neData.getLifecycleState())
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("operationalState")
                        .value(operationalState))
                .addResourceRelationshipItem(new ResourceRelationship()
                        .resourceRef(new ResourceRef()
                                .id(negData.getUuid())
                                .type("NetworkElementGroup")));
    }

    private LogicalResourceUpdate generateNepLogicalResourceUpdate(A4NetworkElementPort nepData, A4NetworkElement neData, String operationalState) {
        return generateGenericLogicalResourceUpdate(nepData.getUuid())
                .type("NetworkElementPort")
                .description("NEP for integration test")
                // NEPs do not have a lifecycle state
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("operationalState")
                        .value(operationalState))
                .addResourceRelationshipItem(new ResourceRelationship()
                        .resourceRef(new ResourceRef()
                                .id(neData.getUuid())
                                .type("NetworkElement")));
    }

    private LogicalResourceUpdate generateNspFtthLogicalResourceUpdate(A4NetworkServiceProfileFtthAccess nspFtthData, A4TerminationPoint tpData, String operationalState) {
        return generateGenericLogicalResourceUpdate(nspFtthData.getUuid())
                .type("NspFtthAccess")
                .description("NSP-FTTH-ACCESS for integration test")
                .lifecycleState(nspFtthData.getLifecycleState())
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("operationalState")
                        .value(operationalState))
                .addResourceRelationshipItem(new ResourceRelationship()
                        .resourceRef(new ResourceRef()
                                .id(tpData.getUuid())
                                .type("TerminationPoint")));
    }

    private LogicalResourceUpdate generateNelLogicalResourceUpdate(A4NetworkElementLink nelData, A4NetworkElementPort nepDataA, A4NetworkElementPort nepDataB, String operationalState) {
        return generateGenericLogicalResourceUpdate(nelData.getUuid())
                .type("NetworkElementLink")
                .description("NEL for integration test")
                .lifecycleState(nelData.getLifecycleState())
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("operationalState")
                        .value(operationalState))
                .addResourceRelationshipItem(new ResourceRelationship()
                        .resourceRef(new ResourceRef()
                                .id(nepDataA.getUuid())
                                .type("NetworkElementPort")))
                .addResourceRelationshipItem(new ResourceRelationship()
                        .resourceRef(new ResourceRef()
                                .id(nepDataB.getUuid())
                                .type("NetworkElementPort")));
    }

    private LogicalResourceUpdate generateGenericLogicalResourceUpdate(String uuid) {
        return new LogicalResourceUpdate()
                .baseType("LogicalResource")
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("creationTime")
                        .value(OffsetDateTime.now().toString()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("lastUpdateTime")
                        .value(OffsetDateTime.now().toString()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("correlationId")
                        .value(uuid));
    }

}
