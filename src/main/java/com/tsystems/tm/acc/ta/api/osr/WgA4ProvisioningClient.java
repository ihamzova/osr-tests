package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_11_0.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_11_0.client.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

@Getter
public class WgA4ProvisioningClient {
    private final ApiClient client;

    public WgA4ProvisioningClient(IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder("wg-a4-provisioning").buildUri())
                .withAccessTokenAuth(accessTokenProvider)
                .build();
    }

    public WgA4ProvisioningClient() {
        this(TokenProviderFactory.getDefaultAccessTokenProvider());
    }

    public static JSON json() {
        return new JSON();
    }
}
