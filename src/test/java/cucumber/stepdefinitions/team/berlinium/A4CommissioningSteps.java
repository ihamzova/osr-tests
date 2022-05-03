package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.robot.osr.A4CommissioningRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.TerminationPointDto;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;

public class A4CommissioningSteps {

    private final A4CommissioningRobot a4Commissioning = new A4CommissioningRobot();
    private final TestContext testContext;

    public A4CommissioningSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    // -----=====[ WHENS ]=====-----

    @When("the wg-a4-provisioning mock sends the callback")
    public void whenUPiterMockSendsTheCallback() {
        // This step is necessary because wiremock can only handle 1 webhook, which is used to create/delete the NSP.
        // Therefore the callback itself cannot be handled via webhook but needs to be sent "by hand".

        // INPUT FROM SCENARIO CONTEXT
        final TerminationPointDto tp = (TerminationPointDto) testContext.getScenarioContext().getContext(Context.A4_TP);

        // ACTION
        final Response response = a4Commissioning.startCallBackA4AccessLineDeprovisioningWithoutChecks(tp.getUuid());

        // Add a bit of waiting time here, to give process the chance to complete
        sleepForSeconds(2);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

}
