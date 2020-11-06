package com.tsystems.tm.acc.ta.robot.osr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsystems.tm.acc.ta.api.osr.A4ResourceInventoryServiceClient;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;

@Slf4j
public class A4ResilienceRobot {

    A4ResourceInventoryServiceClient a4ResourceInventoryService;

    ObjectMapper objectMapper = new ObjectMapper();


    @Step("Get RedeliveryDelay")
    public String getRedeliveryDelay() throws IOException {

        URI uri = new OCUrlBuilder("a4-resource-inventory-service").buildUri();
        String url = uri.toString() + "/actuator/env/queue.redelivery-delay";

        Client client = ClientBuilder.newClient();
        WebTarget resource = client.target(url);
        Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
        Response response = request.get();
        String a = response.readEntity(String.class);

        Environment e = objectMapper.readValue(a, Environment.class);

        log.debug(">>> url = " + url);
        log.debug(">>> delay = " + e.getProperty().getValue());

        log.info(">>> url = " + url);
        log.info(">>> delay = " + e.getProperty().getValue());

        return e.getProperty().getValue();
    }

}

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
class Environment {
    Property property;
}

@Getter
class Property {
    String source;
    String value;
}
