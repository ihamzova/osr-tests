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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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
    private OltDevice oltDeviceManual;
    private OltDevice oltDeviceAutomatic;

    @BeforeClass
    public void init() {
        oltCommissioningRobot.restoreOsrDbState();

        OsrTestContext context = OsrTestContext.get();
        oltDeviceManual = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76HC_MA5600);
        wiremockRobot.setUpSealWiremock(oltDeviceManual);
        wiremockRobot.setUpPslWiremock(oltDeviceManual);
        oltCommissioningRobot.clearResourceInventoryDataBase(oltDeviceManual);

        oltDeviceAutomatic = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76HD_MA5600);
        wiremockRobot.setUpSealWiremock(oltDeviceAutomatic);
        wiremockRobot.setUpPslWiremock(oltDeviceAutomatic);
        oltCommissioningRobot.clearResourceInventoryDataBase(oltDeviceAutomatic);

        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());
    }

    @AfterClass
    public void teardown() {
        oltCommissioningRobot.restoreOsrDbState();

        wiremockRobot.tearDownWiremock(oltDeviceManual.getSealWiremockUuid());
        wiremockRobot.tearDownWiremock(oltDeviceManual.getPslWiremockUuid());
        oltCommissioningRobot.clearResourceInventoryDataBase(oltDeviceManual);

        wiremockRobot.tearDownWiremock(oltDeviceAutomatic.getSealWiremockUuid());
        wiremockRobot.tearDownWiremock(oltDeviceAutomatic.getPslWiremockUuid());
        oltCommissioningRobot.clearResourceInventoryDataBase(oltDeviceAutomatic);
    }

    @Test(description = "Olt-Commissioning (device : MA5600T) automatically case")
    @TmsLink("DIGIHUB-44733")
    @Description("Olt-Commissioning (MA5600T) automatically case")
    @Owner("dmitrii.krylov@t-systems.com")
    public void automaticallyOltCommissioning() {
        oltCommissioningRobot.startAutomaticOltCommissioning(oltDeviceManual);
        oltCommissioningRobot.checkOltCommissioningResult(oltDeviceManual);
    }

    @Test(description = "Olt-Commissioning (device : MA5600T) manually case")
    @TmsLink("DIGIHUB-45656")
    @Description("Olt-Commissioning (MA5600T) manually case")
    @Owner("dmitrii.krylov@t-systems.com")
    public void manuallyOltCommissioning() {
        oltCommissioningRobot.startManualOltCommissioning(oltDeviceAutomatic);
        oltCommissioningRobot.checkOltCommissioningResult(oltDeviceAutomatic);
    }
}
