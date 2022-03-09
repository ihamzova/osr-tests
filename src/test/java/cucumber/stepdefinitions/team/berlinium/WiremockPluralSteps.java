package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.data.osr.models.PluralTnpData;
import com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.PluralStub;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4InventoryImporterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class WiremockPluralSteps {

    private PluralTnpData pluralTnpData;
    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();
    private final A4InventoryImporterRobot a4InventoryImporter = new A4InventoryImporterRobot();
    private final TestContext testContext;
    private OsrTestContext osrTestContext;
    private final A4InventoryImporterRobot a4Importer = new A4InventoryImporterRobot();
    public WiremockPluralSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    // -----=====[ GIVEN ]=====-----

    // test
    @Given("the plural mock will respond HTTP code 200 when called")
    public void PluralWiremockWillRespondHTTPCode200WhenCalled() {
        // INPUT FROM SCENARIO CONTEXT
        WireMockMappingsContext wiremock = (WireMockMappingsContext) testContext.getScenarioContext().getContext(Context.WIREMOCK);
        pluralTnpData = new PluralTnpData();
       // uewegData = osrTestContext.getData().getUewegDataDataProvider().get(UewegDataCase.defaultUeweg);
       // pluralTnpData = osrTestContext.getData().getPluralTnpDataDataProvider().get(PluralTnpDataCase.defaultPluralTnp);
        pluralTnpData.setNegName("testnegname");
        pluralTnpData.setVpsz("testvpsz");
        pluralTnpData.setFachsz("testfsz");


        // ACTION
        wiremock
                //.add(new PluralStub().postPluralResponce())
                //.add(new PluralStub().postPluralResponce200(pluralTnpData))
                .add(new PluralStub().postPluralResponce200(pluralTnpData))
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



    @Given("Mock negname {string}")
    public void PluralMock(String negName) {
        // GIVEN / ARRANGE
        //A4NetworkElementGroup neg = osrTestContext.getData().getA4NetworkElementGroupDataProvider().get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        //UewegData negName = osrTestContext.getData().getA4ImportCsvDataDataProvider().get(UewegDataCase.defaultUeweg);

        // INPUT FROM SCENARIO CONTEXT
        WireMockMappingsContext wiremock = (WireMockMappingsContext) testContext.getScenarioContext().getContext(Context.WIREMOCK);

        wiremock
                .add(new PluralStub().postPluralCallbackResponce(negName))
               // .add(new PluralStub().postPlural)

                .publish();

    }

    @When("Import negname {string}")
    public void importNegname(String negName) {
        // WHEN
        a4Importer.doPluralImport(negName);
    }

    @Then("Assert negname {string}")
    public void PluralAssert(String negName) {
        // THEN
        System.out.println("+++ Assert zu NEGname: "+negName);
        //a4Inventory.checkNetworkElementLinkConnectedToNePortExists(uewegData, nep1Data.getUuid(), nep2Data.getUuid());
        //a4Inventory.getExistingNetworkElementLink(nelData.getUuid());
    }




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
