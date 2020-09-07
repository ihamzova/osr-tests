package com.tsystems.tm.acc.ta.pages.osr.processmanagement;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.tsystems.tm.acc.ta.data.osr.models.Process;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.STATUS_GESTARTET;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;
import static org.testng.Assert.assertEqualsNoOrder;
import static org.testng.Assert.assertTrue;

@Slf4j
public class ProcessSearchPage {

    private static final String APP = "access-process-management-ui";
    private static final String ENDPOINT = "/search";
    private static final By DEVICE_TAB = byQaData("sc-processAddressTab-a");
    private static final By ENDSZ_INPUT = byQaData("pac-EndSZ-input");
    private static final By SLOT_NUMBER_INPUT = byQaData("pac-slotNumber-input");
    private static final By PORT_NUMBER_INPUT = byQaData("pac-portNumber-input");
    private static final By SEARCH_BUTTON = byQaData("search-button");
    private static final By RUNNING_STATUS = byQaData("ucc-runningFilter-label");
    private static final By FINISHED_STATUS = byQaData("ucc-finishedFilter-label");
    private static final By FAILED_STATUS = byQaData("ucc-failedFilter-label");
    private static final By DELETED_STATUS = byQaData("ucc-deletedFilter-label");
    private static final By PROCESS_ID_TAB = byQaData("sc-processIdTab-a");
    private static final By PROCESS_ID_INPUT = byQaData("hic-processId-input");
    private static final By SORT_BY_PROCESS_INFO = byQaData("sc-processInfoTimeSortHeader-td");
    private static final By SORT_BY_START_TIME = byQaData("sc-startTimeSortHeader-td");
    private static final By SORT_BY_STATUS = byQaData("sc-statusSortHeader-td");
    private static final By PAGINATOR_DROPDOWN = By.tagName("p-dropdown");
    private static final By P_SEARCH_TABLE = byQaData("sc-searchTable-pTable");
    private static final By TABLE_MESSAGE = byClassName("p-count");
    private static final By ROW_EXPANSION = byQaData("sc-0rowExpansion-i");
    private static final By RESTART_BUTTON = byText("Neustarten");
    private static final By CONFIRMATION_DIALOG = byText("Hinweis");
    private static final By CONFIRMATION_DIALOG_MESSAGE = byClassName("ui-confirmdialog-message");
    private static final By OK_IN_CONFIRMATION_DIALOG = byClassName("ok-restored");
    private static final By VARIABLES = byCssSelector(".variables .ng-star-inserted");

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
    public ProcessSearchPage searchProcessesByDevice(Process process) {
        $(ENDSZ_INPUT).click();
        $(ENDSZ_INPUT).val(process.getEndSz());
        if (!process.getSlotNumber().isEmpty()) {
            $(SLOT_NUMBER_INPUT).click();
            $(SLOT_NUMBER_INPUT).val(process.getSlotNumber());
        }
        if (!process.getPortNumber().isEmpty()) {
            $(PORT_NUMBER_INPUT).click();
            $(PORT_NUMBER_INPUT).val(process.getPortNumber());
        }
        return this;
    }

    @Step("Search Processes by process id")
    public ProcessSearchPage searchProcessesByProcessId(String processId) {
        $(PROCESS_ID_TAB).click();
        $(PROCESS_ID_INPUT).click();
        $(PROCESS_ID_INPUT).val(processId);
        return this;
    }

    @Step("Click Search button")
    public ProcessSearchPage clickSearchButton() {
        $(SEARCH_BUTTON).click();
        $(SEARCH_BUTTON).find(By.tagName("i")).shouldNot(Condition.cssClass("spinner"));
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
    public ProcessSearchPage sortTableByStatusDescending(){
        $(SORT_BY_START_TIME).click();
        $(SORT_BY_START_TIME).click();
        return this;
    }

    @Step("Click first row expansion")
    public ProcessSearchPage clickFirstRowExpansion(){
        $$(ROW_EXPANSION).get(0).click();
        return this;
    }

    @Step("Click Neustarten Button")
    public ProcessSearchPage clickRestartForFirstProcess(){
        $$(RESTART_BUTTON).get(0).click();
        return this;
    }

    @Step("Click Ok in confirmation dialog")
    public ProcessSearchPage clickOkInConfirmationDialog(){
        $(OK_IN_CONFIRMATION_DIALOG).click();
        return this;
    }

    @Step("Get table headers")
    public List<String> getTableHeaders() {
        return $(P_SEARCH_TABLE).findAll(By.tagName("th")).stream().map(SelenideElement::text)
                .collect(Collectors.toList());
    }

    @Step("Get table message")
    public String getTableMessage() {
        return $(TABLE_MESSAGE).text();
    }

    @Step("Get paginator sizes")
    public List<String> getPaginatorSizes() {
        $(P_SEARCH_TABLE).find(PAGINATOR_DROPDOWN).click();
        return $(P_SEARCH_TABLE).find(PAGINATOR_DROPDOWN)
                .findAll(By.tagName("li"))
                .stream()
                .map(SelenideElement::text)
                .collect(Collectors.toList());
    }

    @Step("Collect data about all processes in the table")
    public List<Process> getTableLines(){
        return getTableRows().stream()
                .map(element -> {
                    ElementsCollection tds = element.findAll(By.tagName("td"));
                    Process processInfo = new Process();
                    processInfo.setProcessId(tds.get(1).getText());
                    processInfo.setProcessInfo(tds.get(2).getText());
                    processInfo.setStartTime(tds.get(3).getText());
                    processInfo.setEndTime(tds.get(4).getText());
                    processInfo.setState(tds.get(5).getText());
                    return processInfo;
                })
                .collect(Collectors.toList());
    }

    @Step("Get all table rows")
    public List<SelenideElement> getTableRows(){
        return $(P_SEARCH_TABLE).find(By.tagName("tbody")).findAll(By.tagName("tr"));
    }

    @Step("Check table headers")
    public void checkTableHeaders(List<String>tableHeaders) {
        List<String> expectedHeaders = Arrays.asList("Prozess ID", "Prozessinfo", "Startzeitpunkt", "Endezeitpunkt", "Status", "Aktion");
        assertEqualsNoOrder(tableHeaders.stream().filter(header -> !header.isEmpty()).toArray(), expectedHeaders.toArray());
    }

    @Step("Check table message")
    public void checkTableMessagePattern(String tableMessage) {
        String expectedPattern = "\\d+ Prozesse? wurden? gefunden";
        assertTrue(Pattern.matches(expectedPattern, tableMessage));
    }

    @Step("Check pagination sizes")
    public void checkPaginationSizes(List<String> paginatorSizes) {
        List<String> expectedSizes = Arrays.asList("10", "20", "50", "100");
        assertEqualsNoOrder(paginatorSizes.toArray(), expectedSizes.toArray());
    }

    @Step("Check confirmation dialog")
    public void checkConfirmationDialog(){
        $(CONFIRMATION_DIALOG).should(visible);
        $(CONFIRMATION_DIALOG_MESSAGE).shouldHave(text("Prozess wurde neugestartet. Sie werden zu einem neuen Tab mit der Prozesssuche weitergeleitet"));
        $(OK_IN_CONFIRMATION_DIALOG).shouldBe(enabled);
    }

    @Step("Check process after restoration")
    public void checkRestoredProcess(Process restoredProcess, Process initialProcess){
        assertTrue(restoredProcess.getProcessId().equals(initialProcess.getProcessId()));
        assertTrue(restoredProcess.getProcessInfo().equals(initialProcess.getProcessInfo()));
        assertTrue(restoredProcess.getStartTime().equals(initialProcess.getStartTime()));
        assertTrue(restoredProcess.getEndTime().equals(initialProcess.getEndTime()));
        assertTrue(restoredProcess.getState().equals(STATUS_GESTARTET));
    }

    @Step("Check row expansion data")
    public void checkRowExpansionData(Process actualProcess, Process expectedProcess){
        assertTrue(actualProcess.getEndSz().contains(expectedProcess.getEndSz()));
        assertTrue(actualProcess.getPortNumber().contains(expectedProcess.getPortNumber()));
        assertTrue(actualProcess.getSlotNumber().contains(expectedProcess.getSlotNumber()));
    }

    @Step("Collect data from row expansion")
    public Process collectDataFromRowExpansion(){
        Process process = new Process();
        process.setEndSz($$(VARIABLES).get(0).getText());
        process.setPortNumber($$(VARIABLES).get(1).getText());
        process.setSlotNumber($$(VARIABLES).get(2).getText());
        return process;
    }
}
