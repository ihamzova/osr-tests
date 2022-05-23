package cucumber.stepdefinitions.team.berlinium.a4.resourceinventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryMapper;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementGroupDto;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.DEFAULT;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static java.util.stream.Collectors.toList;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.FileAssert.fail;

public class NetworkElementGroupSteps {

    private final A4ResourceInventoryRobot a4ResInv;
    private final A4ResourceInventoryMapper a4ResInvMapper;
    private final TestContext testContext;

    public NetworkElementGroupSteps(TestContext testContext,
                                    A4ResourceInventoryRobot a4ResInv,
                                    A4ResourceInventoryMapper a4ResInvMapper) {
        this.testContext = testContext;
        this.a4ResInv = a4ResInv;
        this.a4ResInvMapper = a4ResInvMapper;
    }

    @After
    public void cleanup() {
        final boolean NEG_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NEG);
        if (NEG_PRESENT) {
            final List<NetworkElementGroupDto> negList = testContext.getScenarioContext().getAllContext(Context.A4_NEG).stream()
                    .map(neg -> (NetworkElementGroupDto) neg)
                    .collect(toList());

            negList.forEach(a4ResInv::deleteA4NetworkElementGroupsRecursively);
        }

        final boolean CSV_PRESENT = testContext.getScenarioContext().isContains(Context.A4_CSV);
        if (CSV_PRESENT) {
            final A4ImportCsvData csv = (A4ImportCsvData) testContext.getScenarioContext().getContext(Context.A4_CSV);
            a4ResInv.deleteA4TestDataRecursively(csv);
        }
    }


    // -----=====[ GIVENs ]=====-----

    @Given("a NEG( is existing)( in A4 resource inventory)")
    public void givenA4Neg() {
        createNeg(DEFAULT);
    }

    @Given("a/another NEG {string}( is existing)( in A4 resource inventory)")
    public void givenA4NegWithAlias(String alias) {
        createNeg(alias);
    }

    @Given("a NEG with name {string}( is existing)( in A4 resource inventory)")
    public void givenA4NegWithName(String name) {
        createNegWithName(DEFAULT, name);
    }

    @Given("a/another NEG {string} with name {string}( is existing)( in A4 resource inventory)")
    public void givenA4NegWithAliasWithName(String alias, String name) {
        createNegWithName(alias, name);
    }

    @Given("a NEG with type {string}( is existing)( in A4 resource inventory)")
    public void givenA4NegWithType(String type) {
        createNegWithType(DEFAULT, type);
    }

    @Given("a/another NEG {string} with type {string}( is existing)( in A4 resource inventory)")
    public void givenA4NegWithAliasWithType(String alias, String type) {
        createNegWithType(alias, type);
    }

    @Given("a NEG with operational state {string} and lifecycle state {string}( is existing)( in A4 resource inventory)")
    public void givenA4NegWithStates(String opState, String lcState) {
        createNeg(DEFAULT, opState, lcState);
    }

    @Given("a/another NEG {string} with operational state {string} and lifecycle state {string}( is existing)( in A4 resource inventory)")
    public void givenA4NegWithAliasWithStates(String alias, String ops, String lcs) {
        createNeg(alias, ops, lcs);
    }

    /**
     * Creates a NEG in a4 resource inventory, each property filled with default test data.
     * If any NEG with colliding unique constraint (property 'name') already exists, then the old NEG is deleted first.
     *
     * @param properties Contains explicit properties and values with which the default test data is overwritten
     */
    @Given("a NEG with the following properties( is existing)( in A4 resource inventory):")
    public void givenA4NegWithProperties(DataTable properties) {
        createNeg(DEFAULT, properties);
    }

    @Given("a/another NEG {string} with the following properties( is existing)( in A4 resource inventory):")
    public void givenA4NegWithAliasWithProperties(String alias, DataTable table) {
        createNeg(alias, table);
    }

    @Given("no NEG exists( in A4 resource inventory)")
    public void givenA4NegNotExist() {
        NetworkElementGroupDto neg = new NetworkElementGroupDto();
        neg.setUuid(UUID.randomUUID().toString());

        // Make sure that NEG really doesn't exist
        a4ResInv.deleteA4NetworkElementGroupsRecursively(neg);

        testContext.getScenarioContext().setContext(Context.A4_NEG, neg);
    }

    @Given("no NEG with name {string} exists( in A4 resource inventory)")
    public void givenA4NegWithNameNotExist(String name) {
        NetworkElementGroupDto neg = new NetworkElementGroupDto();
        neg.setName(name);

        // Make sure that NEG really doesn't exist
        a4ResInv.deleteA4NetworkElementGroupsRecursively(neg);

        testContext.getScenarioContext().setContext(Context.A4_NEG, neg);
    }


    // -----=====[ THENs ]=====-----

    @Then("a/one/1 NEG with name {string} does exist( in A4 resource inventory)")
    public void thenANegWithNameDoesExistInAResourceInventory(String negName) {
        final List<NetworkElementGroupDto> negList = a4ResInv.getNetworkElementGroupsByName(negName);

        assertEquals(1, negList.size());
    }

    @Then("the (new )NEG operationalState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNegOperationalStateIsUpdatedInA4ResInv(String operationalState) {
        final NetworkElementGroupDto negData = (NetworkElementGroupDto) testContext.getScenarioContext().getContext(Context.A4_NEG);
        final NetworkElementGroupDto neg = a4ResInv.getExistingNetworkElementGroup(negData.getUuid());

        assertEquals(operationalState, neg.getOperationalState());
    }

    @Then("the (new )NEG lifecycleState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNegLifecycleStateIsUpdatedInA4ResInv(String lifecycleState) {
        final NetworkElementGroupDto negData = (NetworkElementGroupDto) testContext.getScenarioContext().getContext(Context.A4_NEG);
        final NetworkElementGroupDto neg = a4ResInv.getExistingNetworkElementGroup(negData.getUuid());

        assertEquals(lifecycleState, neg.getLifecycleState());
    }

    @Then("the NEG creationTime is not updated")
    public void thenTheNEGCreationTimeIsNotUpdated() {
        final NetworkElementGroupDto negData = (NetworkElementGroupDto) testContext.getScenarioContext().getContext(Context.A4_NEG);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);
        final NetworkElementGroupDto neg = a4ResInv.getExistingNetworkElementGroup(negData.getUuid());
        assertNotNull(neg.getCreationTime());
        assertTrue(neg.getCreationTime().isBefore(oldDateTime), "creationTime (" + neg.getCreationTime() + ") is newer than " + oldDateTime + "!");
    }

    @Then("the NEG lastUpdateTime is updated")
    public void thenTheNEGLastUpdateTimeIsUpdated() {
        final NetworkElementGroupDto negData = (NetworkElementGroupDto) testContext.getScenarioContext().getContext(Context.A4_NEG);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);
        final NetworkElementGroupDto neg = a4ResInv.getExistingNetworkElementGroup(negData.getUuid());

        assertNotNull(neg.getLastUpdateTime());
        assertTrue(neg.getLastUpdateTime().isAfter(oldDateTime), "lastUpdateTime (" + neg.getLastUpdateTime() + ") is older than " + oldDateTime + "!");
    }

    @Then("the NEG lastUpdateTime is not updated")
    public void thenTheNEGLastUpdateTimeIsNotUpdated() {
        final NetworkElementGroupDto negData = (NetworkElementGroupDto) testContext.getScenarioContext().getContext(Context.A4_NEG);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);
        final NetworkElementGroupDto neg = a4ResInv.getExistingNetworkElementGroup(negData.getUuid());

        assertNotNull(neg.getLastUpdateTime());
        assertTrue(neg.getLastUpdateTime().isBefore(oldDateTime), "lastUpdateTime (" + neg.getLastUpdateTime() + ") is newer than " + oldDateTime + "!");
    }

    @Then("the NEG lastSuccessfulSyncTime property was updated")
    public void thenTheNEGLastSuccessfulSyncTimePropertyWasUpdated() {
        final NetworkElementGroupDto neg = (NetworkElementGroupDto) testContext.getScenarioContext().getContext(Context.A4_NEG);
        final OffsetDateTime timeStamp = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);

        sleepForSeconds(2);
        a4ResInv.checkNetworkElementGroupIsUpdatedWithLastSuccessfulSyncTime(neg.getUuid(), timeStamp);
    }

    @Then("the NEG now has the following properties:")
    public void thenTheNEGNowHasTheFollowingProperties(DataTable table) {
        final NetworkElementGroupDto neg = (NetworkElementGroupDto) testContext.getScenarioContext().getContext(Context.A4_NEG);
        final Map<String, String> negMap = table.asMap();
        final NetworkElementGroupDto negDtoActual = a4ResInv.getExistingNetworkElementGroup(neg.getUuid());
        final ObjectMapper om = testContext.getObjectMapper();

        // https://stackoverflow.com/questions/34957051/how-to-get-rid-of-type-safety-unchecked-cast-from-object-to-mapstring-string
        @SuppressWarnings("unchecked")
        Map<String, Object> negMapActual = om.convertValue(negDtoActual, Map.class);

        negMap.keySet().forEach(k -> {
                    if (negMap.get(k) != null && negMapActual.get(k) == null)
                        fail("Expected property '" + k + "' is not present in updated NEG!");
                    assertEquals("Property '" + k + "' differs!", negMap.get(k), negMapActual.get(k).toString());
                }
        );
    }


    // -----=====[ HELPERS ]=====-----

    private NetworkElementGroupDto setupDefaultNegTestData() {
        return a4ResInvMapper.getDefaultNetworkElementGroupData();
    }

    private void persistNeg(String negAlias, NetworkElementGroupDto neg) {
        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteA4NetworkElementGroupsRecursively(neg);

        a4ResInv.createNetworkElementGroup(neg);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NEG, negAlias, neg);
    }

    private void createNeg(String negAlias) {
        NetworkElementGroupDto neg = setupDefaultNegTestData();

        persistNeg(negAlias, neg);
    }

    private void createNegWithName(String negAlias, String name) {
        NetworkElementGroupDto neg = setupDefaultNegTestData();
        neg.setName(name);

        persistNeg(negAlias, neg);
    }

    private void createNegWithType(String negAlias, String type) {
        NetworkElementGroupDto neg = setupDefaultNegTestData();
        neg.setType(type);

        persistNeg(negAlias, neg);
    }

    private void createNeg(String negAlias, String ops, String lcs) {
        NetworkElementGroupDto neg = setupDefaultNegTestData();
        neg.setOperationalState(ops);
        neg.setLifecycleState(lcs);

        persistNeg(negAlias, neg);
    }

    private void createNeg(String alias, DataTable table) {
        final Map<String, String> negMap = table.asMap();
        final ObjectMapper om = testContext.getObjectMapper();
        final TokenBuffer buffer = new TokenBuffer(om, false);

        // First create a new NEG default test data set...
        final NetworkElementGroupDto negDefault = a4ResInvMapper.getDefaultNetworkElementGroupData();

        try {
            // ... then overwrite default data set with data provided in given-step data table
            om.writeValue(buffer, negMap);
            NetworkElementGroupDto neg = om.readerForUpdating(negDefault).readValue(buffer.asParser());
            persistNeg(alias, neg);

        } catch (IOException e) {
            fail("Unexpected mapping error: " + e.getMessage());
        }
    }

}
