package com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory;

import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
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
public class A4ResourceInventoryNeDetailsPage {

    public static final String ENDPOINT = A4_RESOURCE_INVENTORY_UI_MS + "/a4-inventory-browser/network-element";  // + uuid des NE /0571e643-eec2-4c8b-8266-b1e4b35f35dd
    public static final By A4_SEARCH_PAGE_HEADER = byXpath("/html/body/app-root/div/app-portal/app-page-header-v3"); // nicht geprüft!

    @Getter
    public static final By NE_UUID_FIELD_LOCATOR = By.id("uuid");

    @Getter
    public static final By NE_VPSZ_FIELD_LOCATOR = By.id("vpsz");

    @Getter
    public static final By NE_FSZ_FIELD_LOCATOR = By.id("fsz");

    @Getter
    public static final By NE_CATEGORY_FIELD_LOCATOR = By.id("category");

    @Getter
    public static final By NE_TYPE_FIELD_LOCATOR = By.id("type");

    @Getter
    public static final By NE_PlanningDeviceName_FIELD_LOCATOR = By.id("planningDeviceName");

    @Getter
    public static final By NE_KLSID_FIELD_LOCATOR = By.id("klsId");

    @Getter
    public static final By NE_ZTPID_FIELD_LOCATOR = By.id("ztpIdent");

    @Getter
    public static final By NE_OPS_FIELD_LOCATOR = By.id("operationalState");

    @Getter
    public static final By NE_LCS_FIELD_LOCATOR = By.id("lifecycleState");

    @Getter
    public static final By NE_CreationTime_FIELD_LOCATOR = By.id("creationTime");

    @Getter
    public static final By NE_LastUpdateTime_FIELD_LOCATOR = By.id("lastUpdateTime");

    @Getter
    //  public static final By NEL_SEARCH_RESULT_TABLE_LOCATOR = By.className("w3-table-all");
    public static final By NEL_SEARCH_RESULT_TABLE_LOCATOR = By.id("tblNelNep4NeDetails");

    @Step("Validate page")
    public A4ResourceInventoryNeDetailsPage validate() {
        $(A4_SEARCH_PAGE_HEADER).shouldBe(visible, Duration.ofMillis(3000));
        assertContains(url(), ENDPOINT);
        return this;
    }

    @Step("Validate page")
    public A4ResourceInventoryNeDetailsPage validate(String uuid) {
        $(A4_SEARCH_PAGE_HEADER).shouldBe(visible, Duration.ofMillis(3000));
        assertContains(url(), ENDPOINT + "/" + uuid);
        return this;
    }

    @Step("Login")
    public static A4ResourceInventoryNeDetailsPage login() {
        URL url = new OCUrlBuilder(A4_RESOURCE_INVENTORY_UI_MS).withEndpoint(ENDPOINT).build();
        return open(url, A4ResourceInventoryNeDetailsPage.class);
    }

}
