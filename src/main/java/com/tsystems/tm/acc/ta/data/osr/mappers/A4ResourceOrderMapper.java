package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.tmf652.client.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getRandomDigits;

public class A4ResourceOrderMapper {

    public static final String FRAME_CONTRACT_ID = "frameContractId";
    public static final String CARRIER_BSA_REFERENCE = "carrierBsaReference";
    public static final String PUBLIC_REFERENCE_ID = "publicReferenceId";
    public static final String LACP_ACTIVE = "lacpActive";
    public static final String MTU_SIZE = "mtuSize";
    public static final String VLAN_RANGE = "VLAN_Range";
    public static final String QOS_LIST = "QoS_List";
    public static final String OVERALL_BANDWIDTH = "linkOverallBandwidth";

    public ResourceOrder buildResourceOrder() {
        return new ResourceOrder()
                .externalId("sputnik_id_" + getRandomDigits(4))
                .description("resource order of osr-tests")
                .name("resource order name");
    }

    public List<Characteristic> buildResourceCharacteristicList() {
        List<Characteristic> cList = new ArrayList<>();

        addCharacteristic(FRAME_CONTRACT_ID, getRandomDigits(8), "valueTypeId", cList);
        addCharacteristic(CARRIER_BSA_REFERENCE, UUID.randomUUID().toString(), "valueTypeCbr", cList);
        addCharacteristic(PUBLIC_REFERENCE_ID, "A1000851", "valueTypePublic", cList);
        addCharacteristic(LACP_ACTIVE, "true", "valueTypeLacp", cList);
        addCharacteristic(MTU_SIZE, "1590", "valueTypeMtu", cList);
        addCharacteristic(VLAN_RANGE, buildVlanRange(), "valueTypeVlan", cList);
        addCharacteristic(QOS_LIST, buildQosList(), "valueTypeQos", cList);
        addCharacteristic(OVERALL_BANDWIDTH, "7777", "valueTypeBw", cList);
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
