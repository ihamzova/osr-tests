package com.tsystems.tm.acc.ta.data.morpheus.mappers;

import com.tsystems.tm.acc.tests.osr.access.line.management.external.client.model.OnuIdDto;

public class AccessLineManagementMapper {
    public OnuIdDto getOnuIdDto() {
        return new OnuIdDto()
                .onuId(12345678);
    }
}
