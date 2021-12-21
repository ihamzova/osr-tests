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
        glue = {"cucumber.stepdefinitions.team.berlinium",
                "cucumber.stepdefinitions.global"},
        features = {"src/test/resources/cucumberscenarios/team/berlinium"}
)
public class RunCucumberTestsBerlinium extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider() // @DataProvider(parallel = true) <- use this if you want to execute multiple scenarios in parallel
    public Object[][] scenarios() {
        return super.scenarios();
    }

//    @BeforeClass
//    @Parameters({ "subfolder" })
//    public void init(@Optional("/") String subfolder) {
//        final String subfolder1 = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("subfolder");
//    }

}
