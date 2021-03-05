package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryFillDbClient;
import com.tsystems.tm.acc.ta.api.osr.OltDiscoveryClient;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.enums.DevicePortLifeCycleStateUI;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.*;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_8_0.client.model.*;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.*;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

public class OltDeCommissioningRobot {
    private static final Integer HTTP_CODE_OK_200 = 200;
    private static final Integer HTTP_CODE_NOT_FOUND_404 = 404;
    private static final Integer TIMEOUT_FOR_CARD_DEPROVISIONING = 20 * 60_000;

    private static final Integer TIMEOUT_FOR_DEVICE_DELETION = 5_000;
    private static final Integer TIMEOUT_FOR_CARD_DELETION = 5_000;

    private OltResourceInventoryClient oltResourceInventoryClient = new OltResourceInventoryClient();


    @Step("Start olt decommissionung process after manual commissioning")
    public  void startOltDecommissioningAfterManualCommissioning(OltDevice olt) throws InterruptedException {
        //String oltEndSz = olt.getVpsz() + "/" + olt.getFsz();

        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        oltSearchPage.searchDiscoveredOltByParameters(olt);

        OltDetailsPage oltDetailsPage = oltSearchPage.searchDiscoveredOltByParameters(olt);
        oltDetailsPage.startAccessLinesDeProvisioningFromCard(TIMEOUT_FOR_CARD_DEPROVISIONING);
        oltDetailsPage.deconfigureAncpSession();
        oltDetailsPage.deleteUplinkConfiguration();
        Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());
        oltDetailsPage.deleteGponCard();
        Thread.sleep(TIMEOUT_FOR_CARD_DELETION);
        oltDetailsPage.deleteDevice();
        DeleteDevicePage deleteDevicePage = new DeleteDevicePage();
        deleteDevicePage.validateUrl();
        deleteDevicePage.DeleteOltDevice();
        Thread.sleep(TIMEOUT_FOR_DEVICE_DELETION);
    }

    @Step("Start olt decommissionung process after auto commissioning")
    public  void startOltDecommissioningAfterAutoCommissioning(OltDevice olt) throws InterruptedException {

        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        oltSearchPage.searchDiscoveredOltByParameters(olt);

        OltDetailsPage oltDetailsPage = oltSearchPage.searchDiscoveredOltByParameters(olt);
        oltDetailsPage.startAccessLinesDeProvisioningFromDevice(TIMEOUT_FOR_CARD_DEPROVISIONING);
        oltDetailsPage.deconfigureAncpSession();
        oltDetailsPage.deleteUplinkConfiguration();
        Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());
        oltDetailsPage.deleteGponCard();
        Thread.sleep(TIMEOUT_FOR_CARD_DELETION);
        oltDetailsPage.deleteDevice();
        DeleteDevicePage deleteDevicePage = new DeleteDevicePage();
        deleteDevicePage.validateUrl();
        deleteDevicePage.DeleteOltDevice();
        Thread.sleep(TIMEOUT_FOR_DEVICE_DELETION);
    }

    @Step("Checks olt data in olt-ri after decommissioning process")
    public void checkOltDeCommissioningResult(OltDevice olt, String slot) {
        String oltEndSz = olt.getVpsz() + "/" + olt.getFsz();

        List<Device> deviceList = oltResourceInventoryClient.getClient().deviceInternalController().findDeviceByCriteria()
                .endszQuery(oltEndSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertEquals(deviceList.size(), 0L, "Device is present");

        oltResourceInventoryClient.getClient().cardController().findCard()
                .endSzQuery(oltEndSz).slotNumberQuery(slot).executeAs(validatedWith(shouldBeCode(HTTP_CODE_NOT_FOUND_404)));

    }

}
