package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.BearerHeaderAuthTokenInjector;
import com.tsystems.tm.acc.ta.api.RequestSpecBuilders;
import com.tsystems.tm.acc.ta.api.Resetable;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.a4.rebell.sync.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.rebell.sync.client.invoker.JSON;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.invoker.GsonObjectMapper;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_INVENTORY_IMPORTER_MS;

@Getter
public class A4RebellSyncClient implements Resetable {

    private final ApiClient client;

    public A4RebellSyncClient(AuthTokenProvider authTokenProvider){
        client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> RequestSpecBuilders.getDefaultWithAuth(
                        GsonObjectMapper.gson(),
                        new GigabitUrlBuilder(A4_INVENTORY_IMPORTER_MS).buildUri(),
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
