package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.MobileDpuBffClient;
import com.tsystems.tm.acc.tests.osr.mobile.dpu.bff.model.WorkorderResponse;
import io.qameta.allure.Step;
import org.testng.Assert;


import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

public class MobileDpuBffRobot {

    private MobileDpuBffClient mobileDpuBffClient;

    @Step("Returns a WorkorderResponse determined by given Workorder-Id.")

public void getWorkorder (long woid){
        mobileDpuBffClient = new MobileDpuBffClient();
        WorkorderResponse workorderResponse = mobileDpuBffClient.getClient().mobileDpuBffInternal().getWorkorder()
                .woIdPath(woid)
                .executeAs(validatedWith(shouldBeCode(200)));
        Assert.assertEquals(workorderResponse.getId().longValue(), 2L);
        Assert.assertEquals(workorderResponse.getStatus(), "CREATED");
        Assert.assertEquals(workorderResponse.getFolId().longValue(), 2L);
        Assert.assertEquals(workorderResponse.getType(), "DPU_INSTALLATION");

    }

    @Step("Starts a workorder for given Workorder-Id and returns WorkorderResponse.")
    public void startWorkorder(){

    }

    @Step("Completes a workorder for given Workorder-Id and returns WorkorderResponse.")
    public void completeWorkorder(){

    }

}
