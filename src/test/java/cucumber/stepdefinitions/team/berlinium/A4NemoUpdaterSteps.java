package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.When;

import java.time.OffsetDateTime;

public class A4NemoUpdaterSteps {

    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();
    private final TestContext testContext;

    public A4NemoUpdaterSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    // -----=====[ WHENS ]=====-----

    @When("an update call to NEMO for the NEG is triggered")
    public void whenTriggerAnUpdateCallToNemoForNEG() {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementGroup neg = (A4NetworkElementGroup) testContext.getScenarioContext().getContext(Context.A4_NEG);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        a4NemoUpdater.triggerNemoUpdate(neg.getUuid());
    }

}
