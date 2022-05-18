package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.data.osr.models.A10nspA4Dto;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceOrderRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4WiremockA10nspA4Robot;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.tmf652.client.model.ResourceOrder;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.Then;

public class WiremockA10NspSteps {

    private final TestContext testContext;
    private final A4ResourceOrderRobot roRobot;
    private final A4WiremockA10nspA4Robot a10Mock;

    public WiremockA10NspSteps(TestContext testcontext, A4ResourceOrderRobot roRobot, A4WiremockA10nspA4Robot a10mock) {
        this.testContext = testcontext;
        this.roRobot = roRobot;
        this.a10Mock = a10mock;
    }


    // -----=====[ THENS ]=====-----

    @Then("{int} {string} request was sent to the A10NSP Inventory mock for the 1st order item")
    public void thenA10NspInvMockCalled(int count, String httpMethod) {
        final ResourceOrder ro = (ResourceOrder) testContext.getScenarioContext().getContext(Context.A4_RESOURCE_ORDER);
        final A10nspA4Dto a10Nsp = roRobot.getA10NspA4Dto(ro);
        a10Mock.checkSyncRequestToA10nspA4Wiremock(a10Nsp, httpMethod, count);
    }

}
