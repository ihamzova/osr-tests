package cucumber.stepdefinitions.team.berlinium.a4.resourceinventory;

import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryMapper;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementPortDto;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.time.OffsetDateTime;
import java.util.List;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.DEFAULT;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getPortNumberByFunctionalPortLabel;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

public class NetworkElementPortSteps {

    private final A4ResourceInventoryRobot a4ResInv;
    private final A4ResourceInventoryMapper a4ResInvMapper;
    private final NetworkElementSteps a4NeSteps;
    private final TestContext testContext;

    public NetworkElementPortSteps(TestContext testContext,
                                   A4ResourceInventoryRobot a4ResInv,
                                   A4ResourceInventoryMapper a4ResInvMapper,
                                   NetworkElementSteps a4NeSteps) {
        this.testContext = testContext;
        this.a4ResInv = a4ResInv;
        this.a4ResInvMapper = a4ResInvMapper;
        this.a4NeSteps = a4NeSteps;
    }


    // -----=====[ GIVENs ]=====-----

    @Given("a NEP( connected to the NE)( is existing)( in A4 resource inventory)")
    public void givenANEPIsExistingInA4ResourceInventory() {
        createNep(DEFAULT, DEFAULT);
    }

    @Given("a/another NEP {string}( connected to the NE)( is existing)( in A4 resource inventory)")
    public void givenANEPIsExistingInA4ResourceInventory(String nepAlias) {
        createNep(nepAlias, DEFAULT);
    }

    @Given("a/another NEP {string} connected to NE {string}( is existing)( in A4 resource inventory)")
    public void givenANEPIsExistingInA4ResourceInventory(String nepAlias, String neAlias) {
        createNep(nepAlias, neAlias);
    }

    @Given("a NEP with operational state {string} and description {string}( connected to the NE)( is existing)( in A4 resource inventory)")
    public void givenANEPWithOperationalStateAndDescriptionIsExistingInAResourceInventory(String opState, String descr) {
        createNepWithStates(DEFAULT, opState, descr, DEFAULT);
    }

    @Given("a/another NEP {string} with operational state {string} and description {string}( connected to the NE)( is existing)( in A4 resource inventory)")
    public void givenANEPWithOperationalStateAndDescriptionIsExistingInAResourceInventory(String nepAlias, String opState, String descr) {
        createNepWithStates(nepAlias, opState, descr, DEFAULT);
    }

    @Given("a/another NEP {string} with operational state {string} and description {string} connected to NE {string}( is existing)( in A4 resource inventory)")
    public void givenANEPWithOperationalStateAndDescriptionIsExistingInAResourceInventory(String nepAlias, String opState, String descr, String neAlias) {
        createNepWithStates(nepAlias, opState, descr, neAlias);
    }

    @Given("a NEP with type {string} and functional label {string}( connected to the NE)( is existing)( in A4 resource inventory)")
    public void givenANEPWithTypeAndFunctionalLabelIsExistingInA4ResourceInventory(String type, String functionalLabel) {
        createNepWithTypes(DEFAULT, type, functionalLabel, DEFAULT);
    }

    @Given("a/another NEP {string} with type {string} and functional label {string}( connected to the NE)( is existing)( in A4 resource inventory)")
    public void givenANEPWithTypeAndFunctionalLabelIsExistingInA4ResourceInventory(String nepAlias, String type, String functionalLabel) {
        createNepWithTypes(nepAlias, type, functionalLabel, DEFAULT);
    }

    @Given("a/another NEP {string} with type {string} and functional label {string} connected to NE {string}( is existing)( in A4 resource inventory)")
    public void givenANEPWithTypeAndFunctionalLabelIsExistingInA4ResourceInventory(String nepAlias, String type, String functionalLabel, String neAlias) {
        createNepWithTypes(nepAlias, type, functionalLabel, neAlias);
    }


    // -----=====[ THENS ]=====-----

    @Then("{int} NEP(s) connected to the NE with VPSZ {string} and FSZ {string} do/does exist( in A4 resource inventory)")
    public void thenXNepsConnectedToTheNEWithVPSZAndFSZDoExistInAResourceInventory(int count, String vpsz, String fsz) {
        final List<NetworkElementDto> neList = a4ResInv.getNetworkElementsByVpszFsz(vpsz, fsz);
        final List<NetworkElementPortDto> nepList = a4ResInv.getNetworkElementPortsByNetworkElement(neList.get(0).getUuid());

        assertEquals(1, neList.size());
        assertEquals(count, nepList.size());
    }

    @Then("the (new )NEP operationalState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNepOperationalStateIsUpdatedInA4ResInv(String operationalState) {
        final NetworkElementPortDto nepData = (NetworkElementPortDto) testContext.getScenarioContext().getContext(Context.A4_NEP);
        final NetworkElementPortDto nep = a4ResInv.getExistingNetworkElementPort(nepData.getUuid());

        assertEquals(operationalState, nep.getOperationalState());
    }

    @Then("the (new )NEP operationalState is (now )deleted( in the A4 resource inventory)")
    public void thenTheNepOperationalStateIsDeletedInA4ResInv() {
        final NetworkElementPortDto nepData = (NetworkElementPortDto) testContext.getScenarioContext().getContext(Context.A4_NEP);
        final NetworkElementPortDto nep = a4ResInv.getExistingNetworkElementPort(nepData.getUuid());

        assertNull(nep.getOperationalState());
    }

    @Then("the (new )NEP description is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNEPDescriptionIsUpdatedTo(String newDescr) {
        final NetworkElementPortDto nepData = (NetworkElementPortDto) testContext.getScenarioContext().getContext(Context.A4_NEP);
        final NetworkElementPortDto nep = a4ResInv.getExistingNetworkElementPort(nepData.getUuid());

        assertEquals(newDescr, nep.getDescription());
    }

    @Then("the NEP lastUpdateTime is updated")
    public void thenTheNEPLastUpdateTimeIsUpdated() {
        final NetworkElementPortDto nepData = (NetworkElementPortDto) testContext.getScenarioContext().getContext(Context.A4_NEP);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);
        final NetworkElementPortDto nep = a4ResInv.getExistingNetworkElementPort(nepData.getUuid());

        assertNotNull(nep.getLastUpdateTime());
        assertTrue(nep.getLastUpdateTime().isAfter(oldDateTime), "lastUpdateTime (" + nep.getLastUpdateTime() + ") is older than " + oldDateTime + "!");
    }

    @Then("the NEP lastUpdateTime is not updated")
    public void thenTheNEPLastUpdateTimeIsNotUpdated() {
        final NetworkElementPortDto nepData = (NetworkElementPortDto) testContext.getScenarioContext().getContext(Context.A4_NEP);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);
        final NetworkElementPortDto nep = a4ResInv.getExistingNetworkElementPort(nepData.getUuid());

        assertNotNull(nep.getLastUpdateTime());
        assertTrue(nep.getLastUpdateTime().isBefore(oldDateTime), "lastUpdateTime (" + nep.getLastUpdateTime() + ") is newer than " + oldDateTime + "!");
    }


    // -----=====[ HELPERS ]=====-----

    private NetworkElementPortDto setupDefaultNepTestData(String neAlias) {
        final boolean NE_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NE, neAlias);

        // NEP needs to be connected to a NE, so if no NE present, create one
        if (!NE_PRESENT)
            a4NeSteps.givenA4NeWithAlias(neAlias);

        final NetworkElementDto ne = (NetworkElementDto) testContext.getScenarioContext().getContext(Context.A4_NE, neAlias);

        return a4ResInvMapper.getNetworkElementPortDto(ne.getUuid(), ne.getVpsz(), ne.getFsz());
    }

    private void persistNep(String nepAlias, NetworkElementPortDto nep, String neAlias) {
        final NetworkElementDto ne = (NetworkElementDto) testContext.getScenarioContext().getContext(Context.A4_NE, neAlias);

        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteA4NetworkElementPortsRecursively(nep.getLogicalLabel(), ne.getVpsz(), ne.getFsz());

        a4ResInv.createNetworkElementPort(nep);
        testContext.getScenarioContext().setContext(Context.A4_NEP, nepAlias, nep);
    }

    private void createNep(String nepAlias, String neAlias) {
        NetworkElementPortDto nep = setupDefaultNepTestData(neAlias);

        persistNep(nepAlias, nep, neAlias);
    }

    private void createNepWithStates(String nepAlias, String opState, String description, String neAlias) {
        NetworkElementPortDto nep = setupDefaultNepTestData(neAlias);
        nep.setOperationalState(opState);
        nep.setDescription(description);

        persistNep(nepAlias, nep, neAlias);
    }

    private void createNepWithTypes(String nepAlias, String type, String functionalLabel, String neAlias) {
        NetworkElementPortDto nep = setupDefaultNepTestData(neAlias);
        nep.setType(type);
        nep.setLogicalLabel(functionalLabel);
        nep.setPortNumber(getPortNumberByFunctionalPortLabel(functionalLabel));

        persistNep(nepAlias, nep, neAlias);
    }

}
