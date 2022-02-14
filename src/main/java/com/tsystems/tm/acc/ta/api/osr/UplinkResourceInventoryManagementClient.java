package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.BearerHeaderAuthTokenInjector;
import com.tsystems.tm.acc.ta.api.RequestSpecBuilders;
import com.tsystems.tm.acc.ta.api.Resetable;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.uplink.resource.inventory.management.v5_2_1_client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.uplink.resource.inventory.management.v5_2_1_client.invoker.GsonObjectMapper;
import com.tsystems.tm.acc.tests.osr.uplink.resource.inventory.management.v5_2_1_client.invoker.JSON;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.OLT_UPLINK_MANAGEMENT_MS;

@Getter
public class UplinkResourceInventoryManagementClient implements Resetable {
    final private ApiClient client;

    private final String BASE_PATH = "/resource-order-resource-inventory/v5";

    public UplinkResourceInventoryManagementClient(AuthTokenProvider authTokenProvider) {
        client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> RequestSpecBuilders.getDefaultWithAuth(
                        GsonObjectMapper.gson(),
                        new GigabitUrlBuilder(OLT_UPLINK_MANAGEMENT_MS + "-app")
                                .withoutSuffix()
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
