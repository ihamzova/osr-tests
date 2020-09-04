package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.enums.DevicePortLifeCycleStateUI;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.pages.osr.dpucommissioning.DpuCreatePage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltCommissioningPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDetailsPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import io.qameta.allure.Step;
import org.testng.Assert;

public class DpuCommissioningUiRobot {

    private static final Integer HTTP_CODE_OK_200 = 200;

    private OltResourceInventoryClient oltResourceInventoryClient = new OltResourceInventoryClient();
    private AccessLineResourceInventoryClient accessLineResourceInventoryClient = new AccessLineResourceInventoryClient();

    @Step("Start automatic dpu creation and commissioning process")
    public void startDpuCommissioning(DpuDevice dpu) {
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        oltSearchPage = oltSearchPage.searchNotDiscoveredByEndSz(dpu.getEndsz());

        oltSearchPage.pressCreateDpuButton();

        DpuCreatePage dpuCreatePage = new DpuCreatePage();
        dpuCreatePage.validateUrl();
        dpuCreatePage.startDpuCreation(dpu);

    }

    @Step("Checks data in ri after commissioning process")
    public void checkDpuCommissioningResult(DpuDevice olt) {

    }
}
