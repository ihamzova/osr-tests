package com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory;

import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
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
public class A4ResourceInventoryNegDetailsPage {

    public static final String ENDPOINT = A4_RESOURCE_INVENTORY_UI_MS + "/a4-inventory-browser/network-element-group";  // + uuid des NE /0571e643-eec2-4c8b-8266-b1e4b35f35dd
    public static final By NEG_UUID_FIELD_LOCATOR = By.id("uuid");
    public static final By NEG_NAME_FIELD_LOCATOR = By.id("name");
    public static final By NEG_DESCRIPTION_FIELD_LOCATOR = By.id("description");
    public static final By NEG_LCS_FIELD_LOCATOR = By.id("lifecycleState");
    public static final By NEG_CreationTime_FIELD_LOCATOR = By.id("creationTime");
    public static final By NEG_LastUpdateTime_FIELD_LOCATOR = By.id("lastUpdateTime");
    public static final By NEG_LastSuccessfulSync_FIELD_LOCATOR = By.id("lastSuccessfulSyncTime");
    public static final By NELIST_SEARCH_RESULT_TABLE_LOCATOR = By.id("tblNeList");

    @Step("Validate NEG detail page w/o uuid")
    public A4ResourceInventoryNegDetailsPage validate() {
        $(A4_UI_HEADER_LOCATOR).shouldBe(visible, Duration.ofMillis(3000));
        System.out.println("url: " + url()) ;
        System.out.println("ENDPOINT w/o Uuid: " + ENDPOINT) ;
        assertContains(url(), ENDPOINT);
        return this;
    }

    @Step("Validate NEG detail page w/ uuid")
    public A4ResourceInventoryNegDetailsPage validate(String uuid) {
        $(A4_UI_HEADER_LOCATOR).shouldBe(visible, Duration.ofMillis(3000));
        System.out.println("NEG Uuid: " + uuid) ;
        System.out.println("url: " + url()) ;
        System.out.println("ENDPOINT w/ Uuid: " + ENDPOINT + "/" + uuid) ;
        assertContains(url(), ENDPOINT + "/" + uuid);
        return this;
    }

    @Step("Login")
    public static A4ResourceInventoryNegDetailsPage login() {
        URL url = new OCUrlBuilder(A4_RESOURCE_INVENTORY_UI_MS).withEndpoint(ENDPOINT).build();
        return open(url, A4ResourceInventoryNegDetailsPage.class);
    }

}
