package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.data.osr.models.A4TerminationPoint;
import com.tsystems.tm.acc.ta.robot.osr.WgA4PreProvisioningRobot;
import cucumber.Context;
import cucumber.TestContext;
import cucumber.stepdefinitions.BaseSteps;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;

public class A4CommissioningSteps extends BaseSteps {

    private final WgA4PreProvisioningRobot wgA4PreProvisioningRobot = new WgA4PreProvisioningRobot();

    public A4CommissioningSteps(TestContext testContext) {
        super(testContext);
    }

    @When("the U-Piter DPU mock sends the callback")
    public void uPiterSendsTheCallback() {
        A4TerminationPoint tp = (A4TerminationPoint) getScenarioContext().getContext(Context.A4_TP);
        final Response response = wgA4PreProvisioningRobot.startCallBackA4AccessLineDeprovisioningWithoutResponse(tp.getUuid());
        getScenarioContext().setContext(Context.RESPONSE, response);

        // Add a bit of waiting time here, to give process the chance to complete
        sleepForSeconds(2);
    }

    @Then("the deprovisioning request to U-Piter is repeated after {int} minutes")
    public void wait(int min) {
        sleepForSeconds(min * 60);
    }

}
