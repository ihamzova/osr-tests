package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.data.osr.models.A4TerminationPoint;
import com.tsystems.tm.acc.ta.robot.osr.WgA4PreProvisioningRobot;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;

public class A4CommissioningSteps {

    private final WgA4PreProvisioningRobot wgA4PreProvisioningRobot = new WgA4PreProvisioningRobot();
    private final TestContext testContext;

    public A4CommissioningSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    // -----=====[ WHENS ]=====-----

    @When("the wg-a4-provisioning mock sends the callback")
    public void whenUPiterMockSendsTheCallback() {
        // INPUT FROM SCENARIO CONTEXT
        final A4TerminationPoint tp = (A4TerminationPoint) testContext.getScenarioContext().getContext(Context.A4_TP);

        // ACTION
        final Response response = wgA4PreProvisioningRobot.startCallBackA4AccessLineDeprovisioningWithoutChecks(tp.getUuid());

        // Add a bit of waiting time here, to give process the chance to complete
        sleepForSeconds(2);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

}
