package cucumber.stepdefinitions;

import cucumber.BaseSteps;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

import static org.testng.Assert.assertEquals;

public class GlobalSteps extends BaseSteps {

    public GlobalSteps(TestContext testContext) {
        super(testContext);
    }

    @Then("the request is responded/answered with HTTP( error) code {int}")
    public void theRequestIsRespondedWithHTTPCode(int httpCode) {
        Response response = (Response) getScenarioContext().getContext(Context.RESPONSE);
        assertEquals(response.getStatusCode(), httpCode);
    }

}
