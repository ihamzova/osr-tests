package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.pages.osr.dpucommissioning.DpuCreatePage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import io.qameta.allure.Step;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

public class DpuCommissioningUiRobot {

    private static final Integer HTTP_CODE_OK_200 = 200;

    private OltResourceInventoryClient oltResourceInventoryClient = new OltResourceInventoryClient();
    private AccessLineResourceInventoryClient accessLineResourceInventoryClient = new AccessLineResourceInventoryClient();

    @Step("Start automatic dpu creation and commissioning process")
    public void startDpuCommissioning(DpuDevice dpuDevice) {
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        oltSearchPage = oltSearchPage.searchNotDiscoveredByEndSz(dpuDevice.getEndsz());

        oltSearchPage.pressCreateDpuButton();

        DpuCreatePage dpuCreatePage = new DpuCreatePage();
        dpuCreatePage.validateUrl();
        dpuCreatePage.startDpuCreation(dpuDevice);

    }

    @Step("Checks data in ri after commissioning process")
    public void checkDpuCommissioningResult(DpuDevice dpuDevice) {

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

       // accessLineResourceInventoryClient.getClient().fillDatabase().fillDatabaseForOltCommissioning()
       //         .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }
}
