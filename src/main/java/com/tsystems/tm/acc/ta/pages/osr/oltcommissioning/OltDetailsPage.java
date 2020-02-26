package com.tsystems.tm.acc.ta.pages.osr.oltcommissioning;

import com.codeborne.selenide.SelenideElement;
import com.tsystems.tm.acc.data.models.nvt.Nvt;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.util.stream.IntStream;

import static com.codeborne.selenide.Condition.appears;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;

@Slf4j
public class OltDetailsPage {

    public static final Integer MAX_LATENCY_FOR_ELEMENT_APPEARS = 60_000;
    public static final String APP = "olt-resource-inventory-ui";
    public static final String ENDPOINT = "/detail";

    public static final By CARDS_DETAILS_TAB_LOCATOR = byQaData("a-cards-tab");
    public static final By UPLINKS_DETAILS_TAB_LOCATOR = byQaData("a-uplinks-tab");
    public static final By ANCP_SESSIONS_DETAILS_TAB_LOCATOR = byQaData("a-ancpsessions-tab");//fehlt im gitlab
    public static final By UPLINK_CONFIGURATION_BUTTON_LOCATOR = byQaData("button-uplink-configuration");
    public static final By UPLINK_EDIT_SELECT_LOCATOR = byQaData("div-uplink-edit-menu");
    public static final By UPLINK_DECONFIGURATION_BUTTON_LOCATOR = byQaData("div-delete_uplink");//fehlt im gitlab
    public static final By UPLINK_DELETE_OPTION_LOCATOR= byXpath("/html/body/app-root/div/div/div/app-detail/app-olt-detail/div[3]/div[1]/table/tbody/tr/td[10]/div/div");//fehlt im gitlab
    public static final By UPLINK_EDIT_SELECT_ANCP_OPTION_LOCATOR = byQaData("div-configure_ancp_session");
    public static final By ANCP_SESSION_STATUS_LOCATOR = byQaData("a-ancp-status");//fehlt im gitlab
    public static final By ANCP_EDIT_SELECT_LOCATOR = byXpath("/html/body/app-root/div/div/div/app-detail/app-olt-detail/div[2]/div[1]/table/tbody/tr/td[10]/div/div");//fehlt im gitlab
    public static final By ANCP_EDIT_SELECT_ANCP_OPTION_LOCATOR = byXpath("/html/body/app-root/div/div/div/app-detail/app-olt-detail/div[2]/div[1]/table/tbody/tr/td[10]/div/div/div/div[2]");//fehlt im gitlab
    public static final By CARD_EDIT_MENU_LOCATOR = byQaData("div-card-edit-menu");
    public static final By CARD_COMMISSIONING_OPTION_LOCATOR = byQaData("div-card-commissioning");
    public static final By CARD_COMMISSIONING_START_BUTTON_LOCATOR = byQaData("button-start-commissioning");


    @Step("Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

    @Step("Configure uplink")
    public UplinkConfigurationPage startUplinkConfiguration() {
        $(UPLINKS_DETAILS_TAB_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        $(UPLINK_CONFIGURATION_BUTTON_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        return new UplinkConfigurationPage();
    }

    @Step("Deconfigure uplink")
    public UplinkConfigurationPage startUplinkDeConfiguration() {
        $(UPLINKS_DETAILS_TAB_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        $(UPLINK_DELETE_OPTION_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        $(UPLINK_DECONFIGURATION_BUTTON_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        return new UplinkConfigurationPage();
    }

    @Step("Configure ANCP session")
    public OltDetailsPage configureAncpSession() {
        $(UPLINKS_DETAILS_TAB_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        $(UPLINK_EDIT_SELECT_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            log.error("Interrupted");
        }

        $(UPLINK_EDIT_SELECT_ANCP_OPTION_LOCATOR).click();
        return this;
    }

    @Step("Deconfigure ANCP session")
    public OltDetailsPage deconfigureAncpSession() {
        $(ANCP_SESSIONS_DETAILS_TAB_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
       // $(ANCP_EDIT_SELECT_ANCP_OPTION_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        $(ANCP_EDIT_SELECT_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            log.error("Interrupted");
        }

        $(ANCP_EDIT_SELECT_ANCP_OPTION_LOCATOR).click();
        return this;
    }

    @Step("Update ANCP Session Status")
    public OltDetailsPage updateAncpSessionStatus() {
        $(ANCP_SESSIONS_DETAILS_TAB_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        $(ANCP_SESSION_STATUS_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        return this;
    }

    @Step("Access lines provisioning")
    public OltDetailsPage startAccessLinesProvisioning(Nvt nvt, Integer timeout) {
        $(CARDS_DETAILS_TAB_LOCATOR).click();
        $(CARD_EDIT_MENU_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS);

        IntStream.range(0, $$(CARD_EDIT_MENU_LOCATOR).size()).forEach(element -> {
            $$(CARD_EDIT_MENU_LOCATOR).get(element).click();
            $$(CARD_COMMISSIONING_OPTION_LOCATOR).stream().filter(SelenideElement::isDisplayed).findFirst().ifPresent(el -> {
                el.click();
                $(CARD_COMMISSIONING_START_BUTTON_LOCATOR).click();
                $(CARDS_DETAILS_TAB_LOCATOR).waitUntil(appears, timeout).click();
            });
        });
        return this;
    }
}
