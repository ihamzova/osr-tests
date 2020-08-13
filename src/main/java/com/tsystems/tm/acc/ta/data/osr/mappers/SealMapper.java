package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.tests.osr.seal.client.model.*;
import com.tsystems.tm.acc.tests.osr.seal.external.v1_2_01.client.model.CallbackV1DpuConfigurationMcpRequest;
import com.tsystems.tm.acc.tests.osr.seal.external.v1_2_01.client.model.CallbackV1DpuConfigurationOltRequest;
import com.tsystems.tm.acc.tests.osr.seal.external.v1_2_01.client.model.CallbackV1DpuDeconfigurationMcpRequest;

import java.math.BigDecimal;
import java.util.Arrays;
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
    private static final String DEFAULT_PON_SLOT = "1";
    private static final String DEFAULT_ETHERNET_SLOT = "19";
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
                        .modules(Arrays.asList(
                                new CallbackgetaccessnodeinventoryrequestPayloadModules()
                                        .slot(DEFAULT_PON_SLOT)
                                        .manufacturer(olt.getHersteller())
                                        .shelf(DEFAULT_SHELF)
                                        .installedEquipmentObjectType("H805GPBD")
                                        .installedVersion("507(2015-8-27)")
                                        .installedSerialNumber("121BQW10B6123" + String.format("%03d", moduleCount.getAndIncrement()))
                                        .resourceState(CallbackgetaccessnodeinventoryrequestPayloadModules.ResourceStateEnum.INSTALLING_INSTALLED)
                                        .resourceFulfillmentState(CallbackgetaccessnodeinventoryrequestPayloadModules.ResourceFulfillmentStateEnum.IN_SERVICE),
                                new CallbackgetaccessnodeinventoryrequestPayloadModules()
                                        .slot(DEFAULT_ETHERNET_SLOT)
                                        .manufacturer(olt.getHersteller())
                                        .shelf(DEFAULT_SHELF)
                                        .installedEquipmentObjectType("H801X2CS")
                                        .installedVersion("507(2019-07-04)")
                                        .installedSerialNumber("H801X2715258001" + String.format("%03d", moduleCount.getAndIncrement()))
                                        .resourceState(CallbackgetaccessnodeinventoryrequestPayloadModules.ResourceStateEnum.INSTALLING_INSTALLED)
                                        .resourceFulfillmentState(CallbackgetaccessnodeinventoryrequestPayloadModules.ResourceFulfillmentStateEnum.IN_SERVICE)
                        ))
                        .ports(
                                Stream.concat(
                                        IntStream.range(0, PON_PORTS_NUMBER)
                                                .mapToObj(i -> new CallbackgetaccessnodeinventoryrequestPayloadPorts()
                                                        .installedMatNumberSFP(OPTIC_VENDOR_MAT_NUMBER)
                                                        .installedPartNumberSFP(OPTIC_VENDOR_PART_NUMBER)
                                                        .port(String.valueOf(i))
                                                        .portType(CallbackgetaccessnodeinventoryrequestPayloadPorts.PortTypeEnum.PON)
                                                        .shelf(DEFAULT_SHELF)
                                                        .slot(DEFAULT_PON_SLOT)
                                                ),
                                        IntStream.range(0, ETH_PORTS_NUMBER)
                                                .mapToObj(i -> new CallbackgetaccessnodeinventoryrequestPayloadPorts()
                                                        .installedMatNumberSFP(OPTIC_VENDOR_MAT_NUMBER)
                                                        .installedPartNumberSFP(OPTIC_VENDOR_PART_NUMBER)
                                                        .port(String.valueOf(i))
                                                        .portType(CallbackgetaccessnodeinventoryrequestPayloadPorts.PortTypeEnum.ETHERNET)
                                                        .shelf(DEFAULT_SHELF)
                                                        .slot(olt.getOltSlot())
                                                )
                                ).collect(Collectors.toList())
                        )
                );

    }
}
