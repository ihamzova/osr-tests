package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementlink.A4NetworkElementLinkCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilea10nsp.A4NetworkServiceProfileA10NspCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofileftthaccess.A4NetworkServiceProfileFtthAccessCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilel2bsa.A4NetworkServiceProfileL2BsaCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceRobot;
import io.qameta.allure.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_MS;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_SERVICE_MS;

@ServiceLog(A4_RESOURCE_INVENTORY_MS)
@ServiceLog(A4_RESOURCE_INVENTORY_SERVICE_MS)
@Epic("OS&R domain")
@Feature("Status update requests from NEMO for different A4 network element types")
public class NemoStatusUpdateTest {

    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceInventoryRobot a4ResourceInventory = new A4ResourceInventoryRobot();
    private final A4ResourceInventoryServiceRobot nemo = new A4ResourceInventoryServiceRobot();

    private final static String NEW_OPERATIONAL_STATE = "WORKING";
    private final static String EXPECTED_NEW_LIFECYCLE_STATE = "OPERATING";

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData;
    private A4NetworkElementPort nepDataA;
    private A4NetworkElementPort nepDataB;
    private A4TerminationPoint tpData;
    private A4NetworkServiceProfileFtthAccess nspFtthData;
    private A4NetworkServiceProfileA10Nsp nspA10Data;
    private A4NetworkServiceProfileL2Bsa nspL2Data;
    private A4NetworkElementLink nelData;

    @BeforeClass
    public void init() {
        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        neData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        nepDataA = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
        nepDataB = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_10G_001);
        tpData = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointFtthAccess);
        nspFtthData = osrTestContext.getData().getA4NetworkServiceProfileFtthAccessDataProvider()
                .get(A4NetworkServiceProfileFtthAccessCase.defaultNetworkServiceProfileFtthAccess);
        nspA10Data = osrTestContext.getData().getA4NetworkServiceProfileA10NspDataProvider()
                .get(A4NetworkServiceProfileA10NspCase.defaultNetworkServiceProfileA10Nsp);
        nspL2Data = osrTestContext.getData().getA4NetworkServiceProfileL2BsaDataProvider()
                .get(A4NetworkServiceProfileL2BsaCase.defaultNetworkServiceProfileL2Bsa);
        nelData = osrTestContext.getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.defaultNetworkElementLink);

        // Ensure that no old test data is in the way
        cleanup();
    }

    @BeforeMethod
    public void setup() {
        a4ResourceInventory.createTestDataForAllA4ElementTypes(negData, neData, nepDataA, nepDataB, tpData, nspFtthData, nelData);
    }

    @AfterMethod
    public void cleanup() {
        a4ResourceInventory.deleteA4TestData(negData, neData);
    }

    @Test(description = "DIGIHUB-xxxxx NEMO sends a status update for A4 Network Element Group")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Element Group")
    public void testNemoStatusUpdateForNeg() {
        // WHEN
        nemo.sendStatusUpdateForNetworkElementGroup(negData, NEW_OPERATIONAL_STATE);

        // THEN
        a4ResourceInventory.checkNetworkElementGroupIsUpdatedWithNewStates(negData, NEW_OPERATIONAL_STATE, EXPECTED_NEW_LIFECYCLE_STATE);
    }

    @Test(description = "DIGIHUB-xxxxx NEMO sends a status update for A4 Network Element")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Element")
    public void testNemoStatusUpdateForNe() {
        // WHEN
        nemo.sendStatusUpdateForNetworkElement(neData, negData, NEW_OPERATIONAL_STATE);

        // THEN
        a4ResourceInventory.checkNetworkElementIsUpdatedWithNewStates(neData, NEW_OPERATIONAL_STATE, EXPECTED_NEW_LIFECYCLE_STATE);
    }


    @Test(description = "DIGIHUB-xxxxx NEMO sends a status update for A4 Network Element Port")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Element Port")
    public void testNemoStatusUpdateForNep() {
        // WHEN
        final String NEW_DESCRIPTION = "DIGIHUB-77227 new description value";
        nemo.sendStatusUpdateForNetworkElementPort(nepDataA, neData, NEW_OPERATIONAL_STATE, NEW_DESCRIPTION);

        // THEN
        a4ResourceInventory.checkNetworkElementPortIsUpdatedWithNewStateAndDescription(nepDataA, NEW_OPERATIONAL_STATE, NEW_DESCRIPTION);
    }

    @Test(description = "DIGIHUB-xxxxx NEMO sends a status update for A4 Network Service Profile (FTTH Access)")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Service Profile (FTTH Access)")
    public void testNemoStatusUpdateForNspFtth() {
        // WHEN
        nemo.sendStatusUpdateForNetworkServiceProfileFtthAccess(nspFtthData, tpData, NEW_OPERATIONAL_STATE);

        // THEN
        a4ResourceInventory.checkNetworkServiceProfileFtthAccessIsUpdatedWithNewStates(nspFtthData, NEW_OPERATIONAL_STATE, EXPECTED_NEW_LIFECYCLE_STATE);
    }

    @Test(description = "DIGIHUB-xxxxx NEMO sends a status update for A4 Network Service Profile (A10NSP)")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Service Profile (A10NSP)")
    public void testNemoStatusUpdateForNspA10() {
        // WHEN
        nemo.sendStatusUpdateForNetworkServiceProfileA10Nsp(nspA10Data, tpData, NEW_OPERATIONAL_STATE);

        // THEN
        a4ResourceInventory.checkNetworkServiceProfileA10NspIsUpdatedWithNewStates(nspA10Data, NEW_OPERATIONAL_STATE, EXPECTED_NEW_LIFECYCLE_STATE);
    }

    @Test(description = "DIGIHUB-xxxxx NEMO sends a status update for A4 Network Service Profile (L2BSA)")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Service Profile (L2BSA)")
    public void testNemoStatusUpdateForNspL2() {
        // WHEN
        nemo.sendStatusUpdateForNetworkServiceProfileL2Bsa(nspL2Data, tpData, NEW_OPERATIONAL_STATE);

        // THEN
        a4ResourceInventory.checkNetworkServiceProfileL2BsaIsUpdatedWithNewStates(nspL2Data, NEW_OPERATIONAL_STATE, EXPECTED_NEW_LIFECYCLE_STATE);
    }

    @Test(description = "DIGIHUB-xxxxx NEMO sends a status update for A4 Network Element Link")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Element Link")
    public void testNemoStatusUpdateForNel() {
        // WHEN
        nemo.sendStatusUpdateForNetworkElementLink(nelData, nepDataA, nepDataB, NEW_OPERATIONAL_STATE);

        // THEN
        a4ResourceInventory.checkNetworkElementLinkIsUpdatedWithNewStates(nelData, NEW_OPERATIONAL_STATE, EXPECTED_NEW_LIFECYCLE_STATE);
    }

}
