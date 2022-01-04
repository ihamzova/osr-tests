package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.robot.osr.A4InventarSucheRobot;
import cucumber.BaseSteps;
import cucumber.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.testng.Assert.fail;

public class A4ResInvUiPagesSteps extends BaseSteps {

    private final A4InventarSucheRobot a4InventarSuche = new A4InventarSucheRobot();

    public A4ResInvUiPagesSteps(TestContext testContext) {
        super(testContext);
    }

    @When("the user navigates to (the )NE search page")
    public void userOpensNESearchPage() {
        a4InventarSuche.openInventarSuchePage();
        a4InventarSuche.clickNetworkElement();
    }

    @When("(the user )/(she )enters VPSZ {string} into the input fields")
    public void entersVPSZIntoFields(String vpsz) {
        a4InventarSuche.enterNeAkzByVpsz(vpsz);
        a4InventarSuche.enterNeOnkzByVpsz(vpsz);
        a4InventarSuche.enterNeVkzByVpsz(vpsz);
    }

    @When("(the user )/(she )enters FSZ {string} into the input field")
    public void entersFSZIntoField(String fsz) {
        a4InventarSuche.enterNeFsz(fsz);
    }

    @When("(the user )/(she )clicks the submit button")
    public void clicksSubmitButton() {
        a4InventarSuche.clickNeSearchButton();
    }

    @Then("the wanted NE is shown in the search result table")
    public void userGetsSearchResultsInTable() {
        fail("This step still has to be implemented");
    }

}
