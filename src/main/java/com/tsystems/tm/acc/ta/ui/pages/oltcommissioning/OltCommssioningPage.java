package com.tsystems.tm.acc.ta.ui.pages.oltcommissioning;

import com.tsystems.tm.acc.data.osr.models.oltcommissioning.OltCommissioning;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;

public class OltCommssioningPage {
    static final String APP = "olt-resource-inventory-ui";
    static final String ENDPOINT = "/commissioning";

    private static final By OLT_ENDSZ_INPUT_LOCATOR = byQaData("cc-olt-end-sz-input");
    private static final By OLT_KLS_ID_INPUT_LOCATOR = byQaData("cc-olt-kls-id-input");
    private static final By OLT_SLOT_NUMBER_INPUT_LOCATOR = byQaData("cc-olt-slot-number-input");
    private static final By OLT_PORT_NUMBER_INPUT_LOCATOR = byQaData("cc-olt-port-number-input");
    private static final By OLT_BNG_ENDSZ_INPUT_LOCATOR = byQaData("cc-bng-end-sz-input");
    private static final By BNG_EQUIPMENTHOLDER_INPUT_LOCATOR = byQaData("cc-bng-equipmentholder-input");
    private static final By BNG_DOWNLINK_CARD_PORT_INPUT_LOCATOR = byQaData("cc-bng-downlink-card-port-input");
    private static final By LSZ_SELECT_LOCATOR = byQaData("cc-lsz-select");
    private static final By LSZ_VALUE_LOCATOR = byQaData("cc-lsz-select-1");
    private static final By ORDER_NUMBER_INPUT_LOCATOR = byQaData("cc-order-number-input");
    private static final By COMMISSIONING_START_BUTTON_LOCATOR = byQaData("cc-olt-commissioning-start-button");

    @Step("Validate Url")
    public void validate() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

    @Step("Input params and start commissioning")
    public void startOltCommissioning(OltCommissioning oltCommissioning) {
        $(OLT_ENDSZ_INPUT_LOCATOR).click();
        $(OLT_ENDSZ_INPUT_LOCATOR).val(oltCommissioning.getOltEndSz());
        $(OLT_KLS_ID_INPUT_LOCATOR).click();
        $(OLT_KLS_ID_INPUT_LOCATOR).val(oltCommissioning.getOltKlsId());
        $(OLT_SLOT_NUMBER_INPUT_LOCATOR).click();
        $(OLT_SLOT_NUMBER_INPUT_LOCATOR).val(oltCommissioning.getOltSlotNumber());
        $(OLT_PORT_NUMBER_INPUT_LOCATOR).click();
        $(OLT_PORT_NUMBER_INPUT_LOCATOR).val(oltCommissioning.getOltPortNumber());
        $(OLT_BNG_ENDSZ_INPUT_LOCATOR).click();
        $(OLT_BNG_ENDSZ_INPUT_LOCATOR).val(oltCommissioning.getBngEndSz());
        $(BNG_EQUIPMENTHOLDER_INPUT_LOCATOR).click();
        $(BNG_EQUIPMENTHOLDER_INPUT_LOCATOR).val(oltCommissioning.getBngEquipmentHolder());
        $(BNG_DOWNLINK_CARD_PORT_INPUT_LOCATOR).click();
        $(BNG_DOWNLINK_CARD_PORT_INPUT_LOCATOR).val(oltCommissioning.getBngDownlinkCardPort());
        $(LSZ_SELECT_LOCATOR).click();
        $(LSZ_VALUE_LOCATOR).click();
        $(ORDER_NUMBER_INPUT_LOCATOR).click();
        $(ORDER_NUMBER_INPUT_LOCATOR).val(oltCommissioning.getOrderNumber());
        $(COMMISSIONING_START_BUTTON_LOCATOR).click();
    }
}
