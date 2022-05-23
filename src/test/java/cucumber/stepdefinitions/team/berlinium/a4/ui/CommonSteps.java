package cucumber.stepdefinitions.team.berlinium.a4.ui;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.cucumber.steps.AbstractCommonBrowserSteps;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.test.SelenideConfigurationManagerFacade;
import cucumber.TestContext;
import io.cucumber.java.en.Given;

public class CommonSteps extends AbstractCommonBrowserSteps {

    private final TestContext testContext;

    public CommonSteps(TestContext testContext,
                       SelenideConfigurationManagerFacade selenideConfigurationManagerFacade) {
        super(selenideConfigurationManagerFacade);
        this.testContext = testContext;
    }

    // -----=====[ GIVENS ]=====-----

    @Given("a (rhsso )user with Berlinium credentials")
    public void givenAUserWithBerliniumCredentials() {
        // ACTION
        Credentials loginData = testContext.getOsrTestContext().getData().getCredentialsDataProvider()
                .get(CredentialsCase.RHSSOA4InventoryUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());
    }

}
