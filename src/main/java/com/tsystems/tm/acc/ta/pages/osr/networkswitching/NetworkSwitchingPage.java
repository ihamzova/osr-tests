package com.tsystems.tm.acc.ta.pages.osr.networkswitching;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import com.tsystems.tm.acc.ta.helpers.osr.logs.TimeoutBlock;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;

import java.net.URL;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;

@Slf4j
public class NetworkSwitchingPage {
    WebDriver driver;

    private static final String APP = "network-switching-ui";
    private static final String ENDPOINT = "/network-switching-ui/ne3/port-to-port";
    private static final long TIMEOUT = 300000;

    private static final By NE3_UMSCHALTUNG_TAB = byText("NE3 Umschaltung");
    private static final By NE2_UMSCHALTUNG_TAB = byText("NE2 Umschaltung");
    private static final By SLOT_TO_SLOT_PREPARATION_TAB = byQaData("slot-to-slot-tab");
    private static final By SEARCH_TAB = byText("Paket suchen");
    private static final By PAKETVERWALTUNG_TAB = byQaData("actions-tab");

    private static final By PORT_TO_PORT_PREPARE_BUTTON = byQaData("port-to-port-options-btn");
    private static final By GANZEN_PON_PORT_UMSCHALTEN = byText("Ganzen PON Port umsсhalten");
    private static final By HOMEIDS_MANUELLE_AUSWAHL = byText("HomeIDs manuelle Auswahl");
    private static final By VORBETEITUNG_STARTEN = byQaData("port-to-port-btn");
    private static final By CSV_IMPORT = byText("CSV Import");
    private static final By SHOW_DEVICE_BUTTON = byQaData("slot-to-slot-prepare-btn");
    private static final By ADD_ROW_BUTTON = byQaData("add-row-btn");
    private static final By DELETE_ROW_BUTTON = byQaData("delete-row-btn");
    private static final By SLOT_TO_SLOT_PREPARE_BUTTON = byQaData("slot-to-slot-btn");
    private static final By SEARCH_BUTTON = byQaData("search-btn");
    private static final By GET_PACKAGE_DATA_BUTTON = byQaData("get-info-btn");
    private static final By COMMIT_BUTTON = byQaData("commit-btn");
    private static final By EXECUTION_BUTTON = byQaData("execution-btn");
    private static final By ROLLBACK_BUTTON = byQaData("rollback-btn");
    private static final By COPY_PACKAGE_ID_BUTTON = byQaData("copy-btn");
    private static final By DEVICE_INFORMATION = byQaData("device-info-btn");

    private static final By SOURCE_ENDSZ_INPUT = byQaData("endsz-input");
    private static final By SOURCE_SLOT_INPUT = byQaData("slot-input");
    private static final By SOURCE_PORT_INPUT = byQaData("port-input");
    private static final By TARGET_ENDSZ_INPUT = byQaData("targetEndsz-input");
    private static final By TARGET_SLOT_INPUT = byQaData("targetSlot-input");
    private static final By TARGET_PORT_INPUT = byQaData("targetPort-input");

    private static final By SEARCH_BY_PACKAGE_ID_INPUT = byQaData("packageId-input");
    private static final By SEARCH_BY_ENDSZ_INPUT = byQaData("endsz-input");
    private static final By SEARCH_BY_SLOT_INPUT = byQaData("slot-input");
    private static final By SEARCH_BY_PORT_INPUT = byQaData("port-input");
    private static final By SOURCE_PORT1 = byQaData("sourcePort1-input");
    private static final By SOURCE_PORT2 = byQaData("sourcePort2-input");
    private static final By TARGET_PORT1 = byQaData("targetPort1-input");
    private static final By TARGET_PORT2 = byQaData("targetPort2-input");
    private static final By TARGET_SLOT1 = byQaData("targetSlot1-input");
    private static final By TARGET_SLOT2 = byQaData("targetSlot2-input");
    private static final By ADD_LINE_BTN = byQaData("add-row-btn");


    private static final By GET_PACKAGE_DATA_BY_PACKAGE_ID_INPUT = byQaData("packageId-input");

    private static final By HAUTINFO_TAB = byQaData("main-tab");
    private static final By AKTIONEN_TAB = byQaData("actions-tab");
    private static final By HOME_IDS_TAB = byQaData("home-ids-tab");
    private static final By PROCESS_INFO_TAB = byQaData("process-tab");

    private static final By ACTIONS_DROPDOWN = byQaData("actions-dropdown");

    private static final By NOTIFICATION = byXpath("//h2[@role = 'alert']");
    private static final By CLOSE_NOTIFICATION_BUTTON = byXpath("//*[@role='alert']/button");
    private static final By PACKAGE_POLLING_PHASE = byXpath("//*[@class='package-polling--phase");

    private static final By PACKAGE_STATUS = byXpath("//*[@class='package-info']");
    private static final By PACKAGE_ID_PREPARATION_TAB = byXpath("//*[@class='package-polling--id']//a");
    private static final By PACKAGE_ID_SEARCH_TAB = byXpath("//*[@class='p-element p-treetable-tbody']//a");

    private static final By HOMEIDS_SUBMIT_SECTION = byXpath("//*[@class='submit-section__homeids']");
    private static final By HOMEIDS = byClassName("submit-section__homeid");
    private static final By PORTDEPROVISIONING = byText("Portdeprovisionierung");

    @Step("Open Network Switching page")
    public static NetworkSwitchingPage openPage() {
        URL url = new OCUrlBuilder(APP).withEndpoint(ENDPOINT).build();
        log.info("Opening url " + url.toString());
        return open(url, NetworkSwitchingPage.class);
    }

    @Step("Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

    @Step("Start preparation phase")
    public NetworkSwitchingPage startFullPortPreparation(PortProvisioning sourcePort, PortProvisioning targetPort) throws Exception {
        safeJavaScriptClick($(SOURCE_ENDSZ_INPUT));
        $(SOURCE_ENDSZ_INPUT).val(sourcePort.getEndSz());
        safeJavaScriptClick($(SOURCE_SLOT_INPUT));
        $(SOURCE_SLOT_INPUT).val(sourcePort.getSlotNumber());
        safeJavaScriptClick($(SOURCE_PORT_INPUT));
        $(SOURCE_PORT_INPUT).val(sourcePort.getPortNumber());

        safeJavaScriptClick($(TARGET_ENDSZ_INPUT));
        $(TARGET_ENDSZ_INPUT).val(targetPort.getEndSz());
        safeJavaScriptClick($(TARGET_SLOT_INPUT));
        $(TARGET_SLOT_INPUT).val(targetPort.getSlotNumber());
        safeJavaScriptClick($(TARGET_PORT_INPUT));
        $(TARGET_PORT_INPUT).val(targetPort.getPortNumber());

        $(PORT_TO_PORT_PREPARE_BUTTON).click();
        $(GANZEN_PON_PORT_UMSCHALTEN).click();
        $(NOTIFICATION).shouldBe(visible);
        $(NOTIFICATION).shouldHave(text("Die Vorbereitung für den Zielport hat begonnen"));
        closeNotificationButton();
        return this;
    }

    @Step("Start preparation phase for card")
    public NetworkSwitchingPage startCardPreparation(PortProvisioning sourcePort1, PortProvisioning sourcePort2, PortProvisioning targetPort1, PortProvisioning targetPort2) throws Exception {
        $(NE3_UMSCHALTUNG_TAB).click();
        $(SLOT_TO_SLOT_PREPARATION_TAB).click();
        safeJavaScriptClick($(SOURCE_ENDSZ_INPUT));
        $(SOURCE_ENDSZ_INPUT).val(sourcePort1.getEndSz());
        safeJavaScriptClick($(SOURCE_SLOT_INPUT));
        $(SOURCE_SLOT_INPUT).val(sourcePort1.getSlotNumber());

        safeJavaScriptClick($(TARGET_ENDSZ_INPUT));
        $(TARGET_ENDSZ_INPUT).val(targetPort1.getEndSz());
        $(DEVICE_INFORMATION).click();

        safeJavaScriptClick($(SOURCE_PORT1));
        $(SOURCE_PORT1).val(sourcePort1.getPortNumber());
        safeJavaScriptClick($(TARGET_PORT1));
        $(TARGET_PORT1).val(targetPort1.getPortNumber());
        safeJavaScriptClick($(TARGET_SLOT1));
        $(TARGET_SLOT1).val(targetPort1.getSlotNumber());
        $(ADD_LINE_BTN).click();

        safeJavaScriptClick($(SOURCE_PORT2));
        $(SOURCE_PORT2).val(sourcePort2.getPortNumber());
        safeJavaScriptClick($(TARGET_PORT2));
        $(TARGET_PORT2).val(targetPort2.getPortNumber());
        safeJavaScriptClick($(TARGET_SLOT2));
        $(TARGET_SLOT2).val(targetPort2.getSlotNumber());
        $(SLOT_TO_SLOT_PREPARE_BUTTON).click();
        $(NOTIFICATION).shouldHave(text("Die Vorbereitung für den Zielport hat begonnen"));
        closeNotificationButton();
        return this;
    }

    @Step("Get HomeIds for partial port to port preparation")
    public NetworkSwitchingPage clickPartialPortPreparation(PortProvisioning sourcePort, PortProvisioning targetPort) throws Exception {
        safeJavaScriptClick($(SOURCE_ENDSZ_INPUT));
        $(SOURCE_ENDSZ_INPUT).val(sourcePort.getEndSz());
        safeJavaScriptClick($(SOURCE_SLOT_INPUT));
        $(SOURCE_SLOT_INPUT).val(sourcePort.getSlotNumber());
        safeJavaScriptClick($(SOURCE_PORT_INPUT));
        $(SOURCE_PORT_INPUT).val(sourcePort.getPortNumber());

        safeJavaScriptClick($(TARGET_ENDSZ_INPUT));
        $(TARGET_ENDSZ_INPUT).val(targetPort.getEndSz());
        safeJavaScriptClick($(TARGET_SLOT_INPUT));
        $(TARGET_SLOT_INPUT).val(targetPort.getSlotNumber());
        safeJavaScriptClick($(TARGET_PORT_INPUT));
        $(TARGET_PORT_INPUT).val(targetPort.getPortNumber());

        $(PORT_TO_PORT_PREPARE_BUTTON).click();
        $(HOMEIDS_MANUELLE_AUSWAHL).click();
        return this;
    }

    @Step("Collect HomeIds for partial switching")
    public ElementsCollection collectHomeIds() {
        $$(HOMEIDS).shouldHave(CollectionCondition.size(16));
        return $$(HOMEIDS);
    }

    @Step("Select HomeIds for partial switching")
    public List<String> selectHomeIdsForPreparation(int numberOfHomeIds) {
        ElementsCollection homeIdElements = collectHomeIds();
        for (int i = homeIdElements.size() - 1; i >= numberOfHomeIds; i--) {
            homeIdElements.get(i).click();
        }
        return homeIdElements.stream().map(homeIdElement -> homeIdElement.getText()).collect(Collectors.toList()).subList(0, numberOfHomeIds);
    }

    public NetworkSwitchingPage clickPrepareButton() {
        $(VORBETEITUNG_STARTEN).click();
        return this;
    }

    @Step("Click on PackageId Link on Preparation tab")
    public NetworkSwitchingPage clickPackageId() {
        $(PACKAGE_ID_PREPARATION_TAB).click();
        $(GET_PACKAGE_DATA_BUTTON).shouldBe(visible);
        return this;
    }

    @Step("Start commit phase")
    public NetworkSwitchingPage startCommit(String packageId, String sourcePortAction, String expectedInitialState) throws Exception {
        clickPaketverwaltungTab();
        getPackageInfo(packageId);
        getPackageStatus().contains(expectedInitialState);
        $(ACTIONS_DROPDOWN).click();
        By ACTION = byXpath("//li[contains(@aria-label,'" + sourcePortAction+"')]");
        $(ACTION).click();
        $(COMMIT_BUTTON).click();
        $(NOTIFICATION).shouldHave(text("Der Abschließprozess wurde gestartet"));
        closeNotificationButton();
        return this;
    }

    @Step("Start execution phase")
    public NetworkSwitchingPage startExecution(String packageId) throws Exception {
        clickPaketverwaltungTab();
        getPackageInfo(packageId);
        waitUntilNeededStatus("PREPARED",packageId);
        $(EXECUTION_BUTTON).click();
        $(NOTIFICATION).shouldBe(visible);
        $(NOTIFICATION).shouldHave(text("Der Durchführungsprozess für den Zielport hat begonnen"));
        closeNotificationButton();
        return this;
    }

    @Step("Start rollback")
    public NetworkSwitchingPage startRollback(String packageId, String expectedInitialState) throws Exception {
        clickPaketverwaltungTab();
        getPackageInfo(packageId);
        waitUntilNeededStatus(expectedInitialState, packageId);
        $(ROLLBACK_BUTTON).click();
        $(NOTIFICATION).shouldBe(visible);
        $(NOTIFICATION).shouldHave(text("Der Rollback-Prozess für den Zielport hat begonnen"));
        closeNotificationButton();
        return this;
    }

    @Step("Get package info")
    public NetworkSwitchingPage getPackageInfo(String packageId) throws Exception {
        safeJavaScriptClick($(GET_PACKAGE_DATA_BY_PACKAGE_ID_INPUT));
        $(GET_PACKAGE_DATA_BY_PACKAGE_ID_INPUT).val(packageId);
        $(GET_PACKAGE_DATA_BUTTON).click();
        return this;
    }

    @Step("Click search tab")
    public NetworkSwitchingPage clickSearchTab() {
        $(SEARCH_TAB).click();
        return this;
    }

    @Step("Search Packages by Device")
    public NetworkSwitchingPage searchPackagesByDevice(PortProvisioning targetPort) throws Exception {
        $(SEARCH_TAB).click();
        safeJavaScriptClick($(SEARCH_BY_ENDSZ_INPUT));
        $(SEARCH_BY_ENDSZ_INPUT).val(targetPort.getEndSz());
        safeJavaScriptClick($(SEARCH_BY_SLOT_INPUT));
        $(SEARCH_BY_SLOT_INPUT).val(targetPort.getSlotNumber());
        safeJavaScriptClick($(SEARCH_BY_PORT_INPUT));
        $(SEARCH_BY_PORT_INPUT).val(targetPort.getPortNumber());
        $(SEARCH_BUTTON).click();
        return this;
    }

    @Step("Click Paketverwaltung tab")
    public NetworkSwitchingPage clickPaketverwaltungTab() {
        $(NE3_UMSCHALTUNG_TAB).click();
        $(PAKETVERWALTUNG_TAB).click();
        return this;
    }

    @Step("Get Package Status")
    public String getPackageStatus() {
        return $(PACKAGE_STATUS).getText();
    }

    @Step("Close Notification button")
    public void closeNotificationButton() {
        $(CLOSE_NOTIFICATION_BUTTON).click();
    }

    @Step("Get PackageId on Preparation tab")
    public String getPackageIdOnPreparationTab() {
        return $(PACKAGE_ID_PREPARATION_TAB).getText();
    }

    public String getPackageIdOnSearchTab() {
        return $(PACKAGE_ID_SEARCH_TAB).getText();
    }

    public WebElement getCommitButton() {
        return $(COMMIT_BUTTON);
    }
    public WebElement getExecutionButton() {
        return $(EXECUTION_BUTTON);
    }


    public WebElement getRollbackButton() {
        return $(ROLLBACK_BUTTON);
    }

    @Step("Wait until needed status")
    public NetworkSwitchingPage waitUntilNeededStatus(String expectedStatus, String packageId) {
        try {
            TimeoutBlock timeoutBlock = new TimeoutBlock(TIMEOUT); //set timeout in milliseconds
            timeoutBlock.setTimeoutInterval(1000);
            Supplier<Boolean> checkPackageStatus = () -> {
                Boolean result = false;
                try {
                    getPackageInfo(packageId);
                    result = getPackageStatus().contains(expectedStatus);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return result;
            };
            timeoutBlock.addBlock(checkPackageStatus); // execute the runnable precondition
        } catch (Throwable e) {
            //catch the exception here . Which is block didn't execute within the time limit
        }

        return this;
    }

    public void safeJavaScriptClick(SelenideElement element) throws Exception {

        try {
            if (element.isEnabled() && element.isDisplayed()) {

                System.out.println("Clicking on element with using java script click");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            } else {
                System.out.println("Unable to click on element");
            }
        } catch (StaleElementReferenceException e) {
            System.out.println("Element is not attached to the page document " + e.getStackTrace());
        } catch (NoSuchElementException e) {
            System.out.println("Element was not found in DOM " + e.getStackTrace());
        } catch (Exception e) {
            System.out.println("Unable to click on element " + e.getStackTrace());
        }
    }


}
