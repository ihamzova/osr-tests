package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementLink;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.model.*;

import java.util.ArrayList;
import java.util.List;

public class A4ResourceOrderMapper {

    public ResourceOrder buildResourceOrder(A4NetworkElementLink nelData) {
        return new ResourceOrder()
                .externalId("merlin_id_0815")
                .description("resource order of osr-tests")
                .name("resource order name")
                .orderItem(buildOrderItem(nelData));
    }

    public List<Characteristic> buildResourceCharacteristicList() {
        List<Characteristic> cList = new ArrayList<>();

        addCharacteristic("RahmenvertragsNr", "1122334455", "valueTypeRv", cList);
        addCharacteristic("Subscription.keyA", "f26bd5de-2150-47c7-8235-a688438973a4", "valueTypeCbr", cList);
        addCharacteristic("VUEP_Public_Referenz-Nr.", "A1000851", "valueTypeVuep", cList);
        addCharacteristic("MTU-Size", "1590", "valueTypeMtu", cList);
        addCharacteristic("LACP_aktiv", "true", "valueTypeLacp", cList);
        addCharacteristic("VLAN_Range", buildVlanRange(), "Object", cList);
        addCharacteristic("QoS_List", buildQosList(), "Object", cList);

        return cList;
    }

    private VlanRange buildVlanRange() {
        return new VlanRange()
                .vlanRangeLower("2")
                .vlanRangeUpper("3999");
    }

    private QosList buildQosList() {
        List<QosClass> qosClasses = new ArrayList<>();

        addQosClass("1", "0", "110", qosClasses);
        addQosClass("2", "1", "220", qosClasses);

        return new QosList().qosClasses(qosClasses);
    }

    private List<ResourceOrderItem> buildOrderItem(A4NetworkElementLink nelData) {
        List<ResourceOrderItem> orderItemList = new ArrayList<>();

        ResourceRefOrValue resource = new ResourceRefOrValue()
                .name(nelData.getLbz())
                .resourceCharacteristic(buildResourceCharacteristicList());

        ResourceOrderItem orderItem = new ResourceOrderItem()
                .action(OrderItemActionType.ADD)
                .resource(resource)
                .id("orderItemId");

        orderItemList.add(orderItem);

        return orderItemList;
    }

    private void addCharacteristic(String name, Object value, String valueType, List<Characteristic> cList) {
        cList.add(new Characteristic()
                .name(name)
                .value(value)
                .valueType(valueType)
        );
    }

    private void addQosClass(String className, String pBit, String bwDown, List<QosClass> qosClassList) {
        qosClassList.add(new QosClass()
                .qosClass(className)
                .qospBit(pBit)
                .qosBandwidthDown(bwDown)
        );
    }

}
