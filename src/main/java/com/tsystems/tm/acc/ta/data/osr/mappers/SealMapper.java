package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.tests.osr.seal.client.model.*;
import com.tsystems.tm.acc.tests.osr.seal.external.v1_2_01.client.model.CallbackV1DpuConfigurationMcpRequest;
import com.tsystems.tm.acc.tests.osr.seal.external.v1_2_01.client.model.CallbackV1DpuConfigurationOltRequest;
import com.tsystems.tm.acc.tests.osr.seal.external.v1_2_01.client.model.CallbackV1DpuDeconfigurationMcpRequest;
import com.tsystems.tm.acc.tests.osr.seal.external.v1_2_01.client.model.CallbackV1DpuDeconfigurationOltRequest;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SealMapper {
    private static final String OPTIC_VENDOR_PART_NUMBER =
            "OpticVendorSpecific:00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\n" +
                    "                                    " +
                    "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\n" +
                    "                                ";
    private static final String OPTIC_VENDOR_MAT_NUMBER = "OpticUserEeprom:";
    private static final String DEFAULT_SHELF = "0";
    private static final int START_PON_SLOT = 1;
    private static final int START_ETHERNET_SLOT = 19;
    private static final int PON_PORTS_NUMBER = 8;
    private static final int ETH_PORTS_NUMBER = 2;
    private static final AtomicInteger moduleCount = new AtomicInteger(0);

    public CallbackV1DpuConfigurationOltRequest getCallbackV1DpuConfigurationOltRequest(boolean success) {
        if (success) {
            return new CallbackV1DpuConfigurationOltRequest()
                    .status(BigDecimal.valueOf(2000))
                    .statustype("SUCCESS")
                    .message("successfully executed");
        } else {
            return new CallbackV1DpuConfigurationOltRequest()
                    .status(BigDecimal.valueOf(5000))
                    .statustype("ERROR")
                    .message("These arent the droids youre looking for");
        }
    }

    public CallbackV1DpuConfigurationMcpRequest getCallbackV1DpuConfigurationMcpRequest(boolean success) {
        if (success) {
            return new CallbackV1DpuConfigurationMcpRequest()
                    .status(BigDecimal.valueOf(2000))
                    .statustype("SUCCESS")
                    .message("successfully executed");
        } else {
            return new CallbackV1DpuConfigurationMcpRequest()
                    .status(BigDecimal.valueOf(5000))
                    .statustype("ERROR")
                    .message("These arent the droids youre looking for");
        }
    }

    public CallbackV1DpuDeconfigurationMcpRequest getCallbackV1DpuDeconfigurationMcpRequest(boolean success) {
        if (success) {
            return new CallbackV1DpuDeconfigurationMcpRequest()
                    .status(BigDecimal.valueOf(2000))
                    .statustype("SUCCESS")
                    .message("successfully executed");
        } else {
            return new CallbackV1DpuDeconfigurationMcpRequest()
                    .status(BigDecimal.valueOf(5000))
                    .statustype("ERROR")
                    .message("These arent the droids youre looking for");
        }
    }

    public CallbackV1DpuDeconfigurationOltRequest getCallbackV1DpuDeconfigurationOltRequest(boolean success) {
        if (success) {
            return new CallbackV1DpuDeconfigurationOltRequest()
                    .status(BigDecimal.valueOf(2000))
                    .statustype("SUCCESS")
                    .message("successfully executed");
        } else {
            return new CallbackV1DpuDeconfigurationOltRequest()
                    .status(BigDecimal.valueOf(5000))
                    .statustype("ERROR")
                    .message("These arent the droids youre looking for");
        }
    }

    public CallbackGetAccessnodeInventoryRequest getCallbackGetAccessnodeInventoryRequest(OltDevice olt) {
        return new CallbackGetAccessnodeInventoryRequest()
                .payload(new CallbackgetaccessnodeinventoryrequestPayload()
                        .managedElement(new CallbackgetaccessnodeinventoryrequestPayloadManagedElement()
                                .name(olt.getEndsz().replace('/', '_'))
                                .ipAddress(olt.getIpAdresse())
                                .productName(olt.getBezeichnung())
                                .manufacturer(olt.getHersteller())
                                .softwareVersion(olt.getFirmwareVersion())
                                .resourceState(CallbackgetaccessnodeinventoryrequestPayloadManagedElement.ResourceStateEnum.WORKING)
                                .communicationState(CallbackgetaccessnodeinventoryrequestPayloadManagedElement.CommunicationStateEnum.AVAILABLE)
                        )
                        .modules(Stream.concat(
                                IntStream.range(START_PON_SLOT, START_PON_SLOT + olt.getNumberOfPonSlots())
                                        .mapToObj(slot -> new CallbackgetaccessnodeinventoryrequestPayloadModules()
                                                .slot(String.valueOf(slot))
                                                .manufacturer(olt.getHersteller())
                                                .shelf(DEFAULT_SHELF)
                                                .installedEquipmentObjectType("H805GPBD")
                                                .installedVersion("507(2015-8-27)")
                                                .installedSerialNumber("121BQW10B6123" + String.format("%03d", moduleCount.getAndIncrement()))
                                                .resourceState(CallbackgetaccessnodeinventoryrequestPayloadModules.ResourceStateEnum.INSTALLING_INSTALLED)
                                                .resourceFulfillmentState(CallbackgetaccessnodeinventoryrequestPayloadModules.ResourceFulfillmentStateEnum.IN_SERVICE)),
                                IntStream.range(START_ETHERNET_SLOT, START_ETHERNET_SLOT + olt.getNumberOfEthernetSlots())
                                        .mapToObj(slot -> new CallbackgetaccessnodeinventoryrequestPayloadModules()
                                                .slot(String.valueOf(slot))
                                                .manufacturer(olt.getHersteller())
                                                .shelf(DEFAULT_SHELF)
                                                .installedEquipmentObjectType("H801X2CS")
                                                .installedVersion("507(2019-07-04)")
                                                .installedSerialNumber("H801X2715258001" + String.format("%03d", moduleCount.getAndIncrement()))
                                                .resourceState(CallbackgetaccessnodeinventoryrequestPayloadModules.ResourceStateEnum.INSTALLING_INSTALLED)
                                                .resourceFulfillmentState(CallbackgetaccessnodeinventoryrequestPayloadModules.ResourceFulfillmentStateEnum.IN_SERVICE))
                        ).collect(Collectors.toList()))
                        .ports(
                                Stream.of(
                                        IntStream.range(START_PON_SLOT, START_PON_SLOT + olt.getNumberOfPonSlots())
                                                .mapToObj(slot -> IntStream.range(0, PON_PORTS_NUMBER)
                                                        .mapToObj(i -> new CallbackgetaccessnodeinventoryrequestPayloadPorts()
                                                                .installedMatNumberSFP(OPTIC_VENDOR_MAT_NUMBER)
                                                                .installedPartNumberSFP(OPTIC_VENDOR_PART_NUMBER)
                                                                .port(String.valueOf(i))
                                                                .portType(CallbackgetaccessnodeinventoryrequestPayloadPorts.PortTypeEnum.PON)
                                                                .shelf(DEFAULT_SHELF)
                                                                .slot(String.valueOf(slot))
                                                        )),
                                        IntStream.range(START_ETHERNET_SLOT, START_ETHERNET_SLOT + olt.getNumberOfEthernetSlots())
                                                .mapToObj(slot -> IntStream.range(0, ETH_PORTS_NUMBER)
                                                        .mapToObj(i -> new CallbackgetaccessnodeinventoryrequestPayloadPorts()
                                                                .installedMatNumberSFP(OPTIC_VENDOR_MAT_NUMBER)
                                                                .installedPartNumberSFP(OPTIC_VENDOR_PART_NUMBER)
                                                                .port(String.valueOf(i))
                                                                .portType(CallbackgetaccessnodeinventoryrequestPayloadPorts.PortTypeEnum.ETHERNET)
                                                                .shelf(DEFAULT_SHELF)
                                                                .slot(String.valueOf(slot))
                                                        ))
                                )
                                        .flatMap(i -> i)
                                        .flatMap(i -> i)
                                        .collect(Collectors.toList())
                        )
                );

    }

    public CallbackGetAccessnodeInventoryRequest getCallbackGetAccessnodeAdtranInventoryRequest(OltDevice olt) {
        return new CallbackGetAccessnodeInventoryRequest()
                .payload(new CallbackgetaccessnodeinventoryrequestPayload()
                        .managedElement(new CallbackgetaccessnodeinventoryrequestPayloadManagedElement()
                                .name(olt.getEndsz().replace('/', '_'))
                                .ipAddress(olt.getIpAdresse())
                                .productName(olt.getBezeichnung())
                                .manufacturer(olt.getHersteller())
                                .softwareVersion(olt.getFirmwareVersion())
                                .resourceState(CallbackgetaccessnodeinventoryrequestPayloadManagedElement.ResourceStateEnum.WORKING)
                                .communicationState(CallbackgetaccessnodeinventoryrequestPayloadManagedElement.CommunicationStateEnum.AVAILABLE)
                        )
                        .ports(
                                Stream.of(
                                        IntStream.range(1, 8)
                                                .mapToObj(i -> new CallbackgetaccessnodeinventoryrequestPayloadPorts()
                                                        .installedMatNumberSFP(OPTIC_VENDOR_MAT_NUMBER)
                                                        .installedPartNumberSFP(OPTIC_VENDOR_PART_NUMBER)
                                                        .port(String.valueOf(i))
                                                        .portType(CallbackgetaccessnodeinventoryrequestPayloadPorts.PortTypeEnum.PON)
                                                        .shelf(DEFAULT_SHELF)),
                                        IntStream.range(1, 2)
                                                .mapToObj(i -> new CallbackgetaccessnodeinventoryrequestPayloadPorts()
                                                        .installedMatNumberSFP("")
                                                        .installedPartNumberSFP("")
                                                        .port(String.valueOf(i))
                                                        .portType(CallbackgetaccessnodeinventoryrequestPayloadPorts.PortTypeEnum.ETHERNET)
                                                        .shelf(DEFAULT_SHELF))

                                )
                                        .flatMap(i -> i)
                                        .collect(Collectors.toList())
                        ));

    }
}
