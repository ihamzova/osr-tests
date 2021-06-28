package com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selectors.byId;

@Slf4j
public class A4MobileInbetriebnahmeNELPage {

    @Getter
    public static final By CHECKBOX_LOCATOR = byId("checkboxAuswahl");

    @Getter
    public static final By START_INSTALL_BTN = byId("StartInstallBtn");

    @Getter
    public static final By ERROR_LOCATOR = By.id("notFoundMsg");

    @Getter
    public static final By PLANNING_FILTER_LOCATOR = By.id("lcsPLANNING");

}
