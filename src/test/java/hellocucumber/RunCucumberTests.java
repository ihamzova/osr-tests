package hellocucumber;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

//@RunWith(Cucumber.class)
//@CucumberOptions(features = { "classpath:features" }, plugin = { "pretty", "json:target/cucumber/cucumber.json" })

@CucumberOptions(
        features = {"src/test/resources/hellocucumber"},
//        glue = {"StepDefinitions"},
        tags = ""
)
public class RunCucumberTests extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }

}
