package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.Data;

@Data
public class A4NetworkElement {

    private String uuid;
    private String vpsz;
    private String fsz;
    private String klsId;
    private String category;
    private String operationalState;
    private String lifecycleState;
    private String type;
    private String plannedMatNr;
    private String planningDeviceName;

}
