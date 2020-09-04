package com.tsystems.tm.acc.ta.domain.commissioning;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.dpudevice.DpuDeviceCase;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.DpuCommissioningUiRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

// Lutz: for Service Logging: Can we change definitions from UpiterConstants to OsrConstants?
//                      see: UpiterConstants
public class DpuCommissioningSDX2221 extends BaseTest {
    private OsrTestContext context = OsrTestContext.get();
    private DpuCommissioningUiRobot dpuCommissioningUiRobot = new DpuCommissioningUiRobot();
    private DpuDevice dpuDevice;

    @BeforeClass
    public void init() {

    }

    @AfterClass
    public void teardown() {

    }

    @Test(description = "DPU creation and DPU-Commissioning (device : SDX2221-16 TP-AC-MELT) case")
    @TmsLink("DIGIHUB-75965")
    @Description("DPU creation and DPU-Commissioning (device : SDX2221-16 TP-AC-MELT) case")
    @Owner("@t-systems.com")
    public void dpuCommissioning() {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());
        dpuDevice = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.EndSz_49_30_179_71G0_SDX2221);
        dpuCommissioningUiRobot.startDpuCommissioning(dpuDevice);
        dpuCommissioningUiRobot.checkDpuCommissioningResult(dpuDevice);
    }
}
