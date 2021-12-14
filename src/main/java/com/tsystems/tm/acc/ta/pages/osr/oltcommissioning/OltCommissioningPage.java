package com.tsystems.tm.acc.ta.pages.osr.oltcommissioning;

import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;
import static org.openqa.selenium.By.cssSelector;
import static org.testng.Assert.fail;

@Slf4j
public class OltCommissioningPage {

    public static final String APP = "olt-resource-inventory-ui";
    public static final String ENDPOINT =  "/" + APP + "/commissioning";

    public static final By OLT_KLS_ID_INPUT_LOCATOR = byQaData("input-oltklsidtxt");
    public static final By OLT_SLOT_NUMBER_INPUT_LOCATOR = byQaData("input-oltslotnumbertxt");
    public static final By OLT_PORT_NUMBER_INPUT_LOCATOR = byQaData("input-oltportnumbertxt");
    public static final By OLT_BNG_ENDSZ_INPUT_LOCATOR = byQaData("input-bngendsztxt");
    public static final By BNG_EQUIPMENTHOLDER_INPUT_LOCATOR = byQaData("input-bngslotnumbertxt");
    public static final By BNG_DOWNLINK_CARD_PORT_INPUT_LOCATOR = byQaData("input-bngportnumbertxt");
    public static final By LSZ_SELECT_LOCATOR = byQaData("div-lsz");
    public String LSZ_VALUE_LOCATOR = "//*[@qa-data='div-%s']"; //div-4C1
    public static final By ORDER_NUMBER_INPUT_LOCATOR = byQaData("input-ordernumbertxt");
    public static final By COMMISSIONING_START_BUTTON_LOCATOR = byQaData("button-start-commissioning");
    public static final By CARDS_DETAILS_TAB_LOCATOR = byQaData("a-cards-view");
    private static final By ERROR_SECTION_LOCATOR = cssSelector("div[class='ui icon message negative']");

    @Step("Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

    @Step("Start OLT commissioning")
    public OltCommissioningPage startOltCommissioning(OltDevice olt, Integer timeout) {
        $(COMMISSIONING_START_BUTTON_LOCATOR).click();
        Instant start = Instant.now();
        while (Instant.now().minus(timeout, ChronoUnit.MILLIS).isBefore(start)) {
            if ($$(CARDS_DETAILS_TAB_LOCATOR).size() > 0 && $(CARDS_DETAILS_TAB_LOCATOR).isDisplayed()) break;
            if ($$(ERROR_SECTION_LOCATOR).size() > 0 && $(ERROR_SECTION_LOCATOR).isDisplayed()) {
                fail("Error happened during OLT commissioning");
            }
            sleep(30 * 1000);
        }
        $(CARDS_DETAILS_TAB_LOCATOR).shouldBe(visible);
        return this;
    }
}
