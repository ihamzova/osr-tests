package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofileftthaccess.A4NetworkServiceProfileFtthAccessCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilel2bsa.A4NetworkServiceProfileL2BsaCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementGroupDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementPortDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkServiceProfileFtthAccessDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkServiceProfileL2BsaDto;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.time.OffsetDateTime;
import java.util.UUID;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getRandomDigits;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

public class A4ResInvSteps {

    private final A4ResourceInventoryRobot a4ResInv = new A4ResourceInventoryRobot();
    private final TestContext testContext;

    public A4ResInvSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    @After
    public void cleanup() {
        // ATTENTION: If at any time more than 1 NEG is used for tests, the additional ones have to be added here!

        A4NetworkElementGroup neg = null;

        // INPUT FROM SCENARIO CONTEXT
        final boolean NEG_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NEG);
        if (NEG_PRESENT)
            neg = (A4NetworkElementGroup) testContext.getScenarioContext().getContext(Context.A4_NEG);

        // ACTION
        if (NEG_PRESENT)
            a4ResInv.deleteA4NetworkElementGroupsRecursively(neg);
    }

    // -----=====[ GIVENS ]=====-----

    @Given("a NEG is existing in A4 resource inventory")
    public void givenANEGIsExistingInA4ResourceInventory() {
        // ACTION
        A4NetworkElementGroup neg = setupDefaultNegTestData();
        a4ResInv.createNetworkElementGroup(neg);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NEG, neg);
    }

    @Given("a NEG with name {string} is existing in A4 resource inventory")
    public void givenANEGWithNameIsExistingInA4ResourceInventory(String name) {
        // ACTION
        A4NetworkElementGroup neg = setupDefaultNegTestData();
        neg.setName(name);
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
        a4ResInv.createNetworkElementGroup(neg);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NEG, neg);
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

    @Given("a NE is existing in A4 resource inventory")
    public void givenANeIsExistingInA4ResourceInventory() {
        // ACTION
        A4NetworkElement ne = setupDefaultNeTestData();

        final A4NetworkElementGroup neg = (A4NetworkElementGroup) testContext.getScenarioContext().getContext(Context.A4_NEG);

        a4ResInv.createNetworkElement(ne, neg);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NE, ne);
    }

    @Given("a NE with VPSZ {string} and FSZ {string} is existing in A4 resource inventory")
    public void givenANeWithVpszAndFszIsExistingInA4ResourceInventory(String vpsz, String fsz) {
        // ACTION
        A4NetworkElement ne = setupDefaultNeTestData();
        ne.setVpsz(vpsz);
        ne.setFsz(fsz);

        final A4NetworkElementGroup neg = (A4NetworkElementGroup) testContext.getScenarioContext().getContext(Context.A4_NEG);

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

        a4ResInv.createNetworkElementPort(nep, ne);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NEP, nep);
    }

    @Given("a NEP with operational state {string} and and description {string} is existing in A4 resource inventory")
    public void givenANEPWithOperationalStateAndAndDescriptionIsExistingInAResourceInventory(String opState, String descr) {
        // ACTION
        A4NetworkElementPort nep = setupDefaultNepTestData();
        nep.setOperationalState(opState);
        nep.setDescription(descr);

        final A4NetworkElement ne = (A4NetworkElement) testContext.getScenarioContext().getContext(Context.A4_NE);

        a4ResInv.createNetworkElementPort(nep, ne);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NEP, nep);
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

    @And("the NEG lastUpdateTime is not updated")
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

    @And("the NEP lastUpdateTime is not updated")
    public void thenTheNEPLastUpdateTimeIsNotUpdated() {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementPort nepData = (A4NetworkElementPort) testContext.getScenarioContext().getContext(Context.A4_NEP);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);

        // ACTION
        final NetworkElementPortDto nep = a4ResInv.getExistingNetworkElementPort(nepData.getUuid());
        assertNotNull(nep.getLastUpdateTime());
        assertTrue(nep.getLastUpdateTime().isBefore(oldDateTime), "lastUpdateTime (" + nep.getLastUpdateTime() + ") is newer than " + oldDateTime + "!");
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

    @Then("the (new )NSP L2BSA operationalState is (now )(updated to ){string}( in the A4 resource inventory)")
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
        neg.setName("neg integration test name " + getRandomDigits(6));

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
