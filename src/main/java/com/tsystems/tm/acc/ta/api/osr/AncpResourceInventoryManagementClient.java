package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.ancp.resource.inventory.management.v5_0_0.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.ancp.resource.inventory.management.v5_0_0.client.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.ANCP_CONFIGURATION_MS;

@Getter
public class AncpResourceInventoryManagementClient {
    private final ApiClient client;
    private final String BASE_PATH = "/resource-order-resource-inventory/v5";

    public AncpResourceInventoryManagementClient(IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder(ANCP_CONFIGURATION_MS).withEndpoint(BASE_PATH).buildUri())
                .withAccessTokenAuth(accessTokenProvider)
                .build();
    }

    public AncpResourceInventoryManagementClient() {
        this(TokenProviderFactory.getDefaultAccessTokenProvider());
    }

    public static JSON json() {
        return new JSON();
    }
}
