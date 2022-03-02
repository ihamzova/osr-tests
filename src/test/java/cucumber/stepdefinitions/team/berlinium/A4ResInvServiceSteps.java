package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryServiceMapper;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.LogicalResourceUpdate;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.time.OffsetDateTime;

import static com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryServiceMapper.*;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;

public class A4ResInvServiceSteps {

    final int SLEEP_TIMER = 5; // in seconds
    private final A4ResourceInventoryServiceRobot a4ResInvService = new A4ResourceInventoryServiceRobot();
    private final A4ResourceInventoryServiceMapper a4ResInvServiceMapper = new A4ResourceInventoryServiceMapper();
    private final TestContext testContext;

    public A4ResInvServiceSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    // -----=====[ WHENS ]=====-----

    @When("NEMO sends a request to change/update (the )NEG operationalState to {string}")
    public void nemoSendsARequestToChangeNEGOperationalStateTo(String ops) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementGroup neg = (A4NetworkElementGroup) testContext.getScenarioContext().getContext(Context.A4_NEG);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = a4ResInvServiceMapper.createMinimalLogicalResourceUpdate(NEG);
        a4ResInvServiceMapper.addCharacteristic(lru, CHAR_OPSTATE, ops);
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(neg.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to change/update (the )NEG without operationalState( characteristic)")
    public void whenNemoSendsARequestToUpdateNEGWithoutOperationalState() {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementGroup neg = (A4NetworkElementGroup) testContext.getScenarioContext().getContext(Context.A4_NEG);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = a4ResInvServiceMapper.createMinimalLogicalResourceUpdate(NEG);
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(neg.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to change/update (the )NE operationalState to {string}")
    public void nemoSendsARequestToChangeNEOperationalStateTo(String ops) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElement ne = (A4NetworkElement) testContext.getScenarioContext().getContext(Context.A4_NE);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = a4ResInvServiceMapper.createMinimalLogicalResourceUpdate(NE);
        a4ResInvServiceMapper.addCharacteristic(lru, CHAR_OPSTATE, ops);
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(ne.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to change/update (the )NE without operationalState( characteristic)")
    public void whenNemoSendsARequestToUpdateNeWithoutOperationalState() {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElement ne = (A4NetworkElement) testContext.getScenarioContext().getContext(Context.A4_NE);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = a4ResInvServiceMapper.createMinimalLogicalResourceUpdate(NE);
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(ne.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to change/update (the )NEP operationalState to {string} and description to {string}")
    public void nemoSendsARequestToUpdateNEPOperationalStateToAndDescriptionTo(String opState, String descr) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementPort nep = (A4NetworkElementPort) testContext.getScenarioContext().getContext(Context.A4_NEP);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = a4ResInvServiceMapper.createMinimalLogicalResourceUpdate(NEP);
        lru.setDescription(descr);
        a4ResInvServiceMapper.addCharacteristic(lru, CHAR_OPSTATE, opState);
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(nep.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to update/change (the )NEP description to {string}")
    public void nemoSendsARequestToUpdateNEPDescriptionTo(String descr) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementPort nep = (A4NetworkElementPort) testContext.getScenarioContext().getContext(Context.A4_NEP);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = a4ResInvServiceMapper.createMinimalLogicalResourceUpdate(NEP);
        lru.setDescription(descr);
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(nep.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to update/change (the )NEP operational state to {string}")
    public void nemoSendsARequestToUpdateNEPOperationalStateTo(String opState) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementPort nep = (A4NetworkElementPort) testContext.getScenarioContext().getContext(Context.A4_NEP);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = a4ResInvServiceMapper.createMinimalLogicalResourceUpdate(NEP);
        a4ResInvServiceMapper.addCharacteristic(lru, CHAR_OPSTATE, opState);
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(nep.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to update NEP without operationalState nor description")
    public void nemoSendsARequestToUpdateNEPWithoutOperationalStateNorDescription() {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementPort nep = (A4NetworkElementPort) testContext.getScenarioContext().getContext(Context.A4_NEP);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = a4ResInvServiceMapper.createMinimalLogicalResourceUpdate(NEP);
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(nep.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to change/update (the )NEL operationalState to {string}")
    public void nemoSendsARequestToChangeNELOperationalStateTo(String ops) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementLink nel = (A4NetworkElementLink) testContext.getScenarioContext().getContext(Context.A4_NEL);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = a4ResInvServiceMapper.createMinimalLogicalResourceUpdate(NEL);
        a4ResInvServiceMapper.addCharacteristic(lru, CHAR_OPSTATE, ops);
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(nel.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a delete TP request( to A4 resource inventory service)")
    public void whenNemoSendsADeleteTPRequest() {
        // INPUT FROM SCENARIO CONTEXT
        final A4TerminationPoint tp = (A4TerminationPoint) testContext.getScenarioContext().getContext(Context.A4_TP);

        // ACTION
        Response response = a4ResInvService.deleteLogicalResource(tp.getUuid());

        // Add a bit of waiting time here, to give process the chance to complete (because of async callbacks etc.)
        sleepForSeconds(SLEEP_TIMER);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a create TP request with type {string}")
    public void whenNemoSendsACreateTPRequestWithType(String tpType) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementPort nep = (A4NetworkElementPort) testContext.getScenarioContext().getContext(Context.A4_NEP);

        // ACTION
        A4TerminationPoint tp = testContext.getOsrTestContext().getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointFtthAccess);
        tp.setSubType(tpType);
        Response response = a4ResInvService.createTerminationPoint(tp, nep);

        // Add a bit of waiting time here, to give process the chance to complete (because of async callbacks etc.)
        sleepForSeconds(SLEEP_TIMER);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_TP, tp);
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to change/update (the )NSP L2BSA operationalState to {string}")
    public void whenNemoSendsOperationalStateUpdateForNspL2Bsa(String newOperationalState) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkServiceProfileL2Bsa nspL2 = (A4NetworkServiceProfileL2Bsa) testContext.getScenarioContext().getContext(Context.A4_NSP_L2BSA);
        final A4TerminationPoint tp = (A4TerminationPoint) testContext.getScenarioContext().getContext(Context.A4_TP);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = a4ResInvServiceMapper.createMinimalLogicalResourceUpdate(NSP_L2BSA);
        a4ResInvServiceMapper.addCharacteristic(lru, CHAR_OPSTATE, newOperationalState);
        final Response response = a4ResInvService.sendStatusUpdateForNetworkServiceProfileL2BsaWithoutChecks(nspL2, tp, newOperationalState);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to change/update (the )NSP L2BSA without operationalState")
    public void whenNemoSendsOperationalStateUpdateForNspL2Bsa() {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkServiceProfileL2Bsa nspL2 = (A4NetworkServiceProfileL2Bsa) testContext.getScenarioContext().getContext(Context.A4_NSP_L2BSA);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = a4ResInvServiceMapper.createMinimalLogicalResourceUpdate(NSP_L2BSA);
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(nspL2.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

}
