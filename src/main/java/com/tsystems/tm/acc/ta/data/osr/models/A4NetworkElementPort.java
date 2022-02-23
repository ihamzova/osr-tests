package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class A4NetworkElementPort {

    private String uuid;
    private String operationalState;
    private String functionalPortLabel;
    private String type;
    private String description;
    private String lastSuccessfulSyncTime;

}
