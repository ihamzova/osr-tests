package com.tsystems.tm.acc.ta.pages.osr.dpucommissioning;

import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;

@Slf4j
public class DpuCreatePage {

    public static final String APP = "olt-resource-inventory-ui";
    public static final String ENDPOINT = "/deviceeditor";

    private static final Integer WAIT_TIME_FOR_BUTTON_ENABLED = 2_000;
    private static final Integer ANZ_OF_DPU_TYPES = 6;

    public static final By DPU_OPTION_LOCATER = byQaData("dpuoption");
    public String dpuOptionLocatorString = "dpuoption_option_%d";

    public static final By DPU_SERIALNUMBER_INPUT_LOCATOR = byQaData("input-dpuSerialNumber");
    public static final By DPU_KLS_ID_SEARCH_INPUT_LOCATOR = byQaData("klsidsearch_input");
    public static final By DPU_KLS_ID_SEARCH_START_LOCATOR = byQaData("klsidsearch_start");
    public static final By FIBERONLOCATION_OPTION_0 = byQaData("fiberonlocation_option_0");
    public static final By DPU_DEMANDS_OPTION_0 = byQaData("dpudemand_option_0");
    public static final By DPU_DEVICE_CREATE_BUTTON_LOCATOR = byQaData("dpu_create");
    public static final By DPU_DEVICE_BACK_TO_DETAILS_BUTTON_LOCATOR = byQaData("dpu_details");

    @Step("Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

    @Step("Input parameters for DPU creation")
    public DpuCreatePage startDpuCreation(DpuDevice dpuDevice) {

        // If the page is opened, the DPU devices are queried from the material-catalog. The DPU types are then entered in the selection list.
        try {
            Thread.sleep(WAIT_TIME_FOR_BUTTON_ENABLED);
        } catch (Exception e) {
            log.error("Interrupted");
        }
        if($(DPU_OPTION_LOCATER).exists()) {  // Backward compatibility for UI without qa-tags with DPU device selection
            $(DPU_OPTION_LOCATER).click();
            for (int index = 0; index < ANZ_OF_DPU_TYPES; ++index) {
                if ($(byQaData(String.format(dpuOptionLocatorString, index))).exists()) {
                    log.info("startDpuCreation() check DPU entry {} ", $(byQaData(String.format(dpuOptionLocatorString, index))).getText());
                    if ($(byQaData(String.format(dpuOptionLocatorString, index))).getText().contains(dpuDevice.getBezeichnung())) {
                        log.info("startDpuCreation() choose DPU device {} ", $(byQaData(String.format(dpuOptionLocatorString, index))).getText());
                        $(byQaData(String.format(dpuOptionLocatorString, index))).click();
                    }
                }
            }
        }

        $(DPU_SERIALNUMBER_INPUT_LOCATOR).click();
        $(DPU_SERIALNUMBER_INPUT_LOCATOR).val(dpuDevice.getSeriennummer());
        $(DPU_KLS_ID_SEARCH_INPUT_LOCATOR).click();
        $(DPU_KLS_ID_SEARCH_INPUT_LOCATOR).val(dpuDevice.getKlsId());
        $(DPU_KLS_ID_SEARCH_START_LOCATOR).click();
        $(FIBERONLOCATION_OPTION_0).click();
        try {
            Thread.sleep(WAIT_TIME_FOR_BUTTON_ENABLED);
        } catch (Exception e) {
            log.error("Interrupted");
        }
        $(DPU_DEVICE_CREATE_BUTTON_LOCATOR).click();
        return this;
    }

    @Step("Input parameters for DPU creation")
    public DpuCreatePage startDpuCreationWithDpuDemand(DpuDevice dpuDevice) {

        // If the page is opened, the DPU devices are queried from the material-catalog. The DPU types are then entered in the selection list.
        try {
            Thread.sleep(WAIT_TIME_FOR_BUTTON_ENABLED);
        } catch (Exception e) {
            log.error("Interrupted");
        }
        if($(DPU_OPTION_LOCATER).exists()) {  // Backward compatibility for UI without qa-tags with DPU device selection
            $(DPU_OPTION_LOCATER).click();
            for (int index = 0; index < ANZ_OF_DPU_TYPES; ++index) {
                if ($(byQaData(String.format(dpuOptionLocatorString, index))).exists()) {
                    log.info("startDpuCreation() check DPU entry {} ", $(byQaData(String.format(dpuOptionLocatorString, index))).getText());
                    if ($(byQaData(String.format(dpuOptionLocatorString, index))).getText().contains(dpuDevice.getBezeichnung())) {
                        log.info("startDpuCreation() choose DPU device {} ", $(byQaData(String.format(dpuOptionLocatorString, index))).getText());
                        $(byQaData(String.format(dpuOptionLocatorString, index))).click();
                    }
                }
            }
        }

        $(DPU_KLS_ID_SEARCH_INPUT_LOCATOR).click();
        $(DPU_KLS_ID_SEARCH_INPUT_LOCATOR).val(dpuDevice.getKlsId());
        $(DPU_KLS_ID_SEARCH_START_LOCATOR).click();
        $(FIBERONLOCATION_OPTION_0).click();
        if (!$(DPU_DEMANDS_OPTION_0).isSelected())
            $(DPU_DEMANDS_OPTION_0).click();
        $(DPU_SERIALNUMBER_INPUT_LOCATOR).click();
        $(DPU_SERIALNUMBER_INPUT_LOCATOR).val(dpuDevice.getSeriennummer());
        try {
            Thread.sleep(WAIT_TIME_FOR_BUTTON_ENABLED);
        } catch (Exception e) {
            log.error("Interrupted");
        }
        $(DPU_DEVICE_CREATE_BUTTON_LOCATOR).click();
        return this;
    }

    @Step("Go back to DPU Info page")
    public DpuCreatePage openDpuInfoPage() {
        $(DPU_DEVICE_BACK_TO_DETAILS_BUTTON_LOCATOR).click();
        return this;
    }
}
