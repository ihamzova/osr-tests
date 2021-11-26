package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.BearerHeaderAuthTokenInjector;
import com.tsystems.tm.acc.ta.api.RequestSpecBuilders;
import com.tsystems.tm.acc.ta.api.Resetable;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_9_0.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_9_0.client.invoker.GsonObjectMapper;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_9_0.client.invoker.JSON;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_COMMISSIONING_MS;
import static io.restassured.RestAssured.config;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;

//public class A4CommissioningClient implements Resetable {

//    private final ApiClient client;
//
//    public A4InventoryImporterClient() {
//        client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
//                () -> new RequestSpecBuilder().setConfig(config().objectMapperConfig(objectMapperConfig().defaultObjectMapper(gson())))
//                        .addFilter(new RequestLoggingFilter())
//                        .addFilter(new ResponseLoggingFilter())
//                        .addHeader("Content-Type", "application/json")
//                        .setBaseUri(new GigabitUrlBuilder(A4_COMMISSIONING_MS).buildUri())));
//    }
//
//    public A4InventoryImporterClient(AuthTokenProvider authTokenProvider){
//        client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
//                () -> RequestSpecBuilders.getDefaultWithAuth(
//                        GsonObjectMapper.gson(),
//                        new GigabitUrlBuilder(A4_COMMISSIONING_MS).buildUri(),
//                        new BearerHeaderAuthTokenInjector(authTokenProvider)
//                )
//        ));
//    }
//
//    public static JSON json() {
//        return new JSON();
//    }
//
//    @Override
//    public void reset() {
//    }

//}
