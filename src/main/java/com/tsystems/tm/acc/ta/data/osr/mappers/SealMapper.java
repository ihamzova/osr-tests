package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.tests.osr.seal.client.model.*;
import com.tsystems.tm.acc.tests.osr.seal.external.v1_2_01.client.model.*;
import com.tsystems.tm.acc.tests.osr.seal.external.v1_7_1.client.model.CallbackV1OntPonDetectionRequest;
import com.tsystems.tm.acc.tests.osr.seal.external.v1_7_1.client.model.Callbackv1ontpondetectionrequestEvents;
import com.tsystems.tm.api.client.seal.external.model.CallbackV1OltOltBasicConfigurationTaskRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SealMapper<CallbackGetEmptyListOfEmsEventsRequest> {
    private static final String OPTIC_VENDOR_PART_NUMBER =
            "OpticVendorSpecific:00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\n" +
                    "                                    " +
                    "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\n" +
                    "                                ";
    private static final String OPTIC_VENDOR_MAT_NUMBER = "OpticUserEeprom:";
    private static final String DEFAULT_SHELF = "0";
    // HUAWEI MA5600
    private static final int START_PON_SLOT = 1;
    private static final int START_ETHERNET_SLOT = 19;
    private static final int PON_PORTS_NUMBER = 8;
    private static final int ETH_PORTS_NUMBER = 2;
    private static final AtomicInteger moduleCount = new AtomicInteger(0);

    // ADTRAN SDX 6320-16
    private static final int SDX_START_PON_PORT = 1;
    private static final int SDX_START_ETHERNET_PORT = 1;

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
        if (olt.getBezeichnung().equals("SDX 6320-16")) {
            return getCallbackGetAccessnodeInventoryRequestAdtran(olt);
        }
        return getCallbackGetAccessnodeInventoryRequestMA5600(olt);
    }

    public CallbackGetAccessnodeInventoryRequest getCallbackGetAccessnodeInventoryRequestMA5600(OltDevice olt) {
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

    public CallbackGetAccessnodeInventoryRequest getCallbackGetAccessnodeInventoryRequestAdtran(OltDevice olt) {
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
                                                IntStream.range(SDX_START_PON_PORT, olt.getNumberOfPonPorts() + SDX_START_PON_PORT)
                                                        .mapToObj(i -> new CallbackgetaccessnodeinventoryrequestPayloadPorts()
                                                                .installedMatNumberSFP(OPTIC_VENDOR_MAT_NUMBER)
                                                                .installedPartNumberSFP(OPTIC_VENDOR_PART_NUMBER)
                                                                .port(String.valueOf(i))
                                                                .portType(CallbackgetaccessnodeinventoryrequestPayloadPorts.PortTypeEnum.PON)
                                                                .shelf(DEFAULT_SHELF)),
                                                IntStream.range(SDX_START_ETHERNET_PORT, olt.getNumberOfEthernetPorts() + SDX_START_ETHERNET_PORT)
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

    public CallbackV1OntPonDetectionRequest getCallbackGetEmptyListOfEmsEventsFromSeal(boolean success) {
        if (success) {
            return new CallbackV1OntPonDetectionRequest().events(callbackv1ontpondetectionrequestEvents)
                    .status(BigDecimal.valueOf(2000))
                    .statustype("SUCCESS")
                    .message("detect successfully");
        } else {
            return new CallbackV1OntPonDetectionRequest()
                    .status(BigDecimal.valueOf(5000))
                    .statustype("ERROR")
                    .message("Error callback");
        }
    }

    public List<Callbackv1ontpondetectionrequestEvents> callbackv1ontpondetectionrequestEvents = new ArrayList<>();

    public CallbackV1OltOltBasicConfigurationTaskRequest getCallbackV1OltOltBasicConfigurationTaskRequestSuccess() {
        return new CallbackV1OltOltBasicConfigurationTaskRequest()
                .status(BigDecimal.valueOf(2000))
                .statustype("SUCCESS")
                .message("job successfully executed");
    }

    public CallbackV1OltOltBasicConfigurationTaskRequest getCallbackV1OltOltBasicConfigurationTaskRequestError(boolean netconf) {
        if (netconf) {
            // SEAL callback in case of missing connection between OTL and EMS
            return new CallbackV1OltOltBasicConfigurationTaskRequest()
                    .status(BigDecimal.valueOf(5112))
                    .statustype("ERROR")
                    .message("precondition failed: netconf-session to OLT not established");
        } else {
            return new CallbackV1OltOltBasicConfigurationTaskRequest()
                    .status(BigDecimal.valueOf(5100))
                    .statustype("ERROR")
                    .message("Missing param name");
        }
    }

    public CallbackV1CreateAccesslineRequest getCallbackV1CreateAccesslineRequest() {
        return new CallbackV1CreateAccesslineRequest()
                .error(new Callbackv1getaccessnodeinventoryrequestError()
                        .status(BigDecimal.valueOf(5100))
                        .message("Missing param name"));
    }
}
