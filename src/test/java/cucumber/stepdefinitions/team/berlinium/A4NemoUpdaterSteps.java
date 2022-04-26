package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementGroupDto;
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
        final NetworkElementGroupDto neg = (NetworkElementGroupDto) testContext.getScenarioContext().getContext(Context.A4_NEG);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        a4NemoUpdater.triggerNemoUpdate(neg.getUuid());
    }

}
