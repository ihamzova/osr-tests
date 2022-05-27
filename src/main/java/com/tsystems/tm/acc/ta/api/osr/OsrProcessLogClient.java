package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.BearerHeaderAuthTokenInjector;
import com.tsystems.tm.acc.ta.api.RequestSpecBuilders;
import com.tsystems.tm.acc.ta.api.Resetable;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.api.client.olt.commissioning.invoker.GsonObjectMapper;
import com.tsystems.tm.api.client.osr.process.log.invoker.ApiClient;
import com.tsystems.tm.api.client.osr.process.log.invoker.JSON;
import lombok.Getter;

@Getter
public class OsrProcessLogClient implements Resetable {

    private final ApiClient client;

    public OsrProcessLogClient(AuthTokenProvider authTokenProvider) {
        client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> RequestSpecBuilders.getDefaultWithAuth(
                        GsonObjectMapper.gson(),
                        new GigabitUrlBuilder("apigw")
                                .withoutSuffix()
                                .buildExternalUri(),
                        new BearerHeaderAuthTokenInjector(authTokenProvider))
        ));
    }

    public static JSON json() {
        return new JSON();
    }

    @Override
    public void reset() {
    }
}
