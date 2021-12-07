package cucumber.stepdefinitions.team.berlinium;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkServiceProfileFtthAccess;
import com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.A4ResourceInventoryStub;
import com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.DeProvisioningStub;
import com.tsystems.tm.acc.ta.robot.osr.WgA4ProvisioningWiremockRobot;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_9_0.client.model.A4AccessLineRequestDto;
import cucumber.Context;
import cucumber.TestContext;
import cucumber.stepdefinitions.BaseSteps;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import static org.testng.Assert.assertEquals;

public class WiremockSteps extends BaseSteps {

    private final WgA4ProvisioningWiremockRobot a4ProvWiremock = new WgA4ProvisioningWiremockRobot();

    public WiremockSteps(TestContext testContext) {
        super(testContext);
    }

    @Given("U-Piter DPU mock will respond HTTP code {int} when called, and send a callback")
    public void uPiterDPUWiremockWillRespondHTTPCodeWhenCalledAndDoACallback(int httpCode) {
        WireMockMappingsContext wiremock = (WireMockMappingsContext) getScenarioContext().getContext(Context.WIREMOCK);

        wiremock
                .add(new DeProvisioningStub().postDeProvAccessLineWithCallback(httpCode))
                .publish();
    }

    @Given("the U-Piter DPU mock will respond HTTP code {int} when called, and delete the NSP")
    public void uPiterDPUWiremockWillRespondHTTPCodeWhenCalledAndDeleteNsp(int httpCode) {
        WireMockMappingsContext wiremock = (WireMockMappingsContext) getScenarioContext().getContext(Context.WIREMOCK);
        final A4NetworkServiceProfileFtthAccess nspFtth = (A4NetworkServiceProfileFtthAccess) getScenarioContext().getContext(Context.A4_NSP_FTTH);

        wiremock
                .add(new DeProvisioningStub().postDeProvAccessLineWithNspDeletion(httpCode, nspFtth.getUuid()))
                .publish();
    }

    @Given("the U-Piter DPU mock will respond HTTP code {int} when called")
    public void uPiterDPUWiremockWillRespondHTTPCodeWhenCalled(int httpCode) {
        WireMockMappingsContext wiremock = (WireMockMappingsContext) getScenarioContext().getContext(Context.WIREMOCK);

        wiremock
                .add(new DeProvisioningStub().postDeProvAccessLine(httpCode))
                .publish();
    }

    @Given("the U-Piter DPU mock will respond HTTP code {int} when called 1st time, and HTTP code {int} when called 2nd time, and delete the NSP")
    public void uPiterDPUWiremockWillRespondHTTPCodeWhenCalledFirstTime(int httpCodeFirst, int httpCodeSecond) {
        WireMockMappingsContext wiremock = (WireMockMappingsContext) getScenarioContext().getContext(Context.WIREMOCK);
        final A4NetworkServiceProfileFtthAccess nspFtth = (A4NetworkServiceProfileFtthAccess) getScenarioContext().getContext(Context.A4_NSP_FTTH);

        wiremock
                .add(new DeProvisioningStub().postDeProvAccessLineFirstTime(httpCodeFirst))
                .add(new DeProvisioningStub().postDeProvAccessLineSecondTimeWithNspDeletion(httpCodeSecond, nspFtth.getUuid()))
                .publish();
    }

    @Given("the A4 resource inventory will respond HTTP code {int} when called")
    public void RiWiremockWillRespondHTTPCodeWhenCalled(int httpCode) {
        WireMockMappingsContext wiremock = (WireMockMappingsContext) getScenarioContext().getContext(Context.WIREMOCK);

        wiremock
                .add(new A4ResourceInventoryStub().deleteTPWith500(httpCode))
                .publish();
    }

    @Then("a DPU deprovisioning request to U-Piter was triggered")
    public void aDPUDeprovisioningRequestToUPiterWasTriggered() {
        a4ProvWiremock.checkPostToDeprovisioningWiremock(1);
    }

    @Then("a DPU deprovisioning request to U-Piter was triggered with Line ID {string}")
    public void aDPUDeprovisioningRequestToUPiterWasTriggeredWithLineID(String lineId) throws JsonProcessingException {
        final String dpuCallbackBody = a4ProvWiremock.checkPostToDeprovisioningWiremock(1);
        final A4AccessLineRequestDto erg = om.readValue(dpuCallbackBody, A4AccessLineRequestDto.class);
        assertEquals(erg.getLineId(), lineId);
    }

    @Then("no DPU deprovisioning request to U-Piter was triggered")
    public void noDPUDeprovisioningRequestToUPiterWasTriggered() {
        a4ProvWiremock.checkPostToDeprovisioningWiremock(0);
    }

}
