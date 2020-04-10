package com.tsystems.tm.acc.ta.pages.osr.oltcommissioning;

import com.codeborne.selenide.Condition;
import com.tsystems.tm.acc.data.models.oltdevice.OltDevice;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.appears;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;

@Slf4j
public class OltDetailsPage {

    public static final Integer MAX_LATENCY_FOR_ELEMENT_APPEARS = 60_000;
    public static final Integer MAX_ANCP_COFIGURATION_TIME = 2 * 60_000;
    public static final String APP = "olt-resource-inventory-ui";
    public static final String ENDPOINT = "/detail";

    public static final By CARDS_VIEW_TAB_LOCATOR = byQaData("a-cards-view");
    public static final By CONFIGURATION_VIEW_TAB_LOCATOR = byQaData("a-configuration-view");

    public static final By UPLINK_ADD_BUTTON_LOCATOR = byQaData("button-add-uplink");
    public static final By UPLINK_EDIT_BUTTON_LOCATOR = byQaData("button-edit-uplink");

    //  input uplink values
    public static final By OLT_SLOT_SELECT_LOCATOR = byQaData("div-oltSlot");
    public static final By OLT_PORT_SELECT_LOCATOR = byQaData("div-oltPort");
    public static final By BNG_ENDSZ_INPUT_LOCATOR = byQaData("input-bngEndSz");
    public static final By BNG_EQUIPMENTHOLDER_INPUT_LOCATOR = byQaData("input-bngSlot");
    public static final By BNG_DOWNLINK_CARD_PORT_INPUT_LOCATOR = byQaData("input-bngPort");
    public static final By LSZ_SELECT_LOCATOR = byQaData("div-uplinkLsz");
    public static final By ORDER_NUMBER_INPUT_LOCATOR = byQaData("input-orderNumber");
    public String slotValueLocatorString = "div-%s";
    public String portValueLocatorString = "div-%s";
    public String lszValueLocatorString = "div-%s";

    public static final By UPLINK_MODIFY_CONFIRM_BUTTON_LOCATOR = byQaData("button-uplink-modify-uplink");
    public static final By UPLINK_CREATE_CONFIRM_BUTTON_LOCATOR = byQaData("button-uplink-create-uplink");
    public static final By UPLINK_DELETE_BUTTON_LOCATOR = byQaData("button-delete-uplink");
    public static final By UPLINK_DELETE_CONFIRM_BUTTON_LOCATOR = byQaData("button-uplink-delete-uplink");

    public static final By ANCP_CONFIGURE_BUTTON_LOCATOR = byQaData("button-configure-ancp-session");
    public static final By ANCP_DE_CONFIGURE_BUTTON_LOCATOR = byQaData("button-de-configure-ancp-session");
    public static final By ANCP_SESSION_STATUS_UNKNOWN_LOCATOR = byQaData("a-ancpstatetest");
    public static final By ANCP_SESSION_STATUS_LOCATOR = byQaData("span-ancpstate");

    public static final int[] AVAILABLE_LINE_CARD_SLOTS_ARRAY = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 11, 12, 13, 14, 15, 16, 17, 18};
    public String cardCommissioningStartButtonLocator = "button-card-commissioning-slot-%d";
    public String cardDeCommissioningStartButtonLocator = "button-card-de-commissioning-slot-%d";

    //validation
    private static final By ENDSZ_LOCATOR = byQaData("span-olt-endsz");
    private static final By BEZEICHNUNG_LOCATOR = byQaData("span-olt-emsnbiname");
    private static final By KLSID_LOCATOR = byQaData("span-olt-klsid");


    @Step("Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

    @Step("Configure uplink")
    public OltDetailsPage startUplinkConfiguration() {
        $(CONFIGURATION_VIEW_TAB_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        $(UPLINK_ADD_BUTTON_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        return this;
    }

    @Step("Input uplink parameters")
    public OltDetailsPage inputUplinkParameters(OltDevice olt) {
        $(OLT_SLOT_SELECT_LOCATOR).click();
        $(byQaData(String.format(slotValueLocatorString, olt.getOltSlot()))).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        $(OLT_PORT_SELECT_LOCATOR).click();
        $(byQaData(String.format(portValueLocatorString, olt.getOltPort()))).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        $(BNG_ENDSZ_INPUT_LOCATOR).val(olt.getBngEndsz());
        $(BNG_EQUIPMENTHOLDER_INPUT_LOCATOR).val(olt.getBngDownlinkSlot());
        $(BNG_DOWNLINK_CARD_PORT_INPUT_LOCATOR).val(olt.getBngDownlinkPort());
        $(LSZ_SELECT_LOCATOR).click();
        $(byQaData(String.format(lszValueLocatorString, olt.getLsz()))).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        $(ORDER_NUMBER_INPUT_LOCATOR).val(olt.getOrderNumber());
        return this;
    }


    @Step("Save uplink configuration")
    public OltDetailsPage saveUplinkConfiguration() {
        $(UPLINK_CREATE_CONFIRM_BUTTON_LOCATOR).waitUntil(Condition.appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        return new OltDetailsPage();
    }

    @Step("Modify uplink configuration and save uplink configuration without changes")
    public OltDetailsPage modifyUplinkConfiguration() {
        $(CONFIGURATION_VIEW_TAB_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        $(UPLINK_EDIT_BUTTON_LOCATOR).waitUntil(Condition.appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        $(UPLINK_MODIFY_CONFIRM_BUTTON_LOCATOR).waitUntil(Condition.appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        return this;
    }

    @Step("Deconfigure uplink")
    public OltDetailsPage deleteUplinkConfiguration() {
        $(CONFIGURATION_VIEW_TAB_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        $(UPLINK_DELETE_BUTTON_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        $(UPLINK_DELETE_CONFIRM_BUTTON_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        return this;
    }

    @Step("Configure ANCP session")
    public OltDetailsPage configureAncpSession() {
        $(CONFIGURATION_VIEW_TAB_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        $(ANCP_CONFIGURE_BUTTON_LOCATOR).click();
        $(ANCP_DE_CONFIGURE_BUTTON_LOCATOR).waitUntil(visible, MAX_ANCP_COFIGURATION_TIME).isDisplayed();
        return this;
    }

    @Step("Deconfigure ANCP session")
    public OltDetailsPage deconfigureAncpSession() {
        $(CONFIGURATION_VIEW_TAB_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        $(ANCP_DE_CONFIGURE_BUTTON_LOCATOR).click();
        return this;
    }

    @Step("Update ANCP Session State")
    public OltDetailsPage updateAncpSessionStatus() {
        $(CONFIGURATION_VIEW_TAB_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        $(ANCP_SESSION_STATUS_UNKNOWN_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        return this;
    }

    @Step("Check ANCP Session State is displayed")
    public OltDetailsPage checkAncpSessionStatus() {
        $(CONFIGURATION_VIEW_TAB_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        $(ANCP_SESSION_STATUS_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).isDisplayed();
        return this;
    }

    @Step("Access lines provisioning")
    public OltDetailsPage startAccessLinesProvisioning(Integer timeout) {
        $(CARDS_VIEW_TAB_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        for (int slot : AVAILABLE_LINE_CARD_SLOTS_ARRAY) {
            if ($(byQaData(String.format(cardCommissioningStartButtonLocator, slot))).isDisplayed()) {
                $(byQaData(String.format(cardCommissioningStartButtonLocator, slot))).click();
                $(byQaData(String.format(cardDeCommissioningStartButtonLocator, slot))).waitUntil(visible, timeout).isDisplayed();
            }
        }
        return this;
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
}
