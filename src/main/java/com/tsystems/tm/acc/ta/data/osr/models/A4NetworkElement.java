package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class A4NetworkElement {

    private String uuid;
    private String vpsz;
    private String fsz;
    private String klsId;
    private String category;
    private String operationalState;
    private String lifecycleState;
    private String administrativeState;
    private String description;
    private String address;
    private String specificationVersion;
    private String type;
    private String roles;
    private String plannedRackId;
    private String plannedRackPosition;
    private String plannedMatNr;
    private String planningDeviceName;
    private String fiberOnLocationId;
    private String ztpIdent;
    private String creationTime;
    private String lastUpdateTime;
    private String lastSuccessfulSyncTime;

}
