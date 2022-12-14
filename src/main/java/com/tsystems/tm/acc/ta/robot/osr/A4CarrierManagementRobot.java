package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4CarrierManagementClient;
import com.tsystems.tm.acc.tests.osr.a4.carrier.management.client.model.AllocateL2BsaNspTask;
import com.tsystems.tm.acc.tests.osr.a4.carrier.management.client.model.ReleaseA10NspNspTask;
import com.tsystems.tm.acc.tests.osr.a4.carrier.management.client.model.ReleaseL2BsaNspTask;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import static com.tsystems.tm.acc.ta.data.HttpConstants.*;
import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;

@Slf4j
public class A4CarrierManagementRobot {
    private final A4CarrierManagementClient a4CarrierManagement = new A4CarrierManagementClient();

    @Step("send POST for allocateL2BsaNspTask")
    public void sendPostForAllocateL2BsaNsp
            (String lineId, String carrierBsaReference, int dataRateUp, int dataRateDown, String l2CcId) {
        AllocateL2BsaNspTask allocateL2BsaNspTask = new AllocateL2BsaNspTask();
        allocateL2BsaNspTask.setLineId(lineId);
        allocateL2BsaNspTask.setCarrierBsaReference(carrierBsaReference);
        allocateL2BsaNspTask.setDataRateDown(dataRateDown);
        allocateL2BsaNspTask.setDataRateUp(dataRateUp);
        allocateL2BsaNspTask.setL2CcId(l2CcId);
        a4CarrierManagement.getClient()
                .allocateL2BsaNspTask()
                .allocateL2BsaNspTask()
                .body(allocateL2BsaNspTask)
                .execute(checkStatus(HTTP_CODE_OK_200));

    }

    @Step("send POST for allocateL2BsaNspTask and not found free L2Bsa-NSP")
    public void sendPostForAllocateL2BsaNspNotFound
            (String lineId, String carrierBsaReference, int dataRateUp, int dataRateDown, String l2CcId) {
        AllocateL2BsaNspTask allocateL2BsaNspTask = new AllocateL2BsaNspTask();
        allocateL2BsaNspTask.setLineId(lineId);
        allocateL2BsaNspTask.setCarrierBsaReference(carrierBsaReference);
        allocateL2BsaNspTask.setDataRateDown(dataRateDown);
        allocateL2BsaNspTask.setDataRateUp(dataRateUp);
        allocateL2BsaNspTask.setL2CcId(l2CcId);
        a4CarrierManagement.getClient()
                .allocateL2BsaNspTask()
                .allocateL2BsaNspTask()
                .body(allocateL2BsaNspTask)
                .execute(checkStatus(HTTP_CODE_NOT_FOUND_404));

    }

    @Step("send POST for allocateL2BsaNspTask and not found free L2Bsa-NSP")
    public void sendPostForAllocateL2BsaNspBadRequest
            (String lineId, String carrierBsaReference, int dataRateUp, int dataRateDown, String l2CcId) {
        AllocateL2BsaNspTask allocateL2BsaNspTask = new AllocateL2BsaNspTask();
        allocateL2BsaNspTask.setLineId(lineId);
        allocateL2BsaNspTask.setCarrierBsaReference(carrierBsaReference);
        allocateL2BsaNspTask.setDataRateDown(dataRateDown);
        allocateL2BsaNspTask.setDataRateUp(dataRateUp);
        allocateL2BsaNspTask.setL2CcId(l2CcId);

        a4CarrierManagement.getClient()
                .allocateL2BsaNspTask()
                .allocateL2BsaNspTask()
                .body(allocateL2BsaNspTask)
                .execute(checkStatus(HTTP_CODE_BAD_REQUEST_400));

    }

    @Step("send POST for ReleaseL2BsaNspTask")
    public void sendPostForReleaseL2BsaNsp(String uuid) {
        ReleaseL2BsaNspTask releaseL2BsaNspTask = new ReleaseL2BsaNspTask();
        releaseL2BsaNspTask.setUuid(uuid);

        a4CarrierManagement.getClient()
                .releaseL2BsaNspTask()
                .releaseL2BsaNspTask()
                .body(releaseL2BsaNspTask)
                .execute(checkStatus(HTTP_CODE_OK_200));

    }

    @Step("send GET for determination of free L2BSA TP on NEG")
    public void sendGetNegCarrierConnection(String uuid) {

        // 711d393e-a007-49f2-a0cd-0d80195763b0 wird ??bergeben, oder default-neg
        a4CarrierManagement.getClient().negCarrierConnections().getNegCarrierConnections()
                .negUuidQuery(uuid)
                .execute(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("send GET for determination of free L2BSA TP on unknown NEG")
    public void sendGetNoNegCarrierConnection(String uuid) {

        a4CarrierManagement.getClient().negCarrierConnections().getNegCarrierConnections()
                .negUuidQuery(uuid)
                .execute(checkStatus(HTTP_CODE_INTERNAL_SERVER_ERROR_500));

    }

    @Step("send POST for ReleaseA10NspNspTask")
    public void sendPostForReleaseA10NspNsp(String uuid) {
        ReleaseA10NspNspTask releaseA10NspNspTask = new ReleaseA10NspNspTask();
        releaseA10NspNspTask.setUuid(uuid);

        a4CarrierManagement.getClient()
                .releaseA10NspNspTask()
                .releaseA10NspNspTask()
                .body(releaseA10NspNspTask)
                .execute(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("send POST for ReleaseA10NspNspTask")
    public void sendPostForReleaseA10NspNspBadRequest(String uuid) {
        ReleaseA10NspNspTask releaseA10NspNspTask = new ReleaseA10NspNspTask();
        releaseA10NspNspTask.setUuid(uuid);
        a4CarrierManagement.getClient()
                .releaseA10NspNspTask()
                .releaseA10NspNspTask()
                .body(releaseA10NspNspTask)
                .execute(checkStatus(HTTP_CODE_BAD_REQUEST_400));
    }

    @Step("send POST for ReleaseA10NspNspTask")
    public void sendPostForReleaseA10NspNspA10NspNotFound(String uuid) {
        ReleaseA10NspNspTask releaseA10NspNspTask = new ReleaseA10NspNspTask();
        releaseA10NspNspTask.setUuid(uuid);
        a4CarrierManagement.getClient()
                .releaseA10NspNspTask()
                .releaseA10NspNspTask()
                .body(releaseA10NspNspTask)
                .execute(checkStatus(HTTP_CODE_INTERNAL_SERVER_ERROR_500));
    }
}
