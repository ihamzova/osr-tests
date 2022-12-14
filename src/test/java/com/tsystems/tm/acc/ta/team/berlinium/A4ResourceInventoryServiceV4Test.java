package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementlink.A4NetworkElementLinkCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilea10nsp.A4NetworkServiceProfileA10NspCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofileftthaccess.A4NetworkServiceProfileFtthAccessCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceV4Robot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.v4.client.model.TerminationPoint;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getRandomDigits;
import static org.testng.Assert.assertEquals;


@ServiceLog({A4_RESOURCE_INVENTORY_MS, A4_RESOURCE_INVENTORY_SERVICE_MS})
@Epic("OS&R domain")

public class A4ResourceInventoryServiceV4Test extends GigabitTest {

    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private final A4ResourceInventoryServiceV4Robot a4ResourceInventoryServiceV4Robot = new A4ResourceInventoryServiceV4Robot();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neDataA;
    private A4NetworkElement neDataB;
    private A4NetworkElementPort nepDataA;
    private A4NetworkElementPort nepDataB;
    private A4NetworkElementPort nepDataC;
    private A4TerminationPoint tpDataA;
    private A4NetworkElementLink nelData;
    private A4NetworkServiceProfileFtthAccess nspFtthDataA;
    private A4NetworkServiceProfileFtthAccess nspFtthDataB;
    private A4NetworkServiceProfileFtthAccess nspFtthDataC;

    private A4NetworkServiceProfileA10Nsp nspA10DataA;

    @BeforeClass
    public void init() {
        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        neDataA = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        neDataB = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementB);
        tpDataA = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointL2Bsa);
        A4TerminationPoint tpDataB = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.TerminationPointB);
        A4TerminationPoint tpDataC = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.terminationPointFtthAccessPrePro);
        A4TerminationPoint tpA10DataA = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointA10Nsp);
        nepDataA = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
        nepDataB = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_10G_001);
        nepDataC = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_100G_001);
        nelData = osrTestContext.getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.defaultNetworkElementLink);
        nspFtthDataA = osrTestContext.getData().getA4NetworkServiceProfileFtthAccessDataProvider()
                .get(A4NetworkServiceProfileFtthAccessCase.defaultNetworkServiceProfileFtthAccess);
        nspFtthDataB = osrTestContext.getData().getA4NetworkServiceProfileFtthAccessDataProvider()
                .get(A4NetworkServiceProfileFtthAccessCase.NetworkServiceProfileFtthAccessB);
        nspFtthDataC = osrTestContext.getData().getA4NetworkServiceProfileFtthAccessDataProvider()
                .get(A4NetworkServiceProfileFtthAccessCase.NetworkServiceProfileFtthAccessL2Bsa);
        nspA10DataA = osrTestContext.getData().getA4NetworkServiceProfileA10NspDataProvider()
                .get(A4NetworkServiceProfileA10NspCase.defaultNetworkServiceProfileA10Nsp);
        nspA10DataA.setItAccountingKey("itAccKey" + getRandomDigits(6));
        nspA10DataA.setNetworkElementLinkUuid("test-nel-uuid" + getRandomDigits(6));

        // Ensure that no old test data is in the way
        tearDown();

        // Set up test data. Needs only to be done once, because all test cases here are read-only-requests (not necessary to use @BeforeMethod)
        a4ResourceInventoryRobot.createNetworkElementGroup(negData);
        a4ResourceInventoryRobot.createNetworkElement(neDataA, negData);
        a4ResourceInventoryRobot.createNetworkElement(neDataB, negData);
        a4ResourceInventoryRobot.createNetworkElementPort(nepDataA, neDataA);
        a4ResourceInventoryRobot.createNetworkElementPort(nepDataB, neDataB);
        a4ResourceInventoryRobot.createNetworkElementPort(nepDataC, neDataB);
        a4ResourceInventoryRobot.createTerminationPoint(tpDataA, nepDataA);
        a4ResourceInventoryRobot.createTerminationPoint(tpDataB, nepDataB);
        a4ResourceInventoryRobot.createTerminationPoint(tpDataC, nepDataB);
        a4ResourceInventoryRobot.createTerminationPoint(tpA10DataA, nepDataC);
        a4ResourceInventoryRobot.createNetworkElementLink(nelData, nepDataA, nepDataB);
        a4ResourceInventoryRobot.createNetworkServiceProfileFtthAccess(nspFtthDataA, tpDataA);
        a4ResourceInventoryRobot.createNetworkServiceProfileFtthAccess(nspFtthDataB, tpDataB);
        a4ResourceInventoryRobot.createNetworkServiceProfileFtthAccessWithPortReference(nspFtthDataC, tpDataC, nepDataB);
        a4ResourceInventoryRobot.createNetworkServiceProfileA10Nsp(nspA10DataA, tpA10DataA);
    }

    @AfterClass
    public void tearDown() {
        // Delete all A4 data which might provoke problems because of unique constraints
        a4ResourceInventoryRobot.deleteA4NetworkElementGroupsRecursively(negData);
        a4ResourceInventoryRobot.deleteA4NetworkElementsRecursively(neDataA);
        a4ResourceInventoryRobot.deleteA4NetworkElementsRecursively(neDataB);
        a4ResourceInventoryRobot.deleteA4NetworkElementPortsRecursively(nepDataA, neDataA);
        a4ResourceInventoryRobot.deleteA4NetworkElementPortsRecursively(nepDataB, neDataB);
        a4ResourceInventoryRobot.deleteA4NetworkElementPortsRecursively(nepDataC, neDataB);
        a4ResourceInventoryRobot.deleteNspFtthAccess(nspFtthDataA);
        a4ResourceInventoryRobot.deleteNspFtthAccess(nspFtthDataB);
        a4ResourceInventoryRobot.deleteNspFtthAccess(nspFtthDataC);
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

    @Test(description = "Find Terminationpoint by Port in a4 resource inventory")
    @Owner("thea.john@telekom.de")
    public void readTerminationPointFromA4ApiByPort() {
        List<TerminationPoint> tpV4UuidList = a4ResourceInventoryServiceV4Robot.checkIfTerminationPointExistsBy(nepDataA.getUuid(), null);
        assertEquals(tpV4UuidList.size(), 1);
        assertEquals(tpV4UuidList.get(0).getId(), tpDataA.getUuid());
    }

    @Test(description = "DIGIHUB-76377 - Find Terminationpoint by carrierBsaReference in a4 resource inventory")
    @Owner("holger.schenke@telekom.de")
    @TmsLink("DIGIHUB-76377")
    public void readTerminationPointFromA4ApiByCBR() {
        List<TerminationPoint> tpV4UuidList = a4ResourceInventoryServiceV4Robot.checkIfTerminationPointExistsBy(null, tpDataA.getCarrierBsaReference());
        System.out.println("+++ tpV4UuidList.size(): " + tpV4UuidList.size());
        assert (tpV4UuidList.size() >= 1);
        assertEquals(tpV4UuidList.get(0).getCarrierBsaReference(), tpDataA.getCarrierBsaReference());
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
    public void readNetworkServiceProfileFtthAccessFromA4ApiAll() {
        a4ResourceInventoryServiceV4Robot.checkIfAnyNetworkServiceProfileFtthAccessesExist(2);
    }

    @Test(description = "DIGIHUB-76376 Read Network Service Profile A10nsp from resource inventory service v4 api")
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read Network Service Profile A10nsp from resource inventory service v4 api")
    public void readNetworkServiceProfileA10NspAll() {
        a4ResourceInventoryServiceV4Robot.checkIfAnyNetworkServiceProfileA10NspExist(1);
    }

    @Test(description = "DIGIHUB-76376 Read Network Service Profile A10nsp from resource inventory service v4 api")
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read Network Service Profile A10nsp from resource inventory service v4 api")
    public void readNetworkServiceProfileA10NspByUuid() {
        a4ResourceInventoryServiceV4Robot.checkIfNetworkServiceProfileA10NspExistsByUuid(nspA10DataA.getUuid());
    }

    @Test(description = "DIGIHUB-76376 Read Network Service Profile A10nsp from resource inventory service v4 api")
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read Network Service Profile A10nsp from resource inventory service v4 api")
    public void readNetworkServiceProfileA10NspByItAccountingKey() {
        a4ResourceInventoryServiceV4Robot.checkIfNetworkServiceProfileA10NspExistsByItAccountingKey(nspA10DataA.getItAccountingKey());
    }

    @Test(description = "DIGIHUB-76376 Read Network Service Profile A10nsp from resource inventory service v4 api")
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read Network Service Profile A10nsp from resource inventory service v4 api")
    public void readNetworkServiceProfileA10NspByNetworkElementLinkUuid() {
        a4ResourceInventoryServiceV4Robot.checkIfNetworkServiceProfileA10NspExistsByNetworkElementLinkUuid(nspA10DataA.getNetworkElementLinkUuid());
    }

    @Test(description = "DIGIHUB-76376 Read Network Service Profile A10nsp from resource inventory service v4 api")
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read Network Service Profile A10nsp from resource inventory service v4 api")
    public void readNetworkServiceProfileA10NspByWrongValue() {
        // fields, Offset, Limit are not implemented in ris actually
        a4ResourceInventoryServiceV4Robot.checkIfNetworkServiceProfileA10NspGetWrongParameter(nspA10DataA.getItAccountingKey(), "a, b", 7, 1);
    }

    @Test(description = "DIGIHUB-xxx Read Network Service Profile Ftth Access from resource inventory service v4 api")
    @Owner("juergen.mayer@t-systems.com")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read Network Service Profile Ftth Access from resource inventory service v4 api")
    public void readNetworkServiceProfileFtthAccessFromA4ApiByLineId() {
        a4ResourceInventoryServiceV4Robot.checkIfNetworkServiceProfileFtthAccessExistsByLineId(nspFtthDataA);
    }

    @Test(description = "DIGIHUB-xxx Read Network Service Profile Ftth Access by ontSerialNumber from resource inventory service v4 api")
    @Owner("juergen.mayer@t-systems.com")
    @TmsLink("DIGIHUB-xxx")
    @Description("Read Network Service Profile Ftth Access from resource inventory service v4 api")
    public void readNetworkServiceProfileFtthAccessFromA4ApiByOnSerialNumber() {
        a4ResourceInventoryServiceV4Robot.checkIfNetworkServiceProfileFtthAccessExistsByOntSerialNumber(nspFtthDataA);
    }

    @Test(description = "DIGIHUB-75777 Read Network Service ProfileFtthAccess by UUID and check ResourceRelationships")
    @Owner("Swetlana.Okonetschnikow@telekom.de")
    @TmsLink("DIGIHUB-116694")
    @Description("Read Network Service Profile Ftth Access from resource inventory service v4 api")
    public void readNetworkServiceProfileFtthAccessFromA4ApiByUuidWithResourceRelationships() {
        nspFtthDataC.setOltPortOntLastRegisteredOn(nepDataB.getUuid());
        a4ResourceInventoryServiceV4Robot.checkResourceRelationshipsByNetworkServiceProfileFtthAccess(nspFtthDataC);
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
