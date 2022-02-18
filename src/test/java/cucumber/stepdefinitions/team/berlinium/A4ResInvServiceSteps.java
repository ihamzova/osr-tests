package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkServiceProfileL2Bsa;
import com.tsystems.tm.acc.ta.data.osr.models.A4TerminationPoint;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceRobot;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;

public class A4ResInvServiceSteps {

    final int SLEEP_TIMER = 5; // in seconds
    private final A4ResourceInventoryServiceRobot a4ResInvService = new A4ResourceInventoryServiceRobot();
    private final TestContext testContext;

    public A4ResInvServiceSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    // -----=====[ WHENS ]=====-----

    @When("NEMO sends a request to change/update NEG operationalState to {string}")
    public void nemoSendsARequestToChangeNEGOperationalStateTo(String ops) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementGroup neg = (A4NetworkElementGroup) testContext.getScenarioContext().getContext(Context.A4_NEG);

        // ACTION
        final Response response = a4ResInvService.sendStatusUpdateForNetworkElementGroupWithoutChecks(neg, ops);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a delete TP request( to A4 resource inventory service)")
    public void whenNemoSendsADeleteTPRequest() {
        // INPUT FROM SCENARIO CONTEXT
        final A4TerminationPoint tp = (A4TerminationPoint) testContext.getScenarioContext().getContext(Context.A4_TP);

        // ACTION
        Response response = a4ResInvService.deleteLogicalResource(tp.getUuid());

        // Add a bit of waiting time here, to give process the chance to complete (because of async callbacks etc.)
        sleepForSeconds(SLEEP_TIMER);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a create TP request with type {string}")
    public void whenNemoSendsACreateTPRequestWithType(String tpType) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementPort nep = (A4NetworkElementPort) testContext.getScenarioContext().getContext(Context.A4_NEP);

        // ACTION
        A4TerminationPoint tp = testContext.getOsrTestContext().getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointFtthAccess);
        tp.setSubType(tpType);
        Response response = a4ResInvService.createTerminationPoint(tp, nep);

        // Add a bit of waiting time here, to give process the chance to complete (because of async callbacks etc.)
        sleepForSeconds(SLEEP_TIMER);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_TP, tp);
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to change NSP L2BSA operationalState to {string}")
    public void whenNemoSendsOperationalStateUpdateForNspL2Bsa(String newOperationalState) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkServiceProfileL2Bsa nspL2 = (A4NetworkServiceProfileL2Bsa) testContext.getScenarioContext().getContext(Context.A4_NSP_L2BSA);
        final A4TerminationPoint tp = (A4TerminationPoint) testContext.getScenarioContext().getContext(Context.A4_TP);

        // ACTION
        final Response response = a4ResInvService.sendStatusUpdateForNetworkServiceProfileL2BsaWithoutChecks(nspL2, tp, newOperationalState);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

}
