package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
    private String supplierName;
    private String supplierId;
    private Boolean ne3 = true;
    private Boolean ne4 = true;
}
