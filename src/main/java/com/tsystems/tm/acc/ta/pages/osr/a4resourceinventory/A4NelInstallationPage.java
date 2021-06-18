package com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory;

import com.tsystems.tm.acc.domain.osr.csv.A4ResourceInventoryEntry;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvLine;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import io.qameta.allure.Step;
import lombok.Getter;
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
public class A4NelInstallationPage {

    public static final String ENDPOINT = "/";

    public static final By A4_NEL_INSTALLATION_HEADER_LOCATOR = byXpath("//h2[contains(text(),'Access 4.0 Inbetriebnahme')]");
    public static final By NEL_TABLE_LOCATOR = byId("nelTable");

    @Getter
    public static final By CHECKBOX_LOCATOR = byId("checkboxAuswahl");

    @Getter
    public static final By START_INSTALL_BTN = byId("StartInstallBtn");

    @Getter
    public static final By ERROR_LOCATOR = byXpath("/html/body/app-root/div/app-portal/div/app-inbetriebnahme-nel/div/div[3]/div/p");

    @Step("Validate page")
    public void validate() {
        $(A4_NEL_INSTALLATION_HEADER_LOCATOR).waitUntil(visible, 3000);
        assertContains(url(), ENDPOINT);
    }

}
