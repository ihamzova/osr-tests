package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.OltUplinkBusinessReferences;
import com.tsystems.tm.acc.tests.osr.uplink.resource.inventory.management.v5_2_1_client.model.ChangeBngPort;
import com.tsystems.tm.acc.tests.osr.uplink.resource.inventory.management.v5_2_1_client.model.EquipmentBusinessRef;

import java.util.ArrayList;
import java.util.List;

public class OltUplinkBusinessReferencesMapper {

    static public List<ChangeBngPort> getChangeBngPorts(OltUplinkBusinessReferences oltUplinkBusinessReferences) {

        ArrayList<ChangeBngPort> changeBngPorts = new ArrayList<>();

        EquipmentBusinessRef oltPortEquipmentBusinessRef = new EquipmentBusinessRef()
                .endSz(oltUplinkBusinessReferences.getOltPortEquipmentBusinessRef().getEndSz())
                .deviceType(oltUplinkBusinessReferences.getOltPortEquipmentBusinessRef().getDeviceType())
                .portName(oltUplinkBusinessReferences.getOltPortEquipmentBusinessRef().getPortName())
                .portType(oltUplinkBusinessReferences.getOltPortEquipmentBusinessRef().getPortType())
                .slotName(oltUplinkBusinessReferences.getOltPortEquipmentBusinessRef().getSlotName());

        EquipmentBusinessRef bngSourcePortEquipmentBusinessRef = new EquipmentBusinessRef()
                .endSz(oltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef().getEndSz())
                .deviceType(oltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef().getDeviceType())
                .portName(oltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef().getPortName())
                .portType(oltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef().getPortType())
                .slotName(oltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef().getSlotName());

        EquipmentBusinessRef bngTargetPortEquipmentBusinessRef = new EquipmentBusinessRef()
                .endSz(oltUplinkBusinessReferences.getBngTargetPortEquipmentBusinessRef().getEndSz())
                .deviceType(oltUplinkBusinessReferences.getBngTargetPortEquipmentBusinessRef().getDeviceType())
                .portName(oltUplinkBusinessReferences.getBngTargetPortEquipmentBusinessRef().getPortName())
                .portType(oltUplinkBusinessReferences.getBngTargetPortEquipmentBusinessRef().getPortType())
                .slotName(oltUplinkBusinessReferences.getBngTargetPortEquipmentBusinessRef().getSlotName());


        ChangeBngPort changeBngPort = new ChangeBngPort()
                .oltPortEquipmentBusinessRef(oltPortEquipmentBusinessRef)
                .bngSourcePortEquipmentBusinessRef(bngSourcePortEquipmentBusinessRef)
                .bngTargetPortEquipmentBusinessRef(bngTargetPortEquipmentBusinessRef);

        changeBngPorts.add(changeBngPort);
        return changeBngPorts;

    }


    static public com.tsystems.tm.acc.ta.data.osr.models.EquipmentBusinessRef getEquipmentBusinessRef(EquipmentBusinessRef equipmentBusinessRef) {

        com.tsystems.tm.acc.ta.data.osr.models.EquipmentBusinessRef modelEquipmentBusinessRef = new com.tsystems.tm.acc.ta.data.osr.models.EquipmentBusinessRef();
        modelEquipmentBusinessRef.setEndSz(equipmentBusinessRef.getEndSz());
        modelEquipmentBusinessRef.setDeviceType(equipmentBusinessRef.getDeviceType());
        modelEquipmentBusinessRef.setPortName(equipmentBusinessRef.getPortName());
        modelEquipmentBusinessRef.setPortType(equipmentBusinessRef.getPortType());
        modelEquipmentBusinessRef.setSlotName(equipmentBusinessRef.getSlotName());

        return modelEquipmentBusinessRef;
    }

    static public com.tsystems.tm.acc.ta.data.osr.models.EquipmentBusinessRef getAncpEquipmentBusinessRef(com.tsystems.tm.acc.tests.osr.ancp.resource.inventory.management.v5_0_0.client.model.EquipmentBusinessRef equipmentBusinessRef) {

        com.tsystems.tm.acc.ta.data.osr.models.EquipmentBusinessRef modelEquipmentBusinessRef = new com.tsystems.tm.acc.ta.data.osr.models.EquipmentBusinessRef();
        modelEquipmentBusinessRef.setEndSz(equipmentBusinessRef.getEndSz());
        modelEquipmentBusinessRef.setDeviceType(equipmentBusinessRef.getDeviceType());
        modelEquipmentBusinessRef.setPortName(equipmentBusinessRef.getPortName());
        modelEquipmentBusinessRef.setPortType(equipmentBusinessRef.getPortType());
        modelEquipmentBusinessRef.setSlotName(equipmentBusinessRef.getSlotName());

        return modelEquipmentBusinessRef;
    }

}
