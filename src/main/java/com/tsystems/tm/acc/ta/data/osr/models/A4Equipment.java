package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class A4Equipment {

    private String uuid;
    private String manufacturer;
    private String installedPartNumber;
    private String manufactureDate;
    private String installedSerialNumber;
    private String equipmentRevisionLevel;
    private String matNumberStringRetrieved;
    private String matNumber;
    private String installedEquipmentType;
    private String model;
    private String creationTime;
    private String lastUpdateTime;
    private String description;
    private String lastBootTime;
    private String networkElementUuid;
    private String holderUuid;
}
