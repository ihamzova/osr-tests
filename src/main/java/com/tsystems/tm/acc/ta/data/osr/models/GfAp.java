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
public class GfAp {
    private String id;
    private String name;
    private String materialNummer;
    private List<GfApSplitter> splitters;
    private Property property;
    private List<GfTa> gftas;
}
