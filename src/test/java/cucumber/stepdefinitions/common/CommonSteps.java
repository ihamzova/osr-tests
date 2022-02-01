package cucumber.stepdefinitions.common;

import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;
import static org.testng.Assert.assertEquals;

public class CommonSteps {
    private final TestContext testContext;

    public CommonSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    @Before
    public void setup() {
        // ACTION
        WireMockMappingsContext wiremock = new OsrWireMockMappingsContextBuilder(
                new WireMockMappingsContext(WireMockFactory.get(), "CucumberTests"))
                .build();
        wiremock.publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.WIREMOCK, wiremock);
    }

    @After
    public void cleanup() {
        // INPUT FROM SCENARIO CONTEXT
        WireMockMappingsContext wiremock = (WireMockMappingsContext) testContext.getScenarioContext().getContext(Context.WIREMOCK);

        // ACTION
        wiremock.close();
        wiremock
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());

        wiremock.getWireMock().resetRequests();
    }

    // -----=====[ THENS ]=====-----

    @Then("the (callback )request is responded/answered with HTTP( error) code {int}")
    public void thenTheRequestIsRespondedWithHTTPCode(int httpCode) {
        // INPUT FROM SCENARIO CONTEXT
        Response response = (Response) testContext.getScenarioContext().getContext(Context.RESPONSE);

        // ACTION
        assertEquals(response.getStatusCode(), httpCode);
    }

}
