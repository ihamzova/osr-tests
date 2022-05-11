package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.robot.osr.A4ResourceOrderRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementLinkDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.model.ResourceOrderDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.tmf652.client.model.ResourceOrder;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.UUID;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getRandomDigits;
import static com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.tmf652.client.model.OrderItemActionType.ADD;
import static org.testng.Assert.*;

public class A4ResourceOrderSteps {

    final String DEFAULT_ORDER_ITEM_ID = "orderItemId" + getRandomDigits(4);
    final A4ResourceOrderRobot resOrder = new A4ResourceOrderRobot();
    private final TestContext testContext;

    public A4ResourceOrderSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    @After
    public void cleanup() {
        final boolean RO_ID_PRESENT = testContext.getScenarioContext().isContains(Context.A4_RES_ORDER_ID);
        if (RO_ID_PRESENT) {
            final String roId = (String) testContext.getScenarioContext().getContext(Context.A4_RES_ORDER_ID);
            resOrder.deleteA4TestDataRecursively(roId);
        }
    }


    // -----=====[ WHENS ]=====-----

    @When("(CAD@)Sputnik sends a resource order with empty resource order ID")
    public void whenCadSputnikSendsAResourceOrderWithEmptyResourceOrderID() {
        // INPUT FROM SCENARIO CONTEXT
        NetworkElementLinkDto nel = (NetworkElementLinkDto) testContext.getScenarioContext().getContext(Context.A4_NEL);

        // ACTION
        ResourceOrder ro = resOrder.buildResourceOrder();
        ro.setId(null);
        resOrder.addOrderItem(DEFAULT_ORDER_ITEM_ID, ADD, nel.getLbz(), ro);
        final Response response = resOrder.sendPostResourceOrderWithoutChecks(ro);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("(CAD@)Sputnik sends a resource order with filled resource order ID")
    public void whenCadSputnikSendsAResourceOrderWithFilledResourceOrderID() {
        // INPUT FROM SCENARIO CONTEXT
        NetworkElementLinkDto nel = (NetworkElementLinkDto) testContext.getScenarioContext().getContext(Context.A4_NEL);

        // ACTION
        String roId = UUID.randomUUID().toString();
        ResourceOrder ro = resOrder.buildResourceOrder();
        ro.setId(roId);
        resOrder.addOrderItem(DEFAULT_ORDER_ITEM_ID, ADD, nel.getLbz(), ro);
        final Response response = resOrder.sendPostResourceOrderWithoutChecks(ro);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
        testContext.getScenarioContext().setContext(Context.A4_RES_ORDER_ID, roId);
    }


    // -----=====[ THENS ]=====-----

    @Then("the response contains a resource order ID")
    public void thenTheResponseContainsAResourceOrderID() {
        // INPUT FROM SCENARIO CONTEXT
        Response response = (Response) testContext.getScenarioContext().getContext(Context.RESPONSE);

        // ACTION
        final String roId = response.getBody().asString();
        assertNotNull(roId, "Resource order id is null!");
        assertFalse(roId.isEmpty(), "Resource order id is empty!");

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.A4_RES_ORDER_ID, roId);
    }

    @Then("the resource order is saved in RO database")
    public void thenTheResourceOrderIsSavedInRODatabase() {
        // INPUT FROM SCENARIO CONTEXT
        String roId = (String) testContext.getScenarioContext().getContext(Context.A4_RES_ORDER_ID);

        // ACTION
        ResourceOrderDto roDb = resOrder.getResourceOrderFromDb(roId);
        assertNotNull(roDb, "Resource order is not in DB!");
    }

    @Then("the resource order is not saved in RO database")
    public void thenTheResourceOrderIsNotSavedInRODatabase() {
        // INPUT FROM SCENARIO CONTEXT
        String roId = (String) testContext.getScenarioContext().getContext(Context.A4_RES_ORDER_ID);

        // ACTION
        resOrder.checkResourceOrderDoesntExist(roId);

        // Remove resource order id from context so that cleanup() method doesn't run into error (because resource order doesn't exist in repo)
        testContext.getScenarioContext().deleteContext(Context.A4_RES_ORDER_ID);
    }

    @Then("the resource order state is {string}")
    public void thenTheResourceOrderStateIs(String roState) {
        // INPUT FROM SCENARIO CONTEXT
        String roId = (String) testContext.getScenarioContext().getContext(Context.A4_RES_ORDER_ID);

        // ACTION
        ResourceOrderDto roDb = resOrder.getResourceOrderFromDb(roId);
        assertEquals(roDb.getState(), roState);
    }

}
