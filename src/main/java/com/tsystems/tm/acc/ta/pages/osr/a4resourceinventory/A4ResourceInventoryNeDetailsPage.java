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
public class A4ResourceInventoryNeDetailsPage {

    public static final String ENDPOINT = A4_RESOURCE_INVENTORY_UI_MS + "/a4-inventory-browser/network-element";  // + uuid des NE /0571e643-eec2-4c8b-8266-b1e4b35f35dd
    public static final By NE_UUID_FIELD_LOCATOR = By.id("uuid");
    public static final By NE_VPSZ_FIELD_LOCATOR = By.id("vpsz");
    public static final By NE_FSZ_FIELD_LOCATOR = By.id("fsz");
    public static final By NE_CATEGORY_FIELD_LOCATOR = By.id("category");
    public static final By NE_TYPE_FIELD_LOCATOR = By.id("type");
    public static final By NE_KLSID_FIELD_LOCATOR = By.id("klsId");
    public static final By NE_ZTPID_FIELD_LOCATOR = By.id("ztpIdent");
    public static final By NE_OPS_FIELD_LOCATOR = By.id("operationalState");
    public static final By NE_LCS_FIELD_LOCATOR = By.id("lifecycleState");
    public static final By NE_CreationTime_FIELD_LOCATOR = By.id("creationTime");
    public static final By NE_LastUpdateTime_FIELD_LOCATOR = By.id("lastUpdateTime");
    public static final By NE_LastSuccessfulSync_FIELD_LOCATOR = By.id("lastSuccessfulSyncTime");
    public static final By NEL_SEARCH_RESULT_TABLE_LOCATOR = By.id("tblNelNep4NeDetails");

    @Step("Validate NE detail page w/o uuid")
    public A4ResourceInventoryNeDetailsPage validate() {
        $(A4_UI_HEADER_LOCATOR).shouldBe(visible, Duration.ofMillis(3000));
        System.out.println("url: " + url()) ;
        System.out.println("ENDPOINT: " + ENDPOINT) ;
        assertContains(url(), ENDPOINT);
        return this;
    }

    @Step("Validate NE detail page w/ uuid")
    public A4ResourceInventoryNeDetailsPage validate(String uuid) {
        $(A4_UI_HEADER_LOCATOR).shouldBe(visible, Duration.ofMillis(3000));
        System.out.println("NE Uuid: " + uuid) ;
        System.out.println("url: " + url()) ;
        System.out.println("ENDPOINT w/ Uuid: " + ENDPOINT + "/" + uuid) ;
        assertContains(url(), ENDPOINT + "/" + uuid);
        return this;
    }

    @Step("Login")
    public static A4ResourceInventoryNeDetailsPage login() {
        URL url = new GigabitUrlBuilder(A4_RESOURCE_INVENTORY_UI_MS).withEndpoint(ENDPOINT).build();
        return open(url, A4ResourceInventoryNeDetailsPage.class);
    }

}
