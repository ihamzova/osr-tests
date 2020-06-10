package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.Data;

@Data
public class A4NetworkElementPort {
    private String uuid;
    private String port;
    private String operationalState;
    private String logicalLabel;
}
