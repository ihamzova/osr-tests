package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getRandomDigits;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;

public class NemoUpdate extends GigabitTest {

    private OsrTestContext osrTestContext;
    private final A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();
    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();

    // Initialize with dummy wiremock so that cleanUp() call within init() doesn't run into nullpointer
    private WireMockMappingsContext wiremock;

    private String uuid;
    private A4NetworkElementGroup negData;

    @Before
    public void test_context_is_set_up() {
        osrTestContext = OsrTestContext.get();

        wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "A4NemoUpdateTest"))
                .addNemoMock()
                .build();
        wiremock.publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());
    }

    @After
    public void cleanup() {
        wiremock.close();
        wiremock
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());

        a4Inventory.deleteA4TestDataRecursively(negData);
    }

    @Given("a NEG with uuid {string} exists in A4 resource inventory")
    public void a_neg_with_uuid(String negUuid) {
        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);

        uuid = negUuid;

        negData.setUuid(uuid);
        negData.setName("NEG " + getRandomDigits(6));

        a4Inventory.createNetworkElementGroup(negData);
    }

    @Given("no NEG with uuid {string} exists in A4 resource inventory")
    public void no_existing_element_with_uuid(String uuid) {
        a4Inventory.deleteA4TestDataRecursively(uuid);
    }

    @When("a Nemo update is triggered for uuid {string}")
    public void nemo_update_is_triggered_for_uuid(String uuid) {
        a4NemoUpdater.triggerNemoUpdate(uuid);
    }

    @Then("Nemo should have gotten a Put request for uuid {string}")
    public void nemo_should_have_gotten_a_put_request_for_uuid(String uuid) {
        a4NemoUpdater.checkLogicalResourcePutRequestToNemoWiremock(uuid);
    }

    @Then("Nemo should have gotten a Delete request for uuid {string}")
    public void nemo_should_have_gotten_a_delete_request_for_uuid(String uuid) {
        a4NemoUpdater.checkLogicalResourceDeleteRequestToNemoWiremock(uuid);
    }

}
