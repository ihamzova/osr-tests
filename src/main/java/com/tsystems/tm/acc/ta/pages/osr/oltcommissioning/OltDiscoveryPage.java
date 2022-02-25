package com.tsystems.tm.acc.ta.pages.osr.oltcommissioning;

import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.testng.Assert;

import java.time.Duration;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Selenide.*;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.DISCOVERY_RESULT_SUCCESSFUL;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;

@Slf4j
public class OltDiscoveryPage {

    public static final String APP = "olt-resource-inventory-ui";
    public static final String ENDPOINT =  "/" + APP + "/oltdiscovery";

    public static final By OLT_DISCOVERY_PROCESS_START_BUTTON_LOCATOR = byQaData("button-discovery-process-start");
    public static final By UPDATE_HISTORY_BUTTON_LOCATOR = byQaData("button-update-history");
    public static final By DISCOVERY_RESULT_SHOW_BUTTON_LOCATOR = byQaData("button-discovery-result-show");
    public static final By DISCOVERY_RESULT_SAVE_BUTTON_LOCATOR = byQaData("button-save-changes");
    public static final By OLT_SEARCH_PAGE_TAB_LOCATOR = byQaData("a-olt-search-tab");

    private static final Integer TIMEOUT_FOR_OLT_DISCOVERY = 10_000;
    private static final Integer WAITING_TIME_FOR_DISCOVERY_HISTORY_UPDATE = 25_000;

    @Step("Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

    @Step("Make olt discovery")
    public OltDiscoveryPage makeOltDiscovery() {
        $(OLT_DISCOVERY_PROCESS_START_BUTTON_LOCATOR).click();
         // sleep(WAITING_TIME_FOR_DISCOVERY_HISTORY_UPDATE);
        $(OLT_DISCOVERY_PROCESS_START_BUTTON_LOCATOR).should(appear,Duration.ofMillis(WAITING_TIME_FOR_DISCOVERY_HISTORY_UPDATE));
        $(UPDATE_HISTORY_BUTTON_LOCATOR).click();

        $(OLT_DISCOVERY_PROCESS_START_BUTTON_LOCATOR).shouldNotBe(disabled, Duration.ofMillis(TIMEOUT_FOR_OLT_DISCOVERY));
        //Check if the last discovery was successful
        String resultText = $$(DISCOVERY_RESULT_SHOW_BUTTON_LOCATOR).first().getText();
        if(resultText.equals("Report aufrufen")) {  // Backward compatibility will be removed later
            resultText = DISCOVERY_RESULT_SUCCESSFUL;
        }
        Assert.assertEquals(resultText, DISCOVERY_RESULT_SUCCESSFUL,  "Discovery Result Text missmatch");
        return this;
    }

    @Step("Show discovery result and save changes")
    public OltDiscoveryPage saveDiscoveryResults() {
        $$(DISCOVERY_RESULT_SHOW_BUTTON_LOCATOR).first().click();
        $(DISCOVERY_RESULT_SAVE_BUTTON_LOCATOR).click();
        sleep(10000);
        return this;
    }

    @Step("Open olt search page")
    public OltSearchPage openOltSearchPage() {
        $(OLT_SEARCH_PAGE_TAB_LOCATOR).click();
        return new OltSearchPage();
    }
}
