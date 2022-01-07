package cucumber.stepdefinitions.team.upiter;

import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import cucumber.BaseSteps;
import cucumber.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class WgA4ProvisioningSteps extends BaseSteps {

    private final AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();

    public WgA4ProvisioningSteps(TestContext testContext) {
        super(testContext);
    }

    @Before
    public void setup() {
        // ACTION
        cleanup(); // Make sure no old test data is in the way
    }

    @After
    public void cleanup() {
        // ACTION
        accessLineRiRobot.clearDatabase();
    }

}
