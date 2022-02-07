package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.OltUplinkBusinessReferencen;
import com.tsystems.tm.acc.tests.osr.uplink.resource.inventory.management.v5_2_1_client.model.ChangeBngPort;
import com.tsystems.tm.acc.tests.osr.uplink.resource.inventory.management.v5_2_1_client.model.EquipmentBusinessRef;

import java.util.ArrayList;
import java.util.List;

public class OltUplinkBusinessReferencenMapper {

    public List<ChangeBngPort> getChangeBngPorts(OltUplinkBusinessReferencen oltUplinkBusinessReferencen) {

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

}
