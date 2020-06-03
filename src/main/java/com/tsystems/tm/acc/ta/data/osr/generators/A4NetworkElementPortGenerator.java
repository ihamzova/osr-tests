package com.tsystems.tm.acc.ta.data.osr.generators;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkElementPortDto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class A4NetworkElementPortGenerator {

    public NetworkElementPortDto generateAsDto(A4NetworkElementPort nepData, A4NetworkElement neData) {
        if (nepData.getUuid().isEmpty())
            nepData.setUuid(UUID.randomUUID().toString());

        if (nepData.getPort().equals(""))
            nepData.setPort(UUID.randomUUID().toString().substring(0, 4)); // satisfy unique constraints

        return new NetworkElementPortDto()
                .uuid(nepData.getUuid())
                .description("NEP for integration test")
                .networkElementUuid(neData.getUuid())
                .logicalLabel("LogicalLabel_" + nepData.getPort())
                .accessNetworkOperator("NetOp")
                .administrativeState("ACTIVATED")
                .operationalState(nepData.getOperationalState())
                .role("role")
                .creationTime(OffsetDateTime.now())
                .lastUpdateTime(OffsetDateTime.now());
    }
}
