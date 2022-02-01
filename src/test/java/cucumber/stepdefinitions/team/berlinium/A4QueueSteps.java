package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.robot.osr.A4ResilienceRobot;
import cucumber.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;

public class A4QueueSteps {

    private final A4ResilienceRobot a4ResilienceRobot = new A4ResilienceRobot();
    private final String QUEUE_DEPROV_DLQ = "jms.dlq.deprovisioning";
    private TestContext testContext;

    public A4QueueSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    @Before
    public void setup() {
        // ACTION

        // Make sure no old test data is in the way
        cleanup();
    }

    @After
    public void cleanup() {
        // ACTION
        a4ResilienceRobot.removeAllMessagesInQueue(QUEUE_DEPROV_DLQ);
    }

    // -----=====[ THENS ]=====-----

    @Then("the TP UUID is added to A4 deprovisioning DLQ")
    public void thenTheTpUuidIsAddedToA4DeprovisioningDlq() {
        // ACTION
        a4ResilienceRobot.checkMessagesInQueue(QUEUE_DEPROV_DLQ, 1);
    }

}
