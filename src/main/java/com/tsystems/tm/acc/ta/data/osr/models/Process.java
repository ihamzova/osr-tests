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
    private String processName;
    private String endSz;
    private String slotNumber;
    private String portNumber;
    private String lineId;
    private String startTime;
    private String endTime;
    private String duration;
    private String traceId;
    private String state;
    private String processId;
}
