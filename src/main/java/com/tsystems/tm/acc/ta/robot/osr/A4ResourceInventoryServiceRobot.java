package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4ResourceInventoryServiceClient;
import com.tsystems.tm.acc.ta.data.osr.generators.A4TerminationPointGenerator;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.ta.data.osr.models.A4TerminationPoint;
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

    @Step("Create termination point represented as logical resource")
    public void createTerminationPoint(A4TerminationPoint tpData, A4NetworkElementPort nepData) {
        A4TerminationPointGenerator a4TerminationPointGenerator = new A4TerminationPointGenerator();
        LogicalResourceUpdate terminationPointLogicalResource = a4TerminationPointGenerator
                .generateAsLogicalResource(tpData, nepData);

        a4ResourceInventoryService
                .logicalResource()
                .updateLogicalResourcePut()
                .idPath(tpData.getUuid())
                .body(terminationPointLogicalResource)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    @Step("Check network element group as logical resource representation")
    public void checkLogicalResourceIsNetworkElementGroup(A4NetworkElementGroup negData) {
        String uuid = negData.getUuid();

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