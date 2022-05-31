package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.ResponseSpecBuilders;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.UnleashClient;
import com.tsystems.tm.acc.ta.api.osr.*;
import com.tsystems.tm.acc.ta.data.osr.enums.DevicePortLifeCycleStateUI;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltCommissioningPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDetailsPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDiscoveryPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_35_0.client.model.*;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.model.PortType;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.model.*;
import com.tsystems.tm.api.client.olt.commissioning.model.OltCommissioningDto;
import com.tsystems.tm.api.client.olt.commissioning.model.UplinkDto;
import com.tsystems.tm.api.client.olt.material.catalog.external.model.DeviceTemplate;
import com.tsystems.tm.api.client.osr.process.log.model.ProcessLogListItemDTO;
import com.tsystems.tm.api.client.osr.process.log.model.ProcessStatus;
import com.tsystems.tm.api.client.osr.process.log.model.ResolutionStatus;
import de.telekom.it.magic.api.keycloak.AuthorizationCodeTokenProvider;
import io.qameta.allure.Owner;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.*;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.EMS_NBI_NAME_SDX6320_16;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.SERVICE_OLT_RESOURCE_INVENTORY_UI_UPLINK_IMPORT;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static de.telekom.it.magic.api.keycloak.AuthorizationCodeTokenProviderKt.getProviderAuthorizationCodeTokenProvider;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Slf4j
public class OltCommissioningRobot {

    private static final Integer TIMEOUT_FOR_OLT_COMMISSIONING = 40 * 60_000;
    private static final Integer TIMEOUT_FOR_CARD_PROVISIONING = 20 * 60_000;
    private static final Integer TIMEOUT_FOR_ADTRAN_PROVISIONING = 40 * 60_000;
    private static final Integer HOME_ID_POOL_PER_PORT = 0;

    private static final AuthTokenProvider authTokenProviderOltCommissioning = new RhssoClientFlowAuthTokenProvider(OLT_COMMISSIONING_MS/*, RhssoHelper.getSecretOfGigabitHub(OLT_COMMISSIONING_MS)*/);
    private static final AuthTokenProvider authTokenProviderOltBffProxy = new RhssoClientFlowAuthTokenProvider(OLT_BFF_PROXY_MS/*, RhssoHelper.getSecretOfGigabitHub(OLT_BFF_PROXY_MS)*/);
    private static final AuthTokenProvider authTokenProviderAccessProcessManagementBff = new RhssoClientFlowAuthTokenProvider(ACCESS_PROCESS_MANAGEMENT_BFF);

    private DeviceResourceInventoryManagementClient deviceResourceInventoryManagementClient = new DeviceResourceInventoryManagementClient(authTokenProviderOltBffProxy);
    private OltDiscoveryClient oltDiscoveryClient = new OltDiscoveryClient(authTokenProviderOltCommissioning);
    private AccessLineResourceInventoryFillDbClient accessLineResourceInventoryFillDbClient = new AccessLineResourceInventoryFillDbClient(authTokenProviderOltBffProxy);
    private DeviceTestDataManagementClient deviceTestDataManagementClient = new DeviceTestDataManagementClient();
    private OsrProcessLogClient osrProcessLogClient = new OsrProcessLogClient(authTokenProviderAccessProcessManagementBff);
    private OltMaterialCatalogClient oltMaterialCatalogClient = new OltMaterialCatalogClient(authTokenProviderOltCommissioning);

    private UnleashClient unleashClient = new UnleashClient();

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
        assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.OPERATING.toString(), "Device LifeCycleState after Commissioning mismatch");
        oltDetailsPage.openPortView(olt.getOltSlot());
        assertEquals(oltDetailsPage.getPortLifeCycleState(olt.getOltSlot(), olt.getOltPort()), DevicePortLifeCycleStateUI.OPERATING.toString(), "Port LifeCycleState after Commissioning mismatch");
        oltDetailsPage.checkGponPortLifeCycleState(olt, DevicePortLifeCycleStateUI.OPERATING.toString());
    }

    @Owner("TMI")
    @Step("Starts automatic olt commissioning via API")
    public String startOltCommissioningViaApi(OltDevice oltDevice, Credentials credentials) {
        AuthorizationCodeTokenProvider tokenProvider = getProviderAuthorizationCodeTokenProvider(credentials.getLogin(), credentials.getPassword());
        String endSz = oltDevice.getVpsz().concat("/").concat(oltDevice.getFsz());
        OltCommissioningDto oltCommissioningDto = new OltCommissioningDto()
                .endSZ(endSz)
                .uplinkDto(new UplinkDto()
                        .bngEndSz(oltDevice.getBngEndsz())
                        .oltEndSz(endSz)
                        .oltSlot(oltDevice.getOltSlot())
                        .oltPortNumber(oltDevice.getOltPort())
                        .bngSlot(oltDevice.getBngDownlinkSlot())
                        .bngPortNumber(oltDevice.getBngDownlinkPort())
                        .lsz(UplinkDto.LszEnum.fromValue(oltDevice.getLsz()))
                        .orderNumber(Integer.parseInt(oltDevice.getOrderNumber())));

        return new OltCommissioningClient(tokenProvider, tokenProvider).getClient()
                                                                       .oltCommissioningV2()
                                                                       .startDeviceCommissioningV2()
                                                                       .body(oltCommissioningDto)
                                                                       .executeAs(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202))).getId().toString();
    }

    @Owner("TMI")
    @Step("Waits until olt commissioning process ends")
    public void checkOltCommissioningIsFinished(OltDevice oltDevice, String processId) {
        String oltEndSz = oltDevice.getVpsz().concat("/").concat(oltDevice.getFsz());
        await()
                .atMost(15, TimeUnit.MINUTES)
                .and().pollInterval(40, TimeUnit.SECONDS)
                .and().pollInSameThread()
                .untilAsserted(() -> {
                    ProcessLogListItemDTO processLog = osrProcessLogClient.getClient().centralProcessLog()
                                                                          .getProcessLogs()
                                                                          .processInstanceIdQuery(processId)
                                                                          .domainReferenceIdQuery(oltEndSz)
                                                                          .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)))
                                                                          .getProcesslogs().stream()
                                                                          .filter(process -> Objects.equals(process.getProcessId(), "OltCommissioning"))
                                                                          .findAny().orElseThrow(() -> new AssertionError("Olt commissioning processId wasn't found"));

                    assertThat(processLog.getProcessStatus()).isEqualTo(ProcessStatus.COMPLETED);
                    assertThat(processLog.getProcessResolutionStatus()).isEqualTo(ResolutionStatus.SUCCESSFUL);
                });
    }

    @Step("Starts manual olt commissioning process")
    public void startManualOltCommissioning(OltDevice olt) {
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        oltSearchPage = oltSearchPage.searchNotDiscoveredByParameters(olt);

        OltDiscoveryPage oltDiscoveryPage = oltSearchPage.pressManualCommissionigButton();

        oltDiscoveryPage.validateUrl();
        oltDiscoveryPage = oltDiscoveryPage.makeOltDiscovery();
        oltDiscoveryPage = oltDiscoveryPage.saveDiscoveryResults();

        oltSearchPage = oltDiscoveryPage.openOltSearchPage();

        OltDetailsPage oltDetailsPage = oltSearchPage.searchDiscoveredOltByParameters(olt);
        oltDetailsPage.validateUrl();
        assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString(), "Device LifeCycleState before commissioning mismatch");
        oltDetailsPage.openPortView(olt.getOltSlot());
        assertEquals(oltDetailsPage.getPortLifeCycleState(olt.getOltSlot(), olt.getOltPort()), DevicePortLifeCycleStateUI.NOTOPERATING.toString(), "Ethernet Port LifeCycleState before ANCP configuration  mismatch");
        oltDetailsPage.checkGponPortLifeCycleState(olt, DevicePortLifeCycleStateUI.NOTOPERATING.toString());

        oltDetailsPage.startUplinkConfiguration();
        oltDetailsPage.saveUplinkConfiguration();

        oltDetailsPage.configureAncpSessionStart();
        oltDetailsPage.updateAncpSessionStatus();
        oltDetailsPage.checkAncpSessionStatus();

        assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.OPERATING.toString(), "Device LifeCycleState after ANCP configuration is not in operating state");
        oltDetailsPage.openPortView(olt.getOltSlot());
        assertEquals(oltDetailsPage.getPortLifeCycleState(olt.getOltSlot(), olt.getOltPort()), DevicePortLifeCycleStateUI.OPERATING.toString(), "Ethernet Port LifeCycleState after ANCP configuration is not in operating state");

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
        long accessLinesPerPort = getPreProvisioningAccessLineCount(olt);

        AuthTokenProvider authTokenProviderWgAccessProvisioning = new RhssoClientFlowAuthTokenProvider(WG_ACCESS_PROVISIONING_MS, RhssoHelper.getSecretOfGigabitHub(WG_ACCESS_PROVISIONING_MS));
        AccessLineResourceInventoryClient accessLineResourceInventoryClient = new AccessLineResourceInventoryClient(authTokenProviderWgAccessProvisioning);

        List<Device> deviceList = deviceResourceInventoryManagementClient.getClient().device().listDevice()
                                                                         .endSzQuery(oltEndSz).depthQuery(3).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        assertEquals(deviceList.size(), 1L, "Device is not present");
        assertEquals(deviceList.get(0).getDeviceType(), DeviceType.OLT, "Device type is not OLT");
        assertEquals(deviceList.get(0).getEndSz(), oltEndSz, "Device EndSz mismatch");
        Device deviceAfterCommissioning = deviceList.get(0);

        List<Port> portList = deviceResourceInventoryManagementClient.getClient().port().listPort()
                                                                     .parentEquipmentRefEndSzQuery(oltEndSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        if (deviceList.get(0).getEmsNbiName().equals(EMS_NBI_NAME_SDX6320_16)) {
            assertEquals(portList.size(), olt.getNumberOfPonPorts() + olt.getNumberOfEthernetPorts(), "Ports number by Adtran mismatch");
            portsCount = olt.getNumberOfPonPorts();
        } else {
            portsCount = portList.stream()
                                 .filter(port -> port.getPortType().equals(PortType.PON)).count();
        }

        // check device lifecycle state
        assertEquals(deviceAfterCommissioning.getLifeCycleState(), LifeCycleState.OPERATING, "Device LifeCycleState after commissioning is not in operating state");

        // check uplink port lifecycle state
        if (deviceList.get(0).getEmsNbiName().equals(EMS_NBI_NAME_SDX6320_16)) {
            Optional<Port> uplinkPort = portList.stream()
                                                .filter(port -> port.getPortName().equals(olt.getOltPort()))
                                                .filter(port -> port.getPortType().equals(PortType.ETHERNET))
                                                .findFirst();
            assertTrue(uplinkPort.isPresent(), "ADTRAN No uplink port is present");
            assertEquals(uplinkPort.get().getLifeCycleState(), LifeCycleState.OPERATING, "Uplink port state after commissioning is not in operating state");
        } else {
            Optional<Port> uplinkPort = portList.stream()
                                                .filter(port -> port.getParentEquipmentRef().getSlotName().equals(olt.getOltSlot()))
                                                .filter(port -> port.getPortName().equals(olt.getOltPort()))
                                                .filter(port -> port.getPortType().equals(PortType.ETHERNET))
                                                .findFirst();
            assertTrue(uplinkPort.isPresent(), "HUAWEI No uplink port is present");
            assertEquals(uplinkPort.get().getLifeCycleState(), LifeCycleState.OPERATING, "Uplink port state after commissioning is not in operating state");
        }

        // check pon ports lifecycle state
        boolean allPortsInOperatingState = portList.stream()
                                                   .filter(port -> port.getPortType().equals(PortType.PON))
                                                   .map(Port::getLifeCycleState).allMatch(LifeCycleState.OPERATING::equals);
        assertTrue(allPortsInOperatingState, "Some port is in not OPERATING state");

        List<AccessLineDto> wgAccessLines = accessLineResourceInventoryClient.getClient().accessLineController().searchAccessLines()
                                                                             .body(new SearchAccessLineDto().endSz(oltEndSz)).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)))
                                                                             .stream().filter(accessLineDto -> accessLineDto.getStatus().equals(AccessLineStatus.WALLED_GARDEN)).collect(Collectors.toList());
        long wgLinesCount = wgAccessLines.size();

        assertEquals(wgLinesCount, portsCount * accessLinesPerPort, "wgLinesCount mismatch");

        List<Integer> anpTagsList = wgAccessLines.stream().map(accessLineDto -> accessLineDto.getAnpTag().getAnpTag())
                                                 .filter(anpTagValue -> anpTagValue >= 128).collect(Collectors.toList());

        assertEquals(anpTagsList.size(), portsCount * accessLinesPerPort, "anpTagsList size mismatch");

        assertTrue(anpTagsList.contains(128), "anpTagsList contains mismatch");

        long homeIdCount = accessLineResourceInventoryClient.getClient().homeIdController().searchHomeIds()
                                                            .body(new SearchHomeIdDto().endSz(oltEndSz)).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)))
                                                            .stream().filter(homeIdDto -> homeIdDto.getStatus().equals(HomeIdStatus.FREE)).count();

        assertEquals(homeIdCount, portsCount * HOME_ID_POOL_PER_PORT, "HomeIdCount mismatch");

        long backhaulIdCount = accessLineResourceInventoryClient.getClient().backhaulIdController().searchBackhaulIds()
                                                                .body(new SearchBackhaulIdDto().endSz(oltEndSz)).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)))
                                                                .stream().filter(backhaulIdDto -> BackhaulStatus.CONFIGURED.equals(backhaulIdDto.getStatus())).count();

        assertEquals(backhaulIdCount, portsCount, "backhaulIdCount mismatched with portsCount");
    }


    @Step("Check uplink and ancp-session data from olt-uplink-management and ancp-configuration")
    public void checkUplink(OltDevice oltDevice) {

        // check uplink state
        List<Uplink> uplinkList = deviceResourceInventoryManagementClient.getClient().uplink().listUplink()
                                                                         .portsEquipmentBusinessRefEndSzQuery(oltDevice.getEndsz()).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        assertEquals(uplinkList.size(), 1L, "uplinkList.size missmatch");
        assertEquals(uplinkList.get(0).getState(), UplinkState.ACTIVE, "UplinkState is not active");

        // check Slot Port configuration
        assertEquals(uplinkList.get(0).getPortsEquipmentBusinessRef().size(), 2, "getPortsEquipmentBusinessRef.size missmatch");

        EquipmentBusinessRef equipmentBusinessRef = uplinkList.get(0).getPortsEquipmentBusinessRef().get(0);
        if (equipmentBusinessRef.getDeviceType() == DeviceType.OLT) {
            assertEquals(equipmentBusinessRef.getPortName(), oltDevice.getOltPort(), "OLT PortName missmatch 0");
            assertEquals(equipmentBusinessRef.getSlotName(), oltDevice.getOltSlot(), "OLT SlotName missmatch 0");
        }
        if (equipmentBusinessRef.getDeviceType() == DeviceType.BNG) {
            assertEquals(equipmentBusinessRef.getPortName(), oltDevice.getBngDownlinkPort(), "BNG PortName missmatch 0");
            assertEquals(equipmentBusinessRef.getSlotName(), oltDevice.getBngDownlinkSlot(), "BNG SlotName missmatch 0");
        }
        equipmentBusinessRef = uplinkList.get(0).getPortsEquipmentBusinessRef().get(1);
        if (equipmentBusinessRef.getDeviceType() == DeviceType.OLT) {
            assertEquals(equipmentBusinessRef.getPortName(), oltDevice.getOltPort(), "OLT PortName missmatch 1");
            assertEquals(equipmentBusinessRef.getSlotName(), oltDevice.getOltSlot(), "OLT SlotName missmatch 1");
        }
        if (equipmentBusinessRef.getDeviceType() == DeviceType.BNG) {
            assertEquals(equipmentBusinessRef.getPortName(), oltDevice.getBngDownlinkPort(), "BNG PortName missmatch 1");
            assertEquals(equipmentBusinessRef.getSlotName(), oltDevice.getBngDownlinkSlot(), "BNG SlotName missmatch 1");
        }

        // check ANCP Session
        List<AncpSession> ancpSessionList = deviceResourceInventoryManagementClient.getClient().ancpSession().listAncpSession()
                                                                                   .accessNodeEquipmentBusinessRefEndSzQuery(oltDevice.getEndsz()).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        assertEquals(ancpSessionList.size(), 1L, "ancpSessionList.size missmatch");
        assertEquals(ancpSessionList.get(0).getConfigurationStatus(), "ACTIVE", "ANCP ConfigurationStatus missmatch");

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
        deviceTestDataManagementClient.getClient().deviceTestDataManagement().deleteTestData().deviceEndSzQuery(endSz)
                                      .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("enable unleash feature toggle: service.olt-resource-inventory-ui.uplink-import")
    public void enableFeatureToogleUiUplinkImport() {
        unleashClient.enableToggle(SERVICE_OLT_RESOURCE_INVENTORY_UI_UPLINK_IMPORT);
    }

    @Step(" get the count of pre-provisioning access lines from olt-material-catalog")
    public Integer getPreProvisioningAccessLineCount(OltDevice oltDevice) {
        DeviceTemplate deviceTemplate = oltMaterialCatalogClient.getClient().deviceTemplateController()
                .findDeviceTemplateByEmsNbiName().emsnbinamePath(oltDevice.getBezeichnung())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        String str =  deviceTemplate.getPreprovisioningStrategy();
        log.info("getPreprovisioningStrategy {} for emsNbiName = {}", str, oltDevice.getBezeichnung() );

        StringBuffer strBuff = new StringBuffer();
        for (int i = 0; i < str.length() ; i++) {
            if (Character.isDigit(str.charAt(i))) {
                strBuff.append(str.charAt(i));
            } else {
                break;
            }
        }

        return Integer.parseInt(strBuff.toString());
    }
}
