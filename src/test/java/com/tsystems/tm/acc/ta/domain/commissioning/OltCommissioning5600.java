package com.tsystems.tm.acc.ta.domain.commissioning;


import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.OltCommissioningRobot;
import com.tsystems.tm.acc.ta.robot.osr.WiremockRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static com.tsystems.tm.acc.ta.team.upiter.common.UpiterConstants.*;

@ServiceLog(NETWORK_LINE_PROFILE_MANAGEMENT_MS)
@ServiceLog(ACCESS_LINE_RESOURCE_INVENTORY_MS)
@ServiceLog(WG_ACCESS_PROVISIONING_MS)
@ServiceLog(OLT_RESOURCE_INVENTORY_MS)
@ServiceLog(EA_EXT_ROUTE_MS)
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
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());
    }

    @Test(description = "Olt-Commissioning (device : MA5600T) automatically case")
    @TmsLink("DIGIHUB-44733")
    @Description("Olt-Commissioning (MA5600T) automatically case")
    @Owner("dmitrii.krylov@t-systems.com")
    public void automaticallyOltCommissioning() {
        OltDevice oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.FSZ_76HA);

        oltCommissioningRobot.startAutomaticOltCommissioning(oltDevice);
        oltCommissioningRobot.checkOltCommissioningResult(oltDevice);
    }

    @Test(description = "Olt-Commissioning (device : MA5600T) manually case")
    @TmsLink("DIGIHUB-45656")
    @Description("Olt-Commissioning (MA5600T) manually case")
    @Owner("dmitrii.krylov@t-systems.com")
    public void manuallyOltCommissioning() {
        OltDevice oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.FSZ_76HA);

        oltCommissioningRobot.startManualOltCommissioning(oltDevice);
        oltCommissioningRobot.checkOltCommissioningResult(oltDevice);
    }
}
