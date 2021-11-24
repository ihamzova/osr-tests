package cucumber.stepdefinitions.team.berlinium;

import cucumber.TestContext;
import cucumber.stepdefinitions.BaseSteps;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class A4CommissioningSteps extends BaseSteps {

//    private final A4DpuCommissioningRobot a4Commissioning = new A4DpuCommissioningRobot();

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
        // TODO
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
