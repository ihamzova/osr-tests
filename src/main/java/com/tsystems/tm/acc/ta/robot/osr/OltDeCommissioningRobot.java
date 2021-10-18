package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.DeviceResourceInventoryManagementClient;
import com.tsystems.tm.acc.ta.data.osr.enums.DevicePortLifeCycleStateUI;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.DeleteDevicePage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDetailsPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_19_0.client.model.*;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.model.*;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.OLT_BFF_PROXY_MS;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.OLT_COMMISSIONING_MS;
import static org.testng.Assert.assertEquals;

public class OltDeCommissioningRobot {
  private static final Integer HTTP_CODE_OK_200 = 200;
  private static final Integer HTTP_CODE_NOT_FOUND_404 = 404;
  private static final Integer TIMEOUT_FOR_CARD_DEPROVISIONING = 40 * 60_000;

  private static final Integer WAIT_TIME_FOR_DEVICE_DELETION = 1_000;
  private static final Integer WAIT_TIME_FOR_CARD_DELETION = 1_000;

  private static final AuthTokenProvider authTokenProvider = new RhssoClientFlowAuthTokenProvider(OLT_COMMISSIONING_MS, RhssoHelper.getSecretOfGigabitHub(OLT_COMMISSIONING_MS));
  private static final AuthTokenProvider authTokenProviderOltBffProxy = new RhssoClientFlowAuthTokenProvider(OLT_BFF_PROXY_MS, RhssoHelper.getSecretOfGigabitHub(OLT_BFF_PROXY_MS));

  private DeviceResourceInventoryManagementClient deviceResourceInventoryManagementClient = new DeviceResourceInventoryManagementClient(authTokenProviderOltBffProxy);
  private AccessLineResourceInventoryClient accessLineResourceInventoryClient = new AccessLineResourceInventoryClient(authTokenProvider);

  @Step("Start olt decommissioning process after manual commissioning")
  public void startOltDecommissioningAfterManualCommissioning(OltDevice olt) throws InterruptedException {

    OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
    oltSearchPage.validateUrl();

    OltDetailsPage oltDetailsPage = oltSearchPage.searchDiscoveredOltByParameters(olt);
    oltDetailsPage.startAccessLinesDeProvisioningFromCard(TIMEOUT_FOR_CARD_DEPROVISIONING);
    oltDetailsPage.deconfigureAncpSession();
    oltDetailsPage.deleteUplinkConfiguration();
    assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString(), "Device LifeCycleState after manual DeCommissioning mismatch");
    oltDetailsPage.deleteGponCard();
    Thread.sleep(WAIT_TIME_FOR_CARD_DELETION);
    oltDetailsPage.deleteDevice();
    DeleteDevicePage deleteDevicePage = new DeleteDevicePage();
    deleteDevicePage.validateUrl();
    deleteDevicePage.DeleteOltDevice();
    Thread.sleep(WAIT_TIME_FOR_DEVICE_DELETION);
  }

  @Step("Start olt decommissioning process after auto commissioning")
  public void startOltDecommissioningAfterAutoCommissioning(OltDevice olt) throws InterruptedException {

    OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
    oltSearchPage.validateUrl();

    OltDetailsPage oltDetailsPage = oltSearchPage.searchDiscoveredOltByParameters(olt);
    oltDetailsPage.startAccessLinesDeProvisioningFromDevice(TIMEOUT_FOR_CARD_DEPROVISIONING);
    oltDetailsPage.deconfigureAncpSession();
    oltDetailsPage.deleteUplinkConfiguration();
    assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString(), "Device LifeCycleState after auto DeCommissioning mismatch");
    oltDetailsPage.deleteGponCard();
    Thread.sleep(WAIT_TIME_FOR_CARD_DELETION);
    oltDetailsPage.deleteDevice();
    DeleteDevicePage deleteDevicePage = new DeleteDevicePage();
    deleteDevicePage.validateUrl();
    deleteDevicePage.DeleteOltDevice();
    Thread.sleep(WAIT_TIME_FOR_DEVICE_DELETION);
  }

  @Step("Start olt decommissioning process after commissioning for ADTRAN device")
  public void startAdtranOltDecommissioningAfterAutoCommissioning(OltDevice olt) throws InterruptedException {

    OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
    oltSearchPage.validateUrl();

    OltDetailsPage oltDetailsPage = oltSearchPage.searchDiscoveredOltByParameters(olt);
    oltDetailsPage.startAccessLinesDeProvisioningFromDevice(TIMEOUT_FOR_CARD_DEPROVISIONING);
    oltDetailsPage.deconfigureAncpSession();
    oltDetailsPage.deleteUplinkConfiguration();
    assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString(), "Adtran Device LifeCycleState after auto DeCommissioning mismatch");
    oltDetailsPage.deleteDevice();
    DeleteDevicePage deleteDevicePage = new DeleteDevicePage();
    deleteDevicePage.validateUrl();
    deleteDevicePage.DeleteOltDevice();
    Thread.sleep(WAIT_TIME_FOR_DEVICE_DELETION);
  }

  @Step("Checks olt data in olt-ri and al-ri after decommissioning process")
  public void checkOltDeCommissioningResult(OltDevice olt, String slot) {
    String oltEndSz = olt.getEndsz();

    List<Device> deviceList = deviceResourceInventoryManagementClient.getClient().device().listDevice()
            .endSzQuery(oltEndSz).depthQuery(3).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    assertEquals(deviceList.size(), 0L, "Device is present");

    if (slot != null && !slot.isEmpty()) {
      List<Card> cardList = deviceResourceInventoryManagementClient.getClient().card().listCard()
              .parentDeviceEquipmentRefEndSzQuery(oltEndSz)
              .slotNameQuery(slot)
              .depthQuery(1).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
      assertEquals(cardList.size(), 0L, "Card is present");
    }

    List<AccessLineDto> ftthAccessLines = new ArrayList<>(accessLineResourceInventoryClient.getClient().accessLineController().searchAccessLines()
            .body(new SearchAccessLineDto().endSz(olt.getEndsz()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200))));

    List<HomeIdDto> homeIds = new ArrayList<>(accessLineResourceInventoryClient.getClient().homeIdController().searchHomeIds()
            .body(new SearchHomeIdDto().endSz(olt.getEndsz()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200))));

    List<LineIdDto> lineIds = new ArrayList<>(accessLineResourceInventoryClient.getClient().lineIdController().searchLineIds()
            .body(new SearchLineIdDto().endSz(olt.getEndsz()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200))));

    List<BackhaulIdDto> backhaulIds = new ArrayList<>(accessLineResourceInventoryClient.getClient().backhaulIdController().searchBackhaulIds()
            .body(new SearchBackhaulIdDto().endSz(olt.getEndsz()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200))));

    assertEquals(ftthAccessLines.size(), 0, "There are AccessLines left");
    assertEquals(homeIds.size(), 0, "There are HomeIds left");
    assertEquals(lineIds.size(), 0, "There are LineIds left");
    assertEquals(backhaulIds.size(), 0, "There are BackhaulIds left");
  }

}