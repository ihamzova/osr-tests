package com.tsystems.tm.acc.ta.data.osr.generators;

import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.tsystems.tm.acc.WebhookDefinitionModel;
import com.tsystems.tm.acc.data.models.OltDevice;
import com.tsystems.tm.acc.tests.osr.seal.client.invoker.JSON;
import com.tsystems.tm.acc.tests.osr.seal.client.model.*;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMapping;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMappingRequest;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMappingResponse;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SealAccessNodeConfigurationGeneratorMapper {
    private static final String OPTIC_VENDOR_PART_NUMBER = "OpticVendorSpecific:00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\n" +
            "                                    " +
            "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\n" +
            "                                ";
    private static final String OPTIC_VENDOR_MAT_NUMBER = "OpticUserEeprom:";
    private static final String MGMT_ELEMENT_RESOURCE_STATE = "WORKING";
    private static final String MGMT_ELEMENT_COMMUNICATION_STATE = "CS_AVAILABLE";
    private static final String DEFAULT_SHELF = "1";
    private static final String DEFAULT_PON_SLOT = "1";
    private static final String DEFAULT_ETH_SLOT = "19";
    private static final String MODULE_RESOURCE_STATE = "INSTALLING_INSTALLED";
    private static final String MODULE_RESOURCE_FULFILMENT_STATE = "IN_SERVICE";
    private static final int PON_PORTS_NUMBER = 8;
    private static final int ETH_PORTS_NUMBER = 2;
    private static volatile AtomicInteger moduleCount = new AtomicInteger(0);

    public StubMapping getData(OltDevice olt) {
        CallbackGetAccessnodeInventoryRequest entity = new CallbackGetAccessnodeInventoryRequest();
        entity.setError(null);
        CallbackgetaccessnodeinventoryrequestPayload payload = new CallbackgetaccessnodeinventoryrequestPayload();
        payload.setConnections(null);
        payload.setAncpConfigurations(null);
        payload.setOnts(null);

        CallbackgetaccessnodeinventoryrequestPayloadManagedElement managedElement = new CallbackgetaccessnodeinventoryrequestPayloadManagedElement();
        String name = olt.getVpsz() + '/' + olt.getFsz();
        name = name.replace('/', '_');
        managedElement.setName(name);
        managedElement.setIpAddress(olt.getIpAdresse());
        managedElement.setProductName(olt.getBezeichnung());
        managedElement.setManufacturer(olt.getHersteller());
        managedElement.setSoftwareVersion(olt.getFirmwareVersion());
        managedElement.setResourceState(CallbackgetaccessnodeinventoryrequestPayloadManagedElement.
                ResourceStateEnum.fromValue(MGMT_ELEMENT_RESOURCE_STATE));
        managedElement.setCommunicationState(CallbackgetaccessnodeinventoryrequestPayloadManagedElement.
                CommunicationStateEnum.fromValue(MGMT_ELEMENT_COMMUNICATION_STATE));

        payload.setManagedElement(managedElement);

        List<CallbackgetaccessnodeinventoryrequestPayloadModules> moduleList = new ArrayList<>();

        CallbackgetaccessnodeinventoryrequestPayloadModules ponModule = new CallbackgetaccessnodeinventoryrequestPayloadModules();
        ponModule.setSlot("1");
        ponModule.setManufacturer(olt.getHersteller());
        ponModule.setShelf(DEFAULT_SHELF);
        ponModule.setInstalledEquipmentObjectType("H805GPBD");
        ponModule.setInstalledVersion("507(2015-8-27)");
        ponModule.setInstalledSerialNumber("121BQW10B6123" + String.format("%03d", moduleCount.getAndIncrement()));
        ponModule.setResourceState(CallbackgetaccessnodeinventoryrequestPayloadModules.
                ResourceStateEnum.fromValue(MODULE_RESOURCE_STATE));
        ponModule.setResourceFulfillmentState(CallbackgetaccessnodeinventoryrequestPayloadModules.
                ResourceFulfillmentStateEnum.fromValue(MODULE_RESOURCE_FULFILMENT_STATE));
        moduleList.add(ponModule);

        CallbackgetaccessnodeinventoryrequestPayloadModules ethModule = new CallbackgetaccessnodeinventoryrequestPayloadModules();
        ethModule.setSlot("19");
        ethModule.setManufacturer(olt.getHersteller());
        ethModule.setShelf(DEFAULT_SHELF);
        ethModule.setInstalledEquipmentObjectType("H801X2CS");
        ethModule.setInstalledVersion("507(2019-07-04)");
        ethModule.setInstalledSerialNumber("H801X2715258001" + String.format("%03d", moduleCount.getAndIncrement()));
        ethModule.setResourceState(CallbackgetaccessnodeinventoryrequestPayloadModules.
                ResourceStateEnum.fromValue(MODULE_RESOURCE_STATE));
        ethModule.setResourceFulfillmentState(CallbackgetaccessnodeinventoryrequestPayloadModules.
                ResourceFulfillmentStateEnum.fromValue(MODULE_RESOURCE_FULFILMENT_STATE));
        moduleList.add(ethModule);
        payload.setModules(moduleList);

        List<CallbackgetaccessnodeinventoryrequestPayloadPorts> portsList =
                new ArrayList<>(PON_PORTS_NUMBER + ETH_PORTS_NUMBER);
        for (int i = 0; i < PON_PORTS_NUMBER; ++i) {
            CallbackgetaccessnodeinventoryrequestPayloadPorts port = new CallbackgetaccessnodeinventoryrequestPayloadPorts();
            port.setInstalledMatNumberSFP(OPTIC_VENDOR_MAT_NUMBER);
            port.setInstalledPartNumberSFP(OPTIC_VENDOR_PART_NUMBER);
            port.setPort(String.valueOf(i));
            port.setPortType(CallbackgetaccessnodeinventoryrequestPayloadPorts.PortTypeEnum.PON);
            port.setShelf(DEFAULT_SHELF);
            port.setSlot(DEFAULT_PON_SLOT);
            portsList.add(port);
        }

        for (int i = 0; i < ETH_PORTS_NUMBER; ++i) {
            CallbackgetaccessnodeinventoryrequestPayloadPorts port = new CallbackgetaccessnodeinventoryrequestPayloadPorts();
            port.setInstalledMatNumberSFP(OPTIC_VENDOR_MAT_NUMBER);
            port.setInstalledPartNumberSFP(OPTIC_VENDOR_PART_NUMBER);
            port.setPort(String.valueOf(i));
            port.setPortType(CallbackgetaccessnodeinventoryrequestPayloadPorts.PortTypeEnum.ETHERNET);
            port.setShelf(DEFAULT_SHELF);
            port.setSlot(DEFAULT_ETH_SLOT);
            portsList.add(port);
        }
        payload.setPorts(portsList);
        entity.setPayload(payload);
        StubMapping mapping = new StubMapping();
        StubMappingRequest request = new StubMappingRequest();
        request.setMethod("GET");
        request.setUrlPattern("/configuration/v1/accessNodes/" + name + "/?$");
        mapping.setRequest(request);
        StubMappingResponse response = new StubMappingResponse();
        response.setStatus(202);
        Map<String, String> respHeaders = new HashMap<>();
        respHeaders.put("Content-Type", "application/json");
        response.setHeaders(respHeaders);
        List<HttpHeader> webhookHeaders = new ArrayList<>(2);
        webhookHeaders.add(new HttpHeader("X-Callback-Correlation-Id", "{{request.headers.X-Callback-Correlation-Id}}"));
        webhookHeaders.add(new HttpHeader("Content-Type", "application/json"));

        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().setPrettyPrinting().serializeNulls().create());

        WebhookDefinitionModel webhook = new WebhookDefinitionModel(RequestMethod.POST,
                "{{request.headers.X-Callback-Url}}",
                webhookHeaders,
                new Body(json.serialize(entity)),
                0,
                null);
        mapping.setPostServeActions(Collections.singletonMap("webhook", webhook));
        mapping.setResponse(response);
        mapping.setPriority(1);
        return mapping;
    }
}
