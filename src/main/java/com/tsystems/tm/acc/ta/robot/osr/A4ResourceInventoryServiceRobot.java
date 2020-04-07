package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4ResourceInventoryServiceClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkElementGroupDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.LogicalResource;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.LogicalResourceUpdate;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

public class A4ResourceInventoryServiceRobot {
    private static final Integer HTTP_CODE_OK_200 = 200;
    private static final Integer HTTP_CODE_CREATED_201 = 201;

    private ApiClient a4ResourceInventoryService = new A4ResourceInventoryServiceClient().getClient();

    @Step("Create Termination Point represented as Logical Resource")
    public void createTerminationPoint(String uuid, LogicalResourceUpdate terminationPointLogicalResource) {
        a4ResourceInventoryService
                .logicalResource()
                .updateLogicalResourcePut()
                .idPath(uuid)
                .body(terminationPointLogicalResource)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    @Step("Check network element group as logical resource representation")
    public void checkLogicalResourceIsNetworkElementGroup(String uuid) {
        List<LogicalResource> logicalResourceList =
                a4ResourceInventoryService
                .logicalResource()
                .retrieveLogicalResource()
                .idPath(uuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertEquals(logicalResourceList.size(), 1, "Count of returned logicalResources");
        Assert.assertEquals(logicalResourceList.get(0).getId(), uuid, "UUID is the same");
        Assert.assertEquals(logicalResourceList.get(0).getType(), "NetworkElementGroup", "Entity type is the same");
    }
}