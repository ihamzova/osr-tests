package com.tsystems.tm.acc.ta.team.morpheus.dpucommissioning;

import com.tsystems.tm.acc.data.osr.models.jdbcconnectionproperties.JDBCConnectionPropertiesCase;
import com.tsystems.tm.acc.ta.api.osr.DpuCommissioningClient;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.db.sql.JDBCConnectionProperties;
import com.tsystems.tm.acc.ta.db.sql.SqlDatabase;
import com.tsystems.tm.acc.ta.db.sql.strategies.jdbc.postgres.PostgreSqlJDBCConnectionPropertiesFactory;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.WiremockRobot;
import com.tsystems.tm.acc.tests.osr.dpu.commissioning.model.DpuCommissioningResponse;
import com.tsystems.tm.acc.tests.osr.dpu.commissioning.model.StartDpuCommissioningRequest;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
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
import static com.tsystems.tm.acc.ta.team.morpheus.common.Activities.CONFIGURE_DPU_SEAL;
import static com.tsystems.tm.acc.ta.team.morpheus.common.Activities.GET_LLC;
import static com.tsystems.tm.acc.ta.team.morpheus.common.Activities.UPDATE_INV;
import static com.tsystems.tm.acc.ta.team.upiter.common.CommonTestData.HTTP_CODE_CREATED_201;

public class DpuCommissioning extends ApiTest {

    private DpuCommissioningClient dpuCommissioningClient;
    private final Function<String, String> rename = (source) -> source.replace("dpu_commissioning", "dpu_com");
    private static final String ENDSZ_WITHOUT_ERRORS = "49/8571/0/71GA";
    private static final String ENDSZ_400_RI = "49/8571/0/72GA";
    private static final String ENDSZ_404_SEAL = "49/8571/0/74GA";
    private SqlDatabase db;

    @BeforeClass
    public void init() {

        dpuCommissioningClient = new DpuCommissioningClient();
        WiremockRobot wiremockRobot = new WiremockRobot();
        wiremockRobot.initializeWiremock(new File(getClass().getResource("/team/morpheus/wiremock").getFile()));

        OsrTestContext context = OsrTestContext.get();
        JDBCConnectionProperties connectionProperties = context.getData().getJDBCConnectionPropertiesDataProvider().get(JDBCConnectionPropertiesCase.JDBCDpuCommissioning);
        db = new SqlDatabase(connectionProperties);

    }

    @AfterClass
    public void afterTests() {
        db.close();
    }

    @Test(description = "Positive case. DPU-commisioning without errors")
    @TmsLink("DIGIHUB-62083")
    @Description("Positive case. DPU-commisioning without errors")
    public void dpuCommissioningTest() throws SQLException, InterruptedException {

        StartDpuCommissioningRequest dpuCommissioningRequest = new StartDpuCommissioningRequest();
        dpuCommissioningRequest.setEndSZ(ENDSZ_WITHOUT_ERRORS);

        DpuCommissioningResponse response = dpuCommissioningClient.getClient().dpuCommissioning().startDpuDeviceCommissioning()
                .body(dpuCommissioningRequest)
                .xB3ParentSpanIdHeader("1")
                .xB3TraceIdHeader("2")
                .xBusinessContextHeader("3")
                .xB3SpanIdHeader("4")
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        String processId = response.getId();

        String sqlGetProcessState =
                "SELECT act_id_, end_time_ FROM act_hi_actinst where proc_inst_id_ =" + "'" + processId + "'";
        ResultSet rs = db.executeWithResultSet(sqlGetProcessState);

        while (rs.next()) {
            String processstate = rs.getString("end_time_");
            if(rs.getString("act_id_").equals(CONFIGURE_DPU_SEAL)){
                Assert.assertNull(processstate);
            }else{
                Assert.assertNotNull(processstate);
            }
        }

        Thread.sleep(10000);

        rs = db.executeWithResultSet(sqlGetProcessState);

        while (rs.next()) {
            String processstate = rs.getString("end_time_");
            Assert.assertNotNull(processstate);
        }

        String sqlGetIncidents =
                "SELECT activity_id_ FROM act_hi_incident where proc_inst_id_ =" + "'" + processId + "'";
        ResultSet rsInc = db.executeWithResultSet(sqlGetIncidents);
        while (rsInc.next()) {
        Assert.assertNull(rsInc, "Incident on task " + rsInc.getString("activity_id_"));
        }
    }

    @Test(description = "Negative case. POST dpuConfigurations on SEAL returned 404")
    @Description("Negative case. POST dpuConfigurations on SEAL returned 404")
    public void dpuCommissioningTestSealError() throws SQLException, InterruptedException {

        StartDpuCommissioningRequest dpuCommissioningRequest = new StartDpuCommissioningRequest();
        dpuCommissioningRequest.setEndSZ(ENDSZ_404_SEAL);

        DpuCommissioningResponse response = dpuCommissioningClient.getClient().dpuCommissioning().startDpuDeviceCommissioning()
                .body(dpuCommissioningRequest)
                .xB3ParentSpanIdHeader("1")
                .xB3TraceIdHeader("2")
                .xBusinessContextHeader("3")
                .xB3SpanIdHeader("4")
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        String processId = response.getId();

        Thread.sleep(10000);

        String sqlGetProcessState =
                "SELECT act_id_, end_time_ FROM act_hi_actinst where proc_inst_id_ =" + "'" + processId + "'";
        ResultSet rs = db.executeWithResultSet(sqlGetProcessState);

        while (rs.next()) {
            String processstate = rs.getString("act_id_");
            Assert.assertNotEquals(UPDATE_INV, processstate);
        }
        //Temporary workaround till valid error handling will be implemented: here we check, that
        //incident was created and process was interrupted on step Activity_SEAL.POST.DpuConf

        String sqlGetIncidents =
                "SELECT activity_id_ FROM act_hi_incident where proc_inst_id_ =" + "'" + processId + "'";
        ResultSet rsInc = db.executeWithResultSet(sqlGetIncidents);
        while (rs.next()) {
            String incident = rsInc.getString("activity_id_");
            Assert.assertNotNull(incident);
        }
    }

    @Test(description = "Negative case. GET oltResourceInventory returned 400")
    @Description("Negative case. GET oltResourceInventory returned 400")
    public void setDpuCommissioningTestError() throws SQLException {

        StartDpuCommissioningRequest dpuCommissioningRequest = new StartDpuCommissioningRequest();
        dpuCommissioningRequest.setEndSZ(ENDSZ_400_RI);

        DpuCommissioningResponse response = dpuCommissioningClient.getClient().dpuCommissioning().startDpuDeviceCommissioning()
                .body(dpuCommissioningRequest)
                .xB3ParentSpanIdHeader("1")
                .xB3TraceIdHeader("2")
                .xBusinessContextHeader("3")
                .xB3SpanIdHeader("4")
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        String processId = response.getId();

        //Temporary workaround till valid error handling will be implemented: here we check, that
        //incident was created and process was interrupted on step Activity_OLT-RI.GET.DeviceDPU
        String sqlGetProcessState =
                "SELECT act_id_, end_time_ FROM act_hi_actinst where proc_inst_id_ =" + "'" + processId + "'";
        ResultSet rs = db.executeWithResultSet(sqlGetProcessState);

        while (rs.next()) {
            String processstate = rs.getString("act_id_");
            Assert.assertNotEquals(GET_LLC, processstate);
        }

        String sqlGetIncidents =
                "SELECT activity_id_ FROM act_hi_incident where proc_inst_id_ =" + "'" + processId + "'";
        ResultSet rsInc = db.executeWithResultSet(sqlGetIncidents);
        while (rsInc.next()) {
            String incident = rsInc.getString("activity_id_");
            Assert.assertNotNull(incident);
        }
    }
}
