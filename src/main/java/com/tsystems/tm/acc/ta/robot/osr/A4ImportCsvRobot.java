package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.domain.osr.csv.A4ResourceInventoryEntry;
import com.tsystems.tm.acc.ta.csv.CsvStream;
import com.tsystems.tm.acc.ta.data.osr.generators.A4ImportCsvDataGenerator;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ImportPage;
import io.qameta.allure.Step;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class A4ImportCsvRobot {

    @Step("Create CSV file")
    public void generateCsvFile(A4ImportCsvData csvData, File targetFile) {
        A4ImportCsvDataGenerator a4ImportCsvDataGenerator = new A4ImportCsvDataGenerator();
        List<A4ResourceInventoryEntry> data = a4ImportCsvDataGenerator.generateCsv(csvData);

        try {
            new CsvStream(targetFile).withDelimeter(';')
                    .write(A4ResourceInventoryEntry.class, data);
        } catch (IOException e) {
            throw new RuntimeException("cant build csv", e);
        }
    }
    @Step("open ui, log in, and goTo import-page")
    public void openImportPage() {
        A4ImportPage
                .login();
    }

}
