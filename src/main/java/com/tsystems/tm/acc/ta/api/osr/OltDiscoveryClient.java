package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.BearerHeaderAuthTokenInjector;
import com.tsystems.tm.acc.ta.api.RequestSpecBuilders;
import com.tsystems.tm.acc.ta.api.Resetable;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.tests.osr.olt.discovery.v2_1_0.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.olt.discovery.v2_1_0.client.invoker.JSON;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.v2_3_0.client.invoker.GsonObjectMapper;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.OLT_DISCOVERY_MS;
import static com.tsystems.tm.acc.tests.osr.olt.discovery.v2_1_0.client.invoker.GsonObjectMapper.gson;
import static io.restassured.RestAssured.given;

@Getter
public class OltDiscoveryClient implements Resetable {
    private ApiClient client;

    public OltDiscoveryClient() {
        client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> RequestSpecBuilders.getDefault(gson(), new OCUrlBuilder(OLT_DISCOVERY_MS).buildUri())));
    }

    public OltDiscoveryClient(AuthTokenProvider authTokenProvider) {
        client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> RequestSpecBuilders.getDefaultWithAuth(
                        GsonObjectMapper.gson(),
                        new OCUrlBuilder(OLT_DISCOVERY_MS)
                                .buildUri(),
                        new BearerHeaderAuthTokenInjector(authTokenProvider))
        ));
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
                .get(new OCUrlBuilder("olt-discovery").withEndpoint("/api/test/v1/initialize-database/").buildUri())
                .then()
                .statusCode(200);

    }
}
