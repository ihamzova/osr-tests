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
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.model.*;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.model.PortType;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_NO_CONTENT_204;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.EMS_NBI_NAME_SDX6320_16;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.FEATURE_ANCP_MIGRATION_ACTIVE;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;

@Slf4j
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
  private DeviceResourceInventoryManagementClient deviceResourceInventoryManagementClient = new DeviceResourceInventoryManagementClient(authTokenProviderOltBffProxy);
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

    List<Device> deviceList = deviceResourceInventoryManagementClient.getClient().device().listDevice()
            .endSzQuery(oltEndSz).depthQuery(3).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    Assert.assertEquals(deviceList.size(), 1L, "Device is not present");
    Assert.assertEquals(deviceList.get(0).getDeviceType(), DeviceType.OLT, "Device type is not OLT");
    Assert.assertEquals(deviceList.get(0).getEndSz(), oltEndSz, "Device EndSz mismatch");
    Device deviceAfterCommissioning = deviceList.get(0);

    List<Port> portList = deviceResourceInventoryManagementClient.getClient().port().listPort()
            .parentEquipmentRefEndSzQuery(oltEndSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

    if (deviceList.get(0).getEmsNbiName().equals(EMS_NBI_NAME_SDX6320_16)) {
      Assert.assertEquals(portList.size(), olt.getNumberOfPonPorts() + olt.getNumberOfEthernetPorts(), "Ports number by Adtran mismatch");
      portsCount = olt.getNumberOfPonPorts();
      //for ADTRAN device provisioning (strategy 32 on demand)
      accessLinesPerPort = ACCESS_LINE_PER_PORT_SDX6320;
      expectedFreeLineIdCountPerPort = 0;
      expectedUsedLineIdCountPerPort = LINE_ID_POOL_PER_PORT;
      log.info(" portsCount for ADTRAN OLT = {} EndSz = {}", portsCount, oltEndSz);
    } else {
      portsCount = portList.stream()
              .filter(port -> port.getPortType().equals(PortType.PON)).count();
      log.info(" portsCount for HUAWEI OLT = {} EndSz = {}", portsCount, oltEndSz);
    }

    // check device lifecycle state
    Assert.assertEquals(deviceAfterCommissioning.getLifeCycleState(), LifeCycleState.OPERATING, "Device LifeCycleState after commissioning is not in operating state");

    // check uplink port lifecycle state
    if (deviceList.get(0).getEmsNbiName().equals(EMS_NBI_NAME_SDX6320_16)) {
      Optional<Port> uplinkPort = portList.stream()
              .filter(port -> port.getPortName().equals(olt.getOltPort()))
              .filter(port -> port.getPortType().equals(PortType.ETHERNET))
              .findFirst();
      Assert.assertTrue(uplinkPort.isPresent(), "ADTRAN No uplink port is present");
      Assert.assertEquals( uplinkPort.get().getLifeCycleState(), LifeCycleState.OPERATING, "Uplink port state after commissioning is not in operating state");
    } else {
      Optional<Port> uplinkPort = portList.stream()
              .filter(port -> port.getParentEquipmentRef().getSlotName().equals(olt.getOltSlot()))
              .filter(port -> port.getPortName().equals(olt.getOltPort()))
              .filter(port -> port.getPortType().equals(PortType.ETHERNET))
              .findFirst();
      Assert.assertTrue(uplinkPort.isPresent(), "HUAWEI No uplink port is present");
      Assert.assertEquals( uplinkPort.get().getLifeCycleState(), LifeCycleState.OPERATING, "Uplink port state after commissioning is not in operating state");
    }

    // check pon ports lifecycle state
    boolean allPortsInOperatingState = portList.stream().map(Port::getLifeCycleState).allMatch(LifeCycleState.OPERATING::equals);
    Assert.assertTrue(allPortsInOperatingState, "Some port is in not OPERATING state");

    // check uplink state
    List<Uplink> uplinkList = deviceResourceInventoryManagementClient.getClient().uplink().listUplink()
            .portsEquipmentBusinessRefEndSzQuery(oltEndSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    Assert.assertEquals(uplinkList.size(), 1, "There is no uplink");
    Uplink uplink = uplinkList.get(0);
    Assert.assertEquals(uplink.getState(), UplinkState.ACTIVE, "UplinkState is not active");

    // check ANCP Session
    List<AncpSession> ancpSessionList = deviceResourceInventoryManagementClient.getClient().ancpSession().listAncpSession()
            .accessNodeEquipmentBusinessRefEndSzQuery(oltEndSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    Assert.assertEquals(ancpSessionList.size(), 1L, "ancpSessionList.size missmatch");
    Assert.assertEquals(ancpSessionList.get(0).getConfigurationStatus() , "ACTIVE", "ANCP ConfigurationStatus is not active");


    List<AccessLineDto> wgAccessLines = accessLineResourceInventoryClient.getClient().accessLineController().searchAccessLines()
            .body(new SearchAccessLineDto().endSz(oltEndSz)).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)))
            .stream().filter(accessLineDto -> accessLineDto.getStatus().equals(AccessLineStatus.WALLED_GARDEN)).collect(Collectors.toList());
    long wgLinesCount = wgAccessLines.size();

    Assert.assertEquals(wgLinesCount, portsCount * accessLinesPerPort, "wgLinesCount mismatch");

    List<Integer> anpTagsList = wgAccessLines.stream().map(accessLineDto -> accessLineDto.getAnpTag().getAnpTag())
            .filter(anpTagValue -> anpTagValue >= 128).collect(Collectors.toList());

    Assert.assertEquals(anpTagsList.size(), portsCount * accessLinesPerPort, "anpTagsList size mismatch");

    Assert.assertTrue(anpTagsList.contains(128), "anpTagsList contains mismatch");

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
