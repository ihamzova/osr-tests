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
        // It'd be great if wiremock could handle >1 webhooks, then it could delete the NSP _and_ send the callback.
        // Unfortunately it isn't, therefore the callback has to be sent "by hand" in separate step method.

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
        givenUPiterDpuDeprovWiremockWillRespondHTTPCodeWhenCalledFirstXTimes(httpCodeFirst, 1);
    }

    @Given("the wg-a4-provisioning deprovisioning mock will respond HTTP code {int} when called the first {int} time(s)")
    public void givenUPiterDpuDeprovWiremockWillRespondHTTPCodeWhenCalledFirstXTimes(int httpCode, int numberOfRetryAttempts) {
        // INPUT FROM SCENARIO CONTEXT
        WireMockMappingsContext wiremock = (WireMockMappingsContext) testContext.getScenarioContext().getContext(Context.WIREMOCK);

        // ACTION
        wiremock.add(new DeProvisioningStub().postDeProvAccessLineFirstTime(httpCode));

        for (int i = 1; i < numberOfRetryAttempts; i++) {
            wiremock.add(new DeProvisioningStub().postDeProvAccessLineRetry(httpCode, i));
        }

        wiremock.publish();
    }

    @Given("the wg-a4-provisioning deprovisioning mock will respond HTTP code {int} when called the {int} (nd/rd/th) time, and delete the NSP")
    public void givenUPiterDpuDeprovWiremockWillRespondHTTPCodeWhenCalledForthTimeAndDeleteNsp(int httpCode, int attempt) {
        // INPUT FROM SCENARIO CONTEXT
        WireMockMappingsContext wiremock = (WireMockMappingsContext) testContext.getScenarioContext().getContext(Context.WIREMOCK);
        final A4NetworkServiceProfileFtthAccess nspFtth = (A4NetworkServiceProfileFtthAccess) testContext.getScenarioContext().getContext(Context.A4_NSP_FTTH);

        // ACTION
        wiremock
                .add(new DeProvisioningStub().postDeProvAccessLineWithNspDeletion(httpCode, nspFtth.getUuid(), attempt))
                .publish();
    }

    // -----=====[ THENS ]=====-----

    @Then("a DPU preprovisioning request to wg-a4-provisioning mock was triggered")
    public void thenADpuPreprovisioningRequestToUPiterWasTriggered() {
        a4ProvWiremock.checkPostToPreprovisioningWiremock(1);
    }

    @Then("the DPU preprovisioning request to wg-a4-provisioning mock is repeated after {int} minutes")
    public void thenDpuPreprovisioningRequestIsRepeatedAfterMinutes(int minutes) {
        // ACTION
        sleepForSeconds(minutes * 60);
        a4ProvWiremock.checkPostToPreprovisioningWiremock(2);
    }

    @Then("{int} DPU deprovisioning request(s) to wg-a4-provisioning mock was/were triggered with Line ID {string}")
    public void thenADpuDeprovisioningRequestToUPiterWasTriggeredWithLineID(int countExpected, String lineId) throws JsonProcessingException {
        // ACTION
        final String dpuCallbackBody = a4ProvWiremock.checkPostToDeprovisioningWiremock(countExpected);
        final A4AccessLineRequestDto erg = testContext.getObjectMapper().readValue(dpuCallbackBody, A4AccessLineRequestDto.class);
        assertEquals(erg.getLineId(), lineId);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.WIREMOCK_COUNT_WG_A4_DEPROV, countExpected);
    }

    @Then("no/0 DPU deprovisioning request to wg-a4-provisioning mock was/were triggered")
    public void thenNoDpuDeprovisioningRequestToUPiterWasTriggered() {
        // ACTION
        a4ProvWiremock.checkPostToDeprovisioningWiremock(0);
    }

    @Then("the DPU deprovisioning request to wg-a4-provisioning mock is repeated after {int} minutes")
    public void thenDpuDeprovisioningRequestIsRepeatedAfterMinutes(int minutes) {
        // INPUT FROM SCENARIO CONTEXT
        final int count = (int) testContext.getScenarioContext().getContext(Context.WIREMOCK_COUNT_WG_A4_DEPROV);

        // ACTION
        sleepForSeconds(minutes * 60);
        a4ProvWiremock.checkPostToDeprovisioningWiremock(count + 1);

        sleepForSeconds(5); // give following processes chance to finish
    }

}
