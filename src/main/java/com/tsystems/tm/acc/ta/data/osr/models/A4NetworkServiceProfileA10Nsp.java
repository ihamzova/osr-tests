package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class A4NetworkServiceProfileA10Nsp {

    private String uuid;
    private String operationalState;
    private String lifecycleState;
    private String numberOfAssociatedNsps;
    private String lastSuccessfulSyncTime;
}
