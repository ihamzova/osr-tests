package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class A4NetworkElementLink {

    private String uuid;
    private String ueWegId;
    private String lbz;
    private String lifecycleState;
    private String operationalState;

}
