package com.tsystems.tm.acc.ta.data.osr.generators;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkElementDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.LogicalResourceUpdate;

import java.time.OffsetDateTime;
import java.util.UUID;

public class A4NetworkElementGenerator {

    public NetworkElementDto generateAsDto(A4NetworkElement neData, A4NetworkElementGroup negData) {
        neData.setUuid(UUID.randomUUID().toString());

        if(neData.getFsz().equals(""))
            neData.setFsz(UUID.randomUUID().toString().substring(0, 4)); // satisfy unique constraints

        return new NetworkElementDto()
                .uuid(neData.getUuid())
                .networkElementGroupUuid(negData.getUuid())
                .description("NE for integration test")
                .address("address")
                .administrativeState("ACTIVATED")
                .lifecycleState(neData.getOperationalState())
                .operationalState(neData.getLifecycleState())
                .category(neData.getCategory())
                .fsz(neData.getFsz())
                .vpsz(neData.getVpsz())
                .klsId(neData.getKlsId())
                .plannedRackId("rackid")
                .plannedRackPosition("rackpos")
                .planningDeviceName("planname")
                .roles("role")
                .type("SPINE_SWITCH")
                .creationTime(OffsetDateTime.now())
                .lastUpdateTime(OffsetDateTime.now());
    }

    public LogicalResourceUpdate generateAsLogicalResource(A4NetworkElement neData, A4NetworkElementGroup negData) {
        return new LogicalResourceUpdate();
    }
}
