package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.api.Resetable;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.device.test.data.management.v2_0_0.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.device.test.data.management.v2_0_0.client.invoker.JSON;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.OLT_RESOURCE_INVENTORY_MS;
import static com.tsystems.tm.acc.tests.osr.device.test.data.management.v2_0_0.client.invoker.GsonObjectMapper.gson;
import static io.restassured.RestAssured.config;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;

@Getter
public class DeviceTestDataManagementClient implements Resetable {

    private final ApiClient client;

    private final String BASE_PATH = "/resource-order-resource-inventory/v2/device-test-data-management";

    public DeviceTestDataManagementClient() {
        client = com.tsystems.tm.acc.tests.osr.device.test.data.management.v2_0_0.client.invoker.ApiClient.api(com.tsystems.tm.acc.tests.osr.device.test.data.management.v2_0_0.client.invoker.ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> new RequestSpecBuilder().setConfig(config().objectMapperConfig(objectMapperConfig().defaultObjectMapper(gson())))
                        .addFilter(new RequestLoggingFilter())
                        .addFilter(new ResponseLoggingFilter())
                        .setBaseUri(new GigabitUrlBuilder(OLT_RESOURCE_INVENTORY_MS).
                                withEndpoint(BASE_PATH)
                                .buildUri())));
    }

    public static JSON json() {
        return new JSON();
    }

    @Override
    public void reset() {
    }
}
