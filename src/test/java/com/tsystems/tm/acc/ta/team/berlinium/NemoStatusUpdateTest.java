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
                .get(A4TerminationPointCase.defaultTerminationPoint);
        nspFtthData = osrTestContext.getData().getA4NetworkServiceProfileFtthAccessDataProvider()
                .get(A4NetworkServiceProfileFtthAccessCase.defaultNetworkServiceProfileFtthAccess);
        nelData = osrTestContext.getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.defaultNetworkElementLink);

        // Ensure that no old test data is in the way
        cleanup();
    }

    @BeforeMethod
    public void setup() {
        a4ResourceInventory.createNetworkElementGroup(negData);
        a4ResourceInventory.createNetworkElement(neData, negData);
        a4ResourceInventory.createNetworkElementPort(nepDataA, neData);
        a4ResourceInventory.createNetworkElementPort(nepDataB, neData);
        a4ResourceInventory.createTerminationPoint(tpData, nepDataA);
        a4ResourceInventory.createNetworkServiceProfileFtthAccess(nspFtthData, tpData);
        a4ResourceInventory.createNetworkElementLink(nelData, nepDataA, nepDataB);
    }

    @AfterMethod
    public void cleanup() {
        a4ResourceInventory.deleteA4NetworkElementsIncludingChildren(neData);
        a4ResourceInventory.deleteNetworkElementGroups(negData);
    }

    @Test(description = "DIGIHUB-xxxxx NEMO sends a status update for A4 Network Element Group")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Element Group")
    public void testNemoStatusUpdateForNeg() {
        // GIVEN
        negData.setOperationalState("NOT_WORKING");
        negData.setLifecycleState("PLANNING");
        final String newOperationalState = "WORKING";
        final String expectedNewLifecycleState = "OPERATING";

        // WHEN
        nemo.sendStatusUpdateForNetworkElementGroup(negData, newOperationalState);

        // THEN
        a4ResourceInventory.checkNetworkElementGroupIsUpdatedWithNewStates(negData, newOperationalState, expectedNewLifecycleState);
    }

    @Test(description = "DIGIHUB-xxxxx NEMO sends invalid status update for A4 Network Element Group")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends invalid status update for A4 Network Element Group")
    public void testNemoInvalidStatusUpdateForNeg() {
        nemo.receiveErrorWhenSendingInvalidStatusUpdateForNetworkElementGroup(negData);
    }

}
