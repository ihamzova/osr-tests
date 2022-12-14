package com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selectors.byXpath;


@Slf4j
@Getter
public class A4MobileMonitoringPage {

    public static final By A4_SEARCH_PAGE_HEADER = byXpath("/html/body/app-root/div/app-portal/app-page-header-v2/div[4]/h2");

    @Getter
    public static final By SEARCH_NE_RESULT_TABLE_LOCATOR = By.id("NETable");

    @Getter
    public static final By SEARCH_NEL_RESULT_TABLE_LOCATOR = By.id("NELTable");

    @Getter
    //public static final By EMPTY_LIST_MESSAGE_LOCATOR = By.xpath("//div[@id='MessageNoNetworkElementsInMonitoringList']");
    public static final By EMPTY_NE_LIST_MESSAGE_LOCATOR = By.id("MessageNoNetworkElementsInMonitoringList");

    @Getter
    //public static final By EMPTY_LIST_MESSAGE_LOCATOR = By.xpath("//div[@id='MessageNoNetworkElementLinksInMonitoringList']");
    public static final By EMPTY_NEL_LIST_MESSAGE_LOCATOR = By.id("MessageNoNetworkElementLinksInMonitoringList");

    // this Locator is used for both delete buttons (in NE and NEL monitoring) after locating the table
    // e.g. $(A4MobileMonitoringPage.getSEARCH_NEL_RESULT_TABLE_LOCATOR()).toWebElement().findElement(A4MobileMonitoringPage.getDELETE_BUTTON_LOCATOR())
    @Getter
    public static final By DELETE_BUTTON_LOCATOR = By.xpath("//button[contains(text(),'X')]");

}
