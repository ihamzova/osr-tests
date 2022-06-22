package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.OLT_RESOURCE_INVENTORY_MS;

@Getter
public class OltResourceInventoryClient {
    private final ApiClient client;

    public OltResourceInventoryClient(IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder(OLT_RESOURCE_INVENTORY_MS).buildUri())
                .withAccessTokenAuth(accessTokenProvider)
                .build();
    }

    public OltResourceInventoryClient() {
        this(TokenProviderFactory.getDefaultAccessTokenProvider());
    }

    public static JSON json() {
        return new JSON();
    }
}
