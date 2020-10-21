package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.ta.data.osr.models.A4TerminationPoint;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.LogicalResourceUpdate;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceCharacteristic;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceRef;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceRelationship;

import java.time.OffsetDateTime;
import java.util.UUID;

public class A4ResourceInventoryServiceMapper {
    public LogicalResourceUpdate getLogicalResourceUpdate(A4TerminationPoint tpData, A4NetworkElementPort nepData) {
        if (tpData.getUuid().isEmpty())
            tpData.setUuid(UUID.randomUUID().toString());

        return new LogicalResourceUpdate()
                .baseType("LogicalResource")
                .type("TerminationPoint")
                .version("1")
                .description("TP for integration test")
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("creationTime")
                        .value(OffsetDateTime.now().toString()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("lastUpdateTime")
                        .value(OffsetDateTime.now().toString()))
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
}
