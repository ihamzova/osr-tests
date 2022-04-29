package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkServiceProfileA10NspDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkServiceProfileL2BsaDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.TerminationPointDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.LogicalResourceUpdate;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceCharacteristic;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceRef;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceRelationship;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class A4ResourceInventoryServiceMapper {

    public static final String NEG = "NetworkElementGroup";
    public static final String NE = "NetworkElement";
    public static final String NEP = "NetworkElementPort";
    public static final String NEL = "NetworkElementLink";
    public static final String TP = "TerminationPoint";
    public static final String NSP_FTTH_ACCESS = "NspFtthAccess";
    public static final String NSP_A10NSP = "NspA10Nsp";
    public static final String NSP_L2BSA = "NspL2Bsa";
    public static final String LOGICAL_RESOURCE = "LogicalResource";
    public static final String OP_STATE = "operationalState";
    public static final String CREATION_TIME = "creationTime";
    public static final String UPDATE_TIME = "lastUpdateTime";
    public static final String SYNC_TIME = "lastSuccessfulSyncTime";
    public static final String CORRELATION_ID = "correlationId";
    public static final String TYPE_NAME = "neTypeName";
    public static final String CENTRAL_NET_OPERATOR = "centralOfficeNetworkOperator";
    public static final String LACP_ACTIVE = "lacpActive";
    public static final String SUB_TYPE = "subType";
    public static final String STATE = "state";
    public static final String LOCKED_NSP_USAGE = "lockedForNspUsage";
    public static final String SUPPORTED_DIAG_NAMES = "supportedDiagnosesName";
    public static final String SUPPORTED_DIAG_VERSION = "supportedDiagnosesSpecificationVersion";

    // Create logicalResource representation of network element group with manually set operational state
    public LogicalResourceUpdate getLogicalResourceUpdate(A4NetworkElementGroup negData, String operationalState) {
        return generateNegLogicalResourceUpdate(negData, operationalState);
    }

    // Create logicalResource representation of network element with manually set operational state
    public LogicalResourceUpdate getLogicalResourceUpdate(A4NetworkElement neData, A4NetworkElementGroup negData, String operationalState) {
        return generateNeLogicalResourceUpdate(neData, negData, operationalState);
    }

    // Create logicalResource representation of network element port with manually set operational state
    public LogicalResourceUpdate getLogicalResourceUpdate(A4NetworkElementPort nepData, A4NetworkElement neData, String operationalState, String description) {
        return generateNepLogicalResourceUpdate(nepData, neData, operationalState, description);
    }

    public static void addCharacteristic(LogicalResourceUpdate lru, String charName, String charValue) {
        ResourceCharacteristic c = new ResourceCharacteristic();
        c.setName(charName);
        c.setValue(charValue);

        if (lru.getCharacteristic() == null)
            lru.setCharacteristic(new ArrayList<>());

        lru.getCharacteristic().add(c);
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

    public LogicalResourceUpdate getLogicalResourceUpdate(NetworkServiceProfileA10NspDto nspA10Data, TerminationPointDto tpData, String operationalState) {
        return generateNspA10NspLogicalResourceUpdate(nspA10Data.getUuid(), nspA10Data.getLifecycleState(), tpData.getUuid(), operationalState);
    }

    // Create logicalResource representation of network service profile (L2BSA) with manually set operational state
    public LogicalResourceUpdate getLogicalResourceUpdate(A4NetworkServiceProfileL2Bsa nspL2Data, A4TerminationPoint tpData, String operationalState) {
        return generateNspL2BsaLogicalResourceUpdate(nspL2Data, tpData, operationalState);
    }

    public LogicalResourceUpdate getLogicalResourceUpdate(NetworkServiceProfileL2BsaDto nspL2Data, TerminationPointDto tpData, String operationalState) {
        return generateNspL2BsaLogicalResourceUpdate(nspL2Data.getUuid(), nspL2Data.getLifecycleState(), tpData.getUuid(), operationalState);
    }

    // Create logicalResource representation of network element link with manually set operational state
    public LogicalResourceUpdate getLogicalResourceUpdate(A4NetworkElementLink nelData, A4NetworkElementPort nepDataA, A4NetworkElementPort nepDataB, String operationalState) {
        return generateNelLogicalResourceUpdate(nelData, nepDataA, nepDataB, operationalState);
    }

    public static void addResourceRelationship(LogicalResourceUpdate lru, String type, String uuid) {
        ResourceRef rr = new ResourceRef();
        rr.setType(type);
        rr.setId(uuid);

        ResourceRelationship rrs = new ResourceRelationship();
        rrs.setResourceRef(rr);

        if (lru.getResourceRelationship() == null)
            lru.setResourceRelationship(new ArrayList<>());

        lru.getResourceRelationship().add(rrs);
    }

    public LogicalResourceUpdate getLogicalResourceUpdate(A4TerminationPoint tpData, String nepUuid) {

        // Is this really necessary?
        if (tpData.getUuid().isEmpty())
            tpData.setUuid(UUID.randomUUID().toString());

        LogicalResourceUpdate lru = generateGenericLogicalResourceUpdate(tpData.getUuid());
        lru.setAtType(TP);
        lru.setDescription("TP for integration test");
        lru.setVersion("1");

        addCharacteristic(lru, SUB_TYPE, tpData.getSubType());
        addCharacteristic(lru, STATE, "status");
        addCharacteristic(lru, LOCKED_NSP_USAGE, "true");
        addCharacteristic(lru, SUPPORTED_DIAG_NAMES, "(Diagnose1,Diagnose2)");
        addCharacteristic(lru, SUPPORTED_DIAG_VERSION, "(V1,V2");
        addResourceRelationship(lru, NEP, nepUuid);

        return lru;
    }

    public LogicalResourceUpdate getLogicalResourceUpdate(TerminationPointDto tpData, String nepUuid) {

        // Is this really necessary?
        if (Objects.requireNonNull(tpData.getUuid()).isEmpty())
            tpData.setUuid(UUID.randomUUID().toString());

        LogicalResourceUpdate lru = generateGenericLogicalResourceUpdate(tpData.getUuid());
        lru.setAtType(TP);
        lru.setDescription("TP for integration test");
        lru.setVersion("1");

        addCharacteristic(lru, SUB_TYPE, tpData.getType());
        addCharacteristic(lru, STATE, "status");
        addCharacteristic(lru, LOCKED_NSP_USAGE, "true");
        addCharacteristic(lru, SUPPORTED_DIAG_NAMES, "(Diagnose1,Diagnose2)");
        addCharacteristic(lru, SUPPORTED_DIAG_VERSION, "(V1,V2");
        addResourceRelationship(lru, NEP, nepUuid);

        return lru;
    }

    private LogicalResourceUpdate generateNegLogicalResourceUpdate(A4NetworkElementGroup negData, String operationalState) {
        LogicalResourceUpdate lru = generateGenericLogicalResourceUpdate(negData.getUuid());
        lru.setAtType(NEG);
        lru.setName(negData.getName());
        lru.setDescription("NEG for integration test");
        lru.setLifecycleState(negData.getLifecycleState());

        addCharacteristic(lru, CENTRAL_NET_OPERATOR, "CONO");
        addCharacteristic(lru, OP_STATE, operationalState);
        addCharacteristic(lru, TYPE_NAME, "POD");

        return lru;
    }

    private LogicalResourceUpdate generateNeLogicalResourceUpdate(A4NetworkElement neData, A4NetworkElementGroup negData, String operationalState) {
        LogicalResourceUpdate lru = generateGenericLogicalResourceUpdate(neData.getUuid());
        lru.setAtType(NE);
        lru.setDescription("NE for integration test");
        lru.setLifecycleState(neData.getLifecycleState());

        addCharacteristic(lru, OP_STATE, operationalState);
        addResourceRelationship(lru, NEG, negData.getUuid());

        return lru;
    }

    private LogicalResourceUpdate generateNepLogicalResourceUpdate(A4NetworkElementPort nepData, A4NetworkElement neData, String operationalState, String description) {
        LogicalResourceUpdate lru = generateGenericLogicalResourceUpdate(nepData.getUuid());
        lru.setAtType(NEP);
        lru.setDescription(description);

        addCharacteristic(lru, OP_STATE, operationalState);
        addResourceRelationship(lru, NE, neData.getUuid());

        return lru;
    }

    private LogicalResourceUpdate generateNspFtthLogicalResourceUpdate(A4NetworkServiceProfileFtthAccess nspFtthData, A4TerminationPoint tpData, String operationalState) {
        LogicalResourceUpdate lru = generateGenericLogicalResourceUpdate(nspFtthData.getUuid());
        lru.setAtType(NSP_FTTH_ACCESS);
        lru.setDescription("NSP-FTTH-ACCESS for integration test");
        lru.setLifecycleState(nspFtthData.getLifecycleState());

        addCharacteristic(lru, OP_STATE, operationalState);
        addResourceRelationship(lru, TP, tpData.getUuid());

        return lru;
    }

    private LogicalResourceUpdate generateNspFtthLogicalResourceUpdate(A4NetworkServiceProfileFtthAccess nspFtthData,
                                                                       A4TerminationPoint tpData,
                                                                       String operationalState,
                                                                       A4NetworkElementPort nepData) {
        LogicalResourceUpdate lru = generateGenericLogicalResourceUpdate(nspFtthData.getUuid());
        lru.setAtType(NSP_FTTH_ACCESS);
        lru.setDescription("NSP-FTTH-ACCESS with Port Ref for integration test");
        lru.setLifecycleState(nspFtthData.getLifecycleState());

        addCharacteristic(lru, OP_STATE, operationalState);
        addResourceRelationship(lru, TP, tpData.getUuid());
        addResourceRelationship(lru, NEP, nepData.getUuid());

        return lru;
    }

    private LogicalResourceUpdate generateNspA10NspLogicalResourceUpdate(A4NetworkServiceProfileA10Nsp nspA10Data, A4TerminationPoint tpData, String operationalState) {
        return generateNspA10NspLogicalResourceUpdate(nspA10Data.getUuid(), nspA10Data.getLifecycleState(), tpData.getUuid(), operationalState);
    }

    private LogicalResourceUpdate generateNspA10NspLogicalResourceUpdate(String nspA10Uuid, String nspA10LcState, String tpUuid, String operationalState) {
        LogicalResourceUpdate lru = generateGenericLogicalResourceUpdate(nspA10Uuid);
        lru.setAtType(NSP_A10NSP);
        lru.setDescription("NSP-A10NSP for integration test");
        lru.setLifecycleState(nspA10LcState);

        addCharacteristic(lru, OP_STATE, operationalState);
        addCharacteristic(lru, LACP_ACTIVE, "false");
        addResourceRelationship(lru, TP, tpUuid);

        return lru;
    }

    private LogicalResourceUpdate generateNspL2BsaLogicalResourceUpdate(A4NetworkServiceProfileL2Bsa nspL2Data, A4TerminationPoint tpData, String operationalState) {
        return generateNspL2BsaLogicalResourceUpdate(nspL2Data.getUuid(), nspL2Data.getLifecycleState(), tpData.getUuid(), operationalState);
    }

    private LogicalResourceUpdate generateNspL2BsaLogicalResourceUpdate(String nspL2Uuid, String nspL2LcState, String tpUuid, String operationalState) {
        LogicalResourceUpdate lru = generateGenericLogicalResourceUpdate(nspL2Uuid);
        lru.setAtType(NSP_L2BSA);
        lru.setDescription("NSP-L2BSA for integration test");
        lru.setLifecycleState(nspL2LcState);

        addCharacteristic(lru, OP_STATE, operationalState);
        addResourceRelationship(lru, TP, tpUuid);

        return lru;
    }

    private LogicalResourceUpdate generateNelLogicalResourceUpdate(A4NetworkElementLink nelData, A4NetworkElementPort nepDataA, A4NetworkElementPort nepDataB, String operationalState) {
        LogicalResourceUpdate lru = generateGenericLogicalResourceUpdate(nelData.getUuid());
        lru.setAtType(NEL);
        lru.setDescription("NEL for integration test");
        lru.setLifecycleState(nelData.getLifecycleState());

        addCharacteristic(lru, OP_STATE, operationalState);
        addResourceRelationship(lru, NEP, nepDataA.getUuid());
        addResourceRelationship(lru, NEP, nepDataB.getUuid());

        return lru;
    }

    private LogicalResourceUpdate generateGenericLogicalResourceUpdate(String uuid) {
        LogicalResourceUpdate lru = new LogicalResourceUpdate();
        lru.setAtBaseType(LOGICAL_RESOURCE);

        addCharacteristic(lru, CREATION_TIME, OffsetDateTime.now().toString());
        addCharacteristic(lru, UPDATE_TIME, OffsetDateTime.now().toString());
        addCharacteristic(lru, CORRELATION_ID, uuid);

        return lru;
    }

}
