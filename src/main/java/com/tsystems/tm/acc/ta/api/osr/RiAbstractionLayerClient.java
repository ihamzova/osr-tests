package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.BearerHeaderAuthTokenInjector;
import com.tsystems.tm.acc.ta.api.RequestSpecBuilders;
import com.tsystems.tm.acc.ta.api.Resetable;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.tests.osr.ri.abstraction.layer.v1_3_0.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.ri.abstraction.layer.v1_3_0.client.invoker.GsonObjectMapper;
import com.tsystems.tm.acc.tests.osr.ri.abstraction.layer.v1_3_0.client.invoker.JSON;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import lombok.Getter;

import static com.tsystems.tm.acc.tests.osr.ri.abstraction.layer.v1_3_0.client.invoker.GsonObjectMapper.gson;
import static io.restassured.RestAssured.config;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;

@Getter
public class RiAbstractionLayerClient implements Resetable {
  private ApiClient client;

  public RiAbstractionLayerClient() {
    client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
            () -> new RequestSpecBuilder().setConfig(config().objectMapperConfig(objectMapperConfig().defaultObjectMapper(gson())))
                    .addFilter(new RequestLoggingFilter())
                    .addFilter(new ResponseLoggingFilter())
                    .setBaseUri(new OCUrlBuilder("ri-abstraction-layer").buildUri())));
  }

  public RiAbstractionLayerClient(AuthTokenProvider authTokenProvider) {
    client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
            () -> RequestSpecBuilders.getDefaultWithAuth(
                    GsonObjectMapper.gson(),
                    new OCUrlBuilder("ri-abstraction-layer")
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
