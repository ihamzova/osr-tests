package com.tsystems.tm.acc.ta.robot.osr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsystems.tm.acc.ta.robot.utils.Authenticator;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_CARRIER_MANAGEMENT_MS;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_NEMO_UPDATER_MS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

@Slf4j
public class A4ResilienceRobot {

    ObjectMapper objectMapper = new ObjectMapper();
    String queueAuthenticate = "a4_user";
    String urlApiGw = "https://apigw-admin-berlinium-03.priv.cl01.gigadev.telekom.de/";

    @Step("Get RedeliveryDelay time")
    public long getRedeliveryDelayNemoUpdater() throws IOException {

        URI uri = new OCUrlBuilder(A4_NEMO_UPDATER_MS).buildUri();
        String url = uri.toString() + "/actuator/env/queue.redelivery-delay";

        Client client = ClientBuilder.newClient();
        WebTarget resource = client.target(url);
        Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
        Response response = request.get();
        String a = response.readEntity(String.class);

        Environment e = objectMapper.readValue(a, Environment.class);

        return Long.parseLong(e.getProperty().getValue());
    }

    @Step("Get RedeliveryDelay time")
    public long getRedeliveryDelayCarrierManagement() throws IOException {

        URI uri = new OCUrlBuilder(A4_CARRIER_MANAGEMENT_MS).buildUri();
        String url = uri.toString() + "/actuator/env/queue.redelivery-delay";

        Client client = ClientBuilder.newClient();
        WebTarget resource = client.target(url);
        Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
        Response response = request.get();
        Environment e = objectMapper.readValue(response.readEntity(String.class), Environment.class);

        return Long.parseLong(e.getProperty().getValue());
    }

    @Step("changeRouteToWiremock")
    public void changeRouteToWiremock(String route) {
        String routeOfNemo = "resource-order-resource-inventory.v1.nemo.logicalResource";

        Client client = ClientBuilder.newClient();
        WebTarget resource = client.target(urlApiGw + "routes/?size=200");
        Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
        try {
            Response response = request.get();
            Routes r = objectMapper.readValue(response.readEntity(String.class), Routes.class);

            List<Data> routeList = r.getData()
                    .stream()
                    .filter(i -> i.getName().equals(route))
                    .collect(Collectors.toList());

            Data routeData = routeList.get(0);
            String uuidOfRoute = routeData.getId();

            List<Data> mockList = r.getData()
                    .stream()
                    .filter(i -> i.getName().contains(routeOfNemo))
                    .collect(Collectors.toList());

            Data mockData = mockList.get(0);

            String uuidOfMockService = mockData.getService().getId();

            Data newRoute = new Data();
            newRoute.setProtocols(new ArrayList<>(Arrays.asList("http", "https")));
            Service newService = new Service();
            newService.setId(uuidOfMockService);
            newRoute.setService(newService);
            newRoute.setId(uuidOfRoute);
            newRoute.setName(route);

            resource = client.target(urlApiGw + "routes/" + uuidOfRoute);
            request = resource.request(MediaType.APPLICATION_JSON);
            response = request.method("PATCH", Entity.json(objectMapper.writeValueAsString(newRoute)));
            assertEquals(response.getStatus(), HttpStatus.SC_OK);
        }catch(Exception e){
            fail("apigw-admin url is missing!");
        }
    }

    @Step("changeRouteToA4ResourceInventoryService")
    public void changeRouteToA4ResourceInventoryService(String route) throws IOException {

        Client client = ClientBuilder.newClient();
        WebTarget resource = client.target(urlApiGw + "routes/");
        Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
        log.debug("Will call " + urlApiGw);
        Response response = request.get();
        log.debug(String.valueOf(response));
        Routes r = objectMapper.readValue(response.readEntity(String.class), Routes.class);
        List<Data> routeList = r.getData()
                .stream()
                .filter(i -> i.getName().equals(route))
                .collect(Collectors.toList());
        String uuidOfRoute = routeList.get(0).getId();

        client = ClientBuilder.newClient();
        resource = client.target(urlApiGw + "services/");
        request = resource.request(MediaType.APPLICATION_JSON);
        response = request.get();
        Routes services = objectMapper.readValue(response.readEntity(String.class), Routes.class);
        List<Data> servicesList = services.getData()
                .stream()
                .filter(i -> i.getName().equals("a4-resource-inventory"))
                .collect(Collectors.toList());
        String uuidOfService = servicesList.get(0).getId();

        Data newRoute = new Data();
        newRoute.setProtocols(new ArrayList<>(Arrays.asList("http", "https")));
        Service newService = new Service();
        newService.setId(uuidOfService);
        newRoute.setService(newService);
        newRoute.setId(uuidOfRoute);
        newRoute.setName(route);

        client = ClientBuilder.newClient();
        resource = client.target(urlApiGw + "routes/" + uuidOfRoute);
        request = resource.request(MediaType.APPLICATION_JSON);
        response = request.method("PATCH", Entity.json(objectMapper.writeValueAsString(newRoute)));
        assertEquals(response.getStatus(), HttpStatus.SC_OK);
    }

    @Step("checkMessagesInQueue")
    public void checkMessagesInQueue(String queue, String expected) throws IOException {
        assertEquals(countMessagesInQueue(queue), expected, "in " + queue);
    }

    @Step("checkMessagesInQueueNemoUpdater")
    public void checkMessagesInQueueNemoUpdater(String queue, int expected) throws IOException {
        assertEquals(Integer.parseInt(countMessagesInQueueNemoUpdater(queue)), expected, "in " + queue);
    }

    @Step("countMessagesInQueue")
    public String countMessagesInQueue(String queue) throws IOException {
        String url = "https://a4-queue-dispatcher-amq-berlinium-03.priv.cl01.gigadev.telekom.de:443/console/jolokia/exec/org.apache.activemq.artemis:broker=%22broker%22,component=addresses,address=%22"+
                queue + "%22,subcomponent=queues,routing-type=%22anycast%22,queue=%22" +
                queue + "%22/countMessages()";

        Client client = ClientBuilder.newClient().register(new Authenticator(queueAuthenticate, queueAuthenticate));
        WebTarget resource = client.target(url);
        Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
        Response response = request.get();
        String responseAsString = response.readEntity(String.class);
        CountMessage cm = objectMapper.readValue(responseAsString, CountMessage.class);
        assertEquals(response.getStatus(), HttpStatus.SC_OK);
        return cm.getValue();
    }

    @Step("countMessagesInQueueNemoUpdater")
    public String countMessagesInQueueNemoUpdater(String queue) throws IOException {
        String url = "https://a4-nemo-updater-amq-berlinium-03.priv.cl01.gigadev.telekom.de:443/console/jolokia/exec/org.apache.activemq.artemis:broker=%22broker%22,component=addresses,address=%22"+
                queue + "%22,subcomponent=queues,routing-type=%22anycast%22,queue=%22" +
                queue + "%22/countMessages()";

        Client client = ClientBuilder.newClient().register(new Authenticator(queueAuthenticate, queueAuthenticate));
        WebTarget resource = client.target(url);
        Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
        Response response = request.get();
        String responseAsString = response.readEntity(String.class);
        CountMessage cm = objectMapper.readValue(responseAsString, CountMessage.class);
        assertEquals(response.getStatus(), HttpStatus.SC_OK);
        return cm.getValue();
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

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
class Routes {
    ArrayList<Data> data;
}

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
class Data {
    String id;
    String name;
    Service service;
    ArrayList<String> protocols;
}

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
class Service {
    String id;
}

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
class CountMessage {
    String value;
}
