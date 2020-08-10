package com.tsystems.tm.acc.ta.data.osr.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.RequestMethod;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebhookDefinitionModel {
    private String method;
    private String url;
    private Map<String, String> headers;
    private Object jsonBody;
    private Integer fixedDelayMilliseconds;
    private String bodyFileName;

    public WebhookDefinitionModel(RequestMethod method, String url, List<HttpHeader> headers, Body body, Integer fixedDelayMilliseconds, String bodyFileName) {
        this.method = method.getName();
        this.url = url;
        this.headers = new HashMap<>();
        for (HttpHeader header : headers) {
            this.headers.put(header.key(), String.join(",", header.values()));
        }
        try {
            this.jsonBody = body.isBinary() ? null : new ObjectMapper().readValue(body.asString(), Object.class);
        } catch (IOException e) {
            e.printStackTrace();
            this.jsonBody = null;
        }
        this.fixedDelayMilliseconds = fixedDelayMilliseconds;
        this.bodyFileName = bodyFileName;
    }
}
