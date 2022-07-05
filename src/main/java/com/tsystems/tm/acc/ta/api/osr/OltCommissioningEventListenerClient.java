package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.api.client.olt.commissioning.event.listener.invoker.ApiClient;
import com.tsystems.tm.api.client.olt.commissioning.event.listener.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.APIGW;

@Getter
public class OltCommissioningEventListenerClient {
    private final ApiClient client;
    private final String BASE_PATH = "/resource-order-resource-inventory/oltZtCommissioning/v1/";
    private final String TARDIS_PATH = "/downstream-partner/tardis/resource-order-resource-inventory/oltZtCommissioning/v1";

    public OltCommissioningEventListenerClient(IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder(APIGW)
                        .withoutSuffix()
                        .withEndpoint(TARDIS_PATH)
                        .buildUri())
                .withAccessTokenAuth(accessTokenProvider)
                .build();
    }

    public OltCommissioningEventListenerClient() {
        this(TokenProviderFactory.getDefaultAccessTokenProvider());
    }

    public static JSON json() {
        return new JSON();
    }
}
