package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.data.osr.models.A4TerminationPoint;
import com.tsystems.tm.acc.ta.robot.osr.WgA4PreProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.a4provisioning.WgA4Provisioning;
import cucumber.Context;
import cucumber.TestContext;
import cucumber.stepdefinitions.BaseSteps;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class A4CommissioningSteps extends BaseSteps {

//    private final A4DpuCommissioningRobot a4Commissioning = new A4DpuCommissioningRobot();

   private final WgA4PreProvisioningRobot wgA4PreProvisioningRobot = new WgA4PreProvisioningRobot();

    public A4CommissioningSteps(TestContext testContext) {
        super(testContext);
    }

    @Before
    public void init() {
    }

    @After
    public void cleanup() {
    }

    @When("U-Piter sends the callack")
    public void uPiterSendsTheCallack() {
        A4TerminationPoint tp = (A4TerminationPoint) getScenarioContext().getContext(Context.A4_TP);
        getScenarioContext().setContext(Context.RESPONSE, wgA4PreProvisioningRobot.startCallBackA4AccessLineDeprovisioningWithoutResponse(tp.getUuid()));
    }

    @Then("wait {int}min")
    public void wait(int min) {
        long milliseconds = min* 60000L;
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
