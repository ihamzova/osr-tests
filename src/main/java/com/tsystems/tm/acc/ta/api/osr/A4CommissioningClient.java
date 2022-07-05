package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_11_0.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_11_0.client.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_COMMISSIONING_MS;

@Getter
public class A4CommissioningClient {
    private final ApiClient client;

    public A4CommissioningClient(IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder(A4_COMMISSIONING_MS).buildUri())
                .withAccessTokenAuth(accessTokenProvider)
                .build();
    }

    public A4CommissioningClient() {
        this(TokenProviderFactory.getDefaultAccessTokenProvider());
    }

    public static JSON json() {
        return new JSON();
    }
}
