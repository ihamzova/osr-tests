package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class A4ImportCsvLine {

    private String negName;
    private String negDescription;
    private String neVpsz;
    private String neFsz;
    private String neDescription;

}
