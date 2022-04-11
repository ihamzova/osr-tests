package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.api.Resetable;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.api.client.olt.commissioning.event.listener.invoker.ApiClient;
import com.tsystems.tm.api.client.olt.commissioning.event.listener.invoker.JSON;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.OLT_COMMISSIONING_MS;
import static com.tsystems.tm.api.client.olt.commissioning.invoker.GsonObjectMapper.gson;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;

@Getter
public class OltCommissioningEventListenerClient implements Resetable {

    private final ApiClient client;

    public OltCommissioningEventListenerClient() {
        client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> new RequestSpecBuilder().setConfig(config().objectMapperConfig(objectMapperConfig().defaultObjectMapper(gson())))
                        .setContentType("application/json")
                        .addFilter(new RequestLoggingFilter())
                        .addFilter(new ResponseLoggingFilter())
                        .setBaseUri(new GigabitUrlBuilder(OLT_COMMISSIONING_MS).buildUri())));
    }


    public static JSON json() {
        return new JSON();
    }

    @Override
    public void reset() {
    }
}
