package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DpuDemand {
    private String dpuAccessTechnology;
    private String dpuEndSz;
    private String dpuInstallationInstruction;
    private String dpuLocation;
    private String fiberOnLocationId;
    private String klsId;
    private String numberOfNeededDpuPorts;
    private String state;
    private String workorderId;
}
