package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.Then;

public class WiremockNemoSteps {

    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();
    private final TestContext testContext;
    private final A4NemoUpdaterRobot a4NemoUpdaterRobot = new A4NemoUpdaterRobot();

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

    @Then("{int} {string} NE update notification(s) was/were sent to NEMO")
    public void thenANeUpdateNotificationWasSentToNemo(int count, String method) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElement ne = (A4NetworkElement) testContext.getScenarioContext().getContext(Context.A4_NE);

        // ACTION
        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(ne.getUuid(), method, count);
    }

    @Then("{int} {string} NEP update notification(s) was/were sent to NEMO")
    public void thenANepUpdateNotificationWasSentToNemo(int count, String method) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementPort nep = (A4NetworkElementPort) testContext.getScenarioContext().getContext(Context.A4_NEP);

        // ACTION
        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(nep.getUuid(), method, count);
    }

    @Then("{int} {string} NEL update notification(s) was/were sent to NEMO")
    public void thenANelUpdateNotificationWasSentToNemo(int count, String method) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementLink nel = (A4NetworkElementLink) testContext.getScenarioContext().getContext(Context.A4_NEL);

        // ACTION
        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(nel.getUuid(), method, count);
    }

    @Then("{int} {string} NSP FTTH update notification(s) was/were sent to NEMO")
    public void thenANspFtthUpdateNotificationWasSentToNemo(int count, String method) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkServiceProfileFtthAccess nspFtth = (A4NetworkServiceProfileFtthAccess) testContext.getScenarioContext().getContext(Context.A4_NSP_FTTH);

        // ACTION
        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(nspFtth.getUuid(), method, count);
    }

    @Then("{int} {string} NSP L2BSA update notification(s) was/were sent to NEMO")
    public void thenANspL2BsaUpdateNotificationWasSentToNemo(int count, String method) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkServiceProfileL2Bsa nspL2Bsa = (A4NetworkServiceProfileL2Bsa) testContext.getScenarioContext().getContext(Context.A4_NSP_L2BSA);

        // ACTION
        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(nspL2Bsa.getUuid(), method, count);
    }

    @Then("{int} {string} NSP A10NSP update notification(s) was/were sent to NEMO")
    public void thenANspA10nspUpdateNotificationWasSentToNemo(int count, String method) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkServiceProfileA10Nsp nspA10nsp = (A4NetworkServiceProfileA10Nsp) testContext
                .getScenarioContext().getContext(Context.A4_NSP_A10NSP);

        // ACTION
        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(nspA10nsp.getUuid(), method, count);
    }

    @Then("update notifications was sent to NEMO")
    public void updateNotificationsWasSentToNEMO() {
        // checks for NEG, NE, NEP
        System.out.println("+++ Start Nemo-Update-Checks !");
        A4ImportCsvData csvData = (A4ImportCsvData) testContext.getScenarioContext().getContext(Context.A4_CSV);
        a4NemoUpdaterRobot.checkAsyncNemoUpdatePutRequests(csvData);
        System.out.println("+++ Ende Nemo-Update-Checks !");
        /*
        thenANegUpdateNotificationWasSentToNemo(1, "PUT");
        thenANeUpdateNotificationWasSentToNemo(1, "PUT");
        thenANepUpdateNotificationWasSentToNemo(20, "PUT");
         */
    }
}
