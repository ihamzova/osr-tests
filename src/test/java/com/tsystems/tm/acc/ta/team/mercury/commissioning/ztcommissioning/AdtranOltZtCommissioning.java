package com.tsystems.tm.acc.ta.team.mercury.commissioning.ztcommissioning;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.data.mercury.wiremock.MercuryWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.ZtCommissioningRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;

@Slf4j
@ServiceLog({ANCP_CONFIGURATION_MS, OLT_DISCOVERY_MS, OLT_RESOURCE_INVENTORY_MS, OLT_UPLINK_MANAGEMENT_MS, PSL_ADAPTER_MS, PSL_TRANSFORMER_MS, OLT_COMMISSIONING_MS})
public class AdtranOltZtCommissioning extends GigabitTest {

  private final String ACID = "21212";

  private OsrTestContext context;
  private ZtCommissioningRobot ztCommissioningRobot = new ZtCommissioningRobot();
  private OltDevice oltDevice;

  private WireMockMappingsContext mappingsContextOsr;
  private WireMockMappingsContext mappingsContextTeam;

  @BeforeClass
  public void init() {
    context = OsrTestContext.get();
    oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_911_85_76H8_SDX_6320);
    ztCommissioningRobot.clearResourceInventoryDataBase(oltDevice.getEndsz());
    ztCommissioningRobot.clearZtCommisioningData(oltDevice.getEndsz());

    mappingsContextOsr = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "AdtranOltZtCommissioning"))
            .addSealMock(oltDevice)
            .addPslMock(oltDevice)
            .addPslMockXML(oltDevice)
            .build()
            .publish()
            .publishedHook(savePublishedToDefaultDir())
            .publishedHook(attachStubsToAllureReport());

    mappingsContextTeam = new MercuryWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "AdtranOltZtCommissioning"))
            .addPonInventoryMock(oltDevice)
            .addRebellUewegeMock(oltDevice)
            .build()
            .publish()
            .publishedHook(savePublishedToDefaultDir())
            .publishedHook(attachStubsToAllureReport());

  }

  @AfterClass
  public void cleanUp() {

    mappingsContextOsr.close();
    mappingsContextOsr
            .eventsHook(saveEventsToDefaultDir())
            .eventsHook(attachEventsToAllureReport());

    mappingsContextTeam.close();
    mappingsContextTeam
            .eventsHook(saveEventsToDefaultDir())
            .eventsHook(attachEventsToAllureReport());
  }


  @Test(description = "DIGIHUB-104216 Manual commissioning for not discovered SDX 6320-16 device as DTAG user")
  @TmsLink("DIGIHUB-104216") // Jira Id for this test in Xray
  @Description("Perform zero touch commissioning for not discovered SDX 6320-16 device as DTAG user on team environment")
  public void adtranOltZtCommissioningDTAG() {

    Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltMobileUi);
    setCredentials(loginData.getLogin(), loginData.getPassword());
    ztCommissioningRobot.startZtCommissioning(oltDevice, ACID);

  }
}