package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.OltDiscoveryClient;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import io.qameta.allure.Step;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.tests.osr.a10nsp.inventory.internal.client.invoker.ResponseSpecBuilders.shouldBeCode;
import static org.testng.Assert.assertTrue;

public class FTTHMigrationRobot {
    private static final Integer HTTP_CODE_ACCEPTED_202 = 202;
    private static final Integer HTTP_CODE_OK_200 = 200;

    private OltResourceInventoryClient oltResourceInventoryClient = new OltResourceInventoryClient();
    //private AncpConfigurationClient ancpConfigurationClient = new AncpConfigurationClient();
    private OltDiscoveryClient oltDiscoveryClient = new OltDiscoveryClient();

//    @Step("Start Discovery Process")
//    public void deviceDiscoveryStartDiscoveryTask(StartDiscovery startDiscovery) {

//        StartDiscovery startDiscovery = oltDiscoveryClient.getClient().discoveryController().start()
//                .endszQuery("")
//                .mode()
//                .type()
//                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));

    }

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


//}
