package cucumber.stepdefinitions.team.berlinium;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkServiceProfileFtthAccess;
import com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.DeProvisioningStub;
import com.tsystems.tm.acc.ta.robot.osr.WgA4ProvisioningWiremockRobot;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_9_0.client.model.A4AccessLineRequestDto;
import cucumber.BaseSteps;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static org.testng.Assert.assertEquals;

public class WiremockWgA4ProvisioningSteps extends BaseSteps {

    private final WgA4ProvisioningWiremockRobot a4ProvWiremock = new WgA4ProvisioningWiremockRobot();

    public WiremockWgA4ProvisioningSteps(TestContext testContext) {
        super(testContext);
    }

    // -----=====[ GIVENS ]=====-----

    @Given("the wg-a4-provisioning mock will respond HTTP code {int} when called, and delete the NSP")
    public void uPiterDPUWiremockWillRespondHTTPCodeWhenCalledAndDeleteNsp(int httpCode) {
        // INPUT FROM SCENARIO CONTEXT
        WireMockMappingsContext wiremock = (WireMockMappingsContext) getScenarioContext().getContext(Context.WIREMOCK);
        final A4NetworkServiceProfileFtthAccess nspFtth = (A4NetworkServiceProfileFtthAccess) getScenarioContext().getContext(Context.A4_NSP_FTTH);

        // ACTION
        wiremock
                .add(new DeProvisioningStub().postDeProvAccessLineWithNspDeletion(httpCode, nspFtth.getUuid()))
                .publish();
    }

    @Given("the wg-a4-provisioning mock will respond HTTP code {int} when called")
    public void uPiterDPUWiremockWillRespondHTTPCodeWhenCalled(int httpCode) {
        // INPUT FROM SCENARIO CONTEXT
        WireMockMappingsContext wiremock = (WireMockMappingsContext) getScenarioContext().getContext(Context.WIREMOCK);

        // ACTION
        wiremock
                .add(new DeProvisioningStub().postDeProvAccessLine(httpCode))
                .publish();
    }

    @Given("the wg-a4-provisioning mock will respond HTTP code {int} when called the 1st time")
    public void uPiterDPUWiremockWillRespondHTTPCodeWhenCalledFirstTime(int httpCodeFirst) {
        // INPUT FROM SCENARIO CONTEXT
        WireMockMappingsContext wiremock = (WireMockMappingsContext) getScenarioContext().getContext(Context.WIREMOCK);

        // ACTION
        wiremock
                .add(new DeProvisioningStub().postDeProvAccessLineFirstTime(httpCodeFirst))
                .publish();
    }

    @Given("the wg-a4-provisioning mock will respond HTTP code {int} when called the 2nd time, and delete the NSP")
    public void uPiterDPUWiremockWillRespondHTTPCodeWhenCalledSecondTimeTime(int httpCodeSecond) {
        // INPUT FROM SCENARIO CONTEXT
        WireMockMappingsContext wiremock = (WireMockMappingsContext) getScenarioContext().getContext(Context.WIREMOCK);
        final A4NetworkServiceProfileFtthAccess nspFtth = (A4NetworkServiceProfileFtthAccess) getScenarioContext().getContext(Context.A4_NSP_FTTH);

        // ACTION
        wiremock
                .add(new DeProvisioningStub().postDeProvAccessLineSecondTimeWithNspDeletion(httpCodeSecond, nspFtth.getUuid()))
                .publish();
    }

    // -----=====[ THENS ]=====-----

    @Then("a DPU deprovisioning request to wg-a4-provisioning mock was triggered with Line ID {string}")
    public void aDPUDeprovisioningRequestToUPiterWasTriggeredWithLineID(String lineId) throws JsonProcessingException {
        // ACTION
        final String dpuCallbackBody = a4ProvWiremock.checkPostToDeprovisioningWiremock(1);
        final A4AccessLineRequestDto erg = om.readValue(dpuCallbackBody, A4AccessLineRequestDto.class);
        assertEquals(erg.getLineId(), lineId);
    }

    @Then("no DPU deprovisioning request to wg-a4-provisioning mock was triggered")
    public void noDPUDeprovisioningRequestToUPiterWasTriggered() {
        // ACTION
        a4ProvWiremock.checkPostToDeprovisioningWiremock(0);
    }

    @Then("the deprovisioning request to wg-a4-provisioning mock is repeated after {int} minutes")
    public void waitAndRetry(int min) {
        // ACTION
        sleepForSeconds(min * 60);
        a4ProvWiremock.checkPostToDeprovisioningWiremock(2);
    }

}
