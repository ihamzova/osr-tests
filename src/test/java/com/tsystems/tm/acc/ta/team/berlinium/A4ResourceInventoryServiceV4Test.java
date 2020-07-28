package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofileftthaccess.A4NetworkServiceProfileFtthAccessCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.ta.data.osr.models.A4TerminationPoint;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkServiceProfileFtthAccess;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceV4Robot;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class A4ResourceInventoryServiceV4Test extends ApiTest {
    private OsrTestContext osrTestContext = OsrTestContext.get();
    private A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private A4ResourceInventoryServiceV4Robot a4ResourceInventoryServiceV4Robot = new A4ResourceInventoryServiceV4Robot();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData;
    private A4NetworkElementPort nepData;
    private A4TerminationPoint tpData, tpData2;
    private A4NetworkServiceProfileFtthAccess nspData, nspData2;
    private List<A4NetworkServiceProfileFtthAccess> nspDataList;


    @BeforeClass
    public void init() {
        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        neData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        tpData = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPoint);
        tpData2 = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.TerminationPointB);
        nepData = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);

        nspData = osrTestContext.getData().getA4NetworkServiceProfileFtthAccessDataProvider()
                .get(A4NetworkServiceProfileFtthAccessCase.defaultNetworkServiceProfileFtthAccess);

        nspData2 = osrTestContext.getData().getA4NetworkServiceProfileFtthAccessDataProvider()
                .get(A4NetworkServiceProfileFtthAccessCase.NetworkServiceProfileFtthAccessB);

        // Ensure that no old test data is in the way
        a4ResourceInventoryRobot.deleteA4NetworkElementsIncludingChildren(neData);
        a4ResourceInventoryRobot.deleteNetworkElementGroups(negData);

    }

    @BeforeMethod
    public void setup() {
        a4ResourceInventoryRobot.createNetworkElementGroup(negData);
        a4ResourceInventoryRobot.createNetworkElement(neData, negData);
        a4ResourceInventoryRobot.createNetworkElementPort(nepData, neData);
        a4ResourceInventoryRobot.createTerminationPoint(tpData, nepData);
        a4ResourceInventoryRobot.createNetworkServiceProfileFtthAccess(nspData, tpData);

        a4ResourceInventoryRobot.createNetworkServiceProfileFtthAccess(nspData2, tpData2);
    }

    @AfterMethod
    public void cleanup() {
        a4ResourceInventoryRobot.deleteA4NetworkElementsIncludingChildren(neData);
        a4ResourceInventoryRobot.deleteNetworkElementGroups(negData);
        //a4ResourceInventoryRobot.deleteNetworkElementPort(nepData.getUuid());
        //a4ResourceInventoryRobot.deleteTerminationPoint(tpData.getUuid());
    }

    @Test(description = "DIGIHUB-xxxxx Read network element from resource inventory service v4 api")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Read network element from resource inventory service v4 api")
    public void test_readNetworkElementFromA4Api() {
        // GIVEN

        // WHEN

        // THEN
        a4ResourceInventoryServiceV4Robot.checkIfNetworkElementExists(neData);
    }

    @Test(description = "DIGIHUB-67987 Read network element group from resource inventory service v4 api")
    @Owner("thea.john@telekom.de")
    @TmsLink("DIGIHUB-67987")
    @Description("Read network element group from resource inventory service v4 api")
    public void test_readNetworkElementGroupFromA4Api() {
        // GIVEN

        // WHEN

        // THEN
        a4ResourceInventoryServiceV4Robot.checkIfNetworkElementGroupExistsByName(negData);
        a4ResourceInventoryServiceV4Robot.checkIfNetworkElementGroupExistsByUuid(negData);
        a4ResourceInventoryServiceV4Robot.checkNotFoundErrorForNonExistendNeg();
    }

    @Test(description = "DIGIHUB-xxx Read termination point from resource inventory service v4 api")
    @Owner("thea.john@telekom.de")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read terminationPoint from resource inventory service v4 api")
    public void test_readTerminationPointFromA4Api() {
        // GIVEN

        // WHEN

        // THEN
        a4ResourceInventoryServiceV4Robot.checkIfTerminationPointExists(tpData);
        a4ResourceInventoryServiceV4Robot.checkIfTerminationPointExistsByPort(tpData, nepData);
    }

    @Test(description = "DIGIHUB-xxx Read  Network Service Profile Ftth Access from resource inventory service v4 api")
    @Owner("juergen.mayer@t-systems.com")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read Network Service Profile Ftth Access from resource inventory service v4 api")
    public void test_readNetworkServiceProfileFtthAccessFromA4Api() {
        // GIVEN

        // WHEN

        // THEN
        a4ResourceInventoryServiceV4Robot.checkIfNetworkServiceProfileFtthAccessExists(nspData);
    }

    @Test(description = "DIGIHUB-xxx Read  Network Service Profile Ftth Access from resource inventory service v4 api")
    @Owner("juergen.mayer@t-systems.com")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read Network Service Profile Ftth Access from resource inventory service v4 api")
    public void test_readNetworkServiceProfileFtthAccessFromA4ApiByOnSerialNumber() {
        // GIVEN

        // WHEN

        // THEN
        a4ResourceInventoryServiceV4Robot.checkIfNetworkServiceProfilesFtthAccessExistsByOntSerialNumber(nspDataList);
    }




}
