package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.OltUplinkBusinessReferencen;
import com.tsystems.tm.acc.tests.osr.uplink.resource.inventory.management.v5_2_1_client.model.ChangeBngPort;
import com.tsystems.tm.acc.tests.osr.uplink.resource.inventory.management.v5_2_1_client.model.EquipmentBusinessRef;

import java.util.ArrayList;
import java.util.List;

public class OltUplinkBusinessReferencenMapper {

    static public List<ChangeBngPort> getChangeBngPorts(OltUplinkBusinessReferencen oltUplinkBusinessReferencen) {

        ArrayList<ChangeBngPort> changeBngPorts = new ArrayList<>();

        EquipmentBusinessRef oltPortEquipmentBusinessRef = new EquipmentBusinessRef()
                .endSz(oltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef().getEndSz())
                .deviceType(oltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef().getDeviceType())
                .portName(oltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef().getPortName())
                .portType(oltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef().getPortType())
                .slotName(oltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef().getSlotName());

        EquipmentBusinessRef bngSourcePortEquipmentBusinessRef = new EquipmentBusinessRef()
                .endSz(oltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef().getEndSz())
                .deviceType(oltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef().getDeviceType())
                .portName(oltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef().getPortName())
                .portType(oltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef().getPortType())
                .slotName(oltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef().getSlotName());

        EquipmentBusinessRef bngTargetPortEquipmentBusinessRef = new EquipmentBusinessRef()
                .endSz(oltUplinkBusinessReferencen.getBngTargetPortEquipmentBusinessRef().getEndSz())
                .deviceType(oltUplinkBusinessReferencen.getBngTargetPortEquipmentBusinessRef().getDeviceType())
                .portName(oltUplinkBusinessReferencen.getBngTargetPortEquipmentBusinessRef().getPortName())
                .portType(oltUplinkBusinessReferencen.getBngTargetPortEquipmentBusinessRef().getPortType())
                .slotName(oltUplinkBusinessReferencen.getBngTargetPortEquipmentBusinessRef().getSlotName());


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
        modelEquipmentBusinessRef.setPortName(equipmentBusinessRef.getPortName());
        modelEquipmentBusinessRef.setSlotName(equipmentBusinessRef.getSlotName());

        return modelEquipmentBusinessRef;
    }

    static public com.tsystems.tm.acc.ta.data.osr.models.EquipmentBusinessRef getAncpEquipmentBusinessRef(com.tsystems.tm.acc.tests.osr.ancp.resource.inventory.management.v5_0_0.client.model.EquipmentBusinessRef equipmentBusinessRef) {

        com.tsystems.tm.acc.ta.data.osr.models.EquipmentBusinessRef modelEquipmentBusinessRef = new com.tsystems.tm.acc.ta.data.osr.models.EquipmentBusinessRef();
        modelEquipmentBusinessRef.setEndSz(equipmentBusinessRef.getEndSz());

        modelEquipmentBusinessRef.setDeviceType(equipmentBusinessRef.getDeviceType());
        modelEquipmentBusinessRef.setPortName(equipmentBusinessRef.getPortName());
        modelEquipmentBusinessRef.setPortType(equipmentBusinessRef.getPortType());
        modelEquipmentBusinessRef.setPortName(equipmentBusinessRef.getPortName());
        modelEquipmentBusinessRef.setSlotName(equipmentBusinessRef.getSlotName());

        return modelEquipmentBusinessRef;
    }

}
