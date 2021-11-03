package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class A4Connector {

    private String uuid;
    private String creationTime;
    private String lastUpdateTime;
    private String description;
    private String specificationVersion;
    private String formfactor;
    private String media;
    private String speed;
    private String protocol;
    private String physicalLabel;

}
