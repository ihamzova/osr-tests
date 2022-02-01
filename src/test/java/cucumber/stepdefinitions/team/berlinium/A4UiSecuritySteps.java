package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import cucumber.TestContext;
import io.cucumber.java.en.When;

import static com.codeborne.selenide.Selenide.open;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_UI_MS;

public class A4UiSecuritySteps {

    private final TestContext testContext;

    public A4UiSecuritySteps(TestContext testContext) {
        this.testContext = testContext;
    }

    // -----=====[ WHENS ]=====-----

    @When("the user navigates to A4 Inventory UI path {string} and query parameter(s) {string}")
    public void whenUserNavigatesToA4UiPath(String path, String queryParam) {
        // ACTION
        final String url = new GigabitUrlBuilder(A4_RESOURCE_INVENTORY_UI_MS).withEndpoint(path).build() + queryParam;
        open(url);
    }

    @When("(the user )enters Inbetriebnahme page URL with VPSZ query param(eter) {string}")
    public void whenUserNavigatesToInbetriebnahmePageWithVpszQueryParam(String vpsz) {
        // ACTION
        whenUserNavigatesToA4UiPath("/a4-resource-inventory-ui/a4-installation-process/inbetriebnahme", "?sNeVpsz=" + vpsz + "&sNeFsz=7KH0&sNeCategory=OLT");
    }

}
