package com.tsystems.tm.acc.ta.domain.commissioning;

import com.tsystems.tm.acc.data.models.credentials.Credentials;
import com.tsystems.tm.acc.data.models.nvt.Nvt;
import com.tsystems.tm.acc.data.models.oltdevice.OltDevice;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.nvt.NvtCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.data.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.OltCommissioningRobot;
import com.tsystems.tm.acc.ta.robot.osr.WiremockRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.RHSSOAuthListener;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class OltCommissioning5600 extends BaseTest {
    private OsrTestContext context = OsrTestContext.get();
    private OltCommissioningRobot oltCommissioningRobot = new OltCommissioningRobot();
    private WiremockRobot wiremockRobot = new WiremockRobot();

    @AfterClass
    public void teardown() {
        oltCommissioningRobot.restoreOsrDbState();
    }

    @BeforeMethod
    public void prepareData() {
        oltCommissioningRobot.restoreOsrDbState();

        File stubsPath = Paths.get(System.getProperty("user.dir"), "target/order/stubs").toFile();
        List<OltDevice> devices = Collections.singletonList(context.getData().getOltDeviceDataProvider().get(OltDeviceCase.FSZ_76HA));
        wiremockRobot.createMocksForPSL(stubsPath, devices);
        wiremockRobot.createMocksForSEAL(stubsPath, devices);
        // Upload mock to the server may be? They are not being used at the moment

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
