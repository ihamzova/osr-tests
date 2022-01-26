package com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Getter
public class A4MobileNeSearchPage {

    public static final String ENDPOINT = A4_RESOURCE_INVENTORY_UI_MS + "/a4-installation-process";
    public static final By A4_SEARCH_PAGE_HEADER = byXpath("/html/body/app-root/div/app-portal/app-page-header-v2");
    public static final By FERTIG_BUTTON_LOCATOR = By.id("submitButton");
    public static final By ZTPIDENT_FIELD_LOCATOR = By.id("ztpiFromForm");
    public static final By VPSZ_INPUT_FIELD_LOCATOR = By.id("vpsz");
    public static final By AKZ_INPUT_FIELD_LOCATOR = By.id("akz");
    public static final By ONKZ_INPUT_FIELD_LOCATOR = By.id("onkz");
    public static final By VKZ_INPUT_FIELD_LOCATOR = By.id("vkz");
    public static final By FSZ_INPUT_FIELD_LOCATOR = By.id("fsz");
    public static final By CATEGORY_INPUT_FIELD_LOCATOR = By.id("category");
    // checkboxes
    public static final By PLANNING_CHECKBOX_LOCATOR = By.id("lcsPLANNING");
    public static final By OPERATING_CHECKBOX_LOCATOR = By.id("lcsOPERATING");
    public static final By INSTALLING_CHECKBOX_LOCATOR = By.id("lcsINSTALLING");
    // button
    public static final By SEARCH_BUTTON_LOCATOR = By.id("btnSearch");
    public static final By INBETRIEBNAHME_NE_BUTTON_LOCATOR = By.id("neInstallBtn");
    public static final By ZEIGE_NEL_ZU_NE_BUTTON_LOCATOR = By.id("nelShowBtn"); // button doppelt benannt?
    public static final By INBETRIEBNAHME_NEL_BUTTON_LOCATOR = By.id("nelShowBtn");  //war nelBtn
    public static final By ADD_MONITORING_BUTTON_LOCATOR = By.id("neAddMonitorBtn");
    public static final By MONITORING_BUTTON_LOCATOR = By.id("btnMonitoring");
    public static final By NE_RESET_TO_PLANNING_BUTTON_LOCATOR = By.id("neResetInstallBtn");
    //
    public static final By RADIO_BUTTON_LOCATOR = By.id("radioAuswahl");
    public static final By SEARCH_RESULT_TABLE_LOCATOR = By.id("tblResultNetworkElements");
    public static final By ZTPI_INPUT_FIELD_LOCATOR = By.xpath("//table/tr[1]/td[6]");

    public static final By VPSZ_VALUE_LOCATOR = By.id("vpszValue");

    @Step("Validate page")
    public A4MobileNeSearchPage validate() {
        $(A4_SEARCH_PAGE_HEADER).shouldBe(visible, Duration.ofMillis(3000));
        assertContains(url(), ENDPOINT);
        return this;
    }

    @Step("Login")
    public static void login() {
        URL url = new GigabitUrlBuilder(A4_RESOURCE_INVENTORY_UI_MS).withEndpoint(ENDPOINT).build();
        open(url, A4MobileNeSearchPage.class);
    }
}
