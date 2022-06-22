package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.ri.abstraction.layer.v1_10_0.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.ri.abstraction.layer.v1_10_0.client.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

@Getter
public class RiAbstractionLayerClient {
    private final ApiClient client;

    public RiAbstractionLayerClient(IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder("ri-abstraction-layer").buildUri())
                .withAccessTokenAuth(accessTokenProvider)
                .build();
    }

    public RiAbstractionLayerClient() {
        this(TokenProviderFactory.getDefaultAccessTokenProvider());
    }

    public static JSON json() {
        return new JSON();
    }
}
