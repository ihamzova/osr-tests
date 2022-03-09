package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkServiceProfileFtthAccess;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkServiceProfileL2Bsa;
import com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.PluralStub;
import com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.PreProvisioningStub;
import com.tsystems.tm.acc.ta.robot.osr.A4InventoryImporterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class WiremockPluralSteps {

    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();
    private final A4InventoryImporterRobot a4InventoryImporter = new A4InventoryImporterRobot();
    private final TestContext testContext;

    public WiremockPluralSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    // -----=====[ GIVEN ]=====-----

    // test
    @Given("the plural mock will respond HTTP code 200 when called")
    public void PluralWiremockWillRespondHTTPCode200WhenCalled() {
        // INPUT FROM SCENARIO CONTEXT
        WireMockMappingsContext wiremock = (WireMockMappingsContext) testContext.getScenarioContext().getContext(Context.WIREMOCK);

        // ACTION
        wiremock
                .add(new PluralStub().postPluralResponce())
                .publish();
    }


    @Given("the program say hello")
    public void sayHello() {
        // ACTION
        System.out.println("+++ Hello!");
    }

    @And("response from plural for {string} was received")
    public void responseFromPluralForWasReceived(String arg0) {
        // json an inventory-importer übergeben
        System.out.println("+++ Hi, I need a json-file!");
        //a4InventoryImporter.
    }


// response from plural for NEG-name was received





    // -----=====[ THENS ]=====-----
/*
    @Then("{int} {string} NEG update notification(s) was/were sent to NEMO")
    public void thenANegUpdateNotificationWasSentToNemo(int count, String method) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementGroup neg = (A4NetworkElementGroup) testContext.getScenarioContext().getContext(Context.A4_NEG);

        // ACTION
        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(neg.getUuid(), method, count);
    }

    @Then("{int} {string} NEP update notification(s) was/were sent to NEMO")
    public void thenANepUpdateNotificationWasSentToNemo(int count, String method) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementPort nep = (A4NetworkElementPort) testContext.getScenarioContext().getContext(Context.A4_NEP);

        // ACTION
        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(nep.getUuid(), method, count);
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


 */
}
