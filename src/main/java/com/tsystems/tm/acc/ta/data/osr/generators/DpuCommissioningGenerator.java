package com.tsystems.tm.acc.ta.data.osr.generators;

import com.tsystems.tm.acc.data.models.OltDevice;
import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
@Slf4j
public class DpuCommissioningGenerator {

    public File generateGetDpuDeviceStub(Dpu dpu){
        File jsonTemplate = new File(System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockTemplates/wiremock_oltResourceInventory_v1_device.json");

        String content = null;
        try {
            content = FileUtils.readFileToString(jsonTemplate, "UTF-8");
        } catch (IOException e) {
            log.error("could not parse json template");
            throw new RuntimeException();
        }
        content = content.replace("###ENDSZ###", dpu.getEndSz());
        File stub = new File (System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockTemplates/wiremock_oltResourceInventory_v1_device.json");
        try {
            FileUtils.write(stub,content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
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
        File stub = new File (System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockTemplates/wiremock_GET_dpuPonConnection.json");
        try {
            FileUtils.write(stub,content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stub;
    }

    public File generateGetEthLinkStub(OltDevice olt){
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
        File stub = new File (System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockTemplates/wiremock_GET_findEthernetLinksByEndsz.json");
        try {
            FileUtils.write(stub,content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
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
        File stub = new File (System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockTemplates/wiremock_POST_assignOnuIdTask.json");
        try {
            FileUtils.write(stub,content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stub;
    }

    public File generateGetBackhaulIdStub(OltDevice olt){
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
        File stub = new File (System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockTemplates/wiremock_POST_backhaulid_search.json");
        try {
            FileUtils.write(stub,content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stub;
    }
}
