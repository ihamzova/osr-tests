package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.api.Resetable;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.olt.discovery.v2_1_0.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.olt.discovery.v2_1_0.client.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.OLT_DISCOVERY_MS;
import static io.restassured.RestAssured.given;

@Getter
public class OltDiscoveryClient implements Resetable {
    private final ApiClient client;

    public OltDiscoveryClient(IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder(OLT_DISCOVERY_MS).buildUri())
                .withAccessTokenAuth(accessTokenProvider)
                .build();
    }

    public OltDiscoveryClient() {
        this(TokenProviderFactory.getDefaultAccessTokenProvider());
    }

    public static JSON json() {
        return new JSON();
    }

    @Override
    public void reset() {
        given()
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .when()
                .get(new GigabitUrlBuilder("olt-discovery").withEndpoint("/api/test/v1/initialize-database/").buildUri())
                .then()
                .statusCode(200);
    }
}
