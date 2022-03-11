package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.BearerHeaderAuthTokenInjector;
import com.tsystems.tm.acc.ta.api.RequestSpecBuilders;
import com.tsystems.tm.acc.ta.api.Resetable;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.a4.carrier.management.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.carrier.management.client.invoker.GsonObjectMapper;
import com.tsystems.tm.acc.tests.osr.a4.carrier.management.client.invoker.JSON;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_CARRIER_MANAGEMENT_MS;

@Getter
public class A4CarrierManagementClient implements Resetable {

    private final ApiClient client;

    public A4CarrierManagementClient(AuthTokenProvider authTokenProvider){
        client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> RequestSpecBuilders.getDefaultWithAuth(
                        GsonObjectMapper.gson(),
                        new GigabitUrlBuilder(A4_CARRIER_MANAGEMENT_MS).buildUri(),
                        new BearerHeaderAuthTokenInjector(authTokenProvider)
                )
        ));
    }

    public static JSON json() {
        return new JSON();
    }

    @Override
    public void reset() {
    }

}
