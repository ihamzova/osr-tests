package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofileftthaccess.A4NetworkServiceProfileFtthAccessCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilel2bsa.A4NetworkServiceProfileL2BsaCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkServiceProfileFtthAccessDto;
import cucumber.BaseSteps;
import cucumber.Context;
import cucumber.TestContext;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.util.UUID;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_MS;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getRandomDigits;
import static org.testng.AssertJUnit.assertEquals;

@ServiceLog({A4_RESOURCE_INVENTORY_MS})
public class A4ResInvSteps extends BaseSteps {

    private final A4ResourceInventoryRobot a4ResInv = new A4ResourceInventoryRobot();

    public A4ResInvSteps(TestContext testContext) {
        super(testContext);
    }

    @After
    public void cleanup() {
        // ATTENTION: If at any time more than 1 NEG is used for tests, the additional ones have to be added here!

        A4NetworkElementGroup neg = null;

        // INPUT FROM SCENARIO CONTEXT
        final boolean NEG_PRESENT = getScenarioContext().isContains(Context.A4_NEG);
        if (NEG_PRESENT)
            neg = (A4NetworkElementGroup) getScenarioContext().getContext(Context.A4_NEG);

        // ACTION
        if (NEG_PRESENT)
            a4ResInv.deleteA4TestDataRecursively(neg);
    }

    // -----=====[ GIVENS ]=====-----

    @Given("a NEG is existing in A4 resource inventory")
    public void givenANEGIsExistingInA4ResourceInventory() {
        // ACTION
        A4NetworkElementGroup neg = setupDefaultNegTestData();
        a4ResInv.createNetworkElementGroup(neg);

        // OUTPUT INTO SCENARIO CONTEXT
        getScenarioContext().setContext(Context.A4_NEG, neg);
    }

    @Given("no NEG exists in A4 resource inventory")
    public void givenNoNEGExistsInA4ResourceInventory() {
        // ACTION
        A4NetworkElementGroup neg = new A4NetworkElementGroup();
        neg.setUuid(UUID.randomUUID().toString());

        // OUTPUT INTO SCENARIO CONTEXT
        getScenarioContext().setContext(Context.A4_NEG, neg);
    }

    @Given("a NE is existing in A4 resource inventory")
    public void givenANeIsExistingInA4ResourceInventory() {
        // INPUT FROM SCENARIO CONTEXT
        // Has to be done after setupDefaultNeTestData() is called, because NEG might not exist yet

        // ACTION
        A4NetworkElement ne = setupDefaultNeTestData();

        final A4NetworkElementGroup neg = (A4NetworkElementGroup) getScenarioContext().getContext(Context.A4_NEG);

        a4ResInv.createNetworkElement(ne, neg);

        // OUTPUT INTO SCENARIO CONTEXT
        getScenarioContext().setContext(Context.A4_NE, ne);
    }

    @Given("a NE with VPSZ {string} and FSZ {string} is existing in A4 resource inventory")
    public void givenANeWithVpszAndFszIsExistingInA4ResourceInventory(String vpsz, String fsz) {
        // INPUT FROM SCENARIO CONTEXT
        // Has to be done after setupDefaultNeTestData() is called, because NEG might not exist yet

        // ACTION
        A4NetworkElement ne = setupDefaultNeTestData();
        ne.setVpsz(vpsz);
        ne.setFsz(fsz);

        final A4NetworkElementGroup neg = (A4NetworkElementGroup) getScenarioContext().getContext(Context.A4_NEG);

        a4ResInv.createNetworkElement(ne, neg);

        // OUTPUT INTO SCENARIO CONTEXT
        getScenarioContext().setContext(Context.A4_NE, ne);
    }

    @Given("no NE exists in A4 resource inventory")
    public void givenNoNEExistsInA4ResourceInventory() {
        // ACTION
        A4NetworkElement ne = new A4NetworkElement();
        ne.setUuid(UUID.randomUUID().toString());

        // OUTPUT INTO SCENARIO CONTEXT
        getScenarioContext().setContext(Context.A4_NE, ne);
    }

    @Given("a NEP is existing in A4 resource inventory")
    public void givenANEPIsExistingInA4ResourceInventory() {
        // INPUT FROM SCENARIO CONTEXT
        // Has to be done after setupDefaultNeTestData() is called, because NE might not exist yet

        // ACTION
        A4NetworkElementPort nep = setupDefaultNepTestData();

        final A4NetworkElement ne = (A4NetworkElement) getScenarioContext().getContext(Context.A4_NE);

        a4ResInv.createNetworkElementPort(nep, ne);

        // OUTPUT INTO SCENARIO CONTEXT
        getScenarioContext().setContext(Context.A4_NEP, nep);
    }

    @Given("a TP is existing in A4 resource inventory")
    public void givenATPIsExistingInA4ResourceInventory() {
        // INPUT FROM SCENARIO CONTEXT
        // Has to be done after setupDefaultNeTestData() is called, because NEP might not exist yet

        // ACTION
        A4TerminationPoint tp = setupDefaultTpTestData();

        final A4NetworkElementPort nep = (A4NetworkElementPort) getScenarioContext().getContext(Context.A4_NEP);

        a4ResInv.createTerminationPoint(tp, nep);

        // OUTPUT INTO SCENARIO CONTEXT
        getScenarioContext().setContext(Context.A4_TP, tp);
    }

    @Given("a TP with type {string} is existing in A4 resource inventory")
    public void givenTPWithTypeIsExistingInA4ResourceInventory(String tpType) {
        // INPUT FROM SCENARIO CONTEXT
        // Has to be done after setupDefaultNeTestData() is called, because NEP might not exist yet

        // ACTION
        A4TerminationPoint tp = setupDefaultTpTestData();
        tp.setSubType(tpType);

        final A4NetworkElementPort nep = (A4NetworkElementPort) getScenarioContext().getContext(Context.A4_NEP);

        a4ResInv.createTerminationPoint(tp, nep);

        // OUTPUT INTO SCENARIO CONTEXT
        getScenarioContext().setContext(Context.A4_TP, tp);
    }

    @Given("no TP exists in A4 resource inventory")
    public void givenNoTPExistsInA4ResourceInventory() {
        // ACTION
        A4TerminationPoint tp = new A4TerminationPoint();
        tp.setUuid(UUID.randomUUID().toString());

        // OUTPUT INTO SCENARIO CONTEXT
        getScenarioContext().setContext(Context.A4_TP, tp);
    }

    @Given("a NSP FTTH(-Access) with Line ID {string} is existing in A4 resource inventory( for the TP)")
    public void givenANSPFTTHWithLineIDIsExistingInA4ResourceInventoryForTheTP(String lineId) {
        // INPUT FROM SCENARIO CONTEXT
        // Has to be done after setupDefaultNeTestData() is called, because TP might not exist yet

        // ACTION
        A4NetworkServiceProfileFtthAccess nspFtth = setupDefaultNspFtthTestData();
        nspFtth.setLineId(lineId);

        final A4TerminationPoint tp = (A4TerminationPoint) getScenarioContext().getContext(Context.A4_TP);

        a4ResInv.createNetworkServiceProfileFtthAccess(nspFtth, tp);

        // OUTPUT INTO SCENARIO CONTEXT
        getScenarioContext().setContext(Context.A4_NSP_FTTH, nspFtth);
    }

    @Given("no NSP FTTH(-Access) exists in A4 resource inventory( for the TP)")
    public void givenNoNSPFTTHExistsInA4ResourceInventoryForTheTP() {
        // ACTION
        A4NetworkServiceProfileFtthAccess nspFtth = new A4NetworkServiceProfileFtthAccess();
        nspFtth.setUuid(UUID.randomUUID().toString());

        // OUTPUT INTO SCENARIO CONTEXT
        getScenarioContext().setContext(Context.A4_NSP_FTTH, nspFtth);
    }

    @Given("a NSP L2BSA with operationalState {string} is existing in A4 resource inventory")
    public void givenNspL2BsaWithLineIDIsExistingInA4ResourceInventoryForTheTP(String operationalState) {
        // INPUT FROM SCENARIO CONTEXT
        // Has to be done after setupDefaultNeTestData() is called, because TP might not exist yet

        // ACTION
        A4NetworkServiceProfileL2Bsa nspL2Bsa = setupDefaultNspL2BsaTestData();
        nspL2Bsa.setOperationalState(operationalState);

        final A4TerminationPoint tp = (A4TerminationPoint) getScenarioContext().getContext(Context.A4_TP);

        a4ResInv.createNetworkServiceProfileL2Bsa(nspL2Bsa, tp);

        // OUTPUT INTO SCENARIO CONTEXT
        getScenarioContext().setContext(Context.A4_NSP_L2BSA, nspL2Bsa);
    }

    @Given("a NSP L2BSA with operationalState {string} and lifecycleState {string} is existing in A4 resource inventory")
    public void givenNspL2BsaWithLineIDIsExistingInA4ResourceInventoryForTheTP(String operationalState, String lifecycleState) {
        // INPUT FROM SCENARIO CONTEXT
        // Has to be done after setupDefaultNeTestData() is called, because TP might not exist yet

        // ACTION
        A4NetworkServiceProfileL2Bsa nspL2Bsa = setupDefaultNspL2BsaTestData();
        nspL2Bsa.setOperationalState(operationalState);
        nspL2Bsa.setLifecycleState(lifecycleState);

        final A4TerminationPoint tp = (A4TerminationPoint) getScenarioContext().getContext(Context.A4_TP);

        a4ResInv.createNetworkServiceProfileL2Bsa(nspL2Bsa, tp);

        // OUTPUT INTO SCENARIO CONTEXT
        getScenarioContext().setContext(Context.A4_NSP_L2BSA, nspL2Bsa);
    }

    // -----=====[ THENS ]=====-----

    @Then("the TP does exist in A4 resource inventory")
    public void thenTheTPDoesExistInA4ResourceInventory() {
        // INPUT FROM SCENARIO CONTEXT
        final A4TerminationPoint tp = (A4TerminationPoint) getScenarioContext().getContext(Context.A4_TP);

        // ACTION
        a4ResInv.checkTerminationPointExists(tp.getUuid());
    }

    @Then("the TP does not exist in A4 resource inventory( anymore)/( any longer)")
    public void thenTheTPIsDoesNotExistInA4ResourceInventoryAnymore() {
        // INPUT FROM SCENARIO CONTEXT
        final A4TerminationPoint tp = (A4TerminationPoint) getScenarioContext().getContext(Context.A4_TP);

        // ACTION
        a4ResInv.checkTerminationPointIsDeleted(tp.getUuid());
    }

    @Then("a/the NSP FTTH connected to the TP does exist in A4 resource inventory")
    public void thenTheNspFtthConnectedToTpDoesExistInA4ResourceInventory() {
        // INPUT FROM SCENARIO CONTEXT
        final A4TerminationPoint tp = (A4TerminationPoint) getScenarioContext().getContext(Context.A4_TP);

        // ACTION
        NetworkServiceProfileFtthAccessDto nspFtthDto = a4ResInv.checkNetworkServiceProfileFtthAccessConnectedToTerminationPointExists(tp.getUuid(), 1);
        A4NetworkServiceProfileFtthAccess nspFtth = mapDtoToA4NspFtth(nspFtthDto);

        // OUTPUT INTO SCENARIO CONTEXT
        getScenarioContext().setContext(Context.A4_NSP_FTTH, nspFtth);
    }

    @Then("the NSP FTTH does not exist in A4 resource inventory( anymore)/( any longer)")
    public void thenTheNspFtthDoesNotExistInA4ResourceInventoryAnymore() {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkServiceProfileFtthAccess nspFtth = (A4NetworkServiceProfileFtthAccess) getScenarioContext().getContext(Context.A4_NSP_FTTH);

        // ACTION
        a4ResInv.checkNetworkServiceProfileFtthAccessIsDeleted(nspFtth.getUuid());
    }

    @Then("the NSP L2BSA operationalState is {string}")
    public void thenTheNSPLBSAOperationalStateIs(String operationalState) {
        A4NetworkServiceProfileL2Bsa nspL2Data = (A4NetworkServiceProfileL2Bsa) getScenarioContext().getContext(Context.A4_NSP_L2BSA);
        assertEquals(operationalState, nspL2Data.getOperationalState());
    }

    @Then("the NSP L2BSA lifecycleState is {string}")
    public void thenTheNSPLBSALifecycleStateIs(String lifecycleState) {
        A4NetworkServiceProfileL2Bsa nspL2Data = (A4NetworkServiceProfileL2Bsa) getScenarioContext().getContext(Context.A4_NSP_L2BSA);
        assertEquals(lifecycleState, nspL2Data.getLifecycleState());
    }

    // -----=====[ HELPERS ]=====-----

    private A4NetworkElementGroup setupDefaultNegTestData() {
        // ACTION
        A4NetworkElementGroup neg = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        neg.setUuid(UUID.randomUUID().toString());
        neg.setName("neg integration test name " + getRandomDigits(6));

        return neg;
    }

    private A4NetworkElement setupDefaultNeTestData() {
        // INPUT FROM SCENARIO CONTEXT
        final boolean NEG_PRESENT = getScenarioContext().isContains(Context.A4_NEG);

        // ACTION

        // NE needs to be connected to a NEG, so if no NEG present, create one
        if (!NEG_PRESENT)
            givenANEGIsExistingInA4ResourceInventory();

        A4NetworkElement ne = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        ne.setUuid(UUID.randomUUID().toString());

        return ne;
    }

    private A4NetworkElementPort setupDefaultNepTestData() {
        // INPUT FROM SCENARIO CONTEXT
        final boolean NE_PRESENT = getScenarioContext().isContains(Context.A4_NE);

        // ACTION

        // NEP needs to be connected to a NE, so if no NE present, create one
        if (!NE_PRESENT)
            givenANeIsExistingInA4ResourceInventory();

        A4NetworkElementPort nep = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
        nep.setUuid(UUID.randomUUID().toString());

        return nep;
    }

    private A4TerminationPoint setupDefaultTpTestData() {
        // INPUT FROM SCENARIO CONTEXT
        final boolean NEP_PRESENT = getScenarioContext().isContains(Context.A4_NEP);

        // ACTION

        // TP needs to be connected to a NEP, so if no NEP present, create one
        if (!NEP_PRESENT)
            givenANEPIsExistingInA4ResourceInventory();

        A4TerminationPoint tp = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.TerminationPointB);
        tp.setUuid(UUID.randomUUID().toString());

        return tp;
    }

    private A4NetworkServiceProfileFtthAccess setupDefaultNspFtthTestData() {
        // INPUT FROM SCENARIO CONTEXT
        final boolean TP_PRESENT = getScenarioContext().isContains(Context.A4_TP);

        // ACTION

        // NSP needs to be connected to a TP, so if no TP present, create one
        if (!TP_PRESENT)
            givenATPIsExistingInA4ResourceInventory();

        A4NetworkServiceProfileFtthAccess nspFtth = osrTestContext.getData()
                .getA4NetworkServiceProfileFtthAccessDataProvider()
                .get(A4NetworkServiceProfileFtthAccessCase.defaultNetworkServiceProfileFtthAccess);
        nspFtth.setUuid(UUID.randomUUID().toString());

        return nspFtth;
    }

    private A4NetworkServiceProfileL2Bsa setupDefaultNspL2BsaTestData() {
        // INPUT FROM SCENARIO CONTEXT
        final boolean TP_PRESENT = getScenarioContext().isContains(Context.A4_TP);

        // ACTION

        // NSP needs to be connected to a TP, so if no TP present, create one
        if (!TP_PRESENT)
            givenATPIsExistingInA4ResourceInventory();

        A4NetworkServiceProfileL2Bsa nspL2Bsa = osrTestContext.getData()
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
