package com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory;

import io.qameta.allure.Step;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.url;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_UI_MS;
import static com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ImportPage.A4_UI_HEADER_LOCATOR;
import static com.tsystems.tm.acc.ta.util.Assert.assertContains;

@Slf4j
@Getter
public class A4ResourceInventoryNelDetailsPage {

    public static final String ENDPOINT = A4_RESOURCE_INVENTORY_UI_MS + "/a4-inventory-browser/network-element-link";

    @Step("Validate page")
    public A4ResourceInventoryNelDetailsPage validate() {
        $(A4_UI_HEADER_LOCATOR).shouldBe(visible, Duration.ofMillis(3000));
        assertContains(url(), ENDPOINT);
        return this;
    }

    @Step("Validate page")
    public A4ResourceInventoryNelDetailsPage validate(String uuid) {
        $(A4_UI_HEADER_LOCATOR).shouldBe(visible, Duration.ofMillis(3000));
        assertContains(url(), ENDPOINT + "/" + uuid);
        return this;
    }

}
