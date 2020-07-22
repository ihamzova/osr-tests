package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.Data;

@Data
public class Dpu {
    private String endSz;
    private Integer onuId;
    private String stepToFall;
    private String changeBody;
    private String lifeCycleDpu;
    private String lifeCycleUplink;
}
