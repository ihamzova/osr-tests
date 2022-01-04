package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofileftthaccess.A4NetworkServiceProfileFtthAccessCase;
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

@ServiceLog({A4_RESOURCE_INVENTORY_MS})
public class A4ResInvSteps extends BaseSteps {

    private final A4ResourceInventoryRobot a4ResInv = new A4ResourceInventoryRobot();

    public A4ResInvSteps(TestContext testContext) {
        super(testContext);
    }

    @After
    public void cleanup() {
        // ATTENTION: If at any time more than 1 NEG is used for tests, the additional ones have to be added here!
        if (getScenarioContext().isContains(Context.A4_NEG)) {
            A4NetworkElementGroup neg = (A4NetworkElementGroup) getScenarioContext().getContext(Context.A4_NEG);
            a4ResInv.deleteA4TestDataRecursively(neg);
        }
    }

    @Given("no NE exists in A4 resource inventory")
    public void noNEExistsInA4ResourceInventory() {
        A4NetworkElement ne = new A4NetworkElement();
        ne.setUuid(UUID.randomUUID().toString());
        getScenarioContext().setContext(Context.A4_NE, ne);
    }

    @Given("no TP exists in A4 resource inventory")
    public void noTPExistsInA4ResourceInventory() {
        A4TerminationPoint tp = new A4TerminationPoint();
        tp.setUuid(UUID.randomUUID().toString());
        getScenarioContext().setContext(Context.A4_TP, tp);
    }

    @Given("a TP is existing in A4 resource inventory")
    public void aTPIsExistingInA4ResourceInventory() {
        A4TerminationPoint tp = setupDefaultTpTestData();
        tp.setUuid(UUID.randomUUID().toString());
        A4NetworkElementPort nep = (A4NetworkElementPort) getScenarioContext().getContext(Context.A4_NEP);
        getScenarioContext().setContext(Context.A4_TP, tp);
        a4ResInv.createTerminationPoint(tp, nep);
    }

    @Given("a NEP is existing in A4 resource inventory")
    public void aNEPIsExistingInA4ResourceInventory() {
        // NEP needs to be connected to a NE, so if no NE present, create one
        if (!getScenarioContext().isContains(Context.A4_NE))
            aNEIsExistingInA4ResourceInventory();

        A4NetworkElementPort nep = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
        nep.setUuid(UUID.randomUUID().toString());
        getScenarioContext().setContext(Context.A4_NEP, nep);
        A4NetworkElement ne = (A4NetworkElement) getScenarioContext().getContext(Context.A4_NE);
        a4ResInv.createNetworkElementPort(nep, ne);
    }

    @Given("a NE is existing in A4 resource inventory")
    public void aNEIsExistingInA4ResourceInventory() {
        A4NetworkElement ne = setupDefaultNeTestData();
        getScenarioContext().setContext(Context.A4_NE, ne);
        A4NetworkElementGroup neg = (A4NetworkElementGroup) getScenarioContext().getContext(Context.A4_NEG);
        a4ResInv.createNetworkElement(ne, neg);
    }

    @Given("a NE with VPSZ {string} and FSZ {string} is existing in A4 resource inventory")
    public void aNEWithVPSZIsExistingInAResourceInventory(String vpsz, String fsz) {
        A4NetworkElement ne = setupDefaultNeTestData();
        ne.setVpsz(vpsz);
        ne.setFsz(fsz);
        getScenarioContext().setContext(Context.A4_NE, ne);
        A4NetworkElementGroup neg = (A4NetworkElementGroup) getScenarioContext().getContext(Context.A4_NEG);
        a4ResInv.createNetworkElement(ne, neg);
    }

    @Given("a NEG is existing in A4 resource inventory")
    public void aNEGIsExistingInA4ResourceInventory() {
        A4NetworkElementGroup neg = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        neg.setUuid(UUID.randomUUID().toString());
        getScenarioContext().setContext(Context.A4_NEG, neg);
        a4ResInv.createNetworkElementGroup(neg);
    }

    @Given("a NSP FTTH(-Access) with Line ID {string} is existing in A4 resource inventory( for the TP)")
    public void aNSPFTTHWithLineIDIsExistingInA4ResourceInventoryForTheTP(String lineId) {
        // NSP needs to be connected to a TP, so if no TP present, create one
        if (!getScenarioContext().isContains(Context.A4_TP))
            aTPIsExistingInA4ResourceInventory();

        A4NetworkServiceProfileFtthAccess nspFtth = osrTestContext.getData()
                .getA4NetworkServiceProfileFtthAccessDataProvider()
                .get(A4NetworkServiceProfileFtthAccessCase.defaultNetworkServiceProfileFtthAccess);
        nspFtth.setUuid(UUID.randomUUID().toString());
        nspFtth.setLineId(lineId);
        getScenarioContext().setContext(Context.A4_NSP_FTTH, nspFtth);
        A4TerminationPoint tp = (A4TerminationPoint) getScenarioContext().getContext(Context.A4_TP);
        a4ResInv.createNetworkServiceProfileFtthAccess(nspFtth, tp);
    }

    @Given("no NSP FTTH(-Access) exists in A4 resource inventory( for the TP)")
    public void noNSPFTTHExistsInA4ResourceInventoryForTheTP() {
        A4NetworkServiceProfileFtthAccess nspFtth = new A4NetworkServiceProfileFtthAccess();
        nspFtth.setUuid(UUID.randomUUID().toString());
        getScenarioContext().setContext(Context.A4_NSP_FTTH, nspFtth);
    }

    @Given("a TP with type {string} is existing in A4 resource inventory")
    public void aTPWithTypeIsExistingInA4ResourceInventory(String tpType) {
        A4TerminationPoint tp = setupDefaultTpTestData();
        tp.setSubType(tpType);
        getScenarioContext().setContext(Context.A4_TP, tp);
        A4NetworkElementPort nep = (A4NetworkElementPort) getScenarioContext().getContext(Context.A4_NEP);
        a4ResInv.createTerminationPoint(tp, nep);
    }

    @Then("the TP does not exist in A4 resource inventory( anymore)/( any longer)")
    public void theTPIsNotExistingInA4ResourceInventoryAnymore() {
        final A4TerminationPoint tp = (A4TerminationPoint) getScenarioContext().getContext(Context.A4_TP);
        a4ResInv.checkTerminationPointIsDeleted(tp.getUuid());
    }

    @Then("the NSP FTTH does not exist in A4 resource inventory( anymore)/( any longer)")
    public void theNspFtthIsNotExistingInA4ResourceInventoryAnymore() {
        final A4NetworkServiceProfileFtthAccess nspFtth = (A4NetworkServiceProfileFtthAccess) getScenarioContext().getContext(Context.A4_NSP_FTTH);
        a4ResInv.checkNetworkServiceProfileFtthAccessIsDeleted(nspFtth.getUuid());
    }

    @Then("the TP does exist in A4 resource inventory")
    public void theTPDoesExistInA4ResourceInventory() {
        final A4TerminationPoint tp = (A4TerminationPoint) getScenarioContext().getContext(Context.A4_TP);
        a4ResInv.checkTerminationPointExists(tp.getUuid());
    }

    @Then("a/the NSP FTTH (connected to the TP )does exist in A4 resource inventory")
    public void theNspFtthDoesExistInA4ResourceInventory() {
        final A4TerminationPoint tp = (A4TerminationPoint) getScenarioContext().getContext(Context.A4_TP);
        NetworkServiceProfileFtthAccessDto nspFtthDto = a4ResInv.checkNetworkServiceProfileFtthAccessConnectedToTerminationPointExists(tp.getUuid(), 1);

        A4NetworkServiceProfileFtthAccess nspFtth = new A4NetworkServiceProfileFtthAccess();
        nspFtth.setUuid(nspFtthDto.getUuid());
        nspFtth.setLineId(nspFtthDto.getLineId());
        nspFtth.setLifecycleState(nspFtthDto.getLifecycleState());
        nspFtth.setOperationalState(nspFtthDto.getOperationalState());
        nspFtth.setOntSerialNumber(nspFtthDto.getOntSerialNumber());
        nspFtth.setOltPortOntLastRegisteredOn(nspFtthDto.getOltPortOntLastRegisteredOn());
        nspFtth.setTerminationPointUuid(nspFtthDto.getTerminationPointFtthAccessUuid());

        getScenarioContext().setContext(Context.A4_NSP_FTTH, nspFtth);
    }

    private A4NetworkElement setupDefaultNeTestData() {
        // NE needs to be connected to a NEG, so if no NEG present, create one
        if (!getScenarioContext().isContains(Context.A4_NEG))
            aNEGIsExistingInA4ResourceInventory();

        A4NetworkElement ne = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        ne.setUuid(UUID.randomUUID().toString());

        return ne;
    }

    private A4TerminationPoint setupDefaultTpTestData() {
        // TP needs to be connected to a NEP, so if no NEP present, create one
        if (!getScenarioContext().isContains(Context.A4_NEP))
            aNEPIsExistingInA4ResourceInventory();

        A4TerminationPoint tp = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.TerminationPointB);
        tp.setUuid(UUID.randomUUID().toString());

        return tp;
    }

}
