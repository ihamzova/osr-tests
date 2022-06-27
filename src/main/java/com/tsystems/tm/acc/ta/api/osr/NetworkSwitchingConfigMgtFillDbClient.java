package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.network.switching.config.mgt.fill.db.v1_0_0.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.network.switching.config.mgt.fill.db.v1_0_0.client.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

@Getter
public class NetworkSwitchingConfigMgtFillDbClient {
    private final ApiClient client;

    public NetworkSwitchingConfigMgtFillDbClient(IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder("network-switching-config-mgt").buildUri())
                .withAccessTokenAuth(accessTokenProvider)
                .build();
    }

    public NetworkSwitchingConfigMgtFillDbClient() {
        this(TokenProviderFactory.getDefaultAccessTokenProvider());
    }

    public static JSON json() {
        return new JSON();
    }
}
