package cucumber;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

//@Epic("OS&R domain")
//@Owner("bela.kovace@t-systems.com")
//@TmsLink("DIGIHUB-xxxxx")
@CucumberOptions(
//        plugin = { "cucumberHooks.customReportListener", "io.qameta.allure.cucumber6jvm.AllureCucumber6Jvm" },
        plugin = {"pretty",
                "html:target/allure-results/cucumberscenarios.html",
                "io.qameta.allure.cucumber6jvm.AllureCucumber6Jvm",
                "json:target/allure-results/cucumberscenarios.json"},
        monochrome = true,
        glue = { "cucumber/stepdefinitions" },
        features = { "src/test/resources/cucumberscenarios" }
)
public class RunCucumberTests extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider() // @DataProvider(parallel = true) <- use this if you want to execute multiple scenarios in parallel
    public Object[][] scenarios() {
        return super.scenarios();
    }

}
