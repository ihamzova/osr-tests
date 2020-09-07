package com.tsystems.tm.acc.ta.pages.osr.dpucommissioning;

import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;

public class DpuCreatePage {

    public static final String APP = "olt-resource-inventory-ui";
    public static final String ENDPOINT = "/deviceeditor";
    public static final By DPU_KLS_ID_INPUT_LOCATOR = byQaData("input-dpuKlsId");
    public static final By DPU_SERIALNUMBER_INPUT_LOCATOR = byQaData("input-dpuSerialNumber");

    public static final By DPU_WE_INPUT_LOCATOR = byQaData("input-dpuPonConnectionWe");
    public static final By DPU_GE_INPUT_LOCATOR = byQaData("input-dpuPonConnectionGe");
    public static final By DPU_DP_ID_INPUT_LOCATOR = byQaData("input-dpuDistributionPointId");

    public static final By DPU_OLT_ENDSZ_INPUT_LOCATOR = byQaData("input-oltPonPortEndsz");
    public static final By DPU_OLT_SLOT_INPUT_LOCATOR = byQaData("input-oltPonSlotNumber");
    public static final By DPU_OLT_PORT_INPUT_LOCATOR = byQaData("input-oltPonPortNumber");
    public static final By DPU_DEVICE_CREATE_BUTTON_LOCATOR = byXpath("/html/body/app-root/div/div/app-device-editor/app-dpu-editor/form/div[2]/div/div[2]/button"); //workaround as qa-data is in implementation
    public static final By DPU_DEVICE_BACK_TO_DETAILS_BUTTON_LOCATOR = byXpath("/html/body/app-root/div/div/app-device-editor/app-dpu-editor/div[2]/div/div[2]/button"); //workaround as qa-data is in implementation

    @Step("Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

    @Step("Input params and start DPU creation")
    public DpuCreatePage startDpuCreation(DpuDevice dpuDevice) {
        $(DPU_KLS_ID_INPUT_LOCATOR).click();
        $(DPU_KLS_ID_INPUT_LOCATOR).val(dpuDevice.getKlsId());
        $(DPU_SERIALNUMBER_INPUT_LOCATOR).click();
        $(DPU_SERIALNUMBER_INPUT_LOCATOR).val(dpuDevice.getSeriennummer());

        $(DPU_WE_INPUT_LOCATOR).click();
        $(DPU_WE_INPUT_LOCATOR).val(dpuDevice.getPonConnectionWe());
        $(DPU_GE_INPUT_LOCATOR).click();
        $(DPU_GE_INPUT_LOCATOR).val(dpuDevice.getPonConnectionGe());
        $(DPU_DP_ID_INPUT_LOCATOR).click();
        $(DPU_DP_ID_INPUT_LOCATOR).val(dpuDevice.getDpuDistributionPointId());

        $(DPU_OLT_ENDSZ_INPUT_LOCATOR).click();
        $(DPU_OLT_ENDSZ_INPUT_LOCATOR).val(dpuDevice.getOltEndsz());
        $(DPU_OLT_SLOT_INPUT_LOCATOR).click();
        $(DPU_OLT_SLOT_INPUT_LOCATOR).val(dpuDevice.getOltGponSlot());
        $(DPU_OLT_PORT_INPUT_LOCATOR).click();
        $(DPU_OLT_PORT_INPUT_LOCATOR).val(dpuDevice.getOltGponPort());
        $(DPU_DEVICE_CREATE_BUTTON_LOCATOR).click();
        return this;
    }

    @Step("Go back on the DPU Info page")
    public DpuCreatePage openDpuInfoPage() {
        $(DPU_DEVICE_BACK_TO_DETAILS_BUTTON_LOCATOR).click();
        return this;
    }
}
