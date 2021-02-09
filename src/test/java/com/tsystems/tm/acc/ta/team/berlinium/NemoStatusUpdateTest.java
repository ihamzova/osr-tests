package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementlink.A4NetworkElementLinkCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilea10nsp.A4NetworkServiceProfileA10NspCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofileftthaccess.A4NetworkServiceProfileFtthAccessCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilel2bsa.A4NetworkServiceProfileL2BsaCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.ta.data.osr.enums.AllowedOperationalStateL2BsaNSP;
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

import java.util.ArrayList;
import java.util.List;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_MS;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_SERVICE_MS;
import static org.testng.Assert.assertEquals;

@ServiceLog(A4_RESOURCE_INVENTORY_MS)
@ServiceLog(A4_RESOURCE_INVENTORY_SERVICE_MS)
@Epic("OS&R domain")
@Feature("Status update requests from NEMO for different A4 network element types")
public class NemoStatusUpdateTest {

    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private final A4ResourceInventoryServiceRobot nemo = new A4ResourceInventoryServiceRobot();

    private final static String NEW_OPERATIONAL_STATE = "WORKING";
    private final static String EXPECTED_NEW_LIFECYCLE_STATE = "OPERATING";
    private final static String FAULTY_OPERATIONAL_STATE = "FAULTY_OPERATIONAL_STATE";

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData;
    private A4NetworkElementPort nepDataA;
    private A4NetworkElementPort nepDataB;
    private A4TerminationPoint tpFtthAccessData;
    private A4TerminationPoint tpA10NspData;
    private A4TerminationPoint tpL2BsaData;
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
        tpFtthAccessData = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointFtthAccess);
        nspFtthData = osrTestContext.getData().getA4NetworkServiceProfileFtthAccessDataProvider()
                .get(A4NetworkServiceProfileFtthAccessCase.defaultNetworkServiceProfileFtthAccess);
        nspA10Data = osrTestContext.getData().getA4NetworkServiceProfileA10NspDataProvider()
                .get(A4NetworkServiceProfileA10NspCase.defaultNetworkServiceProfileA10Nsp);
        nspL2Data = osrTestContext.getData().getA4NetworkServiceProfileL2BsaDataProvider()
                .get(A4NetworkServiceProfileL2BsaCase.defaultNetworkServiceProfileL2Bsa);
        nelData = osrTestContext.getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.defaultNetworkElementLink);
        tpA10NspData = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointA10Nsp);
        tpL2BsaData = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointL2Bsa);


        // Ensure that no old test data is in the way
        cleanup();
    }

    @BeforeMethod
    public void setup() {
        a4ResourceInventoryRobot.createTestDataForAllA4ElementTypes(negData, neData, nepDataA, nepDataB, tpFtthAccessData, nspFtthData, nelData);
        a4ResourceInventoryRobot.createTerminationPoint(tpA10NspData, nepDataA);
        a4ResourceInventoryRobot.createTerminationPoint(tpL2BsaData, nepDataA);
        a4ResourceInventoryRobot.createNetworkServiceProfileA10Nsp(nspA10Data, tpA10NspData);
        a4ResourceInventoryRobot.createNetworkServiceProfileL2Bsa(nspL2Data, tpL2BsaData);
    }

    @AfterMethod
    public void cleanup() {
        a4ResourceInventoryRobot.deleteA4TestData(negData, neData);
    }

    @Test(description = "DIGIHUB-xxxxx NEMO sends a status update for A4 Network Element Group")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Element Group")
    public void testNemoStatusUpdateForNeg() {
        // WHEN
        nemo.sendStatusUpdateForNetworkElementGroup(negData, NEW_OPERATIONAL_STATE);

        // THEN
        a4ResourceInventoryRobot.checkNetworkElementGroupIsUpdatedWithNewStates(negData, NEW_OPERATIONAL_STATE, EXPECTED_NEW_LIFECYCLE_STATE);
    }

    @Test(description = "DIGIHUB-xxxxx NEMO sends a status update for A4 Network Element")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Element")
    public void testNemoStatusUpdateForNe() {
        // WHEN
        nemo.sendStatusUpdateForNetworkElement(neData, negData, NEW_OPERATIONAL_STATE);

        // THEN
        a4ResourceInventoryRobot.checkNetworkElementIsUpdatedWithNewStates(neData, NEW_OPERATIONAL_STATE, EXPECTED_NEW_LIFECYCLE_STATE);
    }


    @Test(description = "DIGIHUB-xxxxx NEMO sends a status update for A4 Network Element Port")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Element Port")
    public void testNemoStatusUpdateForNep() {
        // WHEN
        final String NEW_DESCRIPTION = "DIGIHUB-77227 new description value";
        nemo.sendStatusUpdateForNetworkElementPort(nepDataA, neData, NEW_OPERATIONAL_STATE, NEW_DESCRIPTION);

        // THEN
        a4ResourceInventoryRobot.checkNetworkElementPortIsUpdatedWithNewStateAndDescription(nepDataA, NEW_OPERATIONAL_STATE, NEW_DESCRIPTION);
    }

    @Test(description = "DIGIHUB-xxxxx NEMO sends a status update for A4 Network Service Profile (FTTH Access)")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Service Profile (FTTH Access)")
    public void testNemoStatusUpdateForNspFtth() {
        // WHEN
        nemo.sendStatusUpdateForNetworkServiceProfileFtthAccess(nspFtthData, tpFtthAccessData, NEW_OPERATIONAL_STATE);

        // THEN
        a4ResourceInventoryRobot.checkNetworkServiceProfileFtthAccessIsUpdatedWithNewStates(nspFtthData, NEW_OPERATIONAL_STATE, EXPECTED_NEW_LIFECYCLE_STATE);
    }

    @Test(description = "DIGIHUB-xxxxx NEMO sends a status update for A4 Network Service Profile (A10NSP)")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Service Profile (A10NSP)")
    public void testNemoStatusUpdateForNspA10() {
        // WHEN
        nemo.sendStatusUpdateForNetworkServiceProfileA10Nsp(nspA10Data, tpFtthAccessData, NEW_OPERATIONAL_STATE);

        // THEN
        a4ResourceInventoryRobot.checkNetworkServiceProfileA10NspIsUpdatedWithNewStates(nspA10Data, NEW_OPERATIONAL_STATE, EXPECTED_NEW_LIFECYCLE_STATE);
    }

    @Test(description = "DIGIHUB-xxxxx NEMO sends a status update for A4 Network Service Profile (L2BSA)")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Service Profile (L2BSA)")
    public void testNemoStatusUpdateForNspL2() {
        // WHEN
        nemo.sendStatusUpdateForNetworkServiceProfileL2Bsa(nspL2Data, tpFtthAccessData, NEW_OPERATIONAL_STATE);

        // THEN
        a4ResourceInventoryRobot.checkNetworkServiceProfileL2BsaIsUpdatedWithNewStates(nspL2Data, NEW_OPERATIONAL_STATE, EXPECTED_NEW_LIFECYCLE_STATE);
    }

    @Test(description = "DIGIHUB-76365 extend NEMO API to handle L2BSA Network Service Profile status PATCH with OperationalState only")
    @Owner("@t-systems.com")
    @Description("NEMO sends a status patch for A4 Network Service Profile (L2BSA)")
    public void testNemoStatusPatchForNspL2BSA() {
        // WHEN
        nemo.sendStatusPatchForNetworkServiceProfileL2Bsa(nspL2Data, NEW_OPERATIONAL_STATE);

        // THEN
        a4ResourceInventoryRobot.checkNetworkServiceProfileL2BsaIsUpdatedWithNewStates(nspL2Data, NEW_OPERATIONAL_STATE, EXPECTED_NEW_LIFECYCLE_STATE);
    }

    @Test(description = "DIGIHUB-94384 extend NEMO API to handle L2BSA Network Service Profile status PATCH with new OperationalState and additionally lineId attribute staying the same")
    @Owner("e.balla@t-systems.com")
    @Description("NEMO sends a status patch for A4 Network Service Profile (L2BSA)")
    public void testNemoStatusPatchForNspL2BSA_lineId() {
        // WHEN
        A4NetworkServiceProfileL2Bsa newNspL2Data = new A4NetworkServiceProfileL2Bsa();

        newNspL2Data.setUuid(nspL2Data.getUuid());
        newNspL2Data.setLineId("NichtErlaubt");
        newNspL2Data.setOperationalState(NEW_OPERATIONAL_STATE);
        nemo.sendStatusPatchForNetworkServiceProfileL2Bsa(newNspL2Data);

        // THEN
        A4NetworkServiceProfileL2Bsa expectedA4NetworkServiceProfileL2Bsa = new A4NetworkServiceProfileL2Bsa();

        expectedA4NetworkServiceProfileL2Bsa.setUuid(nspL2Data.getUuid());
        expectedA4NetworkServiceProfileL2Bsa.setAdministrativeMode(nspL2Data.getAdministrativeMode());
        expectedA4NetworkServiceProfileL2Bsa.setLineId(nspL2Data.getLineId());
        expectedA4NetworkServiceProfileL2Bsa.setOperationalState(NEW_OPERATIONAL_STATE);
        expectedA4NetworkServiceProfileL2Bsa.setLifecycleState(EXPECTED_NEW_LIFECYCLE_STATE);

        a4ResourceInventoryRobot.checkNetworkServiceProfileL2BsaIsSameAsDB(expectedA4NetworkServiceProfileL2Bsa);
    }

    @Test(description = "DIGIHUB-94384 Checking Lifecycle_State staying the same while changing Operational_State")
    @Owner("e.balla@telekom.de")
    @Description("NEMO sends a status patch for A4 Network Service Profile (L2BSA)")
    public void testNemoStatusPatchForNspL2BSA_checkingAllPossibleOperationalStates() {
        // WHEN
        // in order to set operational state to WORKING and lifecycle state to OPERATING
        testNemoStatusPatchForNspL2BSA();

        A4NetworkServiceProfileL2Bsa newNspL2Data = new A4NetworkServiceProfileL2Bsa();
        // for each loop created for iterating through operational states which are defined in Enum class
        for (AllowedOperationalStateL2BsaNSP operationalState: AllowedOperationalStateL2BsaNSP.values()) {

        newNspL2Data.setUuid(nspL2Data.getUuid());
        newNspL2Data.setLineId(nspL2Data.getLineId());
        newNspL2Data.setOperationalState(operationalState.toString());
        nemo.sendStatusPatchForNetworkServiceProfileL2Bsa(newNspL2Data);

        // THEN
        A4NetworkServiceProfileL2Bsa expectedA4NetworkServiceProfileL2Bsa = new A4NetworkServiceProfileL2Bsa();

        expectedA4NetworkServiceProfileL2Bsa.setUuid(nspL2Data.getUuid());
        expectedA4NetworkServiceProfileL2Bsa.setAdministrativeMode(nspL2Data.getAdministrativeMode());
        expectedA4NetworkServiceProfileL2Bsa.setLineId(nspL2Data.getLineId());
        expectedA4NetworkServiceProfileL2Bsa.setOperationalState(operationalState.toString());
        expectedA4NetworkServiceProfileL2Bsa.setLifecycleState(EXPECTED_NEW_LIFECYCLE_STATE);

        a4ResourceInventoryRobot.checkNetworkServiceProfileL2BsaIsSameAsDB(expectedA4NetworkServiceProfileL2Bsa);

        }
    }
    @Test(description = "DIGIHUB-94384 NEMO sends a status patch for A4 Network Service Profile (L2BSA) with garbage values into Operational_State field")
    @Owner("e.balla@telekom.de")
    @Description("NEMO sends a status patch for A4 Network Service Profile (L2BSA) with garbage values into Operational_State field")
    public void testNemoStatusPatchForNspL2BSA_checkingFaultyOperationalStateInsertion() {
        // WHEN
        // in order to set operational state to WORKING and lifecycle state to OPERATING
        testNemoStatusPatchForNspL2BSA();

        // Creating a new Object which will be patched
        A4NetworkServiceProfileL2Bsa newNspL2Data = new A4NetworkServiceProfileL2Bsa();
        newNspL2Data.setUuid(nspL2Data.getUuid());
        newNspL2Data.setOperationalState(FAULTY_OPERATIONAL_STATE);
        nemo.sendStatusPatchForNetworkServiceProfileL2Bsa(newNspL2Data);

        // THEN
        // Creation of another Object expectedA4NetworkServiceProfileL2Bsa  which will be used for comparison with newNspL2Data
        A4NetworkServiceProfileL2Bsa expectedA4NetworkServiceProfileL2Bsa = new A4NetworkServiceProfileL2Bsa();
        expectedA4NetworkServiceProfileL2Bsa.setUuid(nspL2Data.getUuid());

        expectedA4NetworkServiceProfileL2Bsa.setOperationalState(FAULTY_OPERATIONAL_STATE);
        expectedA4NetworkServiceProfileL2Bsa.setLifecycleState(EXPECTED_NEW_LIFECYCLE_STATE);

        a4ResourceInventoryRobot.checkNetworkServiceProfileL2BsaIsUpdatedWithNewStates(nspL2Data, FAULTY_OPERATIONAL_STATE, EXPECTED_NEW_LIFECYCLE_STATE);

        }

    @Test(description = "DIGIHUB-xxxxx NEMO sends a status update for A4 Network Element Link")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Element Link")
    public void testNemoStatusUpdateForNel() {
        // WHEN
        nemo.sendStatusUpdateForNetworkElementLink(nelData, nepDataA, nepDataB, NEW_OPERATIONAL_STATE);

        // THEN
        a4ResourceInventoryRobot.checkNetworkElementLinkIsUpdatedWithNewStates(nelData, NEW_OPERATIONAL_STATE, EXPECTED_NEW_LIFECYCLE_STATE);
    }

}
