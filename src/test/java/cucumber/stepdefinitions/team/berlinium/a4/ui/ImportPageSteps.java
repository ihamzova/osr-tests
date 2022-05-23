package cucumber.stepdefinitions.team.berlinium.a4.ui;

import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvLine;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryImporterUiRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImportPageSteps {

    private final TestContext testContext;
    private final A4ResourceInventoryRobot a4ResInv;
    private final A4ResourceInventoryImporterUiRobot a4ImportUi;

    public ImportPageSteps(TestContext testContext,
                           A4ResourceInventoryRobot a4ResInv,
                           A4ResourceInventoryImporterUiRobot a4ImportUi) {
        this.testContext = testContext;
        this.a4ResInv = a4ResInv;
        this.a4ImportUi = a4ImportUi;
    }

    // -----=====[ GIVENS ]=====-----

    @Given("the user has a CSV file with the following data:")
    public void theUserHasACSVFileWithTheFollowingData(DataTable table) {
        // ACTION
        final List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        final List<A4ImportCsvLine> csvLines = new ArrayList<>();

        rows.forEach(columns -> {
            final A4ImportCsvLine csvLine = new A4ImportCsvLine();
            csvLine.setNegName(columns.get("NEG Name"));
            csvLine.setNeVpsz(columns.get("VPSZ"));
            csvLine.setNeFsz(columns.get("FSZ"));

            csvLines.add(csvLine);
        });

        final A4ImportCsvData csvData = new A4ImportCsvData();
        csvData.setCsvLines(csvLines);

        // Remove any old potentially colliding test data...
        a4ResInv.deleteA4TestDataRecursively(csvData);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_CSV, csvData);
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
