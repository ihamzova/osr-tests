package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.Data;

import java.util.List;

@Data
public class A4ImportCsvData {

    private List<A4ImportCsvLine> csvLines;

}
