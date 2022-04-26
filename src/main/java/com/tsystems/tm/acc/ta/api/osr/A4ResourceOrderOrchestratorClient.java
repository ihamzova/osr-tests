package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.BearerHeaderAuthTokenInjector;
import com.tsystems.tm.acc.ta.api.RequestSpecBuilders;
import com.tsystems.tm.acc.ta.api.Resetable;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.invoker.GsonObjectMapper;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_ORDER_ORCHESTRATOR_MS;

@Getter
public class A4ResourceOrderOrchestratorClient implements Resetable {

    private final ApiClient client;

    public A4ResourceOrderOrchestratorClient(AuthTokenProvider authTokenProvider){
        client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> RequestSpecBuilders.getDefaultWithAuth(
                        GsonObjectMapper.gson(),
                        new GigabitUrlBuilder(A4_RESOURCE_ORDER_ORCHESTRATOR_MS).buildUri(),
                        new BearerHeaderAuthTokenInjector(authTokenProvider)
                )
        ));
    }

    @Override
    public void reset() {
        // Is empty. Why?
    }

}
