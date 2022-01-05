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

public class A4UiSearchPageSteps extends BaseSteps {

    private final A4InventarSucheRobot a4ResInvSearch = new A4InventarSucheRobot();

    public A4UiSearchPageSteps(TestContext testContext) {
        super(testContext);
    }

    // -----=====[ WHENS ]=====-----

    @When("the user navigates to (the )NE search page")
    public void whenUserNavigatesToNeSearchPage() {
        // ACTION
        a4ResInvSearch.openInventarSuchePage();
        a4ResInvSearch.clickNetworkElement();
    }

    @When("(the user )/(she )enters VPSZ {string} into the input fields")
    public void whenUserentersVpszIntoFields(String vpsz) {
        // ACTION
        a4ResInvSearch.enterNeAkzByVpsz(vpsz);
        a4ResInvSearch.enterNeOnkzByVpsz(vpsz);
        a4ResInvSearch.enterNeVkzByVpsz(vpsz);
    }

    @When("(the user )/(she )enters FSZ {string} into the input field")
    public void whenUserentersFszIntoField(String fsz) {
        // ACTION
        a4ResInvSearch.enterNeFsz(fsz);
    }

    @When("(the user )/(she )clicks the submit button")
    public void whenUserClicksSubmitButton() {
        // ACTION
        a4ResInvSearch.clickNeSearchButton();
    }

    // -----=====[ THENS ]=====-----

    @Then("the/a/one/1 NE in the search result list has VPSZ {string} and FSZ {string}")
    public void thenUserGetsSearchResultsWithVpszAndFszInTable(String vpsz, String fsz) {
        // ACTION
        List<NetworkElementDto> neFilteredlist = a4ResInvSearch.createNeListActualResult()
                .stream()
                .filter(ne -> Objects.equals(ne.getVpsz(), vpsz) && Objects.equals(ne.getFsz(), fsz))
                .collect(Collectors.toList());

        assertEquals(neFilteredlist.size(), 1); // NE with both VPSZ & FSZ are unique, there can be only one
        assertEquals(neFilteredlist.get(0).getVpsz(), vpsz);
        assertEquals(neFilteredlist.get(0).getFsz(), fsz);
    }

    @Then("{int} NE(s) in the search result list has/have VPSZ {string}")
    public void thenXNEsAreShownInSearchResultTable(int count, String vpsz) {
        // ACTION
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
    public void thenTheNeSearchResultListIsEmpty() {
        // ACTION
        thenXNesAreShownInSearchResultList(0);
    }

    @Then("{int} NE(s) is/are shown in the search result list")
    public void thenXNesAreShownInSearchResultList(int count) {
        // ACTION
        List<NetworkElementDto> neFoundlist = a4ResInvSearch.createNeListActualResult();

        assertEquals(neFoundlist.size(), count);
    }

}
