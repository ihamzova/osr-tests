package com.tsystems.tm.acc.ta.pages.osr.a4nemoupdater;

import io.qameta.allure.Step;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.url;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_UI_MS;
import static com.tsystems.tm.acc.ta.util.Assert.assertContains;

@Slf4j
public class A4SupportListQueuePage {

    public static final String ENDPOINT = A4_RESOURCE_INVENTORY_UI_MS + "/a4-support-ui/list-queue";

    public static final By A4_SUPPORT_UI_HEADER_LOCATOR = byXpath("/html/body/app-root/div/app-portal/app-page-header-v3");

    @Getter
    public static final By A4_SUPPORT_UI_TABLE_LOCATOR = byId("thMessageID");

    @Step("Validate page")
    public A4SupportListQueuePage validate() {
        $(A4_SUPPORT_UI_HEADER_LOCATOR).shouldBe(visible);
        assertContains(url(), ENDPOINT);
        return this;
    }

}
