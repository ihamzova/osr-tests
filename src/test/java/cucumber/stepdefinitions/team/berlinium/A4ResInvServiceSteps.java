package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkServiceProfileL2Bsa;
import com.tsystems.tm.acc.ta.data.osr.models.A4TerminationPoint;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.LogicalResource;
import cucumber.BaseSteps;
import cucumber.Context;
import cucumber.TestContext;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_SERVICE_MS;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;

@ServiceLog({A4_RESOURCE_INVENTORY_SERVICE_MS})
public class A4ResInvServiceSteps extends BaseSteps {

    final int SLEEP_TIMER = 5;
    private final A4ResourceInventoryServiceRobot a4ResInvService = new A4ResourceInventoryServiceRobot();

    public A4ResInvServiceSteps(TestContext testContext) {
        super(testContext);
    }

    // -----=====[ WHENS ]=====-----

    @When("NEMO sends a delete TP request( to A4 resource inventory service)")
    public void whenNemoSendsADeleteTPRequest() {
        // INPUT FROM SCENARIO CONTEXT
        final A4TerminationPoint tp = (A4TerminationPoint) getScenarioContext().getContext(Context.A4_TP);

        // ACTION
        Response response = a4ResInvService.deleteLogicalResource(tp.getUuid());

        // Add a bit of waiting time here, to give process the chance to complete (because of async callbacks etc.)
        sleepForSeconds(SLEEP_TIMER);

        // OUTPUT INTO SCENARIO CONTEXT
        getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a create TP request with type {string}")
    public void whenNemoSendsACreateTPRequestWithType(String tpType) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkElementPort nep = (A4NetworkElementPort) getScenarioContext().getContext(Context.A4_NEP);

        // ACTION
        A4TerminationPoint tp = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointFtthAccess);
        tp.setSubType(tpType);
        Response response = a4ResInvService.createTerminationPoint(tp, nep);

        // Add a bit of waiting time here, to give process the chance to complete (because of async callbacks etc.)
        sleepForSeconds(SLEEP_TIMER);

        // OUTPUT INTO SCENARIO CONTEXT
        getScenarioContext().setContext(Context.A4_TP, tp);
        getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("NEMO sends a request to change NSP L2BSA operationalState to {string}")
    public void whenNemoSendsOperationalStateUpdateForNspL2Bsa(String newOperationalState) {
        // INPUT FROM SCENARIO CONTEXT
        final A4NetworkServiceProfileL2Bsa nspL2 = (A4NetworkServiceProfileL2Bsa) getScenarioContext().getContext(Context.A4_NSP_L2BSA);
        final A4TerminationPoint tp = (A4TerminationPoint) getScenarioContext().getContext(Context.A4_TP);

        // ACTION
        final Response response = a4ResInvService.sendStatusUpdateForNetworkServiceProfileL2BsaWithoutChecks(nspL2, tp, newOperationalState);
        final String body = response.getBody().asString();
        final LogicalResource lr = a4ResInvService.getLogicalResourceObjectFromJsonString(body);
        final A4NetworkServiceProfileL2Bsa resultNspL2 = mapLrToA4NspL2Bsa(lr);

        // OUTPUT INTO SCENARIO CONTEXT
        getScenarioContext().setContext(Context.RESPONSE, response);
        getScenarioContext().setContext(Context.A4_NSP_L2BSA, resultNspL2);
    }

    // -----=====[ HELPERS ]=====-----

    private A4NetworkServiceProfileL2Bsa mapLrToA4NspL2Bsa(LogicalResource lr) {
        final String RESCHAR_KEY_OPSTATE = "operationalState";
        final String RESCHAR_KEY_ADMMODE = "administrativeMode";
        final String RESCHAR_KEY_LINEID = "lineId";
        final String RESCHAR_KEY_L2CCID = "l2CcId";

        A4NetworkServiceProfileL2Bsa nspL2 = new A4NetworkServiceProfileL2Bsa();
        nspL2.setUuid(lr.getId());
        nspL2.setLifecycleState(lr.getLifecycleState());
        nspL2.setOperationalState(a4ResInvService.getValueFromCharacteristic(RESCHAR_KEY_OPSTATE, lr));
        nspL2.setAdministrativeMode(a4ResInvService.getValueFromCharacteristic(RESCHAR_KEY_ADMMODE, lr));
        nspL2.setLineId(a4ResInvService.getValueFromCharacteristic(RESCHAR_KEY_LINEID, lr));
        nspL2.setL2CcId(a4ResInvService.getValueFromCharacteristic(RESCHAR_KEY_L2CCID, lr));
//        nspL2.setDataRateDown(xx);
//        nspL2.setDataRateUp(xx);

        return nspL2;
    }

}
