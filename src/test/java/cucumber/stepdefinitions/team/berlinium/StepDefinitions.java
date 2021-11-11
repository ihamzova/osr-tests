package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofileftthaccess.A4NetworkServiceProfileFtthAccessCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.UUID;

import static org.testng.Assert.assertEquals;

public class StepDefinitions extends GigabitTest {

    private final A4ResourceInventoryRobot a4ResInv = new A4ResourceInventoryRobot();
    private final A4ResourceInventoryServiceRobot a4ResInvService = new A4ResourceInventoryServiceRobot();
//    private final A4NemoUpdaterRobot nemoUpdater = new A4NemoUpdaterRobot();

    private final OsrTestContext osrTestContext = OsrTestContext.get();
    //    private WireMockMappingsContext wiremock;
    private Response response;

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData;
    private A4NetworkElementPort nepData;
    private A4TerminationPoint tpData;
    private A4NetworkServiceProfileFtthAccess nspFtthData;

    @Before
    public void init() {
//        wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "CucumberA4NemoUpdateTest"))
//                .build();
//        wiremock.publish()
//                .publishedHook(savePublishedToDefaultDir())
//                .publishedHook(attachStubsToAllureReport());
    }

    @After
    public void cleanup() {
//        wiremock.close();
//        wiremock
//                .eventsHook(saveEventsToDefaultDir())
//                .eventsHook(attachEventsToAllureReport());

        if (negData != null)
            a4ResInv.deleteA4TestDataRecursively(negData);

        nspFtthData = null;
        tpData = null;
        nepData = null;
        neData = null;
        negData = null;
    }

//    @Given("NEMO wiremock is set up")
//    public void nemoWiremockIsSetUp() {
//        wiremock
//                .add(new NemoStub().putNemoUpdate201())
//                .add(new NemoStub().deleteNemoUpdate204());
//    }
//
//    @Given("a NEG with uuid {string} exists in A4 resource inventory")
//    public void a_neg_with_uuid(String negUuid) {
//        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
//                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
//
//        negData.setUuid(negData.getUuid());
//        negData.setName("NEG " + getRandomDigits(6));
//
//        a4ResInv.createNetworkElementGroup(negData);
//    }
//
//    @Given("no NEG with uuid {string} exists in A4 resource inventory")
//    public void no_existing_element_with_uuid(String uuid) {
//        a4ResInv.deleteA4TestDataRecursively(uuid);
//    }
//
//    @When("a Nemo update is triggered for uuid {string}")
//    public void nemo_update_is_triggered_for_uuid(String uuid) {
//        nemoUpdater.triggerNemoUpdate(uuid);
//    }
//
//    @Then("Nemo should have gotten a Put request for uuid {string}")
//    public void nemo_should_have_gotten_a_put_request_for_uuid(String uuid) {
//        nemoUpdater.checkLogicalResourcePutRequestToNemoWiremock(uuid);
//    }
//
//    @Then("Nemo should have gotten a Delete request for uuid {string}")
//    public void nemo_should_have_gotten_a_delete_request_for_uuid(String uuid) {
//        nemoUpdater.checkLogicalResourceDeleteRequestToNemoWiremock(uuid);
//    }


    //---------------------------------------------------------

    @Given("no TP exists in A4 resource inventory")
    public void noTPExistsInA4ResourceInventory() {
        tpData = new A4TerminationPoint();
        tpData.setUuid(UUID.randomUUID().toString());
    }

    @When("NEMO sends a delete TP request( to A4 resource inventory service)")
    public void nemoSendsADeleteTPRequest() {
        response = a4ResInvService.deleteLogicalResource(tpData.getUuid());

        // Add a bit of waiting time here, to give process the chance to complete (because of async callbacks etc.)?
    }

    @Then("the request is responded/answered with HTTP( error) code {int}")
    public void theRequestIsRespondedWithHTTPCode(int httpCode) {
        assertEquals(response.getStatusCode(), httpCode);
    }

    @Given("a TP with type {string} is existing in A4 resource inventory")
    public void aTPWithTypeIsExistingInA4ResourceInventory(String tpType) {
        setupDefaultTpTestData();
        tpData.setSubType(tpType);
        a4ResInv.createTerminationPoint(tpData, nepData);
    }

    @Given("a TP is existing in A4 resource inventory")
    public void aTPIsExistingInA4ResourceInventory() {
        setupDefaultTpTestData();
        a4ResInv.createTerminationPoint(tpData, nepData);
    }

    private void setupDefaultTpTestData() {
        if (nepData == null)
            aNEPIsExistingInA4ResourceInventory();

        tpData = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.TerminationPointB);
    }

    @Given("a NEP is existing in A4 resource inventory")
    public void aNEPIsExistingInA4ResourceInventory() {
        if (nepData == null)
            aNEIsExistingInA4ResourceInventory();

        nepData = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
        a4ResInv.createNetworkElementPort(nepData, neData);
    }

    @Given("a NE is existing in A4 resource inventory")
    public void aNEIsExistingInA4ResourceInventory() {
        if (negData == null)
            aNEGIsExistingInA4ResourceInventory();

        neData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        a4ResInv.createNetworkElement(neData, negData);
    }

    @Given("a NEG is existing in A4 resource inventory")
    public void aNEGIsExistingInA4ResourceInventory() {
        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        a4ResInv.createNetworkElementGroup(negData);
    }

    @Given("a NSP FTTH(-Access) with Line ID {string} is existing in A4 resource inventory( for the TP)")
    public void aNSPFTTHWithLineIDIsExistingInA4ResourceInventoryForTheTP(String lineId) {
        if (tpData == null)
            aTPIsExistingInA4ResourceInventory();

        nspFtthData = osrTestContext.getData().getA4NetworkServiceProfileFtthAccessDataProvider()
                .get(A4NetworkServiceProfileFtthAccessCase.defaultNetworkServiceProfileFtthAccess);
        nspFtthData.setLineId(lineId);
        a4ResInv.createNetworkServiceProfileFtthAccess(nspFtthData, tpData);
    }

    @Given("no NSP FTTH(-Access) exists in A4 resource inventory( for the TP)")
    public void noNSPFTTHExistsInA4ResourceInventoryForTheTP() {
        nspFtthData = new A4NetworkServiceProfileFtthAccess();
        nspFtthData.setUuid(UUID.randomUUID().toString());
    }

    @Then("the TP does not exist in A4 resource inventory( anymore)/( any longer)")
    public void theTPIsNotExistingInA4ResourceInventoryAnymore() {
        a4ResInv.checkTerminationPointIsDeleted(tpData.getUuid());
    }

}
