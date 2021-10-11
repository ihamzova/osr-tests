package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.ResponseSpecBuilders;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.*;
import com.tsystems.tm.acc.ta.data.osr.enums.DevicePortLifeCycleStateUI;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltCommissioningPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDetailsPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDiscoveryPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_19_0.client.model.*;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.*;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_NO_CONTENT_204;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.FEATURE_ANCP_MIGRATION_ACTIVE;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;

public class OltCommissioningRobot {

  private static final Integer HTTP_CODE_OK_200 = 200;
  private static final Integer TIMEOUT_FOR_OLT_COMMISSIONING = 40 * 60_000;
  private static final Integer TIMEOUT_FOR_CARD_PROVISIONING = 20 * 60_000;
  private static final Integer TIMEOUT_FOR_ADTRAN_PROVISIONING = 40 * 60_000;
  private static final Integer ACCESS_LINE_PER_PORT_MA5600 = 16;
  private static final Integer ACCESS_LINE_PER_PORT_SDX6320 = 32;
  private static final Integer LINE_ID_POOL_PER_PORT = 32;
  private static final Integer HOME_ID_POOL_PER_PORT = 32;

  private static final AuthTokenProvider authTokenProviderOltCommissioning = new RhssoClientFlowAuthTokenProvider(OLT_COMMISSIONING_MS, RhssoHelper.getSecretOfGigabitHub(OLT_COMMISSIONING_MS));
  private static final AuthTokenProvider authTokenProviderOltBffProxy = new RhssoClientFlowAuthTokenProvider(OLT_BFF_PROXY_MS, RhssoHelper.getSecretOfGigabitHub(OLT_BFF_PROXY_MS));

  private OltResourceInventoryClient oltResourceInventoryClient = new OltResourceInventoryClient(authTokenProviderOltBffProxy);
  private AccessLineResourceInventoryClient accessLineResourceInventoryClient = new AccessLineResourceInventoryClient(authTokenProviderOltBffProxy);
  private OltDiscoveryClient oltDiscoveryClient = new OltDiscoveryClient(authTokenProviderOltCommissioning);
  private AccessLineResourceInventoryFillDbClient accessLineResourceInventoryFillDbClient = new AccessLineResourceInventoryFillDbClient(authTokenProviderOltBffProxy);
  private DeviceTestDataManagementClient deviceTestDataManagementClient = new DeviceTestDataManagementClient();

  @Step("Starts automatic olt commissioning process")
  public void startAutomaticOltCommissioning(OltDevice olt) {
    OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
    oltSearchPage.validateUrl();
    oltSearchPage = oltSearchPage.searchNotDiscoveredByParameters(olt);

    OltCommissioningPage oltCommissioningPage = oltSearchPage.pressAutoCommissionigButton();

    oltCommissioningPage.validateUrl();
    oltCommissioningPage.startOltCommissioning(olt, TIMEOUT_FOR_OLT_COMMISSIONING);

    OltDetailsPage oltDetailsPage = new OltDetailsPage();
    oltDetailsPage.validateUrl();
    Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.OPERATING.toString(), "Device LifeCycleState after Commissioning mismatch");
    oltDetailsPage.openPortView(olt.getOltSlot());
    Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(olt.getOltSlot(), olt.getOltPort()), DevicePortLifeCycleStateUI.OPERATING.toString(), "Port LifeCycleState after Commissioning mismatch");
    oltDetailsPage.checkGponPortLifeCycleState(olt, DevicePortLifeCycleStateUI.OPERATING.toString());
  }

  @Step("Starts manual olt commissioning process")
  public void startManualOltCommissioning(OltDevice olt) {
    OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
    oltSearchPage.validateUrl();
    oltSearchPage = oltSearchPage.searchNotDiscoveredByParameters(olt);

    OltDiscoveryPage oltDiscoveryPage = oltSearchPage.pressManualCommissionigButton();

    oltDiscoveryPage.validateUrl();
    int successfullyDiscoveriesBeforeStart = oltDiscoveryPage.getSuccessfullyDiscoveriesCount();
    oltDiscoveryPage = oltDiscoveryPage.makeOltDiscovery();
    Assert.assertEquals(oltDiscoveryPage.getSuccessfullyDiscoveriesCount(), successfullyDiscoveriesBeforeStart + 1, "Discovery result mismatch");
    oltDiscoveryPage = oltDiscoveryPage.saveDiscoveryResults();

    oltSearchPage = oltDiscoveryPage.openOltSearchPage();

    OltDetailsPage oltDetailsPage = oltSearchPage.searchDiscoveredOltByParameters(olt);
    oltDetailsPage.validateUrl();
    Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString(), "Device LifeCycleState before commissioning mismatch");
    oltDetailsPage.openPortView(olt.getOltSlot());
    Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(olt.getOltSlot(), olt.getOltPort()), DevicePortLifeCycleStateUI.NOTOPERATING.toString(), "Ethernet Port LifeCycleState before ANCP configuration  mismatch");
    oltDetailsPage.checkGponPortLifeCycleState(olt, DevicePortLifeCycleStateUI.NOTOPERATING.toString());

    oltDetailsPage.startUplinkConfiguration();
    oltDetailsPage.inputUplinkParameters(olt);
    oltDetailsPage.saveUplinkConfiguration();

    oltDetailsPage.configureAncpSessionStart();
    oltDetailsPage.updateAncpSessionStatus();
    oltDetailsPage.checkAncpSessionStatus();

    Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.OPERATING.toString(), "Device LifeCycleState after ANCP configuration is not in operating state");
    oltDetailsPage.openPortView(olt.getOltSlot());
    Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(olt.getOltSlot(), olt.getOltPort()), DevicePortLifeCycleStateUI.OPERATING.toString(), "Ethernet Port LifeCycleState after ANCP configuration is not in operating state");

    //check AL Provisioning from device for adtran or from card for huawei
    if (olt.getHersteller().equals("ADTRAN")) {
      oltDetailsPage.startAccessLinesProvisioningFromDevice(TIMEOUT_FOR_ADTRAN_PROVISIONING);
    } else {
      oltDetailsPage.startAccessLinesProvisioning(TIMEOUT_FOR_CARD_PROVISIONING);
    }

    oltDetailsPage.checkGponPortLifeCycleState(olt, DevicePortLifeCycleStateUI.OPERATING.toString());
  }


  @Step("Checks olt data in olt-ri after commissioning process")
  public void checkOltCommissioningResult(OltDevice olt) {
    String oltEndSz = olt.getEndsz();
    long portsCount;
    long accessLinesPerPort = ACCESS_LINE_PER_PORT_MA5600;
    long expectedFreeLineIdCountPerPort = LINE_ID_POOL_PER_PORT / 2;
    long expectedUsedLineIdCountPerPort = LINE_ID_POOL_PER_PORT / 2;

    List<Device> deviceList = oltResourceInventoryClient.getClient().deviceInternalController().findDeviceByCriteria()
            .endszQuery(oltEndSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    Assert.assertEquals(deviceList.size(), 1L, "Device is not present");
    Assert.assertEquals(deviceList.get(0).getType(), Device.TypeEnum.OLT, "Device type is not OLT");
    Assert.assertEquals(deviceList.get(0).getEndSz(), oltEndSz, "Device EndSz mismatch");
    Device deviceAfterCommissioning = deviceList.get(0);

    if (deviceList.get(0).getEquipmentHolders().isEmpty()) {
      Assert.assertEquals(deviceList.get(0).getPorts().size(), olt.getNumberOfPonPorts() + olt.getNumberOfEthernetPorts(), "Ports number by Adtran mismatch");
      portsCount = olt.getNumberOfPonPorts();
      //for ADTRAN device provisioning (strategy 32 on demand)
      accessLinesPerPort = ACCESS_LINE_PER_PORT_SDX6320;
      expectedFreeLineIdCountPerPort = 0;
      expectedUsedLineIdCountPerPort = LINE_ID_POOL_PER_PORT;
    } else {
      Optional<Integer> portsCountOptional = deviceAfterCommissioning.getEquipmentHolders().stream().map(EquipmentHolder::getCard)
              .filter(card -> card.getCardType().equals(Card.CardTypeEnum.GPON)).map(card -> card.getPorts().size()).reduce(Integer::sum);
      portsCount = portsCountOptional.orElse(0);
    }

    // check device lifecycle state
    Assert.assertEquals(Device.LifeCycleStateEnum.OPERATING, deviceAfterCommissioning.getLifeCycleState(), "Device LifeCycleState after commissioning is not in operating state");

    // check uplink port lifecycle state
    if (deviceList.get(0).getEquipmentHolders().isEmpty()) {
      Optional<Port> uplinkPort = deviceList.get(0).getPorts().stream()
              .filter(port -> port.getPortNumber().equals(olt.getOltPort()))
              .filter(port -> port.getPortType().equals(Port.PortTypeEnum.ETHERNET))
              .findFirst();
      Assert.assertTrue(uplinkPort.isPresent(), "No uplink port is present");
      Assert.assertEquals(Port.LifeCycleStateEnum.OPERATING, uplinkPort.get().getLifeCycleState(), "Uplink port state after commissioning is not in operating state");
    } else {
      Optional<Port> uplinkPort = deviceAfterCommissioning.getEquipmentHolders().stream()
              .filter(equipmentHolder -> equipmentHolder.getSlotNumber().equals(olt.getOltSlot()))
              .map(EquipmentHolder::getCard)
              .filter(card -> card.getCardType().equals(Card.CardTypeEnum.UPLINK_CARD) || card.getCardType().equals(Card.CardTypeEnum.PROCESSING_BOARD))
              .flatMap(card -> card.getPorts().stream())
              .filter(port -> port.getPortNumber().equals(olt.getOltPort())).findFirst();
      Assert.assertTrue(uplinkPort.isPresent(), "Uplink is not found");
      Assert.assertEquals(Port.LifeCycleStateEnum.OPERATING, uplinkPort.get().getLifeCycleState(), "Uplink port state after commissioning is not in operating state");
    }

    List<AccessLineDto> wgAccessLines = accessLineResourceInventoryClient.getClient().accessLineController().searchAccessLines()
            .body(new SearchAccessLineDto().endSz(oltEndSz)).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)))
            .stream().filter(accessLineDto -> accessLineDto.getStatus().equals(AccessLineStatus.WALLED_GARDEN)).collect(Collectors.toList());
    long wgLinesCount = wgAccessLines.size();

    Assert.assertEquals(wgLinesCount, portsCount * accessLinesPerPort, "wgLinesCount mismatch");

    boolean allPortsInOperatingState = deviceAfterCommissioning.getEquipmentHolders().stream().map(EquipmentHolder::getCard)
            .filter(card -> card.getCardType().equals(Card.CardTypeEnum.GPON)).map(Card::getPorts)
            .flatMap(List::stream).map(Port::getLifeCycleState).allMatch(Port.LifeCycleStateEnum.OPERATING::equals);

    Assert.assertTrue(allPortsInOperatingState, "Some port is in not OPERATING state");

    List<Integer> anpTagsList = wgAccessLines.stream().map(accessLineDto -> accessLineDto.getAnpTag().getAnpTag())
            .filter(anpTagValue -> anpTagValue >= 128).collect(Collectors.toList());

    Assert.assertEquals(anpTagsList.size(), portsCount * accessLinesPerPort, "anpTagsList size mismatch");

    Assert.assertTrue(anpTagsList.contains(128), "anpTagsList contains mismatch");

    List<UplinkDTO> uplinksList = oltResourceInventoryClient.getClient().ethernetLinkInternalController().findEthernetLinksByEndsz().oltEndSzQuery(oltEndSz)
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

    Assert.assertEquals(uplinksList.size(), 1, "There is no uplink");

    UplinkDTO uplink = uplinksList.get(0);

    Assert.assertEquals(uplink.getIpStatus(), UplinkDTO.IpStatusEnum.ACTIVE, "IpStatus is not active");

    Assert.assertEquals(uplink.getAncpSessions().size(), 1, "There are no AncpSessions");

    Assert.assertEquals(uplink.getAncpSessions().get(0).getSessionStatus(), ANCPSession.SessionStatusEnum.ACTIVE, "ANCPSession is not active");

    long homeIdCount = accessLineResourceInventoryClient.getClient().homeIdController().searchHomeIds()
            .body(new SearchHomeIdDto().endSz(oltEndSz)).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)))
            .stream().filter(homeIdDto -> homeIdDto.getStatus().equals(HomeIdStatus.FREE)).count();

    Assert.assertEquals(homeIdCount, portsCount * HOME_ID_POOL_PER_PORT, "HomeIdCount mismatch");

    long backhaulIdCount = accessLineResourceInventoryClient.getClient().backhaulIdController().searchBackhaulIds()
            .body(new SearchBackhaulIdDto().endSz(oltEndSz)).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)))
            .stream().filter(backhaulIdDto -> BackhaulStatus.CONFIGURED.equals(backhaulIdDto.getStatus())).count();

    Assert.assertEquals(backhaulIdCount, portsCount, "backhaulIdCount mismatched with portsCount");

    List<LineIdDto> lineIdDtos = accessLineResourceInventoryClient.getClient().lineIdController().searchLineIds()
            .body(new SearchLineIdDto().endSz(oltEndSz)).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

    long freeLineIdCount = lineIdDtos.stream().filter(lineIdDto -> lineIdDto.getStatus().equals(LineIdStatus.FREE)).count();
    long usedLineIdCount = lineIdDtos.stream().filter(lineIdDto -> lineIdDto.getStatus().equals(LineIdStatus.USED)).count();


    Assert.assertEquals(freeLineIdCount, portsCount * expectedFreeLineIdCountPerPort, "FreeLineIdCount mismatch");
    Assert.assertEquals(usedLineIdCount, portsCount * expectedUsedLineIdCountPerPort, "UsedLineIdCount mismatch");
  }

  @Step("Restore OSR Database state")
  public void restoreOsrDbState() {
    accessLineResourceInventoryFillDbClient.getClient().fillDatabase().deleteDatabase()
            .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    oltDiscoveryClient.reset();
  }

  @Step("Clear {oltDevice} device in olt-resource-inventory database")
  public void clearResourceInventoryDataBase(OltDevice oltDevice) {
    String endSz = oltDevice.getEndsz();
    if (FEATURE_ANCP_MIGRATION_ACTIVE) {
      deviceTestDataManagementClient.getClient().deviceTestDataManagement().deleteTestData().deviceEndSzQuery(endSz)
              .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    } else {
      oltResourceInventoryClient.getClient().testDataManagementController().deleteDevice().endszQuery(endSz)
              .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
    }
  }
}
