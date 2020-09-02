package com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory;

import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyOrReplyMessage;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.url;
import static com.tsystems.tm.acc.ta.util.Assert.assertContains;

@Slf4j
@Getter
public class A4MobileNeSearchPage {

    public static final String APP = "a4-resource-inventory-ui";
    public static final String ENDPOINT = "a4-resource-inventory-ui/a4-installation-process";

    public static final By A4_SEARCH_PAGE_HEADER = byXpath("/html/body/app-root/div/app-portal/app-page-header-v2/div[4]/h2");

    @Getter
    public static final By VPSZ_INPUT_FIELD_LOCATOR = By.id("vpsz");

    @Getter
    public static final By CATEGORY_INPUT_FIELD_LOCATOR = By.id("category");

    @Getter
    public static final By SEARCH_BUTTON_LOCATOR = By.xpath("//button[contains(text(),'Suchen')]");

    @Getter
    public static final By SEARCH_RESULT_TABLE_LOCATOR = By.xpath("//table");

    @Getter
    public static final By FSZ_INPUT_FIELD_LOCATOR = By.id("fsz");

    @Getter
    public static final By PLANNING_CHECKBOX_LOCATOR = By.id("PLANNING");

    @Getter
    public static final By OPERATING_CHECKBOX_LOCATOR = By.id("OPERATING");

    @Step("Validate page")
    public A4MobileNeSearchPage validate() {
        $(A4_SEARCH_PAGE_HEADER).waitUntil(visible, 3000);
        assertContains(url(), ENDPOINT);
        return this;
    }

    @Step("Login")
    public static A4MobileNeSearchPage login() {
        URL url = new OCUrlBuilder(APP).withEndpoint("a4-resource-inventory-ui/a4-installation-process").build();
        return open(url, A4MobileNeSearchPage.class);
    }

}
