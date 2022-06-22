package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.wg.fttb.access.provisioning.v1_2_0.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.wg.fttb.access.provisioning.v1_2_0.client.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

@Getter
public class WgFttbAccessProvisioningClient {
    private final ApiClient client;

    public WgFttbAccessProvisioningClient(IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder("wg-fttb-access-provisioning").buildUri())
                .withAccessTokenAuth(accessTokenProvider)
                .build();
    }

    public WgFttbAccessProvisioningClient() {
        this(TokenProviderFactory.getDefaultAccessTokenProvider());
    }

    public static JSON json() {
        return new JSON();
    }
}