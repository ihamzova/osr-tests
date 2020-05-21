package com.tsystems.tm.acc.ta.robot.utils;

import com.tsystems.tm.acc.ta.helpers.WiremockHelper;
import com.tsystems.tm.acc.ta.helpers.wiremock.WiremockRequestPatternBuilder;
import com.tsystems.tm.acc.tests.wiremock.client.model.RequestFind;
import com.tsystems.tm.acc.tests.wiremock.client.model.RequestPattern;

import java.time.LocalDateTime;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;

public class WiremockRecordedRequestRetriver {

    private static final Long TIMEOUT = 30_000L;
    private static final Long DELAY = 1_000L;
    private static final String URL_PATTERN = ".*/oltResourceInventory/.*";
    private static final long REQUEST_GAP_ALLOWED_SEC = 3_600;
    private static final String POST = "POST";
    private static final String GET = "GET";

    //System.currentTimeMillis();
    public RequestFind retrieveLastGetRequest(Long timeOfExecution, Long timeout, String url) {
        LocalDateTime end = LocalDateTime.now().plusSeconds(timeout / 1000);

        do {
            RequestPattern requestPattern = new WiremockRequestPatternBuilder().withMethod("GET").withUrl(url).build();
            List<RequestFind> requests = WiremockHelper.requestsFindByCustomPatternAmount(requestPattern, 1).getRequests();
            if (!requests.isEmpty()) {
                for (RequestFind request : requests) {
                    if (Long.valueOf(request.getLoggedDate()) > timeOfExecution)
                        return request;
                }
            }
            sleep(DELAY);
        }
        while (LocalDateTime.now().isBefore(end));
        throw new RuntimeException("DPU request not found");
    }

    public RequestFind retrieveLastGetRequest(Long timeOfExecution, String url) {
        return retrieveLastGetRequest(timeOfExecution, TIMEOUT, url);
    }

    public RequestFind retrieveLastRequest(Long timeOfExecution, String fieldValue, Long timeout, String method, String urlPattern) {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusSeconds(timeout / 1000);


        do {
            RequestPattern requestPattern = new WiremockRequestPatternBuilder().withMethod(method).withUrlPattern(urlPattern).build();
            List<RequestFind> requests = WiremockHelper.requestsFindByCustomPatternAmount(requestPattern, 10).getRequests();
            if (!requests.isEmpty()) {
                for (RequestFind request : requests) {
                    if (request.getBody().contains(fieldValue) && Long.valueOf(request.getLoggedDate()) > timeOfExecution)
                        return request;
                }
            }
            sleep(DELAY);
        }
        while (LocalDateTime.now().isBefore(end));
        throw new RuntimeException("Nothing found");
    }


    public List<RequestFind> retrieveLastRequest(int amount) {
        RequestPattern requestPattern = new WiremockRequestPatternBuilder().withMethod(GET).withUrlPattern(URL_PATTERN).build();
        return WiremockHelper.requestsFindByCustomPatternAmount(requestPattern, amount).getRequests();

    }

}
