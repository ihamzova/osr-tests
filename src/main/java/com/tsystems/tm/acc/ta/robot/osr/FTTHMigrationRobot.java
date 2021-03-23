package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.ResponseSpecBuilders;
import com.tsystems.tm.acc.ta.api.osr.AncpConfigurationClient;
import com.tsystems.tm.acc.ta.api.osr.OltDiscoveryClient;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.HttpConstants;
import com.tsystems.tm.acc.ta.data.mercury.MercuryConstants;
import com.tsystems.tm.acc.ta.data.osr.models.AncpIpSubnetData;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.tests.osr.ancp.configuration.v3_0_0.client.model.AncpIpSubnet;
import com.tsystems.tm.acc.tests.osr.ancp.configuration.v3_0_0.client.model.AncpIpSubnetCreate;
import com.tsystems.tm.acc.tests.osr.ancp.configuration.v3_0_0.client.model.AncpSession;
import com.tsystems.tm.acc.tests.osr.ancp.configuration.v3_0_0.client.model.AncpSessionCreate;
import com.tsystems.tm.acc.tests.osr.olt.discovery.v2_1_0.client.model.DiscoveryMode;
import com.tsystems.tm.acc.tests.osr.olt.discovery.v2_1_0.client.model.DiscoveryStateEnum;
import com.tsystems.tm.acc.tests.osr.olt.discovery.v2_1_0.client.model.DiscoveryStatus;
import com.tsystems.tm.acc.tests.osr.olt.discovery.v2_1_0.client.model.DiscoveryType;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.UplinkDTO;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.description.type.TypeList;
import org.testng.Assert;

import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.COMPOSITE_PARTY_ID_DTAG;
import static com.tsystems.tm.acc.tests.osr.a10nsp.inventory.internal.client.invoker.ResponseSpecBuilders.shouldBeCode;

@Slf4j

public class FTTHMigrationRobot {


    private OltResourceInventoryClient oltResourceInventoryClient = new OltResourceInventoryClient();
    private AncpConfigurationClient ancpConfigurationClient = new AncpConfigurationClient();
    private OltDiscoveryClient oltDiscoveryClient = new OltDiscoveryClient();

    @Step("Create an Ethernet link entity")
    public void createEthernetLink(OltDevice oltDevice) {
        oltResourceInventoryClient.getClient().ethernetLinkInternalController().updateUplink()
                .body(new UplinkDTO()
                        .oltEndSz(oltDevice.getEndsz())
                        .orderNumber(Integer.valueOf(oltDevice.getOrderNumber()))
                        .oltSlot(oltDevice.getOltSlot())
                        .oltPortNumber(oltDevice.getOltPort())
                        .bngEndSz(oltDevice.getBngEndsz())
                        .bngSlot(oltDevice.getBngDownlinkSlot())
                        .bngPortNumber(oltDevice.getBngDownlinkPort())
                        //.lsz(oltDevice.getLsz()))
                        .lsz(UplinkDTO.LszEnum._4C1));

    }

    @Step("Create an AncpIpSubnetData entity")
    public Long createAncpIpSubnet(AncpIpSubnetData ancpIpSubnetData) {

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
        return Long.valueOf(ancpIpSubnet.getId());
    }


    @Step("Create an AncpSession entity")
    public void createAncpSession(long ancpIpSubnetId, OltDevice oltDevice) {

//        AncpSession ancpSession = ancpConfigurationClient.getClient().ancpSessionV3().createAncpSessionV3()
//                .body(new AncpSessionCreate()
//                .partitionId()
//                .rmkEndpointId()
//                )


    }
//    @Step("Start Discovery Process")
//    public void deviceDiscoveryStartDiscoveryTask(StartDiscovery startDiscovery) {

//        StartDiscovery startDiscovery = oltDiscoveryClient.getClient().discoveryController().start()
//                .endszQuery("")
//                .mode()
//                .type()
//                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));

//    }

//    @Step("Delivers status of existing discovery processes")
//    public void deviceDiscoveryDeliveryDiscoveryState(DeliveryDiscoveryState deliveryDiscoveryState) {
//
//        DeliveryDiscoveryState deliveryDiscoveryState = oltDiscoveryClient.getClient().discoveryController().getStatus()
//                .discoveryIdQuery()
//                .endszQuery()
//                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
//
//    }
//
//    @Step("Delivers discrepancy data")
//    public void deviceDiscoveryDeliveryDiscrepancyData(DeliveryDiscrepancyData deliveryDiscrepancyData) {
//
//        DeliveryDiscrepancyData deliveryDiscrepancyData = oltDiscoveryClient.getClient().discrepancyController().
//                .discoveryIdQuery()
//                .endszQuery()
//                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
//
//    }

    @Step("Start device {oltDevice} discovery ")
    public void deviceDiscoveryStartDiscoveryTask(OltDevice oltDevice, String uuid) {

        log.info("deviceDiscoveryStartDiscoveryTask for endSz = {} uuid = {}", oltDevice.getEndsz(), uuid);

        String xCallbackUrl = new OCUrlBuilder(MercuryConstants.WIREMOCK_MS_NAME)
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
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HttpConstants.HTTP_CODE_ACCEPTED_202)));
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


    @Step("Clear {oltDevice} device in olt-resource-inventory database")
    public void clearResourceInventoryDataBase(OltDevice oltDevice) {
        String endSz = oltDevice.getEndsz();
        oltResourceInventoryClient.getClient().testDataManagementController().deleteDevice().endszQuery(endSz)
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
    }

}
