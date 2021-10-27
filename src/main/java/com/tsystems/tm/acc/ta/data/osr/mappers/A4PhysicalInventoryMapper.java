package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.A4Connector;
import com.tsystems.tm.acc.ta.data.osr.models.A4Equipment;
import com.tsystems.tm.acc.ta.data.osr.models.A4Holder;
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
                        .value(eqData.getInstalledPartNumber()))
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

    // Create PhysicalResourceUpdate representation of holder
    public PhysicalResourceUpdate getPhysicalResourceUpdateHolder(A4Holder hoData, String uuidEquipment) {
        if (hoData.getUuid().isEmpty())
            hoData.setUuid(UUID.randomUUID().toString());

        return generateGenericPhysicalResourceUpdate()
                .atType("Holder")
                .description(hoData.getDescription())
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("label")
                        .value(hoData.getLabel()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("type")
                        .value(hoData.getType()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("creationTime")
                        .value(hoData.getCreationTime()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("lastUpdateTime")
                        .value(hoData.getLastUpdateTime()))
                .addResourceRelationshipItem(new ResourceRelationship()
                        .resourceRef(new ResourceRef()
                                .id(uuidEquipment)
                                .type("Equipment")));
    }

    // Create PhysicalResourceUpdate representation of holder, without equipment
    public PhysicalResourceUpdate getPhysicalResourceUpdateHolderWithoutEquipment(A4Holder hoData) {
        if (hoData.getUuid().isEmpty())
            hoData.setUuid(UUID.randomUUID().toString());

        return generateGenericPhysicalResourceUpdate()
                .atType("Holder")
                .description(hoData.getDescription())
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("label")
                        .value(hoData.getLabel()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("type")
                        .value(hoData.getType()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("creationTime")
                        .value(hoData.getCreationTime()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("lastUpdateTime")
                        .value(hoData.getLastUpdateTime()));
    }

    // Create PhysicalResourceUpdate representation of connector
    public PhysicalResourceUpdate getPhysicalResourceUpdateConnector(A4Connector coData, String uuidEquipment) {
        if (coData.getUuid().isEmpty())
            coData.setUuid(UUID.randomUUID().toString());

        return generateGenericPhysicalResourceUpdate()
                .atType("Connector")
                .description(coData.getDescription())
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("label")
                        .value(coData.getPhysicalLabel()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("speed")
                        .value(coData.getSpeed()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("media")
                        .value(coData.getMedia()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("protocol")
                        .value(coData.getProtocol()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("formfactor")
                        .value(coData.getFormfactor()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("creationTime")
                        .value(coData.getCreationTime()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("lastUpdateTime")
                        .value(coData.getLastUpdateTime()))
                .addResourceRelationshipItem(new ResourceRelationship()
                        .resourceRef(new ResourceRef()
                                .id(uuidEquipment)
                                .type("Equipment")));
    }


    // Create PhysicalResourceUpdate representation of connector, without equipment
    public PhysicalResourceUpdate getPhysicalResourceUpdateConnectorWithoutEquipment(A4Connector coData) {
        if (coData.getUuid().isEmpty())
            coData.setUuid(UUID.randomUUID().toString());

        return generateGenericPhysicalResourceUpdate()
                .atType("Connector")
                .description(coData.getDescription())
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("label")
                        .value(coData.getPhysicalLabel()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("speed")
                        .value(coData.getSpeed()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("media")
                        .value(coData.getMedia()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("protocol")
                        .value(coData.getProtocol()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("formfactor")
                        .value(coData.getFormfactor()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("creationTime")
                        .value(coData.getCreationTime()))
                .addCharacteristicItem(new ResourceCharacteristic()
                        .name("lastUpdateTime")
                        .value(coData.getLastUpdateTime()));
    }

    private PhysicalResourceUpdate generateGenericPhysicalResourceUpdate() {
        return new PhysicalResourceUpdate()
                .atBaseType("PhysicalResource");
    }

}
