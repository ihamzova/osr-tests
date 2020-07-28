package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementlink.A4NetworkElementLinkCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.data.osr.models.*;
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

public class A4ResourceInventoryServiceV4Test extends ApiTest {
    private OsrTestContext osrTestContext = OsrTestContext.get();
    private A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private A4ResourceInventoryServiceV4Robot a4ResourceInventoryServiceV4Robot = new A4ResourceInventoryServiceV4Robot();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData;
    private A4NetworkElementPort nepDataA;
    private A4NetworkElementPort nepDataB;
    private A4TerminationPoint tpData;
    private A4NetworkElementLink nelData;


    @BeforeClass
    public void init() {
        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        neData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        tpData = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPoint);
        nepDataA = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
        nepDataB = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_10G_001);
        nelData = osrTestContext.getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.defaultNetworkElementLink);

        // Ensure that no old test data is in the way
        a4ResourceInventoryRobot.deleteA4NetworkElementsIncludingChildren(neData);
        a4ResourceInventoryRobot.deleteNetworkElementGroups(negData);

    }

    @BeforeMethod
    public void setup() {
        a4ResourceInventoryRobot.createNetworkElementGroup(negData);
        a4ResourceInventoryRobot.createNetworkElement(neData, negData);
        a4ResourceInventoryRobot.createNetworkElementPort(nepDataA, neData);
        a4ResourceInventoryRobot.createTerminationPoint(tpData, nepDataA);
        a4ResourceInventoryRobot.createNetworkElementLink(nelData, nepDataA, nepDataB);
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

    @Test(description = "DIGIHUB-xxx Read network element group from resource inventory service v4 api")
    @Owner("thea.john@telekom.de")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read network element group from resource inventory service v4 api")
    public void test_readNetworkElementGroupFromA4Api() {
        a4ResourceInventoryServiceV4Robot.checkIfNetworkElementGroupExistsByUuid(negData);
    }

    @Test(description = "DIGIHUB-xxx Read network element group by name from resource inventory service v4 api")
    @Owner("thea.john@telekom.de")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read network element group from resource inventory service v4 api")
    public void test_readNetworkElementGroupFromA4ApiByName() {
        a4ResourceInventoryServiceV4Robot.checkIfNetworkElementGroupExistsByName(negData);
    }

    @Test(description = "DIGIHUB-xxx Read network element group from resource inventory service v4 api - Not Found")
    @Owner("thea.john@telekom.de")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read network element group from resource inventory service v4 api")
    public void test_readNetworkElementGroupFromA4ApiNotFound() {
        a4ResourceInventoryServiceV4Robot.checkNotFoundErrorForNonExistendNeg();
    }



    @Test(description = "DIGIHUB-xxx Read termination point from resource inventory service v4 api")
    @Owner("thea.john@telekom.de")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read terminationPoint from resource inventory service v4 api")
    public void test_readTerminationPointFromA4Api() {
        a4ResourceInventoryServiceV4Robot.checkIfTerminationPointExists(tpData);
    }

    @Test(description = "DIGIHUB-xxx Find termination point by Port from resource inventory service v4 api")
    @Owner("thea.john@telekom.de")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read terminationPoint from resource inventory service v4 api")
    public void test_readTerminationPointFromA4ApiByPort() {
        a4ResourceInventoryServiceV4Robot.checkIfTerminationPointExistsByPort(tpData, nepDataA);
    }



    @Test(description = "DIGIHUB-xxx Read networkElementLink from resource inventory service v4 api")
    @Owner("thea.john@telekom.de")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read networkElementLink from resource inventory service v4 api")
    public void test_readNetworkElementLinkFromA4Api() {
        a4ResourceInventoryServiceV4Robot.checkIfNetworkElementLinkExists(nelData);
    }

    @Test(description = "DIGIHUB-xxx Read networkElementLink by lbz from resource inventory service v4 api")
    @Owner("thea.john@telekom.de")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read networkElementLink from resource inventory service v4 api")
    public void test_readNetworkElementLinkFromA4ApiByLbz() {
        a4ResourceInventoryServiceV4Robot.checkIfNetworkElementLinkExistsByLbz(nelData);
    }

}
