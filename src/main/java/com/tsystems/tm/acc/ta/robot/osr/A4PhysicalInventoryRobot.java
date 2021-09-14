package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.A4PhysicalInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.mappers.A4PhysicalInventoryMapper;
import com.tsystems.tm.acc.ta.data.osr.models.A4Equipment;
import com.tsystems.tm.acc.ta.data.osr.models.A4Holder;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.PhysicalResource;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.PhysicalResourceUpdate;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.util.UUID;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.*;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_PHYSICAL_INVENTORY_MS;

public class A4PhysicalInventoryRobot {

    private static final AuthTokenProvider authTokenProvider =
            new RhssoClientFlowAuthTokenProvider(A4_PHYSICAL_INVENTORY_MS,
                    RhssoHelper.getSecretOfGigabitHub(A4_PHYSICAL_INVENTORY_MS));

    private final ApiClient a4PhysicalInventory = new A4PhysicalInventoryClient(authTokenProvider).getClient();
    private static final String ENTITY_TYP_MESSAGE = "Entity type is the same";

    @Step("Create Equipment represented as Physical Resource")
    public PhysicalResource createEquipment(A4Equipment eqData) {
        PhysicalResourceUpdate equipmentPhysicalResource = new A4PhysicalInventoryMapper()
                .getPhysicalResourceUpdate(eqData);

        return a4PhysicalInventory
                    .physicalResource()
                    .updatePhysicalResourcePut()
                    .idPath(eqData.getUuid())
                    .body(equipmentPhysicalResource)
                    .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    @Step("Delete Equipment represented as Physical Resource")
    public void deleteEquipment(A4Equipment eqData) {

        a4PhysicalInventory
                .physicalResource()
                .deletePhysicalResource()
                .idPath(eqData.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Delete Equipment from Physical Inventory - not found")
    public void deleteEquipmentNotFound(A4Equipment eqData) {

        a4PhysicalInventory
                .physicalResource()
                .deletePhysicalResource()
                .idPath(eqData.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NOT_FOUND_404)));
    }

    @Step("check Equipment created")
    public void checkEquipmentCreated(A4Equipment eqData) {
        PhysicalResource physicalResource = createEquipment(eqData);
        Assert.assertEquals(physicalResource.getId(), eqData.getUuid(), "UUID is the same");
        Assert.assertEquals(physicalResource.getAtBaseType(), "PhysicalResource", ENTITY_TYP_MESSAGE);
        Assert.assertEquals(physicalResource.getAtType(), "Equipment", ENTITY_TYP_MESSAGE);
        Assert.assertEquals(physicalResource.getResourceRelationship().size(), 2, "ResourceRelationship is the same");
        Assert.assertEquals(physicalResource.getCharacteristic().size(), 12, "Characteristics list is the same");
    }

    @Step("Create Holder represented as Physical Resource")
    public void createHolder(A4Holder hoData, String uuidEquipment) {

        PhysicalResourceUpdate holderPhysicalResource = new A4PhysicalInventoryMapper()
                .getPhysicalResourceUpdateHolder(hoData, uuidEquipment);

        PhysicalResource physicalResourceHolder =  a4PhysicalInventory
                    .physicalResource()
                    .updatePhysicalResourcePut()
                    .idPath(hoData.getUuid())
                    .body(holderPhysicalResource)
                    .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        Assert.assertEquals(physicalResourceHolder.getId(), hoData.getUuid(), "UUID is the same");
        Assert.assertEquals(physicalResourceHolder.getAtBaseType(), "PhysicalResource", ENTITY_TYP_MESSAGE);
        Assert.assertEquals(physicalResourceHolder.getAtType(), "Holder", ENTITY_TYP_MESSAGE);
        Assert.assertEquals(physicalResourceHolder.getResourceRelationship().size(), 1, "ResourceRelationship is the same");
        Assert.assertEquals(physicalResourceHolder.getCharacteristic().size(), 4, "Characteristics list is the same");

    }

    @Step("Create Holder represented as Physical Resource Equipment not exist")
    public void createHolderEquipmentNotFound(A4Holder hoData, String uuidEquipment) {

        PhysicalResourceUpdate holderPhysicalResource = new A4PhysicalInventoryMapper()
                .getPhysicalResourceUpdateHolder(hoData, uuidEquipment);

        a4PhysicalInventory
                .physicalResource()
                .updatePhysicalResourcePut()
                .idPath(hoData.getUuid())
                .body(holderPhysicalResource)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_NOT_FOUND_404)));
    }

    @Step("Delete Holder from Physical Inventory - not found")
    public void deleteHolderNotFound(A4Holder hoData) {
        if (hoData.getUuid().isEmpty())
            hoData.setUuid(UUID.randomUUID().toString());
        a4PhysicalInventory
                .physicalResource()
                .deletePhysicalResource()
                .idPath(hoData.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NOT_FOUND_404)));
    }

    @Step("Delete Holder represented as Physical Resource")
    public void deleteHolder(A4Holder hoData) {

        a4PhysicalInventory
                .physicalResource()
                .deletePhysicalResource()
                .idPath(hoData.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }
}
