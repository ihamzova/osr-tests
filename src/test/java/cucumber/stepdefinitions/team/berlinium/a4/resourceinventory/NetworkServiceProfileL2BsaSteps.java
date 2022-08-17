package cucumber.stepdefinitions.team.berlinium.a4.resourceinventory;

import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryMapper;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkServiceProfileL2BsaDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.TerminationPointDto;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.time.OffsetDateTime;
import java.util.UUID;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.DEFAULT;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getRandomDigits;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

public class NetworkServiceProfileL2BsaSteps {

    private final A4ResourceInventoryRobot a4ResInv;
    private final A4ResourceInventoryMapper a4ResInvMapper;
    private final TerminationPointSteps a4TpSteps;
    private final TestContext testContext;

    public NetworkServiceProfileL2BsaSteps(TestContext testContext,
                                           A4ResourceInventoryRobot a4ResInv,
                                           A4ResourceInventoryMapper a4ResInvMapper,
                                           TerminationPointSteps a4TpSteps) {
        this.testContext = testContext;
        this.a4ResInv = a4ResInv;
        this.a4ResInvMapper = a4ResInvMapper;
        this.a4TpSteps = a4TpSteps;
    }


    // -----=====[ GIVENS ]=====-----

    @Given("a NSP L2BSA( connected to the TP)( is existing)( in A4 resource inventory)")
    public void givenA4NspL2Bsa() {
        createNspL2Bsa(DEFAULT, DEFAULT);
    }

    @Given("a NSP L2BSA connected to TP {string}( is existing)( in A4 resource inventory)")
    public void givenA4NspL2Bsa(String tpAlias) {
        createNspL2Bsa(DEFAULT, tpAlias);
    }

    @Given("{int} TP(s)( with identical carrierBsaReference) and NSP(s) L2BSA with lifecycleState {string} connected to NEG {string}( is existing)( are existing)( in A4 resource inventory)")
    public void givenMultipleA4TPAndNspL2Bsa(int number, String lifecycleState, String negAlias) {
        final String carrierBsaReference = "CarrierBsaReference" + getRandomDigits(6);

        for (int i = 0; i < number; i++)
            createTpAndNspL2BsaWithLcState(DEFAULT + i, carrierBsaReference, lifecycleState, negAlias);
    }

    @Given("a/another NSP L2BSA {string} connected to TP {string}( is existing)( in A4 resource inventory)")
    public void givenA4NspL2Bsa(String nspAlias, String tpAlias) {
        createNspL2Bsa(nspAlias, tpAlias);
    }

    @Given("a NSP L2BSA with lifecycleState {string}( connected to the TP)( is existing)( in A4 resource inventory)")
    public void givenA4NspL2BsaWithLcState(String lcState) {
        createNspL2BsaWithLcState(DEFAULT, lcState, DEFAULT);
    }

    @Given("a/another NSP L2BSA {string} with lifecycleState {string}( connected to the TP)( is existing)( in A4 resource inventory)")
    public void givenA4NspL2BsaWithLcState(String nspAlias, String lcState) {
        createNspL2BsaWithLcState(nspAlias, lcState, DEFAULT);
    }

    @Given("a/another NSP L2BSA {string} with lifecycleState {string} connected to TP {string}( is existing)( in A4 resource inventory)")
    public void givenA4NspL2BsaWithLcState(String nspAlias, String lcState, String tpAlias) {
        createNspL2BsaWithLcState(nspAlias, lcState, tpAlias);
    }

    @Given("a NSP L2BSA with operationalState {string}( connected to the TP)( is existing)( in A4 resource inventory)")
    public void givenNspL2BsaWithLineIDIsExistingInA4ResourceInventoryForTheTP(String operationalState) {
        createNspL2BsaWithOpState(DEFAULT, operationalState, DEFAULT);
    }

    @Given("a/another NSP L2BSA with operationalState {string} connected to TP {string}( is existing)( in A4 resource inventory)")
    public void givenNspL2BsaWithLineID(String operationalState, String tpAlias) {
        createNspL2BsaWithOpState(DEFAULT, operationalState, tpAlias);
    }

    @Given("a/another NSP L2BSA {string} with operationalState {string} connected to TP {string}( is existing)( in A4 resource inventory)")
    public void givenNspL2BsaWithLineID(String nspAlias, String operationalState, String tpAlias) {
        createNspL2BsaWithOpState(nspAlias, operationalState, tpAlias);
    }

    @Given("a NSP L2BSA with operationalState {string} and lifecycleState {string}(connected to the TP)( is existing)( in A4 resource inventory)")
    public void givenNspL2BsaWithLineIDIsExistingInA4ResourceInventoryForTheTP(String operationalState, String lifecycleState) {
        createNspL2BsaWithStates(DEFAULT, operationalState, lifecycleState, DEFAULT);
    }

    @Given("a/another NSP L2BSA with operationalState {string} and lifecycleState {string} connected to TP {string}( is existing)( in A4 resource inventory)")
    public void givenNspL2BsaWithLineIDIsExistingInA4ResourceInventoryForTheTP(String operationalState, String lifecycleState, String tpAlias) {
        createNspL2BsaWithStates(DEFAULT, operationalState, lifecycleState, tpAlias);
    }

    @Given("a/another NSP L2BSA {string} with operationalState {string} and lifecycleState {string} connected to TP {string}( is existing)( in A4 resource inventory)")
    public void givenNspL2BsaWithLineIDIsExistingInA4ResourceInventoryForTheTP(String nspAlias, String operationalState, String lifecycleState, String tpAlias) {
        createNspL2BsaWithStates(nspAlias, operationalState, lifecycleState, tpAlias);
    }

    @Given("no NSP L2BSA( connected to the TP)( exists)( in A4 resource inventory)")
    public void givenNoNspL2BsaExistsInA4ResourceInventoryForTheTP() {
        NetworkServiceProfileL2BsaDto nspL2Bsa = new NetworkServiceProfileL2BsaDto();
        nspL2Bsa.setUuid(UUID.randomUUID().toString());

        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteNetworkServiceProfileL2BsaWithoutCheck(nspL2Bsa.getUuid());

        testContext.getScenarioContext().setContext(Context.A4_NSP_L2BSA, nspL2Bsa);
    }


    // -----=====[ THENS ]=====-----

    @Then("the NSP L2BSA (does )(still )exist(s)( in A4 resource inventory)")
    public void thenA4NspL2BsaExist() {
        final NetworkServiceProfileL2BsaDto nspL2Bsa = (NetworkServiceProfileL2BsaDto) testContext.getScenarioContext().getContext(Context.A4_NSP_L2BSA);

        a4ResInv.getExistingNetworkServiceProfileL2Bsa(nspL2Bsa.getUuid());
    }

    @Then("the NSP L2BSA {string} (does )(still )exist(s)( in A4 resource inventory)")
    public void thenA4NspL2BsaExist(String alias) {
        final NetworkServiceProfileL2BsaDto nspL2Bsa = (NetworkServiceProfileL2BsaDto) testContext.getScenarioContext().getContext(Context.A4_NSP_L2BSA, alias);

        a4ResInv.getExistingNetworkServiceProfileL2Bsa(nspL2Bsa.getUuid());
    }

    @Then("the (new )NSP L2BSA operationalState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNSPLBSAOperationalStateIsUpdatedInA4ResInv(String operationalState) {
        final NetworkServiceProfileL2BsaDto nspL2Data = (NetworkServiceProfileL2BsaDto) testContext.getScenarioContext().getContext(Context.A4_NSP_L2BSA);
        final NetworkServiceProfileL2BsaDto nspL2 = a4ResInv.getExistingNetworkServiceProfileL2Bsa(nspL2Data.getUuid());

        assertEquals(operationalState, nspL2.getOperationalState());
    }

    @Then("the (new )NSP L2BSA lifecycleState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNSPLBSALifecycleStateIsUpdatedInA4ResInv(String lifecycleState) {
        final NetworkServiceProfileL2BsaDto nspL2Data = (NetworkServiceProfileL2BsaDto) testContext.getScenarioContext().getContext(Context.A4_NSP_L2BSA);
        final NetworkServiceProfileL2BsaDto nspL2 = a4ResInv.getExistingNetworkServiceProfileL2Bsa(nspL2Data.getUuid());

        assertEquals(lifecycleState, nspL2.getLifecycleState());
    }

    @Then("the NSP L2BSA lastUpdateTime is updated")
    public void thenTheNSPLBSALastUpdateTimeIsUpdated() {
        final NetworkServiceProfileL2BsaDto nspL2BsaData = (NetworkServiceProfileL2BsaDto) testContext.getScenarioContext().getContext(Context.A4_NSP_L2BSA);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);
        final NetworkServiceProfileL2BsaDto nspL2Bsa = a4ResInv.getExistingNetworkServiceProfileL2Bsa(nspL2BsaData.getUuid());

        assertNotNull(nspL2Bsa.getLastUpdateTime());
        assertTrue(nspL2Bsa.getLastUpdateTime().isAfter(oldDateTime), "lastUpdateTime (" + nspL2Bsa.getLastUpdateTime() + ") is older than " + oldDateTime + "!");
    }

    @Then("the NSP L2BSA lastUpdateTime is not updated")
    public void thenTheNSPLBSALastUpdateTimeIsNotUpdated() {
        final NetworkServiceProfileL2BsaDto nspL2BsaData = (NetworkServiceProfileL2BsaDto) testContext.getScenarioContext().getContext(Context.A4_NSP_L2BSA);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);
        final NetworkServiceProfileL2BsaDto nspL2Bsa = a4ResInv.getExistingNetworkServiceProfileL2Bsa(nspL2BsaData.getUuid());

        assertNotNull(nspL2Bsa.getLastUpdateTime());
        assertTrue(nspL2Bsa.getLastUpdateTime().isBefore(oldDateTime), "lastUpdateTime (" + nspL2Bsa.getLastUpdateTime() + ") is newer than " + oldDateTime + "!");
    }

    @Then("the NSP L2BSA does not exist( in A4 resource inventory)( anymore)( any longer)")
    public void thenNspL2BsaNotExist() {
        final NetworkServiceProfileL2BsaDto nspL2Bsa = (NetworkServiceProfileL2BsaDto) testContext.getScenarioContext().getContext(Context.A4_NSP_L2BSA);

        a4ResInv.checkNetworkServiceProfileL2BsaIsDeleted(nspL2Bsa.getUuid());
    }

    @Then("the NSP L2BSA {string} does not exist( in A4 resource inventory)( anymore)( any longer)")
    public void thenNspL2BsaNotExist(String alias) {
        final NetworkServiceProfileL2BsaDto nspL2Bsa = (NetworkServiceProfileL2BsaDto) testContext.getScenarioContext().getContext(Context.A4_NSP_L2BSA, alias);

        a4ResInv.checkNetworkServiceProfileL2BsaIsDeleted(nspL2Bsa.getUuid());
    }


    // -----=====[ HELPERS ]=====-----

    private NetworkServiceProfileL2BsaDto setupDefaultNspL2BsaTestData(String tpAlias) {
        final boolean TP_PRESENT = testContext.getScenarioContext().isContains(Context.A4_TP, tpAlias);

        // NSP needs to be connected to a TP, so if no TP present, create one
        if (!TP_PRESENT)
            a4TpSteps.givenATPIsExistingInA4ResourceInventory(tpAlias);

        final TerminationPointDto tp = (TerminationPointDto) testContext.getScenarioContext().getContext(Context.A4_TP, tpAlias);
        return a4ResInvMapper.getNetworkServiceProfileL2BsaDto(tp.getUuid());
    }

    private void persistNspL2Bsa(String nspAlias, NetworkServiceProfileL2BsaDto nspL2Bsa) {
        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteNspsL2Bsa(nspL2Bsa.getLineId());

        a4ResInv.createNetworkServiceProfileL2Bsa(nspL2Bsa);

        testContext.getScenarioContext().setContext(Context.A4_NSP_L2BSA, nspAlias, nspL2Bsa);
    }

    private void createNspL2Bsa(String nspAlias, String tpAlias) {
        NetworkServiceProfileL2BsaDto nspL2Bsa = setupDefaultNspL2BsaTestData(tpAlias);

        persistNspL2Bsa(nspAlias, nspL2Bsa);
    }

    private void createNspL2BsaWithLcState(String nspAlias, String lcState, String tpAlias) {
        NetworkServiceProfileL2BsaDto nspL2Bsa = setupDefaultNspL2BsaTestData(tpAlias);
        nspL2Bsa.setLifecycleState(lcState);

        persistNspL2Bsa(nspAlias, nspL2Bsa);
    }

    private void createTpAndNspL2BsaWithLcState(String alias, String tpCarrierBsaReference, String nspLifecycleState, String negAlias) {
        TerminationPointDto tp = a4TpSteps.setupDefaultTpTestDataConnectedToNeg(negAlias);
        tp.setType("L2BSA_TP");
        tp.setCarrierBsaReference(tpCarrierBsaReference);
        a4TpSteps.persistTp(alias, tp);

        NetworkServiceProfileL2BsaDto nspL2Bsa = setupDefaultNspL2BsaTestData(alias);
        nspL2Bsa.setLifecycleState(nspLifecycleState);
        persistNspL2Bsa(alias, nspL2Bsa);
    }

    private void createNspL2BsaWithOpState(String nspAlias, String opState, String tpAlias) {
        NetworkServiceProfileL2BsaDto nspL2Bsa = setupDefaultNspL2BsaTestData(tpAlias);
        nspL2Bsa.setOperationalState(opState);

        persistNspL2Bsa(nspAlias, nspL2Bsa);
    }

    private void createNspL2BsaWithStates(String nspAlias, String opState, String lcState, String tpAlias) {
        NetworkServiceProfileL2BsaDto nspL2Bsa = setupDefaultNspL2BsaTestData(tpAlias);
        nspL2Bsa.setOperationalState(opState);
        nspL2Bsa.setLifecycleState(lcState);

        persistNspL2Bsa(nspAlias, nspL2Bsa);
    }

}
