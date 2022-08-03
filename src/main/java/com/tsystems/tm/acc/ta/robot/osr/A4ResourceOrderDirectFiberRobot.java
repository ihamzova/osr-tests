package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4ResourceOrderDirectFiberClient;
import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceOrderDirectFiberMapper;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.direct.fiber.client.model.OrderItemActionType;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.direct.fiber.client.model.ResourceOrder;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.direct.fiber.client.model.ResourceOrderItem;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.direct.fiber.client.model.ResourceRefOrValue;
import lombok.extern.slf4j.Slf4j;

import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;

@Slf4j
public class A4ResourceOrderDirectFiberRobot {
    private final A4ResourceOrderDirectFiberClient internalClient = new A4ResourceOrderDirectFiberClient();
    private final A4ResourceOrderDirectFiberMapper directFiberMapper = new A4ResourceOrderDirectFiberMapper();

    public String sendPostResourceOrderDirectFiber(ResourceOrder resourceOrder, int statuscode) {
        final String roId = internalClient.getClient()
                .resourceOrder()
                .createResourceOrder()
                .body(resourceOrder)
                .execute(checkStatus(statuscode)).getBody().asString();

        log.info("+++ Resource-Order-Diirect-Fiber ID: " + roId);
        resourceOrder.setId(roId);
        return roId;
    }

    public ResourceOrder buildResourceOrder() {
        return directFiberMapper.buildResourceOrder();
    }

    public ResourceOrderItem createOrderItem(String orderItemId, OrderItemActionType actionType, String uuid_nsp) {
        final ResourceRefOrValue resource = new ResourceRefOrValue()
                .id(uuid_nsp)
                .resourceCharacteristic(directFiberMapper.buildResourceCharacteristicList());

        return new ResourceOrderItem()
                .action(actionType)
                .resource(resource)
                .id(orderItemId);
    }

}

