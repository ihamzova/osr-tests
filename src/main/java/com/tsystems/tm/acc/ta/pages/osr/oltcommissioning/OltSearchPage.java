package com.tsystems.tm.acc.ta.pages.osr.oltcommissioning;

import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import com.tsystems.tm.acc.ta.pages.osr.dpucommissioning.DpuCreatePage;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.net.URL;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;

@Slf4j
public class OltSearchPage {

    public static final String APP = "olt-resource-inventory-ui";
    public static final String ENDPOINT = "/" + APP + "/search";

    public static final By OLT_SEARCH_TYPE_SELECT_LOCATOR = byQaData("div-searchtype");
    public static final By ENDSZ_SEARCH_TYPE_VALUE = byQaData("div-searchtype_option_0");

    public static final By OLT_AKZ_INPUT_LOCATOR = byQaData("input-akz");
    public static final By OLT_ONKZ_INPUT_LOCATOR = byQaData("input-nkz");
    public static final By OLT_VKZ_INPUT_LOCATOR = byQaData("input-vkz");
    public static final By OLT_FSZ_INPUT_LOCATOR = byQaData("input-fsz");
    public static final By SEARCH_BUTTON_LOCATOR = byQaData("button-search");
    public static final By AUTO_OLT_COMMISSIONING_BUTTON_LOCATOR = byQaData("button-auto-commissioning");
    public static final By MANUAL_OLT_COMMISSIONING_BUTTON_LOCATOR = byQaData("button-manual-commissioning");
    public static final By MANUAL_DPU_COMMISSIONING_BUTTON_LOCATOR = byQaData("button-manual-dpu-commissioning");

    @Step("Open OLT-Search page")
    public static OltSearchPage openSearchPage() {
        URL url = new GigabitUrlBuilder(APP).withEndpoint(ENDPOINT).build();
        log.info("Opening url " + url.toString());
        return open(url, OltSearchPage.class);
    }

    @Step("Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

    @Step("Search not existing OLT by parameters")
    public OltSearchPage searchNotDiscoveredByParameters(OltDevice oltDevice) {
        inputOltParameters(oltDevice);
        $(SEARCH_BUTTON_LOCATOR).click();
        return this;
    }

    @Step("Search not existing Device by EndSz")
    public OltSearchPage searchNotDiscoveredByEndSz(String endSz) {
        inputDeviceParameters(endSz);
        $(SEARCH_BUTTON_LOCATOR).click();
        return this;
    }

    @Step("Search existing Device by EndSz")
    public OltSearchPage searchDiscoveredByEndSz(String endSz) {
        inputDeviceParameters(endSz);
        $(SEARCH_BUTTON_LOCATOR).click();
        return this;
    }

    @Step("Search existing OLT by parameters")
    public OltDetailsPage searchDiscoveredOltByParameters(OltDevice oltDevice) {
        inputOltParameters(oltDevice);
        $(SEARCH_BUTTON_LOCATOR).click();
        return new OltDetailsPage();
    }

    @Step("Go to automatically commissioning page")
    public OltCommissioningPage pressAutoCommissionigButton() {
        $(AUTO_OLT_COMMISSIONING_BUTTON_LOCATOR).click();
        return new OltCommissioningPage();
    }

    @Step("Start manually commissioning process and go to old discovery page")
    public OltDiscoveryPage pressManualCommissionigButton() {
        $(MANUAL_OLT_COMMISSIONING_BUTTON_LOCATOR).click();
        return new OltDiscoveryPage();
    }

    @Step("Start DPU commissioning process and go to dpu creation page")
    public DpuCreatePage pressCreateDpuButton() {
        $(MANUAL_DPU_COMMISSIONING_BUTTON_LOCATOR).click();
        return new DpuCreatePage();
    }

    private void inputOltParameters(OltDevice oltDevice) {
        String[] endSz = oltDevice.getVpsz().split("/");

        $(OLT_SEARCH_TYPE_SELECT_LOCATOR).click();
        $(ENDSZ_SEARCH_TYPE_VALUE).click();
        $(OLT_AKZ_INPUT_LOCATOR).click();
        $(OLT_AKZ_INPUT_LOCATOR).val(endSz[0]);
        $(OLT_ONKZ_INPUT_LOCATOR).click();
        $(OLT_ONKZ_INPUT_LOCATOR).val(endSz[1]);
        $(OLT_VKZ_INPUT_LOCATOR).click();
        $(OLT_VKZ_INPUT_LOCATOR).val(endSz[2]);
        $(OLT_FSZ_INPUT_LOCATOR).click();
        $(OLT_FSZ_INPUT_LOCATOR).val(oltDevice.getFsz());
    }

    private void inputDeviceParameters(String endSz) {
        String[] endSzn = endSz.split("/");

        $(OLT_SEARCH_TYPE_SELECT_LOCATOR).click();
        $(ENDSZ_SEARCH_TYPE_VALUE).click();
        $(OLT_AKZ_INPUT_LOCATOR).click();
        $(OLT_AKZ_INPUT_LOCATOR).val(endSzn[0]);
        $(OLT_ONKZ_INPUT_LOCATOR).click();
        $(OLT_ONKZ_INPUT_LOCATOR).val(endSzn[1]);
        $(OLT_VKZ_INPUT_LOCATOR).click();
        $(OLT_VKZ_INPUT_LOCATOR).val(endSzn[2]);
        $(OLT_FSZ_INPUT_LOCATOR).click();
        $(OLT_FSZ_INPUT_LOCATOR).val(endSzn[3]);
    }
}
