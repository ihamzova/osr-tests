package com.tsystems.tm.acc.ta.pages.osr.oltmaintenance;

import com.codeborne.selenide.SelenideElement;
import com.tsystems.tm.acc.ta.pages.osr.oltmaintenance.SelenideExceptions.ErrorMessageFound;
import com.tsystems.tm.acc.ta.pages.osr.oltmaintenance.SelenideExceptions.MessagesNotFound;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.WebDriverRunner.url;
import static com.tsystems.tm.acc.ta.util.Assert.assertContains;

@Slf4j
public class DiscoveryStartenPage {
    private static final String ENDPOINT = "/commissioning";

    private static final By HEADER_LOCATOR = byXpath("//h1//strong[contains(text(),'OLT')]");
    private static final By KLS_ID_LOCATOR = byXpath("//input[@id='klsID']");

    private static final By UPLINK_OLT_SLOT_NUM_LOCATOR = byXpath("//input[@id='oltSlot']");
    private static final By UPLINK_OLT_PORT_LOCATOR = byXpath("//input[@id='oltPortNumber']");
    private static final By DOWNLINK_BNG_ENDSZ_LOCATOR = byXpath("//input[@id='bngEndSz']");
    private static final By DOWNLINK_BNG_SLOT_NUM_LOCATOR = byXpath("//input[@id='bngSlot']");
    private static final By DOWNLINK_BNG_PORT_LOCATOR = byXpath("//input[@id='bngPortNumber']");

    private static final By LCZ_LOCATOR = byXpath("//select[@name='lsz']");
    private static final By ORDER_NUMBER_STRING_LOCATOR = byXpath("//input[@id='orderNumber']");

    private static final By START_BUTTON_LOCATOR = byXpath("//button[@type='button']");

    private static final By NOTIFICATION_MESSAGES_LOCATOR = byXpath("//app-olt-commissioning");
    private static final String COMMISSIONING_MESSAGE_TEMPLATE = "//app-olt-commissioning//div[contains(text(), '%s')]";

    private static final By GO_TO_DEVICE_LOCATOR = byXpath("//a[@type='button']");

    private static final String OLT_DEVICE_CREATED_STRING = "OLT discovery finished";
    private static final String UPLINK_CREATED_STRING = "Uplink created";
    private static final String ANCP_SESSION_CONFIGURED_STRING = "ANCP configuration finished";
    private static final String ANCP_SESSION_CONFIGURATION_ENDED = "OLT commissioning finished";

    private static final String COMMISSIONING_ERROR_STRING = "Error while OLT commissioning";

    private static final int COMMISSIONING_TIMEOUT = 30 * 60 * 1000;

    @Step("Validate page")
    public void validate() {
        assertContains(url(), ENDPOINT);
        $(HEADER_LOCATOR).shouldBe(visible);
    }

    public void typeKLSId(String val) {
        $(KLS_ID_LOCATOR).val(val);
    }

    public void typeUplinkOltSlot(String slotNum) {
        $(UPLINK_OLT_SLOT_NUM_LOCATOR).val(slotNum);
    }

    public void typeUplinkOltPort(String val) {
        $(UPLINK_OLT_PORT_LOCATOR).val(val);
    }

    public void typeDownlinkBNGENDSZ(String val) {
        $(DOWNLINK_BNG_ENDSZ_LOCATOR).val(val);
    }

    public void typeDownlinkBNGSlot(String slotNum) {
        $(DOWNLINK_BNG_SLOT_NUM_LOCATOR).val(slotNum);
    }

    public void typeDownlinkBNGPort(String val) {
        $(DOWNLINK_BNG_PORT_LOCATOR).val(val);
    }

    public void selectLSZ(String val) {
        $(LCZ_LOCATOR).selectOption(val);
    }

    public void typeOrdungNummer(String val) {
        $(ORDER_NUMBER_STRING_LOCATOR).val(val);
    }

    public void clickDiscoveryStartButton() {
        $(START_BUTTON_LOCATOR).click();
    }

    @Step("Wait Until Commissioning is done")
    public void waitUntilComissioningIsDone() {
        $(NOTIFICATION_MESSAGES_LOCATOR).shouldBe(visible);

        List<String> requiredSteps = Arrays.asList(OLT_DEVICE_CREATED_STRING, UPLINK_CREATED_STRING, ANCP_SESSION_CONFIGURED_STRING, ANCP_SESSION_CONFIGURATION_ENDED);
        List<String> errorMessages = Collections.singletonList(COMMISSIONING_ERROR_STRING);

        SelenideElement root = $(NOTIFICATION_MESSAGES_LOCATOR);

        Instant start = Instant.now();
        List<String> messages;
        do {
            sleep(20_000L);
            messages = root.findAll(byXpath(".//div[@class='notification-content']")).stream()
                    .map(WebElement::getText)
                    .collect(Collectors.toList());

            if (messages.stream().anyMatch(errorMessages::contains)) {
                throw new ErrorMessageFound(messages.stream()
                        .filter(errorMessages::contains)
                        .reduce("", (partialString, element) -> String.join(", ", partialString, element)));
            }

            if (Instant.now().toEpochMilli() - start.toEpochMilli() > COMMISSIONING_TIMEOUT) {
                throw new MessagesNotFound();
            }

        } while (!messages.containsAll(requiredSteps));

        /*
        VisibleOkOrErrorCondition visibleOkOrErrorCondition = new VisibleOkOrErrorCondition(Arrays.asList(requiredStepsArray), Arrays.asList(errorMessagesArray));

        Configuration.pollingInterval = 5_000L;
        try {
            $$(NOTIFICATION_MESSAGES_LOCATOR).shouldHave(visibleOkOrErrorCondition, COMMISSIONING_TIMEOUT_SEC * 1000);
        }
        finally {
            Configuration.pollingInterval = Long.parseLong(System.getProperty("selenide.pollingInterval", "200"));
        }*/
    }

    public SearchResultsPage pressGoToDevice() {
        $(GO_TO_DEVICE_LOCATOR).click();

        return new SearchResultsPage();
    }
}
