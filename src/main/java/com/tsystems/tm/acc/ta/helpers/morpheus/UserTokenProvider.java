package com.tsystems.tm.acc.ta.helpers.morpheus;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.tests.rhsso.client.model.Token;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//based on existing fiberbau-tests impl
@Slf4j
public class UserTokenProvider implements AuthTokenProvider {

	public static final String ENDPOINT = "auth/realms/GigabitHub/protocol/openid-connect/token";
	private final String username;
	private final String password;
	private final String client_id;

	public UserTokenProvider(String username, String password, String client_id) {
		this.client_id = client_id;
		this.username = username;
		this.password = password;
	}

	public Token getToken() {
		log.info("Getting User token for " + this.username);
		URL url = new OCUrlBuilder("rhsso-public").withoutSuffix().withEndpoint(ENDPOINT).build();
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost post = new HttpPost(url.toString());
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("username", this.username));
		params.add(new BasicNameValuePair("password", this.password));
		params.add(new BasicNameValuePair("client_id", this.client_id));
		params.add(new BasicNameValuePair("grant_type", "password"));
		params.add(new BasicNameValuePair("typ", "ID"));
		params.add(new BasicNameValuePair("scope", "openid"));
		HttpResponse response = null;
		try {
			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			response = httpclient.execute(post);
			HttpEntity entity = response.getEntity();
			String responseString = EntityUtils.toString(entity);
			JsonObject result = new Gson().fromJson(responseString, JsonObject.class);
			log.info("successfully got id token: " + result.get("id_token").getAsString());
			Token token = new Token();
			token.setIdToken(result.get("id_token").getAsString());
			return token;
		} catch (IOException e) {
			throw new RuntimeException("cant get user token");
		}
	}

	@Override
	public void revokeToken() {
		//not needed
	}
}
