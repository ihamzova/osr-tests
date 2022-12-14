package com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.net.URL;
import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.url;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_UI_MS;
import static com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ImportPage.A4_UI_HEADER_LOCATOR;
import static com.tsystems.tm.acc.ta.util.Assert.assertContains;

@Slf4j
@Getter
public class A4ResourceInventoryBrowserPage {

    public static final String ENDPOINT = A4_RESOURCE_INVENTORY_UI_MS + "/a4-inventory-browser";
    public static final By InventorySearchButton_Locator = By.id("btnInventorySearch");
    public static final By InventoryImportButton_Locator = By.id("btnInventoryImport");
    public static final By MobilUiButton_Locator = By.id("btnMobileUi");

    @Step("Validate page")
    public A4ResourceInventoryBrowserPage validate() {
        $(A4_UI_HEADER_LOCATOR).shouldBe(visible, Duration.ofMillis(3000));
        assertContains(url(), ENDPOINT);
        return this;
    }

    @Step("Login")
    public static A4ResourceInventoryBrowserPage login() {
        URL url = new GigabitUrlBuilder(A4_RESOURCE_INVENTORY_UI_MS).withEndpoint(ENDPOINT).build();
        return open(url, A4ResourceInventoryBrowserPage.class);
    }


}
