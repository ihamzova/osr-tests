package cucumber;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

//@RunWith(Cucumber.class)
//@CucumberOptions(features = { "classpath:features" }, plugin = { "pretty", "json:target/cucumberscenarios/cucumberscenarios.json" })
@CucumberOptions(
//        plugin = { "cucumberHooks.customReportListener", "io.qameta.allure.cucumber6jvm.AllureCucumber6Jvm" },
        monochrome = true,
        glue = { "cucumber/stepdefinitions" }, // Packagename
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
