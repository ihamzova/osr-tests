package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.robot.osr.A4ImportCsvRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4InventarSucheRobot;
import io.cucumber.java.en.When;

public class A4UiImportPageSteps {
    private final A4InventarSucheRobot a4ResInvSearch = new A4InventarSucheRobot();
    private final A4ImportCsvRobot a4Import = new A4ImportCsvRobot();


    @When("open import-ui")
    public void whenUserNavigatesToImportPage() {
        // ACTION
        a4Import.openImportPage();


    }

}
