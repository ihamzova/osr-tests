package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofileftthaccess.A4NetworkServiceProfileFtthAccessCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import cucumber.Context;
import cucumber.TestContext;
import cucumber.stepdefinitions.BaseSteps;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

import java.util.UUID;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_MS;
import static org.testng.Assert.assertEquals;

@ServiceLog({A4_RESOURCE_INVENTORY_MS})
public class A4RevInvSteps extends BaseSteps {

    private final A4ResourceInventoryRobot a4ResInv = new A4ResourceInventoryRobot();

    public A4RevInvSteps(TestContext testContext) {
        super(testContext);
    }

    @Before
    public void init() {
        // Make sure that no old test data is in the way
        cleanup();
    }

    @After
    public void cleanup() {
        // ATTENTION: If at any time more than 1 NEG is used for tests, it has to be added here!!
        if (getScenarioContext().isContains(Context.A4_NEG)) {
            A4NetworkElementGroup neg = (A4NetworkElementGroup) getScenarioContext().getContext(Context.A4_NEG);
            a4ResInv.deleteA4TestDataRecursively(neg);
        }
    }

    @Given("no TP exists in A4 resource inventory")
    public void noTPExistsInA4ResourceInventory() {
        A4TerminationPoint tpData = new A4TerminationPoint();
        tpData.setUuid(UUID.randomUUID().toString());
        getScenarioContext().setContext(Context.A4_TP, tpData);
    }

    @Given("a TP is existing in A4 resource inventory")
    public void aTPIsExistingInA4ResourceInventory() {
        A4TerminationPoint tpData = setupDefaultTpTestData();
        A4NetworkElementPort nep = (A4NetworkElementPort) getScenarioContext().getContext(Context.A4_NEP);
        getScenarioContext().setContext(Context.A4_TP, tpData);
        a4ResInv.createTerminationPoint(tpData, nep);
    }

    @Given("a NEP is existing in A4 resource inventory")
    public void aNEPIsExistingInA4ResourceInventory() {
        // NEP needs to be connected to a NE, so if no NE present, create one
        if(!getScenarioContext().isContains(Context.A4_NE))
            aNEIsExistingInA4ResourceInventory();

        A4NetworkElementPort nepData = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
        getScenarioContext().setContext(Context.A4_NEP, nepData);
        A4NetworkElement ne = (A4NetworkElement) getScenarioContext().getContext(Context.A4_NE);
        a4ResInv.createNetworkElementPort(nepData, ne);
    }

    @Given("a NE is existing in A4 resource inventory")
    public void aNEIsExistingInA4ResourceInventory() {
        // NE needs to be connected to a NEG, so if no NEG present, create one
        if (!getScenarioContext().isContains(Context.A4_NEG))
            aNEGIsExistingInA4ResourceInventory();

        A4NetworkElement neData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        getScenarioContext().setContext(Context.A4_NE, neData);
        A4NetworkElementGroup neg = (A4NetworkElementGroup) getScenarioContext().getContext(Context.A4_NEG);
        a4ResInv.createNetworkElement(neData, neg);
    }

    @Given("a NEG is existing in A4 resource inventory")
    public void aNEGIsExistingInA4ResourceInventory() {
        A4NetworkElementGroup negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        getScenarioContext().setContext(Context.A4_NEG, negData);
        a4ResInv.createNetworkElementGroup(negData);
    }

    @Given("a NSP FTTH(-Access) with Line ID {string} is existing in A4 resource inventory( for the TP)")
    public void aNSPFTTHWithLineIDIsExistingInA4ResourceInventoryForTheTP(String lineId) {
        // NSP needs to be connected to a TP, so if no TP present, create one
        if (!getScenarioContext().isContains(Context.A4_TP))
            aTPIsExistingInA4ResourceInventory();

        A4NetworkServiceProfileFtthAccess nspFtthData = osrTestContext.getData()
                .getA4NetworkServiceProfileFtthAccessDataProvider()
                .get(A4NetworkServiceProfileFtthAccessCase.defaultNetworkServiceProfileFtthAccess);
        nspFtthData.setLineId(lineId);
        getScenarioContext().setContext(Context.A4_NSP_FTTH, nspFtthData);
        A4TerminationPoint tp = (A4TerminationPoint) getScenarioContext().getContext(Context.A4_TP);
        a4ResInv.createNetworkServiceProfileFtthAccess(nspFtthData, tp);
    }

    @Given("no NSP FTTH(-Access) exists in A4 resource inventory( for the TP)")
    public void noNSPFTTHExistsInA4ResourceInventoryForTheTP() {
        A4NetworkServiceProfileFtthAccess nspFtthData = new A4NetworkServiceProfileFtthAccess();
        nspFtthData.setUuid(UUID.randomUUID().toString());
        getScenarioContext().setContext(Context.A4_NSP_FTTH, nspFtthData);
    }

    @Given("a TP with type {string} is existing in A4 resource inventory")
    public void aTPWithTypeIsExistingInA4ResourceInventory(String tpType) {
        A4TerminationPoint tpData = setupDefaultTpTestData();
        tpData.setSubType(tpType);
        getScenarioContext().setContext(Context.A4_TP, tpData);
        A4NetworkElementPort nep = (A4NetworkElementPort) getScenarioContext().getContext(Context.A4_NEP);
        a4ResInv.createTerminationPoint(tpData, nep);
    }

    @Then("the TP does not exist in A4 resource inventory( anymore)/( any longer)")
    public void theTPIsNotExistingInA4ResourceInventoryAnymore() {
        final A4TerminationPoint tp = (A4TerminationPoint) getScenarioContext().getContext(Context.A4_TP);
        a4ResInv.checkTerminationPointIsDeleted(tp.getUuid());
    }

    @Then("the request is responded/answered with HTTP( error) code {int}")
    public void theRequestIsRespondedWithHTTPCode(int httpCode) {
        Response response = (Response) getScenarioContext().getContext(Context.RESPONSE);
        assertEquals(response.getStatusCode(), httpCode);
    }

    private A4TerminationPoint setupDefaultTpTestData() {
        // TP needs to be connected to a NEP, so if no NEP present, create one
        if (!getScenarioContext().isContains(Context.A4_NEP))
            aNEPIsExistingInA4ResourceInventory();

        return osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.TerminationPointB);
    }

}
