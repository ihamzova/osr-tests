package com.tsystems.tm.acc.ta.team.upiter.processmanagement;

import com.tsystems.tm.acc.data.upiter.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.upiter.models.process.ProcessCase;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.Process;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.pages.osr.processmanagement.ProcessSearchPage;
import com.tsystems.tm.acc.ta.robot.osr.WgAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;

@ServiceLog(ACCESS_PROCESS_MANAGEMENT_UI)
@ServiceLog(ACCESS_PROCESS_MANAGEMENT_BFF)
@ServiceLog(ACCESS_PROCESS_MANAGEMENT)

public class ProcessesSearchTest extends BaseTest {

    private static final Integer LATENCY_FOR_PROVISIONING_TO_FAIL = 120_000;
    WgAccessProvisioningRobot wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
    private UpiterTestContext context = UpiterTestContext.get();
    private Process process;
    private String processUuid;

    @BeforeClass
    public void init() throws InterruptedException {
        process = context.getData().getProcessDataProvider().get(ProcessCase.processesData);
        processUuid = wgAccessProvisioningRobot.startPortProvisioningAndGetProcessId(process).toString();
        Thread.sleep(LATENCY_FOR_PROVISIONING_TO_FAIL);
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOTelekomNSOOpsRW);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());
    }

    @Test
    @TmsLink("DIGIHUB-45514")
    @Description("Search processes by EndSZ in Access Process Management UI")
    public void searchProcessesByEndSzTest() {
        ProcessSearchPage processSearchPage = ProcessSearchPage.openPage();
        processSearchPage.validateUrl();
        processSearchPage.searchProcessesByDevice(process)
                .clickSearchButton();

        processSearchPage.checkTableHeaders(processSearchPage.getTableHeaders());
        processSearchPage.checkTableMessagePattern(processSearchPage.getTableMessage());
        processSearchPage.checkPaginationSizes(processSearchPage.getPaginatorSizes());

        processSearchPage.clickFirstRowExpansion();
        Process foundProcess = new Process();
        processSearchPage.checkRowExpansionData(foundProcess, process);
    }

    @Test
    @TmsLink("DIGIHUB-44044")
    @Description("Search processes by ProcessId in Access Process Management UI")
    public void searchProcessesByProcessIdTest() {

        ProcessSearchPage processSearchPage = ProcessSearchPage.openPage();
        processSearchPage.validateUrl();
        processSearchPage.searchProcessesByProcessId(processUuid)
                .clickSearchButton();

        processSearchPage.checkTableHeaders(processSearchPage.getTableHeaders());
        processSearchPage.checkTableMessagePattern(processSearchPage.getTableMessage());
        processSearchPage.checkPaginationSizes(processSearchPage.getPaginatorSizes());
        processSearchPage.clickFirstRowExpansion();
        Process foundProcess = processSearchPage.collectDataFromRowExpansion();
        processSearchPage.checkRowExpansionData(foundProcess, process);
    }

    @Test
    @TmsLink("DIGIHUB-44044")
    @Description("Search processes by ProcessId in Access Process Management UI and restore it")
    public void restoreProcessTest() {
        ProcessSearchPage processSearchPage = new ProcessSearchPage().openPage();
        processSearchPage.validateUrl();
        processSearchPage.searchProcessesByProcessId(processUuid).clickSearchButton();

        Process initialProcess = processSearchPage.getTableLines().get(0);

        processSearchPage.clickRestartForFirstProcess();
        processSearchPage.checkConfirmationDialog();
        processSearchPage.clickOkInConfirmationDialog().clickSearchButton();

        Process restoredProcess = processSearchPage.getTableLines().get(0);

        processSearchPage.checkRestoredProcess(restoredProcess, initialProcess);
    }
}
