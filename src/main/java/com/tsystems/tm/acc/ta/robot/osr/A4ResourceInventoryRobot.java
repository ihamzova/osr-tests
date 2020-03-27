package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4ResourceInventoryClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.AdditionalAttributeDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkElementGroupDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.TerminationPointDto;
import io.qameta.allure.Step;

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
}
