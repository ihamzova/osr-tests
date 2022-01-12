package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import cucumber.BaseSteps;
import cucumber.Context;
import cucumber.TestContext;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.cucumber.java.en.When;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_NEMO_UPDATER_MS;

@ServiceLog({A4_NEMO_UPDATER_MS})
public class A4NemoUpdaterSteps extends BaseSteps {

    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();

    public A4NemoUpdaterSteps(TestContext testContext) {
        super(testContext);
    }

    // -----=====[ WHENS ]=====-----

    @When("an update call to NEMO for the NEG is triggered")
    public void whenTriggerAnUpdateCallToNemoForNEG() {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementGroup neg = (A4NetworkElementGroup) getScenarioContext().getContext(Context.A4_NEG);

        // ACTION
        a4NemoUpdater.triggerNemoUpdate(neg.getUuid());
    }

}
