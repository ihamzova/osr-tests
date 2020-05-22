package com.tsystems.tm.acc.ta.team.morpheus.dpucommissioning;

import com.tsystems.tm.acc.data.osr.models.jdbcconnectionproperties.JDBCConnectionPropertiesCase;
import com.tsystems.tm.acc.ta.api.osr.DpuCommissioningClient;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.db.sql.JDBCConnectionProperties;
import com.tsystems.tm.acc.ta.db.sql.SqlDatabase;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.DpuCommissioningRobot;
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
import static com.tsystems.tm.acc.ta.data.osr.models.DpuActivities.*;
import static com.tsystems.tm.acc.ta.team.upiter.common.CommonTestData.HTTP_CODE_CREATED_201;

public class DpuCommissioning extends ApiTest {

    private DpuCommissioningClient dpuCommissioningClient;
    private final Function<String, String> rename = (source) -> source.replace("dpu_commissioning", "dpu_com");
    private static final String ENDSZ_WITHOUT_ERRORS = "49/8571/0/71GA";
    private static final String ENDSZ_400_RI = "49/8571/0/72GA";
    private static final String ENDSZ_404_SEAL = "49/8571/0/74GA";
    private static final String ENDSZ_400_PON = "49/8571/0/71GB";
    private static final String ENDSZ_400_ONUID = "49/8571/0/71GD";
    private static final String ENDSZ_400_BACKHAULID = "49/8571/0/71GC";
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

        DpuCommissioningRobot dpuCommissioningRobot = new DpuCommissioningRobot();
        dpuCommissioningRobot.checkGetDeviceDPU(0L, "49/8571/0/71GA");


//        String processId = response.getId();
//
//        String sqlGetProcessState =
//                "SELECT act_id_, end_time_ FROM act_hi_actinst where proc_inst_id_ =" + "'" + processId + "'";
//        ResultSet rs = db.executeWithResultSet(sqlGetProcessState);
//
//        while (rs.next()) {
//            String processstate = rs.getString("end_time_");
//            if(rs.getString("act_id_").equals(CONFIGURE_DPU_SEAL)){
//                Assert.assertNull(processstate);
//            }else{
//                Assert.assertNotNull(processstate);
//            }
//        }
//
//        Thread.sleep(10000);
//
//        rs = db.executeWithResultSet(sqlGetProcessState);
//
//        while (rs.next()) {
//            String processstate = rs.getString("end_time_");
//            Assert.assertNotNull(processstate);
//        }
//
//        //This part should be moved to separate method, because incidents created async after several time
//        //when process is ended
//        String sqlGetIncidents =
//                "SELECT activity_id_ FROM act_hi_incident where proc_inst_id_ =" + "'" + processId + "'";
//        ResultSet rsInc = db.executeWithResultSet(sqlGetIncidents);
//        while (rsInc.next()) {
//        Assert.assertNull(rsInc, "Incident on task " + rsInc.getString("activity_id_"));
//        }
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

        //This part should be moved to separate method, because incidents created async after several time
        //when process is ended
        //Assert.assertTrue(rsInc.next());
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

        //This part should be moved to separate method, because incidents created async after several time
        //when process is ended
        //Assert.assertTrue(rsInc.next());
        while (rsInc.next()) {
            String activity = rsInc.getString("activity_id_");
            Assert.assertEquals(GET_DPU, activity);
        }
    }

    @Test(description = "Negative case. GET DpuPonConn returned 400")
    @Description("Negative case. GET DpuPonConn returned 400")
    public void setDpuCommissioningTestGetPonError() throws SQLException {

        StartDpuCommissioningRequest dpuCommissioningRequest = new StartDpuCommissioningRequest();
        dpuCommissioningRequest.setEndSZ(ENDSZ_400_PON);

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
            Assert.assertNotEquals(GET_ETHLINK, processstate);
        }

        //This part should be moved to separate method, because incidents created async after several time
        //when process is ended
        String sqlGetIncidents =
                "SELECT activity_id_ FROM act_hi_incident where proc_inst_id_ =" + "'" + processId + "'";
        ResultSet rsInc = db.executeWithResultSet(sqlGetIncidents);
        //Assert.assertTrue(rsInc.next());
        while (rsInc.next()) {
            String activity = rsInc.getString("activity_id_");
            Assert.assertEquals(GET_LLC, activity);
        }
    }

    @Test(description = "Negative case. GET onuid returned 400")
    @Description("Negative case. GET onuid returned 400")
    public void setDpuCommissioningTestGetOnuidError() throws SQLException {

        StartDpuCommissioningRequest dpuCommissioningRequest = new StartDpuCommissioningRequest();
        dpuCommissioningRequest.setEndSZ(ENDSZ_400_ONUID);

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
            Assert.assertNotEquals(GET_BACKHAUL, processstate);
        }

        //This part should be moved to separate method, because incidents created async after several time
        //when process is ended
        String sqlGetIncidents =
                "SELECT activity_id_ FROM act_hi_incident where proc_inst_id_ =" + "'" + processId + "'";
        ResultSet rsInc = db.executeWithResultSet(sqlGetIncidents);
        //Assert.assertTrue(rsInc.next());
        while (rsInc.next()) {
            String activity = rsInc.getString("activity_id_");
            Assert.assertEquals(GET_ONUID, activity);
        }
    }
    @Test(description = "Negative case. GET backhaul returned 400")
    @Description("Negative case. GET backhaul returned 400")
    public void setDpuCommissioningTestGetBachhaulError() throws SQLException {

        StartDpuCommissioningRequest dpuCommissioningRequest = new StartDpuCommissioningRequest();
        dpuCommissioningRequest.setEndSZ(ENDSZ_400_BACKHAULID);

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
            Assert.assertNotEquals(DEPROVISION_OLT, processstate);
        }

        //This part should be moved to separate method, because incidents created async after several time
        //when process is ended
        String sqlGetIncidents =
                "SELECT activity_id_ FROM act_hi_incident where proc_inst_id_ =" + "'" + processId + "'";
        ResultSet rsInc = db.executeWithResultSet(sqlGetIncidents);
        //Assert.assertTrue(rsInc.next());
        while (rsInc.next()) {
            String activity = rsInc.getString("activity_id_");
            Assert.assertEquals(GET_BACKHAUL, activity);
        }
    }


    @Test
    public void WiremockTest(){
//        WiremockRecordedRequestRetriver wiremockRecordedRequestRetriver = new WiremockRecordedRequestRetriver();
//        List<RequestFind> requests = wiremockRecordedRequestRetriver.retrieveLastRequest(1);

        Long timeOfExecution = System.currentTimeMillis();
        DpuCommissioningRobot dpuCommissioningRobot = new DpuCommissioningRobot();
        dpuCommissioningRobot.checkGetDeviceDPU(timeOfExecution, "49/8571/0/71GA");

    }
}
