package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.robot.osr.A4MobileUiRobot;
import cucumber.BaseSteps;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileNeSearchPage.VPSZ_VALUE_LOCATOR;
import static org.testng.Assert.assertEquals;

public class A4UiMobileUiPageSteps extends BaseSteps {

    public final A4MobileUiRobot a4MobileUiRobot = new A4MobileUiRobot();

    public A4UiMobileUiPageSteps(TestContext testContext) {
        super(testContext);
    }

    // -----=====[ WHENS ]=====-----

    @When("the user searches for NE on Mobile Search page")
    public void whenUserSearchesForNeOnMobilePage() {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElement ne = (A4NetworkElement) getScenarioContext().getContext(Context.A4_NE);

        // ACTION
        a4MobileUiRobot.searchForNetworkElement(ne);
    }

    // -----=====[ THENS ]=====-----

    @Then("the value of shown VPSZ is {string}")
    public void thenTheValueOfShownVPSZIs(String vpsz) {
        String s = $(VPSZ_VALUE_LOCATOR).text().trim();
        assertEquals(s, vpsz);
    }

}
