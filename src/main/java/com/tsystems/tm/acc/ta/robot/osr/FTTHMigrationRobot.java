package com.tsystems.tm.acc.ta.robot.osr;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.ResponseSpecBuilders;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.AncpConfigurationClient;
import com.tsystems.tm.acc.ta.api.osr.OltDiscoveryClient;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.mercury.MercuryConstants;
import com.tsystems.tm.acc.ta.data.osr.models.AncpIpSubnetData;
import com.tsystems.tm.acc.ta.data.osr.models.AncpSessionData;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.tests.osr.ancp.configuration.v3_0_0.client.model.*;
import com.tsystems.tm.acc.tests.osr.olt.discovery.v2_1_0.client.invoker.JSON;
import com.tsystems.tm.acc.tests.osr.olt.discovery.v2_1_0.client.model.*;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.*;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_ACCEPTED_202;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.COMPOSITE_PARTY_ID_DTAG;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.OLT_BFF_PROXY_MS;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.WIREMOCK_MS_NAME;
import static com.tsystems.tm.acc.tests.osr.a10nsp.inventory.internal.client.invoker.ResponseSpecBuilders.shouldBeCode;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Slf4j

public class FTTHMigrationRobot {

    static final String DISCOVRY_CALLBACK_PATH = "/autotestCbDiscoveryStart/";
    static final Long MA5600_PORTS_PER_GPON_CARD = 8L;

    private static final AuthTokenProvider authTokenProvider = new RhssoClientFlowAuthTokenProvider(OLT_BFF_PROXY_MS, RhssoHelper.getSecretOfGigabitHub(OLT_BFF_PROXY_MS));

    private OltResourceInventoryClient oltResourceInventoryClient = new OltResourceInventoryClient(authTokenProvider);
    private AncpConfigurationClient ancpConfigurationClient = new AncpConfigurationClient(authTokenProvider);
    private OltDiscoveryClient oltDiscoveryClient = new OltDiscoveryClient(authTokenProvider);


    @Step("Start device {oltDevice} discovery ")
    public void deviceDiscoveryStartDiscoveryTask(OltDevice oltDevice, String uuid) {

        log.info("deviceDiscoveryStartDiscoveryTask for endSz = {} uuid = {}", oltDevice.getEndsz(), uuid);

        String xCallbackUrl = new OCUrlBuilder(WIREMOCK_MS_NAME)
                .withEndpoint("/autotestCbDiscoveryStart/")
                .build()
                .toString();

        oltDiscoveryClient.getClient().discoveryControllerV2().startV2()
                .endszQuery(oltDevice.getEndsz())
                .modeQuery("MANUAL")
                .typeQuery("SEAL_PSL")
                .compositePartyIdQuery(COMPOSITE_PARTY_ID_DTAG)
                .xCallbackCorrelationIdHeader(uuid)
                .xCallbackIdHeader("DigiOSS")
                .xCallbackMethodHeader("POST")
                .xCallbackUrlHeader(xCallbackUrl)
                .xCallbackErrorUrlHeader(xCallbackUrl)
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_ACCEPTED_202)));
    }

    @Step("Check if callback to wiremock has happened")
    public void checkCallbackWiremock(String uuid, long timeout) {
        List<LoggedRequest> requests = WireMockFactory.get()
                .retrieve(
                        exactly(1),
                        newRequestPattern(RequestMethod.POST, urlPathEqualTo(DISCOVRY_CALLBACK_PATH))
                                .withHeader("X-Callback-Correlation-Id", equalTo(uuid)), timeout);

        //log.info("Discovery callback = {} ", requests);
        assertEquals(requests.size(),  1, "Callback was not received");

        DiscoveryResponseHolder discoveryResponseHolder = new JSON()
                .deserialize(requests.get(0).getBodyAsString(), DiscoveryResponseHolder.class);
        log.info("DiscoveryCallback discoveryResponseHolder = {} ", discoveryResponseHolder);
        assertTrue(discoveryResponseHolder.getSuccess() != null ? discoveryResponseHolder.getSuccess() : false, "callback success");
    }


    @Step("Get discovery status and check discovery status ")
    public void deviceDiscoveryGetDiscoveryStatusTask(OltDevice oltDevice, String uuid) {
        List<DiscoveryStatus> discoveryStatusList = oltDiscoveryClient.getClient().discoveryControllerV2().getStatusV2()
                .discoveryIdQuery(uuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        Assert.assertEquals(discoveryStatusList.size(), 1L);
        DiscoveryStatus discoveryStatus = discoveryStatusList.get(0);
        log.info("deviceDiscoveryGetDiscoveryStatusTask() discoveryStatus = {}", discoveryStatus);
        Assert.assertEquals(discoveryStatus.getDiscoveryId(), uuid, "GetDiscoveryStatus DiscoveryId mismatch");
        Assert.assertEquals(discoveryStatus.getEndsz(), oltDevice.getEndsz(), "GetDiscoveryStatus EndSz mismatch");
        Assert.assertEquals(discoveryStatus.getStatus(), DiscoveryStateEnum.DONE, "GetDiscoveryStatus discovery stete error");
        Assert.assertEquals(discoveryStatus.getType(), DiscoveryType.SEAL_PSL, "GetDiscoveryStatus discovery type mismatch");
        Assert.assertEquals(discoveryStatus.getMode(), DiscoveryMode.MANUAL, "GetDiscoveryStatus discovery mode mismatch");
    }

    @Step("Create discrepancy report. Delivers discrepancy data")
    public InventoryCompareResult deviceDiscoveryCreateDiscrepancyReportTask(OltDevice oltDevice, String uuid) {
        InventoryCompareResult inventoryCompareResult = oltDiscoveryClient.getClient()
                .discrepancyControllerV2()
                .compareWithResourceInventoryV2()
                .discoveryIdQuery(uuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        //log.info("inventoryCompareResult = {}", inventoryCompareResult);
        Assert.assertEquals(Objects.requireNonNull(inventoryCompareResult.getDevice()).getId(), oltDevice.getEndsz(), "InventoryCompareResult endSz missmatch");
        Assert.assertEquals(inventoryCompareResult.getDevice().getChangeStatus(), ComponentCompareResultChangeStatus.ADDED, "InventoryCompareResult changeStatus missmatch");
        Assert.assertEquals(inventoryCompareResult.getDevice().getAction(), ComponentCompareResultAction.UPDATE,  "InventoryCompareResult action missmatch");
        return inventoryCompareResult;
    }

    @Step("Apply device discovery discrepancy to inventory")
    public void deviceDiscoveryApplyDiscrepancyToInventoryTask(InventoryCompareResult inventoryCompareResult, String uuid ) {
        oltDiscoveryClient.getClient()
                .discrepancyControllerV2()
                .applyChangesToResourceInventoryV2()
                .discoveryIdQuery(uuid)
                .body(inventoryCompareResult)
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
        // response body is empty
    }

    @Step("Create an Ethernet link entity")
    public void createEthernetLink(OltDevice oltDevice) {
        UplinkDTO uplinkDTO = oltResourceInventoryClient.getClient().ethernetLinkInternalController().updateUplink()
                .body(new UplinkDTO()
                        .oltEndSz(oltDevice.getEndsz())
                        .orderNumber(Integer.valueOf(oltDevice.getOrderNumber()))
                        .oltSlot(oltDevice.getOltSlot())
                        .oltPortNumber(oltDevice.getOltPort())
                        .bngEndSz(oltDevice.getBngEndsz())
                        .bngSlot(oltDevice.getBngDownlinkSlot())
                        .bngPortNumber(oltDevice.getBngDownlinkPort())
                        .lsz(UplinkDTO.LszEnum._4C1))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        Assert.assertEquals(uplinkDTO.getOltEndSz(),oltDevice.getEndsz(), "create uplink OLT EndSZ mismatch");
        Assert.assertEquals(uplinkDTO.getOrderNumber(),Integer.valueOf(oltDevice.getOrderNumber()), "create uplink OrderNumber mismatch");
        Assert.assertEquals(uplinkDTO.getOltSlot(),oltDevice.getOltSlot(), "create uplink olt slot mismatch");
        Assert.assertEquals(uplinkDTO.getOltPortNumber(),oltDevice.getOltPort(), "create uplink olt port mismatch");
        Assert.assertEquals(uplinkDTO.getBngEndSz(),oltDevice.getBngEndsz(), "create uplink BNG EndSz mismatch");
        Assert.assertEquals(uplinkDTO.getBngSlot(),oltDevice.getBngDownlinkSlot(), "create uplink BNG slot mismatch");
        Assert.assertEquals(uplinkDTO.getBngPortNumber(),oltDevice.getBngDownlinkPort(), "create uplink BNG port mismatch");
        Assert.assertEquals(uplinkDTO.getLsz(), UplinkDTO.LszEnum._4C1, "create uplink LSZ mismatch");
    }

    @Step("Create an AncpIpSubnetData entity")
    public String createAncpIpSubnet(AncpIpSubnetData ancpIpSubnetData) {

        AncpIpSubnet ancpIpSubnet = ancpConfigurationClient.getClient().ancpIpSubnetV3().createAncpIpSubnetV3()
                .body(new AncpIpSubnetCreate()
                        .ipAddressBng(ancpIpSubnetData.getIpAddressBng())
                        .ipAddressBroadcast(ancpIpSubnetData.getIpAddressBroadcast())
                        .ipAddressLoopback(ancpIpSubnetData.getIpAddressLoopback())
                        .subnetMask(ancpIpSubnetData.getSubnetMask())
                        .rmkAccessId(ancpIpSubnetData.getRmkAccessId())
                        .atType(ancpIpSubnetData.getAtType())
                ).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        Assert.assertEquals(ancpIpSubnetData.getIpAddressBng(), ancpIpSubnet.getIpAddressBng(), "IpAddressBng mismatch");
        //log.info("+++ ancpIpSubnet = {}", ancpIpSubnet.getId());
        return ancpIpSubnet.getId();
    }


    @Step("Create an AncpSession entity")
    public void createAncpSession(String ancpIpSubnetId, OltDevice oltDevice, AncpSessionData ancpSessionData) {

        ancpConfigurationClient.getClient().ancpSessionV3().createAncpSessionV3()
                .body(new AncpSessionCreate()
                        .partitionId(ancpSessionData.getPartitionId())
                        .rmkEndpointId(ancpSessionData.getRmkEndpointId())
                        .sealConfigurationId(ancpSessionData.getSealConfigurationId())
                        .sessionId(ancpSessionData.getSessionId())
                        .sessionType(AncpSessionType.fromValue(ancpSessionData.getSessionType()))
                        .vlan(ancpSessionData.getVlan())
                        .accessNodeEquipmentBusinessRef(
                                new EquipmentBusinessRef()
                                        .deviceType(DeviceType.OLT)
                                        .endSz(oltDevice.getEndsz())
                                        .portType(PortType.ETHERNET)
                                        .slotName(oltDevice.getOltSlot())
                                        .portName(oltDevice.getOltPort())
                        )
                        .ancpIpSubnetRef(new EntityRef().id(ancpIpSubnetId))
                        .bngDownlinkPortEquipmentBusinessRef(
                                new EquipmentBusinessRef()
                                        .deviceType(DeviceType.BNG)
                                        .endSz(oltDevice.getBngEndsz())
                                        .portType(PortType.ETHERNET)
                                        .slotName(oltDevice.getBngDownlinkSlot())
                                        .portName(oltDevice.getBngDownlinkPort())
                        )
                        .configurationStatus(AncpConfigurationStatus.fromValue(ancpSessionData.getConfigurationStatus()))
                        .ipAddressAccessNode(ancpSessionData.getIpAddressAccessNode())
                        .oltUplinkPortEquipmentBusinessRef(
                                new EquipmentBusinessRef()
                                        .deviceType(DeviceType.OLT)
                                        .endSz(oltDevice.getEndsz())
                                        .portType(PortType.ETHERNET)
                                        .slotName(oltDevice.getOltSlot())
                                        .portName(oltDevice.getOltPort())
                        )).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

    }

    @Step("Patch device and  set lifeCycleState to OPERATING")
    public void patchDeviceLifeCycleState(Long oltDeviceId)  {
        oltResourceInventoryClient.getClient().deviceInternalController().patchDevice()
                .idPath(oltDeviceId)
                .body(Collections.singletonList(new JsonPatchOperation().op(JsonPatchOperation.OpEnum.ADD)
                        .from("string")
                        .path("/lifeCycleState")
                        .value("OPERATING")))
                .executeAs(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Checks olt data in olt-ri after migration process")
    public Long checkOltMigrationResult(OltDevice oltDevice, boolean uplinkAncpExist, String ancpIpSubnetId) {

        String oltEndSz = oltDevice.getEndsz();

        List<Device> deviceList = oltResourceInventoryClient.getClient().deviceInternalController().findDeviceByCriteria()
                .endszQuery(oltEndSz).executeAs(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertEquals(deviceList.size(), 1L, "device list mismatch");
        Device deviceAfterMigration = deviceList.get(0);
        Assert.assertEquals(deviceAfterMigration.getType(), Device.TypeEnum.OLT, "device type mismatch");
        Assert.assertEquals(deviceAfterMigration.getEndSz(), oltEndSz, "device EndSz mismatch");
        Assert.assertEquals(deviceAfterMigration.getCompositePartyId(), COMPOSITE_PARTY_ID_DTAG, "ddevice composite partyId mismatch");
        Long oltDeviceId = deviceAfterMigration.getId();
        Assert.assertTrue(oltDeviceId > 0);

        Optional<Integer> portsCountOptional = deviceAfterMigration.getEquipmentHolders().stream().map(EquipmentHolder::getCard)
                .filter(card -> card.getCardType().equals(Card.CardTypeEnum.GPON)).map(card -> card.getPorts().size()).reduce(Integer::sum);
        long portsCount = portsCountOptional.orElse(0);
        Assert.assertEquals(portsCount, oltDevice.getNumberOfPonSlots() * MA5600_PORTS_PER_GPON_CARD, "numbers of pon ports mismatch");

        Optional<Port> uplinkPort = deviceAfterMigration.getEquipmentHolders().stream()
                .filter(equipmentHolder -> equipmentHolder.getSlotNumber().equals(oltDevice.getOltSlot()))
                .map(EquipmentHolder::getCard)
                .filter(card -> card.getCardType().equals(Card.CardTypeEnum.UPLINK_CARD) || card.getCardType().equals(Card.CardTypeEnum.PROCESSING_BOARD))
                .flatMap(card -> card.getPorts().stream())
                .filter(port -> port.getPortNumber().equals(oltDevice.getOltPort())).findFirst();
        Assert.assertTrue(uplinkPort.isPresent(), "uplinkPort  not found");

        if (!uplinkAncpExist) {
            // check device lifecycle state
            Assert.assertEquals(Device.LifeCycleStateEnum.NOT_OPERATING, deviceAfterMigration.getLifeCycleState(), "device without ANCP, device LifeCycleState mismatch");
            // check uplink port lifecycle state
            Assert.assertEquals(Port.LifeCycleStateEnum.NOT_OPERATING, uplinkPort.get().getLifeCycleState(), "device without ANCP, uplinkPort LifeCycleState mismatch");
        } else {

            // check device lifecycle state
            Assert.assertEquals(Device.LifeCycleStateEnum.OPERATING, deviceAfterMigration.getLifeCycleState(), "device LifeCycleState mismatch");
            // check uplink port lifecycle state
            Assert.assertEquals(Port.LifeCycleStateEnum.OPERATING, uplinkPort.get().getLifeCycleState(), "uplinkPort LifeCycleState mismatch");

            List<UplinkDTO> uplinksList = oltResourceInventoryClient.getClient().ethernetLinkInternalController().findEthernetLinksByEndsz().oltEndSzQuery(oltEndSz)
                    .executeAs(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));

            Assert.assertEquals(uplinksList.size(), 1);
            UplinkDTO uplink = uplinksList.get(0);
            Assert.assertEquals(uplink.getIpStatus(), UplinkDTO.IpStatusEnum.ACTIVE, "uplink not activ");
            Assert.assertEquals(uplink.getAncpSessions().size(), 1, "ANCP session size");
            ANCPSession ancpSession = uplink.getAncpSessions().get(0);
            Assert.assertEquals(ancpSession.getSessionStatus(), ANCPSession.SessionStatusEnum.ACTIVE, "ANCP session not active");
            Assert.assertEquals(ancpSession.getIpSubnet().getId().toString(), ancpIpSubnetId, "ancpIpSubnetId mismatch");

        }
        return oltDeviceId;
    }

    @Step("Clear {oltDevice} device in olt-resource-inventory database")
    public void clearResourceInventoryDataBase(OltDevice oltDevice) {
        String endSz = oltDevice.getEndsz();
        oltResourceInventoryClient.getClient().testDataManagementController().deleteDevice().endszQuery(endSz)
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
    }

}
