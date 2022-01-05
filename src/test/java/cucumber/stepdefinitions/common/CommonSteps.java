package cucumber.stepdefinitions.common;

import com.codeborne.selenide.SelenideElement;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import cucumber.BaseSteps;
import cucumber.Context;
import cucumber.TestContext;
import de.telekom.it.t3a.kotlin.context.GlobalContextKt;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

import java.net.URL;

import static com.codeborne.selenide.Selenide.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.attachEventsToAllureReport;
import static org.testng.Assert.assertEquals;

public class CommonSteps extends BaseSteps {

    public CommonSteps(TestContext testContext) {
        super(testContext);
    }

    @Before
    public void init() {
        WireMockMappingsContext wiremock = new OsrWireMockMappingsContextBuilder(
                new WireMockMappingsContext(WireMockFactory.get(), "CucumberTests"))
                .build();
        wiremock.publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        getScenarioContext().setContext(Context.WIREMOCK, wiremock);
    }

    @After
    public void cleanup() {
        WireMockMappingsContext wiremock = (WireMockMappingsContext) getScenarioContext().getContext(Context.WIREMOCK);

        wiremock.close();
        wiremock
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());

        wiremock.getWireMock().resetRequests();

        if(getScenarioContext().isContains(Context.BROWSER)) {
            destroySelenium();
            closeWebDriver();
        }
    }

    @Given("user {string} with password {string} is logged in to {string}")
    public void userIsLoggedInToUiWithPassword(String user, String password, String ms) {
        // For some reason the usual way that GigabitTest takes care of setting the webdriver and doing the rhsso login
        // to the ui doesn't work with cucumber. Therefore doing it by hand...

        setCredentials(user, password);
        initSelenium();

        final String WEB_DRIVER = "webdriver.chrome.driver";
        final String driver = GlobalContextKt.getContext().get(WEB_DRIVER, "otherValue");
        System.setProperty(WEB_DRIVER, driver);

        final URL url = new GigabitUrlBuilder(ms).build();
        open(url);
        getScenarioContext().setContext(Context.BROWSER, true);

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
