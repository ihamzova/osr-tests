package com.tsystems.tm.acc.ta.domain.commissioning;


import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.OltCommissioningRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;

@ServiceLog(NETWORK_LINE_PROFILE_MANAGEMENT_MS)
@ServiceLog(ACCESS_LINE_RESOURCE_INVENTORY_MS)
@ServiceLog(WG_ACCESS_PROVISIONING_MS)
@ServiceLog(OLT_RESOURCE_INVENTORY_MS)
@ServiceLog(EA_EXT_ROUTE_MS)
@ServiceLog(LINE_ID_GENERATOR_MS)
@ServiceLog(ACCESS_LINE_MANAGEMENT)
@ServiceLog(OLT_DISCOVERY_MS)
public class OltCommissioning5600 extends BaseTest {
    private OsrTestContext context = OsrTestContext.get();
    private OltCommissioningRobot oltCommissioningRobot = new OltCommissioningRobot();
    private OltDevice oltDeviceManual;
    private OltDevice oltDeviceAutomatic;

    private WireMockMappingsContext mappingsContext;

    @BeforeClass
    public void init() {
        oltCommissioningRobot.restoreOsrDbState();

        OsrTestContext context = OsrTestContext.get();
        oltDeviceManual = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76HC_MA5600);
        oltDeviceAutomatic = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76HD_MA5600);

        oltCommissioningRobot.clearResourceInventoryDataBase(oltDeviceManual);
        oltCommissioningRobot.clearResourceInventoryDataBase(oltDeviceAutomatic);

        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "OltCommissioning5600"))
                .addSealMock(oltDeviceManual)
                .addSealMock(oltDeviceAutomatic)
                .addPslMock(oltDeviceManual)
                .addPslMock(oltDeviceAutomatic)
                .build();

        mappingsContext.publish();
    }

    @AfterClass
    public void teardown() {
        mappingsContext.close();

        oltCommissioningRobot.restoreOsrDbState();
        oltCommissioningRobot.clearResourceInventoryDataBase(oltDeviceManual);
        oltCommissioningRobot.clearResourceInventoryDataBase(oltDeviceAutomatic);
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());
    }

    @Test(description = "Olt-Commissioning (device : MA5600T) automatically case")
    @TmsLink("DIGIHUB-44733")
    @Description("Olt-Commissioning (MA5600T) automatically case")
    @Owner("dmitrii.krylov@t-systems.com")
    public void automaticallyOltCommissioning() {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());
        oltCommissioningRobot.startAutomaticOltCommissioning(oltDeviceAutomatic);
        oltCommissioningRobot.checkOltCommissioningResult(oltDeviceAutomatic);
    }

    @Test(description = "Olt-Commissioning (device : MA5600T) manually case")
    @TmsLink("DIGIHUB-45656")
    @Description("Olt-Commissioning (MA5600T) manually case")
    @Owner("dmitrii.krylov@t-systems.com")
    public void manuallyOltCommissioning() {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());
        oltCommissioningRobot.startManualOltCommissioning(oltDeviceManual);
        oltCommissioningRobot.checkOltCommissioningResult(oltDeviceManual);
    }
}
