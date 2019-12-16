package com.tsystems.tm.acc.ta.ui.pages.oltcommissioning;

import com.tsystems.tm.acc.data.models.oltdevice.OltDevice;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;

@Slf4j
public class UplinkConfigurationPage {

    public static final String APP = "olt-resource-inventory-ui";
    public static final String ENDPOINT = "/uplinkconfiguration";

    public static final By OLT_SLOT_SELECT_LOCATOR = byQaData("sui-select-oltSlot");
    public static final By OLT_SLOT_SELECT_OPTION_LOCATOR = byQaData("sui-select-option-19");
    public static final By OLT_PORT_SELECT_LOCATOR = byQaData("sui-select-oltPort");
    public static final By OLT_PORT_SELECT_OPTION_LOCATOR = byQaData("sui-select-option-1");
    public static final By BNG_ENDSZ_INPUT_LOCATOR = byQaData("input- bngEndSz");
    public static final By BNG_EQUIPMENTHOLDER_INPUT_LOCATOR = byQaData("input-bngSlot");
    public static final By BNG_DOWNLINK_CARD_PORT_INPUT_LOCATOR = byQaData("input-bngPort");
    public static final By LSZ_SELECT_LOCATOR = byQaData("sui-select-lsz");
    public static final By LSZ_SELECT_OPTION_LOCATOR = byQaData("sui-select-option-4C1");
    public static final By ORDER_NUMBER_INPUT_LOCATOR = byQaData("input-orderNumber");
    public static final By CREATE_UPLINK_BUTTON_LOCATOR = byQaData("button-uplink-creation");

    @Step("Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

    @Step("Input uplink parameters")
    public UplinkConfigurationPage inputUplinkParameters(OltDevice oltDevice) {
        $(OLT_SLOT_SELECT_LOCATOR).click();
        $(OLT_SLOT_SELECT_OPTION_LOCATOR).click();
        $(OLT_PORT_SELECT_LOCATOR).click();
        $(OLT_PORT_SELECT_OPTION_LOCATOR).click();
        $(BNG_ENDSZ_INPUT_LOCATOR).val(oltDevice.getBngEndsz());
        $(BNG_EQUIPMENTHOLDER_INPUT_LOCATOR).val(oltDevice.getBngDownlinkSlot());
        $(BNG_DOWNLINK_CARD_PORT_INPUT_LOCATOR).val(oltDevice.getBngDownlinkPort());
        $(LSZ_SELECT_LOCATOR).click();
        $(LSZ_SELECT_OPTION_LOCATOR).click();
        $(ORDER_NUMBER_INPUT_LOCATOR).val(oltDevice.getOrderNumber());
        return this;
    }

    @Step("Save uplink configuration")
    public OltDetailsPage saveUplinkConfiguration() {
        $(CREATE_UPLINK_BUTTON_LOCATOR).click();
        return new OltDetailsPage();
    }
}
