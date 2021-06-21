package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class A10nspCheckData {
    private String oltEndSz;
    private String oltEndSz2;
    private String oltEndSz3;
    private String bngEndSz;
    private String lineId;
    private String rahmenVertragsNr;
}
