package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.LogicalResourceUpdate;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceCharacteristic;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceRef;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceRelationship;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class A4ResourceInventoryServiceMapper {

    public static final String NEG = "NetworkElementGroup";
    public static final String NE = "NetworkElement";
    public static final String NEP = "NetworkElementPort";
    public static final String TP = "TerminationPoint";
    public static final String NSP_FTTH_ACCESS = "NspFtthAccess";
    public static final String NSP_A10NSP = "NspA10Nsp";
    public static final String NSP_L2BSA = "NspL2Bsa";
    public static final String CHAR_OPSTATE = "operationalState";

    // Create logicalResource representation of network element group with manually set operational state
    public LogicalResourceUpdate getLogicalResourceUpdate(A4NetworkElementGroup negData, String operationalState) {
        return generateNegLogicalResourceUpdate(negData, operationalState);
    }

    public LogicalResourceUpdate getMinimalLogicalResourceUpdate(String elementType) {

        LogicalResourceUpdate lru = new LogicalResourceUpdate();
        lru.setAtType(elementType);

        return lru;
    }

    // Create logicalResource representation of network element with manually set operational state
    public LogicalResourceUpdate getLogicalResourceUpdate(A4NetworkElement neData, A4NetworkElementGroup negData, String operationalState) {
        return generateNeLogicalResourceUpdate(neData, negData, operationalState);
    }

    // Create logicalResource representation of network element port with manually set operational state
    public LogicalResourceUpdate getLogicalResourceUpdate(A4NetworkElementPort nepData, A4NetworkElement neData, String operationalState, String description) {
        return generateNepLogicalResourceUpdate(nepData, neData, operationalState, description);
    }

    public LogicalResourceUpdate getLogicalResourceUpdate(A4TerminationPoint tpData, A4NetworkElementPort nepData) {
        if (tpData.getUuid().isEmpty())
            tpData.setUuid(UUID.randomUUID().toString());

        return generateGenericLogicalResourceUpdate(tpData.getUuid())
                .atType(TP)
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
                                .type(NEP))
                );
    }

    // Create logicalResource representation of network service profile (FTTH Access) with manually set operational state
    public LogicalResourceUpdate getLogicalResourceUpdate(A4NetworkServiceProfileFtthAccess nspFtthData, A4TerminationPoint tpData, String operationalState) {
        return generateNspFtthLogicalResourceUpdate(nspFtthData, tpData, operationalState);
    }

    // Create logicalResource representation of network service profile (FTTH Access) with
    // manually set operational state and port reference
    public LogicalResourceUpdate getLogicalResourceUpdate(A4NetworkServiceProfileFtthAccess nspFtthData,
                                                          A4TerminationPoint tpData,
                                                          String operationalState,
                                                          A4NetworkElementPort nepData) {
        return generateNspFtthLogicalResourceUpdate(nspFtthData, tpData, operationalState, nepData);
    }
    // Create logicalResource representation of network service profile (A10NSP) with manually set operational state
    public LogicalResourceUpdate getLogicalResourceUpdate(A4NetworkServiceProfileA10Nsp nspA10Data, A4TerminationPoint tpData, String operationalState) {
        return generateNspA10NspLogicalResourceUpdate(nspA10Data, tpData, operationalState);
    }

    // Create logicalResource representation of network service profile (L2BSA) with manually set operational state
    public LogicalResourceUpdate getLogicalResourceUpdate(A4NetworkServiceProfileL2Bsa nspL2Data, A4TerminationPoint tpData, String operationalState) {
        return generateNspL2BsaLogicalResourceUpdate(nspL2Data, tpData, operationalState);
    }

    // Create logicalResource representation of network element link with manually set operational state
    public LogicalResourceUpdate getLogicalResourceUpdate(A4NetworkElementLink nelData, A4NetworkElementPort nepDataA, A4NetworkElementPort nepDataB, String operationalState) {
        return generateNelLogicalResourceUpdate(nelData, nepDataA, nepDataB, operationalState);
    }

    public void addCharacteristic(LogicalResourceUpdate lru, String charName, String charValue) {
        ResourceCharacteristic c = new ResourceCharacteristic();
        c.setName(charName);
        c.setValue(charValue);

        if (lru.getCharacteristic() == null)
            lru.setCharacteristic(new ArrayList<>());

        lru.getCharacteristic().add(c);
    }

    private LogicalResourceUpdate generateNegLogicalResourceUpdate(A4NetworkElementGroup negData, String operationalState) {
        return generateGenericLogicalResourceUpdate(negData.getUuid())
                .atType(NEG)
                .name(negData.getName())
                .description("NEG for integration test")
                .lifecycleState(negData.getLifecycleState())
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("centralOfficeNetworkOperator")
                        .value("CONO"))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name(CHAR_OPSTATE)
                        .value(operationalState))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("neTypeName")
                        .value("POD"));
    }

    private LogicalResourceUpdate generateNeLogicalResourceUpdate(A4NetworkElement neData, A4NetworkElementGroup negData, String operationalState) {
        return generateGenericLogicalResourceUpdate(neData.getUuid())
                .atType(NE)
                .description("NE for integration test")
                .lifecycleState(neData.getLifecycleState())
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name(CHAR_OPSTATE)
                        .value(operationalState))
                .addResourceRelationshipItem(new ResourceRelationship()
                        .resourceRef(new ResourceRef()
                                .id(negData.getUuid())
                                .type(NEG)));
    }

    private LogicalResourceUpdate generateNepLogicalResourceUpdate(A4NetworkElementPort nepData, A4NetworkElement neData, String operationalState, String description) {
        return generateGenericLogicalResourceUpdate(nepData.getUuid())
                .atType(NEP)
                .description(description)
                // NEPs do not have a lifecycle state
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name(CHAR_OPSTATE)
                        .value(operationalState))
                .addResourceRelationshipItem(new ResourceRelationship()
                        .resourceRef(new ResourceRef()
                                .id(neData.getUuid())
                                .type(NE)));
    }

    private LogicalResourceUpdate generateNspFtthLogicalResourceUpdate(A4NetworkServiceProfileFtthAccess nspFtthData, A4TerminationPoint tpData, String operationalState) {
        return generateGenericLogicalResourceUpdate(nspFtthData.getUuid())
                .atType(NSP_FTTH_ACCESS)
                .description("NSP-FTTH-ACCESS for integration test")
                .lifecycleState(nspFtthData.getLifecycleState())
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name(CHAR_OPSTATE)
                        .value(operationalState))
                .addResourceRelationshipItem(new ResourceRelationship()
                        .resourceRef(new ResourceRef()
                                .id(tpData.getUuid())
                                .type(TP)));
    }

    private LogicalResourceUpdate generateNspFtthLogicalResourceUpdate(A4NetworkServiceProfileFtthAccess nspFtthData,
                                                                       A4TerminationPoint tpData,
                                                                       String operationalState,
                                                                       A4NetworkElementPort nepData) {
        return generateGenericLogicalResourceUpdate(nspFtthData.getUuid())
                .atType(NSP_FTTH_ACCESS)
                .description("NSP-FTTH-ACCESS with Port Ref for integration test")
                .lifecycleState(nspFtthData.getLifecycleState())
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name(CHAR_OPSTATE)
                        .value(operationalState))
                .addResourceRelationshipItem(new ResourceRelationship()
                        .resourceRef(new ResourceRef()
                                .id(tpData.getUuid())
                                .type(TP)))
                .addResourceRelationshipItem(new ResourceRelationship()
                        .resourceRef(new ResourceRef()
                                .id(nepData.getUuid())
                                .type(NEP)));
    }

    private LogicalResourceUpdate generateNspA10NspLogicalResourceUpdate(A4NetworkServiceProfileA10Nsp nspA10Data, A4TerminationPoint tpData, String operationalState) {
        return generateGenericLogicalResourceUpdate(nspA10Data.getUuid())
                .atType(NSP_A10NSP)
                .description("NSP-A10NSP for integration test")
                .lifecycleState(nspA10Data.getLifecycleState())
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name(CHAR_OPSTATE)
                        .value(operationalState))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("lacpActive")
                        .value("false"))
                .addResourceRelationshipItem(new ResourceRelationship()
                        .resourceRef(new ResourceRef()
                                .id(tpData.getUuid())
                                .type(TP)));
    }

    private LogicalResourceUpdate generateNspL2BsaLogicalResourceUpdate(A4NetworkServiceProfileL2Bsa nspL2Data, A4TerminationPoint tpData, String operationalState) {
        return generateGenericLogicalResourceUpdate(nspL2Data.getUuid())
                .atType(NSP_L2BSA)
                .description("NSP-L2BSA for integration test")
                .lifecycleState(nspL2Data.getLifecycleState())
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name(CHAR_OPSTATE)
                        .value(operationalState))
                .addResourceRelationshipItem(new ResourceRelationship()
                        .resourceRef(new ResourceRef()
                                .id(tpData.getUuid())
                                .type(TP)));
    }

    private LogicalResourceUpdate generateNelLogicalResourceUpdate(A4NetworkElementLink nelData, A4NetworkElementPort nepDataA, A4NetworkElementPort nepDataB, String operationalState) {
        return generateGenericLogicalResourceUpdate(nelData.getUuid())
                .atType("NetworkElementLink")
                .description("NEL for integration test")
                .lifecycleState(nelData.getLifecycleState())
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name(CHAR_OPSTATE)
                        .value(operationalState))
                .addResourceRelationshipItem(new ResourceRelationship()
                        .resourceRef(new ResourceRef()
                                .id(nepDataA.getUuid())
                                .type(NEP)))
                .addResourceRelationshipItem(new ResourceRelationship()
                        .resourceRef(new ResourceRef()
                                .id(nepDataB.getUuid())
                                .type(NEP)));
    }

    private LogicalResourceUpdate generateGenericLogicalResourceUpdate(String uuid) {
        return new LogicalResourceUpdate()
                .atBaseType("LogicalResource")
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
