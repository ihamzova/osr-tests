package com.tsystems.tm.acc.ta.pages.osr.dpucommissioning;

import com.codeborne.selenide.Condition;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDetailsPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDiscoveryPage;
import com.tsystems.tm.acc.ta.util.Assert;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.appears;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;

public class DpuInfoPage {

    public static final String APP = "olt-resource-inventory-ui";
    public static final String ENDPOINT = "/detail";
    private static final Integer WAITING_TIME_FOR_DPU_COMMISSIONING_PROCESS_END = 15_000;

    public static final By DPU_PORTS_VIEW_TAB_LOCATOR = byQaData("a-ports-view");
    public static final By DPU_CONFIGURATION_VIEW_TAB_LOCATOR = byQaData("a-configuration-view");
    public static final By DPU_ACCESS_LINES_VIEW_TAB_LOCATOR = byQaData("a-access-lines-view");

    public static final By START_DPU_COMMISSIONING_BUTTON_LOCATOR = byXpath("/html/body/app-root/div/div/app-detail/app-device-detail/div/app-dpu-commissioning/div/div[3]/button[1]"); //workaround as qa-data is in implementation

    @Step("Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

    @Step("Start dpu commissioning")
    public DpuInfoPage startDpuCommissioning() {
        $(START_DPU_COMMISSIONING_BUTTON_LOCATOR).click();
        return this;
    }

    @Step("Open DPU Ports Tab")
    public DpuInfoPage openDpuPortsTab() {
        $(DPU_PORTS_VIEW_TAB_LOCATOR).click();
        return this;
    }

    @Step("Open DPU Konfiguration Tab. Check DPU Verschaltung")
    public DpuInfoPage openDpuConfiguraionTab() {
        $(DPU_CONFIGURATION_VIEW_TAB_LOCATOR).click();
        return this;
    }

    @Step("Open DPU Access-Lines Tab")
    public DpuInfoPage openDpuAccessLinesTab() {
        $(DPU_ACCESS_LINES_VIEW_TAB_LOCATOR).click();
        return this;
    }

}
