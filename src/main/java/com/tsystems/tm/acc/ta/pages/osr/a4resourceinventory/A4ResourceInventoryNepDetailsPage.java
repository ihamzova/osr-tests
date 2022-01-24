package com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory;

import io.qameta.allure.Step;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.url;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_UI_MS;
import static com.tsystems.tm.acc.ta.util.Assert.assertContains;

@Slf4j
@Getter
public class A4ResourceInventoryNepDetailsPage {

    public static final String ENDPOINT = A4_RESOURCE_INVENTORY_UI_MS + "/a4-inventory-browser/network-element-port";
    public static final By A4_SEARCH_PAGE_HEADER = byXpath("/html/body/app-root/div/app-portal/app-page-header-v3");

    @Step("Validate page")
    public A4ResourceInventoryNepDetailsPage validate() {
        $(A4_SEARCH_PAGE_HEADER).shouldBe(visible, Duration.ofMillis(3000));
        assertContains(url(), ENDPOINT);
        return this;
    }

    @Step("Validate page")
    public A4ResourceInventoryNepDetailsPage validate(String uuid) {
        $(A4_SEARCH_PAGE_HEADER).shouldBe(visible, Duration.ofMillis(3000));
        assertContains(url(), ENDPOINT + "/" + uuid);
        return this;
    }

}
