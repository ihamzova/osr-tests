package com.tsystems.tm.acc.ta.api.osr;

import com.tsystems.tm.acc.ta.api.APMHeaderAuthTokenInjector;
import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RequestSpecBuilders;
import com.tsystems.tm.acc.ta.api.Resetable;
import com.tsystems.tm.acc.ta.helpers.morpheus.UserTokenProvider;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.tests.osr.mobile.dpu.bff.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.mobile.dpu.bff.invoker.GsonObjectMapper;
import com.tsystems.tm.acc.tests.osr.mobile.dpu.bff.invoker.JSON;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static com.tsystems.tm.acc.tests.osr.olt.resource.inventory.external.v4_6_0.client.invoker.GsonObjectMapper.gson;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;

@Slf4j
public class MobileDpuBffClient implements Resetable {
	private static final AuthTokenProvider authTokenProvider = new UserTokenProvider(
			"mobile_dpu_admin", "Mobile!3","mobile-dpu-user");


	@Getter
	private ApiClient client;

    public MobileDpuBffClient() {
		client = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(
				() -> RequestSpecBuilders.getDefaultWithAuth(
						GsonObjectMapper.gson(),
						new OCUrlBuilder("mobile-dpu-bff-app")
								.withoutSuffix()
								.withoutAuth()
								.buildUri(),
						new APMHeaderAuthTokenInjector(authTokenProvider))
		));
	}


	public static JSON json() {
		return new JSON();
	}

	@Override
	public void reset() {
	}
}