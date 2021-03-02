package com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory;

import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import io.qameta.allure.Step;
import lombok.Getter;
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

@Slf4j
@Getter
public class A4ResourceInventoryBrowserPage {

    public static final String ENDPOINT = A4_RESOURCE_INVENTORY_UI_MS + "/a4-inventory-browser";
    public static final By A4_SEARCH_PAGE_HEADER = byXpath("/html/body/app-root/div/app-portal/app-page-header-v3");

    @Getter
    public static final By InventorySearchButton_Locator = By.id("btnInventorySearch");

    @Getter
    public static final By InventoryImportButton_Locator = By.id("btnInventoryImport");

    @Getter
    public static final By MobilUiButton_Locator = By.id("btnMobileUi");



    @Step("Validate page")
    public A4ResourceInventoryBrowserPage validate() {
        $(A4_SEARCH_PAGE_HEADER).waitUntil(visible, 3000);
        assertContains(url(), ENDPOINT);
        return this;
    }


    @Step("Login")
    public static A4ResourceInventoryBrowserPage login() {
        URL url = new OCUrlBuilder(A4_RESOURCE_INVENTORY_UI_MS).withEndpoint(ENDPOINT).build();
        return open(url, A4ResourceInventoryBrowserPage.class);
    }


}
