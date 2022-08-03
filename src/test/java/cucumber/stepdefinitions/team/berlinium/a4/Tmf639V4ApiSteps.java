package cucumber.stepdefinitions.team.berlinium.a4;

import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceV4Robot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkServiceProfileA10NspDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.v4.client.model.NspA10Nsp;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class Tmf639V4ApiSteps {

    private final A4ResourceInventoryServiceV4Robot a4ResInvV4Service;
    private final TestContext testContext;

    public Tmf639V4ApiSteps(TestContext testContext,
                            A4ResourceInventoryServiceV4Robot a4ResInvV4Service) {
        this.testContext = testContext;
        this.a4ResInvV4Service = a4ResInvV4Service;
    }

    @When("CA-Integration sends a GET request for the NSP A10NSP via the TMF639 v4 API")
    public void whenSendGetNspA10NspForTmf639V4Api() {
        final NetworkServiceProfileA10NspDto nspA10Nsp = (NetworkServiceProfileA10NspDto) testContext.getScenarioContext().getContext(Context.A4_NSP_A10NSP);
        final Response response = a4ResInvV4Service.getNetworkServiceProfilesA10NspV4ByUuidWithoutChecks(nspA10Nsp.getUuid());
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("CA-Integration sends a GET request for NSP A10NSP {string} via the TMF639 v4 API")
    public void whenGetRequestForNspA10Nsp(String nspAlias) {
        final NetworkServiceProfileA10NspDto nspA10Nsp = (NetworkServiceProfileA10NspDto) testContext.getScenarioContext().getContext(Context.A4_NSP_A10NSP, nspAlias);
        final Response response = a4ResInvV4Service.getNetworkServiceProfilesA10NspV4ByUuidWithoutChecks(nspA10Nsp.getUuid());
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("CA-Integration sends a GET request for NSPs A10NSP with query param itAccountingKey = {string} via the TMF639 v4 API")
    public void whenGetRequestForNspA10NspWithQueryParamItAccountingKey(String itAccountingKey) {
        final Response response = a4ResInvV4Service.getNetworkServiceProfilesA10NspV4ByItAccountingKeyWithoutChecks(itAccountingKey);
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("CA-Integration sends a GET request for NSPs A10NSP with query param NetworkElementLinkUuid = {string} via the TMF639 v4 API")
    public void whenSendGetNspA10NspForTmf639V4ApiWithQueryParamNetworkElementLinkUuid(String networkElementLinkUuid) {
        final Response response = a4ResInvV4Service.getNetworkServiceProfilesA10NspV4ByNetworkElementLinkUuidWithoutChecks(networkElementLinkUuid);
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

//    @When("CA-Integration sends a GET request for NSPs A10NSP with query params itAccountingKey = {string} and NetworkElementLinkUuid = {string} via the TMF639 v4 API")
//    public void whenSendGetNspA10NspForTmf639V4ApiWithQueryParams(String itAccountingKey, String networkElementLinkUuid) {
//        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
//    }

    @Then("the response contains {int} NSP(s) A10NSP in TMF639 v4 format")
    public void thenResponseContainsXNspsA10Nsps(int number) {
        final Response response = (Response) testContext.getScenarioContext().getContext(Context.RESPONSE);
        final List<NspA10Nsp> nspsA10Nsp = Arrays.asList(response.getBody().as(NspA10Nsp[].class));

        assertEquals(nspsA10Nsp.size(), number);
    }

}
