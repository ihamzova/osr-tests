package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.model.*;
//import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getRandomDigits;

public class A4ResourceOrderMapper {

    public static final String RAHMEN_VERTRAGS_NR = "RahmenvertragsNr";
    public static final String CARRIER_BSA_REFERENCE = "Subscription.keyA";
    public static final String VUEP_PUBLIC_REFERENZ_NR = "VUEP_Public_Referenz-Nr.";
    public static final String LACP_AKTVUEP_PUBLIC_REFERENZ_NRIV = "LACP_aktiv";
    public static final String MTU_SIZE = "MTU-Size";
    public static final String VLAN_RANGE = "VLAN_Range";
    public static final String QOS_LIST = "QoS_List";


    public ResourceOrder buildResourceOrder() {
        return new ResourceOrder()
                .externalId("merlin_id_" + getRandomDigits(4))
                .description("resource order of osr-tests")
                .name("resource order name")
                .id(UUID.randomUUID().toString());
    }

    public List<Characteristic> buildResourceCharacteristicList() {
        List<Characteristic> cList = new ArrayList<>();

        addCharacteristic(RAHMEN_VERTRAGS_NR, getRandomDigits(8), "valueTypeRv", cList);
        addCharacteristic(CARRIER_BSA_REFERENCE, UUID.randomUUID().toString(), "valueTypeCbr", cList);
        addCharacteristic(VUEP_PUBLIC_REFERENZ_NR, "A1000851", "valueTypeVuep", cList);
        addCharacteristic(LACP_AKTVUEP_PUBLIC_REFERENZ_NRIV, "true", "valueTypeLacp", cList);
        addCharacteristic(MTU_SIZE, "1590", "valueTypeMtu", cList);
        addCharacteristic(VLAN_RANGE, buildVlanRange(), "Object", cList);
        addCharacteristic(QOS_LIST, buildQosList(), "Object", cList);

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
                .qosBandwidth(bwDown)
        );
    }

}
