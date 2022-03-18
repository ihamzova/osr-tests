package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryImporterUiRobot;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class A4UiImportPageSteps {

    private final TestContext testContext;
    private final A4ResourceInventoryImporterUiRobot a4ImportUi = new A4ResourceInventoryImporterUiRobot();

    public A4UiImportPageSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    @When("open import-ui")
    public void whenUserNavigatesToImportPage() {
        a4ImportUi.openImportPage();
    }

    @Then("positive response from importer at ui is received")
    public void positiveResponseFromImporterAtUiIsReceived() {
        A4ImportCsvData csvData = (A4ImportCsvData) testContext.getScenarioContext().getContext(Context.A4_CSV);
        final String expected = "\"numberCreatedNetworkElements\": \"" + csvData.getCsvLines().size() + "\"";
        a4ImportUi.checkImportResultMessage(expected);
    }

    @When("insert neg name")
    public void insertNegName() {
        A4ImportCsvData csvData = (A4ImportCsvData) testContext.getScenarioContext().getContext(Context.A4_CSV);
        a4ImportUi.insertNegName(csvData.getCsvLines().get(0).getNegName());
        a4ImportUi.clickSendenButton();
        System.out.println("+++ NEG-Name auf UI eingegeben!");
    }

}
