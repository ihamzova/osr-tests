package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.Data;

@Data
public class A4NetworkServiceProfileA10Nsp {

    private String uuid;
    private String operationalState;
    private String lifecycleState;
    private String numberOfAssociatedNsps;
    private String routingInstanceId;
}
