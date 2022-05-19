package cucumber.stepdefinitions.team.berlinium;

import com.tsystems.tm.acc.ta.robot.osr.A4ResourceOrderRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementLinkDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.model.ResourceOrderDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.model.ResourceOrderItemDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.tmf652.client.model.OrderItemActionType;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.tmf652.client.model.ResourceOrder;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.tmf652.client.model.OrderItemActionType.ADD;
import static org.testng.Assert.*;

public class A4ResourceOrderSteps {

    final A4ResourceOrderRobot resOrder = new A4ResourceOrderRobot();
    private final TestContext testContext;

    public A4ResourceOrderSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    @After
    public void cleanup() {
        final boolean RO_PRESENT = testContext.getScenarioContext().isContains(Context.A4_RESOURCE_ORDER);
        if (RO_PRESENT) {
            final String roId = (String) testContext.getScenarioContext().getContext(Context.A4_RESOURCE_ORDER);
            resOrder.deleteA4TestDataRecursively(roId);
        }
    }


    // -----=====[ GIVENs ]=====-----

    @Given("a prepared resource order with the following order items:")
    public void aPreparedResourceOrderWithTheFollowingOrderItems(DataTable orderItems) {
        final List<Map<String, String>> rows = orderItems.asMaps(String.class, String.class);
        ResourceOrder ro = resOrder.buildResourceOrder();

        rows.forEach(r -> {
            final NetworkElementLinkDto nel = (NetworkElementLinkDto) testContext.getScenarioContext().getContext(Context.A4_NEL, r.get("NEL Reference"));
            final OrderItemActionType actionType = OrderItemActionType.valueOf(r.get("Action Type"));
            resOrder.addOrderItem(UUID.randomUUID().toString(), actionType, nel.getLbz(), ro);
        });

        testContext.getScenarioContext().setContext(Context.A4_RESOURCE_ORDER, ro);
    }


    // -----=====[ WHENs ]=====-----

    @When("(CAD@)Sputnik sends a resource order with the following order items:")
    public void whenCadSputnikSendsAResourceOrder(DataTable orderItems) {
        final List<Map<String, String>> rows = orderItems.asMaps(String.class, String.class);
        ResourceOrder ro = resOrder.buildResourceOrder();

        rows.forEach(r -> {
            final NetworkElementLinkDto nel = (NetworkElementLinkDto) testContext.getScenarioContext().getContext(Context.A4_NEL, r.get("NEL Reference"));
            final OrderItemActionType actionType = OrderItemActionType.valueOf(r.get("Action Type"));
            resOrder.addOrderItem(UUID.randomUUID().toString(), actionType, nel.getLbz(), ro);
        });

        testContext.getScenarioContext().setContext(Context.A4_RESOURCE_ORDER, ro);

        // ACTION
        final Response response = resOrder.sendPostResourceOrderWithoutChecks(ro);
        sleepForSeconds(7);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }

    @When("(CAD@)Sputnik sends the prepared resource order")
    public void whenCadSputnikSendsAResourceOrder() {
        // INPUT FROM SCENARIO CONTEXT
        ResourceOrder ro = (ResourceOrder) testContext.getScenarioContext().getContext(Context.A4_RESOURCE_ORDER);

        // ACTION
        final Response response = resOrder.sendPostResourceOrderWithoutChecks(ro);
        final String roId = response.getBody().asString();

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
        testContext.getScenarioContext().setContext(Context.A4_RESOURCE_ORDER_ID, roId);
    }

    @When("(CAD@)Sputnik sends a resource order with filled resource order ID")
    public void whenCadSputnikSendsAResourceOrderWithFilledResourceOrderID() {
        final String roId = UUID.randomUUID().toString();
        ResourceOrder ro = resOrder.buildResourceOrder();
        ro.setId(roId);
        resOrder.addOrderItem(UUID.randomUUID().toString(), ADD, "anyLbz", ro);

        final Response response = resOrder.sendPostResourceOrderWithoutChecks(ro);

        // OUTPUT INTO SCENARIO CONTEXT
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);
    }


    // -----=====[ THENs ]=====-----

    @Then("the response contains a resource order ID")
    public void thenTheResponseContainsAResourceOrderID() {
        final Response response = (Response) testContext.getScenarioContext().getContext(Context.RESPONSE);
        ResourceOrder ro = (ResourceOrder) testContext.getScenarioContext().getContext(Context.A4_RESOURCE_ORDER);

        final String body = response.getBody().asString();

        try {
            final UUID roId = UUID.fromString(body);
            ro.setId(roId.toString());
            testContext.getScenarioContext().setContext(Context.A4_RESOURCE_ORDER, ro);
        } catch (IllegalArgumentException e) {
            fail("Response is not a uuid, but is instead: " + body);
        }
    }

    @Then("the resource order is saved in RO database")
    public void thenTheResourceOrderIsSavedInRODatabase() {
        // INPUT FROM SCENARIO CONTEXT
        final ResourceOrder ro = (ResourceOrder) testContext.getScenarioContext().getContext(Context.A4_RESOURCE_ORDER);

        // ACTION
        ResourceOrderDto roDb = resOrder.getResourceOrderFromDb(ro.getId());
        assertNotNull(roDb, "Resource order is not in DB!");
    }

    @Then("the resource order is not saved in RO database")
    public void thenTheResourceOrderIsNotSavedInRODatabase() {
        // INPUT FROM SCENARIO CONTEXT
        final ResourceOrder ro = (ResourceOrder) testContext.getScenarioContext().getContext(Context.A4_RESOURCE_ORDER);

        // ACTION
        resOrder.checkResourceOrderDoesntExist(ro.getId());

        // Remove resource order id from context so that cleanup() method doesn't run into error (because resource order doesn't exist in repo)
        testContext.getScenarioContext().deleteContext(Context.A4_RESOURCE_ORDER);
    }

    @Then("the resource order state is {string}")
    public void thenTheResourceOrderStateIs(String roState) {
        // INPUT FROM SCENARIO CONTEXT
        final ResourceOrder ro = (ResourceOrder) testContext.getScenarioContext().getContext(Context.A4_RESOURCE_ORDER);

        // ACTION
        ResourceOrderDto roDb = resOrder.getResourceOrderFromDb(ro.getId());
        assertEquals(roDb.getState(), roState);
    }

    @Then("all order item states are {string}")
    public void thenAllOrderItemStateAre(String roiState) {
        // INPUT FROM SCENARIO CONTEXT
        final ResourceOrder ro = (ResourceOrder) testContext.getScenarioContext().getContext(Context.A4_RESOURCE_ORDER);

        // ACTION
        final ResourceOrderDto roDb = resOrder.getResourceOrderFromDb(ro.getId());
        List<ResourceOrderItemDto> roiList = roDb.getOrderItem();

        assertTrue(roiList != null && !roiList.isEmpty());
        roiList.forEach(roi -> assertEquals(roi.getState(), roiState));
    }

}
