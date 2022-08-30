package cucumber.stepdefinitions.team.berlinium.a4.wiremock;

import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.*;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.Then;

import java.util.List;

import static org.testng.AssertJUnit.assertEquals;

public class NemoSteps {

    private final A4NemoUpdaterRobot a4NemoUpdater;
    private final A4ResourceInventoryRobot a4ResInv;
    private final TestContext testContext;

    public NemoSteps(TestContext testContext,
                     A4NemoUpdaterRobot a4NemoUpdater,
                     A4ResourceInventoryRobot a4ResInv) {
        this.testContext = testContext;
        this.a4NemoUpdater = a4NemoUpdater;
        this.a4ResInv = a4ResInv;
    }


    // -----=====[ THENS ]=====-----

    @Then("{int} {string} NEG update notification(s) was/were sent to NEMO")
    public void thenANegUpdateNotificationWasSentToNemo(int count, String method) {
        final NetworkElementGroupDto neg = (NetworkElementGroupDto) testContext.getScenarioContext().getContext(Context.A4_NEG);

        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(neg.getUuid(), method, count);
    }

    @Then("{int} {string} update notification(s) was/were sent to NEMO for the NEG with name {string}")
    public void thenXUpdateNotificationsWereSentToNEMOForTheNEGWithName(int count, String method, String negName) {
        final List<NetworkElementGroupDto> negList = a4ResInv.getNetworkElementGroupsByName(negName);

        assertEquals(1, negList.size());
        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(negList.get(0).getUuid(), method, count);
    }

    @Then("{int} {string} NE update notification(s) was/were sent to NEMO")
    public void thenANeUpdateNotificationWasSentToNemo(int count, String method) {
        final NetworkElementDto ne = (NetworkElementDto) testContext.getScenarioContext().getContext(Context.A4_NE);

        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(ne.getUuid(), method, count);
    }

    @Then("{int} {string} update notification(s) was/were sent to NEMO for the NE with VPSZ {string} and FSZ {string}")
    public void thenXUpdateNotificationsereSentToNEMOForTheNEWithVPSZAndFSZ(int count, String method, String vpsz, String fsz) {
        final List<NetworkElementDto> neList = a4ResInv.getNetworkElementsByVpszFsz(vpsz, fsz);

        assertEquals(1, neList.size());
        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(neList.get(0).getUuid(), method, count);
    }

    @Then("{int} {string} NEP update notification(s) was/were sent to NEMO")
    public void thenANepUpdateNotificationWasSentToNemo(int count, String method) {
        final NetworkElementPortDto nep = (NetworkElementPortDto) testContext.getScenarioContext().getContext(Context.A4_NEP);

        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(nep.getUuid(), method, count);
    }

    @Then("{int} {string} update notification(s) was/were sent to NEMO for each NEP connected to the NE with VPSZ {string} and FSZ {string}")
    public void thenXUpdateNotificationsWereSentToNEMOForEachNEPConnectedToTheNEWithVPSZAndFSZ(int countNemo, String method, String vpsz, String fsz) {
        final List<NetworkElementDto> neList = a4ResInv.getNetworkElementsByVpszFsz(vpsz, fsz);
        final List<NetworkElementPortDto> nepList = a4ResInv.getNetworkElementPortsByNetworkElement(neList.get(0).getUuid());

        assertEquals(1, neList.size());
        nepList.forEach(nep ->
                a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(nep.getUuid(), method, countNemo));
    }

    @Then("{int} {string} NEL update notification(s) was/were sent to NEMO")
    public void thenANelUpdateNotificationWasSentToNemo(int count, String method) {
        final NetworkElementLinkDto nel = (NetworkElementLinkDto) testContext.getScenarioContext().getContext(Context.A4_NEL);

        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(nel.getUuid(), method, count);
    }

    @Then("{int} {string} NSP FTTH update notification(s) was/were sent to NEMO")
    public void thenANspFtthUpdateNotificationWasSentToNemo(int count, String method) {
        final NetworkServiceProfileFtthAccessDto nspFtth = (NetworkServiceProfileFtthAccessDto) testContext.getScenarioContext().getContext(Context.A4_NSP_FTTH);

        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(nspFtth.getUuid(), method, count);
    }

    @Then("{int} {string} NSP L2BSA update notification(s) was/were sent to NEMO")
    public void thenANspL2BsaUpdateNotificationWasSentToNemo(int count, String method) {
        final NetworkServiceProfileL2BsaDto nspL2Bsa = (NetworkServiceProfileL2BsaDto) testContext.getScenarioContext().getContext(Context.A4_NSP_L2BSA);

        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(nspL2Bsa.getUuid(), method, count);
    }

    @Then("{int} {string} NSP A10NSP update notification(s) was/were sent to NEMO")
    public void thenANspA10nspUpdateNotificationWasSentToNemo(int count, String method) {
        final NetworkServiceProfileA10NspDto nspA10nsp = (NetworkServiceProfileA10NspDto) testContext
                .getScenarioContext().getContext(Context.A4_NSP_A10NSP);

        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(nspA10nsp.getUuid(), method, count);
    }

    @Then("{int} {string} NSP A10NSP {string} update notification(s) was/were sent to NEMO")
    public void thenANspA10nspUpdateNotificationWasSentToNemo(int count, String method, String nspAlias) {
        final NetworkServiceProfileA10NspDto nspA10nsp = (NetworkServiceProfileA10NspDto) testContext
                .getScenarioContext().getContext(Context.A4_NSP_A10NSP, nspAlias);

        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(nspA10nsp.getUuid(), method, count);
    }

}
