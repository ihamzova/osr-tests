package cucumber;

import com.tsystems.tm.acc.ta.testng.GigabitTest;
import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.FeatureWrapper;
import io.cucumber.testng.PickleWrapper;
import io.cucumber.testng.TestNGCucumberRunner;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
@CucumberOptions(
        plugin = {"pretty",
                "html:target/allure-results/cucumberscenarios.html",
                "io.qameta.allure.cucumber6jvm.AllureCucumber6Jvm",
                "json:target/allure-results/cucumberscenarios.json"},
        monochrome = true,
        glue = {"cucumber.stepdefinitions.team.berlinium",
                "cucumber.stepdefinitions.common"},
        features = {"src/test/resources/cucumberscenarios/team/berlinium"},
        tags = "@ui"
)
public class TestRunnerGigabitCucumberBerlinium extends GigabitTest {

    private TestNGCucumberRunner testNGCucumberRunner;

    @BeforeClass(alwaysRun = true)
    public void setUpCucumber() {
        testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
    }

    @Test(groups = "cucumber", description = "Runs Cucumber Feature", dataProvider = "features")
    public void feature(PickleWrapper pickle, FeatureWrapper feature) {
        testNGCucumberRunner.runScenario(pickle.getPickle());
    }

    @DataProvider
    //@DataProvider() // @DataProvider(parallel = true) <- use this if you want to execute multiple scenarios in parallel
    public Object[][] features() {
        return testNGCucumberRunner.provideScenarios();
    }

    //@Override
    //@DataProvider() // @DataProvider(parallel = true) <- use this if you want to execute multiple scenarios in //parallel
    //public Object[][] scenarios() {
    //    return super.scenarios();
    //}

    @AfterClass(alwaysRun = true)
    public void tearDownClass() throws Exception {
        testNGCucumberRunner.finish();
    }

}
