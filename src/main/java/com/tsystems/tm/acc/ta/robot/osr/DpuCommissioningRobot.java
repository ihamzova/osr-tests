package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.data.models.OltDevice;
import com.tsystems.tm.acc.ta.api.osr.DpuCommissioningClient;
import com.tsystems.tm.acc.ta.data.osr.generators.DpuCommissioningGenerator;
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

    public static final Integer HTTP_CODE_CREATED_201 = 201;
    private DpuCommissioningClient dpuCommissioningClient;
    private DpuCommissioningGenerator dpuCommissioningGenerator;

    @Step("Start dpuCommissioning")
    public void startProcess(String endsz) {
        dpuCommissioningClient = new DpuCommissioningClient();
        StartDpuCommissioningRequest dpuCommissioningRequest = new StartDpuCommissioningRequest();
        dpuCommissioningRequest.setEndSZ(endsz);

        DpuCommissioningResponse response = dpuCommissioningClient.getClient().dpuCommissioning().startDpuDeviceCommissioning()
                .body(dpuCommissioningRequest)
                .xB3ParentSpanIdHeader("1")
                .xB3TraceIdHeader("2")
                .xBusinessContextHeader("3")
                .xB3SpanIdHeader("4")
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    @Step("setUp wiremock for TeamLevel Test")
        public void setUpWiremock(OltDevice oltDevice, Dpu dpu){
        dpuCommissioningGenerator = new DpuCommissioningGenerator();
        //TODO in current implementation template is override by each generation
        File getDpuDeviceMock = dpuCommissioningGenerator.generateGetDpuDeviceStub(dpu);
        //TODO this mock return oltPonPortEndsz which (should) already set in DefaultOlt.yml
        File dpuPonConn = dpuCommissioningGenerator.generateGetDpuPonConnStub(dpu);
        File getEthLink = dpuCommissioningGenerator.generateGetEthLinkStub(oltDevice);
        File getOnu = dpuCommissioningGenerator.generateGetOnuIdStub(dpu);
        File getBackhaul = dpuCommissioningGenerator.generateGetBackhaulIdStub(oltDevice);
        WiremockRobot wiremockRobot = new WiremockRobot();
        //TODO how to load several files to Wiremock ?
        wiremockRobot.initializeWiremock(getDpuDeviceMock);
    }

    @Step("verfify DPU")
    public void verifyDpu(){}

}