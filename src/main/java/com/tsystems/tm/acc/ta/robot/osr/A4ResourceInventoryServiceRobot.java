package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.A4ResourceInventoryServiceClient;
import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryServiceMapper;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.LogicalResource;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.LogicalResourceUpdate;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceCharacteristic;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_CREATED_201;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_SERVICE_MS;

public class A4ResourceInventoryServiceRobot {

    private static final AuthTokenProvider authTokenProvider =
            new RhssoClientFlowAuthTokenProvider(A4_RESOURCE_INVENTORY_SERVICE_MS,
                    RhssoHelper.getSecretOfGigabitHub(A4_RESOURCE_INVENTORY_SERVICE_MS));

    private final ApiClient a4ResourceInventoryService = new A4ResourceInventoryServiceClient(authTokenProvider).getClient();

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

    @Step("Send new operational state for Network Element Port")
    public void sendStatusUpdateForNetworkElementPort(A4NetworkElementPort nepData, A4NetworkElement neData, String newOperationalState, String newDescription) {
        LogicalResourceUpdate nepLogicalResource = new A4ResourceInventoryServiceMapper()
                .getLogicalResourceUpdate(nepData, neData, newOperationalState, newDescription);

        a4ResourceInventoryService
                .logicalResource()
                .updateLogicalResourcePatch()
                .idPath(nepData.getUuid())
                .body(nepLogicalResource)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    @Step("Send new operational state for Network Service Profile (FTTH Access)")
    public void sendStatusUpdateForNetworkServiceProfileFtthAccess(A4NetworkServiceProfileFtthAccess nspFtthData, A4TerminationPoint tpData, String newOperationalState) {
        LogicalResourceUpdate nepLogicalResource = new A4ResourceInventoryServiceMapper()
                .getLogicalResourceUpdate(nspFtthData, tpData, newOperationalState);

        a4ResourceInventoryService
                .logicalResource()
                .updateLogicalResourcePatch()
                .idPath(nspFtthData.getUuid())
                .body(nepLogicalResource)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    @Step("Send new operational state and Port Reference for Network Service Profile (FTTH Access)")
    public void sendStatusAndPortRefUpdateForNetworkServiceProfileFtthAccess
            (A4NetworkServiceProfileFtthAccess nspFtthData,
             A4TerminationPoint tpData, String newOperationalState, A4NetworkElementPort nepData) {
        LogicalResourceUpdate nepLogicalResource = new A4ResourceInventoryServiceMapper()
                .getLogicalResourceUpdate(nspFtthData, tpData, newOperationalState, nepData);

        a4ResourceInventoryService
                .logicalResource()
                .updateLogicalResourcePatch()
                .idPath(nspFtthData.getUuid())
                .body(nepLogicalResource)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    @Step("Send new operational state for Network Service Profile (A10NSP)")
    public void sendStatusUpdateForNetworkServiceProfileA10Nsp(A4NetworkServiceProfileA10Nsp nspA10Data, A4TerminationPoint tpData, String newOperationalState) {
        LogicalResourceUpdate nspA10LogicalResource = new A4ResourceInventoryServiceMapper()
                .getLogicalResourceUpdate(nspA10Data, tpData, newOperationalState);

        a4ResourceInventoryService
                .logicalResource()
                .updateLogicalResourcePatch()
                .idPath(nspA10Data.getUuid())
                .body(nspA10LogicalResource)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    @Step("Send new operational state for Network Service Profile (A10NSP)")
    public void sendStatusUpdateForNetworkServiceProfileL2Bsa(A4NetworkServiceProfileL2Bsa nspL2Data, A4TerminationPoint tpData, String newOperationalState) {
        LogicalResourceUpdate nspL2LogicalResource = new A4ResourceInventoryServiceMapper()
                .getLogicalResourceUpdate(nspL2Data, tpData, newOperationalState);

        a4ResourceInventoryService
                .logicalResource()
                .updateLogicalResourcePatch()
                .idPath(nspL2Data.getUuid())
                .body(nspL2LogicalResource)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    @Step("Send PATCH request with logical resource")
    public void sendPatchForLogicalResource(String uuid, LogicalResourceUpdate logicalResource) {
        a4ResourceInventoryService
                .logicalResource()
                .updateLogicalResourcePatch()
                .idPath(uuid)
                .body(logicalResource)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    @Step("Send new operational state for Network Element Link")
    public void sendStatusUpdateForNetworkElementLink(A4NetworkElementLink nelData, A4NetworkElementPort nepDataA, A4NetworkElementPort nepDataB, String newOperationalState) {
        LogicalResourceUpdate nepLogicalResource = new A4ResourceInventoryServiceMapper()
                .getLogicalResourceUpdate(nelData, nepDataA, nepDataB, newOperationalState);

        a4ResourceInventoryService
                .logicalResource()
                .updateLogicalResourcePatch()
                .idPath(nelData.getUuid())
                .body(nepLogicalResource)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
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
        Assert.assertEquals(logicalResourceList.get(0).getAtType(), "NetworkElementGroup", "Entity type is the same");
    }

    @Step("Check Logical Resource representation has expected characteristic and value")
    public void checkLogicalResourceHasCharacteristic(String uuid, String characteristic, String expectedValue) {
        List<LogicalResource> logicalResourceList =
                a4ResourceInventoryService
                        .logicalResource()
                        .retrieveLogicalResource()
                        .idPath(uuid)
                        .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        // Only one logical resource should be found by uuid
        Assert.assertEquals(logicalResourceList.size(), 1);

        boolean found = false;
        String foundValue = "";
        List<ResourceCharacteristic> characteristics = logicalResourceList.get(0).getCharacteristic();

        // Search for existence of characteristic in characteristics
        for (ResourceCharacteristic resourceCharacteristic : characteristics) {
            if (resourceCharacteristic.getName().equals(characteristic)) {
                found = true;
                foundValue = resourceCharacteristic.getValue();
                break;
            }
        }

        Assert.assertTrue(found);
        Assert.assertEquals(foundValue, expectedValue);
    }

}
