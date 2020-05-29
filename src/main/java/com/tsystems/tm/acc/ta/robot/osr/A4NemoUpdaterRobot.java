package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4NemoUpdaterClient;
import com.tsystems.tm.acc.ta.helpers.WiremockHelper;
import com.tsystems.tm.acc.ta.helpers.wiremock.WiremockRequestPatternBuilder;
import com.tsystems.tm.acc.tests.osr.a4.nemo.updater.internal.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.nemo.updater.internal.client.model.UpdateNemoTask;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkServiceProfileFtthAccessDto;
import com.tsystems.tm.acc.tests.wiremock.client.model.RequestFind;
import com.tsystems.tm.acc.tests.wiremock.client.model.RequestPattern;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

public class A4NemoUpdaterRobot {
    private static final Integer HTTP_CODE_CREATED_201 = 201;

    private ApiClient a4NemoUpdater = new A4NemoUpdaterClient().getClient();

    private A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();

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

    @Step("Check if PUT request to NEMO wiremock with logical resource has happened")
    public void checkLogicalResourcePutToNemoWiremock(String uuid) {
        RequestPattern requestPattern = new WiremockRequestPatternBuilder()
                .withMethod("PUT")
                .withUrlPathPattern(".*/logicalResource/" + uuid)
                .build();
        List<RequestFind> requests = WiremockHelper.requestsFindByCustomPatternAmount(requestPattern, 1).getRequests();
        Assert.assertEquals(requests.size(), 1);
    }

    @Step("Check if PUT request to NEMO wiremock with network service profile has happened")
    public void checkNetworkServiceProfilePutToNemoWiremock(String uuidTp) {
        List<NetworkServiceProfileFtthAccessDto> nspList = a4Inventory.getNetworkServiceProfilesViaTerminationPoint(uuidTp);
        Assert.assertEquals(nspList.size(), 1);

        checkLogicalResourcePutToNemoWiremock(nspList.get(0).getUuid());
    }

    @Step("Check if DELETE request to NEMO wiremock with logical resource has happened")
    public void checkLogicalResourceDeleteToNemoWiremock(String uuid) {
        RequestPattern requestPattern = new WiremockRequestPatternBuilder()
                .withMethod("DELETE")
                .withUrlPathPattern(".*/logicalResource/" + uuid)
                .build();
        List<RequestFind> requests = WiremockHelper.requestsFindByCustomPatternAmount(requestPattern, 1).getRequests();
        Assert.assertEquals(requests.size(), 1);
    }
}