package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.robot.osr.A4ImportCsvRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4InventarSucheRobot;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static org.testng.AssertJUnit.assertTrue;

public class A4UiImportPageSteps {
    private final A4InventarSucheRobot a4ResInvSearch = new A4InventarSucheRobot();
    private final A4ImportCsvRobot a4Import = new A4ImportCsvRobot();


    @When("open import-ui")
    public void whenUserNavigatesToImportPage() {
        // ACTION
        a4Import.openImportPage();


    }
    /*
    @When("read message")
    public void readMessage() {
        // ACTION
        a4Import.readMessage();
    }
     */

    @Then("positive response from importer at ui is received")
    public void positiveResponseFromImporterAtUiIsReceived() {
       assertTrue(a4Import.readMessage().contains("22"));
       System.out.println("+++ 22er Check ok!");

    }

    @And("insert neg name")
    public void insertNegName() {
        // negName=49/6808/1/POD/01, vpsz=49/4149/1, fachsz=7KH0
        a4Import.insertNegName("49/6808/1/POD/01\n"); // pluralTnpData.getNegName() oder "blablabla\n"
        //System.out.println("+++ Drücke gleich Knopf!");
        a4Import.pressEnterButton();
        System.out.println("+++ Plural-Import gestartet!");
        sleepForSeconds(6);

    }

}
