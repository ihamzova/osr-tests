package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.ResponseSpecBuilders;
import com.tsystems.tm.acc.ta.api.osr.AncpConfigurationClient;
import com.tsystems.tm.acc.ta.api.osr.OltDiscoveryClient;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.HttpConstants;
import com.tsystems.tm.acc.ta.data.mercury.MercuryConstants;
import com.tsystems.tm.acc.ta.data.osr.models.AncpIpSubnet;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.tests.osr.ancp.configuration.v3_0_0.client.model.AncpIpSubnetCreate;
import com.tsystems.tm.acc.tests.osr.olt.discovery.v2_1_0.client.model.DiscoveryMode;
import com.tsystems.tm.acc.tests.osr.olt.discovery.v2_1_0.client.model.DiscoveryStateEnum;
import com.tsystems.tm.acc.tests.osr.olt.discovery.v2_1_0.client.model.DiscoveryStatus;
import com.tsystems.tm.acc.tests.osr.olt.discovery.v2_1_0.client.model.DiscoveryType;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
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

    @Step("create an AncpIpSubnet entity")
    void createAncpIpSubnet(AncpIpSubnet ancpIpSubnet) {

        ancpConfigurationClient.getClient().ancpIpSubnetV3().createAncpIpSubnetV3()
                .body(new AncpIpSubnetCreate()
                .ipAddressBng("10.150.240.102")
                .ipAddressBroadcast("10.150.240.103")
                .ipAddressLoopback("10.150.240.100")
                .subnetMask("/30")
                .rmkAccessId("1341434324")
                .atType("AncpIpSubnet")
        ).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
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
        Assert.assertEquals(discoveryStatus.getType(), DiscoveryType.SEAL_PSL,"GetDiscoveryStatus discovery type mismatch");
        Assert.assertEquals(discoveryStatus.getMode(), DiscoveryMode.MANUAL,"GetDiscoveryStatus discovery mode mismatch");
    }


    @Step("Clear {oltDevice} device in olt-resource-inventory database")
    public void clearResourceInventoryDataBase(OltDevice oltDevice) {
        String endSz = oltDevice.getEndsz();
        oltResourceInventoryClient.getClient().testDataManagementController().deleteDevice().endszQuery(endSz)
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
    }

}
