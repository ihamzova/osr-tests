package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.a10nsp.inventory.internal.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a10nsp.inventory.internal.client.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A10NSP_INVENTORY_MS;

@Getter
public class A10nspInventoryClient {
    private final ApiClient client;

    public A10nspInventoryClient(IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder(A10NSP_INVENTORY_MS).buildUri())
                .withAccessTokenAuth(accessTokenProvider)
                .build();
    }

    public A10nspInventoryClient() {
        this(TokenProviderFactory.getDefaultAccessTokenProvider());
    }

    public static JSON json() {
        return new JSON();
    }
}