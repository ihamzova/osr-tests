package com.tsystems.tm.acc.ta.pages.osr.oltcommissioning;

import com.tsystems.tm.acc.data.models.nvt.Nvt;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selectors.byXpath;
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
    public static final By BNG_ENDSZ_INPUT_LOCATOR = byQaData("input-bngEndSz");
    public static final By BNG_EQUIPMENTHOLDER_INPUT_LOCATOR = byQaData("input-bngSlot");
    public static final By BNG_DOWNLINK_CARD_PORT_INPUT_LOCATOR = byQaData("input-bngPort");
    public static final By LSZ_SELECT_LOCATOR = byQaData("sui-select-lsz");
    public static final By LSZ_SELECT_OPTION_LOCATOR = byQaData("sui-select-option-4C1");
    public static final By ORDER_NUMBER_INPUT_LOCATOR = byQaData("input-orderNumber");
    public static final By CREATE_UPLINK_BUTTON_LOCATOR = byQaData("button-uplink-creation");
    public static final By DELETE_MODIFY_UPLINK_BUTTON_LOCATOR = byXpath("/html/body/app-root/div/div/div/app-uplink-configuration/form/div[6]/div/button[2]");
    //public static final By MODIFY_UPLINK_BUTTON_LOCATOR = byXpath ("/html/body/app-root/div/div/div/app-uplink-configuration/form/div[6]/div/button[2]");
    public String PORT_VALUE_LOCATOR_STRING = "sui-select-option-%s";
    public String LSZ_VALUE_LOCATOR_STRING = "sui-select-option-%s";


    @Step("Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

    @Step("Input uplink parameters")
    public UplinkConfigurationPage inputUplinkParameters(Nvt nvt) {
        $(OLT_SLOT_SELECT_LOCATOR).click();
        $(OLT_SLOT_SELECT_OPTION_LOCATOR).click(); //$(byQaData("sui-select-option-19")).click();
        $(OLT_PORT_SELECT_LOCATOR).click();
        $(byQaData(String.format(PORT_VALUE_LOCATOR_STRING, nvt.getOltPort()))).click();
        $(OLT_PORT_SELECT_OPTION_LOCATOR).click();
        $(BNG_ENDSZ_INPUT_LOCATOR).val(nvt.getOltDevice().getBngEndsz());
        $(BNG_EQUIPMENTHOLDER_INPUT_LOCATOR).val(nvt.getOltDevice().getBngDownlinkSlot());
        $(BNG_DOWNLINK_CARD_PORT_INPUT_LOCATOR).val(nvt.getOltDevice().getBngDownlinkPort());
        $(LSZ_SELECT_LOCATOR).click();
        $(byQaData(String.format(LSZ_VALUE_LOCATOR_STRING, nvt.getOltDevice().getLsz()))).click();
        $(ORDER_NUMBER_INPUT_LOCATOR).val(nvt.getOltDevice().getOrderNumber());
        return this;
    }

    @Step("Save uplink configuration")
    public OltDetailsPage saveUplinkConfiguration() {
        $(CREATE_UPLINK_BUTTON_LOCATOR).click();
        return new OltDetailsPage();
    }

    @Step("Modify uplink")
    public OltDetailsPage modifyUplinkConfiguration() {
        $(DELETE_MODIFY_UPLINK_BUTTON_LOCATOR).click();
        return new OltDetailsPage();
    }

    @Step("Delete uplink")
    public OltDetailsPage deleteUplinkConfiguration() {
        $(DELETE_MODIFY_UPLINK_BUTTON_LOCATOR).click();
        return new OltDetailsPage();
    }

}
