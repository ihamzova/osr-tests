package com.tsystems.tm.acc.ta.data.osr.generators;

import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.tsystems.tm.acc.WebhookDefinitionModel;
import com.tsystems.tm.acc.data.models.OltDevice;
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
import java.util.*;

@Slf4j
public class DpuCommissioningGenerator {
    //TODO already definde in Activities.java
    //steps in bpmn model
    private static final String GET_DPU = "Activity_OLT-RI.GET.DeviceDPU";
    public static final String GET_LLC = "Activity_OLT-RI.GET.DpuPonConn";
    public static final String GET_ETHLINK = "Activity_OLT-RI.GET.EthernetLink";
    public static final String GET_ONUID = "Activity_AL-RI.GET.OnuId";
    public static final String GET_BACKHAUL = "Activity_AL-RI.POST.Backhaul-id";
    public static final String DEPROVISION_OLT = "Activity_DeprovisionOltPort";
    public static final String GET_ANCP = "Activity_OLT-RI.GET.AncpSession";

    public File generateGetDpuDeviceStub(Dpu dpu){
        File jsonTemplate = new File(System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockTemplates/wiremock_GET_oltResourceInventory_v1_device.json");

        String content = null;
        try {
            content = FileUtils.readFileToString(jsonTemplate, "UTF-8");
        } catch (IOException e) {
            log.error("could not parse json template");
            throw new RuntimeException();
        }
        content = content.replace("###ENDSZ###", dpu.getEndSz());
        if(GET_DPU.equals(dpu.getStepToFall()))
        {
            content = content.replace("###STATUS###","\"status\":400");
        }
        else {
            content = content.replace("###STATUS###", "\"status\":200");
        }
        File stub = new File (System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockResult/wiremock_GET_oltResourceInventory_v1_device.json");
        try {
            FileUtils.write(stub,content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("could not write into template");
            throw new RuntimeException();
        }
        return stub;
    }

    public File generateGetDpuPonConnStub(Dpu dpu){
        File jsonTemplate = new File(System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockTemplates/wiremock_GET_dpuPonConnection.json");

        String content = null;
        try {
            content = FileUtils.readFileToString(jsonTemplate, "UTF-8");
        } catch (IOException e) {
            log.error("could not parse json template");
            throw new RuntimeException();
        }
        content = content.replace("###ENDSZ###", dpu.getEndSz());

        if(GET_LLC.equals(dpu.getStepToFall()))
        {
            content = content.replace("###STATUS###","\"status\":400");
        }
        else {
            content = content.replace("###STATUS###", "\"status\":200");
        }

        File stub = new File (System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockResult/wiremock_GET_dpuPonConnection.json");
        try {
            FileUtils.write(stub,content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("could not write into template");
            throw new RuntimeException();
        }
        return stub;
    }

    public File generateGetEthLinkStub(OltDevice olt,Dpu dpu){
        File jsonTemplate = new File(System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockTemplates/wiremock_GET_findEthernetLinksByEndsz.json");

        String content = null;
        try {
            content = FileUtils.readFileToString(jsonTemplate, "UTF-8");
        } catch (IOException e) {
            log.error("could not parse json template");
            throw new RuntimeException();
        }
        String endsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        content = content.replace("###OLT_ENDSZ###",endsz);

        if(GET_ETHLINK.equals(dpu.getStepToFall()))
        {
            content = content.replace("###STATUS###","\"status\":400");
        }
        else {
            content = content.replace("###STATUS###", "\"status\":200");
        }

        File stub = new File (System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockResult/wiremock_GET_findEthernetLinksByEndsz.json");
        try {
            FileUtils.write(stub,content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("could not write into template");
            throw new RuntimeException();
        }
        return stub;
    }

    public File generateGetOnuIdStub(Dpu dpu){
        File jsonTemplate = new File(System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockTemplates/wiremock_POST_assignOnuIdTask.json");

        String content = null;
        try {
            content = FileUtils.readFileToString(jsonTemplate, "UTF-8");
        } catch (IOException e) {
            log.error("could not parse json template");
            throw new RuntimeException();
        }
        content = content.replace("###ENDSZ###",dpu.getEndSz());

        if(GET_ONUID.equals(dpu.getStepToFall()))
        {
            content = content.replace("###STATUS###","\"status\":400");
        }
        else {
            content = content.replace("###STATUS###", "\"status\":200");
        }

        File stub = new File (System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockResult/wiremock_POST_assignOnuIdTask.json");
        try {
            FileUtils.write(stub,content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("could not write into template");
            throw new RuntimeException();
        }
        return stub;
    }

    public File generateGetBackhaulIdStub(OltDevice olt, Dpu dpu){
        File jsonTemplate = new File(System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockTemplates/wiremock_POST_backhaulid_search.json");

        String content = null;
        try {
            content = FileUtils.readFileToString(jsonTemplate, "UTF-8");
        } catch (IOException e) {
            log.error("could not parse json template");
            throw new RuntimeException();
        }
        String endsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        content = content.replace("###OLT_ENDSZ###",endsz);

        if(GET_BACKHAUL.equals(dpu.getStepToFall()))
        {
            content = content.replace("###STATUS###","\"status\":400");
        }
        else {
            content = content.replace("###STATUS###", "\"status\":200");
        }

        File stub = new File (System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockResult/wiremock_POST_backhaulid_search.json");
        try {
            FileUtils.write(stub,content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("could not write into template");
            throw new RuntimeException();
        }
        return stub;
    }

    public File generatePostDeprovisionOltStub(OltDevice olt, Dpu dpu){
        File jsonTemplate = new File(System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockTemplates/wiremock_POST_deprovisioning_port.json");

        String content = null;
        try {
            content = FileUtils.readFileToString(jsonTemplate, "UTF-8");
        } catch (IOException e) {
            log.error("could not parse json template");
            throw new RuntimeException();
        }
        String endsz = new StringBuilder().append(olt.getVpsz()).append("/").append(olt.getFsz()).toString();
        content = content.replace("###OLT_ENDSZ###",endsz);

        if(DEPROVISION_OLT.equals(dpu.getStepToFall()))
        {
            content = content.replace("###STATUS###","\"status\":400");
        }
        else {
            content = content.replace("###STATUS###", "\"status\":200");
        }

        File stub = new File (System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockResult/wiremock_POST_deprovisioning_port.json");
        try {
            FileUtils.write(stub,content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("could not write into template");
            throw new RuntimeException();
        }
        return stub;
    }

    public File generateGetAncpStub(OltDevice olt, Dpu dpu){
        File jsonTemplate = new File(System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockTemplates/wiremock_GET_ancpsession.json");

        String content = null;
        try {
            content = FileUtils.readFileToString(jsonTemplate, "UTF-8");
        } catch (IOException e) {
            log.error("could not parse json template");
            throw new RuntimeException();
        }
        content = content.replace("###ENDSZ###",dpu.getEndSz());

        if(GET_ANCP.equals(dpu.getStepToFall()))
        {
            content = content.replace("###STATUS###","\"status\":400");
        }
        else {
            content = content.replace("###STATUS###", "\"status\":200");
        }

        File stub = new File (System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockResult/wiremock_GET_ancpsession.json");
        try {
            FileUtils.write(stub,content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("could not write into template");
            throw new RuntimeException();
        }
        return stub;
    }


    public File generateSelaDpuStub(OltDevice olt, Dpu dpu){
        File jsonTemplate = new File(System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockTemplates/wiremock_seal_dpu_configuration.json");

        String content = null;
        try {
            content = FileUtils.readFileToString(jsonTemplate, "UTF-8");
        } catch (IOException e) {
            log.error("could not parse json template");
            throw new RuntimeException();
        }

        File stub = new File (System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockResult/wiremock_seal_dpu_configuration.json");
        try {
            FileUtils.write(stub,content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("could not write into template");
            throw new RuntimeException();
        }
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
}
