package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.APIGW;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.OLT_BFF_PROXY_MS;

@Getter
public class DeviceResourceInventoryManagementClient {
    private final ApiClient client;
    private final String BASE_PATH = "/resource-order-resource-inventory/v5";

    public DeviceResourceInventoryManagementClient(IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri(new GigabitUrlBuilder(APIGW)
                        .withoutSuffix()
                        .withEndpoint(BASE_PATH)
                        .buildUri())
                .withAccessTokenAuth(accessTokenProvider)
                .build();
    }

    public DeviceResourceInventoryManagementClient() {
        this(TokenProviderFactory.getAccessTokenProvider(OLT_BFF_PROXY_MS));
    }

    public static JSON json() {
        return new JSON();
    }
}
