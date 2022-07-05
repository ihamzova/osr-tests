package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.network.line.profile.management.v1_5_0.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.network.line.profile.management.v1_5_0.client.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

@Getter
public class NetworkLineProfileManagementClient {
    private final ApiClient client;

    public NetworkLineProfileManagementClient(IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder("network-line-profile-management").buildUri())
                .withAccessTokenAuth(accessTokenProvider)
                .build();
    }

    public NetworkLineProfileManagementClient() {
        this(TokenProviderFactory.getDefaultAccessTokenProvider());
    }

    public static JSON json() {
        return new JSON();
    }
}
