package com.tsystems.tm.acc.ta.api;

import com.tsystems.tm.acc.ont.olt.orchestrator.internal.client.invoker.ApiClient;
import com.tsystems.tm.acc.ont.olt.orchestrator.internal.client.invoker.JSON;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import lombok.Getter;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.get;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;

@Getter
public class ProvisioningClient implements Resetable {
    private ApiClient client;

    public ProvisioningClient() {
        client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> new RequestSpecBuilder().setConfig(config().objectMapperConfig(objectMapperConfig().defaultObjectMapper(gson())))
                        .addFilter(new RequestLoggingFilter())
                        .addFilter(new ResponseLoggingFilter())
                        .setBaseUri(new OCUrlBuilder("pon-inventory").buildUri())));
    }

    public static JSON json() {
        return new JSON();
    }

    @Override
    public void reset() {
    }
}
