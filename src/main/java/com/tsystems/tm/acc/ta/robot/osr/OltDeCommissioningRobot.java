package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.enums.DevicePortLifeCycleStateUI;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.DeleteDevicePage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDetailsPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_14_0.client.model.*;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.Device;
import io.qameta.allure.Step;

import java.util.List;
import java.util.stream.Collectors;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static org.testng.Assert.assertEquals;

public class OltDeCommissioningRobot {
  private static final Integer HTTP_CODE_OK_200 = 200;
  private static final Integer HTTP_CODE_NOT_FOUND_404 = 404;
  private static final Integer TIMEOUT_FOR_CARD_DEPROVISIONING = 20 * 60_000;

  private static final Integer WAIT_TIME_FOR_DEVICE_DELETION = 1_000;
  private static final Integer WAIT_TIME_FOR_CARD_DELETION = 1_000;

  private OltResourceInventoryClient oltResourceInventoryClient = new OltResourceInventoryClient();
  private AccessLineResourceInventoryClient accessLineResourceInventoryClient = new AccessLineResourceInventoryClient();

  @Step("Start olt decommissioning process after manual commissioning")
  public void startOltDecommissioningAfterManualCommissioning(OltDevice olt) throws InterruptedException {

    OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
    oltSearchPage.validateUrl();

    OltDetailsPage oltDetailsPage = oltSearchPage.searchDiscoveredOltByParameters(olt);
    oltDetailsPage.startAccessLinesDeProvisioningFromCard(TIMEOUT_FOR_CARD_DEPROVISIONING);
    oltDetailsPage.deconfigureAncpSession();
    oltDetailsPage.deleteUplinkConfiguration();
    assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());
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
    assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());
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
    assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());
    oltDetailsPage.deleteDevice();
    DeleteDevicePage deleteDevicePage = new DeleteDevicePage();
    deleteDevicePage.validateUrl();
    deleteDevicePage.DeleteOltDevice();
    Thread.sleep(WAIT_TIME_FOR_DEVICE_DELETION);
  }

  @Step("Checks olt data in olt-ri and al-ri after decommissioning process")
  public void checkOltDeCommissioningResult(OltDevice olt, String slot) {
    String oltEndSz = olt.getEndsz();

    List<Device> deviceList = oltResourceInventoryClient.getClient().deviceInternalController().findDeviceByCriteria()
            .endszQuery(oltEndSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    assertEquals(deviceList.size(), 0L, "Device is present");

    if (slot != null && !slot.isEmpty()) {
      oltResourceInventoryClient.getClient().cardController().findCard()
              .endSzQuery(oltEndSz).slotNumberQuery(slot).executeAs(validatedWith(shouldBeCode(HTTP_CODE_NOT_FOUND_404)));
    }

    List<AccessLineDto> ftthAccessLines = accessLineResourceInventoryClient.getClient().accessLineController().searchAccessLines()
            .body(new SearchAccessLineDto().endSz(olt.getEndsz()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)))
            .stream().collect(Collectors.toList());

    List<HomeIdDto> homeIds = accessLineResourceInventoryClient.getClient().homeIdController().searchHomeIds()
            .body(new SearchHomeIdDto().endSz(olt.getEndsz()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)))
            .stream().collect(Collectors.toList());

    List<LineIdDto> lineIds = accessLineResourceInventoryClient.getClient().lineIdController().searchLineIds()
            .body(new SearchLineIdDto().endSz(olt.getEndsz()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)))
            .stream().collect(Collectors.toList());

    List<BackhaulIdDto> backhaulIds = accessLineResourceInventoryClient.getClient().backhaulIdController().searchBackhaulIds()
            .body(new SearchBackhaulIdDto().endSz(olt.getEndsz()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)))
            .stream().collect(Collectors.toList());

    assertEquals(ftthAccessLines.size(), 0, "There are AccessLines left");
    assertEquals(homeIds.size(), 0, "There are HomeIds left");
    assertEquals(lineIds.size(), 0, "There are LineIds left");
    assertEquals(backhaulIds.size(), 0, "There are BackhaulIds left");
  }

}