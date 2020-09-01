package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Process {
    private String processId;
    private String processInfo;
    private String startTime;
    private String endTime;
    private String state;
    private String endSz;
    private String slotNumber;
    private String portNumber;
}
