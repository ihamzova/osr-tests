package com.tsystems.tm.acc.ta.pages.osr.oltcommissioning;

import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import com.tsystems.tm.acc.ta.pages.osr.dpucommissioning.DpuEditPage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.appears;
import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;


public class DeleteDevicePage {

    public static final String APP = "olt-resource-inventory-ui";
    public static final String ENDPOINT =  "/" + APP + "/detail";

    public static final By DELETE_DEVICE_FROM_DELETE_DEVICE_PAGE_BUTTON_LOCATOR = byQaData("device_delete_perform");


    @Step("Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

    @Step("Delete device")

    public DeleteDevicePage DeleteOltDevice() {
        $(DELETE_DEVICE_FROM_DELETE_DEVICE_PAGE_BUTTON_LOCATOR).click();
        return this;
    }

}
