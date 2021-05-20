package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4ResourceInventoryServiceClient;
import com.tsystems.tm.acc.ta.api.osr.A4ResourceOrderClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.LogicalResourceUpdate;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.model.A4ResourceOrder;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.model.ResourceOrder;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.model.ResourceOrderCreate;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_CREATED_201;

@Slf4j
public class A4ResourceOrderRobot {

      private final ApiClient a4ResourceOrder = new A4ResourceOrderClient().getClient();

    @Step("Send POST for A10nsp Resource Order")
    public void sendPostResourceOrder(String corId, ResourceOrderCreate resourceOrderCreate) {

        System.out.println("+++ übergebene RO: "+resourceOrderCreate);
        System.out.println("+++ corId: "+corId);
        //System.out.println("+++ a4ResourceOrder: "+a4ResourceOrder.toString()); // ergibt: com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.invoker.ApiClient@3fcee3d9

        a4ResourceOrder
                .resourceOrder()
                .createResourceOrder()
                .xCallbackCorrelationIdHeader(corId)
                .body(resourceOrderCreate)                                    // falscher Typ
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));




    }
/*
  Vorlage:

    @Step("Send PATCH request with logical resource")
    public void sendPatchForLogicalResource(String uuid, LogicalResourceUpdate logicalResource) {
        a4ResourceInventoryService
                .logicalResource()
                .updateLogicalResourcePatch()
                .idPath(uuid)
                .body(logicalResource)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }
 */



}
