package com.tsystems.tm.acc.ta.pages.osr.a4nemoupdater;

import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.net.URL;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.url;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_UI_MS;
import static com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ImportPage.A4_UI_HEADER_LOCATOR;
import static com.tsystems.tm.acc.ta.util.Assert.assertContains;

@Slf4j
public class A4SupportPage {

    public static final String ENDPOINT = A4_RESOURCE_INVENTORY_UI_MS + "/a4-support-ui";
    @Getter
    public static final By A4_SUPPORT_UI_CLEAN_NEMO_QUEUE_BUTTON_LOCATOR = byId("btnSupportCleanNemoQueue");
    @Getter
    public static final By A4_SUPPORT_UI_MOVE_FROM_DLQ_BUTTON_LOCATOR = byId("btnSupportMoveAllFromDeadletterQueueToNormal");
    @Getter
    public static final By A4_SUPPORT_UI_LIST_QUEUE_BUTTON_LOCATOR = byId("btnSupportListQueue");

    @Step("Validate page")
    public A4SupportPage validate() {
        $(A4_UI_HEADER_LOCATOR).shouldBe(visible);
        assertContains(url(), ENDPOINT);
        return this;
    }

    @Step("Login")
    public static A4SupportPage login() {
        URL url = new OCUrlBuilder(A4_RESOURCE_INVENTORY_UI_MS).withEndpoint(ENDPOINT).build();
        return open(url, A4SupportPage.class);
    }
}
