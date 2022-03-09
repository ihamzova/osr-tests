package cucumber.stepdefinitions.team.berlinium;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementlink.A4NetworkElementLinkCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofileftthaccess.A4NetworkServiceProfileFtthAccessCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilel2bsa.A4NetworkServiceProfileL2BsaCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryMapper;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.*;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.FileAssert.fail;

public class A4ResInvSteps {

    private final A4ResourceInventoryRobot a4ResInv = new A4ResourceInventoryRobot();
    private final TestContext testContext;

    public A4ResInvSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    @After
    public void cleanup() {
        // ATTENTION: If at any time more than 1 NEG is used for tests, the additional ones have to be added here!

        final boolean NEG_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NEG);
        if (NEG_PRESENT) {
            A4NetworkElementGroup neg = (A4NetworkElementGroup) testContext.getScenarioContext().getContext(Context.A4_NEG);
            a4ResInv.deleteA4NetworkElementGroupsRecursively(neg.getName());
        }

    }

    // -----=====[ GIVENS ]=====-----

    @Given("a NEG is existing in A4 resource inventory")
    public void givenANEGIsExistingInA4ResourceInventory() {
        // ACTION
        A4NetworkElementGroup neg = setupDefaultNegTestData();

        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteA4NetworkElementGroupsRecursively(neg);

        a4ResInv.createNetworkElementGroup(neg);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NEG, neg);
    }

    @Given("a NEG with name {string} is existing in A4 resource inventory")
    public void givenANEGWithNameIsExistingInA4ResourceInventory(String name) {
        // ACTION
        A4NetworkElementGroup neg = setupDefaultNegTestData();
        neg.setName(name);

        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteA4NetworkElementGroupsRecursively(neg.getName());

        a4ResInv.createNetworkElementGroup(neg);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NEG, neg);
    }

    @Given("a NEG with operational state {string} and lifecycle state {string} is existing in A4 resource inventory")
    public void givenANEGWithOperationalStateAndLifecycleStateIsExistingInA4ResourceInventory(String ops, String lcs) {
        // ACTION
        A4NetworkElementGroup neg = setupDefaultNegTestData();
        neg.setOperationalState(ops);
        neg.setLifecycleState(lcs);

        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteA4NetworkElementGroupsRecursively(neg.getName());

        a4ResInv.createNetworkElementGroup(neg);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NEG, neg);
    }

    /**
     * Creates a NEG in a4 resource inventory, each property filled with default test data.
     * If any NEG with colliding unique constraint (property 'name') already exists, then the old NEG is deleted first.
     *
     * @param table Contains explicit properties and values with which the default test data is overwritten
     */
    @Given("a NEG with the following properties is existing in A4 resource inventory:")
    public void givenANEGWithTheFollowingProperties(DataTable table) {
        final Map<String, String> negMap = table.asMap();
        final ObjectMapper om = testContext.getObjectMapper();
        final TokenBuffer buffer = new TokenBuffer(om, false);

        // First create a new NEG default test data set...
        final A4NetworkElementGroup negData = setupDefaultNegTestData();
        final NetworkElementGroupDto negDtoDefault = new A4ResourceInventoryMapper().getNetworkElementGroupDto(negData);

        try {
            // ... then overwrite default data set with data provided in given-step data table
            om.writeValue(buffer, negMap);
            final NetworkElementGroupDto negDto = om.readerForUpdating(negDtoDefault).readValue(buffer.asParser());

            // Make sure no old test data is in the way (to avoid colliding unique constraints)
            a4ResInv.deleteA4NetworkElementGroupsRecursively(negDtoDefault.getName());

            // Do the actual NEG creation
            a4ResInv.createNetworkElementGroup(negDto);

            // OUTPUT INTO SCENARIO CONTEXT
            testContext.getScenarioContext().setContext(Context.A4_NEG, mapDtoToNeg(negDto));

        } catch (IOException e) {
            fail("Unexpected mapping error: " + e.getMessage());
        }
    }

    @Given("no NEG exists in A4 resource inventory")
    public void givenNoNEGExistsInA4ResourceInventory() {
        // ACTION
        A4NetworkElementGroup neg = new A4NetworkElementGroup();
        neg.setUuid(UUID.randomUUID().toString());
        a4ResInv.deleteA4NetworkElementGroupsRecursively(neg);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NEG, neg);
    }

    @Given("no NEG with name {string} is existing in resource inventory")
    public void noNEGWithNameIsExistingInResourceInventory(String name) {
        // ACTION
        A4NetworkElementGroup neg = new A4NetworkElementGroup();
        neg.setName(name);

        a4ResInv.deleteA4NetworkElementGroupsRecursively(neg.getName());

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NEG, neg);
    }

    @Given("a NE with operational state {string} and lifecycle state {string} is existing in A4 resource inventory")
    public void givenANEWithOperationalStateAndLifecycleStateIsExistingInA4ResourceInventory(String ops, String lcs) {
        // ACTION
        A4NetworkElement ne = setupDefaultNeTestData();
        final A4NetworkElementGroup neg = (A4NetworkElementGroup) testContext.getScenarioContext().getContext(Context.A4_NEG);
        ne.setOperationalState(ops);
        ne.setLifecycleState(lcs);

        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteA4NetworkElementsRecursively(ne);

        a4ResInv.createNetworkElement(ne, neg);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NE, ne);
    }

    @Given("a NE is existing in A4 resource inventory")
    public void givenANeIsExistingInA4ResourceInventory() {
        // ACTION
        A4NetworkElement ne = setupDefaultNeTestData();

        final A4NetworkElementGroup neg = (A4NetworkElementGroup) testContext.getScenarioContext().getContext(Context.A4_NEG);

        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteA4NetworkElementsRecursively(ne);

        a4ResInv.createNetworkElement(ne, neg);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NE, ne);
    }


    @Given("a second/another NE is existing in A4 resource inventory")
    public void givenASecondNeIsExistingInA4ResourceInventory() {
        // ACTION
        A4NetworkElement ne = setupDefaultNeTestData();

        final A4NetworkElementGroup neg = (A4NetworkElementGroup) testContext.getScenarioContext().getContext(Context.A4_NEG);

        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteA4NetworkElementsRecursively(ne);

        a4ResInv.createNetworkElement(ne, neg);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NE_B, ne);
    }

    @Given("a NE with VPSZ {string} and FSZ {string} is existing in A4 resource inventory")
    public void givenANeWithVpszAndFszIsExistingInA4ResourceInventory(String vpsz, String fsz) {
        // ACTION
        A4NetworkElement ne = setupDefaultNeTestData();
        ne.setVpsz(vpsz);
        ne.setFsz(fsz);

        final A4NetworkElementGroup neg = (A4NetworkElementGroup) testContext.getScenarioContext().getContext(Context.A4_NEG);

        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteA4NetworkElementsRecursively(ne);

        a4ResInv.createNetworkElement(ne, neg);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NE, ne);
    }

    @Given("no NE exists in A4 resource inventory")
    public void givenNoNEExistsInA4ResourceInventory() {
        // ACTION
        A4NetworkElement ne = new A4NetworkElement();
        ne.setUuid(UUID.randomUUID().toString());
        a4ResInv.deleteA4NetworkElementsRecursively(ne);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NE, ne);
    }

    @Given("a NEP is existing in A4 resource inventory")
    public void givenANEPIsExistingInA4ResourceInventory() {
        // ACTION
        A4NetworkElementPort nep = setupDefaultNepTestData();

        final A4NetworkElement ne = (A4NetworkElement) testContext.getScenarioContext().getContext(Context.A4_NE);

        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteA4NetworkElementPortsRecursively(nep, ne);

        a4ResInv.createNetworkElementPort(nep, ne);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NEP, nep);
    }


    @Given("a second/another NEP is existing in A4 resource inventory")
    public void givenASecondNEPIsExistingInA4ResourceInventory() {
        // ACTION
        A4NetworkElementPort nep = setupDefaultNepTestData();

        final A4NetworkElement ne = (A4NetworkElement) testContext.getScenarioContext().getContext(Context.A4_NE_B);

        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteA4NetworkElementPortsRecursively(nep, ne);

        a4ResInv.createNetworkElementPort(nep, ne);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NEP_B, nep);
    }


    @Given("a NEP with operational state {string} and description {string} is existing in A4 resource inventory")
    public void givenANEPWithOperationalStateAndDescriptionIsExistingInAResourceInventory(String opState, String descr) {
        // ACTION
        A4NetworkElementPort nep = setupDefaultNepTestData();
        nep.setOperationalState(opState);
        nep.setDescription(descr);

        final A4NetworkElement ne = (A4NetworkElement) testContext.getScenarioContext().getContext(Context.A4_NE);

        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteA4NetworkElementPortsRecursively(nep, ne);

        a4ResInv.createNetworkElementPort(nep, ne);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NEP, nep);
    }


    @Given("a NEL with operational state {string} and lifecycle state {string} is existing in A4 resource inventory")
    public void givenANELWithOperationalStateAndLifecycleStateIsExistingInA4ResourceInventory(String ops, String lcs) {
        // ACTION
        A4NetworkElementLink nel = setupDefaultNelTestData();
        final A4NetworkElement neA = (A4NetworkElement) testContext.getScenarioContext().getContext(Context.A4_NE);
        final A4NetworkElement neB = (A4NetworkElement) testContext.getScenarioContext().getContext(Context.A4_NE_B);
        final A4NetworkElementPort nepA = (A4NetworkElementPort) testContext.getScenarioContext().getContext(Context.A4_NEP);
        final A4NetworkElementPort nepB = (A4NetworkElementPort) testContext.getScenarioContext().getContext(Context.A4_NEP_B);
        nel.setOperationalState(ops);
        nel.setLifecycleState(lcs);


        a4ResInv.createNetworkElementLink(nel,nepA,nepB,neA,neB);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NEL,nel);
    }


    @Given("a TP is existing in A4 resource inventory")
    public void givenATPIsExistingInA4ResourceInventory() {
        // ACTION
        A4TerminationPoint tp = setupDefaultTpTestData();

        final A4NetworkElementPort nep = (A4NetworkElementPort) testContext.getScenarioContext().getContext(Context.A4_NEP);

        a4ResInv.createTerminationPoint(tp, nep);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_TP, tp);
    }

    @Given("a TP with type {string} is existing in A4 resource inventory")
    public void givenTPWithTypeIsExistingInA4ResourceInventory(String tpType) {
        // ACTION
        A4TerminationPoint tp = setupDefaultTpTestData();
        tp.setSubType(tpType);

        final A4NetworkElementPort nep = (A4NetworkElementPort) testContext.getScenarioContext().getContext(Context.A4_NEP);

        a4ResInv.createTerminationPoint(tp, nep);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_TP, tp);
    }

    @Given("no TP exists in A4 resource inventory")
    public void givenNoTPExistsInA4ResourceInventory() {
        // ACTION
        A4TerminationPoint tp = new A4TerminationPoint();
        tp.setUuid(UUID.randomUUID().toString());

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_TP, tp);
    }

    @Given("a NSP FTTH(-Access) with Line ID {string} is existing in A4 resource inventory( for the TP)")
    public void givenANSPFTTHWithLineIDIsExistingInA4ResourceInventoryForTheTP(String lineId) {
        // ACTION
        A4NetworkServiceProfileFtthAccess nspFtth = setupDefaultNspFtthTestData();
        nspFtth.setLineId(lineId);

        final A4TerminationPoint tp = (A4TerminationPoint) testContext.getScenarioContext().getContext(Context.A4_TP);

        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteNspFtthAccess(nspFtth);

        a4ResInv.createNetworkServiceProfileFtthAccess(nspFtth, tp);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NSP_FTTH, nspFtth);
    }

    @Given("no NSP FTTH(-Access) exists in A4 resource inventory( for the TP)")
    public void givenNoNSPFTTHExistsInA4ResourceInventoryForTheTP() {
        // ACTION
        A4NetworkServiceProfileFtthAccess nspFtth = new A4NetworkServiceProfileFtthAccess();
        nspFtth.setUuid(UUID.randomUUID().toString());
        a4ResInv.deleteNspFtthAccess(nspFtth);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NSP_FTTH, nspFtth);
    }

    @Given("a NSP L2BSA with operationalState {string} is existing in A4 resource inventory")
    public void givenNspL2BsaWithLineIDIsExistingInA4ResourceInventoryForTheTP(String operationalState) {
        // ACTION
        A4NetworkServiceProfileL2Bsa nspL2Bsa = setupDefaultNspL2BsaTestData();
        nspL2Bsa.setOperationalState(operationalState);

        final A4TerminationPoint tp = (A4TerminationPoint) testContext.getScenarioContext().getContext(Context.A4_TP);

        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteNspsL2Bsa(nspL2Bsa);

        a4ResInv.createNetworkServiceProfileL2Bsa(nspL2Bsa, tp);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NSP_L2BSA, nspL2Bsa);
    }

    @Given("a NSP L2BSA with operationalState {string} and lifecycleState {string} is existing in A4 resource inventory")
    public void givenNspL2BsaWithLineIDIsExistingInA4ResourceInventoryForTheTP(String operationalState, String lifecycleState) {
        // ACTION
        A4NetworkServiceProfileL2Bsa nspL2Bsa = setupDefaultNspL2BsaTestData();
        nspL2Bsa.setOperationalState(operationalState);
        nspL2Bsa.setLifecycleState(lifecycleState);

        final A4TerminationPoint tp = (A4TerminationPoint) testContext.getScenarioContext().getContext(Context.A4_TP);

        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteNspsL2Bsa(nspL2Bsa);

        a4ResInv.createNetworkServiceProfileL2Bsa(nspL2Bsa, tp);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NSP_L2BSA, nspL2Bsa);
    }

    // -----=====[ THENS ]=====-----

    @Then("the (new )NEG operationalState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNegOperationalStateIsUpdatedInA4ResInv(String operationalState) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementGroup negData = (A4NetworkElementGroup) testContext.getScenarioContext().getContext(Context.A4_NEG);

        // ACTION
        final NetworkElementGroupDto neg = a4ResInv.getExistingNetworkElementGroup(negData.getUuid());
        assertEquals(operationalState, neg.getOperationalState());
    }

    @Then("the (new )NEG lifecycleState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNegLifecycleStateIsUpdatedInA4ResInv(String lifecycleState) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementGroup negData = (A4NetworkElementGroup) testContext.getScenarioContext().getContext(Context.A4_NEG);

        // ACTION
        final NetworkElementGroupDto neg = a4ResInv.getExistingNetworkElementGroup(negData.getUuid());
        assertEquals(lifecycleState, neg.getLifecycleState());
    }

    @Then("the NEG creationTime is not updated")
    public void thenTheNEGCreationTimeIsNotUpdated() {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementGroup negData = (A4NetworkElementGroup) testContext.getScenarioContext().getContext(Context.A4_NEG);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);

        // ACTION
        final NetworkElementGroupDto neg = a4ResInv.getExistingNetworkElementGroup(negData.getUuid());
        assertNotNull(neg.getCreationTime());
        assertTrue(neg.getCreationTime().isBefore(oldDateTime), "creationTime (" + neg.getCreationTime() + ") is newer than " + oldDateTime + "!");
    }

    @Then("the NEG lastUpdateTime is updated")
    public void thenTheNEGLastUpdateTimeIsUpdated() {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementGroup negData = (A4NetworkElementGroup) testContext.getScenarioContext().getContext(Context.A4_NEG);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);

        // ACTION
        final NetworkElementGroupDto neg = a4ResInv.getExistingNetworkElementGroup(negData.getUuid());
        assertNotNull(neg.getLastUpdateTime());
        assertTrue(neg.getLastUpdateTime().isAfter(oldDateTime), "lastUpdateTime (" + neg.getLastUpdateTime() + ") is older than " + oldDateTime + "!");
    }

    @Then("the NEG lastUpdateTime is not updated")
    public void thenTheNEGLastUpdateTimeIsNotUpdated() {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementGroup negData = (A4NetworkElementGroup) testContext.getScenarioContext().getContext(Context.A4_NEG);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);

        // ACTION
        final NetworkElementGroupDto neg = a4ResInv.getExistingNetworkElementGroup(negData.getUuid());
        assertNotNull(neg.getLastUpdateTime());
        assertTrue(neg.getLastUpdateTime().isBefore(oldDateTime), "lastUpdateTime (" + neg.getLastUpdateTime() + ") is newer than " + oldDateTime + "!");
    }

    @Then("the NEG lastSuccessfulSyncTime property was updated")
    public void thenTheNEGLastSuccessfulSyncTimePropertyWasUpdated() {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementGroup neg = (A4NetworkElementGroup) testContext.getScenarioContext().getContext(Context.A4_NEG);
        final OffsetDateTime timeStamp = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);

        // ACTION
        sleepForSeconds(2);
        a4ResInv.checkNetworkElementGroupIsUpdatedWithLastSuccessfulSyncTime(neg, timeStamp);
    }

    @Then("the NEG now has the following properties:")
    public void thenTheNEGNowHasTheFollowingProperties(DataTable table) {
        final Map<String, String> negMap = table.asMap();

        final A4NetworkElementGroup neg = (A4NetworkElementGroup) testContext.getScenarioContext().getContext(Context.A4_NEG);
        final NetworkElementGroupDto negDtoActual = a4ResInv.getExistingNetworkElementGroup(neg.getUuid());

        final ObjectMapper om = testContext.getObjectMapper();

        // https://stackoverflow.com/questions/34957051/how-to-get-rid-of-type-safety-unchecked-cast-from-object-to-mapstring-string
        @SuppressWarnings("unchecked")
        Map<String, Object> negMapActual = om.convertValue(negDtoActual, Map.class);

        negMap.keySet().forEach(k -> {
                    System.out.println("+++ Property '" + k + "': Expected: '" + negMap.get(k) + "'; Actual: '" + negMapActual.get(k).toString() + '"');
                    assertEquals("Property '" + k + "' differs!", negMap.get(k), negMapActual.get(k).toString());
                }
        );
    }

    @Then("the (new )NE operationalState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNeOperationalStateIsUpdatedInA4ResInv(String operationalState) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElement neData = (A4NetworkElement) testContext.getScenarioContext().getContext(Context.A4_NE);

        // ACTION
        final NetworkElementDto ne = a4ResInv.getExistingNetworkElement(neData.getUuid());
        assertEquals(operationalState, ne.getOperationalState());
    }

    @Then("the (new )NE lifecycleState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNeLifecycleStateIsUpdatedInA4ResInv(String lifecycleState) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElement neData = (A4NetworkElement) testContext.getScenarioContext().getContext(Context.A4_NE);

        // ACTION
        final NetworkElementDto ne = a4ResInv.getExistingNetworkElement(neData.getUuid());
        assertEquals(lifecycleState, ne.getLifecycleState());
    }

    @Then("the NE lastUpdateTime is updated")
    public void thenTheNeLastUpdateTimeIsUpdated() {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElement neData = (A4NetworkElement) testContext.getScenarioContext().getContext(Context.A4_NE);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);

        // ACTION
        final NetworkElementDto ne = a4ResInv.getExistingNetworkElement(neData.getUuid());
        assertNotNull(ne.getLastUpdateTime());
        assertTrue(ne.getLastUpdateTime().isAfter(oldDateTime), "lastUpdateTime (" + ne.getLastUpdateTime() + ") is older than " + oldDateTime + "!");
    }

    @Then("the (new )NEP operationalState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNepOperationalStateIsUpdatedInA4ResInv(String operationalState) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementPort nepData = (A4NetworkElementPort) testContext.getScenarioContext().getContext(Context.A4_NEP);

        // ACTION
        final NetworkElementPortDto nep = a4ResInv.getExistingNetworkElementPort(nepData.getUuid());
        assertEquals(operationalState, nep.getOperationalState());
    }

    @Then("the (new )NEP operationalState is (now )deleted( in the A4 resource inventory)")
    public void thenTheNepOperationalStateIsDeletedInA4ResInv() {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementPort nepData = (A4NetworkElementPort) testContext.getScenarioContext().getContext(Context.A4_NEP);

        // ACTION
        final NetworkElementPortDto nep = a4ResInv.getExistingNetworkElementPort(nepData.getUuid());
        assertNull(nep.getOperationalState());
    }

    @Then("the (new )NEP description is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNEPDescriptionIsUpdatedTo(String newDescr) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementPort nepData = (A4NetworkElementPort) testContext.getScenarioContext().getContext(Context.A4_NEP);

        // ACTION
        final NetworkElementPortDto nep = a4ResInv.getExistingNetworkElementPort(nepData.getUuid());
        assertEquals(newDescr, nep.getDescription());
    }

    @Then("the (new )NEP description is (now )deleted( in the A4 resource inventory)")
    public void thenTheNEPDescriptionIsDeleted() {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementPort nepData = (A4NetworkElementPort) testContext.getScenarioContext().getContext(Context.A4_NEP);

        // ACTION
        final NetworkElementPortDto nep = a4ResInv.getExistingNetworkElementPort(nepData.getUuid());
        assertNull(nep.getDescription());
    }

    @Then("the NEP lastUpdateTime is updated")
    public void thenTheNEPLastUpdateTimeIsUpdated() {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementPort nepData = (A4NetworkElementPort) testContext.getScenarioContext().getContext(Context.A4_NEP);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);

        // ACTION
        final NetworkElementPortDto nep = a4ResInv.getExistingNetworkElementPort(nepData.getUuid());
        assertNotNull(nep.getLastUpdateTime());
        assertTrue(nep.getLastUpdateTime().isAfter(oldDateTime), "lastUpdateTime (" + nep.getLastUpdateTime() + ") is older than " + oldDateTime + "!");
    }

    @Then("the NEP lastUpdateTime is not updated")
    public void thenTheNEPLastUpdateTimeIsNotUpdated() {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementPort nepData = (A4NetworkElementPort) testContext.getScenarioContext().getContext(Context.A4_NEP);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);

        // ACTION
        final NetworkElementPortDto nep = a4ResInv.getExistingNetworkElementPort(nepData.getUuid());
        assertNotNull(nep.getLastUpdateTime());
        assertTrue(nep.getLastUpdateTime().isBefore(oldDateTime), "lastUpdateTime (" + nep.getLastUpdateTime() + ") is newer than " + oldDateTime + "!");
    }

    @Then("the (new )NEL operationalState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNelOperationalStateIsUpdatedInA4ResInv(String operationalState) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementLink nelData = (A4NetworkElementLink) testContext.getScenarioContext().getContext(Context.A4_NEL);

        // ACTION
        final NetworkElementLinkDto nel = a4ResInv.getExistingNetworkElementLink(nelData.getUuid());
        assertEquals(operationalState, nel.getOperationalState());
    }

    @Then("the (new )NEL lifecycleState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNelLifecycleStateIsUpdatedInA4ResInv(String lifecycleState) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementLink nelData = (A4NetworkElementLink) testContext.getScenarioContext().getContext(Context.A4_NEL);

        // ACTION
        final NetworkElementLinkDto nel = a4ResInv.getExistingNetworkElementLink(nelData.getUuid());
        assertEquals(lifecycleState, nel.getLifecycleState());
    }

    @Then("the NEL lastUpdateTime is updated")
    public void thenTheNelLastUpdateTimeIsUpdated() {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementLink nelData = (A4NetworkElementLink) testContext.getScenarioContext().getContext(Context.A4_NEL);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);

        // ACTION
        final NetworkElementLinkDto nel = a4ResInv.getExistingNetworkElementLink(nelData.getUuid());
        assertNotNull(nel.getLastUpdateTime());
        assertTrue(nel.getLastUpdateTime().isAfter(oldDateTime), "lastUpdateTime (" + nel.getLastUpdateTime() + ") is older than " + oldDateTime + "!");
    }

    @Then("the TP does exist in A4 resource inventory")
    public void thenTheTPDoesExistInA4ResourceInventory() {
        // INPUT FROM SCENARIO CONTEXT
        final A4TerminationPoint tp = (A4TerminationPoint) testContext.getScenarioContext().getContext(Context.A4_TP);

        // ACTION
        a4ResInv.checkTerminationPointExists(tp.getUuid());
    }

    @Then("the TP does not exist in A4 resource inventory( anymore)( any longer)")
    public void thenTheTPIsDoesNotExistInA4ResourceInventoryAnymore() {
        // INPUT FROM SCENARIO CONTEXT
        final A4TerminationPoint tp = (A4TerminationPoint) testContext.getScenarioContext().getContext(Context.A4_TP);

        // ACTION
        a4ResInv.checkTerminationPointIsDeleted(tp.getUuid());
    }

    @Then("a/the NSP FTTH connected to the TP does exist in A4 resource inventory")
    public void thenTheNspFtthConnectedToTpDoesExistInA4ResourceInventory() {
        // INPUT FROM SCENARIO CONTEXT
        final A4TerminationPoint tp = (A4TerminationPoint) testContext.getScenarioContext().getContext(Context.A4_TP);

        // ACTION
        final NetworkServiceProfileFtthAccessDto nspFtthDto = a4ResInv.checkNetworkServiceProfileFtthAccessConnectedToTerminationPointExists(tp.getUuid(), 1);
        final A4NetworkServiceProfileFtthAccess nspFtth = mapDtoToA4NspFtth(nspFtthDto);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NSP_FTTH, nspFtth);
    }

    @Then("the NSP FTTH does not exist in A4 resource inventory( anymore)( any longer)")
    public void thenTheNspFtthDoesNotExistInA4ResourceInventoryAnymore() {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkServiceProfileFtthAccess nspFtth = (A4NetworkServiceProfileFtthAccess) testContext.getScenarioContext().getContext(Context.A4_NSP_FTTH);

        // ACTION
        a4ResInv.checkNetworkServiceProfileFtthAccessIsDeleted(nspFtth.getUuid());
    }

    @Then("the (new )NSP L2BSA operationalState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNSPLBSAOperationalStateIsUpdatedInA4ResInv(String operationalState) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkServiceProfileL2Bsa nspL2Data = (A4NetworkServiceProfileL2Bsa) testContext.getScenarioContext().getContext(Context.A4_NSP_L2BSA);

        // ACTION
        final NetworkServiceProfileL2BsaDto nspL2 = a4ResInv.getExistingNetworkServiceProfileL2Bsa(nspL2Data.getUuid());
        assertEquals(operationalState, nspL2.getOperationalState());
    }

    @Then("the (new )NSP L2BSA lifecycleState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNSPLBSALifecycleStateIsUpdatedInA4ResInv(String lifecycleState) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkServiceProfileL2Bsa nspL2Data = (A4NetworkServiceProfileL2Bsa) testContext.getScenarioContext().getContext(Context.A4_NSP_L2BSA);

        // ACTION
        final NetworkServiceProfileL2BsaDto nspL2 = a4ResInv.getExistingNetworkServiceProfileL2Bsa(nspL2Data.getUuid());
        assertEquals(lifecycleState, nspL2.getLifecycleState());
    }

    @Then("the NSP L2BSA lastUpdateTime is updated")
    public void thenTheNSPLBSALastUpdateTimeIsUpdated() {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkServiceProfileL2Bsa nspL2BsaData = (A4NetworkServiceProfileL2Bsa) testContext.getScenarioContext().getContext(Context.A4_NSP_L2BSA);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);

        // ACTION
        final NetworkServiceProfileL2BsaDto nspL2Bsa = a4ResInv.getExistingNetworkServiceProfileL2Bsa(nspL2BsaData.getUuid());
        assertNotNull(nspL2Bsa.getLastUpdateTime());
        assertTrue(nspL2Bsa.getLastUpdateTime().isAfter(oldDateTime), "lastUpdateTime (" + nspL2Bsa.getLastUpdateTime() + ") is older than " + oldDateTime + "!");
    }

    @Then("the NSP L2BSA lastUpdateTime is not updated")
    public void thenTheNSPLBSALastUpdateTimeIsNotUpdated() {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkServiceProfileL2Bsa nspL2BsaData = (A4NetworkServiceProfileL2Bsa) testContext.getScenarioContext().getContext(Context.A4_NSP_L2BSA);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);

        // ACTION
        final NetworkServiceProfileL2BsaDto nspL2Bsa = a4ResInv.getExistingNetworkServiceProfileL2Bsa(nspL2BsaData.getUuid());
        assertNotNull(nspL2Bsa.getLastUpdateTime());
        assertTrue(nspL2Bsa.getLastUpdateTime().isBefore(oldDateTime), "lastUpdateTime (" + nspL2Bsa.getLastUpdateTime() + ") is newer than " + oldDateTime + "!");
    }

    // -----=====[ HELPERS ]=====-----

    private A4NetworkElementGroup setupDefaultNegTestData() {
        // ACTION
        A4NetworkElementGroup neg = testContext.getOsrTestContext().getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        neg.setUuid(UUID.randomUUID().toString());

        return neg;
    }

    private A4NetworkElement setupDefaultNeTestData() {
        // INPUT FROM SCENARIO CONTEXT
        final boolean NEG_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NEG);

        // ACTION

        // NE needs to be connected to a NEG, so if no NEG present, create one
        if (!NEG_PRESENT)
            givenANEGIsExistingInA4ResourceInventory();

        A4NetworkElement ne = testContext.getOsrTestContext().getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        ne.setUuid(UUID.randomUUID().toString());

        return ne;
    }

    private A4NetworkElementPort setupDefaultNepTestData() {
        // INPUT FROM SCENARIO CONTEXT
        final boolean NE_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NE);

        // ACTION

        // NEP needs to be connected to a NE, so if no NE present, create one
        if (!NE_PRESENT)
            givenANeIsExistingInA4ResourceInventory();

        A4NetworkElementPort nep = testContext.getOsrTestContext().getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
        nep.setUuid(UUID.randomUUID().toString());

        return nep;
    }

    private A4TerminationPoint setupDefaultTpTestData() {
        // INPUT FROM SCENARIO CONTEXT
        final boolean NEP_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NEP);

        // ACTION

        // TP needs to be connected to a NEP, so if no NEP present, create one
        if (!NEP_PRESENT)
            givenANEPIsExistingInA4ResourceInventory();

        A4TerminationPoint tp = testContext.getOsrTestContext().getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.TerminationPointB);
        tp.setUuid(UUID.randomUUID().toString());

        return tp;
    }

    private A4NetworkElementLink setupDefaultNelTestData() {
        // INPUT FROM SCENARIO CONTEXT
        final boolean NE_A_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NE);
        final boolean NE_B_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NE_B);
        final boolean NEP_A_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NEP);
        final boolean NEP_B_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NEP_B);

        // ACTION

        // NEL needs to be connected to 2 NEP, so if no NEP present, create one
        if (!NE_A_PRESENT)
            givenANeIsExistingInA4ResourceInventory();
        if (!NE_B_PRESENT)
            givenASecondNeIsExistingInA4ResourceInventory();
        if (!NEP_A_PRESENT)
            givenANEPIsExistingInA4ResourceInventory();
        if (!NEP_B_PRESENT)
            givenASecondNEPIsExistingInA4ResourceInventory();

        A4NetworkElementLink nel = testContext.getOsrTestContext().getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.defaultNetworkElementLink);
        nel.setUuid(UUID.randomUUID().toString());

        return nel;
    }

    private A4NetworkServiceProfileFtthAccess setupDefaultNspFtthTestData() {
        // INPUT FROM SCENARIO CONTEXT
        final boolean TP_PRESENT = testContext.getScenarioContext().isContains(Context.A4_TP);

        // ACTION

        // NSP needs to be connected to a TP, so if no TP present, create one
        if (!TP_PRESENT)
            givenATPIsExistingInA4ResourceInventory();

        A4NetworkServiceProfileFtthAccess nspFtth = testContext.getOsrTestContext().getData()
                .getA4NetworkServiceProfileFtthAccessDataProvider()
                .get(A4NetworkServiceProfileFtthAccessCase.defaultNetworkServiceProfileFtthAccess);
        nspFtth.setUuid(UUID.randomUUID().toString());

        return nspFtth;
    }

    private A4NetworkServiceProfileL2Bsa setupDefaultNspL2BsaTestData() {
        // INPUT FROM SCENARIO CONTEXT
        final boolean TP_PRESENT = testContext.getScenarioContext().isContains(Context.A4_TP);

        // ACTION

        // NSP needs to be connected to a TP, so if no TP present, create one
        if (!TP_PRESENT)
            givenATPIsExistingInA4ResourceInventory();

        A4NetworkServiceProfileL2Bsa nspL2Bsa = testContext.getOsrTestContext().getData()
                .getA4NetworkServiceProfileL2BsaDataProvider()
                .get(A4NetworkServiceProfileL2BsaCase.defaultNetworkServiceProfileL2Bsa);
        nspL2Bsa.setUuid(UUID.randomUUID().toString());

        return nspL2Bsa;
    }

    private A4NetworkElementGroup mapDtoToNeg(NetworkElementGroupDto negDto) {
        A4NetworkElementGroup neg = new A4NetworkElementGroup();
        neg.setUuid(negDto.getUuid());
        neg.setType(negDto.getType());
        neg.setName(negDto.getName());
        neg.setOperationalState(negDto.getOperationalState());
        neg.setLifecycleState(negDto.getLifecycleState());
        neg.setCreationTime(Objects.requireNonNull(negDto.getCreationTime()).toString());
        neg.setLastUpdateTime(Objects.requireNonNull(negDto.getLastUpdateTime()).toString());
        neg.setLastSuccessfulSyncTime(Objects.requireNonNull(negDto.getLastSuccessfulSyncTime()).toString());

        return neg;
    }

    private A4NetworkServiceProfileFtthAccess mapDtoToA4NspFtth(NetworkServiceProfileFtthAccessDto nspFtthDto) {
        A4NetworkServiceProfileFtthAccess nspFtth = new A4NetworkServiceProfileFtthAccess();
        nspFtth.setUuid(nspFtthDto.getUuid());
        nspFtth.setLineId(nspFtthDto.getLineId());
        nspFtth.setLifecycleState(nspFtthDto.getLifecycleState());
        nspFtth.setOperationalState(nspFtthDto.getOperationalState());
        nspFtth.setOntSerialNumber(nspFtthDto.getOntSerialNumber());
        nspFtth.setOltPortOntLastRegisteredOn(nspFtthDto.getOltPortOntLastRegisteredOn());
        nspFtth.setTerminationPointUuid(nspFtthDto.getTerminationPointFtthAccessUuid());

        return nspFtth;
    }

}
