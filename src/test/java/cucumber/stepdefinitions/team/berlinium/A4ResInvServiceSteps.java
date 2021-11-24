package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.data.osr.models.A4TerminationPoint;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceRobot;
import cucumber.Context;
import cucumber.TestContext;
import cucumber.stepdefinitions.BaseSteps;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_SERVICE_MS;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;

@ServiceLog({A4_RESOURCE_INVENTORY_SERVICE_MS})
public class A4ResInvServiceSteps extends BaseSteps {

    private final A4ResourceInventoryServiceRobot a4ResInvService = new A4ResourceInventoryServiceRobot();

    public A4ResInvServiceSteps(TestContext testContext) {
        super(testContext);
    }

    @Before
    public void init() {
    }

    @After
    public void cleanup() {
    }

    @When("NEMO sends a delete TP request( to A4 resource inventory service)")
    public void nemoSendsADeleteTPRequest() {
        A4TerminationPoint tp = (A4TerminationPoint) getScenarioContext().getContext(Context.A4_TP);
        Response response = a4ResInvService.deleteLogicalResource(tp.getUuid());
        getScenarioContext().setContext(Context.RESPONSE, response);

        // Add a bit of waiting time here, to give process the chance to complete (because of async callbacks etc.)
        sleepForSeconds(2);
    }


}
