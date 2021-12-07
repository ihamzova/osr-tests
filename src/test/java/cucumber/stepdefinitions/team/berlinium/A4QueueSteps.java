package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.robot.osr.A4ResilienceRobot;
import cucumber.Context;
import cucumber.TestContext;
import cucumber.stepdefinitions.BaseSteps;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;

import java.io.IOException;

import static org.testng.Assert.fail;

public class A4QueueSteps extends BaseSteps {

    private final A4ResilienceRobot a4ResilienceRobot = new A4ResilienceRobot();
    private final String QUEUE_DEPROV_DLQ = "jms.dlq.deprovisioning";

    public A4QueueSteps(TestContext testContext) {
        super(testContext);
    }

    @Before
    public void init() {
        // Make sure that no old test data is in the way
        cleanup();
    }

    @After
    public void cleanup() {
        if (getScenarioContext().isContains(Context.A4_QUEUES))
            a4ResilienceRobot.removeAllMessagesInQueue(QUEUE_DEPROV_DLQ);
    }

    @Then("the TP UUID is added to Deprovisioning DLQ")
    public void tpUuidIsAddedToDlq() {
        try {
            getScenarioContext().setContext(Context.A4_QUEUES, true); // A4 queue is used, make context aware of that
            a4ResilienceRobot.checkMessagesInQueue(QUEUE_DEPROV_DLQ, 1);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e.getMessage());
        }
    }

}
