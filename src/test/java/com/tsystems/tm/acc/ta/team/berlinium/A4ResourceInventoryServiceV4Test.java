package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementlink.A4NetworkElementLinkCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofileftthaccess.A4NetworkServiceProfileFtthAccessCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceV4Robot;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.*;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_MS;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_SERVICE_MS;

@ServiceLog(A4_RESOURCE_INVENTORY_MS)
@ServiceLog(A4_RESOURCE_INVENTORY_SERVICE_MS)
public class A4ResourceInventoryServiceV4Test extends ApiTest {
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private final A4ResourceInventoryServiceV4Robot a4ResourceInventoryServiceV4Robot = new A4ResourceInventoryServiceV4Robot();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neDataA;
    private A4NetworkElement neDataB;
    private A4NetworkElementPort nepDataA;
    private A4NetworkElementPort nepDataB;
    private A4TerminationPoint tpDataA;
    private A4TerminationPoint tpDataB;
    private A4NetworkElementLink nelData;
    private A4NetworkServiceProfileFtthAccess nspDataA;
    private A4NetworkServiceProfileFtthAccess nspDataB;

    @BeforeClass
    public void init() {
        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        neDataA = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        neDataB = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementB);
        tpDataA = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointFtthAccess);
        tpDataB = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.TerminationPointB);
        nepDataA = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
        nepDataB = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_10G_001);
        nelData = osrTestContext.getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.defaultNetworkElementLink);
        nspDataA = osrTestContext.getData().getA4NetworkServiceProfileFtthAccessDataProvider()
                .get(A4NetworkServiceProfileFtthAccessCase.defaultNetworkServiceProfileFtthAccess);
        nspDataB = osrTestContext.getData().getA4NetworkServiceProfileFtthAccessDataProvider()
                .get(A4NetworkServiceProfileFtthAccessCase.NetworkServiceProfileFtthAccessB);

        // Ensure that no old test data is in the way
        tearDown();

        // Set up test data. Needs only to be done once, because all test cases here are read-only-requests (not necessary to use @BeforeMethod)
        a4ResourceInventoryRobot.createNetworkElementGroup(negData);
        a4ResourceInventoryRobot.createNetworkElement(neDataA, negData);
        a4ResourceInventoryRobot.createNetworkElement(neDataB, negData);
        a4ResourceInventoryRobot.createNetworkElementPort(nepDataA, neDataA);
        a4ResourceInventoryRobot.createNetworkElementPort(nepDataB, neDataB);
        a4ResourceInventoryRobot.createTerminationPoint(tpDataA, nepDataA);
        a4ResourceInventoryRobot.createTerminationPoint(tpDataB, nepDataB);
        a4ResourceInventoryRobot.createNetworkElementLink(nelData, nepDataA, nepDataB);
        a4ResourceInventoryRobot.createNetworkServiceProfileFtthAccess(nspDataA, tpDataA);
        a4ResourceInventoryRobot.createNetworkServiceProfileFtthAccess(nspDataB, tpDataB);
    }

    @AfterClass
    public void tearDown() {
        a4ResourceInventoryRobot.deleteA4NetworkElementsIncludingChildren(neDataA);
        a4ResourceInventoryRobot.deleteA4NetworkElementsIncludingChildren(neDataB);
        a4ResourceInventoryRobot.deleteNetworkElementGroups(negData);
    }

    @Test(description = "DIGIHUB-xxx Read network element group from resource inventory service v4 api")
    @Owner("thea.john@telekom.de")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read network element group from resource inventory service v4 api")
    public void readNetworkElementGroupFromA4Api() {
        a4ResourceInventoryServiceV4Robot.checkIfNetworkElementGroupExistsByUuid(negData);
    }

    @Test(description = "DIGIHUB-xxx Read network element group by name from resource inventory service v4 api")
    @Owner("thea.john@telekom.de")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read network element group from resource inventory service v4 api")
    public void readNetworkElementGroupFromA4ApiByName() {
        a4ResourceInventoryServiceV4Robot.checkIfNetworkElementGroupExistsByName(negData);
    }

    @Test(description = "DIGIHUB-xxx Read network element group from resource inventory service v4 api - Not Found")
    @Owner("thea.john@telekom.de")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read network element group from resource inventory service v4 api")
    public void readNetworkElementGroupFromA4ApiNotFound() {
        a4ResourceInventoryServiceV4Robot.checkNotFoundErrorForNonExistingNeg();
    }

    @Test(description = "DIGIHUB-xxxxx Read network element from resource inventory service v4 api")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Read network element from resource inventory service v4 api")
    public void readNetworkElementFromA4ApiByEndsz() {
        a4ResourceInventoryServiceV4Robot.checkIfNetworkElementExistsByUuid(neDataB);
    }

    @Test(description = "DIGIHUB-xxx Read termination point from resource inventory service v4 api")
    @Owner("thea.john@telekom.de")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read terminationPoint from resource inventory service v4 api")
    public void readTerminationPointFromA4Api() {
        a4ResourceInventoryServiceV4Robot.checkIfAnyTerminationPointsExist(2);
    }

    @Test(description = "DIGIHUB-xxx Find termination point by Port from resource inventory service v4 api")
    @Owner("thea.john@telekom.de")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read terminationPoint from resource inventory service v4 api")
    public void readTerminationPointFromA4ApiByPort() {
        a4ResourceInventoryServiceV4Robot.checkIfTerminationPointExistsByPort(tpDataA, nepDataA);
    }

    @Test(description = "DIGIHUB-xxx Read networkElementLink from resource inventory service v4 api")
    @Owner("thea.john@telekom.de")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read networkElementLink from resource inventory service v4 api")
    public void test_readNetworkElementLinkFromA4Api() {
        a4ResourceInventoryServiceV4Robot.checkIfAnyNetworkElementLinksExist(1);
    }

    @Test(description = "DIGIHUB-xxx Read networkElementLink by lbz from resource inventory service v4 api")
    @Owner("thea.john@telekom.de")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read networkElementLink from resource inventory service v4 api")
    public void readNetworkElementLinkFromA4ApiByLbz() {
        a4ResourceInventoryServiceV4Robot.checkIfNetworkElementLinkExistsByLbz(nelData);
    }

    @Test(description = "DIGIHUB-xxx Read Network Service Profile Ftth Access from resource inventory service v4 api")
    @Owner("juergen.mayer@t-systems.com")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read Network Service Profile Ftth Access from resource inventory service v4 api")
    public void test_readNetworkServiceProfileFtthAccessFromA4Api() {
        a4ResourceInventoryServiceV4Robot.checkIfAnyNetworkServiceProfileFtthAccessesExist(2);
    }

    @Test(description = "DIGIHUB-xxx Read Network Service Profile Ftth Access from resource inventory service v4 api")
    @Owner("juergen.mayer@t-systems.com")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read Network Service Profile Ftth Access from resource inventory service v4 api")
    public void readNetworkServiceProfileFtthAccessFromA4ApiByLineId() {
        a4ResourceInventoryServiceV4Robot.checkIfNetworkServiceProfileFtthAccessExistsByLineId(nspDataA);
    }

    @Test(description = "DIGIHUB-xxx Read Network Service Profile Ftth Access by ontSerialNumber from resource inventory service v4 api")
    @Owner("juergen.mayer@t-systems.com")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read Network Service Profile Ftth Access from resource inventory service v4 api")
    public void readNetworkServiceProfileFtthAccessFromA4ApiByOnSerialNumber() {
        a4ResourceInventoryServiceV4Robot.checkIfNetworkServiceProfileFtthAccessExistsByOntSerialNumber(nspDataA);
    }

    @Test(description = "DIGIHUB-xxx Read Network Element Port by Endsz from resource inventory service v4 api")
    @Owner("juergen.mayer@t-systems.com")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read Network Element Port by Endsz from resource inventory service v4 api")
    public void getNetworkElementPortsByNetworkElementEndsz() {
        a4ResourceInventoryServiceV4Robot.checkIfNetworkElementPortExistsByEndsz(neDataA, nepDataA);
    }

    @Test(description = "DIGIHUB-xxx Read Network Element Port by Endsz and functional port label from resource inventory service v4 api")
    @Owner("juergen.mayer@t-systems.com")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read Network Element Port by Endsz and functional port label from resource inventory service v4 api")
    public void getNetworkElementPortsByNetworkElementEndszAndFunctionalPortLabel() {
        a4ResourceInventoryServiceV4Robot.checkIfNetworkElementPortExistsByEndszAndFunctionPortLabel(neDataA, nepDataA);
    }

    @Test(description = "DIGIHUB-xxx Read Network Element Port by Endsz and type port label from resource inventory service v4 api")
    @Owner("juergen.mayer@t-systems.com")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read Network Element Port by Endsz and type from resource inventory service v4 api")
    public void getNetworkElementPortsByNetworkElementEndszAndType() {
        a4ResourceInventoryServiceV4Robot.checkIfNetworkElementPortExistsByEndszAndType(neDataA, nepDataA);
    }

    @Test(description = "DIGIHUB-xxx Read Network Element Port by Endsz and type and port number from resource inventory service v4 api")
    @Owner("juergen.mayer@t-systems.com")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read Network Element Port by Endsz and type port label from resource inventory service v4 api")
    public void getNetworkElementPortsByNetworkElementEndszAndTypeAndPortnumber() {
        a4ResourceInventoryServiceV4Robot.checkIfNetworkElementPortsExistByEndszAndTypeAndPortnumber(neDataA, nepDataA);
    }

    @Test(description = "DIGIHUB-xxx Read Network Element Port by NetworkElement Uuid from resource inventory service v4 api")
    @Owner("juergen.mayer@t-systems.com")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read Network Element Port by NetworkElement Uuid from resource inventory service v4 api")
    public void getNetworkElementPortsByNetworkElementUuid() {
        // GIVEN
        a4ResourceInventoryServiceV4Robot.checkIfNetworkElementPortExistsByNetworkUuid(neDataA, nepDataA);
    }

}
