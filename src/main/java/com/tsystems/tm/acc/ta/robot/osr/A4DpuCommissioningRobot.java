package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.A4DpuCommissioningClient;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.tests.osr.a4.dpu.commissioning.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.dpu.commissioning.client.model.CommissioningDpuA4Task;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.*;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_SERVICE_MS;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.WIREMOCK_MS_NAME;

@Slf4j
public class A4DpuCommissioningRobot {

    private static final AuthTokenProvider authTokenProviderA4DpuCommissioning =
            new RhssoClientFlowAuthTokenProvider(WIREMOCK_MS_NAME,
                    RhssoHelper.getSecretOfGigabitHub(WIREMOCK_MS_NAME));

    private final ApiClient a4DpuCommissioning = new A4DpuCommissioningClient(authTokenProviderA4DpuCommissioning).getClient();

    @Step("send POST for commissioningDpuA4Tasks")
    public void sendPostForCommissioningDpuA4Tasks
            (String dpuEndSz,
             String dpuSerialNumber,
             String dpuMaterialNumber,
             String dpuKlsId,
             String dpuFiberOnLocationId,
             String oltEndSz,
             String oltPonPort) {
        CommissioningDpuA4Task commissioningDpuA4Task = createCommissioningDpuA4Task(dpuEndSz, dpuSerialNumber, dpuMaterialNumber, dpuKlsId, dpuFiberOnLocationId, oltEndSz, oltPonPort);
        a4DpuCommissioning.commissioningDpuA4Tasks()
                .commissioningDpuA4Tasks()
                .body(commissioningDpuA4Task)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

    }

    @NotNull
    private CommissioningDpuA4Task createCommissioningDpuA4Task(String dpuEndSz, String dpuSerialNumber, String dpuMaterialNumber, String dpuKlsId, String dpuFiberOnLocationId, String oltEndSz, String oltPonPort) {
        CommissioningDpuA4Task commissioningDpuA4Task = new CommissioningDpuA4Task();
        commissioningDpuA4Task.setDpuEndSz(dpuEndSz);
        commissioningDpuA4Task.setDpuSerialNumber(dpuSerialNumber);
        commissioningDpuA4Task.setDpuMaterialNumber(dpuMaterialNumber);
        commissioningDpuA4Task.setDpuKlsId(dpuKlsId);
        commissioningDpuA4Task.setDpuFiberOnLocationId(dpuFiberOnLocationId);
        commissioningDpuA4Task.setOltEndSz(oltEndSz);
        commissioningDpuA4Task.setOltPonPort(oltPonPort);
        return commissioningDpuA4Task;
    }

    @Step("send POST for commissioningDpuA4Tasks")
    public void sendPostForCommissioningDpuA4Tasks(CommissioningDpuA4Task comDpuTask) {
        a4DpuCommissioning.commissioningDpuA4Tasks()
                .commissioningDpuA4Tasks()
                .body(comDpuTask)
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
             String oltPonPort) {
        CommissioningDpuA4Task commissioningDpuA4Task = createCommissioningDpuA4Task(dpuEndSz, dpuSerialNumber, dpuMaterialNumber, dpuKlsId, dpuFiberOnLocationId, oltEndSz, oltPonPort);
        a4DpuCommissioning.commissioningDpuA4Tasks()
                .commissioningDpuA4Tasks()
                .body(commissioningDpuA4Task)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_BAD_REQUEST_400)));
    }

    @Step("send POST for commissioningDpuA4Tasks with Validation Error")
    public void sendPostForCommissioningDpuA4TasksBadRequest(CommissioningDpuA4Task comDpuTask) {
        a4DpuCommissioning.commissioningDpuA4Tasks()
                .commissioningDpuA4Tasks()
                .body(comDpuTask)
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
             String oltPonPort) {
        CommissioningDpuA4Task commissioningDpuA4Task = createCommissioningDpuA4Task(dpuEndSz, dpuSerialNumber, dpuMaterialNumber, dpuKlsId, dpuFiberOnLocationId, oltEndSz, oltPonPort);
        a4DpuCommissioning.commissioningDpuA4Tasks()
                .commissioningDpuA4Tasks()
                .body(commissioningDpuA4Task)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_INTERNAL_SERVER_ERROR_500)));
    }
}
