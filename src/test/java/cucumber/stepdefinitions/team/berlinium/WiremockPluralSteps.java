package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4importcsvdata.A4ImportCsvDataCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.PluralStub;
import com.tsystems.tm.acc.ta.robot.osr.A4PluralImporterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import static org.testng.Assert.assertEquals;

public class WiremockPluralSteps {

    private final A4ResourceInventoryRobot a4ResourceInventory = new A4ResourceInventoryRobot();
    private final TestContext testContext;
    private final A4PluralImporterRobot a4Importer = new A4PluralImporterRobot();
    public WiremockPluralSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    // -----=====[ GIVEN ]=====-----

    @Given("the plural mock will respond HTTP code 201 when called")
    public void PluralWiremockWillRespondHTTPCode201WhenCalled() {

        WireMockMappingsContext wiremock = (WireMockMappingsContext) testContext.getScenarioContext().getContext(Context.WIREMOCK);
        A4ImportCsvData csvData = testContext.getOsrTestContext().getData().getA4ImportCsvDataDataProvider().get(A4ImportCsvDataCase.defaultCsvFile);
        testContext.getScenarioContext().setContext(Context.A4_CSV, csvData);

        wiremock
                .add(new PluralStub().postPluralResponce201(csvData))
                .publish();
    }

    @When("trigger auto-import request to importer")
    public void triggerAutoImportRequestToImporter() {
        A4ImportCsvData csvData = (A4ImportCsvData) testContext.getScenarioContext().getContext(Context.A4_CSV);
        final Response response = a4Importer.doPluralImport(csvData.getCsvLines().get(0).getNegName());

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @And("delete neg in ri recursively")
    public void deleteNegInRiRecursively() {
        A4ImportCsvData csv = (A4ImportCsvData) testContext.getScenarioContext().getContext(Context.A4_CSV);
        a4ResourceInventory.deleteA4TestDataRecursively(csv);
    }

    @Then("positive response from importer received")
    public void positiveResponseFromImporterReceived() {

        Response foundresponse = (Response) testContext.getScenarioContext().getContext(Context.RESPONSE);
        System.out.println("+++ foundresponse: "+foundresponse.getBody().prettyPrint());

        String str = "["+foundresponse.getBody().prettyPrint()+"]";
        JSONArray array = new JSONArray(str);
        for(int i=0; i < array.length(); i++)
        {
            JSONObject object = array.getJSONObject(i);
            System.out.println("+++ "+object.getString("numberCreatedNetworkElementPorts"));
            assertEquals(object.getString("numberCreatedNetworkElementPorts"), "76");
            System.out.println("+++ "+object.getString("numberNemoUpdateTasks"));
            assertEquals(object.getString("numberNemoUpdateTasks"), "79");
            System.out.println("+++ "+object.getString("numberCreatedNetworkElements"));
            assertEquals(object.getString("numberCreatedNetworkElements"), "2");
        }

    }

    @Then("ri was created with neg and ne and neps")
    public void riWasCreatedWithNegAndNeAndNeps() {
        A4ImportCsvData csvData = (A4ImportCsvData) testContext.getScenarioContext().getContext(Context.A4_CSV);
        System.out.println("+++ Start Checks NE: "+csvData);
        a4ResourceInventory.checkNetworkElementByCsvData(csvData);
        System.out.println("+++ Start Checks NEP");
        a4ResourceInventory.checkNetworkElementPortsByImportCsvData(csvData);
        System.out.println("+++ Ende Checks ");
    }

}
