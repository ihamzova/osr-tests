package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DefaultNetworkLineProfile {
    private String accessType;
    private Integer minDownBandwidth;
    private Integer minUpBandwidth;
    private Integer guaranteedDownBandwidth;
    private Integer guaranteedUpBandwidth;
    private Integer maxDownBandwidth;
    private Integer maxUpBandwidth;
    private String state;
}
