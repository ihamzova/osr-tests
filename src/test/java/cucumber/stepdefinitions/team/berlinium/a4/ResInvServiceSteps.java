package cucumber.stepdefinitions.team.berlinium.a4;

import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryMapper;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.*;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.LogicalResourceUpdate;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.time.OffsetDateTime;
import java.util.Map;

import static com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryServiceMapper.*;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;

public class ResInvServiceSteps {

    final int SLEEP_TIMER = 5; // in seconds
    private static final String DESCRIPTION = "description";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String ROLES = "roles";
    private static final String LC_STATE = "lifecycleState";
    private static final String ADMIN_STATE = "administrativeState";
    private static final String SPEC_VERSION = "specificationVersion";
    private static final String ADDRESS = "address";
    private static final String KLS_ID = "klsId";
    private static final String FIBER_ON_LOC_ID = "fiberOnLocationId";
    private static final String PLANNED_RACK_ID = "plannedRackId";
    private static final String PLANNED_RACK_POS = "plannedRackPosition";
    private static final String PLANNED_DEV_NAME = "plannedDeviceName";
    private static final String PLANNED_MAT_NUM = "plannedMatNumber";
    private static final String NEG_UUID = "networkElementGroupUuid";

    private final A4ResourceInventoryServiceRobot a4ResInvService;
    private final A4ResourceInventoryMapper a4ResInvMapper;
    private final TestContext testContext;

    public ResInvServiceSteps(TestContext testContext,
                              A4ResourceInventoryServiceRobot a4ResInvService,
                              A4ResourceInventoryMapper a4ResInvMapper) {
        this.testContext = testContext;
        this.a4ResInvService = a4ResInvService;
        this.a4ResInvMapper = a4ResInvMapper;
    }


    // -----=====[ WHENS ]=====-----

    @When("NEMO sends a request to change/update (the )NEG operationalState to {string}")
    public void nemoSendsARequestToChangeNEGOperationalStateTo(String ops) {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkElementGroupDto neg = (NetworkElementGroupDto) testContext.getScenarioContext().getContext(Context.A4_NEG);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = new LogicalResourceUpdate();
        addCharacteristic(lru, OP_STATE, ops);
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(neg.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to change/update (the )NEG without operationalState( characteristic)")
    public void whenNemoSendsARequestToUpdateNEGWithoutOperationalState() {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkElementGroupDto neg = (NetworkElementGroupDto) testContext.getScenarioContext().getContext(Context.A4_NEG);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = new LogicalResourceUpdate();
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(neg.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to update the NEG's following properties to:")
    public void whenNemoSendsARequestToUpdateNEGSFollowingPropertiesTo(DataTable table) {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkElementGroupDto neg = (NetworkElementGroupDto) testContext.getScenarioContext().getContext(Context.A4_NEG);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        final Map<String, String> negMap = table.asMap();
        LogicalResourceUpdate lru = new LogicalResourceUpdate();
        lru.setAtType(NEG);

        if (negMap.containsKey(DESCRIPTION))
            lru.setDescription(negMap.get(DESCRIPTION));

        if (negMap.containsKey(NAME))
            lru.setName(negMap.get(NAME));

        if (negMap.containsKey(LC_STATE))
            lru.setLifecycleState(negMap.get(LC_STATE));

        if (negMap.containsKey(SPEC_VERSION))
            lru.setVersion(negMap.get(SPEC_VERSION));

        if (negMap.containsKey(TYPE))
            addCharacteristic(lru, TYPE_NAME, negMap.get(TYPE));

        if (negMap.containsKey(OP_STATE))
            addCharacteristic(lru, OP_STATE, negMap.get(OP_STATE));

        if (negMap.containsKey(CENTRAL_NET_OPERATOR))
            addCharacteristic(lru, CENTRAL_NET_OPERATOR, negMap.get(CENTRAL_NET_OPERATOR));

        if (negMap.containsKey(CREATION_TIME))
            addCharacteristic(lru, CREATION_TIME, negMap.get(CREATION_TIME));

        if (negMap.containsKey(UPDATE_TIME))
            addCharacteristic(lru, UPDATE_TIME, negMap.get(UPDATE_TIME));

        if (negMap.containsKey(SYNC_TIME))
            addCharacteristic(lru, SYNC_TIME, negMap.get(SYNC_TIME));

        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(neg.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to update the NE's following properties to:")
    public void whenNemoSendsARequestToUpdateNEsFollowingPropertiesTo(DataTable table) {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkElementDto ne = (NetworkElementDto) testContext.getScenarioContext().getContext(Context.A4_NE);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        final Map<String, String> neMap = table.asMap();
        LogicalResourceUpdate lru = new LogicalResourceUpdate();
        lru.setAtType(NE);

        if (neMap.containsKey(DESCRIPTION))
            lru.setDescription(neMap.get(DESCRIPTION));

        if (neMap.containsKey(LC_STATE))
            lru.setLifecycleState(neMap.get(LC_STATE));

        if (neMap.containsKey(SPEC_VERSION))
            lru.setVersion(neMap.get(SPEC_VERSION));

        if (neMap.containsKey(TYPE))
            addCharacteristic(lru, TYPE_NAME, neMap.get(TYPE));

        if (neMap.containsKey(ROLES))
            addCharacteristic(lru, ROLES, neMap.get(ROLES));

        if (neMap.containsKey(OP_STATE))
            addCharacteristic(lru, OP_STATE, neMap.get(OP_STATE));

        if (neMap.containsKey(ADMIN_STATE))
            addCharacteristic(lru, ADMIN_STATE, neMap.get(ADMIN_STATE));

        if (neMap.containsKey(KLS_ID))
            addCharacteristic(lru, KLS_ID, neMap.get(KLS_ID));

        if (neMap.containsKey(FIBER_ON_LOC_ID))
            addCharacteristic(lru, FIBER_ON_LOC_ID, neMap.get(FIBER_ON_LOC_ID));

        if (neMap.containsKey(ADDRESS))
            addCharacteristic(lru, ADDRESS, neMap.get(ADDRESS));

        if (neMap.containsKey(PLANNED_RACK_ID))
            addCharacteristic(lru, PLANNED_RACK_ID, neMap.get(PLANNED_RACK_ID));

        if (neMap.containsKey(PLANNED_RACK_POS))
            addCharacteristic(lru, PLANNED_RACK_POS, neMap.get(PLANNED_RACK_POS));

        if (neMap.containsKey(PLANNED_DEV_NAME))
            addCharacteristic(lru, PLANNED_DEV_NAME, neMap.get(PLANNED_DEV_NAME));

        if (neMap.containsKey(PLANNED_MAT_NUM))
            addCharacteristic(lru, PLANNED_MAT_NUM, neMap.get(PLANNED_MAT_NUM));

        if (neMap.containsKey(CREATION_TIME))
            addCharacteristic(lru, CREATION_TIME, neMap.get(CREATION_TIME));

        if (neMap.containsKey(UPDATE_TIME))
            addCharacteristic(lru, UPDATE_TIME, neMap.get(UPDATE_TIME));

        if (neMap.containsKey(SYNC_TIME))
            addCharacteristic(lru, SYNC_TIME, neMap.get(SYNC_TIME));

        if (neMap.containsKey(NEG_UUID))
            addResourceRelationship(lru, NEG, neMap.get(NEG_UUID));

        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(ne.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to change/update (the )NE operationalState to {string}")
    public void nemoSendsARequestToChangeNEOperationalStateTo(String ops) {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkElementDto ne = (NetworkElementDto) testContext.getScenarioContext().getContext(Context.A4_NE);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = new LogicalResourceUpdate();
        addCharacteristic(lru, OP_STATE, ops);
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(ne.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to change/update (the )NE without operationalState( characteristic)")
    public void whenNemoSendsARequestToUpdateNeWithoutOperationalState() {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkElementDto ne = (NetworkElementDto) testContext.getScenarioContext().getContext(Context.A4_NE);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = new LogicalResourceUpdate();
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(ne.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to change/update (the )NEP operationalState to {string} and description to {string}")
    public void whenNemoSendsARequestToUpdateNEPOperationalStateToAndDescriptionTo(String opState, String descr) {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkElementPortDto nep = (NetworkElementPortDto) testContext.getScenarioContext().getContext(Context.A4_NEP);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = new LogicalResourceUpdate();
        lru.setDescription(descr);
        addCharacteristic(lru, OP_STATE, opState);
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(nep.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to update/change (the )NEP description to {string}")
    public void whenNemoSendsARequestToUpdateNEPDescriptionTo(String descr) {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkElementPortDto nep = (NetworkElementPortDto) testContext.getScenarioContext().getContext(Context.A4_NEP);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = new LogicalResourceUpdate();
        lru.setDescription(descr);
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(nep.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to update/change (the )NEP operational state to {string}")
    public void whenNemoSendsARequestToUpdateNEPOperationalStateTo(String opState) {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkElementPortDto nep = (NetworkElementPortDto) testContext.getScenarioContext().getContext(Context.A4_NEP);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = new LogicalResourceUpdate();
        addCharacteristic(lru, OP_STATE, opState);
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(nep.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to update NEP without operationalState nor description")
    public void whenNemoSendsARequestToUpdateNEPWithoutOperationalStateNorDescription() {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkElementPortDto nep = (NetworkElementPortDto) testContext.getScenarioContext().getContext(Context.A4_NEP);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = new LogicalResourceUpdate();
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(nep.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to change/update (the )NEL operationalState to {string}")
    public void whenNemoSendsARequestToUpdateNELOperationalStateTo(String ops) {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkElementLinkDto nel = (NetworkElementLinkDto) testContext.getScenarioContext().getContext(Context.A4_NEL);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = new LogicalResourceUpdate();
        addCharacteristic(lru, OP_STATE, ops);
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(nel.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to change/update (the )NEL without operationalState( characteristic)")
    public void whenNemoSendsARequestToUpdateNelWithoutOperationalState() {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkElementLinkDto nel = (NetworkElementLinkDto) testContext.getScenarioContext().getContext(Context.A4_NEL);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = new LogicalResourceUpdate();
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(nel.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a delete TP request( to A4 resource inventory service)")
    public void whenNemoSendsADeleteTPRequest() {
        // INPUT FROM SCENARIO CONTEXT
        final TerminationPointDto tp = (TerminationPointDto) testContext.getScenarioContext().getContext(Context.A4_TP);

        // ACTION
        Response response = a4ResInvService.deleteLogicalResource(tp.getUuid());

        // Add a bit of waiting time here, to give process the chance to complete (because of async callbacks etc.)
        sleepForSeconds(SLEEP_TIMER);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a create TP request with type {string}")
    public void whenNemoSendsACreateTPRequestWithType(String tpType) {
        final NetworkElementPortDto nep = (NetworkElementPortDto) testContext.getScenarioContext().getContext(Context.A4_NEP);

        TerminationPointDto tp = a4ResInvMapper.getDefaultTerminationPointData();
        tp.setType(tpType);

        Response response = a4ResInvService.createTerminationPoint(tp, nep.getUuid());

        // Add a bit of waiting time here, to give process the chance to complete (because of async callbacks etc.)
        sleepForSeconds(SLEEP_TIMER);

        testContext.getScenarioContext().setContext(Context.A4_TP, tp);
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to change/update (the )NSP FTTH-Access operationalState to {string} and NEP reference to {string}")
    public void whenNemoSendsARequestToUpdateNspFtthAccessOperationalStateToAndNepReferenceTo(String opState, String portUuid) {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkServiceProfileFtthAccessDto nspFtthAccess = (NetworkServiceProfileFtthAccessDto) testContext
                .getScenarioContext().getContext(Context.A4_NSP_FTTH);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = new LogicalResourceUpdate();
        addCharacteristic(lru, OP_STATE, opState);
        addResourceRelationship(lru, NEP, portUuid);
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(nspFtthAccess.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to update/change (the )NSP FTTH-Access operational state to {string}")
    public void whenNemoSendsARequestToUpdateNspFtthAccessOperationalStateTo(String opState) {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkServiceProfileFtthAccessDto nspFtthAccess = (NetworkServiceProfileFtthAccessDto) testContext
                .getScenarioContext().getContext(Context.A4_NSP_FTTH);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = new LogicalResourceUpdate();
        addCharacteristic(lru, OP_STATE, opState);
        final Response response = a4ResInvService
                .sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(nspFtthAccess.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to update/change (the )NSP FTTH-Access NEP reference to {string}")
    public void whenNemoSendsARequestToUpdateNspFtthAccessNepReferenceTo(String portUuid) {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkServiceProfileFtthAccessDto nspFtthAccess = (NetworkServiceProfileFtthAccessDto) testContext
                .getScenarioContext().getContext(Context.A4_NSP_FTTH);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = new LogicalResourceUpdate();
        addResourceRelationship(lru, NEP, portUuid);
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(nspFtthAccess.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to update NSP FTTH-Access without operationalState nor NEP reference")
    public void whenNemoSendsARequestToUpdateNspFtthAccessWithoutOperationalStateNorNepReference() {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkServiceProfileFtthAccessDto nspFtthAccess = (NetworkServiceProfileFtthAccessDto) testContext
                .getScenarioContext().getContext(Context.A4_NSP_FTTH);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = new LogicalResourceUpdate();
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(nspFtthAccess.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }


    @When("NEMO sends a request to change/update (the )NSP L2BSA operationalState to {string}")
    public void whenNemoSendsOperationalStateUpdateForNspL2Bsa(String newOperationalState) {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkServiceProfileL2BsaDto nspL2 = (NetworkServiceProfileL2BsaDto) testContext.getScenarioContext().getContext(Context.A4_NSP_L2BSA);
        final TerminationPointDto tp = (TerminationPointDto) testContext.getScenarioContext().getContext(Context.A4_TP);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = new LogicalResourceUpdate();
        addCharacteristic(lru, OP_STATE, newOperationalState);
        final Response response = a4ResInvService.sendStatusUpdateForNetworkServiceProfileL2BsaWithoutChecks(nspL2, tp, newOperationalState);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to change/update (the )NSP L2BSA without operationalState")
    public void whenNemoSendsOperationalStateUpdateForNspL2Bsa() {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkServiceProfileL2BsaDto nspL2 = (NetworkServiceProfileL2BsaDto) testContext.getScenarioContext().getContext(Context.A4_NSP_L2BSA);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = new LogicalResourceUpdate();
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(nspL2.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to change/update (the )NSP A10NSP operationalState to {string}")
    public void whenNemoSendsOperationalStateUpdateForNspA10Nsp(String newOperationalState) {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkServiceProfileA10NspDto nspA10nsp = (NetworkServiceProfileA10NspDto) testContext
                .getScenarioContext().getContext(Context.A4_NSP_A10NSP);
        final TerminationPointDto tp = (TerminationPointDto) testContext.getScenarioContext().getContext(Context.A4_TP);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = new LogicalResourceUpdate();
        addCharacteristic(lru, OP_STATE, newOperationalState);
        final Response response = a4ResInvService
                .sendStatusUpdateForNetworkServiceProfileA10NspWithoutChecks(nspA10nsp, tp, newOperationalState);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to change/update (the )NSP A10NSP without operationalState")
    public void whenNemoSendsOperationalStateUpdateForNspA10Nsp() {
        // INPUT FROM SCENARIO CONTEXT
        final NetworkServiceProfileA10NspDto nspA10nsp = (NetworkServiceProfileA10NspDto) testContext
                .getScenarioContext().getContext(Context.A4_NSP_A10NSP);

        // ACTION

        // Datetime has to be put into scenario context _before_ the actual request happens
        testContext.getScenarioContext().setContext(Context.TIMESTAMP, OffsetDateTime.now());

        LogicalResourceUpdate lru = new LogicalResourceUpdate();
        final Response response = a4ResInvService.sendMinimalStatusUpdateAsLogicalResourceWithoutChecks(nspA10nsp.getUuid(), lru);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

}
