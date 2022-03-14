package cucumber.stepdefinitions.team.berlinium;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tsystems.tm.acc.data.osr.models.pluraltnpdata.PluralTnpDataCase;
import com.tsystems.tm.acc.ta.data.osr.models.PluralTnpData;
import com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.PluralStub;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4InventoryImporterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4PluralImporterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementGroupDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementPortDto;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static org.testng.Assert.assertEquals;

public class WiremockPluralSteps {

    private PluralTnpData pluralTnpData;
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
        pluralTnpData = new PluralTnpData();
        pluralTnpData = testContext.getOsrTestContext().getData().getPluralTnpDataDataProvider().get(PluralTnpDataCase.defaultPluralTnp);

        wiremock
                .add(new PluralStub().postPluralResponce201(pluralTnpData))
                .publish();
    }
/*
    @Given("create Mock")
    public void createPluralMock() {
        // GIVEN / ARRANGE
        //A4NetworkElementGroup neg = osrTestContext.getData().getA4NetworkElementGroupDataProvider().get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        //UewegData negName = osrTestContext.getData().getA4ImportCsvDataDataProvider().get(UewegDataCase.defaultUeweg);

        // INPUT FROM SCENARIO CONTEXT
        WireMockMappingsContext wiremock = (WireMockMappingsContext) testContext.getScenarioContext().getContext(Context.WIREMOCK);

        wiremock
                .add(new PluralStub().postPluralResponce())
               // .add(new PluralStub().postPlural)

                .publish();

    }

 */

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
        final Response response = a4Importer.doPluralImport(pluralTnpData.getNegName());

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);

    }


    @And("delete neg in ri recursively")
    public void deleteNegInRiRecursively() {
        a4ResourceInventory.deleteA4NetworkElementGroupsRecursively(pluralTnpData.getNegName());
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
            assertEquals(object.getString("numberCreatedNetworkElementPorts"), "20");
            System.out.println("+++ "+object.getString("numberNemoUpdateTasks"));
            assertEquals(object.getString("numberNemoUpdateTasks"), "22");
            System.out.println("+++ "+object.getString("numberCreatedNetworkElements"));
            assertEquals(object.getString("numberCreatedNetworkElements"), "1");
        }

    }



    @Then("ri was created with neg and ne and neps")
    public void riWasCreatedWithNegAndNeAndNeps() {
        /*
                A4riRobot:
                getNetworkElementGroupsByName
                getNetworkElementsByNegUuid
                getNetworkElementPortsByNetworkElement
         */

        List<NetworkElementGroupDto> networkElementGroupDtoList = a4ResourceInventory.getNetworkElementGroupsByName(pluralTnpData.getNegName());
        assertEquals(networkElementGroupDtoList.size(), 1);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NEG, networkElementGroupDtoList.get(0).getUuid());

        List<NetworkElementDto> networkElementDtoList = a4ResourceInventory.getNetworkElementsByNegUuid(networkElementGroupDtoList.get(0).getUuid());
        assertEquals(networkElementDtoList.size(), 1);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_NE, networkElementDtoList.get(0).getUuid());

        List<NetworkElementPortDto> networkElementPortDtoList = a4ResourceInventory.getNetworkElementPortsByNetworkElement(networkElementDtoList.get(0).getUuid());
        assertEquals(networkElementPortDtoList.size(), 20);

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
