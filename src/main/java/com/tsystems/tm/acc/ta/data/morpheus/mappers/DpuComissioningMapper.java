package com.tsystems.tm.acc.ta.data.morpheus.mappers;

import com.tsystems.tm.acc.tests.osr.ancp.configuration.external.v2_1_0.client.model.CallbackResponseDto;
import com.tsystems.tm.acc.tests.osr.ancp.configuration.external.v2_1_0.client.model.ConfigurationUplinkDTOResult;
import com.tsystems.tm.acc.tests.osr.ancp.configuration.external.v2_1_0.client.model.ErrorResponse;

public class DpuComissioningMapper {
    public ConfigurationUplinkDTOResult getConfigurationUplinkDTOResult(boolean success) {
        if (success) {
            return new ConfigurationUplinkDTOResult()
                    .error(new ErrorResponse())
                    .success(true)
                    .response(new CallbackResponseDto());
        } else {
            return new ConfigurationUplinkDTOResult()
                    .error(new ErrorResponse()
                            .errorCode("1111")
                            .message("ErrorMessage")
                    )
                    .success(false)
                    .response(new CallbackResponseDto());
        }
    }
}
