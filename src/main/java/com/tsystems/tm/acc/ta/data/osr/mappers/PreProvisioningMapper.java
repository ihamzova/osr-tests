package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkServiceProfileFtthAccessDto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class PreProvisioningMapper {

    public NetworkServiceProfileFtthAccessDto getNetworkServiceProfile() {

        return new NetworkServiceProfileFtthAccessDto()
                .uuid(UUID.randomUUID().toString())
                .lifecycleState("PLANNING")
                .operationalState("NOT_WORKING")
                .description("Berlinium-Test PreProvisioning FtthAccess")
                .administrativeMode("adminMode")
//                .productionScheme("prodScheme")
                .virtualServiceProvider("virtServiceProv")
                .specificationVersion("v1")
                .lineId(UUID.randomUUID().toString())
                .ontSerialNumber(UUID.randomUUID().toString())
                .lastUpdateTime(OffsetDateTime.now())
                .creationTime(OffsetDateTime.now())
                .terminationPointFtthAccessUuid("{{jsonPath request.body '$.tpRef'}}");
    }

}
