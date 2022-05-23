package cucumber.stepdefinitions.team.berlinium.a4.resourceinventory;

import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryMapper;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementPortDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.TerminationPointDto;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.util.UUID;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.DEFAULT;

public class TerminationPointSteps {

    private final A4ResourceInventoryRobot a4ResInv;
    private final A4ResourceInventoryMapper a4ResInvMapper;
    private final NetworkElementPortSteps a4NepSteps;
    private final TestContext testContext;

    public TerminationPointSteps(TestContext testContext,
                                 A4ResourceInventoryRobot a4ResInv,
                                 A4ResourceInventoryMapper a4ResInvMapper,
                                 NetworkElementPortSteps a4NepSteps) {
        this.testContext = testContext;
        this.a4ResInv = a4ResInv;
        this.a4ResInvMapper = a4ResInvMapper;
        this.a4NepSteps = a4NepSteps;
    }


    // -----=====[ GIVENS ]=====-----

    @Given("a TP( connected to the NEP)( is existing)( in A4 resource inventory)")
    public void givenATPIsExistingInA4ResourceInventory() {
        createTpForNep(DEFAULT, DEFAULT);
    }

    @Given("a/another TP connected to NEP {string}( is existing)( in A4 resource inventory)")
    public void givenATPIsExistingInA4ResourceInventory(String nepAlias) {
        createTpForNep(DEFAULT, nepAlias);
    }

    @Given("a/another TP {string} connected to NEP {string}( is existing)( in A4 resource inventory)")
    public void givenATPIsExistingInA4ResourceInventory(String tpAlias, String nepAlias) {
        createTpForNep(tpAlias, nepAlias);
    }

    @Given("a TP with type {string}( connected to the NEP)( is existing)( in A4 resource inventory)")
    public void givenTPWithTypeIsExistingInA4ResourceInventory(String tpType) {
        createTpForNep(DEFAULT, tpType, DEFAULT);
    }

    @Given("a/another TP with type {string} connected to NEP {string}( is existing)( in A4 resource inventory)")
    public void givenTPWithTypeIsExistingInA4ResourceInventory(String tpType, String nepAlias) {
        createTpForNep(DEFAULT, tpType, nepAlias);
    }

    @Given("a/another TP {string} with type {string} connected to NEP {string}( is existing)( in A4 resource inventory)")
    public void givenTPWithTypeIsExistingInA4ResourceInventory(String tpAlias, String tpType, String nepAlias) {
        createTpForNep(tpAlias, tpType, nepAlias);
    }

    @Given("no TP exists( in A4 resource inventory)")
    public void givenNoTPExistsInA4ResourceInventory() {
        TerminationPointDto tp = new TerminationPointDto();
        tp.setUuid(UUID.randomUUID().toString());

        testContext.getScenarioContext().setContext(Context.A4_TP, tp);
    }


    // -----=====[ THENS ]=====-----

    @Then("the TP (does )(still )exist(s)( in A4 resource inventory)")
    public void thenA4TpExist() {
        final TerminationPointDto tp = (TerminationPointDto) testContext.getScenarioContext().getContext(Context.A4_TP);

        a4ResInv.checkTerminationPointExists(tp.getUuid());
    }

    @Then("the TP does not exist in A4 resource inventory( anymore)( any longer)")
    public void thenTheTPIsDoesNotExistInA4ResourceInventoryAnymore() {
        final TerminationPointDto tp = (TerminationPointDto) testContext.getScenarioContext().getContext(Context.A4_TP);

        a4ResInv.checkTerminationPointIsDeleted(tp.getUuid());
    }


    // -----=====[ HELPERS ]=====-----

    private TerminationPointDto setupDefaultTpTestData(String nepAlias) {
        final boolean NEP_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NEP, nepAlias);

        // TP needs to be connected to a NEP, so if no NEP present, create one
        if (!NEP_PRESENT)
            a4NepSteps.givenANEPIsExistingInA4ResourceInventory(nepAlias);

        final NetworkElementPortDto nep = (NetworkElementPortDto) testContext.getScenarioContext().getContext(Context.A4_NEP, nepAlias);

        return a4ResInvMapper.getTerminationPointDto(nep.getUuid());
    }

    private void persistTp(String tpAlias, TerminationPointDto tp) {
        a4ResInv.createTerminationPoint(tp);
        testContext.getScenarioContext().setContext(Context.A4_TP, tpAlias, tp);
    }

    private void createTpForNep(String tpAlias, String nepAlias) {
        TerminationPointDto tp = setupDefaultTpTestData(nepAlias);

        persistTp(tpAlias, tp);
    }

    private void createTpForNep(String tpAlias, String type, String nepAlias) {
        TerminationPointDto tp = setupDefaultTpTestData(nepAlias);
        tp.setType(type);

        persistTp(tpAlias, tp);
    }

}
