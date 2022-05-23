package cucumber.stepdefinitions.team.berlinium.a4;

import com.tsystems.tm.acc.ta.robot.osr.A4ResilienceRobot;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;

public class QueueSteps {

    private final String QUEUE_DEPROV_DLQ = "jms.dlq.deprovisioning";
    private final String QUEUE_RES_ORDER = "jms.queue.roo";

    private final A4ResilienceRobot a4ResilienceRobot;

    public QueueSteps(A4ResilienceRobot a4ResilienceRobot) {
        this.a4ResilienceRobot = a4ResilienceRobot;
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
        a4ResilienceRobot.removeAllMessagesInQueue(QUEUE_RES_ORDER);
    }

    // -----=====[ THENS ]=====-----

    @Then("the TP UUID is added to A4 deprovisioning DLQ")
    public void thenTheTpUuidIsAddedToA4DeprovisioningDlq() {
        // ACTION
        a4ResilienceRobot.checkMessagesInQueue(QUEUE_DEPROV_DLQ, 1);
    }

    @Then("the RO is not added to A4 resource order queue")
    public void thenTheROIsNotAddedToQueue() {
        a4ResilienceRobot.checkMessagesInQueue(QUEUE_RES_ORDER, 0);
    }

}
