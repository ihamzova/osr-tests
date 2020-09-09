package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.enums.DevicePortLifeCycleStateUI;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.pages.osr.dpucommissioning.DpuCreatePage;
import com.tsystems.tm.acc.ta.pages.osr.dpucommissioning.DpuInfoPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.Device;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.DpuPonConnectionDto;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

public class DpuCommissioningUiRobot {

    private static final Integer HTTP_CODE_OK_200 = 200;

    private OltResourceInventoryClient oltResourceInventoryClient = new OltResourceInventoryClient();
    private AccessLineResourceInventoryClient accessLineResourceInventoryClient = new AccessLineResourceInventoryClient();
    private String businessKey;

    @Step("Start automatic dpu creation and commissioning process")
    public void startDpuCommissioning(DpuDevice dpuDevice) throws InterruptedException {
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        oltSearchPage = oltSearchPage.searchNotDiscoveredByEndSz(dpuDevice.getEndsz());

        oltSearchPage.pressCreateDpuButton();

        DpuCreatePage dpuCreatePage = new DpuCreatePage();
        dpuCreatePage.validateUrl();
        dpuCreatePage.startDpuCreation(dpuDevice);
        dpuCreatePage.openDpuInfoPage();

        DpuInfoPage dpuInfoPage = new DpuInfoPage();
        dpuInfoPage.validateUrl();
        Assert.assertEquals(DpuInfoPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());
        Assert.assertEquals(DpuInfoPage.getPortLifeCycleState(dpuDevice.getOltGponPort()), DevicePortLifeCycleStateUI.NOTOPERATING.toString());
        dpuInfoPage.startDpuCommissioning();
        businessKey = dpuInfoPage.getBusinessKey();
        Assert.assertNotNull(businessKey);
        Assert.assertFalse(businessKey.isEmpty());

        Thread.sleep(1000);
        /**testable only on domain level
         * Assert.assertEquals(DpuInfoPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.OPERATING.toString());
         Assert.assertEquals(DpuInfoPage.getPortLifeCycleState(dpuDevice.getOltGponPort()), DevicePortLifeCycleStateUI.OPERATING.toString());*/
        dpuInfoPage.openDpuConfiguraionTab();
        dpuInfoPage.openDpuAccessLinesTab();
        dpuInfoPage.openDpuPortsTab();
    }

    @Step("Checks data in ri after commissioning process")
    public void checkDpuCommissioningResult(DpuDevice dpuDevice) {

        List<Device> deviceList = oltResourceInventoryClient.getClient().deviceInternalController().findDeviceByCriteria()
                .endszQuery(dpuDevice.getEndsz()).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertEquals(deviceList.size(), 1L);
        Assert.assertEquals(deviceList.get(0).getType(), Device.TypeEnum.DPU);
        Assert.assertEquals(deviceList.get(0).getEndSz(), dpuDevice.getEndsz());
        Device deviceAfterCommissioning = deviceList.get(0);

        // check device lifecycle state
        //Assert.assertEquals( deviceAfterCommissioning.getLifeCycleState(), Device.LifeCycleStateEnum.OPERATING);

        List<DpuPonConnectionDto> dpuPonConnectionDtos = oltResourceInventoryClient.getClient().dpuPonConnectionInternalController().findDpuPonConnectionByCriteria()
                .dpuPonPortEndszQuery(dpuDevice.getEndsz()).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertEquals(dpuPonConnectionDtos.size(), 1L);
        DpuPonConnectionDto dpuPonConnection = dpuPonConnectionDtos.get(0);
        Assert.assertEquals(dpuPonConnection.getOltPonPortEndsz(), dpuDevice.getOltEndsz());
        Assert.assertEquals(dpuPonConnection.getOltPonSlotNumber(), dpuDevice.getOltGponSlot());
        Assert.assertEquals(dpuPonConnection.getOltPonPortNumber(), dpuDevice.getOltGponPort());
        Assert.assertEquals(dpuPonConnection.getDpuPonPortEndsz(), dpuDevice.getEndsz());
        Assert.assertEquals(dpuPonConnection.getDpuPonPortNumber(), "1");
        Assert.assertEquals(dpuPonConnection.getDpuPonPortGe(), Integer.valueOf(dpuDevice.getPonConnectionGe()));
        Assert.assertEquals(dpuPonConnection.getDpuPonPortWe(), Integer.valueOf(dpuDevice.getPonConnectionWe()));



    }

    @Step("Restore accessline-resource-inventory Database state")
    public void restoreOsrDbState() {
        accessLineResourceInventoryClient.getClient().fillDatabase().deleteDatabase()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Clear devices (DPU and OLT) in olt-resource-inventory database")
    public void clearResourceInventoryDataBase(DpuDevice dpuDevice) {
        oltResourceInventoryClient.getClient().testDataManagementController().deleteDevice().endszQuery(dpuDevice.getEndsz())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        oltResourceInventoryClient.getClient().testDataManagementController().deleteDevice().endszQuery(dpuDevice.getOltEndsz())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Create the precondition olt-resource-inventory and access-line-resource-inventory database")
    public void prepareResourceInventoryDataBase(DpuDevice dpuDevice) {
        oltResourceInventoryClient.getClient().testDataManagementController().createDevice()
                ._01EmsNbiNameQuery("MA5600T")
                ._02EndszQuery(dpuDevice.getOltEndsz())
                ._03SlotNumbersQuery("3,4,5,19")
                ._06KLSIdQuery("12377812")
                ._07CompositePartyIDQuery("10001")
                ._08UplinkEndszQuery(dpuDevice.getBngEndsz())
                ._10ANCPConfQuery("1")
                ._11RunSQLQuery("1")
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        accessLineResourceInventoryClient.getClient().fillDatabase().fillDatabaseForOltCommissioning()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("get businessKey")
    public String getBusinessKey() {
        return businessKey;
    }
}
