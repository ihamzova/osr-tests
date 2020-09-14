package com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

@Slf4j
@Getter
public class A4MobileInbetriebnahmePage {

    @Getter
    public static final By FERTIG_BUTTON_LOCATOR = By.xpath("//button[contains(text(),'Installation abschließen')]");

    @Getter
    public static final By ZTPIDENT_FIELD_LOCATOR = By.id("ztpiFromForm");



}
