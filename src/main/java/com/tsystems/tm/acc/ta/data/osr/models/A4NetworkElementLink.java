package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.Data;

@Data
public class A4NetworkElementLink {
    private String uuid;
    private String ueWegId;
    private String lbz;
    private String lifecycleState;
    private String operationalState;
}
