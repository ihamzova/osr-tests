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
import com.tsystems.tm.acc.ta.ui.selenide.SelenideScreenshotServiceKt;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;

@Slf4j
@ServiceLog({ANCP_CONFIGURATION_MS, OLT_DISCOVERY_MS, OLT_RESOURCE_INVENTORY_MS, OLT_UPLINK_MANAGEMENT_MS, PSL_ADAPTER_MS, PSL_TRANSFORMER_MS, OLT_COMMISSIONING_MS})
public class AdtranOltZtCommissioning extends GigabitTest {

  final String STUB_GROUP_ID = "AdtranOltZtCommissioning";
  final String ACID = "21212";
  final Integer STATE_BIT_ERROR = 2;
  final Integer STATE_INSTALL_OLT = 201358588;
  final Integer OLT_COMMISSIONING_STARTED =  209714428;
  final Integer STATE_FINISHED_SUCCESS = 268434685;

  private final ZtCommissioningRobot ztCommissioningRobot = new ZtCommissioningRobot();
  private final OltCommissioningRobot oltCommissioningRobot = new OltCommissioningRobot();

  private OsrTestContext context = OsrTestContext.get();
  private OltDevice oltDevice_76H8 = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_911_85_76H8_SDX_6320);
  private OltDevice oltDevice_76H9 = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_911_85_76H9_SDX_6320);
  private OltDevice oltDevice_76HA = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_911_85_76HA_SDX_6320);

  private final WireMockMappingsContext mappingsContextOsr = new WireMockMappingsContext(WireMockFactory.get(), STUB_GROUP_ID);
  private final WireMockMappingsContext mappingsContextTeam = new WireMockMappingsContext(WireMockFactory.get(), STUB_GROUP_ID);
  private WireMockMappingsContext mappingsContext; // Dynamic creation and deletion of wiremock stubs during test execution

  @BeforeClass
  public void init() {
    // WireMockFactory.get().resetToDefaultMappings();
    List<OltDevice> olts = Arrays.asList(oltDevice_76H8, oltDevice_76H9, oltDevice_76HA);

    olts.forEach(olt ->
            new OsrWireMockMappingsContextBuilder(mappingsContextOsr)
                    .addSealMock(olt)
                    .addPslMock(olt)
                    .addPslMockXML(olt)
                    .addDhcp4oltGetOltMock(olt)
                    .addDhcp4oltGetBngMock(olt)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport())
    );
    olts.forEach(olt ->
            new MercuryWireMockMappingsContextBuilder(mappingsContextTeam)
                    .addPonInventoryMock(olt)
                    .addRebellUewegeMock(olt)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport())
    );
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

    if (mappingsContext != null) {
      mappingsContext.close();
      mappingsContext
              .eventsHook(saveEventsToDefaultDir())
              .eventsHook(attachEventsToAllureReport());
    }
  }


  @Test(description = "DIGIHUB-xxxx Zero touch commissioning process for SDX 6320-16 device as DTAG user")
  @TmsLink("DIGIHUB-xxxxx") // Jira Id for this test in Xray
  @Description("Perform the zero touch commissioning process for SDX 6320-16 device as DTAG user on team environment")
  public void adtranOltZtCommissioningManualTriggered() {

    ztCommissioningRobot.clearResourceInventoryDataBase(oltDevice_76H8.getEndsz());
    ztCommissioningRobot.clearZtCommisioningData(oltDevice_76H8.getEndsz());

    Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltMobileUi);
    setCredentials(loginData.getLogin(), loginData.getPassword());
    ztCommissioningRobot.startZtCommissioning(oltDevice_76H8, ACID);
    ztCommissioningRobot.verifyZtCommisioningState(oltDevice_76H8.getEndsz(),STATE_INSTALL_OLT);

    addOltBasicConfigurationMock(oltDevice_76H8, false);  // Missing connection between OTL and EMS.
    ztCommissioningRobot.continueZtCommissioningWaitForError();  // manual triggered oltBasicConfiguration step
    SelenideScreenshotServiceKt.takeScreenshot();
    ztCommissioningRobot.verifyZtCommisioningState(oltDevice_76H8.getEndsz(),STATE_INSTALL_OLT | STATE_BIT_ERROR);
    mappingsContext.close();

    addOltBasicConfigurationMock(oltDevice_76H8, true);
    ztCommissioningRobot.continueZtCommissioning();  // Repetition of the oltBasicConfiguration step from the Mobile-UI
    ztCommissioningRobot.waitZtCommissioningProcessIsFinished();
    mappingsContext.close();

    ztCommissioningRobot.verifyZtCommisioningState(oltDevice_76H8.getEndsz(), STATE_FINISHED_SUCCESS);

    oltCommissioningRobot.checkUplink(oltDevice_76H8);

  }

  @Test(description = "DIGIHUB-xxxx Zero touch commissioning process for SDX 6320-16 device as DTAG user")
  @TmsLink("DIGIHUB-xxxxx") // Jira Id for this test in Xray
  @Description("Perform the zero touch commissioning process for SDX 6320-16 device as DTAG user on team environment")
  public void adtranOltZtCommissioningEventTriggered() {

    ztCommissioningRobot.clearResourceInventoryDataBase(oltDevice_76H9.getEndsz());
    ztCommissioningRobot.clearZtCommisioningData(oltDevice_76H9.getEndsz());

    Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltMobileUi);
    setCredentials(loginData.getLogin(), loginData.getPassword());
    ztCommissioningRobot.startZtCommissioning(oltDevice_76H9, ACID);
    ztCommissioningRobot.verifyZtCommisioningState(oltDevice_76H9.getEndsz(),STATE_INSTALL_OLT);
    ztCommissioningRobot.sendZtCommisioningSealEvent(oltDevice_76H9.getEndsz(), "offline");
    ztCommissioningRobot.chekcForceProceedLinkExist();
    ztCommissioningRobot.verifyZtCommisioningState(oltDevice_76H9.getEndsz(),STATE_INSTALL_OLT);
    ztCommissioningRobot.sendZtCommisioningSealEvent(oltDevice_76H9.getEndsz(), "online"); // event triggered oltBasicConfiguration
    ztCommissioningRobot.waitZtCommissioningProcessIsFinished();
    ztCommissioningRobot.verifyZtCommisioningState(oltDevice_76H9.getEndsz(), STATE_FINISHED_SUCCESS);

    oltCommissioningRobot.checkUplink(oltDevice_76H9);
  }


  @Test(dependsOnMethods = "adtranOltZtCommissioningManualTriggered", description = "DIGIHUB-xxxx Zero touch commissioning process for SDX 6320-16 device as DTAG user")
  @TmsLink("DIGIHUB-xxxxx") // Jira Id for this test in Xray
  @Description("Perform the zero touch commissioning process for SDX 6320-16 device as DTAG user on team environment")
  public void adtranOltZtCommissioningSerialNumberExist()
  {
    ztCommissioningRobot.clearResourceInventoryDataBase(oltDevice_76HA.getEndsz());
    ztCommissioningRobot.clearZtCommisioningData(oltDevice_76HA.getEndsz());

    Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltMobileUi);
    setCredentials(loginData.getLogin(), loginData.getPassword());
    String serialNumber =  oltDevice_76HA.getSeriennummer();
    oltDevice_76HA.setSeriennummer(oltDevice_76H8.getSeriennummer()); // Serial number already exists in olt-ri
    ztCommissioningRobot.startZtCommissioningWithError(oltDevice_76HA, ACID);
    ztCommissioningRobot.getZtCommisioningState(oltDevice_76HA.getEndsz());
    SelenideScreenshotServiceKt.takeScreenshot();
    oltDevice_76HA.setSeriennummer(serialNumber);
    ztCommissioningRobot.restartZtCommissioning(oltDevice_76HA);
    ztCommissioningRobot.verifyZtCommisioningState(oltDevice_76HA.getEndsz(),STATE_INSTALL_OLT);
    ztCommissioningRobot.sendZtCommisioningSealEvent(oltDevice_76HA.getEndsz(), "online"); // event triggered oltBasicConfiguration
    ztCommissioningRobot.waitZtCommissioningProcessIsFinished();
    ztCommissioningRobot.verifyZtCommisioningState(oltDevice_76HA.getEndsz(), STATE_FINISHED_SUCCESS);

    oltCommissioningRobot.checkUplink(oltDevice_76HA);
  }


  private void addOltBasicConfigurationMock(OltDevice oltDevice, boolean success) {
    if (success) {
      mappingsContext = new OsrWireMockMappingsContextBuilder( new WireMockMappingsContext(WireMockFactory.get(), "OltBasicConfiguration"))
              .addOltBasicConfigurationMock(oltDevice)
              .build()
              .publish()
              .publishedHook(savePublishedToDefaultDir())
              .publishedHook(attachStubsToAllureReport());
    } else {
      mappingsContext= new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "OltBasicConfiguration"))
              .addOltBasicConfigurationErrorMock(oltDevice)
              .build()
              .publish()
              .publishedHook(savePublishedToDefaultDir())
              .publishedHook(attachStubsToAllureReport());
    }
  }
}