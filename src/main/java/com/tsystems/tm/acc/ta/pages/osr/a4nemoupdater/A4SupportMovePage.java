package com.tsystems.tm.acc.ta.pages.osr.a4nemoupdater;

import io.qameta.allure.Step;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.url;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_UI_MS;
import static com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ImportPage.A4_UI_HEADER_LOCATOR;
import static com.tsystems.tm.acc.ta.util.Assert.assertContains;

@Slf4j
public class A4SupportMovePage {

    public static final String ENDPOINT = A4_RESOURCE_INVENTORY_UI_MS + "/a4-support-ui/move-all-from-deadletter-queue";
    @Getter
    public static final By A4_SUPPORT_UI_MOVE_FROM_DLQ_BUTTON_LOCATOR = byId("btnSupportCleanNemoQueue");
    @Getter
    public static final By A4_SUPPORT_UI_MOVE_FROM_DLQ_MSG_LOCATOR = byXpath("/html/body/app-root/div/app-support-portal/div/app-move-all-from-deadletter-queue/div/span");

    @Step("Validate page")
    public A4SupportMovePage validate() {
        $(A4_UI_HEADER_LOCATOR).shouldBe(visible);
        assertContains(url(), ENDPOINT);
        return this;
    }

}
