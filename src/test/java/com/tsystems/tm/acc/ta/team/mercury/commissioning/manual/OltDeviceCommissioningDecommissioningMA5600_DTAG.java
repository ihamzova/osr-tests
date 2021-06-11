package com.tsystems.tm.acc.ta.team.mercury.commissioning.manual;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.enums.DevicePortLifeCycleStateUI;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.DeleteDevicePage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDetailsPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDiscoveryPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.ANCPSession;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.Device;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.UplinkDTO;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_NOT_FOUND_404;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.*;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;

@Slf4j
@ServiceLog({ANCP_CONFIGURATION_MS, OLT_DISCOVERY_MS, OLT_RESOURCE_INVENTORY_MS})
public class OltDeviceCommissioningDecommissioningMA5600_DTAG extends GigabitTest {

  private static final Integer WAIT_TIME_FOR_DEVICE_DELETION = 1_000;
  private static final Integer WAIT_TIME_FOR_CARD_DELETION = 1_000;

  private OltResourceInventoryClient oltResourceInventoryClient;

  @BeforeClass
  public void init() {
    oltResourceInventoryClient = new OltResourceInventoryClient(new RhssoClientFlowAuthTokenProvider(OLT_BFF_PROXY_MS, RhssoHelper.getSecretOfGigabitHub(OLT_BFF_PROXY_MS)));
  }

  @Test(description = "DIGIHUB-96866 Manual commissioning and decommissioning for not discovered MA5800 device as DTAG user")
  @TmsLink("DIGIHUB-96866") // Jira Id for this test in Xray
  @Description("Perform manual commissioning and decommissioning for not discovered MA5600 device as DTAG user on team environment")
  public void SearchAndDiscoverOlt() throws InterruptedException {

    OsrTestContext context = OsrTestContext.get();
    Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiDTAG);
    setCredentials(loginData.getLogin(), loginData.getPassword());

    OltDevice oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_7152_1234_76H1_MA5600);
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

    checkDeviceMA5600(endSz);
    checkUplink(endSz);

    //Thread.sleep(1000); // prevent Init Deconfiguration of ANCP session runs in error
    oltDetailsPage.deconfigureAncpSession();
    oltDetailsPage.deleteUplinkConfiguration();
    Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

    // check uplink port life cycle state
    oltDetailsPage.openPortView(oltDevice.getOltSlot());
    Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(oltDevice.getOltSlot(), oltDevice.getOltPort()), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

    Thread.sleep(1000); // ensure that the resource inventory database is updated
    checkUplinkDeleted(endSz);

    //DIGIHUB-55036 device and card deletion
    oltDetailsPage.deleteGponCard();
    Thread.sleep(WAIT_TIME_FOR_CARD_DELETION);
    checkCardDeleted(endSz, "1");
    oltDetailsPage.deleteDevice();
    DeleteDevicePage deleteDevicePage = new DeleteDevicePage();
    deleteDevicePage.validateUrl();
    deleteDevicePage.DeleteOltDevice();
    Thread.sleep(WAIT_TIME_FOR_DEVICE_DELETION);
    checkDeviceDeleted(endSz);
  }

  /**
   * check all port states from ethernet card
   */
  public void checkPortState(OltDevice device, OltDetailsPage detailsPage) {

    for (int port = 0; port <= 1; ++port) {
      log.info("checkPortState() Port={}, Slot={}, PortLifeCycleState ={}", port, device.getOltSlot(), detailsPage.getPortLifeCycleState(device.getOltSlot(), Integer.toString(port)));
      if (device.getOltPort().equals((Integer.toString(port)))) {
        Assert.assertEquals(detailsPage.getPortLifeCycleState(device.getOltSlot(), device.getOltPort()), DevicePortLifeCycleStateUI.OPERATING.toString());
      } else {
        Assert.assertEquals(detailsPage.getPortLifeCycleState(device.getOltSlot(), Integer.toString(port)), DevicePortLifeCycleStateUI.NOTOPERATING.toString());
      }
    }
  }

  /**
   * check device MA5600 data from olt-ressource-inventory
   */
  private void checkDeviceMA5600(String endSz) {

    List<Device> deviceList = oltResourceInventoryClient.getClient().deviceInternalController().findDeviceByCriteria()
            .endszQuery(endSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    Assert.assertEquals(deviceList.size(), 1L);
    Device device = deviceList.get(0);
    Assert.assertEquals(device.getEndSz(), endSz);

    Assert.assertEquals(device.getEmsNbiName(), "MA5600T");
    Assert.assertEquals(device.getTkz1(), "02353310");
    Assert.assertEquals(device.getType(), Device.TypeEnum.OLT);
    Assert.assertEquals(device.getCompositePartyId(), COMPOSITE_PARTY_ID_DTAG);

    OltDetailsPage oltDetailsPage = new OltDetailsPage();
    oltDetailsPage.validateUrl();
    Assert.assertEquals(oltDetailsPage.getEndsz(), endSz);
    Assert.assertEquals(oltDetailsPage.getBezeichnung(), EMS_NBI_NAME_MA5600);
    Assert.assertEquals(oltDetailsPage.getKlsID(), "17056514");
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
   * check CARD is not exist in olt-resource-inventory
   */
  private void checkCardDeleted(String endSz, String slot) {
    oltResourceInventoryClient.getClient().cardController().findCard()
            .endSzQuery(endSz).slotNumberQuery(slot).executeAs(validatedWith(shouldBeCode(HTTP_CODE_NOT_FOUND_404)));
  }

  /**
   * clears complete olt-resource-invemtory database
   */
  private void clearResourceInventoryDataBase(String endSz) {
    oltResourceInventoryClient.getClient().testDataManagementController().deleteDevice().endszQuery(endSz)
            .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
  }
}
