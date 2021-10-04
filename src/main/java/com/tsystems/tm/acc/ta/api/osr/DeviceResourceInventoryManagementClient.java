package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.BearerHeaderAuthTokenInjector;
import com.tsystems.tm.acc.ta.api.RequestSpecBuilders;
import com.tsystems.tm.acc.ta.api.Resetable;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.ancp.configuration.v3_4_1.client.invoker.GsonObjectMapper;
import com.tsystems.tm.acc.tests.osr.ancp.configuration.v3_4_1.client.invoker.JSON;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.invoker.ApiClient;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.OLT_RESOURCE_INVENTORY_MS;

@Getter
public class DeviceResourceInventoryManagementClient implements Resetable {

    private final ApiClient client;

    private final String BASE_PATH = "/resource-order-resource-inventory/v5";

    public DeviceResourceInventoryManagementClient(AuthTokenProvider authTokenProvider) {
        client = com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.invoker.ApiClient.api(com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.invoker.ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> RequestSpecBuilders.getDefaultWithAuth(
                        GsonObjectMapper.gson(),
                        new GigabitUrlBuilder(OLT_RESOURCE_INVENTORY_MS)
                                .withEndpoint(BASE_PATH)
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
