package cucumber.stepdefinitions.team.berlinium.a4.ui;

import com.tsystems.tm.acc.ta.robot.osr.A4MobileUiRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementDto;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileNeSearchPage.VPSZ_VALUE_LOCATOR;
import static org.testng.Assert.assertEquals;

public class MobileUiPageSteps {

    public final A4MobileUiRobot a4MobileUiRobot;
    private final TestContext testContext;

    public MobileUiPageSteps(TestContext testContext,
                             A4MobileUiRobot a4MobileUiRobot) {
        this.testContext = testContext;
        this.a4MobileUiRobot = a4MobileUiRobot;
    }

    // -----=====[ WHENS ]=====-----

    @When("the user searches for NE on Mobile Search page")
    public void whenUserSearchesForNeOnMobilePage() {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkElementDto ne = (NetworkElementDto) testContext.getScenarioContext().getContext(Context.A4_NE);

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
