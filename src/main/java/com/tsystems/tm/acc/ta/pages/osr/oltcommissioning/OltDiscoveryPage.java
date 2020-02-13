package com.tsystems.tm.acc.ta.pages.osr.oltcommissioning;

import com.codeborne.selenide.Condition;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;

@Slf4j
public class OltDiscoveryPage {

    public static final String APP = "olt-resource-inventory-ui";
    public static final String ENDPOINT = "/oltdiscovery";

    public static final By OLT_DISCOVERY_PROCESS_START_BUTTON_LOCATOR = byQaData("button-discovery-process-start");
    public static final By AUTO_UPDATE_BUTTON_LOCATOR = byQaData("button-automatic-update");
    public static final By UPDATE_HISTORY_BUTTON_LOCATOR = byQaData("button-update-history");
    public static final By DISCOVERY_RESULT_SHOW_BUTTON_LOCATOR = byQaData("button-discovery-result-show");
    public static final By DISCOVERY_RESULT_SAVE_BUTTON_LOCATOR = byQaData("button-save-changes");
    public static final By OLT_SEARCH_PAGE_TAB_LOCATOR = byQaData("a-olt-search-tab");

    private static final Integer TIMEOUT_FOR_OLT_DISCOVERY = 60_000;
    private static final Integer TIMEOUT_FOR_ELEMENT_APPEARS = 60_000;

    @Step("Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

    public int getSuccessfullyDiscoveriesCount() {
        $(OLT_DISCOVERY_PROCESS_START_BUTTON_LOCATOR).waitUntil(Condition.appears, TIMEOUT_FOR_OLT_DISCOVERY);
        return $$(DISCOVERY_RESULT_SHOW_BUTTON_LOCATOR).size();
    }

    @Step("Make olt discovery")
    public OltDiscoveryPage makeOltDiscovery() {
        $(OLT_DISCOVERY_PROCESS_START_BUTTON_LOCATOR).click();
        $(AUTO_UPDATE_BUTTON_LOCATOR).waitUntil(Condition.appears, TIMEOUT_FOR_ELEMENT_APPEARS).click();

        $(OLT_DISCOVERY_PROCESS_START_BUTTON_LOCATOR).waitWhile(Condition.disabled, TIMEOUT_FOR_OLT_DISCOVERY);

        $(UPDATE_HISTORY_BUTTON_LOCATOR).click();
        return this;
    }

    @Step("Show discovery result and save changes")
    public OltDiscoveryPage saveDiscoveryResults() {
        $$(DISCOVERY_RESULT_SHOW_BUTTON_LOCATOR).last().click();
        $(DISCOVERY_RESULT_SAVE_BUTTON_LOCATOR).click();
        return this;
    }

    @Step("Open olt search page")
    public OltSearchPage openOltSearchPage() {
        $(OLT_SEARCH_PAGE_TAB_LOCATOR).click();
        return new OltSearchPage();
    }
}
