package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

@Getter
public class OntOltOrchestratorClient {
    private final ApiClient client;

    public OntOltOrchestratorClient(IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder("ont-olt-orchestrator").buildUri())
                .withAccessTokenAuth(accessTokenProvider)
                .build();
    }

    public OntOltOrchestratorClient() {
        this(TokenProviderFactory.getDefaultAccessTokenProvider());
    }

    public static JSON json() {
        return new JSON();
    }
}
