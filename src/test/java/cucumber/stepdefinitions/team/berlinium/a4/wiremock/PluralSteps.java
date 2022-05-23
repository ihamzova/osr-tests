package cucumber.stepdefinitions.team.berlinium.a4.wiremock;

import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvLine;
import com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.PluralStub;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PluralSteps {

    private final A4ResourceInventoryRobot a4ResourceInventory;
    private final TestContext testContext;

    public PluralSteps(TestContext testContext,
                       A4ResourceInventoryRobot a4ResourceInventory) {
        this.testContext = testContext;
        this.a4ResourceInventory = a4ResourceInventory;
    }


    // -----=====[ GIVENS ]=====-----

    @Given("the Plural mock will respond HTTP code {int} and provide the following NE data when called with NEG name {string}:")
    public void givenThePluralMockWillRespondHTTPCodeAndProvideTheFollowingNEDataWhenCalledWithNEGName(int httpStatus, String negName, DataTable table) {
        // INPUT FROM SCENARIO CONTEXT
        WireMockMappingsContext wiremock = (WireMockMappingsContext) testContext.getScenarioContext().getContext(Context.WIREMOCK);

        // ACTION
        final List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        final List<A4ImportCsvLine> csvLines = new ArrayList<>();

        rows.forEach(columns -> {
            final A4ImportCsvLine csvLine = new A4ImportCsvLine();
            csvLine.setNegName(negName);
            csvLine.setNeVpsz(columns.get("VPSZ"));
            csvLine.setNeFsz(columns.get("FSZ"));

            csvLines.add(csvLine);
        });

        final A4ImportCsvData csvData = new A4ImportCsvData();
        csvData.setCsvLines(csvLines);

        // Set up Plural wiremock...
        wiremock
                .add(new PluralStub().postPluralResponse(negName, httpStatus, csvData))
                .publish();

        // Remove any old potentially colliding test data...
        a4ResourceInventory.deleteA4TestDataRecursively(csvData);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_CSV, csvData);
    }

}
