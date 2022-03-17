package cucumber.stepdefinitions.team.berlinium;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tsystems.tm.acc.data.osr.models.a4importcsvdata.A4ImportCsvDataCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.PluralStub;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4InventoryImporterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
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

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static org.testng.Assert.assertEquals;

public class WiremockPluralSteps {

   // private PluralTnpData pluralTnpData;
    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();
    private final A4InventoryImporterRobot a4InventoryImporter = new A4InventoryImporterRobot();
    private final A4ResourceInventoryRobot a4ResourceInventory = new A4ResourceInventoryRobot();
    private final TestContext testContext;
    private OsrTestContext osrTestContext;
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
        //pluralTnpData = new PluralTnpData();
        //pluralTnpData = testContext.getOsrTestContext().getData().getPluralTnpDataDataProvider().get(PluralTnpDataCase.defaultPluralTnp);
/*
        A4NetworkElementGroup a4NetworkElementGroup = new A4NetworkElementGroup();
        a4NetworkElementGroup.setName(csvData.getCsvLines().get(0).getNegName());
        testContext.getScenarioContext().setContext(Context.A4_NEG, a4NetworkElementGroup);


 */
        wiremock
                .add(new PluralStub().postPluralResponce201(csvData))
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
        sleepForSeconds(10);
    }

    @When("trigger auto-import request to importer")
    public void triggerAutoImportRequestToImporter() {
        //a4Importer.doPluralImport(pluralTnpData.getNegName());

        A4ImportCsvData csvData = (A4ImportCsvData) testContext.getScenarioContext().getContext(Context.A4_CSV);
        final Response response = a4Importer.doPluralImport(csvData.getCsvLines().get(0).getNegName());
        //final Response response = a4Importer.doPluralImport(pluralTnpData.getNegName());

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);

        //A4NetworkElementGroup a4negTest= (A4NetworkElementGroup) testContext.getScenarioContext().getContext(Context.A4_NEG);
        //System.out.println("+++ NEG-Test: "+a4negTest);
    }


    @And("delete neg in ri recursively")
    public void deleteNegInRiRecursively() {
        A4ImportCsvData csv = (A4ImportCsvData) testContext.getScenarioContext().getContext(Context.A4_CSV);
        a4ResourceInventory.deleteA4TestDataRecursively(csv);
    }

    @Then("positive response from importer received")
    public void positiveResponseFromImporterReceived() throws JsonProcessingException {

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
       // List<NetworkElementGroupDto> networkElementGroupDtoList = a4ResourceInventory.getNetworkElementGroupsByName(csvData.getCsvLines().get(0).getNegName());
       // assertEquals(networkElementGroupDtoList.size(), 2);

        System.out.println("+++ Start Checks NE: "+csvData);
        a4ResourceInventory.checkNetworkElementByCsvData(csvData);
        System.out.println("+++ Start Checks NEP");
        a4ResourceInventory.checkNetworkElementPortsByImportCsvData(csvData);
        System.out.println("+++ Ende Checks ");
/*
        // OUTPUT INTO SCENARIO CONTEXT
        A4NetworkElementGroup a4neg = new A4NetworkElementGroup();
        a4neg.setUuid(networkElementGroupDtoList.get(0).getUuid());
        a4neg.setName(networkElementGroupDtoList.get(0).getName());
        testContext.getScenarioContext().setContext(Context.A4_NEG, a4neg);

        List<NetworkElementDto> networkElementDtoList = a4ResourceInventory.getNetworkElementsByNegUuid(networkElementGroupDtoList.get(0).getUuid());
        assertEquals(networkElementDtoList.size(), 1);

        // OUTPUT INTO SCENARIO CONTEXT
       // testContext.getScenarioContext().setContext(Context.A4_NE, networkElementDtoList.get(0).getUuid());
        A4NetworkElement a4ne = new A4NetworkElement();
        a4ne.setUuid(networkElementDtoList.get(0).getUuid());
        testContext.getScenarioContext().setContext(Context.A4_NE, a4ne);

        List<NetworkElementPortDto> networkElementPortDtoList = a4ResourceInventory.getNetworkElementPortsByNetworkElement(networkElementDtoList.get(0).getUuid());
        assertEquals(networkElementPortDtoList.size(), 20);
*/
    }


}
