package com.tsystems.tm.acc.ta.domain.commissioning;

import com.tsystems.tm.acc.data.models.credentials.Credentials;
import com.tsystems.tm.acc.data.models.nvt.Nvt;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.nvt.NvtCase;
import com.tsystems.tm.acc.ta.data.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.OltCommissioningRobot;
import com.tsystems.tm.acc.ta.ui.UITest;
import com.tsystems.tm.acc.ta.util.driver.RHSSOAuthListener;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class OltCommissioning5600 extends UITest {
    private OsrTestContext context = OsrTestContext.get();
    private OltCommissioningRobot oltCommissioningRobot = new OltCommissioningRobot();

    @AfterClass
    public void teardown() {
        oltCommissioningRobot.restoreOsrDbState();
    }

    @BeforeMethod
    public void prepareData() {
        oltCommissioningRobot.restoreOsrDbState();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
        RHSSOAuthListener.resetLoginData(loginData.getLogin(), loginData.getPassword());
    }

    @Test(description = "Olt-Commissioning (device : MA5600T) automatically case")
    @TmsLink("DIGIHUB-44733")
    @Description("Olt-Commissioning (MA5600T) automatically case")
    public void automaticallyOltCommissioning() {
        Nvt nvtForOltAutoCommissioning = context.getData().getNvtDataProvider().get(NvtCase.nvtForOltCommissioning);

        oltCommissioningRobot.startAutomaticOltCommissioning(nvtForOltAutoCommissioning);
        oltCommissioningRobot.checkOltCommissioningResult(nvtForOltAutoCommissioning);
    }

    @Test(description = "Olt-Commissioning (device : MA5600T) manually case")
    @TmsLink("DIGIHUB-37121")
    @Description("Olt-Commissioning (MA5600T) manually case")
    public void manuallyOltCommissioning() {
        Nvt nvtForOltManualCommissioning = context.getData().getNvtDataProvider().get(NvtCase.nvtForOltCommissioning);

        oltCommissioningRobot.startManualOltCommissioning(nvtForOltManualCommissioning);
        oltCommissioningRobot.checkOltCommissioningResult(nvtForOltManualCommissioning);
    }
}
