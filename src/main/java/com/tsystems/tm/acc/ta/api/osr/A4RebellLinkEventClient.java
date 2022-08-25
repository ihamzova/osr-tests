package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.a4.inventory.importer.client.invoker.ApiClient;
//import com.tsystems.tm.acc.tests.osr.a4.;

import com.tsystems.tm.acc.tests.osr.a4.inventory.importer.client.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_INVENTORY_IMPORTER_MS;

@Getter
public class A4RebellLinkEventClient {
    private final ApiClient client;

    public A4RebellLinkEventClient(IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder(A4_INVENTORY_IMPORTER_MS).buildUri())
                .withAccessTokenAuth(accessTokenProvider)
                .build();
    }

    public A4RebellLinkEventClient() {
        this(TokenProviderFactory.getDefaultAccessTokenProvider());
    }

    public static JSON json() {
        return new JSON();
    }
}
