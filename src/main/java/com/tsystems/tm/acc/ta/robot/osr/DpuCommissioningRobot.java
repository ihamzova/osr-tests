package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.data.models.stable.OltDevice;
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

import static com.codeborne.selenide.Selenide.sleep;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

@Slf4j
public class DpuCommissioningRobot {

    public static final Integer HTTP_CODE_CREATED_201 = 201;
    private static final Long DELAY = 8_000L;
    private DpuCommissioningClient dpuCommissioningClient;
    private DpuCommissioningGenerator dpuCommissioningGenerator;
    public String businessKey;

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

        businessKey = response.getBusinessKey();
    }

    @Step("get businessKey")
    public String getBusinessKey(){
        return businessKey;
    }

    @Step("setUp wiremock for TeamLevel Test")
    public void setUpWiremock(OltDevice oltDevice, Dpu dpu, boolean isAsyncScenario){
        dpuCommissioningGenerator = new DpuCommissioningGenerator();

        dpuCommissioningGenerator.generateGetDpuDeviceStub(dpu);
        dpuCommissioningGenerator.generateGetDpuPonConnStub(oltDevice, dpu);
        dpuCommissioningGenerator.generateGetEthLinkStub(oltDevice,dpu);
        dpuCommissioningGenerator.generateGetOnuIdStub(dpu);
        dpuCommissioningGenerator.generateGetBackhaulIdStub(oltDevice,dpu);
        dpuCommissioningGenerator.generatePostDeprovisionOltStub(oltDevice,dpu,isAsyncScenario);
        dpuCommissioningGenerator.generatePostAncpConfStub(dpu, isAsyncScenario);
        dpuCommissioningGenerator.generateGetDPUAncpStub(dpu);
        dpuCommissioningGenerator.generateGetOLTAncpStub(oltDevice,dpu);
        dpuCommissioningGenerator.generateGetDpuAtOltConfigStub(dpu);
        dpuCommissioningGenerator.generatePostDpuAtOltConfigStub(dpu);
        dpuCommissioningGenerator.generateDpuConfigurationTaskStub(dpu, isAsyncScenario);
        dpuCommissioningGenerator.generatePutDpuAtOltConfigStub(dpu);
        dpuCommissioningGenerator.generateGetDpuEmsConfigStub(dpu);
        dpuCommissioningGenerator.generatePostDpuEmsConfigStub(dpu);
        dpuCommissioningGenerator.generateSealPostDpuConfStub(dpu, isAsyncScenario);
        dpuCommissioningGenerator.generatePutDpuEmsConfigStub(dpu);
        WiremockRobot wiremockRobot = new WiremockRobot();
        wiremockRobot.initializeWiremock(new File(System.getProperty("user.dir") + "/src/test/resources/team/morpheus/wiremockResult"));
    }


    @Step("cleanup")
    public void cleanup(){
        //Microservice make 3 attempt to receive a positive response. Therefore mocks shouldn't be deleted at once
        sleep(DELAY);
        WiremockHelper.mappingsReset();
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
        Assert.assertTrue(wiremockRecordedRequestRetriver.isGetRequestCalled(timeOfExecution, "/resource-order-resource-inventory/v1/device?endsz=" + dpuEndsz));
    }

    @Step
    public void checkGetDpuPonConnCalled(Long timeOfExecution, String dpuEndsz){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertTrue(wiremockRecordedRequestRetriver.isGetRequestCalled(timeOfExecution, "/resource-order-resource-inventory/v1/dpu/dpuPonConnection?dpuPonPortEndsz=" + dpuEndsz + "&dpuPonPortNumber=1"));
    }

    @Step
    public void checkGetDpuPonConnNotCalled(Long timeOfExecution, String dpuEndsz){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertFalse(wiremockRecordedRequestRetriver.isGetRequestCalled(timeOfExecution, "/resource-order-resource-inventory/v1/dpu/dpuPonConnection?dpuPonPortEndsz=" + dpuEndsz + "&dpuPonPortNumber=1"));
    }

    @Step
    public void checkGetEthernetLinkCalled(Long timeOfExecution, String oltEndsz){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertTrue(wiremockRecordedRequestRetriver.isGetRequestCalled(timeOfExecution, "/resource-order-resource-inventory/v1/ethernetlink/findEthernetLinksByEndsz?oltEndSz=" + oltEndsz));
    }

    @Step
    public void checkGetEthernetLinkNotCalled(Long timeOfExecution, String oltEndsz){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertFalse(wiremockRecordedRequestRetriver.isGetRequestCalled(timeOfExecution, "/resource-order-resource-inventory/v1/ethernetlink/findEthernetLinksByEndsz?oltEndSz=" + oltEndsz));
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
        Assert.assertTrue(wiremockRecordedRequestRetriver.isPostPatternRequestCalled(timeOfExecution, fieldValues, "/resource-order-resource-inventory/v1/deprovisioning/port(.*)"));
    }

    @Step
    public void checkPostDeprovisioningPortNotCalled(Long timeOfExecution, List<String> fieldValues){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertFalse(wiremockRecordedRequestRetriver.isPostPatternRequestCalled(timeOfExecution, fieldValues, "/resource-order-resource-inventory/v1/deprovisioning/port(.*)"));
    }
    @Step
    public void checkPostConfigAncpCalled(Long timeOfExecution, String dpuEndsz){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertTrue(wiremockRecordedRequestRetriver.isPostRequestCalled(timeOfExecution, "/resource-order-resource-inventory/v2/ancp/configuration?uplinkId=1049" + "&endSz=" + dpuEndsz + "&sessionType=DPU" ));
    }

    @Step
    public void checkPostConfigAncpNotCalled(Long timeOfExecution, String dpuEndsz){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertFalse(wiremockRecordedRequestRetriver.isPostRequestCalled(timeOfExecution, "/resource-order-resource-inventory/v2/ancp/configuration?uplinkId=1049" + "&endSz=" + dpuEndsz + "&sessionType=DPU"));
    }

    @Step
    public void checkGetDpuAncpSessionCalled(Long timeOfExecution, String dpuEndsz){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertTrue(wiremockRecordedRequestRetriver.isGetRequestCalled(timeOfExecution, "/resource-order-resource-inventory/v1/ancp/session/endsz?endsz=" + dpuEndsz));
    }

    @Step
    public void checkGetDpuAncpSessionNotCalled(Long timeOfExecution, String dpuEndsz){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertFalse(wiremockRecordedRequestRetriver.isGetRequestCalled(timeOfExecution, "/resource-order-resource-inventory/v1/ancp/session/endsz?endsz=" + dpuEndsz));
    }

    @Step
    public void checkGetOltAncpSessionCalled(Long timeOfExecution, String oltEndsz){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertTrue(wiremockRecordedRequestRetriver.isGetRequestCalled(timeOfExecution, "/resource-order-resource-inventory/v1/ancp/session/endsz?endsz=" + oltEndsz));
    }

    @Step
    public void checkGetOltAncpSessionNotCalled(Long timeOfExecution, String oltEndsz){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertFalse(wiremockRecordedRequestRetriver.isGetRequestCalled(timeOfExecution, "/resource-order-resource-inventory/v1/ancp/session/endsz?endsz=" + oltEndsz));
    }

    @Step
    public void checkGetDpuAtOltConfigCalled(Long timeOfExecution, String dpuEndsz){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertTrue(wiremockRecordedRequestRetriver.isGetRequestCalled(timeOfExecution, "/resource-order-resource-inventory/v1/dpu/dpuAtOltConfiguration?dpuEndsz=" + dpuEndsz));
    }

    @Step
    public void checkGetDpuAtOltConfigNotCalled(Long timeOfExecution, String dpuEndsz){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertFalse(wiremockRecordedRequestRetriver.isGetRequestCalled(timeOfExecution, "/resource-order-resource-inventory/v1/dpu/dpuAtOltConfiguration?dpuEndsz=" + dpuEndsz));
    }

    @Step
    public void checkPostDpuAtOltConfigCalled(Long timeOfExecution, List<String> fieldValues){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertTrue(wiremockRecordedRequestRetriver.isPostRequestCalled(timeOfExecution, fieldValues, "/resource-order-resource-inventory/v1/dpu/dpuAtOltConfiguration"));
    }

    @Step
    public void checkPostDpuAtOltConfigNotCalled(Long timeOfExecution, List<String> fieldValues){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertFalse(wiremockRecordedRequestRetriver.isPostRequestCalled(timeOfExecution, fieldValues, "/resource-order-resource-inventory/v1/dpu/dpuAtOltConfiguration"));
    }

    @Step
    public void checkPostSEALDpuAtOltConfigCalled(Long timeOfExecution, List<String> fieldValues){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertTrue(wiremockRecordedRequestRetriver.isPostRequestCalled(timeOfExecution, fieldValues, "/resource-order-resource-inventory/v1/olt/dpuConfigurationTask"));
    }

    @Step
    public void checkPostSEALDpuAtOltConfigNotCalled(Long timeOfExecution, List<String> fieldValues){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertFalse(wiremockRecordedRequestRetriver.isPostRequestCalled(timeOfExecution, fieldValues, "/resource-order-resource-inventory/v1/olt/dpuConfigurationTask"));
    }

    @Step
    public void checkPutDpuAtOltConfigCalled(Long timeOfExecution, List<String> fieldValues){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertTrue(wiremockRecordedRequestRetriver.isPutRequestCalled(timeOfExecution, fieldValues, "/resource-order-resource-inventory/v1/dpu/dpuAtOltConfiguration/12345"));
    }

    @Step
    public void checkPutDpuAtOltConfigNotCalled(Long timeOfExecution, List<String> fieldValues){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertFalse(wiremockRecordedRequestRetriver.isPutRequestCalled(timeOfExecution, fieldValues, "/resource-order-resource-inventory/v1/dpu/dpuAtOltConfiguration/12345"));
    }

    @Step
    public void checkGetDpuEmsConfigCalled(Long timeOfExecution, String dpuEndsz){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertTrue(wiremockRecordedRequestRetriver.isGetRequestCalled(timeOfExecution, "/resource-order-resource-inventory/v1/dpu/dpuEmsConfiguration?dpuEndsz=" + dpuEndsz));
    }

    @Step
    public void checkGetDpuEmsConfigNotCalled(Long timeOfExecution, String dpuEndsz){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertFalse(wiremockRecordedRequestRetriver.isGetRequestCalled(timeOfExecution, "/resource-order-resource-inventory/v1/dpu/dpuEmsConfiguration?dpuEndsz=" + dpuEndsz));
    }

    @Step
    public void checkPostDpuEmsConfigCalled(Long timeOfExecution, List<String> fieldValues){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertTrue(wiremockRecordedRequestRetriver.isPostRequestCalled(timeOfExecution, fieldValues, "/resource-order-resource-inventory/v1/dpu/dpuEmsConfiguration"));
    }

    @Step
    public void checkPostDpuEmsConfigNotCalled(Long timeOfExecution, List<String> fieldValues){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertFalse(wiremockRecordedRequestRetriver.isPostRequestCalled(timeOfExecution, fieldValues, "/resource-order-resource-inventory/v1/dpu/dpuEmsConfiguration"));
    }

    @Step
    public void checkPostSEALDpuEmsConfigCalled(Long timeOfExecution, List<String> fieldValues){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertTrue(wiremockRecordedRequestRetriver.isPostRequestCalled(timeOfExecution, fieldValues, "/resource-order-resource-inventory/v1/dpu/dpuConfigurationTask"));
    }

    @Step
    public void checkPostSEALDpuEmsConfigNotCalled(Long timeOfExecution, List<String> fieldValues){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertFalse(wiremockRecordedRequestRetriver.isPostRequestCalled(timeOfExecution, fieldValues, "/resource-order-resource-inventory/v1/dpu/dpuConfigurationTask"));
    }

    @Step
    public void checkPutDpuEmsConfigCalled(Long timeOfExecution, List<String> fieldValues){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertTrue(wiremockRecordedRequestRetriver.isPutRequestCalled(timeOfExecution, fieldValues, "/resource-order-resource-inventory/v1/dpu/dpuEmsConfiguration/12345"));
    }

    @Step
    public void checkPutDpuEmsConfigNotCalled(Long timeOfExecution, List<String> fieldValues){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        Assert.assertFalse(wiremockRecordedRequestRetriver.isPutRequestCalled(timeOfExecution, fieldValues, "/resource-order-resource-inventory/v1/dpu/dpuEmsConfiguration/12345"));
    }

}