package com.tsystems.tm.acc.ta.domain.accessprocessmanagement;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.process.ProcessCase;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.Process;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.pages.osr.accessprocessmanagement.ProcessSearchPage;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;

@ServiceLog({
        ACCESS_PROCESS_MANAGEMENT_UI,
        ACCESS_PROCESS_MANAGEMENT_BFF,
        ACCESS_PROCESS_MANAGEMENT
})

public class AccessProcessManagementUi extends GigabitTest {
    private OsrTestContext context = OsrTestContext.get();
    private Process process;
    String today;

    @BeforeClass
    public void init() {
        process = context.getData().getProcessDataProvider().get(ProcessCase.searchProcessByEndSz);
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.AccessProcessManagementUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        today = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("dd MM"));
        System.out.println(today);
    }

    @Test
    @TmsLink("DIGIHUB-117842")
    @Description("Search for OLT Commissioning Processes in APM UI")
    public void searchOltCommissioningProcessesTest() throws Exception {
        ProcessSearchPage processSearchPage = ProcessSearchPage.openPage();
        processSearchPage.validateUrl();
        processSearchPage.searchProcessesByDevice(process)
                .clickSearchButton();

        processSearchPage.checkTableHeaders(processSearchPage.getTableHeaders());
        processSearchPage.checkTableMessagePattern(processSearchPage.getTableMessage());

        processSearchPage.sortTableByStartTimeDescending();
        Process foundProcess = processSearchPage.getInfoForMainProcesses().get(0);
        processSearchPage.checkMainProcess(foundProcess, process, today);

        processSearchPage.clickOpenSubprocessesButton(0);
        processSearchPage.checkSubprocesses(processSearchPage.getSubprocesses());
    }
}
