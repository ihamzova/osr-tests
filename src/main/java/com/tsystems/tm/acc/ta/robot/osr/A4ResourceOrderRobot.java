package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.A4ResourceOrderClient;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.model.ResourceOrderCreate;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
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

    @Step("Send POST for A10nsp Resource Order")
    public void sendPostResourceOrder(String corId, ResourceOrderCreate resourceOrderCreate) {

        System.out.println("+++ übergebene Order: "+resourceOrderCreate);
        System.out.println("+++ corId: "+corId);

        a4ResourceOrder
                .resourceOrder()
                .createResourceOrder()
                .xCallbackCorrelationIdHeader(corId)
                .xCallbackUrlHeader("https://wiremock-acc-app-berlinium-03.priv.cl01.gigadev.telekom.de/test_url")
                .body(resourceOrderCreate)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));


    }


}
