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
public class A4InventarSuchePage {

    public static final String ENDPOINT = A4_RESOURCE_INVENTORY_UI_MS + "/a4-inventory-browser/inventory-search";

    public static final By A4_SEARCH_PAGE_HEADER = byXpath("/html/body/app-root/div/app-portal/app-page-header-v2/div[1]");

    @Getter
    public static final By NEG_CHOOSE_BUTTON_LOCATOR = By.id("searchNetworkElementGroup");

    @Getter
    public static final By NEG_NAME_INPUT_FIELD_LOCATOR = By.id("negName");

    @Getter
    public static final By SEARCH_BUTTON_LOCATOR = By.id("negSearchSubmit");

    @Getter
    public static final By SEARCH_RESULT_TABLE_LOCATOR = By.xpath("/html/body/app-root/div/app-portal/div/app-inventory-search/div[2]/app-search-results-networkelementgroup/div/table");
//    public static final By SEARCH_RESULT_TABLE_LOCATOR = By.id("searchResultsNetworkelementgroup");


    @Step("Validate page")
    public A4InventarSuchePage validate() {
        $(A4_SEARCH_PAGE_HEADER).waitUntil(visible, 3000);
        assertContains(url(), ENDPOINT);
        return this;
    }

    @Step("Login")
    public static A4InventarSuchePage login() {
        URL url = new OCUrlBuilder(A4_RESOURCE_INVENTORY_UI_MS).withEndpoint(ENDPOINT).build();
        return open(url, A4InventarSuchePage.class);
    }

}
