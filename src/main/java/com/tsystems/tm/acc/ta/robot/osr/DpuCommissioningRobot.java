package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.data.models.OltDevice;
import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import com.tsystems.tm.acc.tests.osr.dpu.commissioning.model.DpuCommissioningResponse;
import com.tsystems.tm.acc.tests.osr.dpu.commissioning.model.StartDpuCommissioningRequest;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
@Slf4j
public class DpuCommissioningRobot {

    @Step("Start dpuCommissioning")
    public void startProcess() {

//        StartDpuCommissioningRequest dpuCommissioningRequest = new StartDpuCommissioningRequest();
//        dpuCommissioningRequest.setEndSZ(ENDSZ_WITHOUT_ERRORS);
//
//        DpuCommissioningResponse response = dpuCommissioningClient.getClient().dpuCommissioning().startDpuDeviceCommissioning()
//                .body(dpuCommissioningRequest)
//                .xB3ParentSpanIdHeader("1")
//                .xB3TraceIdHeader("2")
//                .xBusinessContextHeader("3")
//                .xB3SpanIdHeader("4")
//                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
//    }
//}

    }
    @Step("setUp wiremock for TeamLevel Test")
public void setUpWiremock(OltDevice oltDevice, Dpu dpu) throws IOException {

        File jsonTemplate = new File(System.getProperty("user.dir") + "/resources/team/morpheus/wiremockTemplates/wiremock_oltResourceInventory_v1_device.json");
        String content = null;
        try {
            content = FileUtils.readFileToString(jsonTemplate, "UTF-8");
        } catch (IOException e) {
            log.error("could not parse json template");
            throw new RuntimeException();
        }
        content = content.replace("###ENDSZ###", dpu.getEndSz());
        File stub = new File (System.getProperty("user.dir") + "/resources/team/morpheus/wiremockTemplates/wiremock_oltResourceInventory_v1_device.json");
        FileUtils.write(stub,content, StandardCharsets.UTF_8);
        WiremockRobot wiremockRobot = new WiremockRobot();
        //wiremockRobot.initializeWiremock(stub);
    }

    @Step("verfify DPU")
    public void verifyDpu(){}

}