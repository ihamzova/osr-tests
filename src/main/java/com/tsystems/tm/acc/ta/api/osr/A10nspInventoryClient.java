package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.a10nsp.inventory.internal.client.invoker.ApiClient;
import com.tsystems.tm.acc.a10nsp.inventory.internal.client.invoker.JSON;
import com.tsystems.tm.acc.ta.api.Resetable;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import lombok.Getter;

import static com.tsystems.tm.acc.a10nsp.inventory.internal.client.invoker.GsonObjectMapper.gson;
import static io.restassured.RestAssured.config;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;

@Getter
public class A10nspInventoryClient implements Resetable {

    private ApiClient client;

    public A10nspInventoryClient() {
        client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> new RequestSpecBuilder().setConfig(config().objectMapperConfig(objectMapperConfig().defaultObjectMapper(gson())))
                        .addFilter(new RequestLoggingFilter())
                        .addFilter(new ResponseLoggingFilter())
                        .setBaseUri(new OCUrlBuilder("a10nsp-inventory").buildUri())));
    }

    public static JSON json() {
        return new JSON();
    }

    @Override
    public void reset() {
    }
}