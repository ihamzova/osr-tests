package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Property {
    private String residentialUnit;
    private String businessUnit;
    private String floor;
    private Address address;
    private String status;
    private String specialUnit;
    private String technology;
    private String noConstructionReason;
    private String demandCarrier;
    private String areaType;
    private String initiative;
    private String rolloutSponsor;
    private Integer plannedLlcCount;
    private String distributionPointId;
    private String distributionPointName;
    private Double koordX;
    private Double koordY;
}
