package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.line.id.generator.v2_1_0.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.line.id.generator.v2_1_0.client.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

@Getter
public class LineIdGeneratorClient {
    private final ApiClient client;

    public LineIdGeneratorClient(IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder("line-id-generator").buildUri())
                .withAccessTokenAuth(accessTokenProvider)
                .build();
    }

    public LineIdGeneratorClient() {
        this(TokenProviderFactory.getDefaultAccessTokenProvider());
    }

    public static JSON json() {
        return new JSON();
    }
}
