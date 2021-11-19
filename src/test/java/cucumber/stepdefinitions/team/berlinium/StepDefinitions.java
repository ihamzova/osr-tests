package cucumber.stepdefinitions.team.berlinium;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofileftthaccess.A4NetworkServiceProfileFtthAccessCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.DeProvisioningStub;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgA4ProvisioningWiremockRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_9_0.client.model.A4AccessLineRequestDto;
import cucumber.ScenarioContext;
import cucumber.TestContext;
import cucumber.stepdefinitions.BaseSteps;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.UUID;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_MS;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_SERVICE_MS;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;
import static org.testng.Assert.assertEquals;

@ServiceLog({A4_RESOURCE_INVENTORY_MS, A4_RESOURCE_INVENTORY_SERVICE_MS})
public class StepDefinitions extends BaseSteps {

    private final A4ResourceInventoryRobot a4ResInv = new A4ResourceInventoryRobot();
    private final A4ResourceInventoryServiceRobot a4ResInvService = new A4ResourceInventoryServiceRobot();
    private final WgA4ProvisioningWiremockRobot deProvWiremock = new WgA4ProvisioningWiremockRobot();
    private final ObjectMapper om = new ObjectMapper();
    private final OsrTestContext osrTestContext = OsrTestContext.get();

    private WireMockMappingsContext wiremock;
    private Response response;

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData;
    private A4NetworkElementPort nepData;
    private A4TerminationPoint tpData;
    private A4NetworkServiceProfileFtthAccess nspFtthData;

    public StepDefinitions(TestContext testContext) {
        super(testContext);
    }

    @Before
    public void init() {
        wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "CucumberTests"))
                .build();
        wiremock.publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());
    }

    @After
    public void cleanup() {
        wiremock.close();
        wiremock
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());

        // Hopefully next line will reliably remove old requests from our _local_ wiremock (not global one)
        wiremock.getWireMock().resetRequests();

        if (negData != null)
            a4ResInv.deleteA4TestDataRecursively(negData);

        nspFtthData = null;
        tpData = null;
        nepData = null;
        neData = null;
        negData = null;
    }

    @Given("no TP exists in A4 resource inventory")
    public void noTPExistsInA4ResourceInventory() {
        tpData = new A4TerminationPoint();
        tpData.setUuid(UUID.randomUUID().toString());
    }

    @Given("a TP is existing in A4 resource inventory")
    public void aTPIsExistingInA4ResourceInventory() {
        setupDefaultTpTestData();
        a4ResInv.createTerminationPoint(tpData, nepData);
    }

    @Given("a NEP is existing in A4 resource inventory")
    public void aNEPIsExistingInA4ResourceInventory() {
        // NEP needs to be connected to a NE, so if no NE present, create one
        if (neData == null)
            aNEIsExistingInA4ResourceInventory();

        nepData = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
        a4ResInv.createNetworkElementPort(nepData, neData);
    }

    @Given("a NE is existing in A4 resource inventory")
    public void aNEIsExistingInA4ResourceInventory() {
        // NE needs to be connected to a NEG, so if no NEG present, create one
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
        // NSP needs to be connected to a TP, so if no TP present, create one
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

    @Given("a TP with type {string} is existing in A4 resource inventory")
    public void aTPWithTypeIsExistingInA4ResourceInventory(String tpType) {
        setupDefaultTpTestData();
        tpData.setSubType(tpType);
        a4ResInv.createTerminationPoint(tpData, nepData);
    }

    @Given("U-Piter DPU wiremock will respond HTTP code {int} when called, and do a callback")
    public void uPiterDPUWiremockWillRespondHTTPCodeWhenCalledAndDoACallback(int httpCode) {
        wiremock
                .add(new DeProvisioningStub().postDeProvAccessLineWithCallback(httpCode))
                .publish();
    }

    @Given("U-Piter DPU wiremock will respond HTTP code {int} when called")
    public void uPiterDPUWiremockWillRespondHTTPCodeWhenCalled(int httpCode) {
        wiremock
                .add(new DeProvisioningStub().postDeProvAccessLine(httpCode))
                .publish();
    }

    @When("NEMO sends a delete TP request( to A4 resource inventory service)")
    public void nemoSendsADeleteTPRequest() {
        response = a4ResInvService.deleteLogicalResource(tpData.getUuid());

        // Add a bit of waiting time here, to give process the chance to complete (because of async callbacks etc.)
        sleepForSeconds(2);
    }

    @Then("the TP does not exist in A4 resource inventory( anymore)/( any longer)")
    public void theTPIsNotExistingInA4ResourceInventoryAnymore() {
        a4ResInv.checkTerminationPointIsDeleted(tpData.getUuid());
    }

    @Then("a DPU deprovisioning request to U-Piter was triggered")
    public void aDPUDeprovisioningRequestToUPiterWasTriggered() {
        deProvWiremock.checkPostToDeprovisioningWiremock(1);
    }

    @Then("a DPU deprovisioning request to U-Piter was triggered with Line ID {string}")
    public void aDPUDeprovisioningRequestToUPiterWasTriggeredWithLineID(String lineId) throws JsonProcessingException {
        final String dpuCallbackBody = deProvWiremock.checkPostToDeprovisioningWiremock(1);

        final A4AccessLineRequestDto erg = om.readValue(dpuCallbackBody, A4AccessLineRequestDto.class);
        assertEquals(erg.getLineId(), lineId);
    }

    @Then("no DPU deprovisioning request to U-Piter was triggered")
    public void noDPUDeprovisioningRequestToUPiterWasTriggered() {
        deProvWiremock.checkPostToDeprovisioningWiremock(0);
    }

    @Then("the request is responded/answered with HTTP( error) code {int}")
    public void theRequestIsRespondedWithHTTPCode(int httpCode) {
        assertEquals(response.getStatusCode(), httpCode);
    }

    private void setupDefaultTpTestData() {
        // TP needs to be connected to a NEP, so if no NEP present, create one
        if (nepData == null)
            aNEPIsExistingInA4ResourceInventory();

        tpData = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.TerminationPointB);
    }

}
