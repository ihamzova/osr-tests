package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.api.client.olt.commissioning.invoker.ApiClient;
import com.tsystems.tm.api.client.olt.commissioning.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.IIdentityTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.OLT_COMMISSIONING_MS;

@Getter
public class OltCommissioningClient {
    private final ApiClient client;

    public OltCommissioningClient(IIdentityTokenProvider identityTokenProvider, IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder(OLT_COMMISSIONING_MS).buildUri())
                .withAccessTokenAuth(accessTokenProvider)
                .withIdentityTokenAuth(identityTokenProvider)
                .build();
    }

    public OltCommissioningClient(IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder(OLT_COMMISSIONING_MS).buildUri())
                .withAccessTokenAuth(accessTokenProvider)
                .build();
    }

    public OltCommissioningClient() {
        this(TokenProviderFactory.getDefaultAccessTokenProvider());
    }

    public static JSON json() {
        return new JSON();
    }
}
