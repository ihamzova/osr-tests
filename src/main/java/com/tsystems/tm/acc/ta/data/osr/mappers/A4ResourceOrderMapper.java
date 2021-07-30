package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getRandomDigits;

public class A4ResourceOrderMapper {

    public final static String VLAN_RANGE = "VLAN_Range";

    public ResourceOrder buildResourceOrder() {
        return new ResourceOrder()
                .externalId("merlin_id_" + getRandomDigits(4))
                .description("resource order of osr-tests")
                .name("resource order name")
                .id(UUID.randomUUID().toString());
    }

    public List<Characteristic> buildResourceCharacteristicList() {
        List<Characteristic> cList = new ArrayList<>();

        addCharacteristic("RahmenvertragsNr", getRandomDigits(8), "valueTypeRv", cList);
        addCharacteristic("Subscription.keyA", UUID.randomUUID().toString(), "valueTypeCbr", cList);
        addCharacteristic("VUEP_Public_Referenz-Nr.", "A1000851", "valueTypeVuep", cList);
        addCharacteristic("MTU-Size", "1590", "valueTypeMtu", cList);
        addCharacteristic("LACP_aktiv", "true", "valueTypeLacp", cList);
        addCharacteristic(VLAN_RANGE, buildVlanRange(), "Object", cList);
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
