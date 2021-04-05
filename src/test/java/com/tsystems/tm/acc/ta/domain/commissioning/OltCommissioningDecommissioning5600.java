package com.tsystems.tm.acc.ta.domain.commissioning;


import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.OltCommissioningRobot;
import com.tsystems.tm.acc.ta.robot.osr.OltDeCommissioningRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;

import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.attachEventsToAllureReport;

@ServiceLog({
        NETWORK_LINE_PROFILE_MANAGEMENT_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        WG_ACCESS_PROVISIONING_MS,
        OLT_RESOURCE_INVENTORY_MS,
        EA_EXT_ROUTE_MS,
        LINE_ID_GENERATOR_MS,
        ACCESS_LINE_MANAGEMENT,
        OLT_DISCOVERY_MS
})
public class OltCommissioningDecommissioning5600 extends GigabitTest {

    private static final String START_PON_SLOT = "1"; //pon slot from SealMapper

    private OsrTestContext context = OsrTestContext.get();
    private OltCommissioningRobot oltCommissioningRobot = new OltCommissioningRobot();
    private OltDeCommissioningRobot oltDeCommissioningRobot = new OltDeCommissioningRobot();
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

        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "OltCommissioningDecommissioning5600"))
                .addSealMock(oltDeviceManual)
                .addSealMock(oltDeviceAutomatic)
                .addPslMock(oltDeviceManual)
                .addPslMock(oltDeviceAutomatic)
                .build();

        mappingsContext.publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());
    }

    @AfterClass
    public void teardown() {
        mappingsContext.close();
        mappingsContext
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());

        oltCommissioningRobot.restoreOsrDbState();
        oltCommissioningRobot.clearResourceInventoryDataBase(oltDeviceManual);
        oltCommissioningRobot.clearResourceInventoryDataBase(oltDeviceAutomatic);
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());
    }

    @Test(description = "Olt-Commissioning (device : MA5600T) automatically case")
    @TmsLink("DIGIHUB-44733")
    @Description("Olt-Commissioning (MA5600T) automatically case")
    @Owner("DL-T-Magic.Mercury@telekom.de, DL_T-Magic.U-Piter@t-systems.com")
    public void automaticallyOltCommissioning() {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());
        oltCommissioningRobot.startAutomaticOltCommissioning(oltDeviceAutomatic);
        oltCommissioningRobot.checkOltCommissioningResult(oltDeviceAutomatic);
    }

    @Test(dependsOnMethods = "automaticallyOltCommissioning", description = "Olt De-Commissioning (device : MA5600T) automatically case")
    @TmsLink("DIGIHUB-98821")
    @Description("Olt-Decommissioning (MA5600T) automatically case")
    @Owner("DL-T-Magic.Mercury@telekom.de, DL_T-Magic.U-Piter@t-systems.com")
    public void automaticallyOltDeCommissioning() throws InterruptedException {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());
        oltDeCommissioningRobot.startOltDecommissioningAfterAutoCommissioning(oltDeviceAutomatic);
        oltDeCommissioningRobot.checkOltDeCommissioningResult(oltDeviceAutomatic, START_PON_SLOT);
    }

    @Test(description = "Olt-Commissioning (device : MA5600T) manually case")
    @TmsLink("DIGIHUB-45656")
    @Description("Olt-Commissioning (MA5600T) manually case")
    @Owner("DL-T-Magic.Mercury@telekom.de, DL_T-Magic.U-Piter@t-systems.com")
    public void manuallyOltCommissioning() {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());
        oltCommissioningRobot.startManualOltCommissioning(oltDeviceManual);
        oltCommissioningRobot.checkOltCommissioningResult(oltDeviceManual);
    }

    @Test(dependsOnMethods = "manuallyOltCommissioning", description = "Olt-Decommissioning (device : MA5600T) manually case")
    @TmsLink("DIGIHUB-98823")
    @Description("Olt-Decommissioning (MA5600T) manually case")
    @Owner("DL-T-Magic.Mercury@telekom.de, DL_T-Magic.U-Piter@t-systems.com")
    public void manuallyOltDeCommissioning() throws InterruptedException {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());
        oltDeCommissioningRobot.startOltDecommissioningAfterManualCommissioning(oltDeviceManual);
        oltDeCommissioningRobot.checkOltDeCommissioningResult(oltDeviceManual, START_PON_SLOT);
    }
}
