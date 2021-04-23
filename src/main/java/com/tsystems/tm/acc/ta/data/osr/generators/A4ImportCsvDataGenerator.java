package com.tsystems.tm.acc.ta.data.osr.generators;

import com.tsystems.tm.acc.domain.osr.csv.A4ResourceInventoryEntry;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvLine;

import java.util.ArrayList;
import java.util.List;

public class A4ImportCsvDataGenerator {

    public List<A4ResourceInventoryEntry> generateCsv(A4ImportCsvData csvData) {

        List<A4ResourceInventoryEntry> csvList = new ArrayList<>();

        for (int i = 0; i < csvData.getCsvLines().size(); i++) {
            A4ImportCsvLine csvLine = csvData.getCsvLines().get(i);

            A4ResourceInventoryEntry csvEntry = new A4ResourceInventoryEntry()
                    .negName(csvLine.getNegName())
                    .negDescription("NEG created by csv import test")
                    .neVpsz(csvLine.getNeVpsz())
                    .neFsz(csvLine.getNeFsz())
                    .neDescription(csvLine.getNeDescription());

            csvList.add(csvEntry);
        }

        return csvList;
    }

}
