package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.direct.fiber.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.direct.fiber.client.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.APIGW;

@Getter
public class A4ResourceOrderDirectFiberClient {
    private final ApiClient client;

    public A4ResourceOrderDirectFiberClient(IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder(APIGW).withoutSuffix().buildUri())
                .withAccessTokenAuth(accessTokenProvider)
                .build();
    }

    public A4ResourceOrderDirectFiberClient() {
        this(TokenProviderFactory.getDefaultAccessTokenProvider());
    }

    public static JSON json() {
        return new JSON();
    }
}
