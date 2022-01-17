package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.BearerHeaderAuthTokenInjector;
import com.tsystems.tm.acc.ta.api.RequestSpecBuilders;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.tests.osr.osr.support.v1_2_0.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.osr.support.v1_2_0.client.invoker.GsonObjectMapper;
import com.tsystems.tm.acc.tests.osr.osr.support.v1_2_0.client.invoker.JSON;
import lombok.Getter;

@Getter
public class OsrSupportClient {
    private ApiClient client;

    public OsrSupportClient(AuthTokenProvider authTokenProvider) {
        client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> RequestSpecBuilders.getDefaultWithAuth(
                        GsonObjectMapper.gson(),
                        new OCUrlBuilder("osr-support")
                                .buildUri(),
                        new BearerHeaderAuthTokenInjector(authTokenProvider))
        ));
    }

    public static JSON json() {
        return new JSON();
    }
}
