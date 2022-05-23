package cucumber.stepdefinitions.team.berlinium.a4.resourceinventory;

import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryMapper;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementLinkDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementPortDto;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.time.OffsetDateTime;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.DEFAULT;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.DEFAULT_B;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

public class NetworkElementLinkSteps {

    private final A4ResourceInventoryRobot a4ResInv;
    private final A4ResourceInventoryMapper a4ResInvMapper;
    private final NetworkElementPortSteps a4NepSteps;
    private final TestContext testContext;

    public NetworkElementLinkSteps(TestContext testContext,
                                   A4ResourceInventoryRobot a4ResInv,
                                   A4ResourceInventoryMapper a4ResInvMapper,
                                   NetworkElementPortSteps a4NepSteps) {
        this.testContext = testContext;
        this.a4ResInv = a4ResInv;
        this.a4ResInvMapper = a4ResInvMapper;
        this.a4NepSteps = a4NepSteps;
    }


    // -----=====[ GIVENS ]=====-----

    @Given("a NEL( is existing)( in A4 resource inventory)")
    public void givenANELIsExistingInA4ResourceInventory() {
        createNel(DEFAULT, DEFAULT, DEFAULT);
    }

    @Given("a/another NEL {string}( is existing)( in A4 resource inventory)")
    public void givenANELIsExistingInA4ResourceInventory(String nelAlias) {
        createNel(nelAlias, DEFAULT, DEFAULT);
    }

    @Given("a/another NEL connected to NEPs {string} and {string}( is existing)( in A4 resource inventory)")
    public void givenANELIsExistingInA4ResourceInventory(String nepAlias1, String nepAlias2) {
        createNel(DEFAULT, nepAlias1, nepAlias2);
    }

    @Given("a/another NEL {string} connected to NEPs {string} and {string}( is existing)( in A4 resource inventory)")
    public void givenANELIsExistingInA4ResourceInventory(String nelAlias, String nepAlias1, String nepAlias2) {
        createNel(nelAlias, nepAlias1, nepAlias2);
    }

    @Given("a NEL with operational state {string} and lifecycle state {string}( is existing)( in A4 resource inventory)")
    public void givenANELWithOperationalStateAndLifecycleStateIsExistingInA4ResourceInventory(String ops, String lcs) {
        createNelWithStates(DEFAULT, ops, lcs, DEFAULT, DEFAULT_B);
    }

    @Given("a/another NEL with operational state {string} and lifecycle state {string} connected to NEPs {string} and {string}( is existing)( in A4 resource inventory)")
    public void givenANELWithOperationalStateAndLifecycleStateIsExistingInA4ResourceInventory(String ops, String lcs, String nepAlias1, String nepAlias2) {
        createNelWithStates(DEFAULT, ops, lcs, nepAlias1, nepAlias2);
    }

    @Given("a/another NEL {string} with operational state {string} and lifecycle state {string} connected to NEPs {string} and {string}( is existing)( in A4 resource inventory)")
    public void givenANELWithOperationalStateAndLifecycleStateIsExistingInA4ResourceInventory(String nelAlias, String ops, String lcs, String nepAlias1, String nepAlias2) {
        createNelWithStates(nelAlias, ops, lcs, nepAlias1, nepAlias2);
    }

    @Given("a NEL with ueweg id {string}( is existing)( in A4 resource inventory)")
    public void givenANELWithUewegIdIsExistingInA4ResourceInventory(String uewegId) {
        createNel(DEFAULT, uewegId, DEFAULT, DEFAULT);
    }

    @Given("a/another NEL with ueweg id {string} connected to NEPs {string}and {string}( is existing)( in A4 resource inventory)")
    public void givenANELWithUewegIdIsExistingInA4ResourceInventory(String uewegId, String nepAlias1, String nepAlias2) {
        createNel(DEFAULT, uewegId, nepAlias1, nepAlias2);
    }

    @Given("a/another NEL {string} with ueweg id {string} connected to NEPs {string}and {string}( is existing)( in A4 resource inventory)")
    public void givenANELWithUewegIdIsExistingInA4ResourceInventory(String nelAlias, String uewegId, String nepAlias1, String nepAlias2) {
        createNel(nelAlias, uewegId, nepAlias1, nepAlias2);
    }


    // -----=====[ THENS ]=====-----

    @Then("the (new )NEL operationalState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNelOperationalStateIsUpdatedInA4ResInv(String operationalState) {
        final NetworkElementLinkDto nelData = (NetworkElementLinkDto) testContext.getScenarioContext().getContext(Context.A4_NEL);
        final NetworkElementLinkDto nel = a4ResInv.getExistingNetworkElementLink(nelData.getUuid());

        assertEquals(operationalState, nel.getOperationalState());
    }

    @Then("the (new )NEL lifecycleState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNelLifecycleStateIsUpdatedInA4ResInv(String lifecycleState) {
        final NetworkElementLinkDto nelData = (NetworkElementLinkDto) testContext.getScenarioContext().getContext(Context.A4_NEL);
        final NetworkElementLinkDto nel = a4ResInv.getExistingNetworkElementLink(nelData.getUuid());

        assertEquals(lifecycleState, nel.getLifecycleState());
    }

    @Then("the NEL lastUpdateTime is updated")
    public void thenTheNelLastUpdateTimeIsUpdated() {
        final NetworkElementLinkDto nelData = (NetworkElementLinkDto) testContext.getScenarioContext().getContext(Context.A4_NEL);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);
        final NetworkElementLinkDto nel = a4ResInv.getExistingNetworkElementLink(nelData.getUuid());

        assertNotNull(nel.getLastUpdateTime());
        assertTrue(nel.getLastUpdateTime().isAfter(oldDateTime), "lastUpdateTime (" + nel.getLastUpdateTime() + ") is older than " + oldDateTime + "!");
    }


    // -----=====[ HELPERS ]=====-----

    private NetworkElementLinkDto setupDefaultNelTestData(String nepAlias1, String nepAlias2) {
        final boolean NEP_A_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NEP, nepAlias1);
        final boolean NEP_B_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NEP, nepAlias2);

        // NEL needs to be connected to 2 NEPs, so if no NEPs present, create them
        if (!NEP_A_PRESENT)
            a4NepSteps.givenANEPIsExistingInA4ResourceInventory(nepAlias1, nepAlias1);
        if (!NEP_B_PRESENT)
            a4NepSteps.givenANEPIsExistingInA4ResourceInventory(nepAlias2, nepAlias2);

        final NetworkElementPortDto nep1 = (NetworkElementPortDto) testContext.getScenarioContext().getContext(Context.A4_NEP, nepAlias1);
        final NetworkElementPortDto nep2 = (NetworkElementPortDto) testContext.getScenarioContext().getContext(Context.A4_NEP, nepAlias2);
        final NetworkElementDto ne1 = a4ResInv.getExistingNetworkElement(nep1.getNetworkElementUuid());
        final NetworkElementDto ne2 = a4ResInv.getExistingNetworkElement(nep2.getNetworkElementUuid());

        return a4ResInvMapper.getNetworkElementLinkDto(nep1.getUuid(), nep2.getUuid(), ne1.getVpsz(), ne1.getFsz(), ne2.getVpsz(), ne2.getFsz());
    }

    private void persistNel(String nelAlias, NetworkElementLinkDto nel) {
        a4ResInv.createNetworkElementLink(nel);
        testContext.getScenarioContext().setContext(Context.A4_NEL, nelAlias, nel);
    }

    private void createNel(String nelAlias, String nepAlias1, String nepAlias2) {
        NetworkElementLinkDto nel = setupDefaultNelTestData(nepAlias1, nepAlias2);

        persistNel(nelAlias, nel);
    }

    private void createNelWithStates(String nelAlias, String opState, String lcState, String nepAlias1, String nepAlias2) {
        NetworkElementLinkDto nel = setupDefaultNelTestData(nepAlias1, nepAlias2);
        nel.setOperationalState(opState);
        nel.setLifecycleState(lcState);

        persistNel(nelAlias, nel);
    }

    private void createNel(String nelAlias, String uewegId, String nepAlias1, String nepAlias2) {
        NetworkElementLinkDto nel = setupDefaultNelTestData(nepAlias1, nepAlias2);
        nel.setUeWegId(uewegId);

        persistNel(nelAlias, nel);
    }

}
