package com.tsystems.tm.acc.ta.robot.osr;

import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.ResponseSpecBuilders;
import com.tsystems.tm.acc.ta.api.RhssoBrowserFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.DpuCommissioningClient;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.robot.utils.WiremockRecordedRequestRetriver;
import com.tsystems.tm.acc.tests.osr.dpu.commissioning.api.DpuCommissioningApi;
import com.tsystems.tm.acc.tests.osr.dpu.commissioning.model.DpuCommissioningResponse;
import com.tsystems.tm.acc.tests.osr.dpu.commissioning.model.StartDpuCommissioningRequest;
import com.tsystems.tm.acc.tests.osr.dpu.commissioning.model.StartDpuDecommissioningRequest;
import com.tsystems.tm.acc.tests.osr.resource.inventory.adapter.external.client.api.RestoreApi;
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
    public String id;

    public static final String GET_DPU_ANCP_SESSION_URL = "/resource-order-resource-inventory/v5/ancpSession";
    public static final String GET_ETHERNET_LINK_URL = "/resource-order-resource-inventory/v5/uplink";
    public static final String ASSIGN_ONU_ID_TASK_URL = "/resource-order-resource-inventory/v1/assignOnuIdTask";

   // private  final AuthTokenProvider authTokenProvider = new RhssoClientFlowAuthTokenProvider("dpu-commissioning","VfynslyzImAD3LKW");
   private static final AuthTokenProvider authTokenProvider = new RhssoClientFlowAuthTokenProvider("dpu-commissioning", RhssoHelper.getSecretOfGigabitHub("dpu-commissioning"));

    @Step("Start dpuCommissioning")
    public UUID startProcess(String endsz) {
        dpuCommissioningClient = new DpuCommissioningClient(authTokenProvider);
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

    @Step("Start dpuCommissioning error code 500")
    public UUID startProcess500(String endsz) {
        dpuCommissioningClient = new DpuCommissioningClient(authTokenProvider);
        StartDpuCommissioningRequest dpuCommissioningRequest = new StartDpuCommissioningRequest();
        dpuCommissioningRequest.setEndSZ(endsz);

        UUID traceId = UUID.randomUUID();

        dpuCommissioningClient.getClient().dpuCommissioning().startDpuDeviceCommissioning()
                .body(dpuCommissioningRequest)
                .xB3ParentSpanIdHeader(UUID.randomUUID().toString())
                .xB3TraceIdHeader(traceId.toString())
                .xBusinessContextHeader(UUID.randomUUID().toString())
                .xB3SpanIdHeader(UUID.randomUUID().toString())
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(500)));

        return traceId;
    }

    @Step("Start dpuCommissioning")
    public DpuCommissioningResponse startCommissioningProcess(String endsz, UUID traceId) {
        dpuCommissioningClient = new DpuCommissioningClient(authTokenProvider);
        StartDpuCommissioningRequest dpuCommissioningRequest = new StartDpuCommissioningRequest();
        dpuCommissioningRequest.setEndSZ(endsz);

        return dpuCommissioningClient.getClient().dpuCommissioning().startDpuDeviceCommissioning()
                .body(dpuCommissioningRequest)
                .xB3ParentSpanIdHeader(UUID.randomUUID().toString())
                .xB3TraceIdHeader(traceId.toString())
                .xBusinessContextHeader(UUID.randomUUID().toString())
                .xB3SpanIdHeader(UUID.randomUUID().toString())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    @Step("Start dpuDecommissioning")
    public DpuCommissioningResponse startDecommissioningProcess(String endsz) {
        dpuCommissioningClient = new DpuCommissioningClient(authTokenProvider);
        StartDpuDecommissioningRequest dpuDecommissioningRequest = new StartDpuDecommissioningRequest();
        dpuDecommissioningRequest.setEndSZ(endsz);

        return dpuCommissioningClient.getClient().dpuCommissioning().startDpuDeviceDecommissioning()
                .body(dpuDecommissioningRequest)
                .xB3ParentSpanIdHeader("1")
                .xB3TraceIdHeader("2")
                .xBusinessContextHeader("3")
                .xB3SpanIdHeader("4")
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    @Step("Start dpuDecommissioning 500")
    public void startDecommissioningProcess500(String endsz) {
        dpuCommissioningClient = new DpuCommissioningClient(authTokenProvider);
        StartDpuDecommissioningRequest dpuDecommissioningRequest = new StartDpuDecommissioningRequest();
        dpuDecommissioningRequest.setEndSZ(endsz);

        dpuCommissioningClient.getClient().dpuCommissioning().startDpuDeviceDecommissioning()
                .body(dpuDecommissioningRequest)
                .xB3ParentSpanIdHeader("1")
                .xB3TraceIdHeader("2")
                .xBusinessContextHeader("3")
                .xB3SpanIdHeader("4")
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(500)));
    }

    @Step("Start restore process")
    public void startRestoreProcess(String id){
        dpuCommissioningClient = new DpuCommissioningClient(authTokenProvider);
        dpuCommissioningClient.getClient().dpuCommissioning().restoreProcess()
                .processIdPath(id)
                .xBusinessContextHeader("cef0cbf3-6458-4f13-a418-ee4d7e7505dd")
                .xCallbackCorrelationIdHeader("cef0cbf3-6458-4f13-a418-ee4d7e7505dd")
                .execute(validatedWith(shouldBeCode(200)));
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
    public void checkGetDpuPonConnCalled(String gfApFolId) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isGetRequestCalled(urlEqualTo("/resource-order-resource-inventory/v2/llc?gfApFolId=" + gfApFolId + "&page=0&pageSize=64&direction=ASC"));
    }

    @Step
    public void checkGetDpuPonConnNotCalled(String gfApFolId) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isGetRequestNotCalled(urlEqualTo("/resource-order-resource-inventory/v2/llc?gfApFolId=" + gfApFolId + "&page=0&pageSize=64&direction=ASC"));
    }

    @Step
    public void checkGetEthernetLinkCalled(String oltEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isGetRequestCalled(urlPathEqualTo(GET_ETHERNET_LINK_URL));
    }

    @Step
    public void checkGetEthernetLinkNotCalled(String oltEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isGetRequestNotCalled(urlPathEqualTo(GET_ETHERNET_LINK_URL));
    }

    @Step
    public void checkPostOnuIdCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestCalled(consumers, urlPathEqualTo(ASSIGN_ONU_ID_TASK_URL));
    }

    @Step
    public void checkPostOnuIdNotCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestNotCalled(consumers, urlPathEqualTo(ASSIGN_ONU_ID_TASK_URL));
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
        wiremockRecordedRequestRetriver.isPostRequestCalled(urlEqualTo("/api/ancpConfiguration/v2/ancp?uplinkId=1049" + "&endSz=" + dpuEndsz + "&sessionType=DPU"));
    }

    @Step
    public void checkPostConfigAncpNotCalled(String dpuEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestNotCalled(urlEqualTo("/api/ancpConfiguration/v2/ancp?uplinkId=1049" + "&endSz=" + dpuEndsz + "&sessionType=DPU"));
    }

    @Step
    public void checkGetDpuAncpSessionCalled(String dpuEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isGetRequestCalled(urlEqualTo(GET_DPU_ANCP_SESSION_URL + "?accessNodeEquipmentBusinessRef.endSz=" + dpuEndsz));
    }

    @Step
    public void checkGetDpuAncpSessionNotCalled(String dpuEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isGetRequestNotCalled(urlPathEqualTo(GET_DPU_ANCP_SESSION_URL + "?accessNodeEquipmentBusinessRef.endSz=" + dpuEndsz));
    }

    @Step
    public void checkGetOltAncpSessionCalled(String oltEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isGetRequestCalled(urlEqualTo(GET_DPU_ANCP_SESSION_URL + "?accessNodeEquipmentBusinessRef.endSz=" + oltEndsz));
    }

    @Step
    public void checkGetOltAncpSessionNotCalled(String oltEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isGetRequestNotCalled(urlPathEqualTo(GET_DPU_ANCP_SESSION_URL + "?accessNodeEquipmentBusinessRef.endSz=" + oltEndsz));
    }

    @Step
    public void checkGetDpuAtOltConfigCalled(String dpuEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isGetRequestCalled(urlEqualTo("/resource-order-resource-inventory/v1/dpu/dpuAtOltConfiguration?dpuEndsz=" + dpuEndsz));
    }
    @Step
    public void checkGetDpuAtOltConfigForOltCalled(String oltEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isGetRequestCalled(urlEqualTo("/resource-order-resource-inventory/v1/dpu/dpuAtOltConfiguration?oltEndsz=" + oltEndsz));
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
    public void checkDeleteDeviceDeprovisioningCalled(String dpuEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isDeleteRequestCalled(urlEqualTo("/resource-order-resource-inventory/v1/fttbDeprovisioning/device?dpuEndSZ=" + dpuEndsz));
    }

    @Step
    public void checkDeleteDeviceDeprovisioningNotCalled(String dpuEndsz) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isDeleteRequestNotCalled(urlEqualTo("/resource-order-resource-inventory/v1/fttbDeprovisioning/device?dpuEndSZ=" + dpuEndsz));
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
    public void checkPostReleaseOnuIdTaskCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestCalled(consumers, urlPathEqualTo("/resource-order-resource-inventory/v1/releaseOnuIdTask"));
    }

    @Step
    public void checkPostReleaseOnuIdTaskNotCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestNotCalled(consumers, urlPathEqualTo("/resource-order-resource-inventory/v1/releaseOnuIdTask"));
    }

    @Step
    public void checkDeleteDpuOltConfigurationCalled(){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isDeleteRequestCalled(urlMatching("/resource-order-resource-inventory/v1/dpu/dpuAtOltConfiguration.*"));
    }

    @Step
    public void checkDeleteDpuOltConfigurationNotCalled(){
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isDeleteRequestNotCalled(urlMatching("/resource-order-resource-inventory/v1/dpu/dpuAtOltConfiguration.*"));
    }

    @Step
    public void checkDeleteAncpConfigCalled() {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isDeleteRequestCalled(urlEqualTo("/api/ancpConfiguration/v2/ancp/99990"));
    }

    @Step
    public void checkDeleteAncpConfigNotCalled() {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isDeleteRequestNotCalled(urlEqualTo("/api/ancpConfiguration/v2/ancp/99990"));
    }

    @Step
    public void checkPostPreprovisionFTTHTaskCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestCalled(consumers, urlPathEqualTo("/resource-order-resource-inventory/v1/provisioning/port"));
    }

    @Step
    public void checkPostPreprovisionFTTHTaskNotCalled(List<Consumer<RequestPatternBuilder>> consumers) {
        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
        wiremockRecordedRequestRetriver.isPostRequestNotCalled(consumers, urlPathEqualTo("/resource-order-resource-inventory/v1/provisioning/port"));
    }

    @Step
    public void checkDpuCommissioningStepsCalled(){

    }

}