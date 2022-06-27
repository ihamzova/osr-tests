package com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.net.URL;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.url;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_UI_MS;
import static com.tsystems.tm.acc.ta.util.Assert.assertContains;
import static com.tsystems.tm.acc.ta.util.AsyncAssert.assertUrlContainsWithTimeout;

@Slf4j
public class A4ImportPage {

    public static final String ENDPOINT = A4_RESOURCE_INVENTORY_UI_MS + "/a4-inventory-browser/inventory-import";
    public static final By A4_UI_HEADER_LOCATOR = byXpath("/html/body/app-root/div/app-portal/app-page-header-v3");
    public static final By A4_INVENTORY_IMPORTER_DATEI_AUSWAEHLEN_BUTTON_LOCATOR = byXpath("//input[@type=\"file\"]");
    public static final By A4_INVENTORY_IMPORTER_SENDEN_BUTTON_LOCATOR = By.id("btnUploadCSV");
    public static final By A4_INVENTORY_IMPORTER_UPLOAD_MESSAGE_LOCATOR = By.id("UploadMessage");
    public static final By A4_INVENTORY_IMPORTER_PLURAL_FIELD_LOCATOR = By.id("inputNEG");

    @Step("Validate page")
    public A4ImportPage validate() {
        $(A4_UI_HEADER_LOCATOR).shouldBe(visible);
        assertUrlContainsWithTimeout(ENDPOINT);
        return this;
    }

    @Step("Login")
    public static A4ImportPage login() {
        URL url = new GigabitUrlBuilder(A4_RESOURCE_INVENTORY_UI_MS).withEndpoint(ENDPOINT).build();
        return open(url, A4ImportPage.class);
    }
}
