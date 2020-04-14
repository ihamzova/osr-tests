package com.tsystems.tm.acc.ta.pages.osr.oltcommissioning;

import com.tsystems.tm.acc.data.models.OltDevice;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.appears;
import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;

@Slf4j
public class OltCommissioningPage {

    public static final String APP = "olt-resource-inventory-ui";
    public static final String ENDPOINT = "/commissioning";

    public static final By OLT_KLS_ID_INPUT_LOCATOR = byQaData("input-oltklsidtxt");
    public static final By OLT_SLOT_NUMBER_INPUT_LOCATOR = byQaData("input-oltslotnumbertxt");
    public static final By OLT_PORT_NUMBER_INPUT_LOCATOR = byQaData("input-oltportnumbertxt");
    public static final By OLT_BNG_ENDSZ_INPUT_LOCATOR = byQaData("input-bngendsztxt");
    public static final By BNG_EQUIPMENTHOLDER_INPUT_LOCATOR = byQaData("input-bngslotnumbertxt");
    public static final By BNG_DOWNLINK_CARD_PORT_INPUT_LOCATOR = byQaData("input-bngportnumbertxt");
    public static final By LSZ_SELECT_LOCATOR = byQaData("sui-select-lsz");
    public String LSZ_VALUE_LOCATOR = "//*[@qa-data='sui-select-option-%s']";
    public static final By ORDER_NUMBER_INPUT_LOCATOR = byQaData("input-ordernumbertxt");
    public static final By COMMISSIONING_START_BUTTON_LOCATOR = byQaData("button-start-commissioning");
    public static final By CARDS_DETAILS_TAB_LOCATOR = byQaData("a-cards-view");

    @Step("Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

    @Step("Input params and start commissioning")
    public OltCommissioningPage startOltCommissioning(OltDevice olt, Integer timeout) {
        $(OLT_KLS_ID_INPUT_LOCATOR).click();
        $(OLT_KLS_ID_INPUT_LOCATOR).val(olt.getVst().getAddress().getKlsId());
        $(OLT_SLOT_NUMBER_INPUT_LOCATOR).click();
        $(OLT_SLOT_NUMBER_INPUT_LOCATOR).val(olt.getOltSlot());
        $(OLT_PORT_NUMBER_INPUT_LOCATOR).click();
        $(OLT_PORT_NUMBER_INPUT_LOCATOR).val(olt.getOltPort());
        $(OLT_BNG_ENDSZ_INPUT_LOCATOR).click();
        $(OLT_BNG_ENDSZ_INPUT_LOCATOR).val(olt.getBngEndsz());
        $(BNG_EQUIPMENTHOLDER_INPUT_LOCATOR).click();
        $(BNG_EQUIPMENTHOLDER_INPUT_LOCATOR).val(olt.getBngDownlinkSlot());
        $(BNG_DOWNLINK_CARD_PORT_INPUT_LOCATOR).click();
        $(BNG_DOWNLINK_CARD_PORT_INPUT_LOCATOR).val(olt.getBngDownlinkPort());
        $(LSZ_SELECT_LOCATOR).click();
        $(By.xpath(String.format(LSZ_VALUE_LOCATOR, olt.getLsz()))).click();
        $(ORDER_NUMBER_INPUT_LOCATOR).click();
        $(ORDER_NUMBER_INPUT_LOCATOR).val(olt.getOrderNumber());
        $(COMMISSIONING_START_BUTTON_LOCATOR).click();
        $(CARDS_DETAILS_TAB_LOCATOR).waitUntil(appears, timeout);
        return this;
    }
}
