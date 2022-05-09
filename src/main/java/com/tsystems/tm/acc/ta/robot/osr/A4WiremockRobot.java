package com.tsystems.tm.acc.ta.robot.osr;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import lombok.Getter;
import lombok.Setter;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;


/**
 * The type A 4 wiremock robot.<br>
 * Default values are : <br>
 * count = 1 <br>
 * timeout = 1000<br>
 */
@Getter
@Setter
public class A4WiremockRobot {

    private int count = 1;
    private long timeout = 1000;

    @SuppressWarnings("unused")
    public void checkSyncRequest(String url, RequestMethod method) {
        checkSyncRequest(url, method, this.count, this.timeout);
    }

    @SuppressWarnings("unused")
    public void checkSyncRequest(String url, RequestMethod method, int count) {
        checkSyncRequest(url, method, count, this.timeout);
    }

    /**
     * A convient method to ask Wiremock for URL calls, with altogether 3 methods ( overloading )
     *
     * @param url     to checked URL
     * @param method  request method like get, post, delete
     * @param count   how much URL called
     * @param timeout how long request to wiremock
     */
    public void checkSyncRequest(String url, RequestMethod method, int count, long timeout) {
        WireMockFactory.get().retrieve(
                exactly(count),
                newRequestPattern(
                        method,
                        urlPathEqualTo(url)),
                timeout);
    }
}
