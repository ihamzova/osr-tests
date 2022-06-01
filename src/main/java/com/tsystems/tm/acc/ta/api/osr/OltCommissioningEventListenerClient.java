package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.BearerHeaderAuthTokenInjector;
import com.tsystems.tm.acc.ta.api.RequestSpecBuilders;
import com.tsystems.tm.acc.ta.api.Resetable;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.api.client.olt.commissioning.event.listener.invoker.ApiClient;
import com.tsystems.tm.api.client.olt.commissioning.event.listener.invoker.GsonObjectMapper;
import com.tsystems.tm.api.client.olt.commissioning.event.listener.invoker.JSON;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.APIGW;

@Getter
public class OltCommissioningEventListenerClient implements Resetable {

    private final ApiClient client;

    private final String BASE_PATH = "/resource-order-resource-inventory/oltZtCommissioning/v1/";
    private final String TARDIS_PATH = "/downstream-partner/tardis/resource-order-resource-inventory/oltZtCommissioning/v1";

    public OltCommissioningEventListenerClient(AuthTokenProvider authTokenProvider) {
        client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> RequestSpecBuilders.getDefaultWithAuth(
                        GsonObjectMapper.gson(),
                        new GigabitUrlBuilder(APIGW)
                                .withoutSuffix()
                                .withEndpoint(TARDIS_PATH)
                                .buildUri(),
                        new BearerHeaderAuthTokenInjector(authTokenProvider))
        ));

    }


    public static JSON json() {
        return new JSON();
    }

    @Override
    public void reset() {
    }
}
