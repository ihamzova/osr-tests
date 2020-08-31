package com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory;

import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.net.URL;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.url;
import static com.tsystems.tm.acc.ta.util.Assert.assertContains;

public class A4MobileNeSearchPage {

    public static final String APP = "a4-resource-inventory-ui/a4-installation-process";
    public static final String ENDPOINT = "a4-resource-inventory-ui/a4-installation-process";

    public static final By A4_SEARCH_PAGE_HEADER = byXpath("/html/body/app-root/div/app-portal/app-page-header-v2/div[4]/h2");
    public static final By TO_INSTALLATION_BUTTON = By.id("btnInstallation");

    @Step("Validate page")
    public A4MobileNeSearchPage validate() {
        $(A4_SEARCH_PAGE_HEADER).waitUntil(visible, 3000);
        assertContains(url(), ENDPOINT);
        return this;
    }

    @Step("Login")
    public static A4MobileNeSearchPage login() {
        URL url = new OCUrlBuilder(APP).build();
        return open(url, A4MobileNeSearchPage.class);
    }

}