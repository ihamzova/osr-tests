package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.A4PhysicalInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.mappers.A4PhysicalInventoryMapper;
import com.tsystems.tm.acc.ta.data.osr.models.A4Equipment;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.PhysicalResource;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.PhysicalResourceUpdate;
import io.qameta.allure.Step;
import org.testng.Assert;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_CREATED_201;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_NO_CONTENT_204;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_PHYSICAL_INVENTORY_MS;

public class A4PhysicalInventoryRobot {

    private static final AuthTokenProvider authTokenProvider =
            new RhssoClientFlowAuthTokenProvider(A4_PHYSICAL_INVENTORY_MS,
                    RhssoHelper.getSecretOfGigabitHub(A4_PHYSICAL_INVENTORY_MS));

    private final ApiClient a4PhysicalInventory = new A4PhysicalInventoryClient(authTokenProvider).getClient();

    @Step("Create Equipment represented as Physical Resource")
    public void createEquipment(A4Equipment eqData) {
        PhysicalResourceUpdate equipmentPhysicalResource = new A4PhysicalInventoryMapper()
                .getPhysicalResourceUpdate(eqData);
        String uuid = eqData.getUuid();
        PhysicalResource physicalResource =
                a4PhysicalInventory
                        .physicalResource()
                        .updatePhysicalResourcePut()
                        .idPath(eqData.getUuid())
                        .body(equipmentPhysicalResource)
                        .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        Assert.assertEquals(physicalResource.getId(), uuid, "UUID is the same");
        Assert.assertEquals(physicalResource.getAtBaseType(), "PhysicalResource", "Entity type is the same");
        Assert.assertEquals(physicalResource.getAtType(), "Equipment", "Entity type is the same");
        Assert.assertEquals(physicalResource.getResourceRelationship().size(), 2, "ResourceRelationship is the same");
        Assert.assertEquals(physicalResource.getCharacteristic().size(), 12, "Characteristics list is the same");
    }

    @Step("Delete Equipment represented as Physical Resource")
    public void deleteEquipment(A4Equipment eqData) {

        a4PhysicalInventory
                .physicalResource()
                .deletePhysicalResource()
                .idPath(eqData.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }
}
