package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.A4Equipment;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.PhysicalResourceUpdate;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceCharacteristic;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceRef;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceRelationship;

import java.util.UUID;

public class A4PhysicalInventoryMapper {

    // Create PhysicalResourceUpdate representation of equipment
    public PhysicalResourceUpdate getPhysicalResourceUpdate(A4Equipment eqData) {
        if (eqData.getUuid().isEmpty())
            eqData.setUuid(UUID.randomUUID().toString());

        return generateGenericPhysicalResourceUpdate()
                .atType("Equipment")
                .description(eqData.getDescription())
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("manufacturer")
                        .value(eqData.getManufacturer()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("installedPartNumber")
                        .value(eqData.getManufacturer()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("manufactureDate")
                        .value(eqData.getManufactureDate()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("installedSerialNumber")
                        .value(eqData.getInstalledSerialNumber()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("equipmentRevisionLevel")
                        .value(eqData.getEquipmentRevisionLevel()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("matNumberStringRetrieved")
                        .value(eqData.getMatNumberStringRetrieved()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("matNumber")
                        .value(eqData.getMatNumber()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("installedEquipmentType")
                        .value(eqData.getInstalledEquipmentType()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("model")
                        .value(eqData.getModel()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("lastBootTime")
                        .value(eqData.getLastBootTime()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("creationTime")
                        .value(eqData.getCreationTime()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("lastUpdateTime")
                        .value(eqData.getLastUpdateTime()))
                .addResourceRelationshipItem(new ResourceRelationship()
                        .resourceRef(new ResourceRef()
                                .id(eqData.getNetworkElementUuid())
                                .type("NetworkElement")))
                .addResourceRelationshipItem(new ResourceRelationship()
                        .resourceRef(new ResourceRef()
                                .id(eqData.getHolderUuid())
                                .type("Holder")));
    }

    private PhysicalResourceUpdate generateGenericPhysicalResourceUpdate() {
        return new PhysicalResourceUpdate()
                .atBaseType("PhysicalResource");
    }

}
