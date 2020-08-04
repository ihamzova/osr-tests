package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AccessLine {
    private OltDevice oltDevice;
    private String endSz;
    private String slotNumber;
    private String portNumber;
    private String homeId;
    private String lineId;
}
