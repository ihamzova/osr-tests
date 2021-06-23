package com.tsystems.tm.acc.ta.data.morpheus.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.client.model.*;
import org.openqa.selenium.remote.server.handler.interactions.touch.Up;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class DeviceResourceInventoryMapper {

    public EntityRef ancpIpSubnetRef(){
        return new EntityRef()
                .id("11");
    }

    public AncpSession ancpSessionDPU(){
        return new AncpSession()
                .id("99990")
                .href("string")
                .vlan(7)
                .sessionId("98765")
                .sessionType("DPU")
                .configurationStatus("ACTIVE")
                .configurationStep("CREATE_IP_RANGE_ASSIGNMENT")
                .ipAddressAccessNode("10.40.120.2")
                .ipAddressBng("10.40.120.3")
                .ancpIpSubnetRef(ancpIpSubnetRef());
    }

    public AncpSession ancpSessionOLT(){
        return new AncpSession()
                .id("99990")
                .href("href")
                .partitionId(123);
    }


    public Uplink uplink(OltDevice oltDevice){
        return new Uplink()
                .id("1049")
                .href("string")
                .state(UplinkState.ACTIVE)
                .creationDate(OffsetDateTime.now())
                .modificationDate(OffsetDateTime.now())
                .ordnungsnummer(10)
                .lsz(UplinkLsz._4C1)
                .portsEquipmentBusinessRef(Arrays.asList(new EquipmentBusinessRef()
                        .endSz(oltDevice.getEndsz())
                        .portName(oltDevice.getOltPort())
                        .slotName(oltDevice.getOltSlot())
                        .deviceType(DeviceType.OLT)
                        .portType(PortType.PON),
                        new EquipmentBusinessRef()
                        .endSz("49/30/179/43G1")
                        .portName("ge-1/2/3")
                        .slotName("1")
                        .deviceType(DeviceType.BNG)
                        .portType(PortType.ETHERNET)

                ));
    }



}


