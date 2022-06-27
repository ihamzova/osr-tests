package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.api.client.olt.material.catalog.external.invoker.ApiClient;
import com.tsystems.tm.api.client.olt.material.catalog.external.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.APIGW;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.OLT_COMMISSIONING_MS;

@Getter
public class OltMaterialCatalogClient {
    private final String BASE_PATH = "/resource-order-resource-inventory/v1";
    private final ApiClient client;

    public OltMaterialCatalogClient(IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder(APIGW)
                        .withoutSuffix()
                        .withEndpoint(BASE_PATH)
                        .buildUri())
                .withAccessTokenAuth(accessTokenProvider)
                .build();
    }

    public OltMaterialCatalogClient() {
        this(TokenProviderFactory.getAccessTokenProvider(OLT_COMMISSIONING_MS));
    }

    public static JSON json() {
        return new JSON();
    }
}
