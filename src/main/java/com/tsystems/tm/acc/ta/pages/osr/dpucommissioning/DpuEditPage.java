package com.tsystems.tm.acc.ta.pages.osr.dpucommissioning;

import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.appears;
import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;

@Slf4j

public class DpuEditPage {

    public static final Integer MAX_LATENCY_FOR_ELEMENT_APPEARS = 5_000;

    public static final String APP = "olt-resource-inventory-ui";
    public static final String ENDPOINT = "/detail";

    public static final By EDIT_DPU_STATE_RADIO_BUTTON_LOCATOR = byQaData("lifecyclestate_operating");
    public static final By ACCEPT_CHANGES_BUTTON_LOCATOR = byQaData("lifecyclestate-accept-changes_edit");
    public static final By BACK_TO_DPU_INFO_PAGE_BUTTON_LOCATOR = byQaData("back-to-the-detailed-view_edit");


    @Step("Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

    @Step("Set DPU State in Betrieb")

    public DpuEditPage SetDpuState() {
        $(EDIT_DPU_STATE_RADIO_BUTTON_LOCATOR).click();
        $(ACCEPT_CHANGES_BUTTON_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        $(BACK_TO_DPU_INFO_PAGE_BUTTON_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        return this;

    }
}
