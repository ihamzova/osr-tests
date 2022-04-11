package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.BearerHeaderAuthTokenInjector;
import com.tsystems.tm.acc.ta.api.RequestSpecBuilders;
import com.tsystems.tm.acc.ta.api.Resetable;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.api.client.olt.commissioning.invoker.ApiClient;
import com.tsystems.tm.api.client.olt.commissioning.invoker.GsonObjectMapper;
import com.tsystems.tm.api.client.olt.commissioning.invoker.JSON;
import lombok.Getter;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.OLT_COMMISSIONING_MS;

@Getter
public class OltCommissioningClient implements Resetable {

    private final ApiClient client;

    public OltCommissioningClient(AuthTokenProvider authTokenProvider) {
        client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
                () -> RequestSpecBuilders.getDefaultWithAuth(
                        GsonObjectMapper.gson(),
                        new GigabitUrlBuilder(OLT_COMMISSIONING_MS)
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
