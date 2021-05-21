package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.api.*;
import com.tsystems.tm.acc.ta.helpers.morpheus.UserTokenProvider;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;

import com.tsystems.tm.acc.tests.osr.dpu.commissioning.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.dpu.commissioning.invoker.JSON;
import com.tsystems.tm.acc.tests.osr.mobile.dpu.bff.invoker.GsonObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static com.tsystems.tm.acc.tests.osr.line.id.generator.internal.client.invoker.GsonObjectMapper.gson;
import static io.restassured.RestAssured.config;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;

public class DpuCommissioningClient implements Resetable {



    @Getter
    private ApiClient client;

    public DpuCommissioningClient() {
        client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> new RequestSpecBuilder().setConfig(config().objectMapperConfig(objectMapperConfig().defaultObjectMapper(gson())))
                        .addFilter(new RequestLoggingFilter())
                        .addFilter(new ResponseLoggingFilter())
                        .setBaseUri(new OCUrlBuilder("dpu-commissioning").buildUri())));
    }

    public DpuCommissioningClient(AuthTokenProvider authTokenProvider) {
        client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> RequestSpecBuilders.getDefaultWithAuth(
                        GsonObjectMapper.gson(),
                        new OCUrlBuilder("dpu-commissioning")
                                .buildUri(),
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
