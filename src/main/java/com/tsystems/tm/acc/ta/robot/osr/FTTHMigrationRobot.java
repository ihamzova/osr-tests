package com.tsystems.tm.acc.ta.robot.osr;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.ResponseSpecBuilders;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.*;
import com.tsystems.tm.acc.ta.data.osr.models.AncpIpSubnetData;
import com.tsystems.tm.acc.ta.data.osr.models.AncpSessionData;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.model.*;
import com.tsystems.tm.acc.tests.osr.olt.discovery.v2_1_0.client.invoker.JSON;
import com.tsystems.tm.acc.tests.osr.olt.discovery.v2_1_0.client.model.*;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.*;
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

    private DeviceResourceInventoryManagementClient deviceResourceInventoryManagementClient = new DeviceResourceInventoryManagementClient(authTokenProvider);
    private OltDiscoveryClient oltDiscoveryClient = new OltDiscoveryClient(authTokenProvider);
    private AncpResourceInventoryManagementClient ancpResourceInventoryManagementClient = new AncpResourceInventoryManagementClient(authTokenProvider);
    private DeviceTestDataManagementClient deviceTestDataManagementClient = new DeviceTestDataManagementClient();


    @Step("Start device {oltDevice} discovery ")
    public void deviceDiscoveryStartDiscoveryTask(OltDevice oltDevice, String uuid) {

        log.info("deviceDiscoveryStartDiscoveryTask for endSz = {} uuid = {}", oltDevice.getEndsz(), uuid);

        String xCallbackUrl = new GigabitUrlBuilder(WIREMOCK_MS_NAME)
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

        List<EquipmentBusinessRef> equipmentBusinessRefs = new ArrayList<>();
        equipmentBusinessRefs.add(new EquipmentBusinessRef()
                .deviceType(DeviceType.OLT)
                .endSz(oltDevice.getEndsz())
                .portName(oltDevice.getOltPort())
                .portType(PortType.ETHERNET)
                .slotName(oltDevice.getOltSlot())
                .type("EquipmentBusinessRef"));

        equipmentBusinessRefs.add(new EquipmentBusinessRef()
                .deviceType(DeviceType.BNG)
                .endSz(oltDevice.getBngEndsz())
                .portName(oltDevice.getBngDownlinkPort())
                .portType(PortType.ETHERNET)
                .slotName(oltDevice.getBngDownlinkSlot())
                .type("EquipmentBusinessRef"));

        Uplink uplink = deviceResourceInventoryManagementClient.getClient().uplink().createUplink()
                .body(new UplinkCreate()
                        .lsz(UplinkLsz._4C1)
                        .ordnungsnummer(Integer.valueOf(oltDevice.getOrderNumber()))
                        .portsEquipmentBusinessRef(equipmentBusinessRefs)
                        .relatedParty(Collections.singletonList(new RelatedParty().id(COMPOSITE_PARTY_ID_DTAG.toString())))
                        .state(UplinkState.ACTIVE))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        Assert.assertEquals(uplink.getPortsEquipmentBusinessRef(),equipmentBusinessRefs, "create uplink equipmentBusinessRefs mismatch");
        Assert.assertEquals(uplink.getLsz(), UplinkLsz._4C1, "create uplink LSZ mismatch");
    }

    @Step("Create an AncpIpSubnetData entity")
    public String createAncpIpSubnet(AncpIpSubnetData ancpIpSubnetData, OltDevice oltDevice) {

        AncpIpSubnet ancpIpSubnet = deviceResourceInventoryManagementClient.getClient().ancpIpSubnet().createAncpIpSubnet()
                .body(new AncpIpSubnetCreate()
                        .ancpIpSubnetType("OLT")
                        .bngDownlinkPortEquipmentBusinessRef(new EquipmentBusinessRef()
                                .deviceType(DeviceType.BNG)
                                .endSz(oltDevice.getBngEndsz())
                                .portName(oltDevice.getBngDownlinkPort())
                                .portType(PortType.ETHERNET)
                                .slotName(oltDevice.getBngDownlinkSlot()))
                        .ipAddressBng(ancpIpSubnetData.getIpAddressBng())
                        .ipAddressBroadcast(ancpIpSubnetData.getIpAddressBroadcast())
                        .ipAddressLoopback(ancpIpSubnetData.getIpAddressLoopback())
                        .subnetMask(ancpIpSubnetData.getSubnetMask())
                        .rmkAccessId(ancpIpSubnetData.getRmkAccessId()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));


        Assert.assertEquals(ancpIpSubnetData.getIpAddressBng(), ancpIpSubnet.getIpAddressBng(), "IpAddressBng mismatch");
        //log.info("+++ ancpIpSubnet = {}", ancpIpSubnet.getId());
        return ancpIpSubnet.getId();
    }


    @Step("Create an AncpSession entity")
    public void createAncpSession(String ancpIpSubnetId, OltDevice oltDevice, AncpSessionData ancpSessionData) {
    
        deviceResourceInventoryManagementClient.getClient().ancpSession().createAncpSession()
                .body(new AncpSessionCreate()
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
                        .configurationStatus("ACTIVE")
                        .ipAddressAccessNode(ancpSessionData.getIpAddressAccessNode())
                        .ipAddressBng(ancpSessionData.getIpAddressBng())
                        .oltUplinkPortEquipmentBusinessRef(
                                new EquipmentBusinessRef()
                                        .deviceType(DeviceType.OLT)
                                        .endSz(oltDevice.getEndsz())
                                        .portType(PortType.ETHERNET)
                                        .slotName(oltDevice.getOltSlot())
                                        .portName(oltDevice.getOltPort())
                        )
                        .partitionId(ancpSessionData.getPartitionId())
                        .rmkEndpointId(ancpSessionData.getRmkEndpointId())
                        .sealConfigurationId(ancpSessionData.getSealConfigurationId())
                        .sessionId(ancpSessionData.getSessionId().toString())
                        .sessionType("OLT")
                        .vlan(ancpSessionData.getVlan())
                ).executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

    }

    @Step("Patch device and set lifeCycleState to OPERATING")
    public void patchDeviceLifeCycleState(Long oltDeviceId)  {
        log.info("patchDeviceLifeCycleState");
        deviceResourceInventoryManagementClient.getClient().device().patchDevice()
                .idPath(oltDeviceId)
                .body(Collections.singletonList(new JsonPatchOperation().op(JsonPatchOperation.OpEnum.ADD)
                        .from("string")
                        .path("/lifeCycleState")
                        .value("OPERATING")))
                .executeAs(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Checks olt data in olt-ri after migration process")
    public Long checkOltMigrationResult(OltDevice oltDevice, boolean uplinkAncpExist, String ancpIpSubnetId) {
        log.info("checkOltMigrationResult");
        String oltEndSz = oltDevice.getEndsz();

        List<Device> deviceList = deviceResourceInventoryManagementClient.getClient().device().listDevice()
                .endSzQuery(oltEndSz).depthQuery(3).executeAs(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));

        Assert.assertEquals(deviceList.size(), 1L, "device list mismatch");
        Device deviceAfterMigration = deviceList.get(0);
        Assert.assertEquals(deviceAfterMigration.getDeviceType(), DeviceType.OLT, "device type mismatch");
        Assert.assertEquals(deviceAfterMigration.getEndSz(), oltEndSz, "device EndSz mismatch");
        Assert.assertEquals(deviceAfterMigration.getRelatedParty().get(0).getId(), COMPOSITE_PARTY_ID_DTAG.toString(), "ddevice composite partyId mismatch");
        Long oltDeviceId = new Long(deviceAfterMigration.getId());
        Assert.assertTrue(oltDeviceId > 0);

        List<Port> portList  = deviceResourceInventoryManagementClient.getClient().port().listPort()
                .parentEquipmentRefEndSzQuery(oltEndSz)
                .portTypeQuery(PortType.PON)
                .depthQuery(3)
                .executeAs(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));

        Assert.assertEquals(portList.size(), oltDevice.getNumberOfPonSlots() * MA5600_PORTS_PER_GPON_CARD, "numbers of pon ports mismatch");

        List<Port> uplinkPortList  = deviceResourceInventoryManagementClient.getClient().port().listPort()
                .parentEquipmentRefEndSzQuery(oltEndSz)
                .parentEquipmentRefSlotNameQuery(oltDevice.getOltSlot())
                .portNameQuery(oltDevice.getOltPort())
                .depthQuery(3)
                .executeAs(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));

        Assert.assertEquals(uplinkPortList.size(), 1L,  "uplinkPort  not found");

        if (!uplinkAncpExist) {
            // check device lifecycle state
            Assert.assertEquals( deviceAfterMigration.getLifeCycleState(), LifeCycleState.NOT_OPERATING, "device without ANCP, device LifeCycleState mismatch");
            // check uplink port lifecycle state
            Assert.assertEquals( uplinkPortList.get(0).getLifeCycleState(), LifeCycleState.NOT_OPERATING, "device without ANCP, uplinkPort LifeCycleState mismatch");
        } else {
            // check device lifecycle state
            Assert.assertEquals( deviceAfterMigration.getLifeCycleState(), LifeCycleState.OPERATING, "device LifeCycleState mismatch");
            // check uplink port lifecycle state
            Assert.assertEquals( uplinkPortList.get(0).getLifeCycleState(), LifeCycleState.OPERATING, "uplinkPort LifeCycleState mismatch");

            List<Uplink> uplinkList = deviceResourceInventoryManagementClient.getClient().uplink().listUplink()
                    .portsEquipmentBusinessRefEndSzQuery(oltEndSz).executeAs(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
            Assert.assertEquals(uplinkList.size(), 1L, "uplinkList.size missmatch");
            Assert.assertEquals(uplinkList.get(0).getState(), UplinkState.ACTIVE,  "uplink not activ");

            List<AncpSession> ancpSessionList = deviceResourceInventoryManagementClient.getClient().ancpSession().listAncpSession()
                    .accessNodeEquipmentBusinessRefEndSzQuery(oltEndSz).executeAs(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
            Assert.assertEquals(ancpSessionList.size(), 1L, "ancpSessionList.size missmatch");
            Assert.assertEquals(ancpSessionList.get(0).getConfigurationStatus() , "ACTIVE", "ANCP ConfigurationStatus missmatch");

            List<com.tsystems.tm.acc.tests.osr.ancp.resource.inventory.management.v5_0_0.client.model.AncpIpSubnet>
                    ancpIpSubnetList = ancpResourceInventoryManagementClient.getClient().ancpIpSubnet().listAncpIpSubnet()
                    .bngDownlinkPortEquipmentBusinessRefEndSzQuery(oltDevice.getBngEndsz())
                    .bngDownlinkPortEquipmentBusinessRefSlotNameQuery(oltDevice.getBngDownlinkSlot())
                    .bngDownlinkPortEquipmentBusinessRefPortNameQuery(oltDevice.getBngDownlinkPort())
                    .executeAs(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
            Assert.assertEquals(ancpIpSubnetList.size(), 1L, "ancpIpSubnetList.size missmatch");
            Assert.assertEquals(ancpIpSubnetList.get(0).getId(), ancpIpSubnetId, "ancpIpSubnetId missmatch");

            List<com.tsystems.tm.acc.tests.osr.ancp.resource.inventory.management.v5_0_0.client.model.AncpSession>
                    ancpSessionList2 = ancpResourceInventoryManagementClient.getClient().ancpSession().listAncpSession()
                    .accessNodeEquipmentBusinessRefEndSzQuery(oltEndSz)
                    .executeAs(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
            Assert.assertEquals(ancpSessionList2.size(), 1L, "ancpSessionList2.size missmatch");
            Assert.assertEquals(ancpSessionList2.get(0).getConfigurationStatus(), "ACTIVE", "ANCP ConfigurationStatus missmatch");

        }
        return oltDeviceId;
    }

    @Step("Clear {oltDevice} device in olt-resource-inventory database")
    public void clearResourceInventoryDataBase(OltDevice oltDevice) {
        String endSz = oltDevice.getEndsz();
        deviceTestDataManagementClient.getClient().deviceTestDataManagement().deleteTestData().deviceEndSzQuery(endSz)
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

}
