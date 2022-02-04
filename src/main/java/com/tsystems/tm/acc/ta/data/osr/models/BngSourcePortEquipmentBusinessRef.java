package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BngSourcePortEquipmentBusinessRef {
    private String deviceType;
    private String endSz;
    private String portName;
    private String portType;
    private String slotName;
}
