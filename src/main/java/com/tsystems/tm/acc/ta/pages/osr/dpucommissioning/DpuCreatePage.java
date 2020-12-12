package com.tsystems.tm.acc.ta.pages.osr.dpucommissioning;

import com.codeborne.selenide.Condition;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;

public class DpuCreatePage {

    public static final String APP = "olt-resource-inventory-ui";
    public static final String ENDPOINT = "/deviceeditor";

    private static final Integer WAIT_TIME_FOR_BUTTON_ENABLED = 1_000;

    public static final By DPU_SERIALNUMBER_INPUT_LOCATOR = byQaData("input-dpuSerialNumber");
    public static final By DPU_KLS_ID_SEARCH_INPUT_LOCATOR = byQaData("klsidsearch_input");
    public static final By DPU_KLS_ID_SEARCH_START_LOCATOR = byQaData("klsidsearch_start");
    public static final By FIBERONLOCATION_OPTION_0 = byQaData("fiberonlocation_option_0");
    public static final By FIBERONLOCATION_OPTION_1 = byQaData("fiberonlocation_option_1");
    public static final By DPU_DEVICE_CREATE_BUTTON_LOCATOR = byQaData("dpu_create");
    public static final By DPU_DEVICE_BACK_TO_DETAILS_BUTTON_LOCATOR = byQaData("dpu_details");

    @Step("Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

    @Step("Input parameters for DPU creation")
    public DpuCreatePage startDpuCreation(DpuDevice dpuDevice) {
        $(DPU_SERIALNUMBER_INPUT_LOCATOR).click();
        $(DPU_SERIALNUMBER_INPUT_LOCATOR).val(dpuDevice.getSeriennummer());
        $(DPU_KLS_ID_SEARCH_INPUT_LOCATOR).click();
        $(DPU_KLS_ID_SEARCH_INPUT_LOCATOR).val(dpuDevice.getKlsId());
        $(DPU_KLS_ID_SEARCH_START_LOCATOR).click();
        $(FIBERONLOCATION_OPTION_0).click();
        $(DPU_DEVICE_CREATE_BUTTON_LOCATOR).waitUntil(enabled, WAIT_TIME_FOR_BUTTON_ENABLED).click();
        return this;
    }

    @Step("Go back to DPU Info page")
    public DpuCreatePage openDpuInfoPage() {
        $(DPU_DEVICE_BACK_TO_DETAILS_BUTTON_LOCATOR).click();
        return this;
    }
}
