package com.tsystems.tm.acc.ta.ui.pages.oltcommissioning;

import com.tsystems.tm.acc.data.osr.models.oltcommissioning.OltCommissioning;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import lombok.extern.slf4j.Slf4j;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.net.URL;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;

@Slf4j
public class OltSearchPage {
    static final String APP = "olt-resource-inventory-ui";
    static final String ENDPOINT = "/search";

    private static final By OLT_SEARCH_TYPE_SELECT_LOCATOR = byQaData("sc-olt-search-type-select");
    private static final By OLT_SEARCH_TYPE_VALUE_LOCATOR = byQaData("sc-olt-search-type-value-1");
    private static final By OLT_AKZ_INPUT_LOCATOR = byQaData("sc-akz-input");
    private static final By OLT_ONKZ_INPUT_LOCATOR = byQaData("sc-onkz-input");
    private static final By OLT_VKZ_INPUT_LOCATOR = byQaData("sc-vkz-input");
    private static final By OLT_FSZ_INPUT_LOCATOR = byQaData("sc-fsz-input");
    private static final By SEARCH_BUTTON_LOCATOR = byQaData("sc-search-button");
    private static final By AUTO_OLT_COMMISSIONING_BUTTON_LOCATOR = byQaData("sc-auto-olt-commissioning-button");

    @Step("Open OLT-Search page")
    public static OltSearchPage openPage() {
        URL url = new OCUrlBuilder(APP).withEndpoint(ENDPOINT).build();
        log.info("Opening url " + url.toString());
        return open(url, OltSearchPage.class);
    }

    @Step("Validate Url")
    public void validate() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

    @Step("Search OLT by parameters parameters")
    public OltSearchPage searchOlt(OltCommissioning oltCommissioning) {
        $(OLT_SEARCH_TYPE_SELECT_LOCATOR).click();
        $(OLT_SEARCH_TYPE_VALUE_LOCATOR).click();
        $(OLT_AKZ_INPUT_LOCATOR).click();
        $(OLT_AKZ_INPUT_LOCATOR).val(oltCommissioning.getAkz());
        $(OLT_ONKZ_INPUT_LOCATOR).click();
        $(OLT_ONKZ_INPUT_LOCATOR).val(oltCommissioning.getOnkz());
        $(OLT_VKZ_INPUT_LOCATOR).click();
        $(OLT_VKZ_INPUT_LOCATOR).val(oltCommissioning.getVkz());
        $(OLT_FSZ_INPUT_LOCATOR).click();
        $(OLT_FSZ_INPUT_LOCATOR).val(oltCommissioning.getFsz());
        $(SEARCH_BUTTON_LOCATOR).click();
        return this;
    }

    @Step("Go to automatically commissioning page")
    public OltCommssioningPage searchOlt() {
        $(AUTO_OLT_COMMISSIONING_BUTTON_LOCATOR).click();
        return new OltCommssioningPage();
    }
}
