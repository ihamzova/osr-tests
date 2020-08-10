package com.tsystems.tm.acc.ta.data.osr.generators;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkServiceProfileFtthAccess;
import com.tsystems.tm.acc.ta.data.osr.models.A4TerminationPoint;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkServiceProfileFtthAccessDto;
import java.time.OffsetDateTime;
import java.util.UUID;

public class A4NetworkServiceProfileFtthAccessGenerator {



    public NetworkServiceProfileFtthAccessDto generateAsDto(A4NetworkServiceProfileFtthAccess nspData, A4TerminationPoint tpData) {
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
}
