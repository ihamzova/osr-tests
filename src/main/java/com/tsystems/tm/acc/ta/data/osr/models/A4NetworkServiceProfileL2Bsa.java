package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.Data;

@Data
public class A4NetworkServiceProfileL2Bsa {

    private String uuid;
    private String operationalState;
    private String lifecycleState;
    private String administrativeMode;
    private String lineId;
    private String dataRateDown;
    private String dataRateUp;
    //private String faultyOperationalState; // Setup for a field to test PATCH operation with garbage content into Operational_State of a L2Bsa NSP

}
