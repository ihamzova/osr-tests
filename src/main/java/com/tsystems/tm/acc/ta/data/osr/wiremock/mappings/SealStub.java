package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.osr.mappers.SealMapper;
import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.seal.external.v1_2_01.client.invoker.JSON;
import com.tsystems.tm.acc.wiremock.webhook.WebhookPostServeAction;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class SealStub extends AbstractStubMapping {
    public static final String OLT_DPU_CONFIGURATION_TASK_URL = "/resource-order-resource-inventory/v1/olt/dpuConfigurationTask";
    public static final String DPU_DPU_CONFIGURATION_TASK_URL = "/resource-order-resource-inventory/v1/dpu/dpuConfigurationTask";
    public static final String DPU_DPU_DECONFIGURATION_TASK_URL = "/resource-order-resource-inventory/v1/dpu/dpuDeconfigurationTask";
    public static final String DPU_OLT_DECONFIGURATION_TASK_URL = "/resource-order-resource-inventory/v1/olt/dpuDeconfigurationTask";
    public static final String CONFIGURATION_ACCESS_NODES_URL = "/configuration/v1/accessNodes";
    public static final String ONT_PON_DETECTION_IN_SEAL = "/resource-order-resource-inventory/v1/ontPonDetection/?";
    public static final String CONFIGURATION_ACCESS_NODES_3SCALE_URL = "/resource-order-resource-inventory/v1/inventory-retrieval";
    public static final String CONFIGURATION_ACCESS_NODES_UNIVERSAL_URL = String.format("(%s|%s)/{name}/?", CONFIGURATION_ACCESS_NODES_URL, CONFIGURATION_ACCESS_NODES_3SCALE_URL);
    public static final String OLT_BASIC_CONFIGURATION_TASK_URL = "/(resource-order-resource-inventory|configuration)/v1/olt/(oltBasicConfigurationTask)";
    public static final String ACCESS_LINE_CONFIGURATION_URL = "/resource-order-resource-inventory/v1/access-line-configuration/49_911_1100_76H1/accessLines";

    public MappingBuilder postOltDpuConfiguration202(Dpu dpu) {
        return post(urlPathEqualTo(OLT_DPU_CONFIGURATION_TASK_URL))
                .withName("postOltDpuConfiguration202")
                .willReturn(aDefaultResponseWithBody("", 202))
                .withRequestBody(matchingJsonPath(String.format("$[?(@.dpuName=='%s')]", dpu.getEndSz().replace("/", "_"))))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new SealMapper().getCallbackV1DpuConfigurationOltRequest(true))));
    }

    public MappingBuilder postOltDpuConfiguration202CallbackError(Dpu dpu) {
        return post(urlPathEqualTo(OLT_DPU_CONFIGURATION_TASK_URL))
                .withName("postOltDpuConfiguration202CallbackError")
                .willReturn(aDefaultResponseWithBody("", 202))
                .withRequestBody(matchingJsonPath(String.format("$[?(@.dpuName=='%s')]", dpu.getEndSz().replace("/", "_"))))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new SealMapper().getCallbackV1DpuConfigurationOltRequest(false))));
    }

    public MappingBuilder postDpuDpuConfiguration202(Dpu dpu) {
        return post(urlPathEqualTo(DPU_DPU_CONFIGURATION_TASK_URL))
                .withName("postDpuDpuConfiguration202")
                .willReturn(aDefaultResponseWithBody("", 202))
                .withRequestBody(matchingJsonPath(String.format("$[?(@.ancpDpuName=='%s')]", dpu.getEndSz())))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new SealMapper().getCallbackV1DpuConfigurationMcpRequest(true))));
    }

    public MappingBuilder postDomainDpuDpuConfiguration202(DpuDevice dpu) {
        return post(urlPathEqualTo(DPU_DPU_CONFIGURATION_TASK_URL))
                .withName("postDomainDpuDpuConfiguration202")
                .willReturn(aDefaultResponseWithBody("", 202))
                .withRequestBody(matchingJsonPath(String.format("$[?(@.ancpDpuName=='%s')]", dpu.getEndsz())))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new SealMapper().getCallbackV1DpuConfigurationMcpRequest(true))));
    }

    public MappingBuilder postDomainOltDpuConfiguration202(DpuDevice dpu) {
        return post(urlPathEqualTo(OLT_DPU_CONFIGURATION_TASK_URL))
                .withName("postOltDpuConfiguration202")
                .willReturn(aDefaultResponseWithBody("", 202))
                .withRequestBody(matchingJsonPath(String.format("$[?(@.dpuName=='%s')]", dpu.getEndsz().replace("/", "_"))))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new SealMapper().getCallbackV1DpuConfigurationOltRequest(true))));
    }

    public MappingBuilder postDomainDpuDpuDeconfiguration202(DpuDevice dpu) {
        return post(urlPathEqualTo(DPU_DPU_DECONFIGURATION_TASK_URL))
                .withName("postDomainDpuDpuDeconfiguration202")
                .willReturn(aDefaultResponseWithBody("", 202))
                .withRequestBody(matchingJsonPath(String.format("$[?(@.dpuName=='%s')]", dpu.getEndsz().replace("/", "_"))))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new SealMapper().getCallbackV1DpuDeconfigurationMcpRequest(true))));
    }

    public MappingBuilder postDomainDpuOltDeconfiguration202(DpuDevice dpu) {
        return post(urlPathEqualTo(DPU_OLT_DECONFIGURATION_TASK_URL))
                .withName("postDpuOltDeconfiguration202")
                .willReturn(aDefaultResponseWithBody("", 202))
                .withRequestBody(matchingJsonPath(String.format("$[?(@.dpuName=='%s')]", dpu.getEndsz().replace("/", "_"))))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new SealMapper().getCallbackV1DpuDeconfigurationOltRequest(true))));
    }

    public MappingBuilder postDpuDpuConfiguration202CallbackError(Dpu dpu) {
        return post(urlPathEqualTo(DPU_DPU_CONFIGURATION_TASK_URL))
                .withName("postDpuDpuConfiguration202CallbackError")
                .willReturn(aDefaultResponseWithBody("", 202))
                .withRequestBody(matchingJsonPath(String.format("$[?(@.ancpDpuName=='%s')]", dpu.getEndSz())))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new SealMapper().getCallbackV1DpuConfigurationMcpRequest(false))));
    }

    public MappingBuilder postDpuDpuDeconfiguration202(Dpu dpu) {
        return post(urlPathEqualTo(DPU_DPU_DECONFIGURATION_TASK_URL))
                .withName("postDpuDpuDeconfiguration202")
                .willReturn(aDefaultResponseWithBody("", 202))
                .withRequestBody(matchingJsonPath(String.format("$[?(@.dpuName=='%s')]", dpu.getEndSz().replace("/", "_"))))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new SealMapper().getCallbackV1DpuDeconfigurationMcpRequest(true))));
    }

    public MappingBuilder postDpuDpuDeconfiguration202CallbackError(Dpu dpu) {
        return post(urlPathEqualTo(DPU_DPU_DECONFIGURATION_TASK_URL))
                .withName("postDpuDpuDeconfiguration202CallbackError")
                .willReturn(aDefaultResponseWithBody("", 202))
                .withRequestBody(matchingJsonPath(String.format("$[?(@.dpuName=='%s')]", dpu.getEndSz().replace("/", "_"))))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new SealMapper().getCallbackV1DpuDeconfigurationMcpRequest(false))));
    }

    public MappingBuilder postDpuOltDeconfiguration202(Dpu dpu) {
        return post(urlPathEqualTo(DPU_OLT_DECONFIGURATION_TASK_URL))
                .withName("postDpuOltDeconfiguration202")
                .willReturn(aDefaultResponseWithBody("", 202))
                .withRequestBody(matchingJsonPath(String.format("$[?(@.dpuName=='%s')]", dpu.getEndSz().replace("/", "_"))))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new SealMapper().getCallbackV1DpuDeconfigurationOltRequest(true))));
    }

    public MappingBuilder postDpuOltDeconfiguration202CallbackError(Dpu dpu) {
        return post(urlPathEqualTo(DPU_OLT_DECONFIGURATION_TASK_URL))
                .withName("postDpuOltDeconfiguration202CallbackError")
                .willReturn(aDefaultResponseWithBody("", 202))
                .withRequestBody(matchingJsonPath(String.format("$[?(@.dpuName=='%s')]", dpu.getEndSz().replace("/", "_"))))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new SealMapper().getCallbackV1DpuDeconfigurationOltRequest(false))));
    }

    public MappingBuilder getAccessNodesConfiguration202(OltDevice oltDevice) {
        return get(urlPathMatching(CONFIGURATION_ACCESS_NODES_UNIVERSAL_URL.replace("{name}", oltDevice.getEndsz().replace("/", "_"))))
                .willReturn(aDefaultResponseWithBody("", 202))
                .withName("getAccessNodesConfiguration202_" + oltDevice.getEndsz().replace("/", "_"))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new SealMapper().getCallbackGetAccessnodeInventoryRequest(oltDevice))));
    }

    public MappingBuilder getEmptyListOfEmsEvents() {
        return post(urlPathMatching(ONT_PON_DETECTION_IN_SEAL))
                .withName("getEmptyListOfEmsEventsFromSeal")
                .willReturn(aDefaultResponseWithBody("", 202))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new SealMapper().getCallbackGetEmptyListOfEmsEventsFromSeal(true))))
                .atPriority(0);
    }

    public MappingBuilder postOltBasicConfiguration202CallbackSuccess(OltDevice oltDevice) {
        return post(urlPathMatching(OLT_BASIC_CONFIGURATION_TASK_URL))
                .atPriority(4)
                .withName("postOltBasicConfiguration202CallbackSuccess")
                .willReturn(aDefaultResponseWithBody("", 202))
                .withRequestBody(matchingJsonPath("$.oltName", containing(String.format("%s", oltDevice.getEndsz().replace("/", "_")))))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new SealMapper().getCallbackV1OltOltBasicConfigurationTaskRequestSuccess())));

    }

    public MappingBuilder postOltBasicConfiguration202CallbackError(OltDevice oltDevice, boolean netconf) {
        return post(urlPathMatching(OLT_BASIC_CONFIGURATION_TASK_URL))
                .withName("postOltBasicConfiguration202CallbackError")
                .willReturn(aDefaultResponseWithBody("", 202))
                .withRequestBody(matchingJsonPath("$.oltName", containing(String.format("%s", oltDevice.getEndsz().replace("/", "_")))))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new SealMapper().getCallbackV1OltOltBasicConfigurationTaskRequestError(netconf))));
    }

    public MappingBuilder postAccessLineConfigurationCallbackError() {
        return post(urlPathMatching(ACCESS_LINE_CONFIGURATION_URL))
                .withName("postAccessLineConfigurationCallbackError")
                .willReturn(aDefaultResponseWithBody(null, 202))
                .withPostServeAction(WebhookPostServeAction.NAME, aDefaultWebhookWithBody(serialize(new SealMapper().getCallbackV1CreateAccesslineRequest())))
                .atPriority(0);
    }

    private String serialize(Object obj) {
        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }
}
