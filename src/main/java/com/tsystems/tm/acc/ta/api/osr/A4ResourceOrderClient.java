package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.BearerHeaderAuthTokenInjector;
import com.tsystems.tm.acc.ta.api.RequestSpecBuilders;
import com.tsystems.tm.acc.ta.api.Resetable;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.invoker.GsonObjectMapper;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.APIGW;

@Getter
public class A4ResourceOrderClient implements Resetable {

    private final ApiClient client;

    public A4ResourceOrderClient(AuthTokenProvider authTokenProvider){
        client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> RequestSpecBuilders.getDefaultWithAuth(
                        GsonObjectMapper.gson(),
                        new GigabitUrlBuilder(APIGW).withoutSuffix().buildUri(),
                        new BearerHeaderAuthTokenInjector(authTokenProvider)
                )
        ));
    }
    @Override
    public void reset() {

    }
}
