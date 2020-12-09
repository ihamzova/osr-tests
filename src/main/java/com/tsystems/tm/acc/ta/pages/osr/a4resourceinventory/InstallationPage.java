package com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory;

import com.codeborne.selenide.Condition;
import com.tsystems.tm.acc.domain.osr.csv.A4ResourceInventoryEntry;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvLine;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.io.File;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.WebDriverRunner.url;
import static com.tsystems.tm.acc.ta.util.Assert.assertContains;

@Slf4j
public class InstallationPage {

    public static final String ENDPOINT = "/";
    public static final String NETWORK_ELEMENT_FOUND = "plannedRack";
    public static final String ZTP_SAVED_SUCCESSFULLY = "Speichern erfolgreich";

    public static final By A4_INVENTORY_IMPORTER_HEADER_LOCATOR = byXpath("//h2[contains(text(),'Hinzufügen eines Installationsauftrages')]");
    public static final By CSV_FILE_INPUT_FIELD_LOCATOR = byXpath("//input[contains(@name,'profile')]");
    public static final By CSV_FILE_UPLOAD_SUBMIT_BUTTON_LOCATOR = byId("btnUploadCSV");
    public static final By INPUT_VPSZ_LOCATOR = byId("inputVpsz");
    public static final By INPUT_FSZ_LOCATOR = byId("inputFsz");
    public static final By SEARCH_BUTTON = byId("btnSearch");
    public static final By SAVE_BUTTON = byId("btnSave");
    public static final By MONITORING_BUTTON = byId("btnMonitoring");
    public static final By TEXT_NETWORK_ELEMENT = byId("textNetworkElement");
    public static final By RESET_SEARCH_BUTTON = byId("btnCancel");
    public static final By INPUT_ZPTIDENT_LOCATOR = byId("inputZptIdent");

    @Step("Validate page")
    public void validate() {
        $(A4_INVENTORY_IMPORTER_HEADER_LOCATOR).waitUntil(visible, 3000);
        $(CSV_FILE_INPUT_FIELD_LOCATOR).waitUntil(visible, 3000);
        assertContains(url(), ENDPOINT);
    }

    @Step("upload csv via ui")
    public void uploadCSV(File csvFile) {
        $(CSV_FILE_INPUT_FIELD_LOCATOR).uploadFile(csvFile);
        $(CSV_FILE_UPLOAD_SUBMIT_BUTTON_LOCATOR).click();
        //@TODO: The UI needs some nice error/success messages we can check
    }

    @Step("Search for NE")
    public void checkNetworkElementExists(A4NetworkElement neData) {
        checkNetworkElementExists(neData.getVpsz(), neData.getFsz());
    }

//    @Step("Search for NE")
    private void checkNetworkElementExists(String vpsz, String fsz) {
        $(INPUT_VPSZ_LOCATOR).val(vpsz);
        $(INPUT_FSZ_LOCATOR).val(fsz);
        $(SEARCH_BUTTON).waitUntil(enabled, 5000).click();
        $(INPUT_ZPTIDENT_LOCATOR).waitUntil(not(disabled), 5000);
        assertContains($(TEXT_NETWORK_ELEMENT).getText(), NETWORK_ELEMENT_FOUND); // only shows up when NE is found
    }

    @Step("Enter ZTP Ident value")
    public void enterZtpIdent(String value) {
        $(INPUT_ZPTIDENT_LOCATOR).val(value);
        $(SAVE_BUTTON).waitUntil(enabled, 5000).click();
        sleep(5000);
        assertContains($(TEXT_NETWORK_ELEMENT).getText(), ZTP_SAVED_SUCCESSFULLY);
        assertContains($(TEXT_NETWORK_ELEMENT).getText(), value);
    }

    @Step("Go to monitoring page")
    public MonitoringPage openMonitoringPage() {
        $(MONITORING_BUTTON).waitUntil(enabled, 5000).click();
        return new MonitoringPage();
    }

//    @Step("check NE via ui")
//    public void checkNetworkElementExists(A4ResourceInventoryEntry expectedEntry) {
//        $(INPUT_VPSZ_LOCATOR).val(expectedEntry.neVpsz());
//        $(INPUT_FSZ_LOCATOR).val(expectedEntry.neFsz());
//        $(SEARCH_BUTTON).waitUntil(enabled, 5000).click();
//        $(INPUT_ZPTIDENT_LOCATOR).waitUntil(not(disabled), 5000);
//        assertContains($(TEXT_NETWORK_ELEMENT).getText(), expectedEntry.neDescription());
//    }

    @Step("check NE via ui")
    public void checkNetworkElementExists(A4ResourceInventoryEntry expectedEntry) {
        checkNetworkElementExists(expectedEntry.neVpsz(), expectedEntry.neFsz());
        assertContains($(TEXT_NETWORK_ELEMENT).getText(), expectedEntry.neDescription());
    }

    @Step("check NE via ui")
    public void checkNetworkElementExists(A4ImportCsvLine expectedEntry) {
        checkNetworkElementExists(expectedEntry.getNeVpsz(), expectedEntry.getNeFsz());
        assertContains($(TEXT_NETWORK_ELEMENT).getText(), expectedEntry.getNeDescription());
    }

    @Step("reset search for next element")
    public void resetSearch() {
        $(RESET_SEARCH_BUTTON).click();
        $(INPUT_FSZ_LOCATOR).waitUntil(empty, 5000);
    }

}
