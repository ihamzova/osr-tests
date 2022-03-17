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

import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ImportPage.*;

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
    @Step("insert neg name")
    public void insertNegName(String value) {
        $(A4_INVENTORY_IMPORTER_PLURAL_FIELD_LOCATOR).val(value);
    }
    @Step("press enter button")
    public void pressEnterButton() {
        $(A4_INVENTORY_IMPORTER_SENDEN_BUTTON_LOCATOR);
    }
    @Step("read message")
    public String readMessage() {
       return $(A4_INVENTORY_IMPORTER_UPLOAD_MESSAGE_LOCATOR).getText();
    }

    @Step("open ui, log in, and goTo import-page")
    public void openImportPage() {
        A4ImportPage
                .login();
    }

}
