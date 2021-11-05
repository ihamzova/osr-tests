package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
//import com.tsystems.tm.acc.ta.api.osr.A4I
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.api.osr.A4InventoryImporterClient;
import com.tsystems.tm.acc.tests.osr.a4.inventory.importer.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.inventory.importer.client.model.CommissioningDpuA4Task;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.data.HttpConstants.*;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;

@Slf4j
public class A4DpuCommissioningRobot {

    private static final AuthTokenProvider authTokenProviderA4DpuCommissioning =
            new RhssoClientFlowAuthTokenProvider(A4_RESOURCE_INVENTORY_SERVICE_MS,
                    RhssoHelper.getSecretOfGigabitHub(A4_RESOURCE_INVENTORY_SERVICE_MS));

    private final ApiClient a4DpuCommissioning = new A4InventoryImporterClient(authTokenProviderA4DpuCommissioning).getClient();

    @Step("send POST for commissioningDpuA4Tasks")
    public void sendPostForCommissioningDpuA4Tasks
            (String dpuEndSz,
             String dpuSerialNumber,
             String dpuMaterialNumber,
             String dpuKlsId,
             String dpuFiberOnLocationId,
             String oltEndSz,
             String oltPonPort)
    {
    CommissioningDpuA4Task commissioningDpuA4Task = new CommissioningDpuA4Task();
    commissioningDpuA4Task.setDpuEndSz(dpuEndSz);
    commissioningDpuA4Task.setDpuSerialNumber(dpuSerialNumber);
    commissioningDpuA4Task.setDpuMaterialNumber(dpuMaterialNumber);
    commissioningDpuA4Task.setDpuKlsId(dpuKlsId);
    commissioningDpuA4Task.setDpuFiberOnLocationId(dpuFiberOnLocationId);
    commissioningDpuA4Task.setOltEndSz(oltEndSz);
    commissioningDpuA4Task.setOltPonPort(oltPonPort);
    a4DpuCommissioning.commissioningDpuA4Tasks()
                        .commissioningDpuA4Tasks()
                        .body(commissioningDpuA4Task)
                    .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

    }

    @Step("send POST for commissioningDpuA4Tasks with Validation Error")
    public void sendPostForCommissioningDpuA4TasksBadRequest
            (String dpuEndSz,
             String dpuSerialNumber,
             String dpuMaterialNumber,
             String dpuKlsId,
             String dpuFiberOnLocationId,
             String oltEndSz,
             String oltPonPort)
    {
        CommissioningDpuA4Task commissioningDpuA4Task = new CommissioningDpuA4Task();
        commissioningDpuA4Task.setDpuEndSz(dpuEndSz);
        commissioningDpuA4Task.setDpuSerialNumber(dpuSerialNumber);
        commissioningDpuA4Task.setDpuMaterialNumber(dpuMaterialNumber);
        commissioningDpuA4Task.setDpuKlsId(dpuKlsId);
        commissioningDpuA4Task.setDpuFiberOnLocationId(dpuFiberOnLocationId);
        commissioningDpuA4Task.setOltEndSz(oltEndSz);
        commissioningDpuA4Task.setOltPonPort(oltPonPort);
        a4DpuCommissioning.commissioningDpuA4Tasks()
                .commissioningDpuA4Tasks()
                .body(commissioningDpuA4Task)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_BAD_REQUEST_400)));
    }

    @Step("send POST for commissioningDpuA4Tasks with Server Error")
    public void sendPostForCommissioningDpuA4TasksServerError
            (String dpuEndSz,
             String dpuSerialNumber,
             String dpuMaterialNumber,
             String dpuKlsId,
             String dpuFiberOnLocationId,
             String oltEndSz,
             String oltPonPort)
    {
        CommissioningDpuA4Task commissioningDpuA4Task = new CommissioningDpuA4Task();
        commissioningDpuA4Task.setDpuEndSz(dpuEndSz);
        commissioningDpuA4Task.setDpuSerialNumber(dpuSerialNumber);
        commissioningDpuA4Task.setDpuMaterialNumber(dpuMaterialNumber);
        commissioningDpuA4Task.setDpuKlsId(dpuKlsId);
        commissioningDpuA4Task.setDpuFiberOnLocationId(dpuFiberOnLocationId);
        commissioningDpuA4Task.setOltEndSz(oltEndSz);
        commissioningDpuA4Task.setOltPonPort(oltPonPort);
        a4DpuCommissioning.commissioningDpuA4Tasks()
                .commissioningDpuA4Tasks()
                .body(commissioningDpuA4Task)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_INTERNAL_SERVER_ERROR_500)));
    }
}
