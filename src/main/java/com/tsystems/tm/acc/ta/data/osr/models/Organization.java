package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Organization {
    private String id;
    private Boolean isLegalEntity;
    private Boolean isWholeBuy;
    private List<LocationResponsibility> locationResponsibility;
    private String nameType;
    private String status;
    private String tradingName;
    private String type;
    private String uiType;
    private String relationshipType;
    private String carrierCode;
    private List<Characteristic> characteristics;
}
