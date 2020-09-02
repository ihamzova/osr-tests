package com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory;

import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyOrReplyMessage;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.url;
import static com.tsystems.tm.acc.ta.util.Assert.assertContains;

@Slf4j
@Getter
public class A4MobileInbetriebnahmePage {

    @Getter
    public static final By FERTIG_BUTTON_LOCATOR = By.xpath("//button[contains(text(),'Installation Fertig')]");



}
