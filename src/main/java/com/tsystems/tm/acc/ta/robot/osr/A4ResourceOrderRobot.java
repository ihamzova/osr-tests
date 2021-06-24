package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.A4ResourceOrderClient;
import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceOrderMapper;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementLink;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.model.Characteristic;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.model.OrderItemActionType;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.model.ResourceOrder;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.model.ResourceOrderItem;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_CREATED_201;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_QUEUE_DISPATCHER_MS;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_SERVICE_MS;

@Slf4j
public class A4ResourceOrderRobot {

    private static final AuthTokenProvider authTokenProvider =
            new RhssoClientFlowAuthTokenProvider(A4_QUEUE_DISPATCHER_MS,
                    RhssoHelper.getSecretOfGigabitHub(A4_QUEUE_DISPATCHER_MS));

      private final ApiClient a4ResourceOrder = new A4ResourceOrderClient(authTokenProvider).getClient();

      private final A4ResourceOrderMapper resourceOrderMapper = new A4ResourceOrderMapper();

    @Step("Send POST for A10nsp Resource Order")
    public void sendPostResourceOrder(String reqUrl, String corId, ResourceOrder resourceOrderCreate) {

        System.out.println("+++ reqUrl: "+reqUrl);
        System.out.println("+++ corId: "+corId);
        System.out.println("+++ übergebene Order: "+resourceOrderCreate);

        a4ResourceOrder
                .resourceOrder()
                .createResourceOrder()
                .xCallbackCorrelationIdHeader(corId)
                .xCallbackUrlHeader(reqUrl)
                .body(resourceOrderCreate)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    public ResourceOrder buildResourceOrder(A4NetworkElementLink nelData) {
        return resourceOrderMapper.buildResourceOrder(nelData);
    }

    public void setResourceName(String name, ResourceOrder ro) {
        ResourceOrderItem roi = getResourceOrderItemOrderItemId(ro);
        Objects.requireNonNull(roi.getResource()).setName(name);
    }

    public void setOrderItemAction(OrderItemActionType action, String orderItemId, ResourceOrder ro) {
        ResourceOrderItem roi = getResourceOrderItemOrderItemId(ro);  // bisher nur ein Item genutzt
        roi.setAction(action);
    }

    public void setCharacteristicValue(String name, String value, ResourceOrder ro) {
        ResourceOrderItem roi = getResourceOrderItemOrderItemId(ro);
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

    public ResourceOrderItem getResourceOrderItemOrderItemId(ResourceOrder ro) {
        List<ResourceOrderItem> roiList = ro.getOrderItem();

        if (roiList != null) {
            for (ResourceOrderItem resourceOrderItem : roiList) {
                if (resourceOrderItem.getId().equals("orderItemId"))
                    return resourceOrderItem;
            }
        }

        return null;
    }

}
