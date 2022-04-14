package com.tsystems.tm.acc.ta.team.mercury.commissioning.ztcommissioning;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.data.mercury.wiremock.MercuryWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.OltCommissioningRobot;
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


  final String ACID = "21212";
  final Integer STATE_INSTALL_OLT = 201358588;
  final Integer STATE_FINISHED_SUCCESS = 268434685;

  private OsrTestContext context;
  private final ZtCommissioningRobot ztCommissioningRobot = new ZtCommissioningRobot();
  private final OltCommissioningRobot oltCommissioningRobot = new OltCommissioningRobot();
  private OltDevice oltDevice;

  private WireMockMappingsContext mappingsContextOsr;
  private WireMockMappingsContext mappingsContextTeam;
  private WireMockMappingsContext mappingsContext; // Dynamic creation and deletion of wiremock stubs during test execution

  @BeforeClass
  public void init() {
   // WireMockFactory.get().resetToDefaultMappings();

    context = OsrTestContext.get();
    oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_911_85_76H8_SDX_6320);

    mappingsContextOsr = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "AdtranOltZtCommissioning"))
            .addSealMock(oltDevice)
            .addPslMock(oltDevice)
            .addPslMockXML(oltDevice)
            .addDhcp4oltGetOltMock(oltDevice)
            .addDhcp4oltGetBngMock(oltDevice)
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


  @Test(description = "DIGIHUB-xxxx Zero touch commissioning process for SDX 6320-16 device as DTAG user")
  @TmsLink("DIGIHUB-xxxxx") // Jira Id for this test in Xray
  @Description("Perform the zero touch commissioning process for SDX 6320-16 device as DTAG user on team environment")
  public void adtranOltZtCommissioningManualTriggered() {

    ztCommissioningRobot.clearResourceInventoryDataBase(oltDevice.getEndsz());
    ztCommissioningRobot.clearZtCommisioningData(oltDevice.getEndsz());

    Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltMobileUi);
    setCredentials(loginData.getLogin(), loginData.getPassword());
    ztCommissioningRobot.startZtCommissioning(oltDevice, ACID);
    ztCommissioningRobot.verifyZtCommisioningState(oltDevice.getEndsz(),STATE_INSTALL_OLT);

    addOltBasicConfigurationMock(oltDevice, false);  // Missing connection between OTL and EMS.
    ztCommissioningRobot.continueZtCommissioningWaitForError();  // manual triggered oltBasicConfiguration step
    ztCommissioningRobot.verifyZtCommisioningState(oltDevice.getEndsz(),STATE_INSTALL_OLT | 2);
    mappingsContext.close();

    addOltBasicConfigurationMock(oltDevice, true);
    ztCommissioningRobot.continueZtCommissioning();  // Repetition of the oltBasicConfiguration step from the Mobile-UI
    ztCommissioningRobot.waitZtCommissioningProcessIsFinished();
    mappingsContext.close();

    ztCommissioningRobot.verifyZtCommisioningState(oltDevice.getEndsz(), STATE_FINISHED_SUCCESS);

    oltCommissioningRobot.checkUplink(oltDevice);

  }

  @Test(description = "DIGIHUB-xxxx Zero touch commissioning process for SDX 6320-16 device as DTAG user")
  @TmsLink("DIGIHUB-xxxxx") // Jira Id for this test in Xray
  @Description("Perform the zero touch commissioning process for SDX 6320-16 device as DTAG user on team environment")
  public void adtranOltZtCommissioningEventTriggered() {

    ztCommissioningRobot.clearResourceInventoryDataBase(oltDevice.getEndsz());
    ztCommissioningRobot.clearZtCommisioningData(oltDevice.getEndsz());

    Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltMobileUi);
    setCredentials(loginData.getLogin(), loginData.getPassword());
    ztCommissioningRobot.startZtCommissioning(oltDevice, ACID);
    ztCommissioningRobot.verifyZtCommisioningState(oltDevice.getEndsz(),STATE_INSTALL_OLT);
    ztCommissioningRobot.sendZtCommisioningSealEvent(oltDevice.getEndsz()); // event triggered oltBasicConfiguration
    ztCommissioningRobot.waitZtCommissioningProcessIsFinished();
    ztCommissioningRobot.verifyZtCommisioningState(oltDevice.getEndsz(), STATE_FINISHED_SUCCESS);

    oltCommissioningRobot.checkUplink(oltDevice);
  }

  private void addOltBasicConfigurationMock(OltDevice oltDevice, boolean success) {

    if (success) {
      mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "OltBasicConfiguration"))
              .addOltBasicConfigurationMock(oltDevice)
              .build()
              .publish()
              .publishedHook(savePublishedToDefaultDir())
              .publishedHook(attachStubsToAllureReport());
    } else {
      mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "OltBasicConfigurationError"))
              .addOltBasicConfigurationErrorMock(oltDevice)
              .build()
              .publish()
              .publishedHook(savePublishedToDefaultDir())
              .publishedHook(attachStubsToAllureReport());
    }
  }
}