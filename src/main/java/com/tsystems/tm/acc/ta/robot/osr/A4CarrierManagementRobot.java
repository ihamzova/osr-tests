package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4CarrierManagementClient;
import com.tsystems.tm.acc.tests.osr.a4.carrier.management.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.carrier.management.client.model.AllocateL2BsaNspTask;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.*;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;

@Slf4j
public class A4CarrierManagementRobot {

    private final ApiClient a4CarrierManagement = new A4CarrierManagementClient().getClient();

    @Step("send POST for allocateL2BsaNspTask")
    public void sendPostForAllocateL2BsaNsp
            (String lineId, String carrierBsaReference, int dataRateUp, int dataRateDown) {
        AllocateL2BsaNspTask allocateL2BsaNspTask = new AllocateL2BsaNspTask();
        allocateL2BsaNspTask.setLineId(lineId);
        allocateL2BsaNspTask.setCarrierBsaReference(carrierBsaReference);
        allocateL2BsaNspTask.setDataRateDown(dataRateDown);
        allocateL2BsaNspTask.setDataRateUp(dataRateUp);

        a4CarrierManagement
                .allocateL2BsaNspTask()
                .allocateL2BsaNspTask()
                .body(allocateL2BsaNspTask)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

    }
    @Step("send GET for determination of free L2BSA TP on NEG")
    public void sendGetNegCarrierConnection (String uuid) {

        // unbekannte uuid
        a4CarrierManagement.negCarrierConnections().getNegCarrierConnections()
                .negUuidQuery("711d393e-a007-49f2-a0cd-0d80195763b1")
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NOT_FOUND_404)));

        // 711d393e-a007-49f2-a0cd-0d80195763b0 wird übergeben, oder default-neg
        a4CarrierManagement.negCarrierConnections().getNegCarrierConnections()
                .negUuidQuery(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }
    @Step("send GET for determination of free L2BSA TP on unknown NEG")
    public void sendGetNoNegCarrierConnection (String uuid) {

        a4CarrierManagement.negCarrierConnections().getNegCarrierConnections()
                .negUuidQuery(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NOT_FOUND_404)));

    }
}
