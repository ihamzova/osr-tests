package cucumber.stepdefinitions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import cucumber.ScenarioContext;
import cucumber.TestContext;

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

}
