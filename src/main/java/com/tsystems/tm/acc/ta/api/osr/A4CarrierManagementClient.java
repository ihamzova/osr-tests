package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.api.Resetable;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.tests.osr.a4.carrier.management.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.carrier.management.client.invoker.JSON;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_CARRIER_MANAGEMENT_MS;
import static com.tsystems.tm.acc.tests.osr.a4.carrier.management.client.invoker.GsonObjectMapper.gson;
import static io.restassured.RestAssured.config;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;

@Getter
public class A4CarrierManagementClient implements Resetable {

    private final ApiClient client;

    public A4CarrierManagementClient() {
        client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> new RequestSpecBuilder().setConfig(config().objectMapperConfig(objectMapperConfig().defaultObjectMapper(gson())))
                        .addFilter(new RequestLoggingFilter())
                        .addFilter(new ResponseLoggingFilter())
                        .addHeader("Content-Type", "application/json")
                        .setBaseUri(new OCUrlBuilder(A4_CARRIER_MANAGEMENT_MS).buildUri())));
    }

    public static JSON json() {
        return new JSON();
    }

    @Override
    public void reset() {
    }

}