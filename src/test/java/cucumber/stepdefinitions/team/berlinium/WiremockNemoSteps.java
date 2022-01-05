package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkServiceProfileFtthAccess;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import cucumber.BaseSteps;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.Then;

public class WiremockNemoSteps extends BaseSteps {

    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();

    public WiremockNemoSteps(TestContext testContext) {
        super(testContext);
    }

    // -----=====[ THENS ]=====-----

    @Then("{int} {string} NSP FTTH update notification(s) was/were sent to NEMO")
    public void thenANspFtthUpdateNotificationWasSentToNemo(int count, String method) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkServiceProfileFtthAccess nspFtth = (A4NetworkServiceProfileFtthAccess) getScenarioContext().getContext(Context.A4_NSP_FTTH);

        // ACTION
        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(nspFtth.getUuid(), method, count);
    }

}
