package cucumber;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        plugin = {"pretty",
                "html:target/allure-results/cucumberscenarios.html",
                "io.qameta.allure.cucumber6jvm.AllureCucumber6Jvm",
                "json:target/allure-results/cucumberscenarios.json"},
        monochrome = true,
        glue = {"cucumber.stepdefinitions"},
        features = {"src/test/resources/cucumberscenarios/domain"}
)
public class RunCucumberTestsDomain extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider()
    public Object[][] scenarios() {
        return super.scenarios();
    }

}
