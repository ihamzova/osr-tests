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
    public static final By SEARCH_RESULT_TABLE_LOCATOR = By.xpath("//table");

    @Getter
    public static final By DELETE_BUTTON_LOCATOR = By.xpath("//u[contains(text(),'X')]");

}
