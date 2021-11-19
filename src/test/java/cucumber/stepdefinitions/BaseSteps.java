package cucumber.stepdefinitions;

import com.tsystems.tm.acc.ta.testng.GigabitTest;
import cucumber.ScenarioContext;
import cucumber.TestContext;

public class BaseSteps extends GigabitTest {

    private ScenarioContext scenarioContext;

    public BaseSteps(TestContext testContext) {
        scenarioContext = testContext.getScenarioContext();
    }

    public ScenarioContext getScenarioContext() {
        return scenarioContext;
    }

}
