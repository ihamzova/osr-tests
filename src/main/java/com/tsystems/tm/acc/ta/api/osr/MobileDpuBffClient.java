package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.BearerHeaderAuthTokenInjector;
import com.tsystems.tm.acc.ta.api.RequestSpecBuilders;
import com.tsystems.tm.acc.ta.api.Resetable;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.tests.osr.mobile.dpu.bff.invoker.GsonObjectMapper;
import com.tsystems.tm.acc.tests.osr.mobile.dpu.bff.invoker.JSON;
import com.tsystems.tm.acc.tests.osr.mobile.dpu.bff.invoker.ApiClient;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import lombok.Getter;
import static com.tsystems.tm.acc.tests.osr.line.id.generator.internal.client.invoker.GsonObjectMapper.gson;
import static io.restassured.RestAssured.config;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;

public class MobileDpuBffClient implements Resetable {

    @Getter
    private ApiClient client;

    public MobileDpuBffClient() {
        client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> new RequestSpecBuilder().setConfig(config().objectMapperConfig(objectMapperConfig().defaultObjectMapper(gson())))
                        .addFilter(new RequestLoggingFilter())
                        .addFilter(new ResponseLoggingFilter())
                        .setBaseUri(new OCUrlBuilder("mobile-dpu-bff").buildUri())));
    }

    public MobileDpuBffClient(AuthTokenProvider tokenProvider) {
        client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> RequestSpecBuilders.getDefaultWithAuth(
                        GsonObjectMapper.gson(),
                        new OCUrlBuilder("mobile-dpu-bff")
                                .withoutSuffix()
                                .withoutAuth()
                                .buildExternalUri(),
                        new BearerHeaderAuthTokenInjector(tokenProvider))
        ));
    }

    public static JSON json() {
        return new JSON();
    }

    @Override
    public void reset() {
    }

}