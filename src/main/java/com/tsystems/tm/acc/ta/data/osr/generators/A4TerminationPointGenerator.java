package com.tsystems.tm.acc.ta.data.osr.generators;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.ta.data.osr.models.A4TerminationPoint;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.TerminationPointDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.LogicalResourceUpdate;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceCharacteristic;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceRef;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceRelationship;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class A4TerminationPointGenerator {

    public TerminationPointDto generateAsDto(A4TerminationPoint tpData, A4NetworkElementPort nepData) {
        return new TerminationPointDto();
    }

    public LogicalResourceUpdate generateAsLogicalResource(A4TerminationPoint tpData, A4NetworkElementPort nepData) {
        if(tpData.getUuid().isEmpty())
            tpData.setUuid(UUID.randomUUID().toString());

        List<ResourceCharacteristic> tpCharacteristics = new ArrayList<>();
        ResourceCharacteristic rc1 = new ResourceCharacteristic()
                .name("creationTime")
                .value(OffsetDateTime.now().toString());
        tpCharacteristics.add(rc1);
        ResourceCharacteristic rc2 = new ResourceCharacteristic()
                .name("lastUpdateTime")
                .value(OffsetDateTime.now().toString());
        tpCharacteristics.add(rc2);
        ResourceCharacteristic rc3 = new ResourceCharacteristic()
                .name("subType")
                .value("type");
        tpCharacteristics.add(rc3);
        ResourceCharacteristic rc4 = new ResourceCharacteristic()
                .name("state")
                .value("state");
        tpCharacteristics.add(rc4);
        ResourceCharacteristic rc5 = new ResourceCharacteristic()
                .name("lockedForNspUsage")
                .value("true");
        tpCharacteristics.add(rc5);
        ResourceCharacteristic rc6 = new ResourceCharacteristic()
                .name("supportedDiagnosesName")
                .value("(Diagnose1,Diagnose2)");
        tpCharacteristics.add(rc6);
        ResourceCharacteristic rc7 = new ResourceCharacteristic()
                .name("supportedDiagnosesSpecificationVersion")
                .value("(V1,V2)");
        tpCharacteristics.add(rc7);

        ResourceRef resourceRef = new ResourceRef()
                .id(nepData.getUuid())
                .type("NetworkElementPort");

        ResourceRelationship resourceRelationship = new ResourceRelationship();
        resourceRelationship.setResourceRef(resourceRef);

        List<ResourceRelationship> tpResourceRelationships = new ArrayList<>();
        tpResourceRelationships.add(resourceRelationship);

        LogicalResourceUpdate terminationPointLogicalResource = new LogicalResourceUpdate()
                .baseType("LogicalResource")
                .type("TerminationPoint")
                .version("1")
                .description("TP for integration test");
        terminationPointLogicalResource.setCharacteristic(tpCharacteristics);
        terminationPointLogicalResource.setResourceRelationship(tpResourceRelationships);

        return terminationPointLogicalResource;
    }
}
