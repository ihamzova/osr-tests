package hellocucumber;

import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;

public class StepDefinitions extends GigabitTest {

    private OsrTestContext osrTestContext;
    private A4ResourceInventoryRobot a4Inventory;
    private A4NemoUpdaterRobot a4NemoUpdater;

    // Initialize with dummy wiremock so that cleanUp() call within init() doesn't run into nullpointer
    private WireMockMappingsContext wiremock;

    public void setup(A4NetworkElementGroup negData) {
        a4Inventory.createNetworkElementGroup(negData);
    }

    public void cleanup(A4NetworkElementGroup negData) {
        wiremock.close();
        wiremock
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());

        a4Inventory.deleteA4TestDataRecursively(negData);
    }

    @Given("test context is set up")
    public void test_context_is_set_up() {
        osrTestContext = OsrTestContext.get();
        a4Inventory = new A4ResourceInventoryRobot();
        a4NemoUpdater = new A4NemoUpdaterRobot();

        // Initialize with dummy wiremock so that cleanUp() call within init() doesn't run into nullpointer
        wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "")).build();

    }

    @Given("Nemo wiremock is set up")
    public void nemo_wiremock_is_set_up() {
        wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "A4NemoUpdateTest"))
                .addWgA4ProvisioningMock()
                .addNemoMock()
                .build();
        wiremock.publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());
    }

    @Given("test data is cleaned up for uuid {string}")
    public void test_data_is_cleaned_up_for_uuid(String uuid) {
        a4Inventory.deleteA4TestDataRecursively(uuid);
    }

    @Given("a NEG with uuid {string}")
    public void a_neg_with_uuid(String uuid) {
        A4NetworkElementGroup negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        negData.setUuid(uuid);

        cleanup(negData);
        setup(negData);
    }

    @Given("no existing element with uuid {string}")
    public void no_existing_element_with_uuid(String uuid) {
        A4NetworkElementGroup negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        negData.setUuid(uuid);

        cleanup(negData);
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
