package com.tsystems.tm.acc.ta.robot.utils;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class WiremockRecordedRequestRetriver {
    private static final Long TIMEOUT = 30_000L;

    public void isPostRequestCalled(List<Consumer<RequestPatternBuilder>> consumers, UrlPattern url) {
        isPostRequestCalled(consumers, TIMEOUT, url);
    }

    public void isPostRequestCalled(UrlPattern url) {
        isPostRequestCalled(Collections.emptyList(), TIMEOUT, url);
    }

    public void isGetRequestCalled(UrlPattern url) {
        isGetRequestCalled(TIMEOUT, url);
    }

    public void isPutRequestCalled(List<Consumer<RequestPatternBuilder>> consumers, UrlPattern url) {
        isPutRequestCalled(consumers, TIMEOUT, url);
    }

    public void isPutRequestCalled(UrlPattern url) {
        isPutRequestCalled(Collections.emptyList(), TIMEOUT, url);
    }

    public void isPatchRequestCalled(List<Consumer<RequestPatternBuilder>> consumers, UrlPattern url) {
        isPatchRequestCalled(consumers, TIMEOUT, url);
    }

    public void isDeleteRequestCalled(UrlPattern url) {
        isDeleteRequestCalled(Collections.emptyList(), TIMEOUT, url);
    }

    public void isGetRequestCalled(Long timeout, UrlPattern url) {
        RequestPatternBuilder requestPatternBuilder = getRequestedFor(url);
        WireMockFactory.get().retrieve(
                WireMock.moreThanOrExactly(1),
                requestPatternBuilder,
                timeout);
    }

    public void isPostRequestCalled(List<Consumer<RequestPatternBuilder>> consumers, Long timeout, UrlPattern url) {
        RequestPatternBuilder requestPatternBuilder = postRequestedFor(url);
        consumers.forEach(c -> c.accept(requestPatternBuilder));
        WireMockFactory.get().retrieve(
                WireMock.moreThanOrExactly(1),
                requestPatternBuilder,
                timeout);
    }

    public void isPutRequestCalled(List<Consumer<RequestPatternBuilder>> consumers, Long timeout, UrlPattern url) {
        RequestPatternBuilder requestPatternBuilder = putRequestedFor(url);
        consumers.forEach(c -> c.accept(requestPatternBuilder));
        WireMockFactory.get().retrieve(
                WireMock.moreThanOrExactly(1),
                requestPatternBuilder,
                timeout);
    }

    public void isPatchRequestCalled(List<Consumer<RequestPatternBuilder>> consumers, Long timeout, UrlPattern url) {
        RequestPatternBuilder requestPatternBuilder = patchRequestedFor(url);
        consumers.forEach(c -> c.accept(requestPatternBuilder));
        WireMockFactory.get().retrieve(
                WireMock.moreThanOrExactly(1),
                requestPatternBuilder,
                timeout);
    }

    public void isDeleteRequestCalled(List<Consumer<RequestPatternBuilder>> consumers, Long timeout, UrlPattern url) {
        RequestPatternBuilder requestPatternBuilder = deleteRequestedFor(url);
        consumers.forEach(c -> c.accept(requestPatternBuilder));
        WireMockFactory.get().retrieve(
                WireMock.moreThanOrExactly(1),
                requestPatternBuilder,
                timeout);
    }

    public void isPatchRequestNotCalled(UrlPattern url) {
        isPatchRequestNotCalled(Collections.emptyList(), url);
    }

    public void isPostRequestNotCalled(UrlPattern url) {
        isPostRequestNotCalled(Collections.emptyList(), url);
    }

    public void isPutRequestNotCalled(UrlPathPattern url) {
        isPutRequestNotCalled(Collections.emptyList(), url);
    }

    public void isDeleteRequestNotCalled(UrlPattern url) {
        isDeleteRequestNotCalled(Collections.emptyList(), url);
    }

    public void isPatchRequestNotCalled(List<Consumer<RequestPatternBuilder>> consumers, UrlPattern url) {
        RequestPatternBuilder requestPatternBuilder = patchRequestedFor(url);
        consumers.forEach(c -> c.accept(requestPatternBuilder));
        WireMockFactory.get().retrieve(
                WireMock.exactly(0),
                requestPatternBuilder,
                TIMEOUT);
    }

    public void isPostRequestNotCalled(List<Consumer<RequestPatternBuilder>> consumers, UrlPattern url) {
        RequestPatternBuilder requestPatternBuilder = postRequestedFor(url);
        consumers.forEach(c -> c.accept(requestPatternBuilder));
        WireMockFactory.get().retrieve(
                WireMock.exactly(0),
                requestPatternBuilder,
                TIMEOUT);
    }

    public void isPutRequestNotCalled(List<Consumer<RequestPatternBuilder>> consumers, UrlPathPattern url) {
        RequestPatternBuilder requestPatternBuilder = putRequestedFor(url);
        consumers.forEach(c -> c.accept(requestPatternBuilder));
        WireMockFactory.get().retrieve(
                WireMock.exactly(0),
                requestPatternBuilder,
                TIMEOUT);
    }

    public void isGetRequestNotCalled(UrlPattern url) {
        RequestPatternBuilder requestPatternBuilder = getRequestedFor(url);
        WireMockFactory.get().retrieve(
                WireMock.exactly(0),
                requestPatternBuilder,
                TIMEOUT);
    }

    public void isDeleteRequestNotCalled(List<Consumer<RequestPatternBuilder>> consumers, UrlPattern url) {
        RequestPatternBuilder requestPatternBuilder = postRequestedFor(url);
        consumers.forEach(c -> c.accept(requestPatternBuilder));
        WireMockFactory.get().retrieve(
                WireMock.exactly(0),
                requestPatternBuilder,
                TIMEOUT);
    }
}
