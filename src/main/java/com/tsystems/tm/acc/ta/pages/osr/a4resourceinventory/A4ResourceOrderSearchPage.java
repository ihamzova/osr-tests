package com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory;

import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import io.qameta.allure.Step;
import lombok.Getter;
import org.openqa.selenium.By;

import java.net.URL;
import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.url;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_UI_MS;
import static com.tsystems.tm.acc.ta.util.Assert.assertContains;

public class A4ResourceOrderSearchPage {


    public static final String ENDPOINT = A4_RESOURCE_INVENTORY_UI_MS + "/a4-resource-order-browser/resource-order-search";
    public static final By A4_SEARCH_PAGE_HEADER = byXpath("/html/body/app-root/div/app-portal/app-page-header-v3/div[1]");



    @Getter
    public static final By RO_PUBLIC_REFERENCE_ID_FIELD_LOCATOR = By.id("publicReferenceId");

    @Getter
    public static final By RO_SEARCH_BUTTON_LOCATOR = By.id("searchSubmit");


    @Getter
    public static final By RO_SEARCH_RESULT_TABLE_LOCATOR = By.id("ResourceOrderTable");

    @Getter
    public static final By RO_TABLE_ROW_1 = By.id("tr0");

    @Getter
    public static final By RO_DETAIL_LINK_LOCATOR_1 = By.id("linkRO0");


    @Getter
    public static final By RO_CHECKBOX_IN_PROGRESS = By.id("orderStateINPROGRESS");

    @Getter
    public static final By RO_CHECKBOX_COMPLETED = By.id("orderStateCOMPLETED");

    @Getter
    public static final By RO_CHECKBOX_REJECTED = By.id("orderStateREJECTED");


    // common
    @Step("Validate page")
    public A4ResourceOrderSearchPage validate() {
        $(A4_SEARCH_PAGE_HEADER).shouldBe(visible, Duration.ofMillis(3000));
        assertContains(url(), ENDPOINT);
        return this;
    }

    @Step("Login")
    public static A4ResourceOrderSearchPage login() {
        URL url = new OCUrlBuilder(A4_RESOURCE_INVENTORY_UI_MS).withEndpoint(ENDPOINT).build();
        return open(url, A4ResourceOrderSearchPage.class);
    }

}
