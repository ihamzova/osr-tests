package com.tsystems.tm.acc.ta.team.upiter.processmanagement;

import com.tsystems.tm.acc.data.upiter.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.upiter.models.process.ProcessCase;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.Process;
import com.tsystems.tm.acc.ta.pages.osr.accessprocessmanagement.ProcessSearchPage;
import com.tsystems.tm.acc.ta.robot.osr.WgAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.STATUS_FAILED;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.STATUS_RUNNING;
import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static org.testng.Assert.assertTrue;

@ServiceLog({
        ACCESS_PROCESS_MANAGEMENT_UI,
        ACCESS_PROCESS_MANAGEMENT_BFF,
        OSR_PROCESS_LOG
})
public class ProcessesSearchTest extends GigabitTest {

  WgAccessProvisioningRobot wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
  private UpiterTestContext context = UpiterTestContext.get();
  private Process process;
  private String processUuid;
  private String today;
  private String dayAgo;
  private String weekAgo;
  private String threeHoursAgo;

  @BeforeClass
  public void init() {
    process = context.getData().getProcessDataProvider().get(ProcessCase.processesData);
    processUuid = wgAccessProvisioningRobot.startPortProvisioningAndGetProcessId(process).toString();
    Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOTelekomNSOOpsRW);
    setCredentials(loginData.getLogin(), loginData.getPassword());
    today = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("dd MM"));
    dayAgo = OffsetDateTime.now().minusDays(2).format(DateTimeFormatter.ofPattern("dd MM"));
    weekAgo = OffsetDateTime.now().minusDays(8).format(DateTimeFormatter.ofPattern("dd MM"));
    threeHoursAgo = OffsetDateTime.now().minusHours(3).format(DateTimeFormatter.ofPattern("HH"));
  }

  @Test
  @TmsLink("DIGIHUB-45514")
  @Description("Search processes by EndSZ, Slot, Port in Access Process Management UI")
  public void searchProcessesByEndSzTest() throws Exception {
    ProcessSearchPage processSearchPage = ProcessSearchPage.openPage();
    processSearchPage.validateUrl();
    processSearchPage.searchProcessesByDevice(process)
            .clickSearchButton();

    Process runningProcess = processSearchPage.getInfoForMainProcesses().get(0);
    processSearchPage.checkMainProcess(runningProcess, process, today);
    processSearchPage.checkProcessStatus(runningProcess.getState(), STATUS_RUNNING);

    processSearchPage.checkTableHeaders(processSearchPage.getTableHeaders());
    processSearchPage.checkTableMessagePattern(processSearchPage.getTableMessage());
    processSearchPage.clickOpenSubprocessesButton(0);
    processSearchPage.checkSubprocesses(processSearchPage.getSubprocesses());

    processSearchPage.waitUntilNeededStatus(STATUS_FAILED);

    Process failedProcess = processSearchPage.getInfoForMainProcesses().get(0);
    processSearchPage.checkMainProcess(failedProcess, process, today);
    processSearchPage.checkProcessStatus(failedProcess.getState(), STATUS_FAILED);
  }

  @Test
  @TmsLink("DIGIHUB-44044")
  @Description("Search processes by ProcessId in Access Process Management UI")
  public void searchProcessesByProcessIdTest() throws Exception {

    ProcessSearchPage processSearchPage = ProcessSearchPage.openPage();
    processSearchPage.validateUrl();
    processSearchPage.searchProcessesByProcessId(processUuid)
            .clickSearchButton();

    processSearchPage.checkTableHeaders(processSearchPage.getTableHeaders());
    processSearchPage.checkTableMessagePattern(processSearchPage.getTableMessage());
    Process foundProcess = processSearchPage.getInfoForMainProcesses().get(0);
    processSearchPage.checkMainProcess(foundProcess, process, today);
    processSearchPage.waitUntilNeededStatus(STATUS_FAILED);
    processSearchPage.clickOpenSubprocessesButton(0);
    processSearchPage.checkSubprocesses(processSearchPage.getSubprocesses());
  }

  @Test(dependsOnMethods = {"searchProcessesByEndSzTest", "searchProcessesByProcessIdTest"})
  @TmsLink("DIGIHUB-44044")
  @Description("Search processes by ProcessId in Access Process Management UI and restore it")
  public void restoreProcessTest() throws Exception {
    ProcessSearchPage processSearchPage = new ProcessSearchPage().openPage();
    processSearchPage.validateUrl();
    processSearchPage.searchProcessesByProcessId(processUuid)
            .clickSearchButton();

    Process initialProcess = processSearchPage.getInfoForMainProcesses().get(0);

    processSearchPage.clickRestartButton(0);
    processSearchPage.checkConfirmationDialog();
    processSearchPage.clickOkInConfirmationDialog();

    Process restoredProcess = processSearchPage.getInfoForMainProcesses().get(0);
    processSearchPage.checkMainProcess(restoredProcess, initialProcess, today);
    processSearchPage.checkProcessStatus(restoredProcess.getState(), STATUS_RUNNING);
  }

  @Test(priority = 1)
  @TmsLink("DIGIHUB-119127")
  @Description("Search processes only by last 3 hours filter")
  public void filterByLastThreeHoursTest() {
    ProcessSearchPage processSearchPage = new ProcessSearchPage().openPage();
    processSearchPage.validateUrl();
    processSearchPage.filterByThreeHours()
            .clickSearchButton()
            .sortByStartTimeAscending();
    Process oldestProcess = processSearchPage.getInfoForMainProcesses().get(0);
    String oldestHour = processSearchPage.parseStartTimeFromUI(oldestProcess);
    assertTrue(processSearchPage.compareTime(oldestProcess, oldestHour, threeHoursAgo, today));
  }

  @Test(priority = 1)
  @TmsLink("DIGIHUB-119128")
  @Description("Search processes only by last day filter")
  public void filterByLastDayTest() {
    ProcessSearchPage processSearchPage = new ProcessSearchPage().openPage();
    processSearchPage.validateUrl();
    processSearchPage.filterByLastDay()
            .clickSearchButton()
            .sortByStartTimeAscending();
    String oldestDate = processSearchPage.parseStartDateFromUi(processSearchPage.getInfoForMainProcesses().get(0));
    assertTrue(processSearchPage.compareDates(oldestDate, dayAgo));
  }

  @Test(priority = 1)
  @TmsLink("DIGIHUB-119129")
  @Description("Search processes only by last week filter")
  public void filterByLastWeekTest() {
    ProcessSearchPage processSearchPage = new ProcessSearchPage().openPage();
    processSearchPage.validateUrl();
    processSearchPage.filterByLastWeek()
            .clickSearchButton()
            .sortByStartTimeAscending();
    String oldestDate = processSearchPage.parseStartDateFromUi(processSearchPage.getInfoForMainProcesses().get(0));
    assertTrue(processSearchPage.compareDates(oldestDate, weekAgo));
  }
}
