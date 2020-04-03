package com.tsystems.tm.acc.ta.team.morpheus.dpucommissioning;

import com.tsystems.tm.acc.ta.api.osr.DpuCommissioningClient;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.db.JDBCConnectionProperties;
import com.tsystems.tm.acc.ta.db.JDBCConnectionPropertiesFactory;
import com.tsystems.tm.acc.ta.db.PostgreSqlDatabase;
import com.tsystems.tm.acc.ta.robot.osr.WiremockRobot;
import com.tsystems.tm.acc.tests.osr.dpu.commissioning.model.DpuCommissioningResponse;
import com.tsystems.tm.acc.tests.osr.dpu.commissioning.model.StartDpuCommissioningRequest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.team.upiter.common.CommonTestData.HTTP_CODE_CREATED_201;
import static com.tsystems.tm.acc.ta.team.upiter.common.CommonTestData.HTTP_CODE_INTERNAL_SERVER_ERROR_500;

public class DpuCommissioning extends ApiTest {

    private DpuCommissioningClient dpuCommissioningClient;

    private final Function<String, String> rename = (source) -> source.replace("dpu_commissioning", "dpu_com");

    private PostgreSqlDatabase db;

    @BeforeClass
    public void init() {

        dpuCommissioningClient = new DpuCommissioningClient();
        WiremockRobot wiremockRobot = new WiremockRobot();
        wiremockRobot.initializeWiremock(new File(getClass().getResource("/team/morpheus/wiremock").getFile()));

        JDBCConnectionProperties properties = JDBCConnectionPropertiesFactory.get("dpu-commissioning");
        properties.setPassword("dpu_com");
        properties.setUsername("dpu_com");

        properties.setUri(rename.apply(properties.getUri()));

        db = new PostgreSqlDatabase(properties);

    }

    @AfterClass
    public void afterTests() {
        db.close();
    }

    @Test
    public void dpuCommissioningTest() throws SQLException, InterruptedException {
        String endSZ = "49/8571/0/73GA";

        StartDpuCommissioningRequest dpuCommissioningRequest = new StartDpuCommissioningRequest();
        dpuCommissioningRequest.setEndSZ(endSZ);

        DpuCommissioningResponse response = dpuCommissioningClient.getClient().dpuCommissioning().startDpuDeviceCommissioning()
                .body(dpuCommissioningRequest)
                .xB3ParentSpanIdHeader("1")
                .xB3TraceIdHeader("2")
                .xBusinessContextHeader("3")
                .xB3SpanIdHeader("4")
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        String processId = response.getProcessId();

        Assert.assertTrue(response.getComment().contains("SEAL-Interface is CALLED : PROCESS WAITS FOR CALLBACK"));
        Assert.assertEquals(response.getStatus(), "WAIT");

        String sqlGetProcessState =
                "SELECT processstate FROM businessprocess where processid =" + "'" + processId + "'";
        ResultSet rs = db.executeWithResultSet(sqlGetProcessState);

        while (rs.next()) {
            String processstate = rs.getString("processstate");
            Assert.assertEquals(processstate, "WAIT");
        }

        Thread.sleep(10000);

        rs = db.executeWithResultSet(sqlGetProcessState);

        while (rs.next()) {
            String processstate = rs.getString("processstate");
            Assert.assertEquals(processstate, "CLOSED");
        }
    }

    @Test
    public void dpuCommissioningTestSealError() throws SQLException, InterruptedException {
        String endSZ = "49/8571/0/74GA";

        StartDpuCommissioningRequest dpuCommissioningRequest = new StartDpuCommissioningRequest();
        dpuCommissioningRequest.setEndSZ(endSZ);

        DpuCommissioningResponse response = dpuCommissioningClient.getClient().dpuCommissioning().startDpuDeviceCommissioning()
                .body(dpuCommissioningRequest)
                .xB3ParentSpanIdHeader("1")
                .xB3TraceIdHeader("2")
                .xBusinessContextHeader("3")
                .xB3SpanIdHeader("4")
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_INTERNAL_SERVER_ERROR_500)));

        String processId = response.getProcessId();

        Assert.assertTrue(response.getComment().contains("Error while Call SEAL.createDpuConfiguration :[404 Not Found]"));
        Assert.assertEquals(response.getStatus(), "ERROR");

        Thread.sleep(10000);

        String sqlGetProcessState =
                "SELECT processstate FROM businessprocess where processid =" + "'" + processId + "'";
        ResultSet rs = db.executeWithResultSet(sqlGetProcessState);

        while (rs.next()) {
            String processstate = rs.getString("processstate");
            Assert.assertEquals(processstate, "CLOSED");
        }
    }

    @Test
    public void setDpuCommissioningTestError() {
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

        Assert.assertTrue(response.getComment().contains("Error while Call Inventory.findDeviceByCriteria :[400 Bad Request]"));
        Assert.assertEquals(response.getStatus(), "ERROR");
    }
}
