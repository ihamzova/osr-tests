package com.tsystems.tm.acc.ta.data.osr.generators;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementLink;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkElementLinkDto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class A4NetworkElementLinkGenerator {

    public NetworkElementLinkDto generateAsDto(A4NetworkElementLink nelData, A4NetworkElementPort nepDataA, A4NetworkElementPort nepDataB){
        if (nelData.getUuid().isEmpty())
            nelData.setUuid(UUID.randomUUID().toString());
        if(nelData.getLbz().isEmpty())
            nelData.setLbz("NEL-lbz-" + UUID.randomUUID().toString().substring(0, 6)); // satisfy unique constraints
        if(nelData.getUeWegId().isEmpty())
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

}
