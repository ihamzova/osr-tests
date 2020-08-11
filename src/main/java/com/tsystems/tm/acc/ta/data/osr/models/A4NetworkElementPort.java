package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.Data;

@Data
public class A4NetworkElementPort {
    private String uuid;
    private String operationalState;
    private String functionalPortLabel;
    private String networkElementEndsz;
    private String networkElementUuid;
    private String type;
    private String portNumber;
}
