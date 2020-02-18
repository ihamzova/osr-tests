package com.tsystems.tm.acc.ta.pages.osr.oltresourceinventory;

import com.codeborne.selenide.Condition;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;

@Slf4j
public class OltRiOltDetailPage {
    private static final String APP = "olt-resource-inventory-ui";
    private static final String ENDPOINT = "/detail/";

    private static final By ENDSZ_LOCATOR = byXpath("//div[div[text()='EndSz:']]");
    private static final By BEZEICHNUNG_LOCATOR = byXpath("//div[div[text()='Bezeichnung:']]");
    private static final By KLSID_LOCATOR = byXpath("//form[div[text()='KLS-ID:']]");
    private static final By HERSTELLER_LOCATOR = byXpath("//div[div[text()='Hersteller:']]");
    private static final By SERIENNUMMER_LOCATOR = byXpath("//div[div[text()='Seriennummer:']]");
    private static final By FIRMWAREVERSION_LOCATOR = byXpath("//div[div[text()='Firmware-Version:']]");
    private static final By IP_ADRESSE_LOCATOR = byXpath("//div[div[text()='IP-Adresse:']]");
    private static final By STATUS_LOCATOR = byXpath("//div[div[text()='Status:']]");
    private static final By LATEST_DISCOVERY_LOCATOR = byXpath("//div[div[text()='Letztes Discovery:']]");
    private static final By ERROR_MESSAGE = byXpath("//div/i[@class='small red delete icon']");

    @Step("Validate top level page")
    public void validate() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
        $(ERROR_MESSAGE).shouldNot(Condition.exist);
    }

    @Step("Get EndSz")
    public String getEndsz() {
        return $(ENDSZ_LOCATOR).getText();
    }

    @Step("Get Bezeichnung")
    public String getBezeichnung() {
        return $(BEZEICHNUNG_LOCATOR).getText();
    }

    @Step("Get KLS-ID")
    public String getKlsID() {
        return $(KLSID_LOCATOR).getText();
    }

    @Step("Get Hersteller")
    public String getHersteller() {
        return $(HERSTELLER_LOCATOR).getText();
    }

    @Step("Get Seriennummer")
    public String getSeriennummer() {
        return $(SERIENNUMMER_LOCATOR).getText();
    }

    @Step("Get Firmware-Version")
    public String getFirmwareVersion() {
        return $(FIRMWAREVERSION_LOCATOR).getText();
    }

    @Step("Get IP-Adresse")
    public String getIpAdresse() {
        return $(IP_ADRESSE_LOCATOR).getText();
    }

    @Step("Get Status")
    public String getStatus() {
        return $(STATUS_LOCATOR).getText();
    }

    @Step("Get Letztes Discovery")
    public String getLatestDiscovery() {
        return $(LATEST_DISCOVERY_LOCATOR).getText();
    }
}
