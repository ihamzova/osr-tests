package cucumber.stepdefinitions.team.berlinium;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementlink.A4NetworkElementLinkCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilea10nsp.A4NetworkServiceProfileA10NspCase;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.DEFAULT;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.DEFAULT_B;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getRandomDigits;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static java.util.stream.Collectors.toList;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.FileAssert.fail;

public class A4ResInvSteps {

    private final A4ResourceInventoryRobot a4ResInv = new A4ResourceInventoryRobot();
    private final A4ResourceInventoryMapper a4ResInvMapper = new A4ResourceInventoryMapper();
    private final TestContext testContext;

    public A4ResInvSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    @After
    public void cleanup() {
        final boolean NEG_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NEG);
        if (NEG_PRESENT) {
            final List<NetworkElementGroupDto> negList = testContext.getScenarioContext().getAllContext(Context.A4_NEG).stream()
                    .map(neg -> (NetworkElementGroupDto) neg)
                    .collect(toList());

            negList.forEach(neg -> a4ResInv.deleteA4NetworkElementGroupsRecursively(neg.getName()));
        }

        final boolean CSV_PRESENT = testContext.getScenarioContext().isContains(Context.A4_CSV);
        if (CSV_PRESENT) {
            final A4ImportCsvData csv = (A4ImportCsvData) testContext.getScenarioContext().getContext(Context.A4_CSV);
            a4ResInv.deleteA4TestDataRecursively(csv);
        }
    }

    // -----=====[ GIVENS ]=====-----

    @Given("a NEG( is existing)( in A4 resource inventory)")
    public void givenANEGIsExistingInA4ResourceInventory() {
        createNeg(DEFAULT);
    }

    @Given("a/another NEG \\(called {string})( is existing)( in A4 resource inventory)")
    public void givenANEGWithAliasIsExistingInA4ResourceInventory(String alias) {
        createNeg(alias);
    }

    @Given("a NEG with name {string}( is existing)( in A4 resource inventory)")
    public void givenANEGWithNameIsExistingInA4ResourceInventory(String name) {
        createNegWithName(DEFAULT, name);
    }

    @Given("a/another NEG \\(called {string}) with name {string}( is existing)( in A4 resource inventory)")
    public void givenANEGWithAliasWithNameIsExistingInA4ResourceInventory(String alias, String name) {
        createNegWithName(alias, name);
    }

    @Given("a NEG with type {string}( is existing)( in A4 resource inventory)")
    public void givenANEGWithTypeIsExistingInA4ResourceInventory(String type) {
        createNegWithType(DEFAULT, type);
    }

    @Given("a/another NEG \\(called {string}) with type {string}( is existing)( in A4 resource inventory)")
    public void givenANEGWithAliasWithTypeIsExistingInA4ResourceInventory(String alias, String type) {
        createNegWithType(alias, type);
    }

    @Given("a NEG with operational state {string} and lifecycle state {string}( is existing)( in A4 resource inventory)")
    public void givenANEGWithOperationalStateAndLifecycleStateIsExistingInA4ResourceInventory(String ops, String lcs) {
        createNeg(DEFAULT, ops, lcs);
    }

    @Given("a/another NEG \\(called {string}) with operational state {string} and lifecycle state {string}( is existing)( in A4 resource inventory)")
    public void givenANEGWithAliasWithOperationalStateAndLifecycleStateIsExistingInA4ResourceInventory(String alias, String ops, String lcs) {
        createNeg(alias, ops, lcs);
    }

    /**
     * Creates a NEG in a4 resource inventory, each property filled with default test data.
     * If any NEG with colliding unique constraint (property 'name') already exists, then the old NEG is deleted first.
     *
     * @param table Contains explicit properties and values with which the default test data is overwritten
     */
    @Given("a NEG with the following properties( is existing)( in A4 resource inventory):")
    public void givenANEGWithTheFollowingProperties(DataTable table) {
        createNeg(DEFAULT, table);
    }

    @Given("a/another NEG \\(called {string}) with the following properties( is existing)( in A4 resource inventory):")
    public void givenANEGWithAliasWithTheFollowingProperties(String alias, DataTable table) {
        createNeg(alias, table);
    }

    @Given("no NEG exists( in A4 resource inventory)")
    public void givenNoNEGExistsInA4ResourceInventory() {
        NetworkElementGroupDto neg = new NetworkElementGroupDto();
        neg.setUuid(UUID.randomUUID().toString());

        // Make sure that NEG really doesn't exist
        a4ResInv.deleteA4NetworkElementGroupsRecursively(neg.getUuid());

        testContext.getScenarioContext().setContext(Context.A4_NEG, neg);
    }

    @Given("no NEG with name {string} exists( in A4 resource inventory)")
    public void noNEGWithNameIsExistingInResourceInventory(String name) {
        NetworkElementGroupDto neg = new NetworkElementGroupDto();
        neg.setName(name);

        // Make sure that NEG really doesn't exist
        a4ResInv.deleteA4NetworkElementGroupsRecursively(neg.getName());

        testContext.getScenarioContext().setContext(Context.A4_NEG, neg);
    }

    @Given("a NE( is existing)( in A4 resource inventory)")
    public void givenANeIsExistingInA4ResourceInventory() {
        createNe(DEFAULT, DEFAULT);
    }

    @Given("a/another NE \\(called {string})( connected to the NEG)( is existing)( in A4 resource inventory)")
    public void givenANeWithAliasIsExistingInA4ResourceInventory(String neAlias) {
        createNe(neAlias, DEFAULT);
    }

    @Given("a/another NE \\(called {string}) connected to the NEG {string}( is existing)( in A4 resource inventory)")
    public void givenANeWithAliasConnectedToNegIsExistingInA4ResourceInventory(String neAlias, String negAlias) {
        createNe(neAlias, negAlias);
    }

    private void createNe(String neAlias, String negAlias) {
        NetworkElementDto ne = setupDefaultNeTestData(negAlias);

        persistNe(neAlias, ne);
    }

    private void persistNe(String neAlias, NetworkElementDto ne) {
        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteA4NetworkElementsRecursivelyDto(ne);

        a4ResInv.createNetworkElement(ne);
        testContext.getScenarioContext().setContext(Context.A4_NE, neAlias, ne);
    }

    @Given("a NE with operational state {string} and lifecycle state {string}( connected to the NEG)( is existing)( in A4 resource inventory)")
    public void givenANEWithOperationalStateAndLifecycleStateIsExistingInA4ResourceInventory(String ops, String lcs) {
        createNeWithStates(DEFAULT, ops, lcs, DEFAULT);
    }

    @Given("a/another NE \\(called {string}) with operational state {string} and lifecycle state {string}( connected to the NEG)( is existing)( in A4 resource inventory)")
    public void givenANEWithOperationalStateAndLifecycleStateIsExistingInA4ResourceInventory(String neAlias, String ops, String lcs) {
        createNeWithStates(neAlias, ops, lcs, DEFAULT);
    }

    @Given("a/another NE \\(called {string}) with operational state {string} and lifecycle state {string} connected to the NEG {string}( is existing)( in A4 resource inventory)")
    public void givenANEWithOperationalStateAndLifecycleStateIsExistingInA4ResourceInventory(String neAlias, String ops, String lcs, String negAlias) {
        createNeWithStates(neAlias, ops, lcs, negAlias);
    }

    private void createNeWithStates(String neAlias, String opState, String lcState, String negAlias) {
        NetworkElementDto ne = setupDefaultNeTestData(negAlias);
        ne.setOperationalState(opState);
        ne.setLifecycleState(lcState);

        persistNe(neAlias, ne);
    }

    /**
     * Creates a NE in a4 resource inventory, each property filled with default test data.
     * If any NE with colliding unique constraint ('ztpIdent' and 'EndSz') already exists, then the old NE is deleted first.
     *
     * @param table Contains explicit properties and values with which the default test data is overwritten
     */
    @Given("a NE with the following properties( connected to the NEG)( is existing)( in A4 resource inventory):")
    public void givenANeWithTheFollowingProperties(DataTable table) {
        createNe(DEFAULT, table, DEFAULT);
    }

    @Given("a/another NE \\(called {string}) with the following properties( connected to the NEG)( is existing)( in A4 resource inventory):")
    public void givenANeWithAliasWithTheFollowingProperties(String neAlias, DataTable table) {
        createNe(neAlias, table, DEFAULT);
    }

    @Given("a/another NE \\(called {string}) with the following properties connected to the NEG {string}( is existing)( in A4 resource inventory):")
    public void givenANeWithAliasWithTheFollowingProperties(String neAlias, DataTable table, String negAlias) {
        createNe(neAlias, table, negAlias);
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

    @Given("a NE with VPSZ {string} and FSZ {string}( connected to the NEG)( is existing)( in A4 resource inventory)")
    public void givenANeWithVpszAndFszIsExistingInA4ResourceInventory(String vpsz, String fsz) {
        createNeWithEndsz(DEFAULT, vpsz, fsz, DEFAULT);
    }

    @Given("a/another NE \\(called {string}) with VPSZ {string} and FSZ {string}( connected to the NEG)( is existing)( in A4 resource inventory)")
    public void givenANeWithVpszAndFszIsExistingInA4ResourceInventory(String neAlias, String vpsz, String fsz) {
        createNeWithEndsz(neAlias, vpsz, fsz, DEFAULT);
    }

    @Given("a/another NE \\(called {string}) with VPSZ {string} and FSZ {string} connected to the NEG {string} ( is existing)( in A4 resource inventory)")
    public void givenANeWithVpszAndFszIsExistingInA4ResourceInventory(String neAlias, String vpsz, String fsz, String negAlias) {
        createNeWithEndsz(neAlias, vpsz, fsz, negAlias);
    }

    private void createNeWithEndsz(String neAlias, String vpsz, String fsz, String negAlias) {
        NetworkElementDto ne = setupDefaultNeTestData(negAlias);
        ne.setVpsz(vpsz);
        ne.setFsz(fsz);

        persistNe(neAlias, ne);
    }

    @Given("a NE with type {string} and category {string}( connected to the NEG)( is existing)( in A4 resource inventory)")
    public void givenANeWithTypeAndCategoryIsExistingInA4ResourceInventory(String type, String category) {
        createNeWithTypes(DEFAULT, type, category, DEFAULT);
    }

    @Given("a/another NE \\(called {string}) with type {string} and category {string}( connected to the NEG)( is existing)( in A4 resource inventory)")
    public void givenANeWithTypeAndCategoryIsExistingInA4ResourceInventory(String neAlias, String type, String category) {
        createNeWithTypes(neAlias, type, category, DEFAULT);
    }

    @Given("a/another NE \\(called {string}) with type {string} and category {string} connected to NEG {string}( is existing)( in A4 resource inventory)")
    public void givenANeWithTypeAndCategoryIsExistingInA4ResourceInventory(String neAlias, String type, String category, String negAlias) {
        createNeWithTypes(neAlias, type, category, negAlias);
    }

    private void createNeWithTypes(String neAlias, String type, String category, String negAlias) {
        NetworkElementDto ne = setupDefaultNeTestData(negAlias);
        ne.setType(type);
        ne.setCategory(category);

        persistNe(neAlias, ne);
    }

    @Given("no NE exists( in A4 resource inventory)")
    public void givenNoNEExistsInA4ResourceInventory() {
        A4NetworkElement ne = new A4NetworkElement();
        ne.setUuid(UUID.randomUUID().toString());

        a4ResInv.deleteA4NetworkElementsRecursively(ne); // TODO work with only uuid here

        testContext.getScenarioContext().setContext(Context.A4_NE, ne);
    }

    @Given("no NE with VPSZ {string} and FSZ {string} exists( in A4 resource inventory)")
    public void noNEWithVPSZAndFSZIsExistingInA4ResourceInventory(String vpsz, String fsz) {
        A4NetworkElement ne = new A4NetworkElement();
        ne.setUuid(UUID.randomUUID().toString());
        ne.setVpsz(vpsz);
        ne.setFsz(fsz);

        a4ResInv.deleteA4NetworkElementsRecursively(ne); // TODO work with only endsz here

        testContext.getScenarioContext().setContext(Context.A4_NE, ne);
    }

    @Given("a NEP( connected to the NE)( is existing)( in A4 resource inventory)")
    public void givenANEPIsExistingInA4ResourceInventory() {
        createNep(DEFAULT, DEFAULT);
    }

    @Given("a/another NEP \\(called {string})( connected to the NE)( is existing)( in A4 resource inventory)")
    public void givenANEPIsExistingInA4ResourceInventory(String nepAlias) {
        createNep(nepAlias, DEFAULT);
    }

    @Given("a/another NEP \\(called {string}) connected to NE {string}( is existing)( in A4 resource inventory)")
    public void givenANEPIsExistingInA4ResourceInventory(String nepAlias, String neAlias) {
        createNep(nepAlias, neAlias);
    }

    private void createNep(String nepAlias, String neAlias) {
        NetworkElementPortDto nep = setupDefaultNepTestData(neAlias);

        persistNep(nepAlias, nep, neAlias);
    }

    private void persistNep(String nepAlias, NetworkElementPortDto nep, String neAlias) {
        final NetworkElementDto ne = (NetworkElementDto) testContext.getScenarioContext().getContext(Context.A4_NE, neAlias);

        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteA4NetworkElementPortsRecursively(nep.getLogicalLabel(), ne.getVpsz(), ne.getFsz());

        a4ResInv.createNetworkElementPort(nep);
        testContext.getScenarioContext().setContext(Context.A4_NEP, nepAlias, nep);
    }

    @Given("a NEP with operational state {string} and description {string}( connected to the NE)( is existing)( in A4 resource inventory)")
    public void givenANEPWithOperationalStateAndDescriptionIsExistingInAResourceInventory(String opState, String descr) {
        createNepWithStates(DEFAULT, opState, descr, DEFAULT);
    }

    @Given("a/another NEP \\(called {string}) with operational state {string} and description {string}( connected to the NE)( is existing)( in A4 resource inventory)")
    public void givenANEPWithOperationalStateAndDescriptionIsExistingInAResourceInventory(String nepAlias, String opState, String descr) {
        createNepWithStates(nepAlias, opState, descr, DEFAULT);
    }

    @Given("a/another NEP \\(called {string}) with operational state {string} and description {string} connected to NE {string}( is existing)( in A4 resource inventory)")
    public void givenANEPWithOperationalStateAndDescriptionIsExistingInAResourceInventory(String nepAlias, String opState, String descr, String neAlias) {
        createNepWithStates(nepAlias, opState, descr, neAlias);
    }

    private void createNepWithStates(String nepAlias, String opState, String description, String neAlias) {
        NetworkElementPortDto nep = setupDefaultNepTestData(neAlias);
        nep.setOperationalState(opState);
        nep.setDescription(description);

        persistNep(nepAlias, nep, neAlias);
    }

    @Given("a NEP with type {string} and functional label {string}( connected to the NE)( is existing)( in A4 resource inventory)")
    public void givenANEPWithTypeAndFunctionalLabelIsExistingInA4ResourceInventory(String type, String functionalLabel) {
        createNepWithTypes(DEFAULT, type, functionalLabel, DEFAULT);
    }

    @Given("a/another NEP \\(called {string}) with type {string} and functional label {string}( connected to the NE)( is existing)( in A4 resource inventory)")
    public void givenANEPWithTypeAndFunctionalLabelIsExistingInA4ResourceInventory(String nepAlias, String type, String functionalLabel) {
        createNepWithTypes(nepAlias, type, functionalLabel, DEFAULT);
    }

    @Given("a/another NEP \\(called {string}) with type {string} and functional label {string} connected to NE {string}( is existing)( in A4 resource inventory)")
    public void givenANEPWithTypeAndFunctionalLabelIsExistingInA4ResourceInventory(String nepAlias, String type, String functionalLabel, String neAlias) {
        createNepWithTypes(nepAlias, type, functionalLabel, neAlias);
    }

    private void createNepWithTypes(String nepAlias, String type, String functionalLabel, String neAlias) {
        NetworkElementPortDto nep = setupDefaultNepTestData(neAlias);
        nep.setType(type);
        nep.setLogicalLabel(functionalLabel);

        persistNep(nepAlias, nep, neAlias);
    }

    @Given("a NEL is existing in A4 resource inventory")
    public void givenANELIsExistingInA4ResourceInventory() {
        // ACTION
        A4NetworkElementLink nel = setupDefaultNelTestData();
        final NetworkElementDto neA = (NetworkElementDto) testContext.getScenarioContext().getContext(Context.A4_NE, DEFAULT);
        final NetworkElementDto neB = (NetworkElementDto) testContext.getScenarioContext().getContext(Context.A4_NE, DEFAULT_B);
        final NetworkElementPortDto nepA = (NetworkElementPortDto) testContext.getScenarioContext().getContext(Context.A4_NEP, DEFAULT);
        final NetworkElementPortDto nepB = (NetworkElementPortDto) testContext.getScenarioContext().getContext(Context.A4_NEP, DEFAULT_B);

//        a4ResInv.createNetworkElementLink(nel, nepA, nepB, neA, neB);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NEL, nel);
    }

    private void createNel() {
        NetworkElementLinkDto nel = setupDefaultNelTestData(nepAlias1, nepAlias2);

        persistNel(nelAlias, nel);
    }

    private void persistNel() {
        a4ResInv.createNetworkElementLink(nel, nepA, nepB, neA, neB);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NEL, nel);
    }

    @Given("a NEL with operational state {string} and lifecycle state {string} is existing in A4 resource inventory")
    public void givenANELWithOperationalStateAndLifecycleStateIsExistingInA4ResourceInventory(String ops, String lcs) {
        // ACTION
        A4NetworkElementLink nel = setupDefaultNelTestData();
        final NetworkElementDto neA = (NetworkElementDto) testContext.getScenarioContext().getContext(Context.A4_NE, DEFAULT);
        final NetworkElementDto neB = (NetworkElementDto) testContext.getScenarioContext().getContext(Context.A4_NE, DEFAULT_B);
        final NetworkElementPortDto nepA = (NetworkElementPortDto) testContext.getScenarioContext().getContext(Context.A4_NEP, DEFAULT);
        final NetworkElementPortDto nepB = (NetworkElementPortDto) testContext.getScenarioContext().getContext(Context.A4_NEP, DEFAULT_B);
        nel.setOperationalState(ops);
        nel.setLifecycleState(lcs);

//        a4ResInv.createNetworkElementLink(nel, nepA, nepB, neA, neB);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NEL, nel);
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

    @Given("a NSP FTTH-Access with operational state {string} and NEP reference {string} is existing in A4 resource inventory")
    public void givenANspFtthAccessWithOperationalStateAndNepReferenceIsExistingInAResourceInventory(String opState, String portUuid) {
        // ACTION
        A4NetworkServiceProfileFtthAccess nspFtthAccess = setupDefaultNspFtthTestData();
        nspFtthAccess.setOperationalState(opState);
        nspFtthAccess.setOltPortOntLastRegisteredOn(portUuid);

        final A4TerminationPoint tp = (A4TerminationPoint) testContext.getScenarioContext().getContext(Context.A4_TP);

        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteNetworkServiceProfilesFtthAccessConnectedToTerminationPoint(tp.getUuid());

        a4ResInv.createNetworkServiceProfileFtthAccess(nspFtthAccess, tp);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NSP_FTTH, nspFtthAccess);
    }


    @Given("a NSP FTTH-Access with operationalState {string} and lifecycleState {string} is existing in A4 resource inventory")
    public void givenNspFtthAccessWithLineIDIsExistingInA4ResourceInventoryForTheTP(String operationalState, String lifecycleState) {
        // ACTION

        A4NetworkServiceProfileFtthAccess nspFtthAccess = setupDefaultNspFtthTestData();
        nspFtthAccess.setOperationalState(operationalState);
        nspFtthAccess.setLifecycleState(lifecycleState);

        final A4TerminationPoint tp = (A4TerminationPoint) testContext.getScenarioContext().getContext(Context.A4_TP);

        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteNetworkServiceProfilesFtthAccessConnectedToTerminationPoint(tp.getUuid());

        a4ResInv.createNetworkServiceProfileFtthAccess(nspFtthAccess, tp);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NSP_FTTH, nspFtthAccess);
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

    @Given("a NSP A10NSP with operationalState {string} and lifecycleState {string} is existing in A4 resource inventory")
    public void givenNspA10nspWithLineIDIsExistingInA4ResourceInventoryForTheTP(String operationalState, String lifecycleState) {
        // ACTION
        A4NetworkServiceProfileA10Nsp nspA10nsp = setupDefaultNspA10NspTestData();
        nspA10nsp.setOperationalState(operationalState);
        nspA10nsp.setLifecycleState(lifecycleState);

        final A4TerminationPoint tp = (A4TerminationPoint) testContext.getScenarioContext().getContext(Context.A4_TP);

        a4ResInv.createNetworkServiceProfileA10Nsp(nspA10nsp, tp);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NSP_A10NSP, nspA10nsp);
    }


    // -----=====[ THENS ]=====-----

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
        // INPUT FROM SCENARIO CONTEXT
        final NetworkElementDto neData = (NetworkElementDto) testContext.getScenarioContext().getContext(Context.A4_NE);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);

        // ACTION
        final NetworkElementDto ne = a4ResInv.getExistingNetworkElement(neData.getUuid());
        assertNotNull(ne.getCreationTime());
        assertTrue(ne.getCreationTime().isBefore(oldDateTime), "creationTime (" + ne.getCreationTime() + ") is newer than " + oldDateTime + "!");
    }


    @Then("{int} NEP(s) connected to the NE with VPSZ {string} and FSZ {string} do/does exist( in A4 resource inventory)")
    public void thenXNepsConnectedToTheNEWithVPSZAndFSZDoExistInAResourceInventory(int count, String vpsz, String fsz) {
        // ACTION
        final List<NetworkElementDto> neList = a4ResInv.getNetworkElementsByVpszFsz(vpsz, fsz);
        assertEquals(1, neList.size());

        final List<NetworkElementPortDto> nepList = a4ResInv.getNetworkElementPortsByNetworkElement(neList.get(0).getUuid());
        assertEquals(count, nepList.size());
    }

    @Then("the (new )NEP operationalState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNepOperationalStateIsUpdatedInA4ResInv(String operationalState) {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkElementPortDto nepData = (NetworkElementPortDto) testContext.getScenarioContext().getContext(Context.A4_NEP);

        // ACTION
        final NetworkElementPortDto nep = a4ResInv.getExistingNetworkElementPort(nepData.getUuid());
        assertEquals(operationalState, nep.getOperationalState());
    }

    @Then("the (new )NEP operationalState is (now )deleted( in the A4 resource inventory)")
    public void thenTheNepOperationalStateIsDeletedInA4ResInv() {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkElementPortDto nepData = (NetworkElementPortDto) testContext.getScenarioContext().getContext(Context.A4_NEP);

        // ACTION
        final NetworkElementPortDto nep = a4ResInv.getExistingNetworkElementPort(nepData.getUuid());
        assertNull(nep.getOperationalState());
    }

    @Then("the (new )NEP description is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNEPDescriptionIsUpdatedTo(String newDescr) {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkElementPortDto nepData = (NetworkElementPortDto) testContext.getScenarioContext().getContext(Context.A4_NEP);

        // ACTION
        final NetworkElementPortDto nep = a4ResInv.getExistingNetworkElementPort(nepData.getUuid());
        assertEquals(newDescr, nep.getDescription());
    }

    @Then("the NEP lastUpdateTime is updated")
    public void thenTheNEPLastUpdateTimeIsUpdated() {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkElementPortDto nepData = (NetworkElementPortDto) testContext.getScenarioContext().getContext(Context.A4_NEP);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);

        // ACTION
        final NetworkElementPortDto nep = a4ResInv.getExistingNetworkElementPort(nepData.getUuid());
        assertNotNull(nep.getLastUpdateTime());
        assertTrue(nep.getLastUpdateTime().isAfter(oldDateTime), "lastUpdateTime (" + nep.getLastUpdateTime() + ") is older than " + oldDateTime + "!");
    }

    @Then("the NEP lastUpdateTime is not updated")
    public void thenTheNEPLastUpdateTimeIsNotUpdated() {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkElementPortDto nepData = (NetworkElementPortDto) testContext.getScenarioContext().getContext(Context.A4_NEP);
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


    @Then("the (new )NSP FTTH-Access operationalState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNspFtthAccessOperationalStateIsUpdatedInA4ResInv(String operationalState) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkServiceProfileFtthAccess nspFtthAccessData = (A4NetworkServiceProfileFtthAccess) testContext
                .getScenarioContext().getContext(Context.A4_NSP_FTTH);

        // ACTION
        final NetworkServiceProfileFtthAccessDto nspFtthAccess = a4ResInv
                .getExistingNetworkServiceProfileFtthAccess(nspFtthAccessData.getUuid());
        assertEquals(operationalState, nspFtthAccess.getOperationalState());
    }

    @Then("the (new )NSP FTTH-Access NEP reference is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNspFtthAccessNepReferenceIsUpdatedTo(String portUuid) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkServiceProfileFtthAccess nspFtthAccessData = (A4NetworkServiceProfileFtthAccess) testContext
                .getScenarioContext().getContext(Context.A4_NSP_FTTH);

        // ACTION
        final NetworkServiceProfileFtthAccessDto nspFtthAccess = a4ResInv
                .getExistingNetworkServiceProfileFtthAccess(nspFtthAccessData.getUuid());
        assertEquals(portUuid, nspFtthAccess.getOltPortOntLastRegisteredOn());
    }

    @Then("the (new )NSP FTTH-Access lifecycleState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNspFtthAccessLifecycleStateIsUpdatedInA4ResInv(String lifecycleState) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkServiceProfileFtthAccess nspFtthAccessData = (A4NetworkServiceProfileFtthAccess) testContext
                .getScenarioContext().getContext(Context.A4_NSP_FTTH);

        // ACTION
        final NetworkServiceProfileFtthAccessDto nspFtthAccess = a4ResInv
                .getExistingNetworkServiceProfileFtthAccess(nspFtthAccessData.getUuid());
        assertEquals(lifecycleState, nspFtthAccess.getLifecycleState());
    }


    @Then("the NSP FTTH-Access lastUpdateTime is updated")
    public void thenTheNspFtthAccessLastUpdateTimeIsUpdated() {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkServiceProfileFtthAccess nspFtthAccessData = (A4NetworkServiceProfileFtthAccess) testContext
                .getScenarioContext().getContext(Context.A4_NSP_FTTH);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);

        // ACTION
        final NetworkServiceProfileFtthAccessDto nspFtthAccess = a4ResInv
                .getExistingNetworkServiceProfileFtthAccess(nspFtthAccessData.getUuid());
        assertNotNull(nspFtthAccess.getLastUpdateTime());
        assertTrue(nspFtthAccess.getLastUpdateTime().isAfter(oldDateTime), "lastUpdateTime (" + nspFtthAccess.getLastUpdateTime() + ") is older than " + oldDateTime + "!");
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

    @Then("the (new )NSP A10NSP operationalState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNspA10nspOperationalStateIsUpdatedInA4ResInv(String operationalState) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkServiceProfileA10Nsp nspA10nspData = (A4NetworkServiceProfileA10Nsp) testContext
                .getScenarioContext().getContext(Context.A4_NSP_A10NSP);

        // ACTION
        final NetworkServiceProfileA10NspDto nspA10nsp = a4ResInv
                .getExistingNetworkServiceProfileA10Nsp(nspA10nspData.getUuid());
        assertEquals(operationalState, nspA10nsp.getOperationalState());
    }

    @Then("the (new )NSP A10NSP lifecycleState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNspA10nspLifecycleStateIsUpdatedInA4ResInv(String lifecycleState) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkServiceProfileA10Nsp nspA10nspData = (A4NetworkServiceProfileA10Nsp) testContext
                .getScenarioContext().getContext(Context.A4_NSP_A10NSP);

        // ACTION
        final NetworkServiceProfileA10NspDto nspA10nsp = a4ResInv
                .getExistingNetworkServiceProfileA10Nsp(nspA10nspData.getUuid());
        assertEquals(lifecycleState, nspA10nsp.getLifecycleState());
    }

    @Then("the NSP A10NSP lastUpdateTime is updated")
    public void thenTheNspA10nspLastUpdateTimeIsUpdated() {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkServiceProfileA10Nsp nspA10nspData = (A4NetworkServiceProfileA10Nsp) testContext
                .getScenarioContext().getContext(Context.A4_NSP_A10NSP);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);

        // ACTION
        final NetworkServiceProfileA10NspDto nspA10nsp = a4ResInv
                .getExistingNetworkServiceProfileA10Nsp(nspA10nspData.getUuid());
        assertNotNull(nspA10nsp.getLastUpdateTime());
        assertTrue(nspA10nsp.getLastUpdateTime().isAfter(oldDateTime), "lastUpdateTime (" + nspA10nsp.getLastUpdateTime() + ") is older than " + oldDateTime + "!");
    }

    // -----=====[ HELPERS ]=====-----

    private NetworkElementGroupDto setupDefaultNegTestData() {
        // ACTION
        A4NetworkElementGroup neg = testContext.getOsrTestContext().getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        neg.setUuid(UUID.randomUUID().toString());
        neg.setName("NEG-" + getRandomDigits(6));

        return a4ResInvMapper.getNetworkElementGroupDto(neg);
    }

    private void persistNeg(String negAlias, NetworkElementGroupDto neg) {
        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteA4NetworkElementGroupsRecursively(neg.getName());

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
        final NetworkElementGroupDto negDefault = setupDefaultNegTestData();

        try {
            // ... then overwrite default data set with data provided in given-step data table
            om.writeValue(buffer, negMap);
            NetworkElementGroupDto neg = om.readerForUpdating(negDefault).readValue(buffer.asParser());
            persistNeg(alias, neg);

        } catch (IOException e) {
            fail("Unexpected mapping error: " + e.getMessage());
        }
    }

    private NetworkElementDto setupDefaultNeTestData(String negAlias) {
        final boolean NEG_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NEG, negAlias);

        // NE needs to be connected to a NEG, so if no NEG present, create one
        if (!NEG_PRESENT)
            givenANEGWithAliasIsExistingInA4ResourceInventory(negAlias);

        final NetworkElementGroupDto neg = (NetworkElementGroupDto) testContext.getScenarioContext().getContext(Context.A4_NEG, negAlias);

        A4NetworkElement ne = testContext.getOsrTestContext().getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        ne.setUuid(UUID.randomUUID().toString());
        ne.setVpsz("49/" + getRandomDigits(4) + "/" + getRandomDigits(3));

        return a4ResInvMapper.getNetworkElementDto(ne, neg);
    }

    private NetworkElementPortDto setupDefaultNepTestData(String neAlias) {
        final boolean NE_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NE, neAlias);

        // NEP needs to be connected to a NE, so if no NE present, create one
        if (!NE_PRESENT)
            givenANeWithAliasIsExistingInA4ResourceInventory(neAlias);

        final NetworkElementDto ne = (NetworkElementDto) testContext.getScenarioContext().getContext(Context.A4_NE, neAlias);

        A4NetworkElementPort nep = testContext.getOsrTestContext().getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
        nep.setUuid(UUID.randomUUID().toString());

        return a4ResInvMapper.getNetworkElementPortDto(nep, ne);
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

    private NetworkElementLinkDto setupDefaultNelTestData(String nepAlias1, String nepAlias2) {
        // INPUT FROM SCENARIO CONTEXT
        final boolean NE_A_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NE, DEFAULT);
        final boolean NE_B_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NE, DEFAULT_B);
        final boolean NEP_A_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NEP, nepAlias1);
        final boolean NEP_B_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NEP, nepAlias2);

        // ACTION

        // NEL needs to be connected to 2 NEP, so if no NEP present, create one
        if (!NE_A_PRESENT)
            givenANeIsExistingInA4ResourceInventory();
        if (!NE_B_PRESENT)
            givenANeWithAliasIsExistingInA4ResourceInventory(DEFAULT_B);
        if (!NEP_A_PRESENT)
            givenANEPIsExistingInA4ResourceInventory(DEFAULT);
        if (!NEP_B_PRESENT)
            givenANEPIsExistingInA4ResourceInventory(DEFAULT_B, DEFAULT_B);

        A4NetworkElementLink nel = testContext.getOsrTestContext().getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.defaultNetworkElementLink);
        nel.setUuid(UUID.randomUUID().toString());

        return a4ResInvMapper.getNetworkElementLinkDto(nel);
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

    private A4NetworkServiceProfileA10Nsp setupDefaultNspA10NspTestData() {
        // INPUT FROM SCENARIO CONTEXT
        final boolean TP_PRESENT = testContext.getScenarioContext().isContains(Context.A4_TP);

        // ACTION

        // NSP needs to be connected to a TP, so if no TP present, create one
        if (!TP_PRESENT)
            givenATPIsExistingInA4ResourceInventory();

        A4NetworkServiceProfileA10Nsp nspA10nsp = testContext.getOsrTestContext().getData()
                .getA4NetworkServiceProfileA10NspDataProvider()
                .get(A4NetworkServiceProfileA10NspCase.defaultNetworkServiceProfileA10Nsp);
        nspA10nsp.setUuid(UUID.randomUUID().toString());

        return nspA10nsp;
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
