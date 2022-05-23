package cucumber.stepdefinitions.team.upiter;

import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class WgA4ProvisioningSteps {

    private final AccessLineRiRobot accessLineRiRobot;

    public WgA4ProvisioningSteps(AccessLineRiRobot accessLineRiRobot) {
        this.accessLineRiRobot = accessLineRiRobot;
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
