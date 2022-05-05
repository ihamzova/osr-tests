package cucumber.stepdefinitions.team.berlinium;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryMapper;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkServiceProfileL2Bsa;
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
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getPortNumberByFunctionalPortLabel;
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
        a4ResInv.deleteA4NetworkElementGroupsRecursively(neg.getUuid());

        testContext.getScenarioContext().setContext(Context.A4_NEG, neg);
    }

    @Given("no NEG with name {string} exists( in A4 resource inventory)")
    public void givenA4NegWithNameNotExist(String name) {
        NetworkElementGroupDto neg = new NetworkElementGroupDto();
        neg.setName(name);

        // Make sure that NEG really doesn't exist
        a4ResInv.deleteA4NetworkElementGroupsRecursively(neg.getName());

        testContext.getScenarioContext().setContext(Context.A4_NEG, neg);
    }

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
        A4NetworkElement ne = new A4NetworkElement();
        ne.setUuid(UUID.randomUUID().toString());

        a4ResInv.deleteA4NetworkElementsRecursivelyByUuid(ne.getUuid());

        testContext.getScenarioContext().setContext(Context.A4_NE, ne);
    }

    @Given("no NE with VPSZ {string} and FSZ {string} exists( in A4 resource inventory)")
    public void givenA4NWithVpszFszNotExist(String vpsz, String fsz) {
        A4NetworkElement ne = new A4NetworkElement();
        ne.setUuid(UUID.randomUUID().toString());
        ne.setVpsz(vpsz);
        ne.setFsz(fsz);

        a4ResInv.deleteA4NetworkElementsRecursively(vpsz, fsz);

        testContext.getScenarioContext().setContext(Context.A4_NE, ne);
    }

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

    @Given("a NSP A10NSP with operationalState {string} and lifecycleState {string}( connected to the TP)( is existing)( in A4 resource inventory)")
    public void givenNspA10nspWithLineIDIsExistingInA4ResourceInventoryForTheTP(String operationalState, String lifecycleState) {
        createNspA10NspWithStates(DEFAULT, operationalState, lifecycleState, DEFAULT);
    }

    @Given("no NSP L2BSA( connected to the TP)( exists in A4)( resource inventory)")
    public void givenNoNspL2BsaExistsInA4ResourceInventoryForTheTP() {
        NetworkServiceProfileL2BsaDto nspL2Bsa = new NetworkServiceProfileL2BsaDto();
        nspL2Bsa.setUuid(UUID.randomUUID().toString());

        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteNetworkServiceProfileL2BsaWithoutCheck(nspL2Bsa.getUuid());

        testContext.getScenarioContext().setContext(Context.A4_NSP_L2BSA, nspL2Bsa);
    }

    @Given("a/another NSP A10NSP with operationalState {string} and lifecycleState {string} connected to TP {string}( is existing)( in A4 resource inventory)")
    public void givenNspA10nspWithLineIDIsExistingInA4ResourceInventoryForTheTP(String operationalState, String lifecycleState, String tpAlias) {
        createNspA10NspWithStates(DEFAULT, operationalState, lifecycleState, tpAlias);
    }

    @Given("a/another NSP A10NSP {string} with operationalState {string} and lifecycleState {string} connected to TP {string}( is existing)( in A4 resource inventory)")
    public void givenNspA10nspWithLineIDIsExistingInA4ResourceInventoryForTheTP(String nspAlias, String operationalState, String lifecycleState, String tpAlias) {
        createNspA10NspWithStates(nspAlias, operationalState, lifecycleState, tpAlias);
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
        final NetworkElementDto neData = (NetworkElementDto) testContext.getScenarioContext().getContext(Context.A4_NE);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);
        final NetworkElementDto ne = a4ResInv.getExistingNetworkElement(neData.getUuid());

        assertNotNull(ne.getCreationTime());
        assertTrue(ne.getCreationTime().isBefore(oldDateTime), "creationTime (" + ne.getCreationTime() + ") is newer than " + oldDateTime + "!");
    }


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

    @Then("the TP does exist in A4 resource inventory")
    public void thenTheTPDoesExistInA4ResourceInventory() {
        final TerminationPointDto tp = (TerminationPointDto) testContext.getScenarioContext().getContext(Context.A4_TP);

        a4ResInv.checkTerminationPointExists(tp.getUuid());
    }

    @Then("the TP does not exist in A4 resource inventory( anymore)( any longer)")
    public void thenTheTPIsDoesNotExistInA4ResourceInventoryAnymore() {
        final TerminationPointDto tp = (TerminationPointDto) testContext.getScenarioContext().getContext(Context.A4_TP);

        a4ResInv.checkTerminationPointIsDeleted(tp.getUuid());
    }

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

    @Then("the (new )NSP A10NSP operationalState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNspA10nspOperationalStateIsUpdatedInA4ResInv(String operationalState) {
        final NetworkServiceProfileA10NspDto nspA10nspData = (NetworkServiceProfileA10NspDto) testContext
                .getScenarioContext().getContext(Context.A4_NSP_A10NSP);
        final NetworkServiceProfileA10NspDto nspA10nsp = a4ResInv
                .getExistingNetworkServiceProfileA10Nsp(nspA10nspData.getUuid());

        assertEquals(operationalState, nspA10nsp.getOperationalState());
    }

    @Then("the (new )NSP A10NSP lifecycleState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNspA10nspLifecycleStateIsUpdatedInA4ResInv(String lifecycleState) {
        final NetworkServiceProfileA10NspDto nspA10nspData = (NetworkServiceProfileA10NspDto) testContext
                .getScenarioContext().getContext(Context.A4_NSP_A10NSP);
        final NetworkServiceProfileA10NspDto nspA10nsp = a4ResInv
                .getExistingNetworkServiceProfileA10Nsp(nspA10nspData.getUuid());

        assertEquals(lifecycleState, nspA10nsp.getLifecycleState());
    }

    @Then("the NSP A10NSP lastUpdateTime is updated")
    public void thenTheNspA10nspLastUpdateTimeIsUpdated() {
        final NetworkServiceProfileA10NspDto nspA10nspData = (NetworkServiceProfileA10NspDto) testContext
                .getScenarioContext().getContext(Context.A4_NSP_A10NSP);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);
        final NetworkServiceProfileA10NspDto nspA10nsp = a4ResInv
                .getExistingNetworkServiceProfileA10Nsp(nspA10nspData.getUuid());

        assertNotNull(nspA10nsp.getLastUpdateTime());
        assertTrue(nspA10nsp.getLastUpdateTime().isAfter(oldDateTime), "lastUpdateTime (" + nspA10nsp.getLastUpdateTime() + ") is older than " + oldDateTime + "!");
    }

    // -----=====[ HELPERS ]=====-----

    private NetworkElementGroupDto setupDefaultNegTestData() {
        return a4ResInvMapper.getDefaultNetworkElementGroupData();
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

    private NetworkElementDto setupDefaultNeTestData(String negAlias) {
        final boolean NEG_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NEG, negAlias);

        // NE needs to be connected to a NEG, so if no NEG present, create one
        if (!NEG_PRESENT)
            givenA4NegWithAlias(negAlias);

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

    private NetworkElementPortDto setupDefaultNepTestData(String neAlias) {
        final boolean NE_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NE, neAlias);

        // NEP needs to be connected to a NE, so if no NE present, create one
        if (!NE_PRESENT)
            givenA4NeWithAlias(neAlias);

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

    private NetworkElementLinkDto setupDefaultNelTestData(String nepAlias1, String nepAlias2) {
        final boolean NEP_A_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NEP, nepAlias1);
        final boolean NEP_B_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NEP, nepAlias2);

        // NEL needs to be connected to 2 NEPs, so if no NEPs present, create them
        if (!NEP_A_PRESENT)
            givenANEPIsExistingInA4ResourceInventory(nepAlias1, nepAlias1);
        if (!NEP_B_PRESENT)
            givenANEPIsExistingInA4ResourceInventory(nepAlias2, nepAlias2);

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

    private TerminationPointDto setupDefaultTpTestData(String nepAlias) {
        final boolean NEP_PRESENT = testContext.getScenarioContext().isContains(Context.A4_NEP, nepAlias);

        // TP needs to be connected to a NEP, so if no NEP present, create one
        if (!NEP_PRESENT)
            givenANEPIsExistingInA4ResourceInventory(nepAlias);

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

    private NetworkServiceProfileFtthAccessDto setupDefaultNspFtthTestData(String tpAlias) {
        final boolean TP_PRESENT = testContext.getScenarioContext().isContains(Context.A4_TP, tpAlias);

        // NSP needs to be connected to a TP, so if no TP present, create one
        if (!TP_PRESENT)
            givenATPIsExistingInA4ResourceInventory(tpAlias);

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

    private NetworkServiceProfileL2BsaDto setupDefaultNspL2BsaTestData(String tpAlias) {
        final boolean TP_PRESENT = testContext.getScenarioContext().isContains(Context.A4_TP, tpAlias);

        // NSP needs to be connected to a TP, so if no TP present, create one
        if (!TP_PRESENT)
            givenATPIsExistingInA4ResourceInventory(tpAlias);

        final TerminationPointDto tp = (TerminationPointDto) testContext.getScenarioContext().getContext(Context.A4_TP, tpAlias);
        return a4ResInvMapper.getNetworkServiceProfileL2BsaDto(tp.getUuid());
    }

    private void persistNspL2Bsa(String nspAlias, NetworkServiceProfileL2BsaDto nspL2Bsa) {
        // Make sure no old test data is in the way (to avoid colliding unique constraints)
        a4ResInv.deleteNspsL2Bsa(nspL2Bsa.getLineId());

        a4ResInv.createNetworkServiceProfileL2Bsa(nspL2Bsa);

        testContext.getScenarioContext().setContext(Context.A4_NSP_L2BSA, nspAlias, nspL2Bsa);
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

    private NetworkServiceProfileA10NspDto setupDefaultNspA10NspTestData(String tpAlias) {
        final boolean TP_PRESENT = testContext.getScenarioContext().isContains(Context.A4_TP, tpAlias);

        // NSP needs to be connected to a TP, so if no TP present, create one
        if (!TP_PRESENT)
            givenATPIsExistingInA4ResourceInventory(tpAlias);

        final TerminationPointDto tp = (TerminationPointDto) testContext.getScenarioContext().getContext(Context.A4_TP, tpAlias);
        return a4ResInvMapper.getNetworkServiceProfileA10NspDto(tp.getUuid());
    }

    private void persistNspA10Nsp(String nspAlias, NetworkServiceProfileA10NspDto nspA10Nsp) {
        a4ResInv.createNetworkServiceProfileA10Nsp(nspA10Nsp);

        testContext.getScenarioContext().setContext(Context.A4_NSP_A10NSP, nspAlias, nspA10Nsp);
    }

    private void createNspA10NspWithStates(String nspAlias, String opState, String lcState, String tpAlias) {
        // ACTION
        NetworkServiceProfileA10NspDto nspA10nsp = setupDefaultNspA10NspTestData(tpAlias);
        nspA10nsp.setOperationalState(opState);
        nspA10nsp.setLifecycleState(lcState);

        persistNspA10Nsp(nspAlias, nspA10nsp);
    }

}
