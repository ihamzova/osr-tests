package cucumber;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
//        plugin = { "cucumberHooks.customReportListener", "io.qameta.allure.cucumber6jvm.AllureCucumber6Jvm" },
        plugin = { "pretty", "json:target/cucumberscenarios/cucumberscenarios.json" },
        monochrome = true,
        glue = { "cucumber/stepdefinitions" },
        features = { "src/test/resources/cucumberscenarios" }
//        tags = ""
)
public class RunCucumberTests extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }

}
