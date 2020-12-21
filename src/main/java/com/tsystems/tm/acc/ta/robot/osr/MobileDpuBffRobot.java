package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.ResponseSpecBuilders;
import com.tsystems.tm.acc.ta.api.osr.MobileDpuBffClient;
import com.tsystems.tm.acc.tests.osr.mobile.dpu.bff.model.DpuResponse;
import com.tsystems.tm.acc.tests.osr.mobile.dpu.bff.model.MarkDpuAsOperatingRequest;
import com.tsystems.tm.acc.tests.osr.mobile.dpu.bff.model.UpdateDpuSerialNumberRequest;
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
        Assert.assertEquals(workorderResponse.getStatus(), WorkorderResponse.StatusEnum.CREATED);
        Assert.assertEquals(workorderResponse.getType(), "DPU_INSTALLATION");

    }

    @Step("Returns a WorkorderResponse determined by given Workorder-Id. Negative case, error code 404, DPU not found")

    public void getWorkorderNegative (long woid){
        mobileDpuBffClient = new MobileDpuBffClient();
        mobileDpuBffClient.getClient().mobileDpuBffInternal().getWorkorder()
                .woIdPath(woid)
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(404)));

    }

    @Step("Starts a workorder for given Workorder-Id and returns WorkorderResponse.")
    public void startWorkorder(long woid){
        mobileDpuBffClient = new MobileDpuBffClient();
        WorkorderResponse workorderResponse = mobileDpuBffClient.getClient().mobileDpuBffInternal().startWorkorder()
                .woIdPath(woid)
                .executeAs(validatedWith(shouldBeCode(200)));
        Assert.assertEquals(workorderResponse.getId().longValue(), 2L);
        Assert.assertEquals(workorderResponse.getStatus(), WorkorderResponse.StatusEnum.IN_PROGRESS);
        Assert.assertEquals(workorderResponse.getType(), "DPU_INSTALLATION");
    }

    @Step("Starts a workorder for given Workorder-Id and returns WorkorderResponse. Negative case, error code 404, DPU not found")
    public void startWorkorderNegative (long woid){
        mobileDpuBffClient = new MobileDpuBffClient();
        mobileDpuBffClient.getClient().mobileDpuBffInternal().startWorkorder()
                .woIdPath(woid)
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(404)));
    }

    @Step("Completes a workorder for given Workorder-Id and returns WorkorderResponse.")
    public void completeWorkorder(long woid){
        mobileDpuBffClient = new MobileDpuBffClient();
        WorkorderResponse workorderResponse = mobileDpuBffClient.getClient().mobileDpuBffInternal().completeWorkorder()
                .woIdPath(woid)
                .executeAs(validatedWith(shouldBeCode(200)));
        Assert.assertEquals(workorderResponse.getId().longValue(), woid);
        Assert.assertEquals(workorderResponse.getStatus(),WorkorderResponse.StatusEnum.COMPLETED);
        Assert.assertEquals(workorderResponse.getType(), "DPU_INSTALLATION");
    }

    @Step("Completes a workorder for given Workorder-Id and returns WorkorderResponse. Negative case, error code 404, DPU not found")
    public void completeWorkorderNegative (long woid){
        mobileDpuBffClient = new MobileDpuBffClient();
        mobileDpuBffClient.getClient().mobileDpuBffInternal().completeWorkorder()
                .woIdPath(woid)
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(404)));
    }

    @Step("Returns a dpu response determined by given fiberOnLocationId.")
    public void getDpuByFolId(String folId, String dpuEndsz, String serialNumber){
        mobileDpuBffClient = new MobileDpuBffClient();
        DpuResponse dpuResponse = mobileDpuBffClient.getClient().mobileDpuBffDpuInternal().getDpuByFiberOnLocationId()
                .fiberOnLocationIdPath(folId)
                .executeAs(validatedWith(shouldBeCode(200)));
        Assert.assertEquals(dpuResponse.getFiberOnLocationId(), folId);
        Assert.assertEquals(dpuResponse.getEndSZ(), dpuEndsz);
        Assert.assertEquals(dpuResponse.getSerialNumber(), serialNumber);
        Assert.assertEquals(dpuResponse.getLifeCycleState(), DpuResponse.LifeCycleStateEnum.OPERATING);
    }

    @Step("Returns a dpu response determined by given fiberOnLocationId. Negative case, error code 404")
    public void getDpuByFolIdNegative(String folId){
        mobileDpuBffClient = new MobileDpuBffClient();
        mobileDpuBffClient.getClient().mobileDpuBffDpuInternal().getDpuByFiberOnLocationId()
                .fiberOnLocationIdPath(folId)
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(404)));
    }

    @Step("Update SerialNumber of DPU.")
    public void updateDpuSerialNumber(String folId, String dpuEndsz, String serialNumber){
        UpdateDpuSerialNumberRequest updateDpuSerialNumberRequest = new UpdateDpuSerialNumberRequest();
        updateDpuSerialNumberRequest.setEndSZ(dpuEndsz);
        updateDpuSerialNumberRequest.setSerialNumber(serialNumber);
        mobileDpuBffClient = new MobileDpuBffClient();
        DpuResponse dpuResponse = mobileDpuBffClient.getClient().mobileDpuBffDpuInternal().updateDpuSerialNumber()
                .body(updateDpuSerialNumberRequest)
                .executeAs(validatedWith(shouldBeCode(200)));
        Assert.assertEquals(dpuResponse.getFiberOnLocationId(), folId);
        Assert.assertEquals(dpuResponse.getEndSZ(), dpuEndsz);
        Assert.assertEquals(dpuResponse.getSerialNumber(), serialNumber);
        Assert.assertEquals(dpuResponse.getLifeCycleState(), DpuResponse.LifeCycleStateEnum.OPERATING);
    }

    @Step("Update SerialNumber of DPU. Negative case, error code 404")
    public void updateDpuSerialNumberNegative(String dpuEndsz, String serialNumber) {
        UpdateDpuSerialNumberRequest updateDpuSerialNumberRequest = new UpdateDpuSerialNumberRequest();
        updateDpuSerialNumberRequest.setEndSZ(dpuEndsz);
        updateDpuSerialNumberRequest.setSerialNumber(serialNumber);
        mobileDpuBffClient = new MobileDpuBffClient();
        mobileDpuBffClient.getClient().mobileDpuBffDpuInternal().updateDpuSerialNumber()
                .body(updateDpuSerialNumberRequest)
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(404)));
    }

    @Step("Mark the DPU functional as OPERATING.")
    public void setDpuAsOperating(String folId, String dpuEndsz, String serialNumber){
        MarkDpuAsOperatingRequest markDpuAsOperatingRequest = new MarkDpuAsOperatingRequest();
        markDpuAsOperatingRequest.setEndSZ(dpuEndsz);
        markDpuAsOperatingRequest.setUplinkPortOperating(true);
        mobileDpuBffClient = new MobileDpuBffClient();
        DpuResponse dpuResponse = mobileDpuBffClient.getClient().mobileDpuBffDpuInternal().markDpuAsOperating()
                .body(markDpuAsOperatingRequest)
                .executeAs(validatedWith(shouldBeCode(200)));
        Assert.assertEquals(dpuResponse.getFiberOnLocationId(), folId);
        Assert.assertEquals(dpuResponse.getEndSZ(), dpuEndsz);
        Assert.assertEquals(dpuResponse.getSerialNumber(), serialNumber);
        Assert.assertEquals(dpuResponse.getLifeCycleState(), DpuResponse.LifeCycleStateEnum.OPERATING);
    }

    @Step("Mark the DPU functional as OPERATING. Negative case, error code 404, DPU not found")
    public void setDpuAsOperatingNegative(String dpuEndsz, String serialNumber){
        MarkDpuAsOperatingRequest markDpuAsOperatingRequest = new MarkDpuAsOperatingRequest();
        markDpuAsOperatingRequest.setEndSZ(dpuEndsz);
        markDpuAsOperatingRequest.setUplinkPortOperating(true);
        mobileDpuBffClient = new MobileDpuBffClient();
        mobileDpuBffClient.getClient().mobileDpuBffDpuInternal().markDpuAsOperating()
                .body(markDpuAsOperatingRequest)
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(404)));
    }

}