package com.tsystems.tm.acc.ta.team.upiter.processmanagement;

import com.tsystems.tm.acc.data.upiter.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.upiter.models.dpudevice.DpuDeviceCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.data.upiter.models.process.ProcessCase;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.data.osr.models.Process;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.pages.osr.accessprocessmanagement.ProcessSearchPage;
import com.tsystems.tm.acc.ta.pages.osr.networkswitching.NetworkSwitchingPage;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.NetworkSwitchingRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgFttbAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.*;
import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.attachStubsToAllureReport;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.savePublishedToDefaultDir;
import static org.testng.Assert.assertTrue;

@ServiceLog({
        ACCESS_PROCESS_MANAGEMENT_UI,
        ACCESS_PROCESS_MANAGEMENT_BFF,
        OSR_PROCESS_LOG
})

@Epic("Access Process Management UI")
public class ProcessesSearchTest extends GigabitTest {

    WgAccessProvisioningRobot wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
    WgFttbAccessProvisioningRobot wgFttbAccessProvisioningRobot = new WgFttbAccessProvisioningRobot();
    private AccessLineRiRobot accessLineRiRobot;
    private UpiterTestContext context = UpiterTestContext.get();
    private Process ftthProcess;
    private Process fttbProcess;
    private Process networkSwitchingProcess;
    private String processUuid;
    private String today;
    private String dayAgo;
    private String weekAgo;
    private String threeHoursAgo;
    private PortProvisioning oltDeviceFttbProvisioningTwistedPair;
    private DpuDevice dpuDeviceFttbProvisioningTwistedPair;
    private NetworkSwitchingRobot networkSwitchingRobot;
    private PortProvisioning endSz_49_30_179_76H1_3_0;
    private PortProvisioning endSz_49_911_1100_76H1_1_0;
    private WireMockMappingsContext mappingsContext;

    @BeforeClass
    public void init() {
        accessLineRiRobot = new AccessLineRiRobot();
        networkSwitchingRobot = new NetworkSwitchingRobot();
        accessLineRiRobot.clearDatabase();
        ftthProcess = context.getData().getProcessDataProvider().get(ProcessCase.ftthFailedProcess);
        fttbProcess = context.getData().getProcessDataProvider().get(ProcessCase.fttbFailedProcess);
        networkSwitchingProcess = context.getData().getProcessDataProvider().get(ProcessCase.networkSwitchingFailedProcess);
        processUuid = wgAccessProvisioningRobot.startPortProvisioningAndGetProcessId(ftthProcess).toString();
        today = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("dd MM"));
        dayAgo = OffsetDateTime.now().minusDays(2).format(DateTimeFormatter.ofPattern("dd MM"));
        weekAgo = OffsetDateTime.now().minusDays(8).format(DateTimeFormatter.ofPattern("dd MM"));
        threeHoursAgo = OffsetDateTime.now().minusHours(3).format(DateTimeFormatter.ofPattern("HH"));
        dpuDeviceFttbProvisioningTwistedPair = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.dpuDeviceForFttbProvisioningTwistedPair);
        oltDeviceFttbProvisioningTwistedPair = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.oltDeviceForFailedFttbProvisioningTwistedPair);
        endSz_49_30_179_76H1_3_0 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.EndSz_49_30_179_76H1_3_0);
        endSz_49_911_1100_76H1_1_0 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.EndSz_49_911_1100_76H1_1_0);
    }

    @BeforeMethod
    void setup() {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOTelekomNSOOpsRW);
        setCredentials(loginData.getLogin(), loginData.getPassword());
    }

    @Test
    @TmsLink("DIGIHUB-45514")
    @Description("Search processes by EndSZ, Slot, Port in Access Process Management UI")
    public void searchProcessesByEndSzTest() throws Exception {
        ProcessSearchPage processSearchPage = ProcessSearchPage.openPage();
        processSearchPage.validateUrl();
        processSearchPage.searchProcessesByDevice(ftthProcess)
                .clickSearchButton();

        Process runningProcess = processSearchPage.getInfoForMainProcesses().get(0);
        processSearchPage.checkMainProcess(runningProcess, ftthProcess, today);
        processSearchPage.checkProcessStatus(runningProcess.getState(), STATUS_RUNNING);

        processSearchPage.checkTableHeaders(processSearchPage.getTableHeaders());
        processSearchPage.checkTableMessagePattern(processSearchPage.getTableMessage());
        processSearchPage.clickOpenSubprocessesButton(0);
        processSearchPage.checkSubprocesses(processSearchPage.getSubprocesses());

        processSearchPage.waitUntilNeededStatus(STATUS_FAILED);

        Process failedProcess = processSearchPage.getInfoForMainProcesses().get(0);
        processSearchPage.checkMainProcess(failedProcess, ftthProcess, today);
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
        processSearchPage.checkMainProcess(foundProcess, ftthProcess, today);
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
    @TmsLink("DIGIHUB-122534")
    @Description("Search processes by DPU Endsz in Access Process Management UI")
    public void searchFailedFttbProcess() throws Exception {
        accessLineRiRobot.fillDatabaseForDpuPreprovisioningV2(1000, 1000, dpuDeviceFttbProvisioningTwistedPair, oltDeviceFttbProvisioningTwistedPair);
        wgFttbAccessProvisioningRobot.startWgFttbAccessProvisioningForDevice(dpuDeviceFttbProvisioningTwistedPair.getEndsz());
        ProcessSearchPage processSearchPage = new ProcessSearchPage().openPage();
        processSearchPage.validateUrl();
        processSearchPage.searchProcessesByDevice(fttbProcess)
                .clickSearchButton();

        Process runningProcess = processSearchPage.getInfoForMainProcesses().get(0);
        processSearchPage.checkMainProcess(runningProcess, fttbProcess, today);
        processSearchPage.checkProcessStatus(runningProcess.getState(), STATUS_RUNNING);
        processSearchPage.checkTableHeaders(processSearchPage.getTableHeaders());
        processSearchPage.checkTableMessagePattern(processSearchPage.getTableMessage());
        processSearchPage.clickOpenSubprocessesButton(0);
        processSearchPage.checkSubprocesses(processSearchPage.getSubprocesses());
        processSearchPage.waitUntilNeededStatus(STATUS_FAILED);
        Process failedProcess = processSearchPage.getInfoForMainProcesses().get(0);
        processSearchPage.checkMainProcess(failedProcess, fttbProcess, today);
        processSearchPage.checkProcessStatus(failedProcess.getState(), STATUS_FAILED);
    }

    @Test(dependsOnMethods = "searchFailedFttbProcess", priority = 1)
    @TmsLink("DIGIHUB-122535")
    @Description("Restore failed fttb process in Access Process Management UI")
    public void restoreFailedFttbProcess() throws Exception {
        ProcessSearchPage processSearchPage = new ProcessSearchPage().openPage();
        processSearchPage.validateUrl();
        processSearchPage.searchProcessesByDevice(fttbProcess)
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
        System.out.println("expected threeHoursAgo = " + threeHoursAgo);
        System.out.println("expected today = " + today);
        System.out.println("actual oldestProcess = " + oldestProcess);
        System.out.println("actual oldestHour = " + oldestProcess);
        assertTrue(processSearchPage.compareTime(oldestProcess, oldestHour, threeHoursAgo, today), "Oldest start time is not correct");
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
        assertTrue(processSearchPage.compareDates(oldestDate, dayAgo), "Oldest start time is not correct");
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
        assertTrue(processSearchPage.compareDates(oldestDate, weekAgo), "Oldest start time is not correct");
    }

    @Test
    @TmsLink("DIGIHUB-159865")
    @Description("Search for failed NE3 networkswitching process")
    public void searchAndRestoreFailedFtthNetworkswitchingProcess() throws Exception {
        accessLineRiRobot.clearDatabase();
        networkSwitchingRobot.clearDatabase();

        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "PartialPortSwitchingFailed"))
                .addSealAccessLineConfigNegativeMock()
                .build()
                .publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        accessLineRiRobot.fillDatabaseForNetworkSwitching(endSz_49_30_179_76H1_3_0, endSz_49_911_1100_76H1_1_0);
        int numberOfAccessLinesForSwitching = 3;
        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage
                .clickPartialPortPreparation(endSz_49_30_179_76H1_3_0, endSz_49_911_1100_76H1_1_0)
                .selectHomeIdsForPreparation(numberOfAccessLinesForSwitching);
        networkSwitchingPage.clickPrepareButton();
        ProcessSearchPage processSearchPage = new ProcessSearchPage().openPage();
        processSearchPage.validateUrl();
        processSearchPage.searchProcessesByDevice(networkSwitchingProcess)
                .clickSearchButton();
        Process runningProcess = processSearchPage.getInfoForMainProcesses().get(0);
        processSearchPage.checkMainProcess(runningProcess, networkSwitchingProcess, today);
        processSearchPage.checkProcessStatus(runningProcess.getState(), STATUS_RUNNING);
        processSearchPage.checkTableHeaders(processSearchPage.getTableHeaders());
        processSearchPage.checkTableMessagePattern(processSearchPage.getTableMessage());
        processSearchPage.clickOpenSubprocessesButton(0)
                        .checkSubprocesses(processSearchPage.getSubprocesses());
        processSearchPage.waitUntilNeededStatus(STATUS_FAILED);
        mappingsContext.deleteStubs();
        Process failedProcess = processSearchPage.getInfoForMainProcesses().get(0);
        processSearchPage.checkMainProcess(failedProcess, networkSwitchingProcess, today);
        processSearchPage.checkProcessStatus(failedProcess.getState(), STATUS_FAILED);
        Process initialProcess = processSearchPage.getInfoForMainProcesses().get(0);
        processSearchPage.clickRestartButton(0)
                         .checkConfirmationDialog();
        processSearchPage.clickOkInConfirmationDialog();
        Process restoredProcess = processSearchPage.getInfoForMainProcesses().get(0);
        processSearchPage.checkMainProcess(restoredProcess, initialProcess, today);
        processSearchPage.checkProcessStatus(restoredProcess.getState(), STATUS_RUNNING);
        processSearchPage.waitUntilNeededStatus(STATUS_FINISHED);
    }

}
