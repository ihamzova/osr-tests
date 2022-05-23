package cucumber.stepdefinitions.team.berlinium.a4;

import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvLine;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImportCsvSteps {

    private final A4ResourceInventoryRobot a4ResInv;
    private final TestContext testContext;

    public ImportCsvSteps(TestContext testContext,
                          A4ResourceInventoryRobot a4ResInv) {
        this.testContext = testContext;
        this.a4ResInv = a4ResInv;
    }

    @After
    public void cleanup() {
        final boolean CSV_PRESENT = testContext.getScenarioContext().isContains(Context.A4_CSV);
        if (CSV_PRESENT) {
            final A4ImportCsvData csv = (A4ImportCsvData) testContext.getScenarioContext().getContext(Context.A4_CSV);
            a4ResInv.deleteA4TestDataRecursively(csv);
        }
    }


    // -----=====[ GIVENS ]=====-----

    @Given("(the user has )a CSV file with the following data:")
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

}
