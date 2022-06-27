package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.uplink.resource.inventory.management.v5_2_1_client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.uplink.resource.inventory.management.v5_2_1_client.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.OLT_UPLINK_MANAGEMENT_MS;

@Getter
public class UplinkResourceInventoryManagementClient {
    final private ApiClient client;
    private final String BASE_PATH = "/resource-order-resource-inventory/v5";

    public UplinkResourceInventoryManagementClient(IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder(OLT_UPLINK_MANAGEMENT_MS)
                        .withEndpoint(BASE_PATH)
                        .buildUri())
                .withAccessTokenAuth(accessTokenProvider)
                .build();
    }

    public UplinkResourceInventoryManagementClient() {
        this(TokenProviderFactory.getDefaultAccessTokenProvider());
    }

    public static JSON json() {
        return new JSON();
    }
}
