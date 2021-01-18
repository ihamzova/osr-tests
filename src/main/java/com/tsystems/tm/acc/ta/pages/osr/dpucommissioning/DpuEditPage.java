package com.tsystems.tm.acc.ta.pages.osr.dpucommissioning;

import com.codeborne.selenide.Condition;
import com.tsystems.tm.acc.ta.data.osr.enums.DevicePortLifeCycleStateUI;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.appears;
import static com.codeborne.selenide.Condition.exactTextCaseSensitive;
import static com.codeborne.selenide.Selectors.byCssSelector;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDetailsPage.MAX_LATENCY_FOR_ELEMENT_APPEARS;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;

@Slf4j

public class DpuEditPage {
    public static final String APP = "olt-resource-inventory-ui";
    public static final String ENDPOINT = "/detail";
    public static final Integer MAX_LATENCY_FOR_LIFECYCLE_CHANGE = 5000;
    private static final Integer TIMEOUT_FOR_DPU_COMMISSIONING = 10 * 60_000;

    public static final By EDIT_DPU_STATE_LOCATOR = byCssSelector(".field:nth-child(3) label");
    public static final By SAVE_CHANGES_BUTTON_LOCATOR = byCssSelector("div.ui.right.aligned.basic.segment> button.ui.primary.compact.button");
    public static final By BACK_TO_DPU_INFO_PAGE_BUTTON_LOCATOR = byQaData("cancel_edit");


    @Step("Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

    @Step("Set DPU State in Betrieb")

    public DpuEditPage SetDpuState() {
        $(EDIT_DPU_STATE_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        $(SAVE_CHANGES_BUTTON_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        $(BACK_TO_DPU_INFO_PAGE_BUTTON_LOCATOR).waitUntil(appears, MAX_LATENCY_FOR_ELEMENT_APPEARS).click();
        return this;

    }
}
