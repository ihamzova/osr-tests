package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.ont.usage.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.ont.usage.client.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.IIdentityTokenProvider;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

@Getter
public class OntUsageClient {
    private final ApiClient client;

    /**
     * Usage:
     * <pre>
     *     AuthorizationCodeTokenProvider tokenProvider = getPublicAuthorizationCodeTokenProvider("username", "password", "realmId");
     *     OntUsageClient client = new OntUsageClient(tokenProvider, tokenProvider);
     * </pre>
     *
     * @param identityTokenProvider
     */
    public OntUsageClient(IAccessTokenProvider accessTokenProvider, IIdentityTokenProvider identityTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder("ont-usage").buildUri())
                .withAccessTokenAuth(accessTokenProvider)
                .withIdentityTokenAuth(identityTokenProvider)
                .build();
    }

    public static JSON json() {
        return new JSON();
    }
}
