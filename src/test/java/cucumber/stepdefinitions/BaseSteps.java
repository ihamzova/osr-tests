package cucumber.stepdefinitions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import cucumber.Context;
import cucumber.ScenarioContext;
import cucumber.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;

import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.attachEventsToAllureReport;

public class BaseSteps extends GigabitTest {

    public final OsrTestContext osrTestContext = OsrTestContext.get();
    public final ObjectMapper om = new ObjectMapper();
    public ScenarioContext scenarioContext;

    public BaseSteps(TestContext testContext) {
        scenarioContext = testContext.getScenarioContext();
    }

    public ScenarioContext getScenarioContext() {
        return scenarioContext;
    }

//    @Before
//    public void init() {
//        WireMockMappingsContext wiremock = new OsrWireMockMappingsContextBuilder(
//                new WireMockMappingsContext(WireMockFactory.get(), "CucumberTests"))
//                .build();
//        wiremock.publish()
//                .publishedHook(savePublishedToDefaultDir())
//                .publishedHook(attachStubsToAllureReport());
//
//        getScenarioContext().setContext(Context.WIREMOCK, wiremock);
//    }
//
//    @After
//    public void cleanup() {
//        WireMockMappingsContext wiremock = (WireMockMappingsContext) getScenarioContext().getContext(Context.WIREMOCK);
//
//        wiremock.close();
//        wiremock
//                .eventsHook(saveEventsToDefaultDir())
//                .eventsHook(attachEventsToAllureReport());
//
//        // Hopefully next line will reliably remove old requests from our _local_ wiremock (not global one)
//        wiremock.getWireMock().resetRequests();
//    }

}
