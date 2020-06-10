package com.tsystems.tm.acc.ta.data.osr.generators;


import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.tsystems.tm.acc.WebhookDefinitionModel;
import com.tsystems.tm.acc.data.models.OltDevice;
import com.tsystems.tm.acc.ta.data.osr.models.DpuActivities;
import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import com.tsystems.tm.acc.ta.data.osr.models.DpuCommissioningCallbackErrors;
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
        content = setResponseStatus(dpu, content, currentStep, DpuCommissioningCallbackErrors.DEPROVISIONING_OLT_ERROR, isAsyncScenario);

        File stub = new File (generatedStubFolder + "wiremock_POST_deprovisioning_port.json");
        writeStubToFolder(content, stub);
        return stub;
    }

    public File generatePostAncpConfStub(OltDevice olt, Dpu dpu, boolean isAsyncScenario){
        File jsonTemplate = new File(stubTemplateFolder + "wiremock_POST_configureANCP.json");

        String content = getTemplateContent(jsonTemplate);
        content = content.replace("###ENDSZ###",dpu.getEndSz());
        String currentStep = DpuActivities.SET_ANCP;
        content = setResponseStatus(dpu, content, currentStep, DpuCommissioningCallbackErrors.CONFIGURE_ANCP_ERROR, isAsyncScenario);

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

    public File generatePutDpuAtOltConfigStub(Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "wiremock_PUT_dpuAtOltConfiguration.json");
        String content = getTemplateContent(jsonTemplate);
        content = content.replace("###ENDSZ###",dpu.getEndSz());

        String currentStep = DpuActivities.UPDATE_INV;
        content = setResponseStatus(dpu, content, currentStep);

        File stub = new File (generatedStubFolder + "wiremock_PUT_dpuAtOltConfiguration.json");
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

    public File generateDpuConfigurationTaskStub(Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "wiremock_POST_Seal_dpuAtOltConfiguration.json");
        String content = getTemplateContent(jsonTemplate);
        content = content.replace("###ENDSZ###",dpu.getEndSz().replace("/","_"));

        String currentStep = DpuActivities.CONFIGURE_DPU_SEAL;
        content = setResponseStatus(dpu, content, currentStep);

        File stub = new File (generatedStubFolder + "wiremock_POST_Seal_dpuAtOltConfiguration.json");
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
        //TODO durty hack. Refactor. take status to yaml.
        if (currentStep.equals(DpuActivities.CONFIGURE_DPU_SEAL))
        {
            content = content.replace("###STATUS###", "\"status\":202");
        }
        if (currentStep.equals(dpu.getStepToFall())) {
            content = content.replace("###STATUS###", "\"status\":400");
        } else {
            content = content.replace("###STATUS###", "\"status\":200");
        }
        if(currentStep.equals(dpu.getChangeBody())) {
            content = content.replace("###STATUS###", "\"status\":200");
            content = content.replace("###BODY###", "{\n" +
                    "      \"id\": 12345,\n" +
                    "      \"dpuEndsz\": \"49/0001/0/71AA\",\n" +
                    "      \"backhaulId\": \"blackhole\",\n" +
                    "      \"onuId\": 12345,\n" +
                    "      \"configurationState\": \"ACTIVE\",\n" +
                    "      \"serialNumber\": \"111\",\n" +
                    "      \"oltEndsz\": \"49/40/179/76H1\",\n" +
                    "      \"oltPonSlot\": \"5\",\n" +
                    "      \"oltPonPort\": \"5\",\n" +
                    "      \"oltUplinkSlot\": \"5\",\n" +
                    "      \"oltUplinkPort\": \"5\"\n" +
                    "    }");
        }else {
            content = content.replace("###STATUS###", "\"status\":200");
            content = content.replace("###BODY###", "");
        }
        return content;
    }

    /**
     * overload method to handle callback error
     */
    private String setResponseStatus(Dpu dpu, String content, String currentStep, String errorMessage, boolean isAsyncScenario) {
        if(!currentStep.equals(dpu.getStepToFall())){
            content = content.replace("###STATUS###", "\"status\":200");
            content = content.replace("###CALLBACK_START###","");
            content = content.replace("###CALLBACK_END###","");
        }else if(currentStep.equals(dpu.getStepToFall())&&!isAsyncScenario){
            content = content.replace("###STATUS###", "\"status\":400");
            content = replaceCallback(content, errorMessage,isAsyncScenario);
        }else if(currentStep.equals(dpu.getStepToFall())&&isAsyncScenario){
            content = content.replace("###STATUS###", "\"status\":200");
            content = replaceCallback(content, errorMessage, isAsyncScenario);
        }
        return content;
    }

    private String replaceCallback(String content, String errorMessage, boolean isAsyncScenario){
        if(!isAsyncScenario) {
            content = content.replaceAll("(?s)###CALLBACK_START###(.*)###CALLBACK_END###", "{}");
        }else {
            String  errorCallback = generatePostServeActionForStub(errorMessage);
            content = content.replaceAll("(?s)###CALLBACK_START###(.*)###CALLBACK_END###", errorCallback);
        }
        return content;
    }

    private String generatePostServeActionForStub(String errorMessage){
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
                new Body(errorMessage),
                3000,
                null);
        mapping.setPostServeActions(Collections.singletonMap("webhook", model));
        return doGenerate(mapping);

    }
    private String doGenerate(StubMapping stub)
    {
        WiremockMappingGenerator generator = new WiremockMappingGenerator();
        List<StubMapping> stubMappings = new ArrayList<>();
        stubMappings.add(stub);
        File storeToFolder = new File (generatedStubFolder);
        generator.generate(stubMappings, Paths.get(storeToFolder.toURI()));

        File[] files = storeToFolder.listFiles();
        if (files != null) {
            for(File file : files) {
                if (file.getName().startsWith("stub_"))
                {
                    String callback = getTemplateContent(file);
                    callback = callback.replace("\"postServeActions\": {", "");
                    callback = callback.substring(0, callback.length() - 1);
                    file.delete();
                    return callback;
                }
            }
        }
        return null;
    }
}
