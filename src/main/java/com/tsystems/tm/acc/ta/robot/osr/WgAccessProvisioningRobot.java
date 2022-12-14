package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.UnleashClient;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.WgAccessProvisioningClient;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.BusinessInformation;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.data.osr.models.Process;
import com.tsystems.tm.acc.ta.data.upiter.UpiterConstants;
import com.tsystems.tm.acc.ta.helpers.osr.logs.LogConverter;
import com.tsystems.tm.acc.ta.log.ContainsExpecter;
import com.tsystems.tm.acc.ta.log.ServiceLogExpectSince;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.client.model.Card;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.Device;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.Port;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.PortAndHomeIdDto;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.v2_9_1.client.model.AccessLineIdDto;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.v2_9_1.client.model.CardRequestDto;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.v2_9_1.client.model.DeviceDto;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.v2_9_1.client.model.PortDto;
import de.telekom.it.t3a.kotlin.log.ServiceDiscoveryStrategy;
import de.telekom.it.t3a.kotlin.log.query.ServiceDescriptor;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_ACCEPTED_202;
import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static com.tsystems.tm.acc.ta.wiremock.ExtendedWireMock.CONSUMER_ENDPOINT;
import static com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.Port.PortTypeEnum.PON;
import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;

@Slf4j
public class WgAccessProvisioningRobot {
    private static final String CORRELATION_ID = UUID.randomUUID().toString();
    private ServiceLogExpectSince logExpect;
    private final WgAccessProvisioningClient wgAccessProvisioningClient = new WgAccessProvisioningClient();
    private final OntOltOrchestratorRobot ontOltOrchestratorRobot = new OntOltOrchestratorRobot();
    private final UnleashClient unleashClient = new UnleashClient();

    @Step("Start port provisioning")
    public void startPortProvisioning(PortProvisioning port) {
        wgAccessProvisioningClient.getClient().provisioningProcessV2().startPortProvisioningV2()
                .body(new PortDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber())
                        .portNumber(port.getPortNumber()))
                .executeAs(checkStatus(HTTP_CODE_ACCEPTED_202));
    }

    @Step("Start card provisioning for 1 card")
    public void startCardProvisioning(PortProvisioning port) {
        wgAccessProvisioningClient.getClient().provisioningProcessV2().startCardProvisioningV2()
                .body(new CardRequestDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber()))
                .executeAs(checkStatus(HTTP_CODE_ACCEPTED_202));
    }

    @Step("Start device provisioning")
    public void startDeviceProvisioning(PortProvisioning port) {
        wgAccessProvisioningClient.getClient().provisioningProcessV2().startDeviceProvisioningV2()
                .body(new DeviceDto()
                        .endSz(port.getEndSz()))
                .executeAs(checkStatus(HTTP_CODE_ACCEPTED_202));
    }

    @Step("Start port deprovisioning")
    public void startPortDeprovisioning(PortProvisioning port, boolean noDeconfigInSEAL) {
        wgAccessProvisioningClient.getClient().deprovisioningProcessV2()
                .startPortDeprovisioningV2()
                .noDeconfigInSEALQuery(noDeconfigInSEAL)
                .body(new PortDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber())
                        .portNumber(port.getPortNumber()))
                .executeAs(checkStatus(HTTP_CODE_ACCEPTED_202));
    }

    @Step("Start port deprovisioning with deprovisioningForDpu flag")
    public void startPortDeprovisioningForDpu(PortProvisioning port, boolean deprovisioningForDpu) {
        wgAccessProvisioningClient.getClient().deprovisioningProcessV2().startPortDeprovisioningV2()
                .body(new PortDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber())
                        .portNumber(port.getPortNumber()))
                .deprovisioningForDpuQuery(deprovisioningForDpu)
                .executeAs(checkStatus(HTTP_CODE_ACCEPTED_202));
    }

    @Step("Start card deprovisioning for 1 card")
    public void startCardDeprovisioningV2(PortProvisioning port, boolean noDeconfigInSEAL) {
        wgAccessProvisioningClient.getClient().deprovisioningProcessV2().startCardDeprovisioningV2().noDeconfigInSEALQuery(noDeconfigInSEAL)
                .body(new CardRequestDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber()))
                .executeAs(checkStatus(HTTP_CODE_ACCEPTED_202));
    }

    @Step("Start device deprovisioning")
    public void startDeviceDeprovisioning(PortProvisioning port, boolean noDeconfigInSEAL) {
        wgAccessProvisioningClient.getClient().deprovisioningProcessV2().startDeviceDeprovisioningV2().noDeconfigInSEALQuery(noDeconfigInSEAL)
                .body(new DeviceDto()
                        .endSz(port.getEndSz()))
                .executeAs(checkStatus(HTTP_CODE_ACCEPTED_202));
    }

    @Step("Start reconfiguration")
    public void startReconfiguration(String lineId) {
        wgAccessProvisioningClient.getClient()
                .provisioningProcessV2()
                .startUpdateLineProfilesV2()
                .body(new AccessLineIdDto().lineId(lineId))
                .execute(checkStatus(HTTP_CODE_ACCEPTED_202));
    }

    @Step("Collect wg-access-provisioning logs")
    public void startWgAccessProvisioningLog() throws InterruptedException {
        // Set a start time from which logs will be fetched

        logExpect = new ServiceLogExpectSince(new ServiceDescriptor(WG_ACCESS_PROVISIONING_MS, ServiceDiscoveryStrategy.APP));
        logExpect.attach(WG_ACCESS_PROVISIONING_MS, new ContainsExpecter("business_information"));
        Thread.sleep(10000);
    }

    @Step("Get businessInformation from log")
    public List<BusinessInformation> getBusinessInformation() throws InterruptedException {
        Thread.sleep(20000);
        logExpect.fetch();

        List<BusinessInformation> businessInformations = LogConverter.logsToBusinessInformationMessages(
                ((ContainsExpecter) logExpect
                        .getExpecterMap()
                        .get(WG_ACCESS_PROVISIONING_MS))
                        .getCatched());
        Assert.assertNotNull(businessInformations, "Business Info is not collected.");
        return businessInformations;
    }

    @Step("Start port provisioning and get processId")
    public String startPortProvisioningAndGetProcessId(Process process) {
        return wgAccessProvisioningClient.getClient().provisioningProcessV2().startPortProvisioningV2()
                .body(new PortDto()
                        .endSz(process.getEndSz())
                        .slotNumber(process.getSlotNumber())
                        .portNumber(process.getPortNumber()))
                .executeAs(checkStatus(HTTP_CODE_ACCEPTED_202)).getProcessInstanceId();
    }

    @Step("Start postprovisioning")
    public void startPostprovisioning(PortProvisioning port) {
        wgAccessProvisioningClient
                .getClient()
                .postProvisioningProcessController()
                .postProvisioning()
                .xCallbackCorrelationIdHeader(CORRELATION_ID)
                .xCallbackUrlHeader(new GigabitUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .xCallbackErrorUrlHeader(new GigabitUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .body(new PortDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber())
                        .portNumber(port.getPortNumber()))
                .execute(checkStatus(HTTP_CODE_ACCEPTED_202));
    }

    @Step("Prepare data for postprovisioning")
    public void prepareForPostprovisioning(int linesCount, AccessLine accessLine) {
        for (int i = 0; i < linesCount; i++) {
            ontOltOrchestratorRobot.reserveAccessLineByPortAndHomeId(new PortAndHomeIdDto().homeId(accessLine.getHomeId())
                    .portNumber(accessLine.getPortNumber())
                    .slotNumber(accessLine.getSlotNumber())
                    .fachSz(accessLine.getOltDevice().getFsz())
                    .vpSz(accessLine.getOltDevice().getVpsz())
                    .homeId(accessLine.getHomeId())
                    .portType(PortAndHomeIdDto.PortTypeEnum.PON)); //assigned linesCount
        }
    }

    @Step("Get PON Ports")
    public List<Port> getPonPorts(PortProvisioning port) {
        return getDevice(port).getPorts().stream()
                .filter(ponPort -> ponPort.getPortType().getValue().equals(PON.toString()))
                .collect(Collectors.toList());
    }

    @Step("Check card before provisioning")
    public Card getCard(PortProvisioning port) {
        URL cardUrl = new GigabitUrlBuilder("wiremock-acc")
                .withEndpoint("/resource-order-resource-inventory/v5/card")
                .withParameter("parentDeviceEquipmentRef.endSz", port.getEndSz())
                .withParameter("slotName", port.getSlotNumber())
                .withParameter("depth", "1").build();
        String response = RestAssured.given().when().get(cardUrl.toString().replace("%2F", "/"))
                .then().extract().body().asString().replaceFirst("\"lastDiscovery\": \".+\",\n", "");
        final Card[] result = OltResourceInventoryClient.json().deserialize(response, Card[].class);
        return result[0];
    }

    @Step("Check device before/after provisioning")
    public Device getDevice(PortProvisioning port) {
        URL deviceUrl = new GigabitUrlBuilder("wiremock-acc")
                .withEndpoint("/api/oltResourceInventory/v1/olt")
                .withParameter("endSZ", port.getEndSz()).build();
        String response = RestAssured.given().when().get(deviceUrl.toString().replace("%2F", "/"))
                .then().extract().body().asString().replaceFirst("\"lastDiscovery\": \".+\",\n", "");
        return OltResourceInventoryClient.json().deserialize(response, Device.class);
    }

    @Step("Check port after provisioning")
    public PortProvisioning getPortProvisioning(String endSz, String slotNumber, String portNumber, PortProvisioning port) {
        PortProvisioning portBeforeProvisioning = new PortProvisioning();
        portBeforeProvisioning.setEndSz(endSz);
        portBeforeProvisioning.setSlotNumber(slotNumber);
        portBeforeProvisioning.setPortNumber(portNumber);
        portBeforeProvisioning.setHomeIdPool(port.getHomeIdPool());
        portBeforeProvisioning.setBackhaulId(port.getBackhaulId());
        portBeforeProvisioning.setDefaultNEProfilesActive(port.getDefaultNEProfilesActive());
        portBeforeProvisioning.setDefaultNetworkLineProfilesActive(port.getDefaultNetworkLineProfilesActive());
        portBeforeProvisioning.setAccessLinesWG(port.getAccessLinesWG());
        return portBeforeProvisioning;
    }

    @Step("enable-64-pon-splitting - ??hange feature toggle state")
    public void changeFeatureToogleEnable64PonSplittingState(boolean toggleState) {
        if (toggleState) {
            unleashClient.enableToggle(FEATURE_TOGGLE_ENABLE_64_PON_SPLITTING);
        } else {
            unleashClient.disableToggle(FEATURE_TOGGLE_ENABLE_64_PON_SPLITTING);
        }
        log.info("toggleState for {} = {}", FEATURE_TOGGLE_ENABLE_64_PON_SPLITTING, toggleState);
    }

    @Step("enable-64-pon-splitting - get feature toggle state")
    public boolean getFeatureToogleEnable64PonSplittingState() {
        return unleashClient.isToggleEnabled(FEATURE_TOGGLE_ENABLE_64_PON_SPLITTING);
    }

    @Step("disable-home-id-pools-creation - ??hange feature toggle state")
    public void changeFeatureToggleHomeIdPoolState(boolean toggleState) {
        if (toggleState) {
            unleashClient.enableToggle(FEATURE_TOGGLE_CREATE_HOME_ID_POOL);
        } else {
            unleashClient.disableToggle(FEATURE_TOGGLE_CREATE_HOME_ID_POOL);
        }
        log.info("toggleState for {} = {}", FEATURE_TOGGLE_CREATE_HOME_ID_POOL, toggleState);
    }
}
