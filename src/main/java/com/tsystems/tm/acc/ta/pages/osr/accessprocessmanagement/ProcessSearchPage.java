package com.tsystems.tm.acc.ta.pages.osr.accessprocessmanagement;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.tsystems.tm.acc.ta.data.osr.models.Process;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byClassName;
import static com.codeborne.selenide.Selenide.*;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.*;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;
import static org.testng.Assert.assertEqualsNoOrder;
import static org.testng.Assert.assertTrue;

@Slf4j
public class ProcessSearchPage {

    WebDriver driver;

    private static final String APP = "access-process-management-ui";
    private static final String ENDPOINT = "/search";

    private static final By ENDSZ_INPUT = byQaData("fic-endsz-input");
    private static final By SLOT_NUMBER_INPUT = byQaData("fic-slot-input");
    private static final By PORT_NUMBER_INPUT = byQaData("fic-port-input");
    private static final By PROCESS_ID_INPUT = byQaData("fic-processid-input");

    private static final By SEARCH_BUTTON = byQaData("sc-search-button");
    private static final By RUNNING_STATUS = byQaData("sc-running-input");
    private static final By FINISHED_STATUS = byQaData("sc-finished-input");
    private static final By FAILED_STATUS = byQaData("sc-failed-input");
    private static final By DELETED_STATUS = byQaData("sc-deleted-input");

    private static final By SORT_BY_START_TIME = byQaData("tc-starttimesort-th");
    private static final By SORT_BY_END_TIME = byQaData("tc-endtimesort-th");
    private static final By SORT_BY_STATUS = byQaData("tc-statussort-th");

    private static final By SEARCH_TABLE = byQaData("tc-processes-ptreetable");
    private static final By TABLE_MESSAGE = byQaData("tc-itemscount-p");
    private static final By SUBPROCESSES = byQaData("tc-toggler-ptreetabletoggler");
    private static final By RESTART_BUTTON = byQaData("tc-restore-button");

    private static final By CONFIRMATION_DIALOG = byQaData("tc-restoredialog-pdialog");
    private static final By CONFIRMATION_DIALOG_MESSAGE = byQaData("tc-restoremessage-p");
    private static final By OK_IN_CONFIRMATION_DIALOG = byQaData("tc-confirmrestore-button");

    @Step("Open Process Search page")
    public static ProcessSearchPage openPage() {
        URL url = new OCUrlBuilder(APP).withEndpoint(ENDPOINT).build();
        log.info("Opening url " + url.toString());
        return open(url, ProcessSearchPage.class);
    }

    @Step("Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

    @Step("Search Processes by device parameters")
    public ProcessSearchPage searchProcessesByDevice(Process process) throws Exception {
        safeJavaScriptClick($(ENDSZ_INPUT));
        $(ENDSZ_INPUT).val(process.getEndSz());
        if (!process.getSlotNumber().isEmpty()) {
            safeJavaScriptClick($(SLOT_NUMBER_INPUT));
            $(SLOT_NUMBER_INPUT).val(process.getSlotNumber());
        }
        if (!process.getPortNumber().isEmpty()) {
            safeJavaScriptClick($(PORT_NUMBER_INPUT));
            $(PORT_NUMBER_INPUT).val(process.getPortNumber());
        }
        return this;
    }

    @Step("Search Processes by process id")
    public ProcessSearchPage searchProcessesByProcessId(String processId) throws Exception {
        safeJavaScriptClick($(PROCESS_ID_INPUT));
        $(PROCESS_ID_INPUT).val(processId);
        return this;
    }

    @Step("Click Search button")
    public ProcessSearchPage clickSearchButton() {
        $(SEARCH_BUTTON).shouldBe(visible).click();
        $(SEARCH_TABLE).shouldBe(visible);
        return this;
    }

    @Step("Set Running status")
    public ProcessSearchPage setRunningStatus() {
        $(RUNNING_STATUS).click();
        return this;
    }

    @Step("Set Finished status")
    public ProcessSearchPage setFinishedStatus() {
        $(FINISHED_STATUS).click();
        return this;
    }

    @Step("Set Failed status")
    public ProcessSearchPage setFailedStatus() {
        $(FAILED_STATUS).click();
        return this;
    }

    @Step("Set Deleted status")
    public ProcessSearchPage setDeletedStatus() {
        $(DELETED_STATUS).click();
        return this;
    }

    @Step("Sort table by starttime, descending")
    public ProcessSearchPage sortTableByStartTimeDescending(){
        $(SORT_BY_START_TIME).click();
        $(SORT_BY_START_TIME).click();
        $(SEARCH_TABLE).shouldBe(visible);
        return this;
    }

    @Step("Click Open subprocesses button ")
    public ProcessSearchPage clickOpenSubprocessesButton(int processIndex) {
        getMainProcesses().get(processIndex).find("button.p-treetable-toggler").click();
        return this;
    }

    @Step("Click Neustarten button")
    public ProcessSearchPage clickRestartButton(int processIndex) {
        getMainProcesses().get(processIndex).find("button.restore__button").click();
        return this;
    }

    @Step("Click Ok in confirmation dialog")
    public ProcessSearchPage clickOkInConfirmationDialog(){
        $(OK_IN_CONFIRMATION_DIALOG).click();
        switchTo().window(1);
        $(SEARCH_TABLE).shouldBe(visible);
        return this;
    }

    @Step("Get table headers")
    public List<String> getTableHeaders() {
        $(SEARCH_TABLE).findAll(By.tagName("th")).shouldHaveSize(11);
        return $(SEARCH_TABLE).findAll(By.tagName("th")).stream()
                .map(SelenideElement::text)
                .collect(Collectors.toList());
    }

    @Step("Get table message")
    public String getTableMessage() {
        return $(TABLE_MESSAGE).shouldBe(visible).text();
    }

    @Step("Get the main process")
    public List<SelenideElement> getMainProcesses() {
        return $(SEARCH_TABLE).find(By.tagName("tbody")).findAll(byClassName("level--0"));
    }

    @Step("Get subprocesses")
    public List<SelenideElement> getSubprocesses() {
        return $(SEARCH_TABLE).find(By.tagName("tbody")).findAll(byClassName("level--1"));
    }

    @Step("Get info of the main processes")
    public List<Process> getInfoForMainProcesses() {
        return getMainProcesses().stream()
                .map(element -> {
                    ElementsCollection tds = element.findAll(By.tagName("td"));
                    Process processList = new Process();
                    processList.setProcessName(tds.get(0).getText());
                    processList.setEndSz(tds.get(1).getText());
                    processList.setSlotNumber(tds.get(2).getText());
                    processList.setPortNumber(tds.get(3).getText());
                    processList.setLineId(tds.get(4).getText());
                    processList.setStartTime(tds.get(5).getText());
                    processList.setEndTime(tds.get(6).getText());
                    processList.setDuration(tds.get(7).getText());
                    processList.setState(tds.get(8).getText());
                    return processList;
                })
                .collect(Collectors.toList());
    }

    @Step("Check table headers")
    public void checkTableHeaders(List<String> tableHeaders) {
        List<String> supposedHeaders = Arrays.asList("Prozessname", "EndSZ", "Slot", "Port", "Line ID", "Startzeitpunkt", "Endezeitpunkt",
                "Dauer", "Status", "Letzter Fehler", "Aktion");
        assertEqualsNoOrder(tableHeaders.stream().filter(header -> !header.isEmpty()).toArray(),
                supposedHeaders.toArray());
    }

    @Step("Check table message")
    public void checkTableMessagePattern(String tableMessage) {
        String expectedPattern = "\\d+ Prozesse? wurden? gefunden";
        assertTrue(Pattern.matches(expectedPattern, tableMessage));
    }

    @Step("Check main process")
    public void checkMainProcess (Process actualProcess, Process expectedProcess) {
        assertTrue(actualProcess.getProcessName().equals(expectedProcess.getProcessName()));
        assertTrue(actualProcess.getEndSz().equals(expectedProcess.getEndSz()));
        assertTrue(actualProcess.getSlotNumber().equals(expectedProcess.getSlotNumber()));
        assertTrue(actualProcess.getPortNumber().equals(expectedProcess.getPortNumber()));
        assertTrue(actualProcess.getLineId().equals(expectedProcess.getLineId()));
        assertTrue(actualProcess.getEndTime().equals(expectedProcess.getEndTime()));
        assertTrue(actualProcess.getDuration().equals(expectedProcess.getDuration()));
        assertTrue(actualProcess.getState().equals(STATUS_FAILED));
    }

    @Step("Check subprocesses")
    public void checkSubprocesses(List<SelenideElement> subprocesses) {
        assertTrue(subprocesses.size() > 0);
    }

    @Step("Check confirmation dialog")
    public void checkConfirmationDialog(){
        $(CONFIRMATION_DIALOG).should(visible);
        $(CONFIRMATION_DIALOG_MESSAGE).shouldHave(text("Prozess wurde neugestartet. Sie werden zu einem neuen Tab mit der Prozesssuche weitergeleitet"));
        $(OK_IN_CONFIRMATION_DIALOG).shouldBe(enabled);
    }

    @Step("Check process after restoration")
    public void checkRestoredProcess(Process restoredProcess, Process initialProcess){
        assertTrue(restoredProcess.getProcessName().equals(initialProcess.getProcessName()));
        assertTrue(restoredProcess.getEndSz().equals(initialProcess.getEndSz()));
        assertTrue(restoredProcess.getSlotNumber().equals(initialProcess.getSlotNumber()));
        assertTrue(restoredProcess.getPortNumber().equals(initialProcess.getPortNumber()));
        assertTrue(restoredProcess.getLineId().equals(initialProcess.getLineId()));
        assertTrue(restoredProcess.getStartTime().equals(initialProcess.getStartTime()));
        assertTrue(restoredProcess.getEndTime().equals(initialProcess.getEndTime()));
        assertTrue(restoredProcess.getDuration().equals(initialProcess.getDuration()));
        assertTrue(restoredProcess.getState().equals(STATUS_GESTARTET));
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
            System.out.println("Element is not attached to the page document "+ e.getStackTrace());
        } catch (NoSuchElementException e) {
            System.out.println("Element was not found in DOM "+ e.getStackTrace());
        } catch (Exception e) {
            System.out.println("Unable to click on element "+ e.getStackTrace());
        }
    }

}
