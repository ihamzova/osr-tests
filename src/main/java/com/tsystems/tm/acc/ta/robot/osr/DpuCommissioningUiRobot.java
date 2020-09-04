package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import io.qameta.allure.Step;

public class DpuCommissioningUiRobot {

    private static final Integer HTTP_CODE_OK_200 = 200;

    private OltResourceInventoryClient oltResourceInventoryClient = new OltResourceInventoryClient();
    private AccessLineResourceInventoryClient accessLineResourceInventoryClient = new AccessLineResourceInventoryClient();

    @Step("Starts automatic dpu creation and commissioning process")
    public void startDpuCommissioning(DpuDevice dpu) {

    }

    @Step("Checks data in ri after commissioning process")
    public void checkDpuCommissioningResult(DpuDevice olt) {

    }
}
