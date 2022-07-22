package com.tsystems.tm.acc.ta.pages.osr.oltcommissioning;

import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.tsystems.tm.acc.ta.util.AsyncAssert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;
import static org.openqa.selenium.By.cssSelector;
import static org.testng.Assert.fail;

@Slf4j
public class OltCommissioningPage {

    public static final String APP = "olt-resource-inventory-ui";
    public static final String ENDPOINT =  "/" + APP + "/commissioning";

    public static final By COMMISSIONING_START_BUTTON_LOCATOR = byQaData("button-start-commissioning");
    public static final By CARDS_DETAILS_TAB_LOCATOR = byQaData("a-cards-view");
    private static final By ERROR_SECTION_LOCATOR = cssSelector("div[class='ui icon message negative']");

    @Step("Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP);
        assertUrlContainsWithTimeout(ENDPOINT);
    }

    @Step("Start OLT commissioning and wait for result")
    public OltCommissioningPage startOltCommissioning(OltDevice olt, Integer timeout) {
        $(COMMISSIONING_START_BUTTON_LOCATOR).click();
        Instant start = Instant.now();
        while (Instant.now().minus(timeout, ChronoUnit.MILLIS).isBefore(start)) {
            if ($$(CARDS_DETAILS_TAB_LOCATOR).size() > 0 && $(CARDS_DETAILS_TAB_LOCATOR).isDisplayed()) break;
            if ($$(ERROR_SECTION_LOCATOR).size() > 0 && $(ERROR_SECTION_LOCATOR).isDisplayed()) {
                fail("Error happened during OLT commissioning");
            }
            sleep(5 * 1000);
        }
        $(CARDS_DETAILS_TAB_LOCATOR).shouldBe(visible);
        return this;
    }
}
