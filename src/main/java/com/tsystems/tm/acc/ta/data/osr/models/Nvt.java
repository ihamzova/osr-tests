package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Nvt {
    private String id;
    private String name;
    private String type;
    private Integer splitterType;
    private String gebietstyp;
    private Address address;
    private OltDevice oltDevice;
    private String oltSlot;
    private String oltPort;
}
