package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.dpu.planning.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.dpu.planning.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.morpheus.CommonTestData.DPU_PLANNING;

@Getter
public class DpuPlanningClient {
    private final ApiClient client;

    public DpuPlanningClient(IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder(DPU_PLANNING)
                        .withEndpoint("/resource-order-resource-inventory/v1")
                        .buildUri())
                .withAccessTokenAuth(accessTokenProvider)
                .build();
    }

    public DpuPlanningClient() {
        this(TokenProviderFactory.getDefaultAccessTokenProvider());
    }

    public static JSON json() {
        return new JSON();
    }
}
