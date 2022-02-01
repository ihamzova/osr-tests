package cucumber.stepdefinitions.team.berlinium;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkServiceProfileFtthAccess;
import com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.DeProvisioningStub;
import com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.PreProvisioningStub;
import com.tsystems.tm.acc.ta.robot.osr.WgA4ProvisioningWiremockRobot;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_9_0.client.model.A4AccessLineRequestDto;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static org.testng.Assert.assertEquals;

public class WiremockWgA4ProvisioningSteps {

    private final WgA4ProvisioningWiremockRobot a4ProvWiremock = new WgA4ProvisioningWiremockRobot();
    private final TestContext testContext;

    public WiremockWgA4ProvisioningSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    // -----=====[ GIVENS ]=====-----

    @Given("the wg-a4-provisioning preprovisioning mock will respond HTTP code {int} when called, and create the NSP")
    public void givenUPiterDpuPreprovWiremockWillRespondHTTPCodeWhenCalledAndCreateNsp(int httpCode) {
        // INPUT FROM SCENARIO CONTEXT
        WireMockMappingsContext wiremock = (WireMockMappingsContext) testContext.getScenarioContext().getContext(Context.WIREMOCK);

        // ACTION
        wiremock
                .add(new PreProvisioningStub().postPreProvAccessLineWithNspCreation(httpCode))
                .publish();
    }

    @Given("the wg-a4-provisioning preprovisioning mock will respond HTTP code {int} when called the 1st time")
    public void givenUPiterDpuPreprovWiremockWillRespondHTTPCodeWhenCalledFirstTime(int httpCode) {
        // INPUT FROM SCENARIO CONTEXT
        WireMockMappingsContext wiremock = (WireMockMappingsContext) testContext.getScenarioContext().getContext(Context.WIREMOCK);

        // ACTION
        wiremock
                .add(new PreProvisioningStub().postPreProvAccessLineFirstTime(httpCode))
                .publish();
    }

    @Given("the wg-a4-provisioning preprovisioning mock will respond HTTP code {int} when called the 2nd time, and create the NSP")
    public void givenUPiterDpuPreprovWiremockWillRespondHTTPCodeWhenCalledSecondTimeAndCreateNsp(int httpCode) {
        // INPUT FROM SCENARIO CONTEXT
        WireMockMappingsContext wiremock = (WireMockMappingsContext) testContext.getScenarioContext().getContext(Context.WIREMOCK);

        // ACTION
        wiremock
                .add(new PreProvisioningStub().postPreProvAccessLineSecondTimeWithNspCreation(httpCode))
                .publish();
    }

    @Given("the wg-a4-provisioning deprovisioning mock will respond HTTP code {int} when called, and delete the NSP")
    public void givenUPiterDpuDeprovWiremockWillRespondHTTPCodeWhenCalledAndDeleteNsp(int httpCode) {
        // INPUT FROM SCENARIO CONTEXT
        WireMockMappingsContext wiremock = (WireMockMappingsContext) testContext.getScenarioContext().getContext(Context.WIREMOCK);
        final A4NetworkServiceProfileFtthAccess nspFtth = (A4NetworkServiceProfileFtthAccess) testContext.getScenarioContext().getContext(Context.A4_NSP_FTTH);

        // ACTION
        wiremock
                .add(new DeProvisioningStub().postDeProvAccessLineWithNspDeletion(httpCode, nspFtth.getUuid()))
                .publish();
    }

    @Given("the wg-a4-provisioning deprovisioning mock will respond HTTP code {int} when called")
    public void givenUPiterDpuDeprovWiremockWillRespondHTTPCodeWhenCalled(int httpCode) {
        // INPUT FROM SCENARIO CONTEXT
        WireMockMappingsContext wiremock = (WireMockMappingsContext) testContext.getScenarioContext().getContext(Context.WIREMOCK);

        // ACTION
        wiremock
                .add(new DeProvisioningStub().postDeProvAccessLine(httpCode))
                .publish();
    }

    @Given("the wg-a4-provisioning deprovisioning mock will respond HTTP code {int} when called the 1st time")
    public void givenUPiterDpuDeprovWiremockWillRespondHTTPCodeWhenCalledFirstTime(int httpCodeFirst) {
        // INPUT FROM SCENARIO CONTEXT
        WireMockMappingsContext wiremock = (WireMockMappingsContext) testContext.getScenarioContext().getContext(Context.WIREMOCK);

        // ACTION
        wiremock
                .add(new DeProvisioningStub().postDeProvAccessLineFirstTime(httpCodeFirst))
                .publish();
    }

    @Given("the wg-a4-provisioning deprovisioning mock will respond HTTP code {int} when called the 2nd time, and delete the NSP")
    public void givenUPiterDpuDeprovWiremockWillRespondHTTPCodeWhenCalledSecondTimeAndDeleteNsp(int httpCodeSecond) {
        // INPUT FROM SCENARIO CONTEXT
        WireMockMappingsContext wiremock = (WireMockMappingsContext) testContext.getScenarioContext().getContext(Context.WIREMOCK);
        final A4NetworkServiceProfileFtthAccess nspFtth = (A4NetworkServiceProfileFtthAccess) testContext.getScenarioContext().getContext(Context.A4_NSP_FTTH);

        // ACTION
        wiremock
                .add(new DeProvisioningStub().postDeProvAccessLineSecondTimeWithNspDeletion(httpCodeSecond, nspFtth.getUuid()))
                .publish();
    }

    // -----=====[ THENS ]=====-----

    @Then("a DPU preprovisioning request to wg-a4-provisioning mock was triggered")
    public void thenADpuPreprovisioningRequestToUPiterWasTriggered() {
        a4ProvWiremock.checkPostToPreprovisioningWiremock(1);
    }

    @Then("the DPU preprovisioning request to wg-a4-provisioning mock is repeated after {int} minutes")
    public void thenDpuPreprovisioningRequestIsRepeatedAfterMinutes(int min) {
        // ACTION
        sleepForSeconds(min * 60);
        a4ProvWiremock.checkPostToPreprovisioningWiremock(2);
    }

    @Then("a DPU deprovisioning request to wg-a4-provisioning mock was triggered with Line ID {string}")
    public void thenADpuDeprovisioningRequestToUPiterWasTriggeredWithLineID(String lineId) throws JsonProcessingException {
        // ACTION
        final String dpuCallbackBody = a4ProvWiremock.checkPostToDeprovisioningWiremock(1);
        final A4AccessLineRequestDto erg = testContext.getObjectMapper().readValue(dpuCallbackBody, A4AccessLineRequestDto.class);
        assertEquals(erg.getLineId(), lineId);
    }

    @Then("no DPU deprovisioning request to wg-a4-provisioning mock was triggered")
    public void thenNoDpuDeprovisioningRequestToUPiterWasTriggered() {
        // ACTION
        a4ProvWiremock.checkPostToDeprovisioningWiremock(0);
    }

    @Then("the DPU deprovisioning request to wg-a4-provisioning mock is repeated after {int} minutes")
    public void thenDpuDeprovisioningRequestIsRepeatedAfterMinutes(int min) {
        // ACTION
        sleepForSeconds(min * 60);
        a4ProvWiremock.checkPostToDeprovisioningWiremock(2);
    }

}
