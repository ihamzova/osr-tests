package com.tsystems.tm.acc.ta.pages.osr.networkswitching;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLineManagementTableElement;
import com.tsystems.tm.acc.ta.data.osr.models.NetworkSwitchingUplinkElement;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import com.tsystems.tm.acc.ta.helpers.osr.logs.TimeoutBlock;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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
    private static final By PROCESS_INFO_TAB = byQaData("process-tab");

    private static final By ACTIONS_DROPDOWN = byQaData("actions-dropdown");
    private static final By DETAILED_INFO = byQaData("subpackages-tab");

    private static final By NOTIFICATION = byXpath("//h2[@role = 'alert']");
    private static final By CLOSE_NOTIFICATION_BUTTON = byXpath("//*[@role='alert']/button");
    private static final By UPLINKS_ERROR_MESSAGE = byXpath("//*[contains(@class, 'error-message')]");
    private static final By PACKAGE_POLLING_PHASE = byXpath("//*[@class='package-polling--phase");

    private static final By PACKAGE_STATUS = byXpath("//*[@class='package-info']");
    private static final By PACKAGE_ID_PREPARATION_TAB = byXpath("//*[@class='package-polling--id']//a");
    private static final By PACKAGE_ID_SEARCH_TAB = byXpath("//*[@class='p-element p-treetable-tbody']//a");

    private static final By HOMEIDS_SUBMIT_SECTION = byXpath("//*[@class='submit-section__homeids']");
    private static final By HOMEIDS = byClassName("submit-section__homeid");

    @Step("Open Network Switching page")
    public static NetworkSwitchingPage openPage() {
        URL url = new GigabitUrlBuilder(APP).withEndpoint(ENDPOINT).build();
        log.info("Opening url " + url.toString());
        return open(url, NetworkSwitchingPage.class);
    }

    @Step("Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

    @Step("Start preparation phase for port")
    public NetworkSwitchingPage startFullPortPreparation(PortProvisioning sourcePort, PortProvisioning targetPort) throws Exception {
        fillInputField($(SOURCE_ENDSZ_INPUT), sourcePort.getEndSz());
        fillInputField($(SOURCE_SLOT_INPUT), sourcePort.getSlotNumber());
        fillInputField($(SOURCE_PORT_INPUT), sourcePort.getPortNumber());

        fillInputField($(TARGET_ENDSZ_INPUT), targetPort.getEndSz());
        fillInputField($(TARGET_SLOT_INPUT), targetPort.getSlotNumber());
        fillInputField($(TARGET_PORT_INPUT), targetPort.getPortNumber());

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

        fillInputField($(SOURCE_ENDSZ_INPUT), sourcePort1.getEndSz());
        fillInputField($(SOURCE_SLOT_INPUT), sourcePort1.getSlotNumber());

        fillInputField($(TARGET_ENDSZ_INPUT), targetPort1.getEndSz());
        $(DEVICE_INFORMATION).click();

        fillInputField($(SOURCE_PORT1), sourcePort1.getPortNumber());
        fillInputField($(TARGET_PORT1), targetPort1.getPortNumber());
        fillInputField($(TARGET_SLOT1), targetPort1.getSlotNumber());
        $(ADD_LINE_BTN).click();

        fillInputField($(SOURCE_PORT2), sourcePort2.getPortNumber());
        fillInputField($(TARGET_PORT2), targetPort2.getPortNumber());
        fillInputField($(TARGET_PORT2), targetPort2.getSlotNumber());

        $(SLOT_TO_SLOT_PREPARE_BUTTON).click();
        $(NOTIFICATION).shouldHave(text("Die Vorbereitung für den Zielport hat begonnen"));
        closeNotificationButton();
        return this;
    }

    @Step("Get HomeIds for partial port to port preparation")
    public NetworkSwitchingPage clickPartialPortPreparation(PortProvisioning sourcePort, PortProvisioning targetPort) {
        fillInputField($(SOURCE_ENDSZ_INPUT), sourcePort.getEndSz());
        fillInputField($(SOURCE_SLOT_INPUT), sourcePort.getSlotNumber());
        fillInputField($(SOURCE_PORT_INPUT), sourcePort.getPortNumber());

        fillInputField($(TARGET_ENDSZ_INPUT), targetPort.getEndSz());
        fillInputField($(TARGET_SLOT_INPUT), targetPort.getSlotNumber());
        fillInputField($(TARGET_PORT_INPUT), targetPort.getPortNumber());

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

    @Step("Switch to NE2 Umschaltung tab")
    public NetworkSwitchingPage switchToNe2Switching() {
        $(NE2_UMSCHALTUNG_TAB).click();
        return this;
    }

    @Step("Start NE2 Preparation")
    public NetworkSwitchingPage startNe2Preparation(String endSz) {
        switchToNe2Switching();
        clickGetUplinks(endSz);
        clickPrepareButton();
        assertTrue(getNotification().equals("Die Vorbereitung für den Zielport hat begonnen"));
        closeNotificationButton();
        return this;
    }

    @Step("Click Get Uplinks button")
    public NetworkSwitchingPage clickGetUplinks(String endSz) {
        fillInputField($(SOURCE_ENDSZ_INPUT), endSz);
        $(DEVICE_INFORMATION).click();
        return this;
    }

    @Step("Collect information about Uplinks")
    public List<NetworkSwitchingUplinkElement> getUplinkInformation() {
        ElementsCollection uplinks = $$x("//*[contains(@class, 'uplink-container')]");

        return uplinks.stream().map(element ->{
            NetworkSwitchingUplinkElement networkSwitchingUplinkElement = new NetworkSwitchingUplinkElement();
            SelenideElement radio = element.$x(".//div[@class='radio']/input");
     //       boolean checked = Boolean.parseBoolean(radio.$("input[type='radio']").getAttribute("checked"));
            String state = element.$x(".//*[contains(@class, 'uplink')]//*[@class='state']//p").getText().trim();
            String endSz = element.$$x(".//*[contains(@class, 'uplink')]//*[@class='device']//*[@class='device__item']//p").get(0).getText();
            String port = element.$$x(".//*[contains(@class, 'uplink')]//*[@class='device']//*[@class='device__item']//p").get(2).getText();

            PortProvisioning uplink = new PortProvisioning();
            uplink.setEndSz(endSz);
            uplink.setPortNumber(port);

            networkSwitchingUplinkElement.setRadio(radio);
            networkSwitchingUplinkElement.setUplink(uplink);
            networkSwitchingUplinkElement.setState(state);
            return networkSwitchingUplinkElement;
        }).collect(Collectors.toList());
    }

    @Step("Click Prepare button")
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

    @Step("Start NE3 Execution phase")
    public NetworkSwitchingPage startNe3Execution(String packageId) {
        clickPaketverwaltungNe3Tab();
        getPackageInfo(packageId);
        waitUntilNeededStatus("PREPARED",packageId);
        $(EXECUTION_BUTTON).click();
        $(NOTIFICATION).shouldBe(visible);
        $(NOTIFICATION).shouldHave(text("Der Durchführungsprozess für den Zielport hat begonnen"));
        closeNotificationButton();
        return this;
    }

    @Step("Start NE2 Execution phase")
    public NetworkSwitchingPage startNe2Execution(String packageId) {
        clickPaketverwaltungNe2Tab();
        getPackageInfo(packageId);
        waitUntilNeededStatus("PREPARED",packageId);
        $(EXECUTION_BUTTON).click();
        $(NOTIFICATION).shouldBe(visible);
        $(NOTIFICATION).shouldHave(text("Der Durchführungsprozess hat begonnen"));
        closeNotificationButton();
        return this;
    }

    @Step("Click Execution button")
    public NetworkSwitchingPage clickExecutionButton() {
        $(EXECUTION_BUTTON).click();
        return this;
    }

    @Step("Start rollback")
    public NetworkSwitchingPage startRollback(String packageId, String expectedInitialState) {
        clickPaketverwaltungNe3Tab();
        getPackageInfo(packageId);
        waitUntilNeededStatus(expectedInitialState, packageId);
        clickRollbackButton();
        $(NOTIFICATION).shouldBe(visible);
        $(NOTIFICATION).shouldHave(text("Der Rollback-Prozess für den Zielport hat begonnen"));
        closeNotificationButton();
        return this;
    }

    @Step("ClickRollback button")
    public NetworkSwitchingPage clickRollbackButton() {
        $(ROLLBACK_BUTTON).click();
        $(NOTIFICATION).shouldBe(visible);
        return this;
    }

    @Step("Start NE3 commit phase")
    public NetworkSwitchingPage startNe3Commit(String packageId, String sourcePortAction, String expectedInitialState) {
        clickPaketverwaltungNe3Tab();
        getPackageInfo(packageId);
        getPackageStatus().contains(expectedInitialState);
        $(ACTIONS_DROPDOWN).click();
        By ACTION = byXpath("//li[contains(@aria-label,'" + sourcePortAction+"')]");
        $(ACTION).click();
        $(COMMIT_BUTTON).click();
        $(NOTIFICATION).shouldBe(visible);
        $(NOTIFICATION).shouldHave(text("Der Abschließprozess wurde gestartet"));
        closeNotificationButton();
        return this;
    }

    @Step("Start NE2 commit phase")
    public NetworkSwitchingPage startNe2Commit(String packageId) {
        clickPaketverwaltungNe2Tab();
        getPackageInfo(packageId);
        getPackageStatus().contains("EXECUTED");
        $(COMMIT_BUTTON).click();
        $(NOTIFICATION).shouldBe(visible);
        $(NOTIFICATION).shouldHave(text("Der Abschließprozess wurde gestartet"));
        closeNotificationButton();
        return this;
    }

    @Step("Get package info")
    public NetworkSwitchingPage getPackageInfo(String packageId) {
        fillInputField($(GET_PACKAGE_DATA_BY_PACKAGE_ID_INPUT), packageId);
        $(GET_PACKAGE_DATA_BUTTON).click();
        return this;
    }

    @Step("Click Detailed info button")
    public NetworkSwitchingPage clickDetailedInfoButton() {
        $(DETAILED_INFO).click();
        return this;
    }

    @Step("Check Source and Target Devices")
    public void checkSourceAndTargetDevices(List<NetworkSwitchingUplinkElement> uplinksInfo) {
        NetworkSwitchingUplinkElement activeUplink = uplinksInfo.stream()
                .filter(uplink -> uplink.getState().equals("ACTIVE")).collect(Collectors.toList()).get(0);
        NetworkSwitchingUplinkElement plannedUplink = uplinksInfo.stream()
                .filter(uplink -> uplink.getState().equals("PLANNED"))
                .collect(Collectors.toList()).get(0);

        PortProvisioning sourceBng = new PortProvisioning();
        PortProvisioning targetBng = new PortProvisioning();
        String sourceBngDevice = $x("//*[contains(text(), 'Von BNG')]/following-sibling::p").getText();
        String targetBngDevice = $x("//*[contains(text(), 'Auf BNG')]/following-sibling::p").getText();

        sourceBng.setEndSz(sourceBngDevice.split(" /")[0].trim());
        sourceBng.setPortNumber(sourceBngDevice.split(" /")[2].trim());

        targetBng.setEndSz(targetBngDevice.split(" /")[0].trim());
        targetBng.setPortNumber(targetBngDevice.split(" /")[2].trim());

        assertEquals(activeUplink.getUplink(), sourceBng);
        assertEquals(plannedUplink.getUplink(), targetBng);
    }

    @Step("Click search tab")
    public NetworkSwitchingPage clickSearchTab() {
        $(SEARCH_TAB).click();
        return this;
    }

    @Step("Search Packages by Device")
    public NetworkSwitchingPage searchPackagesByDevice(PortProvisioning targetPort) {
        $(SEARCH_TAB).click();
        fillInputField($(SEARCH_BY_ENDSZ_INPUT), targetPort.getEndSz());
        fillInputField($(SEARCH_BY_SLOT_INPUT), targetPort.getSlotNumber());
        fillInputField($(SEARCH_BY_PORT_INPUT), targetPort.getPortNumber());
        $(SEARCH_BUTTON).click();
        return this;
    }

    @Step("Click Paketverwaltung NE3 tab")
    public NetworkSwitchingPage clickPaketverwaltungNe3Tab() {
        $(NE3_UMSCHALTUNG_TAB).click();
        $(PAKETVERWALTUNG_TAB).click();
        return this;
    }

    @Step("Click Paketverwaltung NE2 tab")
    public NetworkSwitchingPage clickPaketverwaltungNe2Tab() {
        $(NE2_UMSCHALTUNG_TAB).click();
        $(PAKETVERWALTUNG_TAB).click();
        return this;
    }

    @Step("Get Preparation button")
    public SelenideElement getPreparationButton() {
        return $(VORBETEITUNG_STARTEN);
    }

    @Step("Get Package Status")
    public String getPackageStatus() {
        return $(PACKAGE_STATUS).getText();
    }

    @Step("Close Notification button")
    public void closeNotificationButton() {
        $(CLOSE_NOTIFICATION_BUTTON).click();
    }

    public SelenideElement getUplinksErrorMessage() {
        return $(UPLINKS_ERROR_MESSAGE);
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
    public String getNotification() { return $(NOTIFICATION).getText(); }
    public ElementsCollection getUplinks() {
        return $$(byXpath("//*[contains(@class, 'uplinks-section')]/*[contains(@class, 'uplink-container')]"));
    }
    public List<String> getUplinksStates() {
        ElementsCollection uplinkStates = $$(byXpath("//*[@class='state']//p"));
        return uplinkStates.stream()
                .map(element -> element.getText()).collect(Collectors.toList());
    }

    public NetworkSwitchingPage fillInputField(SelenideElement element, String value) {
        safeJavaScriptClick(element);
        element.val(value);
        return this;
    }

    public void safeJavaScriptClick(SelenideElement element) {
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
