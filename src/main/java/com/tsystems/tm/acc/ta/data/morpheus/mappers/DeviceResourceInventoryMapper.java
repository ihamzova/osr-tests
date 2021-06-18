package com.tsystems.tm.acc.ta.data.morpheus.mappers;

import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.client.model.AncpSession;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.client.model.EntityRef;

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




}
