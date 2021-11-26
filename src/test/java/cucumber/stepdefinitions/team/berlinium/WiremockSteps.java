package cucumber.stepdefinitions.team.berlinium;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.A4ResourceInventoryStub;
import com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.DeProvisioningStub;
import com.tsystems.tm.acc.ta.robot.osr.A4ResilienceRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgA4ProvisioningWiremockRobot;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_9_0.client.model.A4AccessLineRequestDto;
import cucumber.Context;
import cucumber.TestContext;
import cucumber.stepdefinitions.BaseSteps;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.io.IOException;

import static io.netty.handler.codec.mqtt.MqttMessageBuilders.publish;
import static org.testng.Assert.assertEquals;

public class WiremockSteps extends BaseSteps {

    private final WgA4ProvisioningWiremockRobot deProvWiremock = new WgA4ProvisioningWiremockRobot();
    private final A4ResilienceRobot a4ResilienceRobot = new A4ResilienceRobot();

    public WiremockSteps(TestContext testContext) {
        super(testContext);
    }

    @Before
    public void init() {
    }

    @After
    public void cleanup() {
    }

    @Given("U-Piter DPU wiremock will respond HTTP code {int} when called, and do a callback")
    public void uPiterDPUWiremockWillRespondHTTPCodeWhenCalledAndDoACallback(int httpCode) {
        WireMockMappingsContext wiremock = (WireMockMappingsContext) getScenarioContext().getContext(Context.WIREMOCK);

        wiremock
                .add(new DeProvisioningStub().postDeProvAccessLineWithCallback(httpCode))
                .publish();
    }

    @Given("U-Piter DPU wiremock will respond HTTP code {int} when called")
    public void uPiterDPUWiremockWillRespondHTTPCodeWhenCalled(int httpCode) {
        WireMockMappingsContext wiremock = (WireMockMappingsContext) getScenarioContext().getContext(Context.WIREMOCK);

        wiremock
                .add(new DeProvisioningStub().postDeProvAccessLine(httpCode))
                .publish();
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

    @Then("TP UUID is added to DLQ")
    public void tpUuidIsAddedToDlq() {

        try {
            a4ResilienceRobot.checkMessagesInQueue("jms.dlq.deprovisioning", 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Given ("DLQ is empty")
    public void dlqIsEmpty() {
        a4ResilienceRobot.removeAllMessagesInQueue("jms.dlq.deprovisioning");
    }

    @Given("A4 resource inventory will respond HTTP code {int} when called")
    public void RiWiremockWillRespondHTTPCodeWhenCalled(int httpCode) {
        WireMockMappingsContext wiremock = (WireMockMappingsContext) getScenarioContext().getContext(Context.WIREMOCK);

        wiremock
                .add(new A4ResourceInventoryStub().deleteTPWith500(httpCode))
                .publish();
    }

}
