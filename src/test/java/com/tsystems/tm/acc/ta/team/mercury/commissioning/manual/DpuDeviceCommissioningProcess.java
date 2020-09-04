package com.tsystems.tm.acc.ta.team.mercury.commissioning.manual;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.dpudevice.DpuDeviceCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.pages.osr.dpucommissioning.DpuCreatePage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class DpuDeviceCommissioningProcess extends BaseTest {

    private static final Integer HTTP_CODE_OK_200 = 200;
    private OltResourceInventoryClient oltResourceInventoryClient;
    private DpuDevice dpuDevice;


    @BeforeMethod
    public void init() {

    }

    @AfterMethod
    public void cleanUp() {
    }

    @Test(description = "DIGIHUB-53694 Manual commissioning for MA5800 with DTAG user on team environment")
    @TmsLink("DIGIHUB-53694") // Jira Id for this test in Xray
    @Description("Perform manual commissioning for not discovered MA5800 device as DTAG user")
    public void createDpu() throws InterruptedException {

        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiDTAG);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());
        dpuDevice = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.EndSz_49_30_179_71G0_SDX2221);

        String endSz = dpuDevice.getEndsz();
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();

        oltSearchPage.searchNotDiscoveredByEndSz(endSz);
        Thread.sleep(1000);
        DpuCreatePage dpuCreatePage = oltSearchPage.pressCreateDpuButton();
        dpuCreatePage.validateUrl();
        dpuCreatePage.startDpuCreation(dpuDevice);

        Thread.sleep(50000);
    }
}
