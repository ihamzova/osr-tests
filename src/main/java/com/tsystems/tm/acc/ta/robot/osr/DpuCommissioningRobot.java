package com.tsystems.tm.acc.ta.robot.osr;

import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.tsystems.tm.acc.ta.api.osr.DpuCommissioningClient;
import com.tsystems.tm.acc.ta.robot.utils.WiremockRecordedRequestRetriver;
import com.tsystems.tm.acc.tests.osr.dpu.commissioning.model.DpuCommissioningResponse;
import com.tsystems.tm.acc.tests.osr.dpu.commissioning.model.StartDpuCommissioningRequest;
import com.tsystems.tm.acc.tests.osr.dpu.commissioning.model.StartDpuDecommissioningRequest;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

@Slf4j
public class DpuCommissioningRobot {

    public static final Integer HTTP_CODE_CREATED_201 = 201;
    private static final Long DELAY = 8_000L;
    private DpuCommissioningClient dpuCommissioningClient;
    public String businessKey;

    @Step("Start dpuCommissioning")
    public UUID startProcess(String endsz) {
        dpuCommissioningClient = new DpuCommissioningClient();
        StartDpuCommissioningRequest dpuCommissioningRequest = new StartDpuCommissioningRequest();
        dpuCommissioningRequest.setEndSZ(endsz);

        UUID traceId = UUID.randomUUID();

        DpuCommissioningResponse response = dpuCommissioningClient.getClient().dpuCommissioning().startDpuDeviceCommissioning()
                .body(dpuCommissioningRequest)
                .xB3ParentSpanIdHeader(UUID.randomUUID().toString())
                .xB3TraceIdHeader(traceId.toString())
                .xBusinessContextHeader(UUID.randomUUID().toString())
                .xB3SpanIdHeader(UUID.randomUUID().toString())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        businessKey = response.getBusinessKey();
        return traceId;
    }

    @Step("Start dpuDecommissioning")
    public void startDecomissioningProcess(String endsz) {
        dpuCommissioningClient = new DpuCommissioningClient();
        StartDpuDecommissioningRequest dpuDecommissioningRequest = new StartDpuDecommissioningRequest();
        dpuDecommissioningRequest.setEndSZ(endsz);

        DpuCommissioningResponse response = dpuCommissioningClient.getClient().dpuCommissioning().startDpuDeviceDecommissioning()
                .body(dpuDecommissioningRequest)
                .xB3ParentSpanIdHeader("1")
                .xB3TraceIdHeader("2")
                .xBusinessContextHeader("3")
                .xB3SpanIdHeader("4")
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        businessKey = response.getBusinessKey();
    }


    @Step("get businessKey")
    public String getBusinessKey() {
        return businessKey;
    }

    @Step
    public void checkGetDeviceDPUCalled(String dpuEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isGetRequestCalled(urlEqualTo("/resource-order-resource-inventory/v1/device?endsz=" + dpuEndsz));
    }

    @Step
    public void checkGetDpuPonConnCalled(String dpuEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isGetRequestCalled(urlEqualTo("/resource-order-resource-inventory/v1/dpu/dpuPonConnection?dpuPonPortEndsz=" + dpuEndsz + "&dpuPonPortNumber=1"));
    }

    @Step
    public void checkGetDpuPonConnNotCalled(String dpuEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isGetRequestNotCalled(urlEqualTo("/resource-order-resource-inventory/v1/dpu/dpuPonConnection?dpuPonPortEndsz=" + dpuEndsz + "&dpuPonPortNumber=1"));
    }

    @Step
    public void checkGetEthernetLinkCalled(String oltEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isGetRequestCalled(urlEqualTo("/resource-order-resource-inventory/v1/ethernetlink/findEthernetLinksByEndsz?oltEndSz=" + oltEndsz));
    }

    @Step
    public void checkGetEthernetLinkNotCalled(String oltEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isGetRequestNotCalled(urlEqualTo("/resource-order-resource-inventory/v1/ethernetlink/findEthernetLinksByEndsz?oltEndSz=" + oltEndsz));
    }

    @Step
    public void checkPostOnuIdCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestCalled(consumers, urlPathEqualTo("/resource-order-resource-inventory/v1/assignOnuIdTask"));
    }

    @Step
    public void checkPostOnuIdNotCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestNotCalled(consumers, urlPathEqualTo("/resource-order-resource-inventory/v1/assignOnuIdTask"));
    }

    @Step
    public void checkPostBackhaulidCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestCalled(consumers, urlPathEqualTo("/resource-order-resource-inventory/v3/backhaulId/search"));
    }

    @Step
    public void checkPostBackhaulidNotCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestNotCalled(consumers, urlPathEqualTo("/resource-order-resource-inventory/v3/backhaulId/search"));
    }

    @Step
    public void checkPostDeprovisioningPortCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestCalled(consumers, urlMatching("/resource-order-resource-inventory/v1/deprovisioning/port(.*)"));
    }

    @Step
    public void checkPostDeprovisioningPortNotCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestNotCalled(consumers, urlMatching("/resource-order-resource-inventory/v1/deprovisioning/port(.*)"));
    }

    @Step
    public void checkPostConfigAncpCalled(String dpuEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestCalled(urlEqualTo("/resource-order-resource-inventory/v2/ancp/configuration?uplinkId=1049" + "&endSz=" + dpuEndsz + "&sessionType=DPU"));
    }

    @Step
    public void checkPostConfigAncpNotCalled(String dpuEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestNotCalled(urlEqualTo("/resource-order-resource-inventory/v2/ancp/configuration?uplinkId=1049" + "&endSz=" + dpuEndsz + "&sessionType=DPU"));
    }

    @Step
    public void checkGetDpuAncpSessionCalled(String dpuEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isGetRequestCalled(urlEqualTo("/resource-order-resource-inventory/v1/ancp/session/endsz?endsz=" + dpuEndsz));
    }

    @Step
    public void checkGetDpuAncpSessionNotCalled(String dpuEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isGetRequestNotCalled(urlEqualTo("/resource-order-resource-inventory/v1/ancp/session/endsz?endsz=" + dpuEndsz));
    }

    @Step
    public void checkGetOltAncpSessionCalled(String oltEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isGetRequestCalled(urlEqualTo("/resource-order-resource-inventory/v1/ancp/session/endsz?endsz=" + oltEndsz));
    }

    @Step
    public void checkGetOltAncpSessionNotCalled(String oltEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isGetRequestNotCalled(urlEqualTo("/resource-order-resource-inventory/v1/ancp/session/endsz?endsz=" + oltEndsz));
    }

    @Step
    public void checkGetDpuAtOltConfigCalled(String dpuEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isGetRequestCalled(urlEqualTo("/resource-order-resource-inventory/v1/dpu/dpuAtOltConfiguration?dpuEndsz=" + dpuEndsz));
    }

    @Step
    public void checkGetDpuAtOltConfigNotCalled(String dpuEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isGetRequestNotCalled(urlEqualTo("/resource-order-resource-inventory/v1/dpu/dpuAtOltConfiguration?dpuEndsz=" + dpuEndsz));
    }

    @Step
    public void checkPostDpuAtOltConfigCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestCalled(consumers, urlPathEqualTo("/resource-order-resource-inventory/v1/dpu/dpuAtOltConfiguration"));
    }

    @Step
    public void checkPostDpuAtOltConfigNotCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestNotCalled(consumers, urlPathEqualTo("/resource-order-resource-inventory/v1/dpu/dpuAtOltConfiguration"));
    }

    @Step
    public void checkPostSEALDpuAtOltConfigCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestCalled(consumers, urlPathEqualTo("/resource-order-resource-inventory/v1/olt/dpuConfigurationTask"));
    }

    @Step
    public void checkPostSEALDpuAtOltConfigNotCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestNotCalled(consumers, urlPathEqualTo("/resource-order-resource-inventory/v1/olt/dpuConfigurationTask"));
    }

    @Step
    public void checkPutDpuAtOltConfigCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPutRequestCalled(consumers, urlPathEqualTo("/resource-order-resource-inventory/v1/dpu/dpuAtOltConfiguration/12345"));
    }

    @Step
    public void checkPutDpuAtOltConfigNotCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPutRequestNotCalled(consumers, urlPathEqualTo("/resource-order-resource-inventory/v1/dpu/dpuAtOltConfiguration/12345"));
    }

    @Step
    public void checkGetDpuEmsConfigCalled(String dpuEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isGetRequestCalled(urlEqualTo("/resource-order-resource-inventory/v1/dpu/dpuEmsConfiguration?dpuEndsz=" + dpuEndsz));
    }

    @Step
    public void checkGetDpuEmsConfigNotCalled(String dpuEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isGetRequestNotCalled(urlEqualTo("/resource-order-resource-inventory/v1/dpu/dpuEmsConfiguration?dpuEndsz=" + dpuEndsz));
    }

    @Step
    public void checkPostDpuEmsConfigCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestCalled(consumers, urlPathEqualTo("/resource-order-resource-inventory/v1/dpu/dpuEmsConfiguration"));
    }

    @Step
    public void checkPostDpuEmsConfigNotCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestNotCalled(consumers, urlPathEqualTo("/resource-order-resource-inventory/v1/dpu/dpuEmsConfiguration"));
    }

    @Step
    public void checkPostSEALDpuEmsConfigCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestCalled(consumers, urlPathEqualTo("/resource-order-resource-inventory/v1/dpu/dpuConfigurationTask"));
    }

    @Step
    public void checkPostSEALDpuEmsConfigNotCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestNotCalled(consumers, urlPathEqualTo("/resource-order-resource-inventory/v1/dpu/dpuConfigurationTask"));
    }

    @Step
    public void checkPutDpuEmsConfigCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPutRequestCalled(consumers, urlPathEqualTo("/resource-order-resource-inventory/v1/dpu/dpuEmsConfiguration/12345"));
    }

    @Step
    public void checkPutDpuEmsConfigNotCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPutRequestNotCalled(consumers, urlPathEqualTo("/resource-order-resource-inventory/v1/dpu/dpuEmsConfiguration/12345"));
    }

    @Step
    public void checkPostDeviceProvisioningCalled(String dpuEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestCalled(urlEqualTo("/resource-order-resource-inventory/v1/fttbProvisioning/device?endSZ=" + dpuEndsz));
    }

    @Step
    public void checkPostDeviceProvisioningNotCalled(String dpuEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestNotCalled(urlEqualTo("/resource-order-resource-inventory/v1/fttbProvisioning/device?endSZ=" + dpuEndsz));
    }

    @Step
    public void checkPatchDeviceCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPatchRequestCalled(consumers, urlMatching("/resource-order-resource-inventory/v1/device/.*"));
    }

    @Step
    public void checkPatchDeviceNotCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPatchRequestNotCalled(consumers, urlMatching("/resource-order-resource-inventory/v1/device/.*"));
    }

    @Step
    public void checkPatchPortCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPatchRequestCalled(consumers, urlMatching("/resource-order-resource-inventory/v1/port/.*"));
    }

    @Step
    public void checkPatchPortNotCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPatchRequestNotCalled(consumers, urlMatching("/resource-order-resource-inventory/v1/port/.*"));
    }

    @Step
    public void checkPostDeviceDeprovisioningCalled(String dpuEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestCalled(urlEqualTo("/resource-order-resource-inventory/v1/fttbDeprovisioning/device?endSZ=" + dpuEndsz));
    }

    @Step
    public void checkPostDeviceDeprovisioningNotCalled(String dpuEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestNotCalled(urlEqualTo("/resource-order-resource-inventory/v1/fttbDeprovisioning/device?endSZ=" + dpuEndsz));
    }

    @Step
    public void checkPostSEALDpuEmsDEConfigCalled(List<Consumer<RequestPatternBuilder>> consumers){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestCalled(consumers, urlMatching("/resource-order-resource-inventory/v1/dpu/dpuDeconfigurationTask"));
    }

    @Step
    public void checkPostSEALDpuEmsDEConfigNotCalled(List<Consumer<RequestPatternBuilder>> consumers){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestNotCalled(consumers, urlMatching("/resource-order-resource-inventory/v1/dpu/dpuDeconfigurationTask"));
    }

    @Step
    public void checkPostSEALDpuOltDEConfigCalled(List<Consumer<RequestPatternBuilder>> consumers){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestCalled(consumers, urlMatching("/resource-order-resource-inventory/v1/olt/dpuDeconfigurationTask"));
    }

    @Step
    public void checkPostSEALDpuOltDEConfigNotCalled(List<Consumer<RequestPatternBuilder>> consumers){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestNotCalled(consumers, urlMatching("/resource-order-resource-inventory/v1/olt/dpuDeconfigurationTask"));
    }

    @Step
    public void checkDeleteDpuEmsConfigurationCalled(){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isDeleteRequestCalled(urlMatching("/resource-order-resource-inventory/v1/dpu/dpuEmsConfiguration.*"));
    }

    @Step
    public void checkDeleteDpuEmsConfigurationNotCalled(){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isDeleteRequestNotCalled(urlMatching("/resource-order-resource-inventory/v1/dpu/dpuEmsConfiguration.*"));
    }

    @Step
    public void checkDeleteDpuOltConfigurationCalled(){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isDeleteRequestCalled(urlMatching("/resource-order-resource-inventory/v1/dpu/dpuEmsConfiguration.*"));
    }

    @Step
    public void checkDeleteDpuOltConfigurationNotCalled(){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isDeleteRequestNotCalled(urlMatching("/resource-order-resource-inventory/v1/dpu/dpuAtOltConfiguration.*"));
    }

}