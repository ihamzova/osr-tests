package com.tsystems.tm.acc.ta.data.osr.generators;

import com.tsystems.tm.acc.data.models.stable.OltDevice;

import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.tsystems.tm.acc.WebhookDefinitionModel;

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

    public void generateGetDpuDeviceStub(Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "1_OLT_RI_GET_DeviceDPU.json");

        String content = getTemplateContent(jsonTemplate);
        content = content.replace("###ENDSZ###", dpu.getEndSz());

        String currentStep = DpuActivities.GET_DPU;
        content = setResponseStatus(dpu, content, currentStep);

        File stub = new File (generatedStubFolder + "1_OLT_RI_GET_DeviceDPU.json");
        writeStubToFolder(content, stub);
    }

    public void generateGetDpuPonConnStub(OltDevice olt, Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "2_OLT_RI_GET_DpuPonConn.json");

        String content = getTemplateContent(jsonTemplate);
        content = content.replace("###ENDSZ###", dpu.getEndSz());
        String endsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        content = content.replace("###OLT_ENDSZ###",endsz);
        content = content.replace("###OLTSLOT###", olt.getOltSlot());
        content = content.replace("###OLTPORT###", olt.getOltPort());

        String currentStep = DpuActivities.GET_LLC;
        content = setResponseStatus(dpu, content, currentStep);

        File stub = new File (generatedStubFolder + "2_OLT_RI_GET_DpuPonConn.json");
        writeStubToFolder(content, stub);
    }

    public void generateGetEthLinkStub(OltDevice olt,Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "3_OLT_RI_GET_EthernetLink.json");

        String content = getTemplateContent(jsonTemplate);

        String endsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        content = content.replace("###OLT_ENDSZ###",endsz);
        content = content.replace("###OLTSLOT###", olt.getOltSlot());
        content = content.replace("###OLTPORT###", olt.getOltPort());

        String currentStep = DpuActivities.GET_ETHLINK;
        content = setResponseStatus(dpu, content, currentStep);

        File stub = new File (generatedStubFolder + "3_OLT_RI_GET_EthernetLink.json");
        writeStubToFolder(content, stub);
    }

    public void generateGetOnuIdStub(Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "4_AL_RI_POST_OnuId.json");

        String content = getTemplateContent(jsonTemplate);
        content = content.replace("###ENDSZ###",dpu.getEndSz());

        String currentStep = DpuActivities.GET_ONUID;
        content = setResponseStatus(dpu, content, currentStep);

        File stub = new File (generatedStubFolder + "4_AL_RI_POST_OnuId.json");
        writeStubToFolder(content, stub);
    }

    public void generateGetBackhaulIdStub(OltDevice olt, Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "5_AL_RI_POST_Backhaul_id.json");

        String content = getTemplateContent(jsonTemplate);
        String endsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        content = content.replace("###OLT_ENDSZ###",endsz);

        String currentStep = DpuActivities.GET_BACKHAUL;
        content = setResponseStatus(dpu, content, currentStep);

        File stub = new File (generatedStubFolder + "5_AL_RI_POST_Backhaul_id.json");
        writeStubToFolder(content, stub);
    }

    public void generatePostDeprovisionOltStub(OltDevice olt, Dpu dpu, boolean isAsyncScenario){
        File jsonTemplate = new File(stubTemplateFolder + "6_Wg_FTTH_AP_POST_DeprovisionOltPort.json");

        String content = getTemplateContent(jsonTemplate);
        String endsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        content = content.replace("###OLT_ENDSZ###",endsz);

        String currentStep = DpuActivities.DEPROVISION_OLT;
        content = setResponseStatus(dpu, content, currentStep, DpuCommissioningCallbackErrors.DEPROVISIONING_OLT_ERROR, isAsyncScenario);

        File stub = new File (generatedStubFolder + "6_Wg_FTTH_AP_POST_DeprovisionOltPort.json");
        writeStubToFolder(content, stub);
    }

    public void generatePostAncpConfStub(Dpu dpu, boolean isAsyncScenario){
        File jsonTemplate = new File(stubTemplateFolder + "7_Ancp_Conf_POST_AncpConf.json");

        String content = getTemplateContent(jsonTemplate);
        content = content.replace("###ENDSZ###",dpu.getEndSz());
        String currentStep = DpuActivities.SET_ANCP;
        content = setResponseStatus(dpu, content, currentStep, DpuCommissioningCallbackErrors.CONFIGURE_ANCP_ERROR, isAsyncScenario);

        File stub = new File (generatedStubFolder + "7_Ancp_Conf_POST_AncpConf.json");
        writeStubToFolder(content, stub);
    }

    public void generateGetDPUAncpStub(Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "8_OLT_RI_GET_DPUAncpSession.json");

        String content = getTemplateContent(jsonTemplate);

        content = content.replace("###ENDSZ###",dpu.getEndSz());

        String currentStep = DpuActivities.GET_ANCP;
        content = setResponseStatus(dpu, content, currentStep);

        File stub = new File (generatedStubFolder + "8_OLT_RI_GET_DPUAncpSession.json");
        writeStubToFolder(content, stub);
    }

    public void generateGetOLTAncpStub(OltDevice olt,Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "8_OLT_RI_GET_OLTAncpSession.json");

        String content = getTemplateContent(jsonTemplate);
        String endsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        content = content.replace("###OLT_ENDSZ###",endsz);

        String currentStep = DpuActivities.GET_ANCP;
        content = setResponseStatus(dpu, content, currentStep);

        File stub = new File (generatedStubFolder + "8_OLT_RI_GET_OLTAncpSession.json");
        writeStubToFolder(content, stub);
    }


    public void generateGetDpuAtOltConfigStub(Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "9_OLT_RI_POST_DpuAtOltConf_GET.json");
        String content = getTemplateContent(jsonTemplate);
        content = content.replace("###ENDSZ###",dpu.getEndSz());

        String currentStep = DpuActivities.GET_DPUOLT;
        content = setResponseStatus(dpu, content, currentStep, "{\n" +
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

        File stub = new File (generatedStubFolder + "9_OLT_RI_POST_DpuAtOltConf_GET.json");
        writeStubToFolder(content, stub);
    }

    public void generatePostDpuAtOltConfigStub(Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "9_OLT_RI_POST_DpuAtOltConf_POST.json");
        String content = getTemplateContent(jsonTemplate);
        content = content.replace("###ENDSZ###",dpu.getEndSz());

        String currentStep = DpuActivities.CREATE_DPUOLT;
        content = setResponseStatus(dpu, content, currentStep);

        File stub = new File (generatedStubFolder + "9_OLT_RI_POST_DpuAtOltConf_POST.json");
        writeStubToFolder(content, stub);
    }

    public void generateDpuConfigurationTaskStub(Dpu dpu, boolean isAsyncScenario){
        File jsonTemplate = new File(stubTemplateFolder + "10_SEAL_POST_DpuAtOltConf_OLT.json");
        String content = getTemplateContent(jsonTemplate);
        content = content.replace("###ENDSZ###",dpu.getEndSz().replace("/","_"));

        String currentStep = DpuActivities.CONFIGURE_DPU_SEAL;
        content = setResponseStatus(dpu, content, currentStep, DpuCommissioningCallbackErrors.SEAL_DPU_AT_OLT, isAsyncScenario);

        File stub = new File (generatedStubFolder + "10_SEAL_POST_DpuAtOltConf_OLT.json");
        writeStubToFolder(content, stub);
    }

    public void generatePutDpuAtOltConfigStub(Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "11_OLT_RI_PUT_DpuAtOltConf.json");
        String content = getTemplateContent(jsonTemplate);
        content = content.replace("###ENDSZ###",dpu.getEndSz());

        String currentStep = DpuActivities.UPDATE_INV;
        content = setResponseStatus(dpu, content, currentStep);

        File stub = new File (generatedStubFolder + "11_OLT_RI_PUT_DpuAtOltConf.json");
        writeStubToFolder(content, stub);
    }

    public void generateGetDpuEmsConfigStub(Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "12_OLT_RI_POST_DpuEmsConf_GET.json");
        String content = getTemplateContent(jsonTemplate);
        content = content.replace("###ENDSZ###",dpu.getEndSz());

        String currentStep = DpuActivities.CREATE_DPUEMS_CONF;
        content = setResponseStatus(dpu, content, currentStep, "{\n" +
                "    \"id\": 12345,\n" +
                "    \"ancpBngIpAddress\": \"string\",\n" +
                "    \"ancpIpAddressSubnetMask\": \"string\",\n" +
                "    \"ancpOwnIpAddress\": \"string\",\n" +
                "    \"backhaulId\": \"string\",\n" +
                "    \"configurationState\": \"ACTIVE\",\n" +
                "    \"emsNbiName\": \"string\",\n" +
                "    \"dpuEndsz\": \"49/0002/0/71AA\",\n" +
                "    \"managementDomain\": \"string\",\n" +
                "    \"serialNumber\": \"12345\"\n" +
                "  }" );

        File stub = new File (generatedStubFolder + "12_OLT_RI_POST_DpuEmsConf_GET.json");
        writeStubToFolder(content, stub);
    }

    public void generatePostDpuEmsConfigStub(Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "12_OLT_RI_POST_DpuEmsConf_POST.json");
        String content = getTemplateContent(jsonTemplate);
        content = content.replace("###ENDSZ###",dpu.getEndSz());

        String currentStep = DpuActivities.CREATE_DPUOLT;
        content = setResponseStatus(dpu, content, currentStep);

        File stub = new File (generatedStubFolder + "12_OLT_RI_POST_DpuEmsConf_POST.json");
        writeStubToFolder(content, stub);
    }

    public void generateSealPostDpuConfStub(Dpu dpu, boolean isAsyncScenario){
        File jsonTemplate = new File(stubTemplateFolder + "13_SEAL_POST_DpuAtOltConf_DPU.json");
        String content = getTemplateContent(jsonTemplate);
        content = content.replace("###ENDSZ###",dpu.getEndSz());

        String currentStep = DpuActivities.CONFIGURE_DPUEMS_SEAL;
        content = setResponseStatus(dpu, content, currentStep, DpuCommissioningCallbackErrors.SEAL_DPU_AT_OLT, isAsyncScenario);

        File stub = new File (generatedStubFolder + "13_SEAL_POST_DpuAtOltConf_DPU.json");
        writeStubToFolder(content, stub);
    }

    public void generatePutDpuEmsConfigStub(Dpu dpu){
        File jsonTemplate = new File(stubTemplateFolder + "14_OLT_RI_PUT_DpuEmsConf.json");
        String content = getTemplateContent(jsonTemplate);
        content = content.replace("###ENDSZ###",dpu.getEndSz());

        String currentStep = DpuActivities.SET_DPUEMS_CONF;
        content = setResponseStatus(dpu, content, currentStep);

        File stub = new File (generatedStubFolder + "14_OLT_RI_PUT_DpuEmsConf.json");
        writeStubToFolder(content, stub);
    }

    public void generatePostProvisioningDeviceStub(Dpu dpu, boolean isAsyncScenario){
        File jsonTemplate = new File(stubTemplateFolder + "15_Wg_FTTB_AP_POST_ProvisioningDevice.json");
        String content = getTemplateContent(jsonTemplate);
        content = content.replace("###ENDSZ###",dpu.getEndSz());

        String currentStep = DpuActivities.PROVISIONING_DEVICE;
        content = setResponseStatus(dpu, content, currentStep, DpuCommissioningCallbackErrors.PROVISIONING_DEVICE, isAsyncScenario);

        File stub = new File (generatedStubFolder + "15_Wg_FTTB_AP_POST_ProvisioningDevice.json");
        writeStubToFolder(content, stub);
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


    private String setResponseStatus(Dpu dpu, String content, String currentStep){
        String body = "{\n" +
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
                "    }";
        return setResponseStatus(dpu, content,currentStep, body);
    }



    /**
     * when defined in testdata.yml "getStepToFall" this call should return a 400 error
     * when defined in testdata.yml "getChangeBody" should replace body with body
     */
    private String setResponseStatus(Dpu dpu, String content, String currentStep, String body){
        //TODO durty hack. Refactor. take status to yaml.
        if (DpuActivities.STEPS_WITH_202_CODE.contains(currentStep))
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
            content = content.replace("###BODY###", body);
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
        }else if (DpuActivities.STEPS_WITH_202_CODE.contains(currentStep)&&isAsyncScenario){
            //TODO durty hack. Refactor. take status to yaml.
            content = content.replace("###STATUS###", "\"status\":202");
            content = replaceCallback(content, errorMessage, isAsyncScenario);
        }
        else if(currentStep.equals(dpu.getStepToFall())&&isAsyncScenario){
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
                1000,
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
