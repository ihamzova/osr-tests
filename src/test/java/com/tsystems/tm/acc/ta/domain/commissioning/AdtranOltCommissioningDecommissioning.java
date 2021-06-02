package com.tsystems.tm.acc.ta.domain.commissioning;


import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.OltCommissioningRobot;
import com.tsystems.tm.acc.ta.robot.osr.OltDeCommissioningRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.OLT_DISCOVERY_MS;
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

public class AdtranOltCommissioningDecommissioning extends GigabitTest {

  private OsrTestContext context = OsrTestContext.get();
  private OltCommissioningRobot oltCommissioningRobot = new OltCommissioningRobot();
  private OltDeCommissioningRobot oltDeCommissioningRobot = new OltDeCommissioningRobot();
  private AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
  private OltDevice oltDeviceManual;
  private OltDevice oltDeviceAutomatic;

  private WireMockMappingsContext mappingsContext;

  @BeforeClass
  public void init() {
    oltCommissioningRobot.restoreOsrDbState();

    OsrTestContext context = OsrTestContext.get();
    oltDeviceManual = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76HH_SDX_6320_16);
    oltDeviceAutomatic = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76HI_SDX_6320_16);

    oltCommissioningRobot.clearResourceInventoryDataBase(oltDeviceManual);
    oltCommissioningRobot.clearResourceInventoryDataBase(oltDeviceAutomatic);

    mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "OltCommissioningDecommissioningSDX_6320-16"))
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
  }

  @Test(description = "Adtran Olt-Commissioning (device : SDX 6320-16) automatically case")
  @TmsLink("DIGIHUB-xxxx")
  @Description("Olt-Commissioning (SDX 6320-16) automatically case")
  @Owner("DL-T-Magic.Mercury@telekom.de, DL_T-Magic.U-Piter@t-systems.com")
  public void automaticallyAdtranOltCommissioning() {
    Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
    setCredentials(loginData.getLogin(), loginData.getPassword());
    oltCommissioningRobot.startAutomaticOltCommissioning(oltDeviceAutomatic);
    oltCommissioningRobot.checkOltCommissioningResult(oltDeviceAutomatic);
  }

  @Test(dependsOnMethods = "automaticallyAdtranOltCommissioning", description = "Adtran Olt De-Commissioning (device : SDX 6320-16) automatically case")
  @TmsLink("DIGIHUB-xxxx")
  @Description("Olt-Decommissioning (SDX 6320-16) automatically case")
  @Owner("DL-T-Magic.Mercury@telekom.de, DL_T-Magic.U-Piter@t-systems.com")
  public void automaticallyAdtranOltDeCommissioning() throws InterruptedException {
    Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
    setCredentials(loginData.getLogin(), loginData.getPassword());
    oltDeCommissioningRobot.startAdtranOltDecommissioningAfterAutoCommissioning(oltDeviceAutomatic);
    oltDeCommissioningRobot.checkOltDeCommissioningResult(oltDeviceAutomatic, null);
    accessLineRiRobot.checkPhysicalResourceRefAfterAutoOltDecommissioning(oltDeviceAutomatic);
  }

  @Test(description = "Olt-Commissioning (device : SDX 6320-16) manually case")
  @TmsLink("DIGIHUB-xxxx")
  @Description("Olt-Commissioning (SDX 6320-16) manually case")
  @Owner("DL-T-Magic.Mercury@telekom.de, DL_T-Magic.U-Piter@t-systems.com")
  public void manuallyAdtranOltCommissioning() {
    Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
    setCredentials(loginData.getLogin(), loginData.getPassword());
    oltCommissioningRobot.startManualOltCommissioning(oltDeviceManual);
    oltCommissioningRobot.checkOltCommissioningResult(oltDeviceManual);
  }

  @Test(dependsOnMethods = "manuallyAdtranOltCommissioning", description = "Olt-Decommissioning (device : SDX 6320-16) manually case")
  @TmsLink("DIGIHUB-xxxx")
  @Description("Olt-Decommissioning (SDX 6320-16) manually case")
  @Owner("DL-T-Magic.Mercury@telekom.de, DL_T-Magic.U-Piter@t-systems.com")
  public void manuallyAdtranOltDeCommissioning() throws InterruptedException {
    Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
    setCredentials(loginData.getLogin(), loginData.getPassword());
    oltDeCommissioningRobot.startAdtranOltDecommissioningAfterAutoCommissioning(oltDeviceManual);
    oltDeCommissioningRobot.checkOltDeCommissioningResult(oltDeviceManual,null);
    accessLineRiRobot.checkPhysicalResourceRefAfterManualOltDecommissioning(oltDeviceManual);
  }
}
