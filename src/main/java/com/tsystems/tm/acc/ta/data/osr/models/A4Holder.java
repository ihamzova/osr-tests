package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class A4Holder {

    private String uuid;
    private String label;
    private String type;
    private String creationTime;
    private String lastUpdateTime;
    private String description;
}
