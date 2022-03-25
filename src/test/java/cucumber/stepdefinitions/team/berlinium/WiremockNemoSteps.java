package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementGroupDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementPortDto;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.Then;

import java.util.List;

import static org.testng.AssertJUnit.assertEquals;

public class WiremockNemoSteps {

    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();
    private final A4ResourceInventoryRobot a4ResInv = new A4ResourceInventoryRobot();
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

    @Then("{int} {string} update notification(s) was/were sent to NEMO for the NEG with name {string}")
    public void thenXUpdateNotificationsWereSentToNEMOForTheNEGWithName(int count, String method, String negName) {
        // ACTION
        final List<NetworkElementGroupDto> negList = a4ResInv.getNetworkElementGroupsByName(negName);
        assertEquals(1, negList.size());
        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(negList.get(0).getUuid(), method, count);
    }

    @Then("{int} {string} NE update notification(s) was/were sent to NEMO")
    public void thenANeUpdateNotificationWasSentToNemo(int count, String method) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElement ne = (A4NetworkElement) testContext.getScenarioContext().getContext(Context.A4_NE);

        // ACTION
        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(ne.getUuid(), method, count);
    }

    @Then("{int} {string} update notification(s) was/were sent to NEMO for the NE with VPSZ {string} and FSZ {string}")
    public void thenXUpdateNotificationsereSentToNEMOForTheNEWithVPSZAndFSZ(int count, String method, String vpsz, String fsz) {
        // ACTION
        final List<NetworkElementDto> neList = a4ResInv.getNetworkElementsByVpszFsz(vpsz, fsz);
        assertEquals(1, neList.size());
        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(neList.get(0).getUuid(), method, count);
    }

    @Then("{int} {string} NEP update notification(s) was/were sent to NEMO")
    public void thenANepUpdateNotificationWasSentToNemo(int count, String method) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementPort nep = (A4NetworkElementPort) testContext.getScenarioContext().getContext(Context.A4_NEP);

        // ACTION
        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(nep.getUuid(), method, count);
    }

    @Then("{int} {string} update notification(s) was/were sent to NEMO for each NEP connected to the NE with VPSZ {string} and FSZ {string}")
    public void thenXUpdateNotificationsWereSentToNEMOForEachNEPConnectedToTheNEWithVPSZAndFSZ(int countNemo, String method, String vpsz, String fsz) {
        // ACTION
        final List<NetworkElementDto> neList = a4ResInv.getNetworkElementsByVpszFsz(vpsz, fsz);
        assertEquals(1, neList.size());

        final List<NetworkElementPortDto> nepList = a4ResInv.getNetworkElementPortsByNetworkElement(neList.get(0).getUuid());
        nepList.forEach(nep ->
                a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(nep.getUuid(), method, countNemo));
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

}
