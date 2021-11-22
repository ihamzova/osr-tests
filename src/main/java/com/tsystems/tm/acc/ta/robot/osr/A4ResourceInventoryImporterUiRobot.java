package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ImportPage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Paths;
import java.util.UUID;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ImportPage.*;

@Slf4j
public class A4ResourceInventoryImporterUiRobot {

    private final A4ImportCsvRobot a4ImportCsvRobot = new A4ImportCsvRobot();

    @Step("uploadCsvFile")
    public void uploadCsvFile(File csvFile) {
        $(A4_INVENTORY_IMPORTER_DATEI_AUSWAEHLEN_BUTTON_LOCATOR).uploadFile(csvFile);
    }

    @Step("click Senden button")
    public void clickSendenButton() {
        $(A4_INVENTORY_IMPORTER_SENDEN_BUTTON_LOCATOR).click();
    }

    @Step("Open UI, log in, and search for existing Network Element")
    public void openA4ImportPage() {
        A4ImportPage
                .login()
                .validate();
    }

    @Step("Open UI, log in, and upload CSV file, then submit")
    public void importCsvFileViaUi(A4ImportCsvData csvData) {
        File csvFile = Paths.get("target/", "a4Testcase" + UUID.randomUUID().toString().substring(1, 6)
                + ".csv").toFile();
        a4ImportCsvRobot.generateCsvFile(csvData, csvFile);
        uploadCsvFile(csvFile);
        clickSendenButton();

        $(A4_INVENTORY_IMPORTER_UPLOAD_MESSAGE_LOCATOR).waitUntil(visible, 25000);
        $(A4_INVENTORY_IMPORTER_UPLOAD_MESSAGE_LOCATOR).waitUntil(matchText("csvLine"), 25000);
    }

}
