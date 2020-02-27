package com.tsystems.tm.acc.ta.data.osr.generators;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.tsystems.tm.acc.WebhookDefinitionModel;
import com.tsystems.tm.acc.data.AbstractGeneratorMapper;
import com.tsystems.tm.acc.data.exceptions.MapperError;
import com.tsystems.tm.acc.data.model.Artifact;
import com.tsystems.tm.acc.data.model.DataKey;
import com.tsystems.tm.acc.data.models.oltdevice.OltDevice;
import com.tsystems.tm.acc.data.registry.RegistryRegistry;
import com.tsystems.tm.acc.swagger.plugin.JSONInterface;
import com.tsystems.tm.acc.tests.osr.seal.client.model.*;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMapping;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMappingRequest;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMappingResponse;
import com.tsystems.tm.acc.tests.osr.seal.client.invoker.JSON;

import java.util.*;

public class SealAccessNodeConfigurationGeneratorMapper extends AbstractGeneratorMapper<StubMapping> {
    private static final String OPTIC_VENDOR_PART_NUMBER = "OpticVendorSpecific:00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\n" +
            "                                    " +
            "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\n" +
            "                                ";
    private static final String OPTIC_VENDOR_MAT_NUMBER = "OpticUserEeprom:";
    private static final String MGMT_ELEMENT_RESOURCE_STATE = "WORKING";
    private static final String MGMT_ELEMENT_COMMUNICATION_STATE = "CS_AVAILABLE";
    private static final String DEFAULT_SHELF = "1";
    private static final String DEFAULT_SLOT = "1";
    private static final String MODULE_RESOURCE_STATE = "INSTALLING_INSTALLED";
    private static final String MODULE_RESOURCE_FULFILMENT_STATE = "IN_SERVICE";
    private static final int PORTS_NUMBER = 8;
    private static volatile int moduleCount = 0;

    @Override
    public List<StubMapping> getData(Artifact artifact, RegistryRegistry registry) throws MapperError {
        List<StubMapping> values = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        for (String value : artifact.getParameters()) {
            OltDevice olt =
                    mapper.convertValue(registry.getObjectForKey(new DataKey(value, artifact.getType())),
                            OltDevice.class);

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
            managedElement.setProductName(olt.getHersteller());
            managedElement.setManufacturer(olt.getBezeichnung());
            managedElement.setSoftwareVersion(olt.getFirmwareVersion());
            managedElement.setResourceState(CallbackgetaccessnodeinventoryrequestPayloadManagedElement.
                    ResourceStateEnum.fromValue(MGMT_ELEMENT_RESOURCE_STATE));
            managedElement.setCommunicationState(CallbackgetaccessnodeinventoryrequestPayloadManagedElement.
                    CommunicationStateEnum.fromValue(MGMT_ELEMENT_COMMUNICATION_STATE));

            payload.setManagedElement(managedElement);

            List<CallbackgetaccessnodeinventoryrequestPayloadModules> moduleList = new ArrayList<>();

            CallbackgetaccessnodeinventoryrequestPayloadModules module = new CallbackgetaccessnodeinventoryrequestPayloadModules();
            module.setSlot("1");
            module.setManufacturer(olt.getBezeichnung());
            module.setShelf(DEFAULT_SHELF);
            module.setInstalledEquipmentObjectType("H805GPBD");
            module.setInstalledVersion("507(2015-8-27)");
            module.setInstalledSerialNumber("121BQW10B6123" + String.format("%03d", moduleCount++));
            module.setResourceState(CallbackgetaccessnodeinventoryrequestPayloadModules.
                    ResourceStateEnum.fromValue(MODULE_RESOURCE_STATE));
            module.setResourceFulfillmentState(CallbackgetaccessnodeinventoryrequestPayloadModules.
                    ResourceFulfillmentStateEnum.fromValue(MODULE_RESOURCE_FULFILMENT_STATE));
            moduleList.add(module);
            payload.setModules(moduleList);

            List<CallbackgetaccessnodeinventoryrequestPayloadPorts> portsList = new ArrayList<>(PORTS_NUMBER);
            for (int i = 0; i < PORTS_NUMBER; ++i) {
                CallbackgetaccessnodeinventoryrequestPayloadPorts port = new CallbackgetaccessnodeinventoryrequestPayloadPorts();
                port.setInstalledMatNumberSFP(OPTIC_VENDOR_MAT_NUMBER);
                port.setInstalledPartNumberSFP(OPTIC_VENDOR_PART_NUMBER);
                port.setPort(String.valueOf(i));
                port.setPortType(CallbackgetaccessnodeinventoryrequestPayloadPorts.PortTypeEnum.PON);
                port.setShelf(DEFAULT_SHELF);
                port.setSlot(DEFAULT_SLOT);
                portsList.add(port);
            }
            payload.setPorts(portsList);
            entity.setPayload(payload);
            StubMapping mapping = new StubMapping();
            StubMappingRequest request = new StubMappingRequest();
            request.setMethod("GET");
            request.setUrlPattern("/configuration/v1/accessNodes/([\\w\\d\\_]*)/?$");
            mapping.setRequest(request);
            StubMappingResponse response = new StubMappingResponse();
            response.setStatus(202);
            Map<String, String> respHeaders = new HashMap<>();
            respHeaders.put("Content-Type", "application/json");
            response.setHeaders(respHeaders);
            List<HttpHeader> webhookHeaders = new ArrayList<>(2);
            webhookHeaders.add(new HttpHeader("X-Callback-Correlation-Id", "{{request.headers.X-Callback-Correlation-Id}}"));
            webhookHeaders.add(new HttpHeader("Content-Type", "application/json"));

            JSONInterface json = new JSON();
            json.setGson(json.getGson().newBuilder().setPrettyPrinting().serializeNulls().create());

            WebhookDefinitionModel webhook = new WebhookDefinitionModel(RequestMethod.POST,
                    "{{request.headers.X-Callback-Url}}",
                    webhookHeaders,
                    new Body(json.serialize(entity)),
                    0,
                    null);
            mapping.setPostServeActions(Collections.singletonMap("webhook", webhook));
            mapping.setResponse(response);
            values.add(mapping);
        }
        return values;
    }
}
