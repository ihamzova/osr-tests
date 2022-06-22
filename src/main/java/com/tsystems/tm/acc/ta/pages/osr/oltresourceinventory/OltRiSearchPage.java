package com.tsystems.tm.acc.ta.pages.osr.oltresourceinventory;

import com.codeborne.selenide.Condition;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.net.URL;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;


@Slf4j
public class OltRiSearchPage {
    private static final String APP = "olt-resource-inventory-ui";
    private static final String ENDPOINT = "/search";

    private static final By HEADER_LOCATOR = byXpath("//h2[contains(text(),'OLT Suche')]");

    private static final By AKZ_LOCATOR = byXpath("//input[@name='akz']");
    private static final By ONKZ_LOCATOR = byXpath("//input[@name='nkz']");
    private static final By VKZ_LOCATOR = byXpath("//input[@name='vkz']");
    private static final By FSZ_LOCATOR = byXpath("//input[@name='fsz']");
    private static final By SUCHEN_BUTTON_LOCATOR = byXpath("//button[normalize-space(text())='Suche']");
    private static final By OLT_NOT_FOUND_MESSAGE_LOCATOR_1 = byXpath("//div[normalize-space(text())='OLT wurde nicht gefunden.']");
    private static final By OLT_NOT_FOUND_MESSAGE_LOCATOR_2 = byXpath("//p[normalize-space(text())='Anhand der Suchparamter konnte kein OLT im Inventar gefunden werden. Bitte überprüfen sie ihre Eingabe oder starten sie ein OLT Discovery.']");
    private static final By START_AUTO_OLT_COMMISSIONING_BUTTON_LOCATOR = byXpath("//button[normalize-space(text())='automatisierte OLT-Inbetriebnahme']");

    @Step("Open Search page")
    public static OltRiSearchPage openPage() {
        URL url = new GigabitUrlBuilder(APP).withEndpoint(ENDPOINT).build();
        log.info("Opening url " + url.toString());
        return open(url, OltRiSearchPage.class);
    }

    @Step("Validate top level page")
    public void validate() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
        $(HEADER_LOCATOR).shouldBe(visible);
    }


    @Step("Type Akz")
    public void typeAkz(String value) {
        $(AKZ_LOCATOR).val(value);
    }

    @Step("Type ONKz")
    public void typeOnkz(String value) {
        $(ONKZ_LOCATOR).val(value);
    }

    @Step("Type VKZ")
    public void typeVkz(String value) {
        $(VKZ_LOCATOR).val(value);
    }

    @Step("Type FSZ")
    public void typeFsz(String value) {
        $(FSZ_LOCATOR).val(value);
    }

    @Step("Click \"Suchen\" button")
    public void clickSearchButton() {
        $(SUCHEN_BUTTON_LOCATOR).click();
    }

    @Step("Check that no OLT was found")
    public void checkOltWasNotFound() {
        $(OLT_NOT_FOUND_MESSAGE_LOCATOR_1).should(Condition.exist);
        $(OLT_NOT_FOUND_MESSAGE_LOCATOR_2).should(Condition.exist);
    }

    @Step("Click \"automatisierte OLT-Inbetriebnahme\" button")
    public OltRiCommissioningPage clickStartAutomaticOltCommissioning() {
        $(START_AUTO_OLT_COMMISSIONING_BUTTON_LOCATOR).click();
        return new OltRiCommissioningPage();
    }
}
