package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.robot.osr.A4InventarSucheRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementDto;
import cucumber.BaseSteps;
import cucumber.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;

public class A4ResInvUiPagesSteps extends BaseSteps {

    private final A4InventarSucheRobot a4ResInvSearch = new A4InventarSucheRobot();

    public A4ResInvUiPagesSteps(TestContext testContext) {
        super(testContext);
    }

    @When("the user navigates to (the )NE search page")
    public void userOpensNESearchPage() {
        a4ResInvSearch.openInventarSuchePage();
        a4ResInvSearch.clickNetworkElement();
    }

    @When("(the user )/(she )enters VPSZ {string} into the input fields")
    public void entersVPSZIntoFields(String vpsz) {
        a4ResInvSearch.enterNeAkzByVpsz(vpsz);
        a4ResInvSearch.enterNeOnkzByVpsz(vpsz);
        a4ResInvSearch.enterNeVkzByVpsz(vpsz);
    }

    @When("(the user )/(she )enters FSZ {string} into the input field")
    public void entersFSZIntoField(String fsz) {
        a4ResInvSearch.enterNeFsz(fsz);
    }

    @When("(the user )/(she )clicks the submit button")
    public void clicksSubmitButton() {
        a4ResInvSearch.clickNeSearchButton();
    }

    @Then("the/a/one/1 NE in the search result list has VPSZ {string} and FSZ {string}")
    public void userGetsSearchResultsInTable(String vpsz, String fsz) {
        List<NetworkElementDto> neFilteredlist = a4ResInvSearch.createNeListActualResult()
                .stream()
                .filter(ne -> Objects.equals(ne.getVpsz(), vpsz) && Objects.equals(ne.getFsz(), fsz))
                .collect(Collectors.toList());

        assertEquals(neFilteredlist.size(), 1); // NE with both VPSZ & FSZ are unique, there can be only one
        neFilteredlist.forEach(ne -> {
            assertEquals(ne.getVpsz(), vpsz);
            assertEquals(ne.getFsz(), fsz);
        });
    }

    @Then("{int} NE(s) in the search result list has/have VPSZ {string}")
    public void xNEsAreShownInSearchResults(int count, String vpsz) {
        List<NetworkElementDto> neFilteredlist = a4ResInvSearch.createNeListActualResult()
                .stream()
                .filter(ne -> Objects.equals(ne.getVpsz(), vpsz))
                .collect(Collectors.toList());

        assertEquals(neFilteredlist.size(), count);
        neFilteredlist.forEach(ne ->
                assertEquals(ne.getVpsz(), vpsz)
        );
    }

    @Then("the NE search result list is empty")
    public void theNESearchResultListIsEmpty() {
        xNEAreShownInSearchResultList(0);
    }

    @Then("{int} NE(s) is/are shown in the search result list")
    public void xNEAreShownInSearchResultList(int count) {
        List<NetworkElementDto> neFoundlist = a4ResInvSearch.createNeListActualResult();

        assertEquals(neFoundlist.size(), count);
    }

}
