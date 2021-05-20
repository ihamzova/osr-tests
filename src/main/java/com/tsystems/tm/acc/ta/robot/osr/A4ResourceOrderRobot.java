package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4ResourceOrderClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.invoker.ApiClient;
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

        System.out.println("+++ übergebene Order: "+resourceOrderCreate);
        System.out.println("+++ corId: "+corId);

        a4ResourceOrder
                .resourceOrder()
                .createResourceOrder()
                .xCallbackCorrelationIdHeader(corId)
                .body(resourceOrderCreate)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));


    }


}
