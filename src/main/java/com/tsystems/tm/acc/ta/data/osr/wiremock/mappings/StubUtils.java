package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.tsystems.tm.acc.tests.osr.rebell.client.invoker.JSON;
import com.tsystems.tm.acc.wiremock.webhook.WebhookPostServeAction;
import com.tsystems.tm.acc.wiremock.webhook.WebhookPostServeActionDefinition;

public class StubUtils {

    public static String serialize(Object obj) {
        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }

    public static WebhookPostServeActionDefinition costumizedWebhookWithBody(String body, String url) {
        return WebhookPostServeAction.webhook()
                .withUrl(url)
                .withHeader("Content-Type", "application/json")
                .withMethod(RequestMethod.PUT)
                .withBody(body);
    }

}
