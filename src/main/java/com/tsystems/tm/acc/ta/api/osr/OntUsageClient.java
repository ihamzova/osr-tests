package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.api.*;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.tests.osr.ont.usage.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.ont.usage.client.invoker.JSON;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.v2_3_0.client.invoker.GsonObjectMapper;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.IIdentityTokenProvider;
import lombok.Getter;
import lombok.Setter;

@Getter
public class OntUsageClient {

    @Setter
    private AuthTokenProvider userTokenProvider;
    private ApiClient client;

    public OntUsageClient(AuthTokenProvider authTokenProvider) {
        client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> RequestSpecBuilders.getDefault(
                                GsonObjectMapper.gson(),
                                new OCUrlBuilder("ont-usage")
                                        .buildUri())
                        .addFilter(new AuthTokenInjectorFilter(
                                new APMHeaderAuthTokenInjector(userTokenProvider)))
                        .addFilter(new AuthTokenInjectorFilter(
                                new BearerHeaderAuthTokenInjector(authTokenProvider))))
        );
    }

    /**
     * Usage:
     * <pre>
     *     AuthorizationCodeTokenProvider tokenProvider = getPublicAuthorizationCodeTokenProvider("username", "password", "realmId");
     *     OntUsageClient client = new OntUsageClient(tokenProvider, tokenProvider);
     * </pre>
     * @param identityTokenProvider
     */
    public OntUsageClient(IAccessTokenProvider accessTokenProvider, IIdentityTokenProvider identityTokenProvider) {
        client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> RequestSpecBuilders.getDefault(
                                GsonObjectMapper.gson(),
                                new GigabitUrlBuilder("ont-usage").buildUri())
                        .addFilter((requestSpec, responseSpec, ctx) -> {
                            requestSpec.header("Authorization", "Bearer " + accessTokenProvider.getAccessToken());
                            requestSpec.header("apm-principal-token", identityTokenProvider.getIdentityToken());
                            return ctx.next(requestSpec, responseSpec);
                        })
        ));
    }

    public static JSON json() {
        return new JSON();
    }

}
