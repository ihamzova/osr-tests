package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class A4NetworkServiceProfileFtthAccess {

    private String uuid;
    private String ontSerialNumber;
    private String lineId;
    private String operationalState;
    private String lifecycleState;

}
