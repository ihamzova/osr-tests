package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.BearerHeaderAuthTokenInjector;
import com.tsystems.tm.acc.ta.api.RequestSpecBuilders;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.invoker.GsonObjectMapper;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.invoker.JSON;
import lombok.Getter;

@Getter
public class OntOltOrchestratorClient {
  private ApiClient client;

  public OntOltOrchestratorClient(AuthTokenProvider authTokenProvider) {
    client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
            () -> RequestSpecBuilders.getDefaultWithAuth(
                    GsonObjectMapper.gson(),
                    new OCUrlBuilder("ont-olt-orchestrator")
                            .buildUri(),
                    new BearerHeaderAuthTokenInjector(authTokenProvider))
    ));
  }

  public static JSON json() {
    return new JSON();
  }

}
