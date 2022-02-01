package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkServiceProfileFtthAccess;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.Then;

public class WiremockNemoSteps {

    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();
    private final TestContext testContext;

    public WiremockNemoSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    // -----=====[ THENS ]=====-----

    @Then("{int} {string} NEG update notification(s) was/were sent to NEMO")
    public void thenANegUpdateNotificationWasSentToNemo(int count, String method) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementGroup neg = (A4NetworkElementGroup) testContext.getScenarioContext().getContext(Context.A4_NEG);

        // ACTION
        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(neg.getUuid(), method, count);
    }

    @Then("{int} {string} NSP FTTH update notification(s) was/were sent to NEMO")
    public void thenANspFtthUpdateNotificationWasSentToNemo(int count, String method) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkServiceProfileFtthAccess nspFtth = (A4NetworkServiceProfileFtthAccess) testContext.getScenarioContext().getContext(Context.A4_NSP_FTTH);

        // ACTION
        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(nspFtth.getUuid(), method, count);
    }

}
