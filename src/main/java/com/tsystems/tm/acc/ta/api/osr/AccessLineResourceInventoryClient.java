package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

@Getter
public class AccessLineResourceInventoryClient {
    private final ApiClient client;

    public AccessLineResourceInventoryClient(IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder("access-line-resource-inventory").buildUri())
                .withAccessTokenAuth(accessTokenProvider)
                .build();
    }

    public AccessLineResourceInventoryClient() {
        this(TokenProviderFactory.getDefaultAccessTokenProvider());
    }

    public static JSON json() {
        return new JSON();
    }
}
