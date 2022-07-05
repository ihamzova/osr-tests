package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.mobile.dpu.bff.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.mobile.dpu.bff.invoker.JSON;
import de.telekom.it.magic.api.IIdentityTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

@Getter
public class MobileDpuBffClient {
    private final ApiClient client;

    public MobileDpuBffClient(IIdentityTokenProvider identityTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder("mobile-dpu-bff").buildUri())
                .withIdentityTokenAuth(identityTokenProvider)
                .build();
    }

    public MobileDpuBffClient() {
        this(TokenProviderFactory.getPublicIdentityTokenProvider("autotest", "Mobile!5", ""));
    }

    public static JSON json() {
        return new JSON();
    }
}