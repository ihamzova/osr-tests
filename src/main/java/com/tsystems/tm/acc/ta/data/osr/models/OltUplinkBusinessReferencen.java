package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OltUplinkBusinessReferencen {
    private BngSourcePortEquipmentBusinessRef bngSourcePortEquipmentBusinessRef;
    private BngTargetPortEquipmentBusinessRef bngTargetPortEquipmentBusinessRef;
    private OltPortEquipmentBusinessRef oltPortEquipmentBusinessRef;
}
