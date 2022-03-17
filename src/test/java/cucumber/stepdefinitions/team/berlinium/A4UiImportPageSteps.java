package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.robot.osr.A4ImportCsvRobot;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.testng.Assert.assertTrue;

public class A4UiImportPageSteps {

    private final A4ImportCsvRobot a4Import = new A4ImportCsvRobot();
    private final TestContext testContext;
    public A4UiImportPageSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    @When("open import-ui")
    public void whenUserNavigatesToImportPage() {
        a4Import.openImportPage();
    }

    @Then("positive response from importer at ui is received")
    public void positiveResponseFromImporterAtUiIsReceived() {
        A4ImportCsvData csvData = (A4ImportCsvData) testContext.getScenarioContext().getContext(Context.A4_CSV);
        // check: "numberCreatedNetworkElements": "2"     --> funktioniert nicht zuverlässig
        System.out.println("+++ Check UI Ausgabe startet");
       assertTrue(a4Import.readMessage().contains("\"numberCreatedNetworkElements\": \""+csvData.getCsvLines().size()+"\""));
       System.out.println("+++ Check UI Ausgabe ok");
       }

    @And("insert neg name")
    public void insertNegName() {
        A4ImportCsvData csvData = (A4ImportCsvData) testContext.getScenarioContext().getContext(Context.A4_CSV);
        a4Import.insertNegName(csvData.getCsvLines().get(0).getNegName()+"\n"); // inkl. ENTER
       // a4Import.pressEnterButton();
        System.out.println("+++ NEG-Name auf UI eingegeben!");

    }

}
