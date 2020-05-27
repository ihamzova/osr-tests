package com.tsystems.tm.acc.ta.robot.utils;

import com.tsystems.tm.acc.ta.helpers.WiremockHelper;
import com.tsystems.tm.acc.ta.helpers.wiremock.WiremockRequestPatternBuilder;
import com.tsystems.tm.acc.tests.wiremock.client.model.RequestFind;
import com.tsystems.tm.acc.tests.wiremock.client.model.RequestPattern;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;

public class WiremockRecordedRequestRetriver {

    private static final Long TIMEOUT = 10_000L;
    private static final Long DELAY = 1_000L;

    public boolean isPostRequestCalled(Long timeOfExecution, List<String> fieldValues, Long timeout, String url) {
        LocalDateTime end = LocalDateTime.now().plusSeconds(timeout / 1000);

        do {
            RequestPattern requestPattern = new WiremockRequestPatternBuilder().withMethod("POST").withUrlPattern(url).build();
            List<RequestFind> requests = WiremockHelper.requestsFindByCustomPatternAmount(requestPattern, 0).getRequests();
            if (!requests.isEmpty()) {
                for (RequestFind request : requests) {
                    if (isAllFieldValuesContained(request.getBody(), fieldValues) && Long.valueOf(request.getLoggedDate()) > timeOfExecution)
                        return true;
                }
            }
            sleep(DELAY);
        }
        while (LocalDateTime.now().isBefore(end));
        return false;
    }

    private boolean isAllFieldValuesContained(String body, List<String> fieldValues) {
        Boolean result = true;
        for (String fieldValue : fieldValues) {
            if (!body.contains(fieldValue)) {
                result = false;
            }
        }
        return result;
    }

    public boolean isPostRequestCalled(Long timeOfExecution, List<String> fieldValues, String url) {
        return isPostRequestCalled(timeOfExecution, fieldValues, TIMEOUT, url);
    }

    public boolean isPostRequestCalled(Long timeOfExecution, String url) {
        return isPostRequestCalled(timeOfExecution, new ArrayList<>(), TIMEOUT, url);
    }

    public boolean isGetRequestCalled(Long timeOfExecution, Long timeout, String url) {
        LocalDateTime end = LocalDateTime.now().plusSeconds(timeout / 1000);
        do {
            RequestPattern requestPattern = new WiremockRequestPatternBuilder().withMethod("GET").withUrl(url).build();
            List<RequestFind> requests = WiremockHelper.requestsFindByCustomPatternAmount(requestPattern, 0).getRequests();
            if (!requests.isEmpty()) {
                for (RequestFind request : requests) {
                    if (Long.valueOf(request.getLoggedDate()) > timeOfExecution)
                        return true;
                }
            }
            sleep(DELAY);
        }
        while (LocalDateTime.now().isBefore(end));
        return false;
    }

    public boolean isGetRequestCalled(Long timeOfExecution, String url) {
        return isGetRequestCalled(timeOfExecution, TIMEOUT, url);
    }
}
