package com.tsystems.tm.acc.ta.data.morpheus.mappers;


import com.tsystems.tm.acc.tests.osr.wg.fttb.access.provisioning.external.v1.client.model.AsyncResponseNotification;
import com.tsystems.tm.acc.tests.osr.wg.fttb.access.provisioning.external.v1.client.model.Notification;

public class WgFttbAccessProvisioningMapper {
    public AsyncResponseNotification getAsyncResponseNotification(String endsz, boolean success) {
        return new AsyncResponseNotification()
                .response(new Notification()
                        .eventId("123")
                        .endSz(endsz)
                        .port("1")
                        .operation("string")
                        .operationState(Notification.OperationStateEnum.COMPLETED)
                )
                .success(success);
    }
}
