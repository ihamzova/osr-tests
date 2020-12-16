package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvLine;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ImportPage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.InstallationPage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@Slf4j
public class A4ResourceInventoryImporterUiRobot {

    private final A4ImportCsvRobot a4ImportCsvRobot = new A4ImportCsvRobot();

    private final A4ImportPage a4ImportPage = new A4ImportPage();

    @Step("uploadCsvFile")
    public void uploadCsvFile(File csvFile) {
        $(a4ImportPage.getA4_INVENTORY_IMPORTER_DATEI_AUSWAEHLEN_BUTTON_LOCATOR()).uploadFile(csvFile);
    }

    @Step("click Senden button")
    public void clickSendenButton(){
        $(a4ImportPage.getA4_INVENTORY_IMPORTER_SENDEN_BUTTON_LOCATOR()).click();
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

        $(a4ImportPage.getA4_INVENTORY_IMPORTER_UPLOAD_MESSAGE_LOCATOR()).waitUntil(visible, 10000);
    }
}
