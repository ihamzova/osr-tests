package com.tsystems.tm.acc.ta.ui.pages.oltmaintenance;

import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
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
public class OLTSuchePage {
    private static final String APP = "olt-maintenance-ui";
    private static final String ENDPOINT = "/search";

    private static final By ENDSZ_INPUT_LOCATOR = byXpath("//input[@id='endSZ']");
    private static final By SUCHEN_BUTTON_LOCATOR = byXpath("//button[@type='button']");
    private static final By BRAND_LOGO_LOCATOR = byXpath("//a[@href='#']//img[@alt='Telekom Logo']");
    private static final By HEADER_LOCATOR = byXpath("//h1/strong[contains(text(),'OLT Suche')]");
    private static final By NOT_FOUND_NOTIFICATION_LOCATOR = byXpath("//div[@class='notification-content']//strong[contains(text(),'Suchergebnisse')]");
    private static final By DISCOVERY_BUTTON_LOCATOR = byXpath("//button[contains(text(),'OLT anlegen und Discovery starten')]");
    private static final By NOTIFICATION_HEADING_LOCATOR = byXpath("//div[@class='notification-heading']");
    private static final By ANLEGEN_BUTTON_LOCATOR = byXpath("//button[contains(text(),'OLT anlegen')]");

    @Step("Open Search page")
    public static OLTSuchePage openPage() {
        URL url = new OCUrlBuilder(APP).withEndpoint(ENDPOINT).build();
        log.info("Opening url " + url.toString());
        return open(url, OLTSuchePage.class);
    }

    @Step("Validate top level page")
    public void validate() {
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
        $(HEADER_LOCATOR).shouldBe(visible);
    }

    @Step("Enter EndSZ")
    public OLTSuchePage typeEndSZ(String inputStr) {
        $(ENDSZ_INPUT_LOCATOR).val(inputStr);
        return this;
    }

    @Step("Click Suche Button")
    public void pressSuchenButton() {
        $(SUCHEN_BUTTON_LOCATOR).click();
    }


    @Step("Click Brand Button")
    public OLTSuchePage pressBrandButton() {
        $(BRAND_LOGO_LOCATOR).click();
        return this;
    }

    @Step("Click Discovery Button")
    public DiscoveryStartenPage pressDiscoveryStarten() {
        $(DISCOVERY_BUTTON_LOCATOR).click();
        return new DiscoveryStartenPage();
    }

    public String getDiscoveryNotification() {
        $(NOTIFICATION_HEADING_LOCATOR).shouldBe(visible);
        return $(NOTIFICATION_HEADING_LOCATOR).text();
    }

    @Step("Click Anlegen Button")
    public SearchResultsPage pressAnlegen() {
        $(ANLEGEN_BUTTON_LOCATOR).click();
        return new SearchResultsPage();
    }
}
