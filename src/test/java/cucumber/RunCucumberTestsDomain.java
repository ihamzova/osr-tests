package cucumber;

import com.tsystems.tm.acc.ta.cucumber.AbstractGigabitCucumberTest;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        plugin = {"pretty",
                "html:target/allure-results/cucumberscenarios.html",
                "io.qameta.allure.cucumber6jvm.AllureCucumber6Jvm",
                "json:target/allure-results/cucumberscenarios.json"},
        monochrome = true,
        glue = {"com.tsystems.tm.acc.ta.cucumber.steps",
                "cucumber.stepdefinitions"},
        features = {"src/test/resources/cucumberscenarios/domain"}
)
public class RunCucumberTestsDomain extends AbstractGigabitCucumberTest {
}
