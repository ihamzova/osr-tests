package com.tsystems.tm.acc.ta.pages.osr.oltresourceinventory;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import com.tsystems.tm.acc.ta.pages.osr.oltmaintenance.SelenideExceptions.ErrorMessageFound;
import com.tsystems.tm.acc.ta.pages.osr.oltmaintenance.SelenideExceptions.MessagesNotFound;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;


@Slf4j
public class OltRiCommissioningPage {
    private static final String APP = "olt-resource-inventory-ui";
    private static final String ENDPOINT = "/commissioning";

    private static final By KLSID_LOCATOR = byXpath("//input[@name='oltklsidtxt']");
    private static final By UPLINK_SLOT_LOCATOR = byXpath("//input[@name='oltslotnumbertxt']");
    private static final By UPLINK_PORT_LOCATOR = byXpath("//input[@name='oltportnumbertxt']");
    private static final By DOWNLINK_ENDSZ_LOCATOR = byXpath("//input[normalize-space(@name)='bngendsztxt']");
    private static final By DOWNLINK_SLOT_LOCATOR = byXpath("//input[@name='bngslotnumbertxt']");
    private static final By DOWNLINK_PORT_LOCATOR = byXpath("//input[@name='bngportnumbertxt']");
    private static final By LSZ_DROPDOWN_LOCATOR = byXpath("//app-sui-drop-down[@name='lsz']");
    private static final By ORDNUNGS_NUMBER_LOCATOR = byXpath("//input[@name='ordernumbertxt']");
    private static final By START_AUTO_OLT_COMMISSIONING_BUTTON_LOCATOR = byXpath("//button[normalize-space(text())='automatisierte OLT-Inbetriebnahme starten']");

    private static final String KONFIGURATION_INITIIEREN = "Konfiguration initiieren";
    private static final String OLT_ANLEGEN = "OLT anlegen";
    private static final String KLSID_UBERNEHMEN = "KLS-ID übernehmen";
    private static final String OLT_DISCOVERY_DURCHFUHREN = "OLT-Discovery durchführen";
    private static final String HOME_IDS_ERZEUGEN = "HOME-ID's erzeugen";
    private static final String LINE_IDS_ERZEUGEN = "Line-ID's erzeugen";
    private static final String UPLINK_KONFIGURATION = "Uplink Konfiguration";
    private static final String ANCP_SESSION_KONFIGURATION = "ANCP-Session Konfiguration";
    private static final String ACCESS_LINES_ERZEUGEN = "Access-Lines erzeugen";

    private static final String DER_OLT_WIRD_IM_INVNENTAR_ANGELEGT = "Der OLT wird im Invnentar angelegt";
    private static final String DIE_KLSID_WIRD_UBERNOMMEN = "Die KLS-ID wird übernommen";
    private static final String OLT_MITTELS_LIFE_ABFRAGE_ERMITTELN = "OLT mittels Life-Abfrage ermitteln";
    private static final String DIE_HOME_IDS_WERDEN_ERSTELLT_UND_ZUGEWIESEN = "Die Home-ID's werden erstellt und zugewiesen";
    private static final String DIE_LINE_IDS_WERDEN_ERSTELLT_UND_ZUGEWIESEN = "Die Line-ID's werden erstellt und zugewiesen";
    private static final String DER_UPLINK_WIRD_KONFIGURIERT = "Der Uplink wird konfiguriert";
    private static final String DIE_ANCP_SESSION_WIRD_KONFIGURIERT = "Die ANCP-Session wird konfiguriert";
    private static final String ACCESS_LINES_WIRD_ERZEUGEN = "Access-Lines werden erstellt";

    private static final String COMMISSIONING_ERROR_STRING = "Error while OLT commissioning";

    private static final By NOTIFICATION_MESSAGES_LOCATOR = byXpath("//app-commissioning");

    private static final int COMMISSIONING_TIMEOUT = 30 * 60 * 1000;

    @Step("Validate top level page")
    public void validate() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

    @Step("Type KLS-ID")
    public void typeKls(String value) {
        $(KLSID_LOCATOR).val(value);
    }

    @Step("Type OLT Slot-Nummer")
    public void typeUplinkSlot(String value) {
        $(UPLINK_SLOT_LOCATOR).val(value);
    }

    @Step("Type OLT Port-Nummer")
    public void typeUplinkPort(String value) {
        $(UPLINK_PORT_LOCATOR).val(value);
    }

    @Step("Type BNG EndSz")
    public void typeDownlinkBNGENDSZ(String value) {
        $(DOWNLINK_ENDSZ_LOCATOR).val(value);
    }

    @Step("Type BNG Equipmentholder")
    public void typeDownlinkBNGSlot(String value) {
        $(DOWNLINK_SLOT_LOCATOR).val(value);
    }

    @Step("Type BNG Downlink-Karten Port")
    public void typeDownlinkBNGPort(String value) {
        $(DOWNLINK_PORT_LOCATOR).val(value);
    }

    @Step("Select LSZ")
    public void selectLSZ(String value) {
        $(LSZ_DROPDOWN_LOCATOR).click();
        $(LSZ_DROPDOWN_LOCATOR).find(byXpath(String.format(".//span[text()='%s']", value))).click();
    }

    @Step("Type Ordnungsnummer")
    public void typeOrdungNummer(String value) {
        $(ORDNUNGS_NUMBER_LOCATOR).val(value);
    }

    @Step("Click \"automatisierte OLT-Inbetriebnahme starten\" button")
    public void clickStartAutoOltCommissioningButton() {
        $(START_AUTO_OLT_COMMISSIONING_BUTTON_LOCATOR).click();
    }

    @Step("Wait Until Commissioning is done")
    public OltRiOltDetailPage waitUntilComissioningIsDone() {
        $(NOTIFICATION_MESSAGES_LOCATOR).shouldBe(visible);
        SelenideElement root = $(NOTIFICATION_MESSAGES_LOCATOR);

        List<String> requiredSteps = Arrays.asList(
                KONFIGURATION_INITIIEREN,
                OLT_ANLEGEN,
                KLSID_UBERNEHMEN,
                OLT_DISCOVERY_DURCHFUHREN,
                HOME_IDS_ERZEUGEN,
                LINE_IDS_ERZEUGEN,
                UPLINK_KONFIGURATION,
                ANCP_SESSION_KONFIGURATION,
                ACCESS_LINES_ERZEUGEN);
        List<String> requiredMessages = Arrays.asList(
                KONFIGURATION_INITIIEREN,
                DER_OLT_WIRD_IM_INVNENTAR_ANGELEGT,
                DIE_KLSID_WIRD_UBERNOMMEN,
                OLT_MITTELS_LIFE_ABFRAGE_ERMITTELN,
                DIE_HOME_IDS_WERDEN_ERSTELLT_UND_ZUGEWIESEN,
                DIE_LINE_IDS_WERDEN_ERSTELLT_UND_ZUGEWIESEN,
                DER_UPLINK_WIRD_KONFIGURIERT,
                DIE_ANCP_SESSION_WIRD_KONFIGURIERT,
                ACCESS_LINES_WIRD_ERZEUGEN);

        List<String> errorMessages = Collections.singletonList(COMMISSIONING_ERROR_STRING);

        Instant start = Instant.now();
        List<String> steps;
        List<String> messages;
        int greenCircleCount = 0;

        do {
            steps = root.findAll(byXpath(".//div[@class='title']")).stream()
                    .map(WebElement::getText)
                    .collect(Collectors.toList());
            messages = root.findAll(byXpath(".//div[@class='description']")).stream()
                    .map(WebElement::getText)
                    .collect(Collectors.toList());

            greenCircleCount = (int) root.findAll
                    (byXpath(".//i[@class='small green check circle icon']")).stream()
                    .count();
            if (messages.stream().anyMatch(errorMessages::contains)) {
                throw new ErrorMessageFound(messages.stream()
                        .filter(errorMessages::contains)
                        .reduce("", (partialString, element) -> String.join(", ", partialString, element)));
            }
            if (Instant.now().toEpochMilli() - start.toEpochMilli() > COMMISSIONING_TIMEOUT) {
                throw new MessagesNotFound();
            }
        } while (!(steps.containsAll(requiredSteps) && messages.containsAll(requiredMessages) && greenCircleCount == requiredSteps.size()-1));

        root.find(byXpath(".//div[@class='step active']")).shouldNot(exist, Duration.ofMillis(COMMISSIONING_TIMEOUT));


        return new OltRiOltDetailPage();
    }
}
