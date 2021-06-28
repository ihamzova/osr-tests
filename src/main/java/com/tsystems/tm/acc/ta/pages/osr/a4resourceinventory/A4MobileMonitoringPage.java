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
    public static final By SEARCH_NE_RESULT_TABLE_LOCATOR = By.xpath("//NETable");

    @Getter
    public static final By SEARCH_NEL_RESULT_TABLE_LOCATOR = By.xpath("//NELTable");

    @Getter
    //public static final By EMPTY_LIST_MESSAGE_LOCATOR = By.xpath("//div[@id='MessageNoNetworkElementsInMonitoringList']");
    public static final By EMPTY_NE_LIST_MESSAGE_LOCATOR = By.id("ErrorMessageNE");

    @Getter
    //public static final By EMPTY_LIST_MESSAGE_LOCATOR = By.xpath("//div[@id='MessageNoNetworkElementLinksInMonitoringList']");
    public static final By EMPTY_NEL_LIST_MESSAGE_LOCATOR = By.id("ErrorMessageNEL");

    @Getter
    public static final By DELETE_NE_BUTTON_LOCATOR = By.xpath("//button[contains(text(),'X')]");
    @Getter
    public static final By DELETE_NEL_BUTTON_LOCATOR = By.xpath("//button[contains(text(),'X')]");

}
