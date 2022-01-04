package cucumber.stepdefinitions.team.berlinium;

import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.robot.osr.A4InventarSucheRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementDto;
import cucumber.BaseSteps;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CollectionCondition.size;
import static org.testng.Assert.assertEquals;

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

    @Then("the/a NE with VPSZ {string} and FSZ {string} is shown in the search result list")
    public void userGetsSearchResultsInTable(String vpsz, String fsz) {
        List<NetworkElementDto> neFoundlist = a4InventarSuche.createNeListActualResult();

        List<NetworkElementDto> neFilteredlist = neFoundlist.stream()
                .filter(ne -> Objects.equals(ne.getVpsz(), vpsz) && Objects.equals(ne.getFsz(), fsz))
                .collect(Collectors.toList());

        assertEquals(neFilteredlist.size(), 1);

        NetworkElementDto neFound = neFilteredlist.get(0);
        assertEquals(neFound.getVpsz(), vpsz);
        assertEquals(neFound.getFsz(), fsz);
    }

    @Then("the NE search result list is empty")
    public void theNESearchResultListIsEmpty() {
        List<NetworkElementDto> neFoundlist = a4InventarSuche.createNeListActualResult();

        assertEquals(neFoundlist.size(), 0);
    }

}
