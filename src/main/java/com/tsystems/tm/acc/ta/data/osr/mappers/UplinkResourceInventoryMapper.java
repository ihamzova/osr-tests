package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.tests.osr.uplink.resource.inventory.management.v5_2_1_client.model.RelatedParty;
import com.tsystems.tm.acc.tests.osr.uplink.resource.inventory.management.v5_2_1_client.model.Uplink;
import com.tsystems.tm.acc.tests.osr.uplink.resource.inventory.management.v5_2_1_client.model.EquipmentBusinessRef;

import java.time.OffsetDateTime;
import java.util.*;

public class UplinkResourceInventoryMapper {

    public static final String PLANNED = "PLANNED";
    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";

    public List <Uplink> getUplinks (String endSz, String state1, String state2, String state3, String manufacturer) {
        List <Uplink> uplinks = new ArrayList<>();
        uplinks.add(getUplink1(endSz, state1, manufacturer));
        if (state2!=null) {
            uplinks.add(getUplink2(endSz, state2, manufacturer));
        }
        if (state3!=null) {
            uplinks.add(getUplink3(endSz, state3, manufacturer));
        }
        return uplinks;
    }

    public Uplink getUplink1(String endSz, String state, String manufacturer) {
        return new Uplink()
                .id(Integer.toString(0 + (int) (Math.random() * 9999)))
                .href("/resource-order-resource-inventory/v5/uplink/1226")
                .creationDate(OffsetDateTime.now())
                .modificationDate(OffsetDateTime.now())
                .ordnungsnummer(10)
                .resourceId(UUID.randomUUID().toString())
                .lsz("4C1")
                .state(state)
                .baseType("PhysicalResource")
                .type("Uplink")
                .addRelatedPartyItem(relatedParty10001)
                .addPortsEquipmentBusinessRefItem(getPortEquipmentBusinesRefOlt(endSz, manufacturer))
                .addPortsEquipmentBusinessRefItem(portEquipmentBusinesRefBng1);
    }

    public Uplink getUplink2(String endSz, String state, String manufacturer) {
        return new Uplink()
                .id(Integer.toString(0 + (int) (Math.random() * 9999)))
                .href("/resource-order-resource-inventory/v5/uplink/1227")
                .creationDate(OffsetDateTime.now())
                .modificationDate(OffsetDateTime.now())
                .ordnungsnummer(10)
                .resourceId(UUID.randomUUID().toString())
                .lsz("4C1")
                .state(state)
                .baseType("PhysicalResource")
                .type("Uplink")
                .addRelatedPartyItem(relatedParty10001)
                .addPortsEquipmentBusinessRefItem(getPortEquipmentBusinesRefOlt(endSz, manufacturer))
                .addPortsEquipmentBusinessRefItem(portEquipmentBusinesRefBng2);
    }

    public Uplink getUplink3(String endSz, String state, String manufacturer) {
        return new Uplink()
                .id("1228")
                .href("/resource-order-resource-inventory/v5/uplink/1228")
                .creationDate(OffsetDateTime.now())
                .modificationDate(OffsetDateTime.now())
                .ordnungsnummer(10)
                .resourceId(UUID.randomUUID().toString())
                .lsz("4C1")
                .state(state)
                .baseType("PhysicalResource")
                .type("Uplink")
                .addRelatedPartyItem(relatedParty10001)
                .addPortsEquipmentBusinessRefItem(getPortEquipmentBusinesRefOlt(endSz, manufacturer))
                .addPortsEquipmentBusinessRefItem(portEquipmentBusinesRefBng3);
    }

    RelatedParty relatedParty10001 = new RelatedParty()
            .id("10001")
            .href("https://apigw-mercury-01.magic-dev.telekom.de/party/v2/partyManagement/organizations/10001")
            .role("Owner")
            .baseType("EntityRef")
            .type("RelatedParty");

    public EquipmentBusinessRef getPortEquipmentBusinesRefOlt(String endSz, String manufacturer) {
        if (manufacturer == "Huawei") {
            return new EquipmentBusinessRef()
                    .endSz(endSz)
                    .portName("0")
                    .slotName("19")
                    .deviceType("OLT")
                    .portType("ETHERNET")
                    .type("EquipmentBusinessRef");
        } else {
            return new EquipmentBusinessRef()
                    .endSz(endSz)
                    .portName("0")
                    .deviceType("OLT")
                    .portType("ETHERNET")
                    .type("EquipmentBusinessRef");
        }
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
