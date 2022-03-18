package cucumber;

import com.tsystems.tm.acc.ta.cucumber.AbstractGigabitCucumberTest;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        plugin = {"pretty",
                "html:target/allure-results/cucumberscenarios.html",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "json:target/allure-results/cucumberscenarios.json"},
        monochrome = true,
        glue = {"com.tsystems.tm.acc.ta.cucumber.steps",
                "cucumber.stepdefinitions.team.berlinium",
                "cucumber.stepdefinitions.common"},
        features = {"src/test/resources/cucumberscenarios/team/berlinium"},
        tags = "@smoke"
)
public class RunCucumberTestsBerliniumQuickSmokeTest extends AbstractGigabitCucumberTest {
}
