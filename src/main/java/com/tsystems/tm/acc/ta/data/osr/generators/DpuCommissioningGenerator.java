package com.tsystems.tm.acc.ta.data.osr.generators;

import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.tsystems.tm.acc.data.models.OltDevice;
import com.tsystems.tm.acc.ta.data.osr.models.DpuActivities;
import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import com.tsystems.tm.acc.tests.osr.seal.client.invoker.JSON;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMapping;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMappingRequest;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMappingResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This Mock Generator is only used on team level by now
 * so there is no need to be more flexible here at the moment
 */
@Slf4j
public class DpuCommissioningGenerator {

    private String generatedStubFolder = System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockResult/";
    private String stubTemplateFolder = System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockTemplates/";

    public File generateGetDpuDeviceStub(Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "wiremock_GET_oltResourceInventory_v1_device.json");

        String content = getTemplateContent(jsonTemplate);
        content = content.replace("###ENDSZ###", dpu.getEndSz());

        String currentStep = DpuActivities.GET_DPU;
        content = setResponseStatus(dpu, content, currentStep);

        File stub = new File (generatedStubFolder + "wiremock_GET_oltResourceInventory_v1_device.json");
        writeStubToFolder(content, stub);
        return stub;
    }

    public File generateGetDpuPonConnStub(Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "wiremock_GET_dpuPonConnection.json");

        String content = getTemplateContent(jsonTemplate);
        content = content.replace("###ENDSZ###", dpu.getEndSz());

        String currentStep = DpuActivities.GET_LLC;
        content = setResponseStatus(dpu, content, currentStep);

        File stub = new File (System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockResult/wiremock_GET_dpuPonConnection.json");
        writeStubToFolder(content, stub);
        return stub;
    }

    public File generateGetEthLinkStub(OltDevice olt,Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "wiremock_GET_findEthernetLinksByEndsz.json");

        String content = getTemplateContent(jsonTemplate);

        String endsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        content = content.replace("###OLT_ENDSZ###",endsz);
        content = content.replace("###OLTSLOT###", olt.getOltSlot());
        content = content.replace("###OLTPORT###", olt.getOltPort());

        String currentStep = DpuActivities.GET_ETHLINK;
        content = setResponseStatus(dpu, content, currentStep);

        File stub = new File (generatedStubFolder + "wiremock_GET_findEthernetLinksByEndsz.json");
        writeStubToFolder(content, stub);
        return stub;
    }

    public File generateGetOnuIdStub(Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "wiremock_POST_assignOnuIdTask.json");

        String content = getTemplateContent(jsonTemplate);
        content = content.replace("###ENDSZ###",dpu.getEndSz());

        String currentStep = DpuActivities.GET_ONUID;
        content = setResponseStatus(dpu, content, currentStep);

        File stub = new File (generatedStubFolder + "wiremock_POST_assignOnuIdTask.json");
        writeStubToFolder(content, stub);
        return stub;
    }

    public File generateGetBackhaulIdStub(OltDevice olt, Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "wiremock_POST_backhaulid_search.json");

        String content = getTemplateContent(jsonTemplate);
        String endsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        content = content.replace("###OLT_ENDSZ###",endsz);

        String currentStep = DpuActivities.GET_BACKHAUL;
        content = setResponseStatus(dpu, content, currentStep);

        File stub = new File (generatedStubFolder + "wiremock_POST_backhaulid_search.json");
        writeStubToFolder(content, stub);
        return stub;
    }

    public File generatePostDeprovisionOltStub(OltDevice olt, Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "wiremock_POST_deprovisioning_port.json");

        String content = getTemplateContent(jsonTemplate);
        String endsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        content = content.replace("###OLT_ENDSZ###",endsz);

        String currentStep = DpuActivities.DEPROVISION_OLT;
        content = setResponseStatus(dpu, content, currentStep);

        File stub = new File (generatedStubFolder + "wiremock_POST_deprovisioning_port.json");
        writeStubToFolder(content, stub);
        return stub;
    }

    public File generateGetAncpStub(OltDevice olt, Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "wiremock_GET_ancpsession.json");

        String content = getTemplateContent(jsonTemplate);
        content = content.replace("###ENDSZ###",dpu.getEndSz());

        String currentStep = DpuActivities.GET_ANCP;
        content = setResponseStatus(dpu, content, currentStep);

        File stub = new File (generatedStubFolder + "wiremock_GET_ancpsession.json");
        writeStubToFolder(content, stub);
        return stub;
    }

    public File generateSelaDpuStub(OltDevice olt, Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "wiremock_seal_dpu_configuration.json");

        String content = getTemplateContent(jsonTemplate);
        File stub = new File (generatedStubFolder + "wiremock_seal_dpu_configuration.json");
        writeStubToFolder(content, stub);
        return stub;
    }

    public StubMapping getData(OltDevice olt) {
        StubMapping mapping = new StubMapping();
        StubMappingRequest request = new StubMappingRequest();
        request.setMethod("GET");
        request.setUrlPattern("/api/oltResourceInventory/v1/device");
        request.setQueryParameters("{\n" +
                "      \"endsz\": {\n" +
                "        \"matches\": \"49/8571/0/71GA\"\n" +
                "      }");
        mapping.setRequest(request);

        StubMappingResponse response = new StubMappingResponse();
        response.setStatus(201);
        Map<String, String> respHeaders = new HashMap<>();
        respHeaders.put("Content-Type", "application/json");
        response.setHeaders(respHeaders);
        List<HttpHeader> webhookHeaders = new ArrayList<>(2);
        webhookHeaders.add(new HttpHeader("X-Callback-Correlation-Id", "{{request.headers.X-Callback-Correlation-Id}}"));
        webhookHeaders.add(new HttpHeader("Content-Type", "application/json"));

        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().setPrettyPrinting().serializeNulls().create());

//        WebhookDefinitionModel webhook = new WebhookDefinitionModel(RequestMethod.POST,
//                "{{request.headers.X-Callback-Url}}",
//                webhookHeaders,
//                new Body(json.serialize(entity)),
//                0,
//                null);
//        mapping.setPostServeActions(Collections.singletonMap("webhook", webhook));
//        mapping.setResponse(response);
//        mapping.setPriority(1);

        return mapping;
    }

    private String getTemplateContent(File jsonTemplate) {
        String content;
        try {
            content = FileUtils.readFileToString(jsonTemplate, "UTF-8");
        } catch (IOException e) {
            log.error("could not parse json template");
            throw new RuntimeException();
        }
        return content;
    }

    private void writeStubToFolder(String content, File stub) {
        try {
            FileUtils.write(stub, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("could not write into template");
            throw new RuntimeException();
        }
    }

    /**
     * when defined in testdata.yml "getStepToFall" this call should return a 400 error
     */
    private String setResponseStatus(Dpu dpu, String content, String currentStep) {
        if (currentStep.equals(dpu.getStepToFall())) {
            content = content.replace("###STATUS###", "\"status\":400");
        } else {
            content = content.replace("###STATUS###", "\"status\":200");
        }
        return content;
    }
}
