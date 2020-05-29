package com.tsystems.tm.acc.ta.data.osr.generators;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkElementGroupDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.LogicalResourceUpdate;

import java.time.OffsetDateTime;
import java.util.UUID;

public class A4NetworkElementGroupGenerator {

    public NetworkElementGroupDto generateAsDto(A4NetworkElementGroup negData) {
        if(negData.getUuid().isEmpty())
            negData.setUuid(UUID.randomUUID().toString());

        if(negData.getName().equals(""))
            negData.setName("NEG-" + UUID.randomUUID().toString().substring(0, 6)); // satisfy unique constraints

        return new NetworkElementGroupDto()
                .uuid(negData.getUuid())
                .type("POD")
                .specificationVersion("1")
                .operationalState(negData.getOperationalState())
                .name(negData.getName())
                .lifeCycleState(negData.getLifecycleState())
                .lastUpdateTime(OffsetDateTime.now())
                .description("NEG created during osr-test integration test")
                .creationTime(OffsetDateTime.now())
                .centralOfficeNetworkOperator("neg_centOffNetOp_for_integration_test");
    }
}
