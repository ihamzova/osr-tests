package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.data.osr.mappers.RebellMapper;
import com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.RebellStub;
import com.tsystems.tm.acc.ta.robot.osr.A4WiremockRebellRobot;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementLinkDto;
import com.tsystems.tm.acc.tests.osr.rebell.client.model.Ueweg;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getEndsz;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getEndszFromLbz;

public class WiremockRebellSteps {

    private final TestContext testContext;
    private final A4WiremockRebellRobot rebell;
    private final RebellMapper rebellMapper;

    public WiremockRebellSteps(TestContext testContext, A4WiremockRebellRobot rebell, RebellMapper rebellMapper) {
        this.testContext = testContext;
        this.rebell = rebell;
        this.rebellMapper = rebellMapper;
    }


    // -----=====[ GIVENs ]=====-----

    @And("the REBELL wiremock will respond HTTP code {int} when called for NE {string}(,) with the following data:")
    public void theREBELLWiremockWillRespondHTTPCodeWhenCalledWithTheFollowingData(int httpCode, String neAlias, DataTable rebellProperties) {
        final NetworkElementDto ne = (NetworkElementDto) testContext.getScenarioContext().getContext(Context.A4_NE, neAlias);
        WireMockMappingsContext wiremock = (WireMockMappingsContext) testContext.getScenarioContext().getContext(Context.WIREMOCK);
        final List<Map<String, String>> rows = rebellProperties.asMaps(String.class, String.class);
        List<Ueweg> uewegList = new ArrayList<>();

        rows.forEach(columns -> {
            final NetworkElementLinkDto nel = (NetworkElementLinkDto) testContext.getScenarioContext().getContext(Context.A4_NEL, columns.get("NEL Reference"));
            final String endszA = getEndszFromLbz(nel.getLbz()).get(0);
            final String endszB = getEndszFromLbz(nel.getLbz()).get(1);

            final Ueweg ueweg = rebellMapper.getUewegByNel(endszA, endszB, nel, columns.get("Vendor Port Name A"), columns.get("Vendor Port Name B"));
            uewegList.add(ueweg);
        });

        wiremock
                .add(new RebellStub().getUewegMultiple(httpCode, getEndsz(ne), uewegList))
                .publish();
    }


    // -----=====[ THENs ]=====-----

    @Then("{int} {string} request was sent to the REBELL wiremock for NE {string}")
    public void thenRebellWiremockCalled(int count, String httpMethod, String neAlias) {
        final NetworkElementDto ne = (NetworkElementDto) testContext.getScenarioContext().getContext(Context.A4_NE, neAlias);
        rebell.checkSyncRequestToRebellWiremock(getEndsz(ne), httpMethod, count);
    }

}
