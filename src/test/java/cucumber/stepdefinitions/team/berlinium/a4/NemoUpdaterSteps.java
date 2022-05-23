package cucumber.stepdefinitions.team.berlinium.a4;

import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementGroupDto;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.When;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;

public class NemoUpdaterSteps {

    private static final int SLEEP_TIME = 2; // in seconds

    private final A4NemoUpdaterRobot a4NemoUpdater;
    private final TestContext testContext;

    public NemoUpdaterSteps(TestContext testContext,
                            A4NemoUpdaterRobot a4NemoUpdater) {
        this.testContext = testContext;
        this.a4NemoUpdater = a4NemoUpdater;
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

    @When("an async(h)(cron)(ous) update call to NEMO for the NEG is triggered")
    public void whenAnAsyncUpdateCallToNEMOForTheNEGIsTriggered() {
        final NetworkElementGroupDto neg = (NetworkElementGroupDto) testContext.getScenarioContext().getContext(Context.A4_NEG);
        List<String> uuidList = new ArrayList<>();
        uuidList.add(neg.getUuid());

        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());
        a4NemoUpdater.triggerAsyncNemoUpdate(uuidList);

        // Give async process enough time to finish
        sleepForSeconds(SLEEP_TIME);
    }

}
