package com.tsystems.tm.acc.ta.team.morpheus.dpucommissioning;

import com.github.tomakehurst.wiremock.admin.RequestSpec;
import com.tsystems.tm.acc.ta.api.osr.DpuCommissioningClient;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.robot.osr.WiremockRobot;
import com.tsystems.tm.acc.tests.osr.dpu.commissioning.model.DpuCommissioningResponse;
import com.tsystems.tm.acc.tests.osr.dpu.commissioning.model.StartDpuCommissioningRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.team.upiter.common.CommonTestData.HTTP_CODE_CREATED_201;
import static com.tsystems.tm.acc.ta.team.upiter.common.CommonTestData.HTTP_CODE_INTERNAL_SERVER_ERROR_500;

public class DpuCommissioning extends ApiTest {

    private DpuCommissioningClient dpuCommissioningClient;

    @BeforeClass
    public void init() {

        dpuCommissioningClient = new DpuCommissioningClient();
        WiremockRobot wiremockRobot = new WiremockRobot();
        wiremockRobot.initializeWiremock("/team.morpheus/wiremock");

    }

    @Test
    public void dpuCommissioningTest(){
        String endSZ = "49/8571/0/71GA";

        StartDpuCommissioningRequest dpuCommissioningRequest = new StartDpuCommissioningRequest();
        dpuCommissioningRequest.setEndSZ(endSZ);

        DpuCommissioningResponse response = dpuCommissioningClient.getClient().dpuCommissioning().startDpuDeviceCommissioning()
                .body(dpuCommissioningRequest)
                .xB3ParentSpanIdHeader("1")
                .xB3TraceIdHeader("2")
                .xBusinessContextHeader("3")
                .xB3SpanIdHeader("4")
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        Assert.assertTrue(response.getComment().contains("SEAL-Interface is CALLED : PROCESS WAITS FOR CALLBACK"));
        Assert.assertEquals("WAIT", response.getStatus());

    }

    @Test
    public void setDpuCommissioningTestError(){

        String endSZ = "49/8571/0/72GA";

        StartDpuCommissioningRequest dpuCommissioningRequest = new StartDpuCommissioningRequest();
        dpuCommissioningRequest.setEndSZ(endSZ);

        DpuCommissioningResponse response = dpuCommissioningClient.getClient().dpuCommissioning().startDpuDeviceCommissioning()
                .body(dpuCommissioningRequest)
                .xB3ParentSpanIdHeader("1")
                .xB3TraceIdHeader("2")
                .xBusinessContextHeader("3")
                .xB3SpanIdHeader("4")
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_INTERNAL_SERVER_ERROR_500)));

        Assert.assertEquals("ERROR", response.getStatus());

    }

}
