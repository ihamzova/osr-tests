package cucumber.stepdefinitions.team.berlinium.a4;

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
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.tmf652.client.model.OrderItemActionType.ADD;
import static java.util.stream.Collectors.toList;
import static org.testng.Assert.*;

@Slf4j
public class ResourceOrderSteps {

    private final A4ResourceOrderRobot resOrder;
    private final TestContext testContext;

    public ResourceOrderSteps(TestContext testContext,
                              A4ResourceOrderRobot resOrder) {
        this.testContext = testContext;
        this.resOrder = resOrder;
    }

    @After
    public void cleanup() {
        log.info("Checking if any A4 resource order(s) exists in scenario context...");
        final boolean RO_PRESENT = testContext.getScenarioContext().isContains(Context.A4_RESOURCE_ORDER);
        if (RO_PRESENT) {
            log.info("A4 resource order(s) found! Deleting them...");
            final List<ResourceOrder> roList = testContext.getScenarioContext().getAllContext(Context.A4_RESOURCE_ORDER).stream()
                    .map(ro -> (ResourceOrder) ro)
                    .collect(toList());

            roList.forEach(resOrder::deleteA4TestDataRecursively);
        } else
            log.info("No A4 resource order found. Nothing to do.");
    }


    // -----=====[ WHENs ]=====-----

    @When("(CAD@)Sputnik sends a resource order with the following order items:")
    public void whenCadSputnikSendsAResourceOrder(DataTable orderItems) {
        // Prepare the RO...
        final List<Map<String, String>> rows = orderItems.asMaps(String.class, String.class);
        ResourceOrder ro = resOrder.buildResourceOrder();

        rows.forEach(r -> {
            final NetworkElementLinkDto nel = (NetworkElementLinkDto) testContext.getScenarioContext().getContext(Context.A4_NEL, r.get("NEL Reference"));
            final OrderItemActionType actionType = OrderItemActionType.valueOf(r.get("Action Type"));
            resOrder.addOrderItem(UUID.randomUUID().toString(), actionType, nel.getLbz(), ro);
        });

        // ... and perform the request with the prepared RO
        final Response response = resOrder.sendPostResourceOrderWithoutChecks(ro);
        testContext.getScenarioContext().setContext(Context.RESPONSE, response);

        // Give the long RO process some time to finish
        sleepForSeconds(7);

        // The RO id is known only in the response for the RO request, so we check if the id is valid and add it to the RO in the context
        final String body = response.getBody().asString();
        try {
            final UUID roId = UUID.fromString(body);
            ro.setId(roId.toString());
            testContext.getScenarioContext().setContext(Context.A4_RESOURCE_ORDER, ro);
        } catch (IllegalArgumentException e) {
            fail("Response is not a uuid, but is instead: " + body);
        }
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
        testContext.getScenarioContext().setContext(Context.A4_RESOURCE_ORDER, ro);
    }


    // -----=====[ THENs ]=====-----

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
