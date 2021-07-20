package com.tsystems.tm.acc.ta.robot.osr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.A4ResourceOrderClient;
import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceOrderMapper;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementLink;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.model.*;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_CREATED_201;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_QUEUE_DISPATCHER_MS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

@Slf4j
public class A4ResourceOrderRobot {

    public final static String cbPath = "/test_url";
    private final String cbUrl = "https://wiremock-acc-app-berlinium-03.priv.cl01.gigadev.telekom.de" + cbPath; // Wiremock for merlin MS // TODO dynamically get env name

    private static final AuthTokenProvider authTokenProvider =
            new RhssoClientFlowAuthTokenProvider(A4_QUEUE_DISPATCHER_MS,
                    RhssoHelper.getSecretOfGigabitHub(A4_QUEUE_DISPATCHER_MS));

    private final ApiClient a4ResourceOrder = new A4ResourceOrderClient(authTokenProvider).getClient();

    private final A4ResourceOrderMapper resourceOrderMapper = new A4ResourceOrderMapper();

    @Step("Send POST for A10nsp Resource Order")
    public void sendPostResourceOrder(ResourceOrder resourceOrder) {
        final String correlationId = UUID.randomUUID().toString();

        a4ResourceOrder
                .resourceOrder()
                .createResourceOrder()
                .xCallbackCorrelationIdHeader(correlationId)
                .xCallbackUrlHeader(cbUrl)
                .body(resourceOrder)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    public ResourceOrder buildResourceOrder() {
        return resourceOrderMapper.buildResourceOrder();
    }

    public void addOrderItemAdd(String orderItemId, A4NetworkElementLink nelData, ResourceOrder ro) {
        addOrderItem(orderItemId, OrderItemActionType.ADD, nelData, ro);
    }

    public void addOrderItemModify(String orderItemId, A4NetworkElementLink nelData, ResourceOrder ro) {
        addOrderItem(orderItemId, OrderItemActionType.MODIFY, nelData, ro);
    }

    public void addOrderItemDelete(String orderItemId, A4NetworkElementLink nelData, ResourceOrder ro) {
        addOrderItem(orderItemId, OrderItemActionType.DELETE, nelData, ro);
    }

    public void addOrderItem(String orderItemId, OrderItemActionType actionType, A4NetworkElementLink nelData, ResourceOrder ro) {
        ResourceRefOrValue resource = new ResourceRefOrValue()
                .name(nelData.getLbz())
                .resourceCharacteristic(resourceOrderMapper.buildResourceCharacteristicList());

        ResourceOrderItem orderItem = new ResourceOrderItem()
                .action(actionType)
                .resource(resource)
                .id(orderItemId);

        ro.addOrderItemItem(orderItem);
    }

    public void setResourceName(String name, String orderItemId, ResourceOrder ro) {
        ResourceOrderItem roi = getResourceOrderItemByOrderItemId(orderItemId, ro);
        Objects.requireNonNull(roi.getResource()).setName(name);
    }

    public void setCharacteristicValue(String name, Object value, String orderItemId, ResourceOrder ro) {
        ResourceOrderItem roi = getResourceOrderItemByOrderItemId(orderItemId, ro);
        Characteristic c = getCharacteristic(name, roi);
        c.setValue(value);
    }

    public Characteristic getCharacteristic(String name, ResourceOrderItem roi) {
        List<Characteristic> rcList = Objects.requireNonNull(roi.getResource()).getResourceCharacteristic();

        if (rcList != null) {
            for (Characteristic characteristic : rcList) {
                if (characteristic.getName().equals(name)) {
                    return characteristic;
                }
            }
        }

        return null;
    }

    public void removeCharacteristic(String chName, String orderItemId, ResourceOrder ro) {
        List<Characteristic> rcList = Objects.requireNonNull(getResourceOrderItemByOrderItemId(orderItemId, ro).getResource()).getResourceCharacteristic();

        if (rcList != null) {
            rcList.removeIf(characteristic -> characteristic.getName().equals(chName));
        }
    }

    public ResourceOrderItem getResourceOrderItemByOrderItemId(String orderItemId, ResourceOrder ro) {
        List<ResourceOrderItem> roiList = ro.getOrderItem();

        if (roiList != null) {
            for (ResourceOrderItem resourceOrderItem : roiList) {
                if (resourceOrderItem.getId().equals(orderItemId))
                    return resourceOrderItem;
            }
        }

        return null;
    }

    private ResourceOrder getResourceOrderFromCallback() {
        final ObjectMapper objectMapper = new ObjectMapper();

        List<LoggedRequest> ergList = WireMockFactory.get()
                .retrieve(
                        newRequestPattern(
                                RequestMethod.fromString("POST"),
                                urlPathEqualTo(cbPath)));

        String response = ergList.get(0).getBodyAsString();

        try {
            return objectMapper.readValue(response, ResourceOrder.class);
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }

        return null;
    }

    public void checkResourceOrderIsCompleted() {
        checkResourceOrderHasState(ResourceOrderStateType.COMPLETED);
    }

    public void checkResourceOrderIsRejected() {
        checkResourceOrderHasState(ResourceOrderStateType.REJECTED);
    }

    private void checkResourceOrderHasState(ResourceOrderStateType state) {
        ResourceOrder roFromCb = getResourceOrderFromCallback();
        if (roFromCb != null)
            assertEquals(roFromCb.getState(), state);
        else
            fail("No callback resource order to check");
    }

    public void checkResourceOrderItemIsCompleted(String orderItemId) {
        checkResourceOrderItemHasState(orderItemId, ResourceOrderItemStateType.COMPLETED);
    }

    public void checkResourceOrderItemIsRejected(String orderItemId) {
        checkResourceOrderItemHasState(orderItemId, ResourceOrderItemStateType.REJECTED);
    }

    private void checkResourceOrderItemHasState(String orderItemId, ResourceOrderItemStateType state) {
        ResourceOrder roFromCb = getResourceOrderFromCallback();

        if (roFromCb != null) {
            ResourceOrderItem roi = getResourceOrderItemByOrderItemId(orderItemId, roFromCb);
            assertEquals(roi.getState(), state);
        } else
            fail("No callback resource order to check");
    }

}
