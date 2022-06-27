package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.tests.osr.device.test.data.management.v2_0_0.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.device.test.data.management.v2_0_0.client.invoker.JSON;
import de.telekom.it.magic.api.IAccessTokenProvider;
import de.telekom.it.magic.api.keycloak.TokenProviderFactory;
import de.telekom.it.magic.api.restassured.ApiClientBuilder;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.OLT_RESOURCE_INVENTORY_MS;

@Getter
public class DeviceTestDataManagementClient {
    private final ApiClient client;
    private final String BASE_PATH = "/resource-order-resource-inventory/v2/device-test-data-management";

    public DeviceTestDataManagementClient(IAccessTokenProvider accessTokenProvider) {
        client = new ApiClientBuilder<>(ApiClient.class)
                .withBaseUri((new GigabitUrlBuilder(OLT_RESOURCE_INVENTORY_MS).
                        withEndpoint(BASE_PATH)
                        .buildUri()))
                .withAccessTokenAuth(accessTokenProvider)
                .build();
    }

    public DeviceTestDataManagementClient() {
        this(TokenProviderFactory.getDefaultAccessTokenProvider());
    }

    public static JSON json() {
        return new JSON();
    }
}
