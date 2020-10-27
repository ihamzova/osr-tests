package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4ResourceInventoryServiceClient;
import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryServiceMapper;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
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
    private static final Integer HTTP_CODE_BAD_REQUEST_400 = 400;

    private static final String INVALID_OPERATIONAL_STATE = "grmblfx";

    private final ApiClient a4ResourceInventoryService = new A4ResourceInventoryServiceClient().getClient();

    @Step("Create Termination Point represented as Logical Resource")
    public void createTerminationPoint(A4TerminationPoint tpData, A4NetworkElementPort nepData) {
        LogicalResourceUpdate terminationPointLogicalResource = new A4ResourceInventoryServiceMapper()
                .getLogicalResourceUpdate(tpData, nepData);

        a4ResourceInventoryService
                .logicalResource()
                .updateLogicalResourcePut()
                .idPath(tpData.getUuid())
                .body(terminationPointLogicalResource)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    @Step("Send new operational state for Network Element Group")
    public void sendStatusUpdateForNetworkElementGroup(A4NetworkElementGroup negData, String newOperationalState) {
        LogicalResourceUpdate negLogicalResource = new A4ResourceInventoryServiceMapper()
                .getLogicalResourceUpdate(negData, newOperationalState);

        a4ResourceInventoryService
                .logicalResource()
                .updateLogicalResourcePatch()
                .idPath(negData.getUuid())
                .body(negLogicalResource)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    @Step("Send invalid operational state for Network Element Group")
    public void receiveErrorWhenSendingInvalidStatusUpdateForNetworkElementGroup(A4NetworkElementGroup negData) {
        LogicalResourceUpdate negLogicalResource = new A4ResourceInventoryServiceMapper()
                .getLogicalResourceUpdate(negData, INVALID_OPERATIONAL_STATE);

        a4ResourceInventoryService
                .logicalResource()
                .updateLogicalResourcePatch()
                .idPath(negData.getUuid())
                .body(negLogicalResource)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_BAD_REQUEST_400)));
    }

    @Step("Send new operational state for Network Element")
    public void sendStatusUpdateForNetworkElement(A4NetworkElement neData, A4NetworkElementGroup negData, String newOperationalState) {
        LogicalResourceUpdate neLogicalResource = new A4ResourceInventoryServiceMapper()
                .getLogicalResourceUpdate(neData, negData, newOperationalState);

        a4ResourceInventoryService
                .logicalResource()
                .updateLogicalResourcePatch()
                .idPath(neData.getUuid())
                .body(neLogicalResource)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    @Step("Send invalid operational state for Network Element")
    public void receiveErrorWhenSendingInvalidStatusUpdateForNetworkElement(A4NetworkElement neData, A4NetworkElementGroup negData) {
        LogicalResourceUpdate neLogicalResource = new A4ResourceInventoryServiceMapper()
                .getLogicalResourceUpdate(neData, negData, INVALID_OPERATIONAL_STATE);

        a4ResourceInventoryService
                .logicalResource()
                .updateLogicalResourcePatch()
                .idPath(neData.getUuid())
                .body(neLogicalResource)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_BAD_REQUEST_400)));
    }

    @Step("Send new operational state for Network Element Port")
    public void sendStatusUpdateForNetworkElementPort(A4NetworkElementPort nepData, A4NetworkElement neData, String newOperationalState) {
        LogicalResourceUpdate nepLogicalResource = new A4ResourceInventoryServiceMapper()
                .getLogicalResourceUpdate(nepData, neData, newOperationalState);

        a4ResourceInventoryService
                .logicalResource()
                .updateLogicalResourcePatch()
                .idPath(nepData.getUuid())
                .body(nepLogicalResource)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    @Step("Send invalid operational state for Network Element Port")
    public void receiveErrorWhenSendingInvalidStatusUpdateForNetworkElementPort(A4NetworkElementPort nepData, A4NetworkElement neData) {
        LogicalResourceUpdate nepLogicalResource = new A4ResourceInventoryServiceMapper()
                .getLogicalResourceUpdate(nepData, neData, INVALID_OPERATIONAL_STATE);

        a4ResourceInventoryService
                .logicalResource()
                .updateLogicalResourcePatch()
                .idPath(nepData.getUuid())
                .body(nepLogicalResource)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_BAD_REQUEST_400)));
    }

    @Step("Check Network Element Group as Logical Resource representation")
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
