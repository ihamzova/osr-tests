package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FttbNeProfile {
    private String gfastInterfaceProfile;
    private String dpuLineSpectrumProfile;
    private Integer numberOfGemPorts;
    private String bandwidthProfile;
    private String stateOlt;
    private String stateMosaic;
}
