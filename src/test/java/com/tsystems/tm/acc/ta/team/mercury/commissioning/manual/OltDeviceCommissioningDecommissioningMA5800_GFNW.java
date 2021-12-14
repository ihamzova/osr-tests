package com.tsystems.tm.acc.ta.team.mercury.commissioning.manual;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.DeviceResourceInventoryManagementClient;
import com.tsystems.tm.acc.ta.data.osr.enums.DevicePortLifeCycleStateUI;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.DeleteDevicePage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDetailsPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDiscoveryPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.ta.robot.osr.OltCommissioningRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.model.*;
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
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.COMPOSITE_PARTY_ID_GFNW;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.EMS_NBI_NAME_MA5800;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;


@Slf4j
@ServiceLog({ANCP_CONFIGURATION_MS, OLT_DISCOVERY_MS, OLT_RESOURCE_INVENTORY_MS, OLT_UPLINK_MANAGEMENT_MS})
public class OltDeviceCommissioningDecommissioningMA5800_GFNW extends GigabitTest {

  private static final int WAIT_TIME_FOR_RENDERING = 5_000;

  private static final Integer WAIT_TIME_FOR_DEVICE_DELETION = 1_000;
  private static final Integer WAIT_TIME_FOR_CARD_DELETION = 1_000;

  private OltCommissioningRobot oltCommissioningRobot = new OltCommissioningRobot();
  private DeviceResourceInventoryManagementClient deviceResourceInventoryManagementClient;

  @BeforeClass
  public void init() {
    oltCommissioningRobot.enableFeatureToogleUiUplinkImport();
    deviceResourceInventoryManagementClient = new DeviceResourceInventoryManagementClient(new RhssoClientFlowAuthTokenProvider(OLT_BFF_PROXY_MS, RhssoHelper.getSecretOfGigabitHub(OLT_BFF_PROXY_MS)));
  }

  @Test(description = "DIGIHUB-96865 Manual commissioning and decommissioning for not discovered MA5800 device as GFNW user")
  @TmsLink("DIGIHUB-96865") // Jira Id for this test in Xray
  @Description("Perform manual commissioning and decommissioning for not discovered MA5800 device as GFNW user on team environment")
  public void SearchAndDiscoverOlt() throws InterruptedException {

    OsrTestContext context = OsrTestContext.get();
    Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiGFNW);
    setCredentials(loginData.getLogin(), loginData.getPassword());

    OltDevice oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_911_1234_76ZC_MA5800);
    String endSz = oltDevice.getEndsz();
    oltCommissioningRobot.clearResourceInventoryDataBase(oltDevice);
    OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
    oltSearchPage.validateUrl();

    oltSearchPage.searchNotDiscoveredByParameters(oltDevice);
    oltSearchPage.pressManualCommissionigButton();
    OltDiscoveryPage oltDiscoveryPage = new OltDiscoveryPage();
    oltDiscoveryPage.makeOltDiscovery();
    oltDiscoveryPage.saveDiscoveryResults();
    oltDiscoveryPage.openOltSearchPage();

    Thread.sleep(WAIT_TIME_FOR_RENDERING); // During the pipeline test no EndSz Search can be selected for the user GFNW if the page is not yet finished.
    OltDetailsPage oltDetailsPage = oltSearchPage.searchDiscoveredOltByParameters(oltDevice);
    Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());
    oltDetailsPage.openPortView(oltDevice.getOltSlot());
    Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(oltDevice.getOltSlot(), oltDevice.getOltPort()), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

    oltDetailsPage.startUplinkConfiguration();
    oltDetailsPage.saveUplinkConfiguration();

    oltDetailsPage.configureAncpSessionStart();
    oltDetailsPage.updateAncpSessionStatus();
    oltDetailsPage.checkAncpSessionStatus();
    Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.OPERATING.toString());
    oltDetailsPage.openPortView(oltDevice.getOltSlot());
    checkPortState(oltDevice, oltDetailsPage);

    checkDeviceMA5800(endSz);
    oltCommissioningRobot.checkUplink(oltDevice);

    Thread.sleep(1000); // prevent Init Deconfiguration of ANCP session runs in error
    oltDetailsPage.deconfigureAncpSession();
    oltDetailsPage.deleteUplinkConfiguration();
    Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

    // check uplink port life cycle state
    oltDetailsPage.openPortView(oltDevice.getOltSlot());
    Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(oltDevice.getOltSlot(), oltDevice.getOltPort()), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

    //Thread.sleep(1000); // ensure that the resource inventory database is updated
    checkUplinkDeleted(endSz);

    //DIGIHUB-55036 device and card deletion
    oltDetailsPage.deleteGponCard();
    Thread.sleep(WAIT_TIME_FOR_CARD_DELETION);
    checkCardIsNotDeleted(endSz, "1");
    oltDetailsPage.deleteDevice();
    DeleteDevicePage deleteDevicePage = new DeleteDevicePage();
    deleteDevicePage.validateUrl();

    deleteDevicePage.DeleteOltDevice();
    Thread.sleep(WAIT_TIME_FOR_DEVICE_DELETION);
    checkDeviceIsNotDeleted(endSz);
  }

  /**
   * check all port states from ethernet card
   */
  public void checkPortState(OltDevice device, OltDetailsPage detailsPage) {
    for (int port = 0; port <= 3; ++port) {
      log.info("checkPortState() Port={}, Slot={}, PortLifeCycleState ={}", port, device.getOltSlot(), detailsPage.getPortLifeCycleState(device.getOltSlot(), Integer.toString(port)));
      if (device.getOltPort().equals((Integer.toString(port)))) {
        Assert.assertEquals(detailsPage.getPortLifeCycleState(device.getOltSlot(), device.getOltPort()), DevicePortLifeCycleStateUI.OPERATING.toString());
      } else {
        Assert.assertEquals(detailsPage.getPortLifeCycleState(device.getOltSlot(), Integer.toString(port)), DevicePortLifeCycleStateUI.NOTOPERATING.toString());
      }
    }
  }

  /**
   * check device MA5800 data from olt-resource-inventory and UI
   */
  private void checkDeviceMA5800(String endSz) {

    List<Device> deviceList = deviceResourceInventoryManagementClient.getClient().device().listDevice()
            .endSzQuery(endSz).depthQuery(3).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

    Assert.assertEquals(deviceList.size(), 1L, "OLT deviceList.size mismatch");
    Device device = deviceList.get(0);
    Assert.assertEquals(device.getEndSz(), endSz, "OLT EndSz missmatch");

    Assert.assertEquals(device.getEmsNbiName(), EMS_NBI_NAME_MA5800, "EMS NBI name missmatch");
    Assert.assertEquals(device.getDeviceType(), DeviceType.OLT, "DeviceType missmatch");
    Assert.assertEquals(device.getRelatedParty().get(0).getId(), COMPOSITE_PARTY_ID_GFNW.toString(), "composite partyId GFNW missmatch");

    OltDetailsPage oltDetailsPage = new OltDetailsPage();
    oltDetailsPage.validateUrl();
    Assert.assertEquals(oltDetailsPage.getEndsz(), endSz);
    Assert.assertEquals(oltDetailsPage.getBezeichnung(), EMS_NBI_NAME_MA5800);
    Assert.assertEquals(oltDetailsPage.getKlsID(), "17056514");

  }

  /**
   * check uplink is not exist in olt-resource-inventory
   */
  private void checkUplinkDeleted(String endSz) {
    List<Uplink> uplinkList = deviceResourceInventoryManagementClient.getClient().uplink().listUplink()
            .portsEquipmentBusinessRefEndSzQuery(endSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

    Assert.assertTrue(uplinkList.isEmpty());
  }

  /**
   * check device exists in olt-resource-inventory
   */
  private void checkDeviceIsNotDeleted(String endSz) {
    List<Device> deviceList = deviceResourceInventoryManagementClient.getClient().device().listDevice()
            .endSzQuery(endSz).depthQuery(3).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

    Assert.assertEquals(deviceList.size(), 1L, "Device is deleted");
  }

  /**
   * check CARD exists in olt-resource-inventory
   */
  private void checkCardIsNotDeleted(String endSz, String slot) {
    List<Card> cardList = deviceResourceInventoryManagementClient.getClient().card().listCard()
            .parentDeviceEquipmentRefEndSzQuery(endSz).slotNameQuery(slot).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

    Assert.assertEquals(cardList.size(), 1L, "Card is deleted");
  }
}