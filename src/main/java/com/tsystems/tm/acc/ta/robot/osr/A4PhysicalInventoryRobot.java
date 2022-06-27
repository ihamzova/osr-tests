package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4PhysicalInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.mappers.A4PhysicalInventoryMapper;
import com.tsystems.tm.acc.ta.data.osr.models.A4Connector;
import com.tsystems.tm.acc.ta.data.osr.models.A4Equipment;
import com.tsystems.tm.acc.ta.data.osr.models.A4Holder;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.PhysicalResource;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.PhysicalResourceUpdate;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.util.UUID;

import static com.tsystems.tm.acc.ta.data.HttpConstants.*;
import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;

public class A4PhysicalInventoryRobot {
    private final A4PhysicalInventoryClient a4PhysicalInventory = new A4PhysicalInventoryClient();
    private static final String ENTITY_TYP_MESSAGE = "Entity type is the same";

    @Step("Create Equipment represented as Physical Resource")
    public PhysicalResource createEquipment(A4Equipment eqData) {
        PhysicalResourceUpdate equipmentPhysicalResource = new A4PhysicalInventoryMapper()
                .getPhysicalResourceUpdate(eqData);

        return a4PhysicalInventory.getClient()
                .physicalResource()
                .updatePhysicalResourcePut()
                .idPath(eqData.getUuid())
                .body(equipmentPhysicalResource)
                .executeAs(checkStatus(HTTP_CODE_CREATED_201));
    }

    @Step("Delete Equipment represented as Physical Resource")
    public void deleteEquipment(A4Equipment eqData) {

        a4PhysicalInventory.getClient()
                .physicalResource()
                .deletePhysicalResource()
                .idPath(eqData.getUuid())
                .execute(checkStatus(HTTP_CODE_NO_CONTENT_204));
    }

    @Step("Delete Equipment from Physical Inventory - not found")
    public void deleteEquipmentNotFound(A4Equipment eqData) {

        a4PhysicalInventory.getClient()
                .physicalResource()
                .deletePhysicalResource()
                .idPath(eqData.getUuid())
                .execute(checkStatus(HTTP_CODE_NOT_FOUND_404));
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

        PhysicalResource physicalResourceHolder = a4PhysicalInventory.getClient()
                .physicalResource()
                .updatePhysicalResourcePut()
                .idPath(hoData.getUuid())
                .body(holderPhysicalResource)
                .executeAs(checkStatus(HTTP_CODE_CREATED_201));

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

        a4PhysicalInventory.getClient()
                .physicalResource()
                .updatePhysicalResourcePut()
                .idPath(hoData.getUuid())
                .body(holderPhysicalResource)
                .executeAs(checkStatus(HTTP_CODE_NOT_FOUND_404));
    }

    @Step("Create Holder represented as Physical Resource, without Equipment")
    public void createHolderWithoutEquipment(A4Holder hoData) {

        PhysicalResourceUpdate holderPhysicalResource = new A4PhysicalInventoryMapper()
                .getPhysicalResourceUpdateHolderWithoutEquipment(hoData);

        a4PhysicalInventory.getClient()
                .physicalResource()
                .updatePhysicalResourcePut()
                .idPath(hoData.getUuid())
                .body(holderPhysicalResource)
                .executeAs(checkStatus(HTTP_CODE_BAD_REQUEST_400));
    }

    @Step("Delete Holder from Physical Inventory - not found")
    public void deleteHolderNotFound(A4Holder hoData) {
        if (hoData.getUuid().isEmpty())
            hoData.setUuid(UUID.randomUUID().toString());
        a4PhysicalInventory.getClient()
                .physicalResource()
                .deletePhysicalResource()
                .idPath(hoData.getUuid())
                .execute(checkStatus(HTTP_CODE_NOT_FOUND_404));
    }

    @Step("Delete Holder represented as Physical Resource")
    public void deleteHolder(A4Holder hoData) {

        a4PhysicalInventory.getClient()
                .physicalResource()
                .deletePhysicalResource()
                .idPath(hoData.getUuid())
                .execute(checkStatus(HTTP_CODE_NO_CONTENT_204));
    }

    @Step("Create Connector represented as Physical Resource")
    public void createConnector(A4Connector coData, String uuidEquipment) {

        PhysicalResourceUpdate connectorPhysicalResource = new A4PhysicalInventoryMapper()
                .getPhysicalResourceUpdateConnector(coData, uuidEquipment);

        PhysicalResource physicalResourceConnector = a4PhysicalInventory.getClient()
                .physicalResource()
                .updatePhysicalResourcePut()
                .idPath(coData.getUuid())
                .body(connectorPhysicalResource)
                .executeAs(checkStatus(HTTP_CODE_CREATED_201));

        Assert.assertEquals(physicalResourceConnector.getId(), coData.getUuid(), "UUID is the same");
        Assert.assertEquals(physicalResourceConnector.getAtBaseType(), "PhysicalResource", ENTITY_TYP_MESSAGE);
        Assert.assertEquals(physicalResourceConnector.getAtType(), "Connector", ENTITY_TYP_MESSAGE);
        Assert.assertEquals(physicalResourceConnector.getResourceRelationship().size(), 1, "ResourceRelationship is the same");
        Assert.assertEquals(physicalResourceConnector.getCharacteristic().size(), 7, "Characteristics list is the same");
    }

    @Step("Delete Connector represented as Physical Resource")
    public void deleteConnector(A4Connector coData) {
        a4PhysicalInventory.getClient()
                .physicalResource()
                .deletePhysicalResource()
                .idPath(coData.getUuid())
                .execute(checkStatus(HTTP_CODE_NO_CONTENT_204));
    }

    @Step("Create Connector represented as Physical Resource, Equipment not exist")
    public void createConnectorEquipmentNotFound(A4Connector coData, String uuidEquipment) {

        PhysicalResourceUpdate connectorPhysicalResource = new A4PhysicalInventoryMapper()
                .getPhysicalResourceUpdateConnector(coData, uuidEquipment);

        a4PhysicalInventory.getClient()
                .physicalResource()
                .updatePhysicalResourcePut()
                .idPath(coData.getUuid())
                .body(connectorPhysicalResource)
                .executeAs(checkStatus(HTTP_CODE_NOT_FOUND_404));
    }

    @Step("Create Connector represented as Physical Resource, without Equipment")
    public void createConnectorWithoutEquipment(A4Connector coData) {

        PhysicalResourceUpdate connectorPhysicalResource = new A4PhysicalInventoryMapper()
                .getPhysicalResourceUpdateConnectorWithoutEquipment(coData);

        a4PhysicalInventory.getClient()
                .physicalResource()
                .updatePhysicalResourcePut()
                .idPath(coData.getUuid())
                .body(connectorPhysicalResource)
                .executeAs(checkStatus(HTTP_CODE_BAD_REQUEST_400));
    }

    @Step("Delete Connector from Physical Inventory - not found")
    public void deleteConnectorNotFound(A4Connector coData) {
        if (coData.getUuid().isEmpty())
            coData.setUuid(UUID.randomUUID().toString());
        a4PhysicalInventory.getClient()
                .physicalResource()
                .deletePhysicalResource()
                .idPath(coData.getUuid())
                .execute(checkStatus(HTTP_CODE_NOT_FOUND_404));
    }
}
