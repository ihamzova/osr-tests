package cucumber.stepdefinitions.team.berlinium.a4.resourceinventory;

import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryMapper;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkServiceProfileFtthAccessDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.TerminationPointDto;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.time.OffsetDateTime;
import java.util.UUID;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.DEFAULT;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

public class NetworkServiceProfileFtthAccessSteps {

    private final A4ResourceInventoryRobot a4ResInv;
    private final A4ResourceInventoryMapper a4ResInvMapper;
    private final TerminationPointSteps a4TpSteps;
    private final TestContext testContext;

    public NetworkServiceProfileFtthAccessSteps(TestContext testContext,
                                                A4ResourceInventoryRobot a4ResInv,
                                                A4ResourceInventoryMapper a4ResInvMapper,
                                                TerminationPointSteps a4TpSteps) {
        this.testContext = testContext;
        this.a4ResInv = a4ResInv;
        this.a4ResInvMapper = a4ResInvMapper;
        this.a4TpSteps = a4TpSteps;
    }


    // -----=====[ GIVENS ]=====-----

    @Given("a NSP FTTH(-Access) with Line ID {string}( connected to the TP)( is existing)( in A4 resource inventory)")
    public void givenANSPFTTHWithLineIDIsExistingInA4ResourceInventoryForTheTP(String lineId) {
        createNspFtth(DEFAULT, lineId, DEFAULT);
    }

    @Given("a/another NSP FTTH(-Access) with Line ID {string} connected to TP {string}( is existing)( in A4 resource inventory)")
    public void givenANSPFTTHWithLineIDIsExistingInA4ResourceInventoryForTheTP(String lineId, String tpAlias) {
        createNspFtth(DEFAULT, lineId, tpAlias);
    }

    @Given("a/another NSP FTTH(-Access) {string} with Line ID {string} connected to TP {string}( is existing)( in A4 resource inventory)")
    public void givenANSPFTTHWithLineIDIsExistingInA4ResourceInventoryForTheTP(String nspAlias, String lineId, String tpAlias) {
        createNspFtth(nspAlias, lineId, tpAlias);
    }

    @Given("a NSP FTTH-Access with operational state {string} and NEP reference {string}( connected to the TP)( is existing)( in A4 resource inventory)")
    public void givenANspFtthAccessWithOperationalStateAndNepReferenceIsExistingInAResourceInventory(String opState, String portUuid) {
        createNspFtthWithRef(DEFAULT, opState, portUuid, DEFAULT);
    }

    @Given("a/another NSP FTTH-Access with operational state {string} and NEP reference {string} connected to TP {string}( is existing)( in A4 resource inventory)")
    public void givenANspFtthAccessWithOperationalStateAndNepReferenceIsExistingInAResourceInventory(String opState, String portUuid, String tpAlias) {
        createNspFtthWithRef(DEFAULT, opState, portUuid, tpAlias);
    }

    @Given("a/another NSP FTTH-Access {string} with operational state {string} and NEP reference {string} connected to TP {string}( is existing)( in A4 resource inventory)")
    public void givenANspFtthAccessWithOperationalStateAndNepReferenceIsExistingInAResourceInventory(String nspAlias, String opState, String portUuid, String tpAlias) {
        createNspFtthWithRef(nspAlias, opState, portUuid, tpAlias);
    }

    @Given("a NSP FTTH-Access with operationalState {string} and lifecycleState {string}( connected to the TP)( is existing)( in A4 resource inventory)")
    public void givenNspFtthAccessWithLineIDIsExistingInA4ResourceInventoryForTheTP(String operationalState, String lifecycleState) {
        createNspFtthWithStates(DEFAULT, operationalState, lifecycleState, DEFAULT);
    }

    @Given("a/another NSP FTTH-Access with operationalState {string} and lifecycleState {string} connected to TP {string}( is existing)( in A4 resource inventory)")
    public void givenNspFtthAccessWithLineIDIsExistingInA4ResourceInventoryForTheTP(String operationalState, String lifecycleState, String tpAlias) {
        createNspFtthWithStates(DEFAULT, operationalState, lifecycleState, tpAlias);
    }

    @Given("a/another NSP FTTH-Access {string} with operationalState {string} and lifecycleState {string} connected to TP {string}( is existing)( in A4 resource inventory)")
    public void givenNspFtthAccessWithLineIDIsExistingInA4ResourceInventoryForTheTP(String nspAlias, String operationalState, String lifecycleState, String tpAlias) {
        createNspFtthWithStates(nspAlias, operationalState, lifecycleState, tpAlias);
    }

    @Given("no NSP FTTH(-Access)( connected to the TP)( exists in A4)( resource inventory)")
    public void givenNoNSPFTTHExistsInA4ResourceInventoryForTheTP() {
        NetworkServiceProfileFtthAccessDto nspFtth = new NetworkServiceProfileFtthAccessDto();
        nspFtth.setUuid(UUID.randomUUID().toString());

        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteNspFtthAccess(nspFtth);

        testContext.getScenarioContext().setContext(Context.A4_NSP_FTTH, nspFtth);
    }


    // -----=====[ THENS ]=====-----

    @Then("the (new )NSP FTTH-Access operationalState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNspFtthAccessOperationalStateIsUpdatedInA4ResInv(String operationalState) {
        final NetworkServiceProfileFtthAccessDto nspFtthAccessData = (NetworkServiceProfileFtthAccessDto) testContext
                .getScenarioContext().getContext(Context.A4_NSP_FTTH);
        final NetworkServiceProfileFtthAccessDto nspFtthAccess = a4ResInv
                .getExistingNetworkServiceProfileFtthAccess(nspFtthAccessData.getUuid());

        assertEquals(operationalState, nspFtthAccess.getOperationalState());
    }

    @Then("the (new )NSP FTTH-Access NEP reference is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNspFtthAccessNepReferenceIsUpdatedTo(String portUuid) {
        final NetworkServiceProfileFtthAccessDto nspFtthAccessData = (NetworkServiceProfileFtthAccessDto) testContext
                .getScenarioContext().getContext(Context.A4_NSP_FTTH);
        final NetworkServiceProfileFtthAccessDto nspFtthAccess = a4ResInv
                .getExistingNetworkServiceProfileFtthAccess(nspFtthAccessData.getUuid());

        assertEquals(portUuid, nspFtthAccess.getOltPortOntLastRegisteredOn());
    }

    @Then("the (new )NSP FTTH-Access lifecycleState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNspFtthAccessLifecycleStateIsUpdatedInA4ResInv(String lifecycleState) {
        final NetworkServiceProfileFtthAccessDto nspFtthAccessData = (NetworkServiceProfileFtthAccessDto) testContext
                .getScenarioContext().getContext(Context.A4_NSP_FTTH);
        final NetworkServiceProfileFtthAccessDto nspFtthAccess = a4ResInv
                .getExistingNetworkServiceProfileFtthAccess(nspFtthAccessData.getUuid());

        assertEquals(lifecycleState, nspFtthAccess.getLifecycleState());
    }

    @Then("the NSP FTTH-Access lastUpdateTime is updated")
    public void thenTheNspFtthAccessLastUpdateTimeIsUpdated() {
        final NetworkServiceProfileFtthAccessDto nspFtthAccessData = (NetworkServiceProfileFtthAccessDto) testContext
                .getScenarioContext().getContext(Context.A4_NSP_FTTH);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);
        final NetworkServiceProfileFtthAccessDto nspFtthAccess = a4ResInv
                .getExistingNetworkServiceProfileFtthAccess(nspFtthAccessData.getUuid());

        assertNotNull(nspFtthAccess.getLastUpdateTime());
        assertTrue(nspFtthAccess.getLastUpdateTime().isAfter(oldDateTime), "lastUpdateTime (" + nspFtthAccess.getLastUpdateTime() + ") is older than " + oldDateTime + "!");
    }

    @Then("a/the NSP FTTH connected to the TP does exist in A4 resource inventory")
    public void thenTheNspFtthConnectedToTpDoesExistInA4ResourceInventory() {
        final TerminationPointDto tp = (TerminationPointDto) testContext.getScenarioContext().getContext(Context.A4_TP);
        final NetworkServiceProfileFtthAccessDto nspFtthDto = a4ResInv.checkNetworkServiceProfileFtthAccessConnectedToTerminationPointExists(tp.getUuid(), 1);

        testContext.getScenarioContext().setContext(Context.A4_NSP_FTTH, nspFtthDto);
    }

    @Then("the NSP FTTH does not exist in A4 resource inventory( anymore)( any longer)")
    public void thenTheNspFtthDoesNotExistInA4ResourceInventoryAnymore() {
        final NetworkServiceProfileFtthAccessDto nspFtth = (NetworkServiceProfileFtthAccessDto) testContext.getScenarioContext().getContext(Context.A4_NSP_FTTH);

        a4ResInv.checkNetworkServiceProfileFtthAccessIsDeleted(nspFtth.getUuid());
    }


    // -----=====[ HELPERS ]=====-----

    private NetworkServiceProfileFtthAccessDto setupDefaultNspFtthTestData(String tpAlias) {
        final boolean TP_PRESENT = testContext.getScenarioContext().isContains(Context.A4_TP, tpAlias);

        // NSP needs to be connected to a TP, so if no TP present, create one
        if (!TP_PRESENT)
            a4TpSteps.givenATPIsExistingInA4ResourceInventory(tpAlias);

        final TerminationPointDto tp = (TerminationPointDto) testContext.getScenarioContext().getContext(Context.A4_TP, tpAlias);
        return a4ResInvMapper.getNetworkServiceProfileFtthAccessDto(tp.getUuid());
    }

    private void persistNspFtth(String nspAlias, NetworkServiceProfileFtthAccessDto nspFtth) {
        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteNspFtthAccess(nspFtth);

        a4ResInv.createNetworkServiceProfileFtthAccess(nspFtth);
        testContext.getScenarioContext().setContext(Context.A4_NSP_FTTH, nspAlias, nspFtth);
    }

    private void createNspFtth(String nspAlias, String lineId, String tpAlias) {
        NetworkServiceProfileFtthAccessDto nspFtth = setupDefaultNspFtthTestData(tpAlias);
        nspFtth.setLineId(lineId);

        persistNspFtth(nspAlias, nspFtth);
    }

    private void createNspFtthWithRef(String nspAlias, String opState, String nepRef, String tpAlias) {
        NetworkServiceProfileFtthAccessDto nspFtth = setupDefaultNspFtthTestData(tpAlias);
        nspFtth.setOperationalState(opState);
        nspFtth.setOltPortOntLastRegisteredOn(nepRef);

        persistNspFtth(nspAlias, nspFtth);
    }

    private void createNspFtthWithStates(String nspAlias, String opState, String lcState, String tpAlias) {
        NetworkServiceProfileFtthAccessDto nspFtth = setupDefaultNspFtthTestData(tpAlias);
        nspFtth.setOperationalState(opState);
        nspFtth.setLifecycleState(lcState);

        persistNspFtth(nspAlias, nspFtth);
    }

}
