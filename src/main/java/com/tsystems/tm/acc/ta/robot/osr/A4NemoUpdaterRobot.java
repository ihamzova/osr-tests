package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4NemoUpdaterClient;
import com.tsystems.tm.acc.tests.osr.a4.nemo.updater.internal.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.nemo.updater.internal.client.model.UpdateNemoTask;
import io.qameta.allure.Step;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

public class A4NemoUpdaterRobot {
    private static final Integer HTTP_CODE_CREATED_201 = 201;

    private ApiClient a4NemoUpdater = new A4NemoUpdaterClient().getClient();

    @Step("Trigger NEMO Update")
    public void triggerNemoUpdate(String uuid) {
        UpdateNemoTask updateNemoTask = new UpdateNemoTask();
        updateNemoTask.setEntityUuid(uuid);
        a4NemoUpdater
                .nemoUpdateService()
                .updateNemoTask()
                .body(updateNemoTask)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }
}
