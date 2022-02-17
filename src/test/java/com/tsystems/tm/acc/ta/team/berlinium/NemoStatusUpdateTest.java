package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementlink.A4NetworkElementLinkCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilea10nsp.A4NetworkServiceProfileA10NspCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofileftthaccess.A4NetworkServiceProfileFtthAccessCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilel2bsa.A4NetworkServiceProfileL2BsaCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryServiceMapper;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkServiceProfileL2BsaDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.LogicalResourceUpdate;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceCharacteristic;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceRef;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceRelationship;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.*;
import org.testng.annotations.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;


@ServiceLog({A4_RESOURCE_INVENTORY_MS,A4_RESOURCE_INVENTORY_SERVICE_MS,A4_CARRIER_MANAGEMENT_MS,A4_NEMO_UPDATER_MS})
@Epic("OS&R domain")
@Feature("Status update requests from NEMO for different A4 network element types")
public class NemoStatusUpdateTest {

    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private final A4ResourceInventoryServiceRobot nemo = new A4ResourceInventoryServiceRobot();

    private final static String OPERATIONAL_STATE_WORKING = "WORKING";
    private final static String LIFECYCLE_STATE_OPERATING = "OPERATING";

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
        a4ResourceInventoryRobot.createNetworkElementGroup(negData);
        a4ResourceInventoryRobot.createNetworkElement(neData, negData);
        a4ResourceInventoryRobot.createNetworkElementPort(nepDataA, neData);
        a4ResourceInventoryRobot.createNetworkElementPort(nepDataB, neData);
        a4ResourceInventoryRobot.createTerminationPoint(tpFtthAccessData, nepDataA);
        a4ResourceInventoryRobot.createNetworkServiceProfileFtthAccess(nspFtthData, tpFtthAccessData);
        a4ResourceInventoryRobot.createNetworkElementLink(nelData, nepDataA, nepDataB);
        a4ResourceInventoryRobot.createTerminationPoint(tpA10NspData, nepDataA);
        a4ResourceInventoryRobot.createTerminationPoint(tpL2BsaData, nepDataA);
        a4ResourceInventoryRobot.createNetworkServiceProfileA10Nsp(nspA10Data, tpA10NspData);
        a4ResourceInventoryRobot.createNetworkServiceProfileL2Bsa(nspL2Data, tpL2BsaData);
    }

    @AfterMethod
    public void cleanup() {
        // Delete all A4 data which might provoke problems because of unique constraints
        a4ResourceInventoryRobot.deleteA4NetworkElementGroupsRecursively(negData);
        a4ResourceInventoryRobot.deleteA4NetworkElementsRecursively(neData);
        a4ResourceInventoryRobot.deleteA4NetworkElementPortsRecursively(nepDataA, neData);
        a4ResourceInventoryRobot.deleteA4NetworkElementPortsRecursively(nepDataB, neData);
        a4ResourceInventoryRobot.deleteNspFtthAccess(nspFtthData);
        a4ResourceInventoryRobot.deleteNspsL2Bsa(nspL2Data);
    }

    @Test(description = "DIGIHUB-xxxxx NEMO sends a status update for A4 Network Element Group")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Element Group")
    public void testNemoStatusUpdateForNeg() {
        //GIVEN
        OffsetDateTime timeBeforeNemoStatusUpdate = OffsetDateTime.now();
        // WHEN
        nemo.sendStatusUpdateForNetworkElementGroup(negData, OPERATIONAL_STATE_WORKING);

        // THEN
        a4ResourceInventoryRobot.checkNetworkElementGroupIsUpdatedWithNewStates(negData, OPERATIONAL_STATE_WORKING, LIFECYCLE_STATE_OPERATING);
        a4ResourceInventoryRobot.checkNetworkElementGroupIsUpdatedWithLastSuccessfulSyncTime(negData,timeBeforeNemoStatusUpdate);
    }

    @Test(description = "DIGIHUB-xxxxx NEMO sends a status update for A4 Network Element")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Element")
    public void testNemoStatusUpdateForNe() {
        //GIVEN
        OffsetDateTime timeBeforeNemoStatusUpdate = OffsetDateTime.now();
        // WHEN
        nemo.sendStatusUpdateForNetworkElement(neData, negData, OPERATIONAL_STATE_WORKING);

        // THEN
        a4ResourceInventoryRobot.checkNetworkElementIsUpdatedWithNewStates(neData, OPERATIONAL_STATE_WORKING, LIFECYCLE_STATE_OPERATING);
        a4ResourceInventoryRobot.checkNetworkElementIsUpdatedWithLastSuccessfulSyncTime(neData,timeBeforeNemoStatusUpdate);
    }


    @Test(description = "DIGIHUB-xxxxx NEMO sends a status update for A4 Network Element Port")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Element Port")
    public void testNemoStatusUpdateForNep() {
        //GIVEN
        OffsetDateTime timeBeforeNemoStatusUpdate = OffsetDateTime.now();
        // WHEN
        final String NEW_DESCRIPTION = "DIGIHUB-77227 new description value";
        nemo.sendStatusUpdateForNetworkElementPort(nepDataA, neData, OPERATIONAL_STATE_WORKING, NEW_DESCRIPTION);

        // THEN
        a4ResourceInventoryRobot.checkNetworkElementPortIsUpdatedWithNewStateAndDescription(nepDataA, OPERATIONAL_STATE_WORKING, NEW_DESCRIPTION);
        a4ResourceInventoryRobot.checkNetworkElementPortIsUpdatedWithLastSuccessfulSyncTime(nepDataA,timeBeforeNemoStatusUpdate);
    }

    @Test(description = "DIGIHUB-xxxxx NEMO sends a status update for A4 Network Service Profile (FTTH Access)")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Service Profile (FTTH Access)")
    public void testNemoStatusUpdateForNspFtth() {
        //GIVEN
        OffsetDateTime timeBeforeNemoStatusUpdate = OffsetDateTime.now();
        // WHEN
        nemo.sendStatusUpdateForNetworkServiceProfileFtthAccess(nspFtthData, tpFtthAccessData, OPERATIONAL_STATE_WORKING);

        // THEN
        a4ResourceInventoryRobot.checkNetworkServiceProfileFtthAccessIsUpdatedWithNewStates
                (nspFtthData, OPERATIONAL_STATE_WORKING, LIFECYCLE_STATE_OPERATING);
        a4ResourceInventoryRobot.checkNetworkServiceProfileFtthAccessIsUpdatedWithLastSuccessfulSyncTime
                (nspFtthData,timeBeforeNemoStatusUpdate);
    }

    @Test(description = "DIGIHUB-75778 NEMO sends a status and port reference update for A4 Network Service Profile (FTTH Access)")
    @Owner("Swetlana.Okonetschnikow@telekom.de")
    @TmsLink("DIGIHUB-116417")
    @Description("NEMO sends a status and port reference update for A4 Network Service Profile (FTTH Access)")
    public void testNemoStatusAndPortRefPatchForNspFtth() {
        // WHEN
        nemo.sendStatusAndPortRefUpdateForNetworkServiceProfileFtthAccess(nspFtthData, tpFtthAccessData,
                OPERATIONAL_STATE_WORKING, nepDataA);

        // THEN
        a4ResourceInventoryRobot
                .checkNetworkServiceProfileFtthAccessIsUpdatedWithNewStatesAndPortRef(nspFtthData,
                        OPERATIONAL_STATE_WORKING, LIFECYCLE_STATE_OPERATING, nepDataA);
    }

    @Test(description = "DIGIHUB-xxxxx NEMO sends a status update for A4 Network Service Profile (A10NSP)")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Service Profile (A10NSP)")
    public void testNemoStatusUpdateForNspA10() {
        //GIVEN
        OffsetDateTime timeBeforeNemoStatusUpdate = OffsetDateTime.now();
        // WHEN
        nemo.sendStatusUpdateForNetworkServiceProfileA10Nsp(nspA10Data, tpFtthAccessData, OPERATIONAL_STATE_WORKING);

        // THEN
        a4ResourceInventoryRobot.checkNetworkServiceProfileA10NspIsUpdatedWithNewStates(nspA10Data, OPERATIONAL_STATE_WORKING, LIFECYCLE_STATE_OPERATING);
        a4ResourceInventoryRobot.checkNetworkServiceProfileA10NspIsUpdatedWithLastSuccessfulSyncTime(nspA10Data,timeBeforeNemoStatusUpdate);
    }

    @DataProvider(name = "toBeChangedLcs")
    public static Object[][] toBeChangedLifefcycleStates() {
        return new Object[][]{{"PLANNING"}, {"INSTALLING"}};
    }

    @Test(dataProvider = "toBeChangedLcs", description = "DIGIHUB-xxxxx NEMO sends a status update (WORKING) for A4 NSP L2BSA which also changes Lifecycle State")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Service Profile (L2BSA)")
    public void testNemoStatusUpdateWorkingForNspL2WithChangedLcs(String oldLifecycleState) {
        //GIVEN
        OffsetDateTime timeBeforeNemoStatusUpdate = OffsetDateTime.now();
        // Prepare existing NSP L2BSA to have lifecycle state 'oldLifecycleState'
        a4ResourceInventoryRobot.setLifecycleState(nspL2Data, oldLifecycleState);

        // WHEN
        nemo.sendStatusUpdateForNetworkServiceProfileL2Bsa(nspL2Data, tpFtthAccessData, OPERATIONAL_STATE_WORKING);

        // THEN
        a4ResourceInventoryRobot.checkNetworkServiceProfileL2BsaIsUpdatedWithNewStates(nspL2Data, OPERATIONAL_STATE_WORKING, LIFECYCLE_STATE_OPERATING);
        a4ResourceInventoryRobot.checkNetworkServiceProfileL2BsaIsUpdatedWithLastSuccessfulSyncTime(nspL2Data,timeBeforeNemoStatusUpdate);
    }

    @DataProvider(name = "toNotBeChangedLcs")
    public static Object[][] toNotBeChangedLiefcycleStates() {
        return new Object[][]{{"OPERATING"}, {"RETIRING"}};
    }

    @Test(dataProvider = "toNotBeChangedLcs", description = "DIGIHUB-xxxxx NEMO sends a status update (WORKING) for A4 NSP L2BSA which DOESN'T change Lifecycle State")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Service Profile (L2BSA)")
    public void testNemoStatusUpdateWorkingForNspL2WithUnchangedLcs(String oldLifecycleState) {
        // GIVEN
        // Prepare existing NSP L2BSA to have lifecycle state 'oldLifecycleState'
        a4ResourceInventoryRobot.setLifecycleState(nspL2Data, oldLifecycleState);

        // WHEN
        nemo.sendStatusUpdateForNetworkServiceProfileL2Bsa(nspL2Data, tpFtthAccessData, OPERATIONAL_STATE_WORKING);

        // THEN
        a4ResourceInventoryRobot.checkNetworkServiceProfileL2BsaIsUpdatedWithNewStates(nspL2Data, OPERATIONAL_STATE_WORKING, oldLifecycleState);
    }

    @DataProvider(name = "opStatesWoWorking")
    public static Object[][] allOpStatesExceptWorking() {
        String[] opStates = new String[]{"INSTALLING", "NOT_WORKING", "NOT_MANAGEABLE", "FAILED", "ACTIVATING", "DEACTIVATING"};
        String[] lcStates = new String[]{"PLANNING", "INSTALLING", "OPERATING", "RETIRING"};

        // Build cartesian product of op and lc states where lifecycle state is not to be changed
        return Arrays.stream(opStates)
                .flatMap(
                        op -> Arrays.stream(lcStates)
                                .map(lc -> new String[]{op, lc})
                )
                .toArray(String[][]::new);
    }

    @Test(dataProvider = "opStatesWoWorking", description = "DIGIHUB-94384 NEMO sends a status update (all but WORKING) for A4 NSP L2BSA which DOESN'T change Lifecycle State")
    @Owner("e.balla@telekom.de, bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Service Profile (L2BSA)")
    public void testNemoStatusUpdateAllExceptWorkingForNspL2WithUnchangedLcs(String[] states) {
        // GIVEN
        final String operationalState = states[0];
        final String lifecycleState = states[1];

        // Prepare existing NSP L2BSA to have lifecycle state 'lifecycleState'
        a4ResourceInventoryRobot.setLifecycleState(nspL2Data, lifecycleState);

        // WHEN
        nemo.sendStatusUpdateForNetworkServiceProfileL2Bsa(nspL2Data, tpFtthAccessData, operationalState);

        // THEN
        a4ResourceInventoryRobot.checkNetworkServiceProfileL2BsaIsUpdatedWithNewStates(nspL2Data, operationalState, lifecycleState);
    }

    @Test(description = "DIGIHUB-94384 No other fields than operational and lifecycle states should be changed in PATCH request for L2BSA Network Service Profile")
    @Owner("e.balla@t-systems.com, bela.kovac@t-systems.com")
    @Description("NEMO sends a status patch for A4 Network Service Profile (L2BSA)")
    public void testNemoStatusPatchForNspL2BSA_noChanges() {
        // GIVEN
        LogicalResourceUpdate changedL2BsaAsLogicalResource = createLogicalResourceWithAllAttributesChanged(nspL2Data, tpL2BsaData, OPERATIONAL_STATE_WORKING);
        NetworkServiceProfileL2BsaDto nspOld = a4ResourceInventoryRobot.getExistingNetworkServiceProfileL2Bsa(nspL2Data.getUuid());

        // WHEN
        nemo.sendPatchForLogicalResource(nspL2Data.getUuid(), changedL2BsaAsLogicalResource);

        // THEN
        a4ResourceInventoryRobot.checkThatNoFieldsAreChanged(nspL2Data, nspOld);
    }

    private LogicalResourceUpdate createLogicalResourceWithAllAttributesChanged(A4NetworkServiceProfileL2Bsa nspL2Data, A4TerminationPoint tpData, String newOperationalState) {
        // First get original logical resource for NSP L2BSA
        LogicalResourceUpdate nspL2LogicalResource = new A4ResourceInventoryServiceMapper()
                .getLogicalResourceUpdate(nspL2Data, tpData, newOperationalState);

        final String value = "changed_value";

        // Now set all NSP L2BSA related fields (except operational states, which have all been tested elsewhere) to different value
        List<ResourceCharacteristic> characteristics = new ArrayList<>();
        characteristics.add(new ResourceCharacteristic()
                .name("creationTime")
                .value(OffsetDateTime.now().toString()));
        characteristics.add(new ResourceCharacteristic()
                .name("lastUpdateTime")
                .value(OffsetDateTime.now().toString()));
        characteristics.add(new ResourceCharacteristic()
                .name("lineId")
                .value(value));
        characteristics.add(new ResourceCharacteristic()
                .name("operationalState")
                .value(nspL2Data.getOperationalState()));
        characteristics.add(new ResourceCharacteristic()
                .name("virtualServiceProvider")
                .value(value));
        characteristics.add(new ResourceCharacteristic()
                .name("administrativeMode")
                .value(value));
        characteristics.add(new ResourceCharacteristic()
                .name("serviceBandwidth")
                .value("[{\"dataRateDown\":\"1\",\"dataRateUp\":\"2\"}]"));

        List<ResourceRelationship> resourceRelationships = new ArrayList<>();
        resourceRelationships.add(new ResourceRelationship()
                .type("requires")
                .resourceRef(new ResourceRef()
                        .id(value)
                        .type(value)));

        nspL2LogicalResource.setLifecycleState(nspL2Data.getLifecycleState());
        nspL2LogicalResource.setDescription(value);
        nspL2LogicalResource.setName(value);
        nspL2LogicalResource.setVersion(value);
        nspL2LogicalResource.setCharacteristic(characteristics);
        nspL2LogicalResource.setResourceRelationship(resourceRelationships);

        return nspL2LogicalResource;
    }

    @Test(description = "DIGIHUB-94384 NEMO sends a status patch for A4 Network Service Profile (L2BSA) with garbage value for Operational_State field, should be allowed")
    @Owner("e.balla@telekom.de, bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Service Profile (L2BSA)")
    public void testNemoInvalidStatusUpdateForNspL2() {
        // GIVEN
        final String operationalState = "I_am_an_invalid_operational_state";

        // WHEN
        nemo.sendStatusUpdateForNetworkServiceProfileL2Bsa(nspL2Data, tpFtthAccessData, operationalState);

        // THEN
        a4ResourceInventoryRobot.checkNetworkServiceProfileL2BsaIsUpdatedWithNewStates(nspL2Data, operationalState, "PLANNING");
    }

    @Test(description = "DIGIHUB-xxxxx NEMO sends a status update for A4 Network Element Link")
    @Owner("bela.kovac@t-systems.com")
    @Description("NEMO sends a status update for A4 Network Element Link")
    public void testNemoStatusUpdateForNel() {
        //GIVEN
        OffsetDateTime timeBeforeNemoStatusUpdate = OffsetDateTime.now();
        // WHEN
        nemo.sendStatusUpdateForNetworkElementLink(nelData, nepDataA, nepDataB, OPERATIONAL_STATE_WORKING);

        // THEN
        a4ResourceInventoryRobot.checkNetworkElementLinkIsUpdatedWithNewStates(nelData, OPERATIONAL_STATE_WORKING, LIFECYCLE_STATE_OPERATING);
        a4ResourceInventoryRobot.checkNetworkElementLinkIsUpdatedWithLastSuccessfulSyncTime(nelData,timeBeforeNemoStatusUpdate);
    }

}
