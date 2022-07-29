package com.tsystems.tm.acc.ta.robot;

import com.tsystems.tm.acc.ta.api.osr.A4ResourceOrderDirectFiberClient;
import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceOrderDirectFiberMapper;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementLink;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.direct.fiber.client.model.OrderItemActionType;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.direct.fiber.client.model.ResourceOrder;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.direct.fiber.client.model.ResourceOrderItem;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.direct.fiber.client.model.ResourceRefOrValue;
import lombok.extern.slf4j.Slf4j;

import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_CREATED_201;
import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;

@Slf4j
public class A4ResourceOrderDirectFiberRobot {
    private final A4ResourceOrderDirectFiberClient internalClient = new A4ResourceOrderDirectFiberClient();
    private final A4ResourceOrderDirectFiberMapper directFiberMapper = new A4ResourceOrderDirectFiberMapper();

    public String sendPostResourceOrderDirectFiber(ResourceOrder resourceOrder) {
        final String roId = internalClient.getClient()
                .resourceOrder()
                .createResourceOrder()
                .body(resourceOrder)
                .execute(checkStatus(HTTP_CODE_CREATED_201)).getBody().asString();

        log.info("+++ Resource-Order-Diirect-Fiber ID: " + roId);
        resourceOrder.setId(roId);
        return roId;
    }

    public ResourceOrder buildResourceOrder() {
        return directFiberMapper.buildResourceOrder();
    }

    public void addOrderItem(String orderItemId, OrderItemActionType actionType, A4NetworkElementLink nelData, ResourceOrder ro) {
        addOrderItem(orderItemId, actionType, nelData.getLbz(), ro);
    }

    public void addOrderItem(String orderItemId, OrderItemActionType actionType, String nelLbz, ResourceOrder ro) {
        final ResourceRefOrValue resource = new ResourceRefOrValue()
                .name(nelLbz)
                .resourceCharacteristic(directFiberMapper.buildResourceCharacteristicList());

        final ResourceOrderItem orderItem = new ResourceOrderItem()
                .action(actionType)
                .resource(resource)
                .id(orderItemId);

        ro.addOrderItemItem(orderItem);
    }

}

