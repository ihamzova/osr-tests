package com.tsystems.tm.acc.ta.team.mercury.commissioning.manual;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.mercury.wiremock.MercuryWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.data.osr.enums.DevicePortLifeCycleStateUI;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.DeleteDevicePage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDetailsPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDiscoveryPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.ANCPSession;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.Device;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.UplinkDTO;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;

@Slf4j
@ServiceLog({ANCP_CONFIGURATION_MS, OLT_DISCOVERY_MS, OLT_RESOURCE_INVENTORY_MS})
public class AdtranOltDeviceCommissioningDecommissioningSDX6320_16_GFNW extends GigabitTest {

  private static final Integer WAIT_TIME_FOR_DEVICE_DELETION = 1_000;

  private OltResourceInventoryClient oltResourceInventoryClient;
  private OltDevice oltDevice;

  private WireMockMappingsContext mappingsContext;
  private WireMockMappingsContext mappingsContext2;

  @BeforeClass
  public void init() {
    oltResourceInventoryClient = new OltResourceInventoryClient();

    OsrTestContext context = OsrTestContext.get();
    oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76Z8_SDX_6320);

    mappingsContext = new OsrWireMockMappingsContextBuilder(WireMockFactory.get())
            .addSealMock(oltDevice)
            .addPslMock(oltDevice)
            .build();

    mappingsContext.publish()
            .publishedHook(savePublishedToDefaultDir())
            .publishedHook(attachStubsToAllureReport());

    mappingsContext2 = new MercuryWireMockMappingsContextBuilder(WireMockFactory.get()) //create mocks
            .addPonInventoryMock(oltDevice)
            .addAccessLineInventoryMock()
            .build();

    mappingsContext2.publish()                                              //inject in WM
            .publishedHook(savePublishedToDefaultDir())
            .publishedHook(attachStubsToAllureReport());

    String endSz = oltDevice.getEndsz();
    clearResourceInventoryDataBase(endSz);
  }

  @AfterClass
  public void cleanUp() {
    mappingsContext.close();
    mappingsContext
            .eventsHook(saveEventsToDefaultDir())
            .eventsHook(attachEventsToAllureReport());

    mappingsContext2.close();
    mappingsContext2
            .eventsHook(saveEventsToDefaultDir())
            .eventsHook(attachEventsToAllureReport());
  }

  @Test(description = "DIGIHUB-104219 Manual commissioning for not discovered SDX 6320-16 device as GFNW user")
  @TmsLink("DIGIHUB-104219") // Jira Id for this test in Xray
  @Description("Perform manual commissioning for not discovered SDX 6320-16 device as GFNW user on team environment")
  public void manuallyAdtranOltCommissioningGFNW() {

    OsrTestContext context = OsrTestContext.get();
    Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiGFNW);
    setCredentials(loginData.getLogin(), loginData.getPassword());

    OltDevice oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76Z8_SDX_6320);
    String endSz = oltDevice.getEndsz();
    clearResourceInventoryDataBase(endSz);
    OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
    oltSearchPage.validateUrl();

    oltSearchPage.searchNotDiscoveredByParameters(oltDevice);
    oltSearchPage.pressManualCommissionigButton();
    OltDiscoveryPage oltDiscoveryPage = new OltDiscoveryPage();
    oltDiscoveryPage.makeOltDiscovery();
    oltDiscoveryPage.saveDiscoveryResults();
    oltDiscoveryPage.openOltSearchPage();

    OltDetailsPage oltDetailsPage = oltSearchPage.searchDiscoveredOltByParameters(oltDevice);
    Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());
    oltDetailsPage.openPortView(oltDevice.getOltSlot());
    Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(oltDevice.getOltSlot(), oltDevice.getOltPort()), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

    oltDetailsPage.startUplinkConfiguration();
    oltDetailsPage.inputUplinkParameters(oltDevice);
    oltDetailsPage.saveUplinkConfiguration();
    oltDetailsPage.modifyUplinkConfiguration();

    oltDetailsPage.configureAncpSessionStart();
    oltDetailsPage.updateAncpSessionStatus();
    oltDetailsPage.checkAncpSessionStatus();
    Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.OPERATING.toString());
    oltDetailsPage.openPortView(oltDevice.getOltSlot());
    checkPortState(oltDevice, oltDetailsPage);

    checkDeviceSDX3620(endSz);
    checkUplink(endSz);
  }

  @Test(dependsOnMethods = "manuallyAdtranOltCommissioningGFNW", description = "Manual decommissioning for SDX 6320-16 device as GFNW user")
  @TmsLink("DIGIHUB-104221")
  @Description("Manual decommissioning for SDX 6320-16 device as GFNW user on team environment")
  public void manuallyAdtranOltDeCommissioningGFNW() throws InterruptedException {
    OsrTestContext context = OsrTestContext.get();
    Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiGFNW);
    setCredentials(loginData.getLogin(), loginData.getPassword());

    OltDevice oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76Z8_SDX_6320);
    String endSz = oltDevice.getEndsz();
    OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
    oltSearchPage.validateUrl();

    OltDetailsPage oltDetailsPage = oltSearchPage.searchDiscoveredOltByParameters(oltDevice);
    Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.OPERATING.toString());
    oltDetailsPage.openPortView(null);
    Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(null, oltDevice.getOltPort()), DevicePortLifeCycleStateUI.OPERATING.toString());

    oltDetailsPage.deconfigureAncpSession();
    oltDetailsPage.deleteUplinkConfiguration();
    Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

    // check uplink port life cycle state
    oltDetailsPage.openPortView(null);
    Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(oltDevice.getOltSlot(), oltDevice.getOltPort()), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

    Thread.sleep(1000); // ensure that the resource inventory database is updated
    checkUplinkDeleted(endSz);

    oltDetailsPage.deleteDevice();
    DeleteDevicePage deleteDevicePage = new DeleteDevicePage();
    deleteDevicePage.validateUrl();
    deleteDevicePage.DeleteOltDevice();
    Thread.sleep(WAIT_TIME_FOR_DEVICE_DELETION);
    checkDeviceDeleted(endSz);

  }

  /**
   * check ethernet port state
   */
  public void checkPortState(OltDevice device, OltDetailsPage detailsPage) {

    for (int port = 1; port <= device.getNumberOfEthernetPorts(); ++port) {
      log.info("checkPortState() Port={}, Slot={}, PortLifeCycleState ={}", port, device.getOltSlot(), detailsPage.getPortLifeCycleState(device.getOltSlot(), Integer.toString(port)));
      if (device.getOltPort().equals((Integer.toString(port)))) {
        Assert.assertEquals(detailsPage.getPortLifeCycleState(device.getOltSlot(), device.getOltPort()), DevicePortLifeCycleStateUI.OPERATING.toString());
      } else {
        Assert.assertEquals(detailsPage.getPortLifeCycleState(device.getOltSlot(), Integer.toString(port)), DevicePortLifeCycleStateUI.NOTOPERATING.toString());
      }
    }
  }

  /**
   * check device SDX3620-16 data from olt-resource-inventory and UI
   */
  private void checkDeviceSDX3620(String endSz) {

    List<Device> deviceList = oltResourceInventoryClient.getClient().deviceInternalController().findDeviceByCriteria()
            .endszQuery(endSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    Assert.assertEquals(deviceList.size(), 1L, "OLT deviceList.size mismatch");
    Device device = deviceList.get(0);
    Assert.assertEquals(device.getEndSz(), endSz, "OLT EndSz missmatch");

    Assert.assertEquals(device.getEmsNbiName(), EMS_NBI_NAME_SDX6320_16, "EMS NBI name missmatch");
    Assert.assertEquals(device.getTkz1(), "11971330F1", "TKZ1 missmatch");
    Assert.assertEquals(device.getType(), Device.TypeEnum.OLT);
    Assert.assertEquals(device.getCompositePartyId(), COMPOSITE_PARTY_ID_GFNW, "composite partyId missmatch");

    OltDetailsPage oltDetailsPage = new OltDetailsPage();
    oltDetailsPage.validateUrl();
    Assert.assertEquals(oltDetailsPage.getEndsz(), endSz);
    Assert.assertEquals(oltDetailsPage.getBezeichnung(), EMS_NBI_NAME_SDX6320_16, "UI EMS NBI name missmatch");
    Assert.assertEquals(oltDetailsPage.getKlsID(), oltDevice.getVst().getAddress().getKlsId(), "KlsId coming from PSL (dynamic Mock)");
    Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.OPERATING.toString(), "Device/ port lifecycle state missmatch");
  }

  /**
   * check uplink and ancp-session data from olt-ressource-inventory
   */
  private void checkUplink(String endSz) {
    List<UplinkDTO> uplinkDTOList = oltResourceInventoryClient.getClient().ethernetLinkInternalController().findEthernetLinksByEndsz()
            .oltEndSzQuery(endSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

    Assert.assertEquals(uplinkDTOList.size(), 1L);
    Assert.assertEquals(uplinkDTOList.get(0).getAncpSessions().size(), 1L);
    Assert.assertEquals(uplinkDTOList.get(0).getAncpSessions().get(0).getSessionStatus(), ANCPSession.SessionStatusEnum.ACTIVE);
  }

  /**
   * check uplink is not exist in olt-resource-inventory
   */
  private void checkUplinkDeleted(String endSz) {
    List<UplinkDTO> uplinkDTOList = oltResourceInventoryClient.getClient().ethernetLinkInternalController().findEthernetLinksByEndsz()
            .oltEndSzQuery(endSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

    Assert.assertTrue(uplinkDTOList.isEmpty());
  }

  /**
   * check device is not exist in olt-resource-inventory
   */
  private void checkDeviceDeleted(String endSz) {
    List<Device> deviceList = oltResourceInventoryClient.getClient().deviceInternalController().findDeviceByCriteria()
            .endszQuery(endSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    Assert.assertEquals(deviceList.size(), 0L, "Device is present");
  }

  /**
   * clears complete olt-resource-invemtory database
   */
  private void clearResourceInventoryDataBase(String endSz) {
    oltResourceInventoryClient.getClient().testDataManagementController().deleteDevice().endszQuery(endSz)
            .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
  }

}