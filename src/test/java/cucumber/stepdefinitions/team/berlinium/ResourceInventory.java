package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import io.cucumber.java.en.Given;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getRandomDigits;

public class ResourceInventory {

//    private OsrTestContext osrTestContext;
//    private final A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();
//    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();
//
//    // Initialize with dummy wiremock so that cleanUp() call within init() doesn't run into nullpointer
//    private WireMockMappingsContext wiremock;
//
//    private String uuid;
//
//    @Given("no NEG with uuid {string} exists in A4 resource inventory")
//    public void no_existing_element_with_uuid(String uuid) {
//        a4Inventory.deleteA4TestDataRecursively(uuid);
//    }
//
//    @Given("a NEG with uuid {string} exists in A4 resource inventory")
//    public void a_neg_with_uuid(String negUuid) {
//        A4NetworkElementGroup negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
//                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
//
//        uuid = negUuid;
//
//        negData.setUuid(uuid);
//        negData.setName("NEG " + getRandomDigits(6));
//
//        a4Inventory.createNetworkElementGroup(negData);
//    }

}
