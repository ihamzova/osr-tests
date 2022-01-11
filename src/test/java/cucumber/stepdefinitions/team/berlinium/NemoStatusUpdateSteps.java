package cucumber.stepdefinitions.team.berlinium;

import com.fasterxml.jackson.core.JsonProcessingException;
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

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;

@ServiceLog({A4_RESOURCE_INVENTORY_MS,A4_RESOURCE_INVENTORY_SERVICE_MS,A4_CARRIER_MANAGEMENT_MS,A4_NEMO_UPDATER_MS})
public class NemoStatusUpdateSteps extends BaseSteps {

    private final A4ResourceInventoryServiceRobot nemo = new A4ResourceInventoryServiceRobot();

    public NemoStatusUpdateSteps(TestContext testContext) {
        super(testContext);

    }

    // -----=====[ WHENS ]=====-----
    @When("NEMO sends a request to change NSP L2BSA operationalState to {string}")
    public void whenNemoSendsStatusUpdateForNetworkServiceProfileL2Bsa_operationalState(String newOperationalState) throws JsonProcessingException {
        A4NetworkServiceProfileL2Bsa nspL2Data = (A4NetworkServiceProfileL2Bsa) getScenarioContext().getContext(Context.A4_NSP_L2BSA);
        A4TerminationPoint tpL2BsaData= (A4TerminationPoint) getScenarioContext().getContext(Context.A4_TP);

       Response response = nemo.sendStatusUpdateForNetworkServiceProfileL2BsaWithoutChecks(nspL2Data, tpL2BsaData, newOperationalState);
       getScenarioContext().setContext(Context.RESPONSE, response);
       //NetworkServiceProfileL2BsaDto networkServiceProfileL2BsaDto = getExistingNetworkServiceProfileL2Bsa(nspL2Data.getUuid());

        String body= response.getBody().asString();


       System.out.println("-----------------------------------------------------------");
       System.out.println(body);
        System.out.println("-----------------------------------------------------------");
        LogicalResource logicalResource = om.readValue(body, LogicalResource.class);
       System.out.println(logicalResource);
        System.out.println("-----------------------------------------------------------");
    }

    // -----=====[ GIVENS ]=====-----


    // -----=====[ THENS ]=====-----

    // -----=====[ HELPERS ]=====-----
}
