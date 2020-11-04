package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementlink.A4NetworkElementLinkCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofileftthaccess.A4NetworkServiceProfileFtthAccessCase;
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

import static com.tsystems.tm.acc.ta.data.berlinium.BerliniumConstants.A4_RESOURCE_INVENTORY;
import static com.tsystems.tm.acc.ta.data.berlinium.BerliniumConstants.A4_RESOURCE_INVENTORY_SERVICE;

@ServiceLog(A4_RESOURCE_INVENTORY)
@ServiceLog(A4_RESOURCE_INVENTORY_SERVICE)
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

    @Test(description = "DIGIHUB-xxxxx NEMO sends invalid status update for A4 Network Element Group")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends invalid status update for A4 Network Element Group")
    public void testNemoInvalidStatusUpdateForNeg() {
        nemo.receiveErrorWhenSendingInvalidStatusUpdateForNetworkElementGroup(negData);
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

    @Test(description = "DIGIHUB-xxxxx NEMO sends invalid status update for A4 Network Element")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends invalid status update for A4 Network Element")
    public void testNemoInvalidStatusUpdateForNe() {
        nemo.receiveErrorWhenSendingInvalidStatusUpdateForNetworkElement(neData, negData);
    }

    @Test(description = "DIGIHUB-xxxxx NEMO sends a status update for A4 Network Element Port")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Element Port")
    public void testNemoStatusUpdateForNep() {
        // WHEN
        nemo.sendStatusUpdateForNetworkElementPort(nepDataA, neData, NEW_OPERATIONAL_STATE);

        // THEN
        a4ResourceInventory.checkNetworkElementPortIsUpdatedWithNewState(nepDataA, NEW_OPERATIONAL_STATE);
    }

    @Test(description = "DIGIHUB-xxxxx NEMO sends invalid status update for A4 Network Element Port")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends invalid status update for A4 Network Element Port")
    public void testNemoInvalidStatusUpdateForNep() {
        nemo.receiveErrorWhenSendingInvalidStatusUpdateForNetworkElementPort(nepDataA, neData);
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

    @Test(description = "DIGIHUB-xxxxx NEMO sends invalid status update for A4 Network Service Profile (FTTH Access)")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends invalid status update for A4 Network Service Profile (FTTH Access)")
    public void testNemoInvalidStatusUpdateForNspFtth() {
        nemo.receiveErrorWhenSendingInvalidStatusUpdateForNetworkServiceProfileFtthAccess(nspFtthData, tpData);
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

    @Test(description = "DIGIHUB-xxxxx NEMO sends invalid status update for A4 Network Element Link")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends invalid status update for A4 Network Element Link")
    public void testNemoInvalidStatusUpdateForNel() {
        nemo.receiveErrorWhenSendingInvalidStatusUpdateForNetworkElementLink(nelData, nepDataA, nepDataB);
    }

}
