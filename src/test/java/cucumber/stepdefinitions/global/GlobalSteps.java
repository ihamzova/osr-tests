package cucumber.stepdefinitions.global;

import com.codeborne.selenide.SelenideElement;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import cucumber.BaseSteps;
import cucumber.Context;
import cucumber.TestContext;
import de.telekom.it.t3a.kotlin.context.GlobalContextKt;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

import java.net.URL;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.testng.Assert.assertEquals;

public class GlobalSteps extends BaseSteps {

    public GlobalSteps(TestContext testContext) {
        super(testContext);
    }

    @Given("user {string} with password {string} is logged in to {string}")
    public void userIsLoggedInToUiWithPassword(String user, String password, String ms) {
        final String WEB_DRIVER = "webdriver.chrome.driver";
        final String driver = GlobalContextKt.getContext().get(WEB_DRIVER, "otherValue");
        System.setProperty(WEB_DRIVER, driver);

        final URL url = new GigabitUrlBuilder(ms).build();
        open(url);

        final SelenideElement usernameInput = $("#username");
        final SelenideElement passwordInput = $("#password");
        final SelenideElement loginButton = $("#kc-login");
        usernameInput.setValue(user);
        passwordInput.setValue(password);
        loginButton.click();
    }

    @Then("the request is responded/answered with HTTP( error) code {int}")
    public void theRequestIsRespondedWithHTTPCode(int httpCode) {
        Response response = (Response) getScenarioContext().getContext(Context.RESPONSE);
        assertEquals(response.getStatusCode(), httpCode);
    }

}
