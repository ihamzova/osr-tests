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
public class Area {
    private String areaName;
    private String grobStartDate;
    private String grobEndDate;
    private String gebietsName;
    private String gebietsId;
    private String vvmStartDate;
    private String vvmEndDate;
    private Workorder gftaWorkorder;
    private Workorder oneboxWorkorder;
    private Workorder auskundungWorkorder;
    private List<Supplier> suppliers;
    private List<Property> properties;
    private String status;
    private String ausbauSponsor;
    private String ausbauDatum;
}
