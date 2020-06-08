package com.tsystems.tm.acc.ta.data.osr.generators;


import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.tsystems.tm.acc.WebhookDefinitionModel;
import com.tsystems.tm.acc.data.models.OltDevice;
import com.tsystems.tm.acc.ta.data.osr.models.DpuActivities;
import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import com.tsystems.tm.acc.ta.generators.WiremockMappingGenerator;
import com.tsystems.tm.acc.tests.osr.dpu.commissioning.invoker.JSON;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMapping;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public File generateGetDpuPonConnStub(OltDevice olt, Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "wiremock_GET_dpuPonConnection.json");

        String content = getTemplateContent(jsonTemplate);
        content = content.replace("###ENDSZ###", dpu.getEndSz());
        String endsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        content = content.replace("###OLT_ENDSZ###",endsz);
        content = content.replace("###OLTSLOT###", olt.getOltSlot());
        content = content.replace("###OLTPORT###", olt.getOltPort());

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

    public File generatePostDeprovisionOltStub(OltDevice olt, Dpu dpu, boolean isAsyncScenario){
        File jsonTemplate = new File(stubTemplateFolder + "wiremock_POST_deprovisioning_port.json");

        String content = getTemplateContent(jsonTemplate);
        String endsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        content = content.replace("###OLT_ENDSZ###",endsz);

        String currentStep = DpuActivities.DEPROVISION_OLT;
        content = setResponseStatus(dpu, content, currentStep, isAsyncScenario);

        File stub = new File (generatedStubFolder + "wiremock_POST_deprovisioning_port.json");
        writeStubToFolder(content, stub);
        return stub;
    }

    public File generatePostAncpConfStub(OltDevice olt, Dpu dpu, boolean isAsyncScenario){
        File jsonTemplate = new File(stubTemplateFolder + "wiremock_POST_configureANCP.json");

        String content = getTemplateContent(jsonTemplate);
        content = content.replace("###ENDSZ###",dpu.getEndSz());
        String currentStep = DpuActivities.SET_ANCP;
        content = setResponseStatus(dpu, content, currentStep, isAsyncScenario);

        File stub = new File (generatedStubFolder + "wiremock_POST_configureANCP.json");
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

    public File generateGetDpuAtOltConfigStub(Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "wiremock_GET_dpuAtOltConfiguration.json");
        String content = getTemplateContent(jsonTemplate);
        content = content.replace("###ENDSZ###",dpu.getEndSz());

        String currentStep = DpuActivities.GET_DPUOLT;
        content = setResponseStatus(dpu, content, currentStep);

        File stub = new File (generatedStubFolder + "wiremock_GET_dpuAtOltConfiguration.json");
        writeStubToFolder(content, stub);
        return stub;
    }

    public File generatePostDpuAtOltConfigStub(Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "wiremock_POST_dpuAtOltConfiguration.json");
        String content = getTemplateContent(jsonTemplate);
        content = content.replace("###ENDSZ###",dpu.getEndSz());

        String currentStep = DpuActivities.CREATE_DPUOLT;
        content = setResponseStatus(dpu, content, currentStep);

        File stub = new File (generatedStubFolder + "wiremock_POST_dpuAtOltConfiguration.json");
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

    /**
     * overload method to handle callback error
     */
    private String setResponseStatus(Dpu dpu, String content, String currentStep, boolean isAsyncScenario) {
        if(!currentStep.equals(dpu.getStepToFall())){
            content = content.replace("###STATUS###", "\"status\":200");
            content = content.replace("###CALLBACK_START###","");
            content = content.replace("###CALLBACK_END###","");
        }else if(currentStep.equals(dpu.getStepToFall())&&!isAsyncScenario){
            content = content.replace("###STATUS###", "\"status\":400");
            content = replaceCallback(content);
        }
        return content;
    }

    private String replaceCallback(String content){
        content = content.replaceAll("(?s)###CALLBACK_START###(.*)###CALLBACK_END###","{}");
        return content;
    }

    public StubMapping generatePostServeActionForStub(){
        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().setPrettyPrinting().serializeNulls().create());

        StubMapping mapping = new StubMapping();
        List<HttpHeader> webhookHeaders = new ArrayList<>(4);
        webhookHeaders.add(new HttpHeader("X-Callback-Correlation-Id", "{{request.headers.X-Callback-Correlation-Id}}"));
        webhookHeaders.add(new HttpHeader("X-B3-TraceId", "{{request.headers.X-B3-TraceId}}"));
        webhookHeaders.add(new HttpHeader("X-B3-SpanId", "{{request.headers.X-B3-SpanId}}"));
        webhookHeaders.add(new HttpHeader("Content-Type", "application/json"));


        WebhookDefinitionModel model = new WebhookDefinitionModel(RequestMethod.POST,
                "{{request.headers.X-Callback-Url}}",
                webhookHeaders,
                new Body("{ \"response\": {}, \"success\": false }"),
                3000,
                null);
        mapping.setPostServeActions(Collections.singletonMap("webhook", model));

        doGenerate(mapping);

        return mapping;
    }
    private void doGenerate(StubMapping stub)
    {

        WiremockMappingGenerator generator = new WiremockMappingGenerator();
        List<StubMapping> stubMappings = new ArrayList<>();
        stubMappings.add(stub);
        File storeToFolder = new File (generatedStubFolder + "async.json");
        generator.generate(stubMappings, Paths.get(storeToFolder.toURI()));
    }

}
