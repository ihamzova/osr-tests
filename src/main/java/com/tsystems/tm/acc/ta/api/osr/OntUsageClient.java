package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.api.*;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.tests.osr.ont.usage.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.ont.usage.client.invoker.JSON;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.v2_3_0.client.invoker.GsonObjectMapper;
import lombok.Getter;
import lombok.Setter;

@Getter
public class OntUsageClient {

    @Setter
    private AuthTokenProvider userTokenProvider;
    private ApiClient client;

    public OntUsageClient(AuthTokenProvider authTokenProvider) {
        client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> RequestSpecBuilders.getDefault(
                        GsonObjectMapper.gson(),
                        new OCUrlBuilder("ont-usage")
                                .buildUri())
                        .addFilter(new AuthTokenInjectorFilter(
                                new APMHeaderAuthTokenInjector(userTokenProvider)))
                        .addFilter(new AuthTokenInjectorFilter(
                                new BearerHeaderAuthTokenInjector(authTokenProvider))))
        );
    }

    public static JSON json() {
        return new JSON();
    }

}
