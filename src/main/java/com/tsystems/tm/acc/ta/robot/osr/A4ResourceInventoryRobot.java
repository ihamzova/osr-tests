package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4ResourceInventoryClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.*;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.LogicalResourceUpdate;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

public class A4ResourceInventoryRobot {
    private static final Integer HTTP_CODE_OK_200 = 200;
    private static final Integer HTTP_CODE_NO_CONTENT_204 = 204;

    private ApiClient a4ResourceInventory = new A4ResourceInventoryClient().getClient();

    @Step("Create network element group")
    public void createNetworkElementGroup(NetworkElementGroupDto networkElementGroup) {
        a4ResourceInventory
                .networkElementGroups()
                .createOrUpdateNetworkElementGroup()
                .body(networkElementGroup)
                .uuidPath(networkElementGroup.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Delete network element group")
    public void deleteNetworkElementGroup(String uuid) {
        a4ResourceInventory
                .networkElementGroups()
                .deleteNetworkElementGroup()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Create network element")
    public void createNetworkElement(NetworkElementDto networkElement) {
        a4ResourceInventory
                .networkElements()
                .createOrUpdateNetworkElement()
                .body(networkElement)
                .uuidPath(networkElement.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Delete network element")
    public void deleteNetworkElement(String uuid) {
        a4ResourceInventory
                .networkElements()
                .deleteNetworkElement()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Create network element port")
    public void createNetworkElementPort(NetworkElementPortDto networkElementPort) {
        a4ResourceInventory
                .networkElementPorts()
                .createOrUpdateNetworkElementPort()
                .body(networkElementPort)
                .uuidPath(networkElementPort.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Delete network element port")
    public void deleteNetworkElementPort(String uuid) {
        a4ResourceInventory
                .networkElementPorts()
                .deleteNetworkElementPort()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Create termination point")
    public void createTerminationPoint(TerminationPointDto terminationPoint) {
        List<AdditionalAttributeDto> additionalAttributes = new ArrayList<>();
        terminationPoint.getAdditionalAttribute().forEach(attribute -> additionalAttributes.add(new AdditionalAttributeDto().key(attribute.getKey()).value(attribute.getValue())));

        a4ResourceInventory
                .terminationPoints()
                .createOrUpdateTerminationPoint()
                .body(terminationPoint)
                .uuidPath(terminationPoint.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Delete termination point")
    public void deleteTerminationPoint(String uuid) {
        a4ResourceInventory
                .terminationPoints()
                .deleteTerminationPoint()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Delete termination point by it's NEP parent")
    public void deleteTerminationPointViaNepParent(NetworkElementPortDto networkElementPortParent) {
        // As we don't know the TP UUID we have to find via it's parent, in this case the parent is a NEP
        List<TerminationPointDto> terminationPointList = a4ResourceInventory
                .terminationPoints()
                .findTerminationPoints()
                .parentUuidQuery(networkElementPortParent.getUuid())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertEquals(terminationPointList.size(), 1);

        deleteTerminationPoint(terminationPointList.get(0).getUuid());
    }
}
