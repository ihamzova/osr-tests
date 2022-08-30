package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.v2_9_1.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.v2_9_1.client.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

@Getter
public class WgAccessProvisioningClient {
    private final ApiClient client;

    public WgAccessProvisioningClient(IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder("wg-access-provisioning").buildUri())
                .withAccessTokenAuth(accessTokenProvider)
                .build();
    }

    public WgAccessProvisioningClient() {
        this(TokenProviderFactory.getDefaultAccessTokenProvider());
    }

    public static JSON json() {
        return new JSON();
    }
}
