package com.tsystems.tm.acc.ta.data.morpheus.mappers;

import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.external.v1_1_1_0.client.model.DeprovisioningResponseHolder;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.external.v1_1_1_0.client.model.ProcessDto;

import java.util.UUID;

public class WgAccessProvisioningMapper {
    public ProcessDto getProcessDto() {
        return new ProcessDto()
                .source("12345")
                .id(UUID.randomUUID())
                .businessKey("{{request.requestLine.query.businessKey}}")
                .index(0);
    }

    public DeprovisioningResponseHolder getDeprovisioningResponseHolder(boolean success) {
        if (success) {
            return new DeprovisioningResponseHolder()
                    .success(true)
                    .response(new Object());
        } else {
            return new DeprovisioningResponseHolder()
                    .success(false)
                    .response(new Object());
        }
    }
}
