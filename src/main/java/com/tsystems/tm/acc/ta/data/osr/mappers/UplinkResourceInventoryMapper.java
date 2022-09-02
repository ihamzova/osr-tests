package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.tests.osr.uplink.resource.inventory.management.v5_2_1_client.model.RelatedParty;
import com.tsystems.tm.acc.tests.osr.uplink.resource.inventory.management.v5_2_1_client.model.Uplink;
import com.tsystems.tm.acc.tests.osr.uplink.resource.inventory.management.v5_2_1_client.model.EquipmentBusinessRef;

import java.time.OffsetDateTime;
import java.util.*;

public class UplinkResourceInventoryMapper {

    public List<Uplink> getUplinks(String endSz, String state1, String state2, String state3, String manufacturer) {
        List<Uplink> uplinks = new ArrayList<>();
        uplinks.add(getUplinkWithParams(endSz,state1,manufacturer,1));
        if (state2 != null) {
            uplinks.add(getUplinkWithParams(endSz,state2,manufacturer,2));
        }
        if (state3 != null) {
            uplinks.add(getUplinkWithParams(endSz,state3,manufacturer,3));
        }
        return uplinks;
    }

    private Uplink getUplinkWithParams(String endSz, String state, String manufacturer, int version) {
        String href;
        EquipmentBusinessRef equipmentBusinessRef;

        switch (version) {
            case 1:
            default:
                href = "/resource-order-resource-inventory/v5/uplink/1226";
                equipmentBusinessRef = portEquipmentBusinesRefBng1;
                break;
            case 2:
                href = "/resource-order-resource-inventory/v5/uplink/1227";
                equipmentBusinessRef = portEquipmentBusinesRefBng2;
                break;
            case 3:
                href = "/resource-order-resource-inventory/v5/uplink/1228";
                equipmentBusinessRef = portEquipmentBusinesRefBng3;
                break;
        }
        return getCommonUplink()
                .addPortsEquipmentBusinessRefItem(getPortEquipmentBusinesRefOlt(endSz, manufacturer))
                .state(state)
                .href(href)
                .addPortsEquipmentBusinessRefItem(equipmentBusinessRef);
    }

    private Uplink getCommonUplink() {
        return new Uplink()
                .id(String.valueOf(new Random().nextInt()))
                .creationDate(OffsetDateTime.now())
                .modificationDate(OffsetDateTime.now())
                .ordnungsnummer(10)
                .resourceId(UUID.randomUUID().toString())
                .lsz("4C1")
                .baseType("PhysicalResource")
                .type("Uplink")
                .addRelatedPartyItem(relatedParty10001);
    }

    RelatedParty relatedParty10001 = new RelatedParty()
            .id("10001")
            .href("https://apigw-mercury-01.magic-dev.telekom.de/party/v2/partyManagement/organizations/10001")
            .role("Owner")
            .baseType("EntityRef")
            .type("RelatedParty");

    public EquipmentBusinessRef getPortEquipmentBusinesRefOlt(String endSz, String manufacturer) {
        EquipmentBusinessRef equipmentBusinessRef = new EquipmentBusinessRef()
                .endSz(endSz)
                .portName("0")
                .deviceType("OLT")
                .portType("ETHERNET")
                .type("EquipmentBusinessRef");
        if (Objects.equals(manufacturer, "Huawei")) {
            equipmentBusinessRef.slotName("19");
        }
        return equipmentBusinessRef;
    }

    EquipmentBusinessRef portEquipmentBusinesRefBng1 = new EquipmentBusinessRef()
            .endSz("49/30/179/43G1")
            .portName("ge-1/2/3")
            .deviceType("BNG")
            .portType("ETHERNET")
            .type("EquipmentBusinessRef");

    EquipmentBusinessRef portEquipmentBusinesRefBng2 = new EquipmentBusinessRef()
            .endSz("49/30/179/43G2")
            .portName("ge-2/2/3")
            .deviceType("BNG")
            .portType("ETHERNET")
            .type("EquipmentBusinessRef");

    EquipmentBusinessRef portEquipmentBusinesRefBng3 = new EquipmentBusinessRef()
            .endSz("49/30/179/43G3")
            .portName("ge-3/2/3")
            .deviceType("BNG")
            .portType("ETHERNET")
            .type("EquipmentBusinessRef");

}
