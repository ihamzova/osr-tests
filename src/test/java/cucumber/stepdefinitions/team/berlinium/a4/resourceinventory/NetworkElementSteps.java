package cucumber.stepdefinitions.team.berlinium.a4.resourceinventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryMapper;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementGroupDto;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.DEFAULT;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.FileAssert.fail;

public class NetworkElementSteps {

    private final A4ResourceInventoryRobot a4ResInv;
    private final A4ResourceInventoryMapper a4ResInvMapper;
    private final NetworkElementGroupSteps a4NegSteps;
    private final TestContext testContext;

    public NetworkElementSteps(TestContext testContext,
                               A4ResourceInventoryRobot a4ResInv,
                               A4ResourceInventoryMapper a4ResInvMapper,
                               NetworkElementGroupSteps a4NegSteps) {
        this.testContext = testContext;
        this.a4ResInv = a4ResInv;
        this.a4ResInvMapper = a4ResInvMapper;
        this.a4NegSteps = a4NegSteps;
    }


    // -----=====[ GIVENs ]=====-----

    @Given("a NE( is existing)( in A4 resource inventory)")
    public void givenA4Ne() {
        createNe(DEFAULT, DEFAULT);
    }

    @Given("a/another NE {string}( connected to the NEG)( is existing)( in A4 resource inventory)")
    public void givenA4NeWithAlias(String neAlias) {
        createNe(neAlias, DEFAULT);
    }

    @Given("a/another NE {string} connected to the NEG {string}( is existing)( in A4 resource inventory)")
    public void givenA4NeWithAliasConnectedToNeg(String neAlias, String negAlias) {
        createNe(neAlias, negAlias);
    }

    @Given("a NE with operational state {string} and lifecycle state {string}( connected to the NEG)( is existing)( in A4 resource inventory)")
    public void givenA4NeWithStates(String ops, String lcs) {
        createNeWithStates(DEFAULT, ops, lcs, DEFAULT);
    }

    @Given("a/another NE {string} with operational state {string} and lifecycle state {string}( connected to the NEG)( is existing)( in A4 resource inventory)")
    public void givenA4NeWithAliasWithStates(String neAlias, String ops, String lcs) {
        createNeWithStates(neAlias, ops, lcs, DEFAULT);
    }

    @Given("a/another NE {string} with operational state {string} and lifecycle state {string} connected to the NEG {string}( is existing)( in A4 resource inventory)")
    public void givenA4NeWithAliasWithStatesConnectedToNeg(String neAlias, String ops, String lcs, String negAlias) {
        createNeWithStates(neAlias, ops, lcs, negAlias);
    }

    /**
     * Creates a NE in a4 resource inventory, each property filled with default test data.
     * If any NE with colliding unique constraint ('ztpIdent' and 'EndSz') already exists, then the old NE is deleted first.
     *
     * @param properties Contains explicit properties and values with which the default test data is overwritten
     */
    @Given("a NE with the following properties( connected to the NEG)( is existing)( in A4 resource inventory):")
    public void givenA4NeWithProperties(DataTable properties) {
        createNe(DEFAULT, properties, DEFAULT);
    }

    @Given("a/another NE {string} with the following properties( connected to the NEG)( is existing)( in A4 resource inventory):")
    public void givenA4NeWithAliasWithProperties(String neAlias, DataTable properties) {
        createNe(neAlias, properties, DEFAULT);
    }

    @Given("a/another NE {string} with the following properties connected to the NEG {string}( is existing)( in A4 resource inventory):")
    public void givenA4NeWithAliasWithPropertiesConnectedToNeg(String neAlias, DataTable properties, String negAlias) {
        createNe(neAlias, properties, negAlias);
    }

    @Given("a NE with VPSZ {string} and FSZ {string}( connected to the NEG)( is existing)( in A4 resource inventory)")
    public void givenA4NeWithVpszFsz(String vpsz, String fsz) {
        createNeWithEndsz(DEFAULT, vpsz, fsz, DEFAULT);
    }

    @Given("a/another NE {string} with VPSZ {string} and FSZ {string}( connected to the NEG)( is existing)( in A4 resource inventory)")
    public void givenA4NeWithAliasWithVpszAndFsz(String neAlias, String vpsz, String fsz) {
        createNeWithEndsz(neAlias, vpsz, fsz, DEFAULT);
    }

    @Given("a/another NE {string} with VPSZ {string} and FSZ {string} connected to the NEG {string} ( is existing)( in A4 resource inventory)")
    public void givenA4NeWithAliasWithVpszFszConnectedToNeg(String neAlias, String vpsz, String fsz, String negAlias) {
        createNeWithEndsz(neAlias, vpsz, fsz, negAlias);
    }

    @Given("a NE with type {string} and category {string}( connected to the NEG)( is existing)( in A4 resource inventory)")
    public void givenA4NeWithTypeCategory(String type, String category) {
        createNeWithTypes(DEFAULT, type, category, DEFAULT);
    }

    @Given("a/another NE {string} with type {string} and category {string}( connected to the NEG)( is existing)( in A4 resource inventory)")
    public void givenA4NeWithAliasWithTypeCategory(String neAlias, String type, String category) {
        createNeWithTypes(neAlias, type, category, DEFAULT);
    }

    @Given("a/another NE {string} with type {string} and category {string} connected to NEG {string}( is existing)( in A4 resource inventory)")
    public void givenA4NeWithAliasWithTypeCategoryConnectedToNeg(String neAlias, String type, String category, String negAlias) {
        createNeWithTypes(neAlias, type, category, negAlias);
    }

    @Given("no NE exists( in A4 resource inventory)")
    public void givenA4NeNotExist() {
        NetworkElementDto ne = new NetworkElementDto();
        ne.setUuid(UUID.randomUUID().toString());

        a4ResInv.deleteA4NetworkElementsRecursivelyByUuid(ne.getUuid());

        testContext.getScenarioContext().setContext(Context.A4_NE, ne);
    }

    @Given("no NE with VPSZ {string} and FSZ {string} exists( in A4 resource inventory)")
    public void givenA4NWithVpszFszNotExist(String vpsz, String fsz) {
        NetworkElementDto ne = new NetworkElementDto();
        ne.setUuid(UUID.randomUUID().toString());
        ne.setVpsz(vpsz);
        ne.setFsz(fsz);

        a4ResInv.deleteA4NetworkElementsRecursively(vpsz, fsz);

        testContext.getScenarioContext().setContext(Context.A4_NE, ne);
    }


    // -----=====[ THENS ]=====-----

    @Then("the NE now has the following properties:")
    public void thenTheNENowHasTheFollowingProperties(DataTable table) {
        final NetworkElementDto ne = (NetworkElementDto) testContext.getScenarioContext().getContext(Context.A4_NE);
        final Map<String, String> neMap = table.asMap();
        final NetworkElementDto neDtoActual = a4ResInv.getExistingNetworkElement(ne.getUuid());
        final ObjectMapper om = testContext.getObjectMapper();

        // https://stackoverflow.com/questions/34957051/how-to-get-rid-of-type-safety-unchecked-cast-from-object-to-mapstring-string
        @SuppressWarnings("unchecked")
        Map<String, Object> neMapActual = om.convertValue(neDtoActual, Map.class);

        neMap.keySet().forEach(k -> {
                    if (neMap.get(k) != null && neMapActual.get(k) == null)
                        fail("Expected property '" + k + "' is not present in updated NE!");
                    assertEquals("Property '" + k + "' differs!", neMap.get(k), neMapActual.get(k).toString());
                }
        );
    }

    @Then("a/one/1 NE with VPSZ {string} and FSZ {string} does exist( in A4 resource inventory)")
    public void aNEWithVPSZAndFSZDoesExistInAResourceInventory(String vpsz, String fsz) {
        final List<NetworkElementDto> neList = a4ResInv.getNetworkElementsByVpszFsz(vpsz, fsz);

        assertEquals(1, neList.size());
    }

    @Then("the (new )NE operationalState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNeOperationalStateIsUpdatedInA4ResInv(String operationalState) {
        final NetworkElementDto neData = (NetworkElementDto) testContext.getScenarioContext().getContext(Context.A4_NE);
        final NetworkElementDto ne = a4ResInv.getExistingNetworkElement(neData.getUuid());

        assertEquals(operationalState, ne.getOperationalState());
    }

    @Then("the (new )NE lifecycleState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNeLifecycleStateIsUpdatedInA4ResInv(String lifecycleState) {
        final NetworkElementDto neData = (NetworkElementDto) testContext.getScenarioContext().getContext(Context.A4_NE);
        final NetworkElementDto ne = a4ResInv.getExistingNetworkElement(neData.getUuid());

        assertEquals(lifecycleState, ne.getLifecycleState());
    }

    @Then("the NE lastUpdateTime is updated")
    public void thenTheNeLastUpdateTimeIsUpdated() {
        final NetworkElementDto neData = (NetworkElementDto) testContext.getScenarioContext().getContext(Context.A4_NE);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);
        final NetworkElementDto ne = a4ResInv.getExistingNetworkElement(neData.getUuid());

        assertNotNull(ne.getLastUpdateTime());
        assertTrue(ne.getLastUpdateTime().isAfter(oldDateTime), "lastUpdateTime (" + ne.getLastUpdateTime() + ") is older than " + oldDateTime + "!");
    }

    @Then("the NE lastSuccessfulSyncTime property was updated")
    public void thenTheNELastSuccessfulSyncTimePropertyWasUpdated() {
        final NetworkElementDto ne = (NetworkElementDto) testContext.getScenarioContext().getContext(Context.A4_NE);
        final OffsetDateTime timeStamp = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);

        sleepForSeconds(2);
        a4ResInv.checkNetworkElementIsUpdatedWithLastSuccessfulSyncTime(ne.getUuid(), timeStamp);
    }

    @Then("the NE creationTime is not updated")
    public void thenTheNECreationTimeIsNotUpdated() {
        final NetworkElementDto neData = (NetworkElementDto) testContext.getScenarioContext().getContext(Context.A4_NE);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);
        final NetworkElementDto ne = a4ResInv.getExistingNetworkElement(neData.getUuid());

        assertNotNull(ne.getCreationTime());
        assertTrue(ne.getCreationTime().isBefore(oldDateTime), "creationTime (" + ne.getCreationTime() + ") is newer than " + oldDateTime + "!");
    }


    // -----=====[ HELPERS ]=====-----

    private NetworkElementDto setupDefaultNeTestData(String negAlias) {
        final boolean NEG_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NEG, negAlias);

        // NE needs to be connected to a NEG, so if no NEG present, create one
        if (!NEG_PRESENT)
            a4NegSteps.givenA4NegWithAlias(negAlias);

        final NetworkElementGroupDto neg = (NetworkElementGroupDto) testContext.getScenarioContext().getContext(Context.A4_NEG, negAlias);

        return a4ResInvMapper.getNetworkElementDto(neg.getUuid());
    }

    private void persistNe(String neAlias, NetworkElementDto ne) {
        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteA4NetworkElementsRecursivelyDto(ne);

        a4ResInv.createNetworkElement(ne);
        testContext.getScenarioContext().setContext(Context.A4_NE, neAlias, ne);
    }

    private void createNe(String neAlias, String negAlias) {
        NetworkElementDto ne = setupDefaultNeTestData(negAlias);

        persistNe(neAlias, ne);
    }

    private void createNeWithStates(String neAlias, String opState, String lcState, String negAlias) {
        NetworkElementDto ne = setupDefaultNeTestData(negAlias);
        ne.setOperationalState(opState);
        ne.setLifecycleState(lcState);

        persistNe(neAlias, ne);
    }

    private void createNeWithEndsz(String neAlias, String vpsz, String fsz, String negAlias) {
        NetworkElementDto ne = setupDefaultNeTestData(negAlias);
        ne.setVpsz(vpsz);
        ne.setFsz(fsz);

        persistNe(neAlias, ne);
    }

    private void createNeWithTypes(String neAlias, String type, String category, String negAlias) {
        NetworkElementDto ne = setupDefaultNeTestData(negAlias);
        ne.setType(type);
        ne.setCategory(category);

        persistNe(neAlias, ne);
    }

    private void createNe(String neAlias, DataTable table, String negAlias) {
        final Map<String, String> neMap = table.asMap();
        final ObjectMapper om = testContext.getObjectMapper();
        final TokenBuffer buffer = new TokenBuffer(om, false);

        // First create a new NE default test data set...
        final NetworkElementDto neDefault = setupDefaultNeTestData(negAlias);

        try {
            // ... then overwrite default data set with data provided in given-step data table
            om.writeValue(buffer, neMap);

            NetworkElementDto ne = om.readerForUpdating(neDefault).readValue(buffer.asParser());
            persistNe(neAlias, ne);

        } catch (IOException e) {
            fail("Unexpected mapping error: " + e.getMessage());
        }
    }

}
