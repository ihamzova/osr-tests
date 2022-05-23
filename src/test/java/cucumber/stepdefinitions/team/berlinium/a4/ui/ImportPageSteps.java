package cucumber.stepdefinitions.team.berlinium.a4.ui;

import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryImporterUiRobot;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ImportPageSteps {

    private final TestContext testContext;
    private final A4ResourceInventoryImporterUiRobot a4ImportUi;

    public ImportPageSteps(TestContext testContext,
                           A4ResourceInventoryImporterUiRobot a4ImportUi) {
        this.testContext = testContext;
        this.a4ImportUi = a4ImportUi;
    }


    // -----=====[ WHENS ]=====-----

    @When("the (the )user navigates to (the )import page")
    public void whenUserNavigatesToImportPage() {
        a4ImportUi.openImportPage();
    }

    @When("enters NEG name {string} into the input field")
    public void whenUserEntersNegNameIntoImportInputField(String negName) {
        a4ImportUi.insertNegName(negName);
    }

    @When("clicks the import submit button")
    public void whenUserClicksImportSubmitButton() {
        a4ImportUi.clickSendenButton();
    }

    @When("uploads the CSV file")
    public void uploadsTheCSVFile() {
        // INPUT FROM SCENARIO CONTEXT
        final A4ImportCsvData csvData = (A4ImportCsvData) testContext.getScenarioContext().getContext(Context.A4_CSV);

        // ACTION
        a4ImportUi.importCsvFileViaUi(csvData);
    }

    // -----=====[ THENS ]=====-----

    @Then("the ui displays a positive response")
    public void positiveResponseFromImporterAtUiIsReceived() {
        // INPUT FROM SCENARIO CONTEXT
        A4ImportCsvData csvData = (A4ImportCsvData) testContext.getScenarioContext().getContext(Context.A4_CSV);

        // ACTION
        final String expected = "\"numberCreatedNetworkElements\": \"" + csvData.getCsvLines().size() + "\"";
        a4ImportUi.checkImportResultMessage(expected);
    }

}
