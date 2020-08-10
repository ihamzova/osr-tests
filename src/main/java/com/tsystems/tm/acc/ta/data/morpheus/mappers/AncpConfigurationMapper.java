package com.tsystems.tm.acc.ta.data.morpheus.mappers;

import com.tsystems.tm.acc.tests.osr.ancp.configuration.external.v2_1_0.client.model.ANCPResponse;

public class AncpConfigurationMapper {
    public ANCPResponse getANCPResponse() {
        return new ANCPResponse()
                .correlationId("string")
                .modifiedIndex(0)
                .status(ANCPResponse.StatusEnum.RMK_ENDPOINT_START)
                .message("string");
    }
}
