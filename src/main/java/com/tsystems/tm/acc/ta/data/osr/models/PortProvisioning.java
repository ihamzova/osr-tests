package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PortProvisioning {
    private String endSz;
    private String slotNumber;
    private String portNumber;
    private Integer homeIdPool;
    private Integer lineIdPool;
    private Integer accessLinesWG;
    private Integer defaultNEProfilesActive;
    private Integer defaultNetworkLineProfilesActive;
    private Integer accessLinesCount;
}