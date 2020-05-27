package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.data.models.OltDevice;
import com.tsystems.tm.acc.ta.api.osr.DpuCommissioningClient;
import com.tsystems.tm.acc.ta.data.osr.generators.DpuCommissioningGenerator;
import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import com.tsystems.tm.acc.ta.helpers.WiremockHelper;
import com.tsystems.tm.acc.ta.robot.utils.WiremockRecordedRequestRetriver;
import com.tsystems.tm.acc.tests.osr.dpu.commissioning.model.DpuCommissioningResponse;
import com.tsystems.tm.acc.tests.osr.dpu.commissioning.model.StartDpuCommissioningRequest;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
        File getDpuDeviceMock = dpuCommissioningGenerator.generateGetDpuDeviceStub(dpu);

        dpuCommissioningGenerator.generateGetDpuPonConnStub(oltDevice, dpu);
        dpuCommissioningGenerator.generateGetEthLinkStub(oltDevice,dpu);
        dpuCommissioningGenerator.generateGetOnuIdStub(dpu);
        dpuCommissioningGenerator.generateGetBackhaulIdStub(oltDevice,dpu);
        dpuCommissioningGenerator.generatePostDeprovisionOltStub(oltDevice,dpu);
        dpuCommissioningGenerator.generatePostAncpConfStub(oltDevice,dpu);
        dpuCommissioningGenerator.generateGetAncpStub(oltDevice,dpu);
        dpuCommissioningGenerator.generateSelaDpuStub(oltDevice,dpu);
        WiremockRobot wiremockRobot = new WiremockRobot();
        wiremockRobot.initializeWiremock(new File(System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockResult"));
    }


    @Step("cleanup")
    public void cleanup(){
        //WiremockHelper.mappingsReset();
        try {
            FileUtils.cleanDirectory(new File(System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockResult"));
        } catch (IOException e) {
            log.error("directory is empty");
            throw new RuntimeException();
        }
    }

    @Step
    public void checkGetDeviceDPUCalled(Long timeOfExecution, String dpuEndsz){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertTrue(wiremockRecordedRequestRetriver.isGetRequestCalled(timeOfExecution, "/api/oltResourceInventory/v1/device?endsz=" + dpuEndsz));
    }

    @Step
    public void checkGetDpuPonConnCalled(Long timeOfExecution, String dpuEndsz){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertTrue(wiremockRecordedRequestRetriver.isGetRequestCalled(timeOfExecution, "/api/oltResourceInventory/v1/dpu/dpuPonConnection?dpuPonPortEndsz=" + dpuEndsz + "&dpuPonPortNumber=1"));
    }

    @Step
    public void checkGetDpuPonConnNotCalled(Long timeOfExecution, String dpuEndsz){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertFalse(wiremockRecordedRequestRetriver.isGetRequestCalled(timeOfExecution, "/api/oltResourceInventory/v1/dpu/dpuPonConnection?dpuPonPortEndsz=" + dpuEndsz + "&dpuPonPortNumber=1"));
    }

    @Step
    public void checkGetEthernetLinkCalled(Long timeOfExecution, String oltEndsz){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertTrue(wiremockRecordedRequestRetriver.isGetRequestCalled(timeOfExecution, "/api/oltResourceInventory/v1/olt/findEthernetLinksByEndsz?oltEndSz=" + oltEndsz));
    }

    @Step
    public void checkGetEthernetLinkNotCalled(Long timeOfExecution, String oltEndsz){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertFalse(wiremockRecordedRequestRetriver.isGetRequestCalled(timeOfExecution, "/api/oltResourceInventory/v1/olt/findEthernetLinksByEndsz?oltEndSz=" + oltEndsz));
    }

    @Step
    public void checkPostOnuIdCalled(Long timeOfExecution, List<String> fieldValues){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertTrue(wiremockRecordedRequestRetriver.isPostRequestCalled(timeOfExecution, fieldValues, "/resource-order-resource-inventory/v1/assignOnuIdTask"));
    }

    @Step
    public void checkPostOnuIdNotCalled(Long timeOfExecution, List<String> fieldValues){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertFalse(wiremockRecordedRequestRetriver.isPostRequestCalled(timeOfExecution, fieldValues, "/resource-order-resource-inventory/v1/assignOnuIdTask"));
    }

    @Step
    public void checkPostBackhaulidCalled(Long timeOfExecution, List<String> fieldValues){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertTrue(wiremockRecordedRequestRetriver.isPostRequestCalled(timeOfExecution, fieldValues, "/resource-order-resource-inventory/v3/backhaulId/search"));
    }

    @Step
    public void checkPostBackhaulidNotCalled(Long timeOfExecution, List<String> fieldValues){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertFalse(wiremockRecordedRequestRetriver.isPostRequestCalled(timeOfExecution, fieldValues, "/resource-order-resource-inventory/v3/backhaulId/search"));
    }

    @Step
    public void checkPostDeprovisioningPortCalled(Long timeOfExecution, List<String> fieldValues){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertTrue(wiremockRecordedRequestRetriver.isPostRequestCalled(timeOfExecution, fieldValues, "/resource-order-resource-inventory/v1/deprovisioning/port"));
    }

    @Step
    public void checkPostDeprovisioningPortNotCalled(Long timeOfExecution, List<String> fieldValues){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertFalse(wiremockRecordedRequestRetriver.isPostRequestCalled(timeOfExecution, fieldValues, "/resource-order-resource-inventory/v1/deprovisioning/port"));
    }
    @Step
    public void checkPostConfigAncpCalled(Long timeOfExecution, String dpuEndsz){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertTrue(wiremockRecordedRequestRetriver.isPostRequestCalled(timeOfExecution, "/api/ancpConfiguration/v2/ancp?uplinkId=1049" + "&endSz=" + dpuEndsz + "&sessionType=DPU" ));
    }

    @Step
    public void checkPostConfigAncpNotCalled(Long timeOfExecution, String dpuEndsz){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertFalse(wiremockRecordedRequestRetriver.isPostRequestCalled(timeOfExecution, "/api/ancpConfiguration/v2/ancp?uplinkId=1049" + "&endSz=" + dpuEndsz + "&sessionType=DPU"));
    }

    @Step
    public void checkGetAncpSessionCalled(Long timeOfExecution, String dpuEndsz){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertTrue(wiremockRecordedRequestRetriver.isGetRequestCalled(timeOfExecution, "/api/oltResourceInventory/v1/ancp/endsz?endsz=" + dpuEndsz));
    }

    @Step
    public void checkGetAncpSessionNotCalled(Long timeOfExecution, String dpuEndsz){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertFalse(wiremockRecordedRequestRetriver.isGetRequestCalled(timeOfExecution, "/api/oltResourceInventory/v1/ancp/endsz?endsz=" + dpuEndsz));
    }

}