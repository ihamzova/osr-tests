package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.api.client.osr.process.log.invoker.ApiClient;
import com.tsystems.tm.api.client.osr.process.log.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.ACCESS_PROCESS_MANAGEMENT_BFF;

@Getter
public class OsrProcessLogClient {
    private final ApiClient client;

    public OsrProcessLogClient(IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder("apigw")
                        .withoutSuffix()
                        .buildExternalUri())
                .withAccessTokenAuth(accessTokenProvider)
                .build();
    }

    public OsrProcessLogClient() {
        this(TokenProviderFactory.getAccessTokenProvider(ACCESS_PROCESS_MANAGEMENT_BFF));
    }

    public static JSON json() {
        return new JSON();
    }
}
