package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.helpers.WiremockHelper;
import com.tsystems.tm.acc.ta.helpers.WiremockMappingsPublisher;
import io.qameta.allure.Step;

import java.io.File;
import java.io.IOException;

public class WiremockRobot {
    @Step("Upload mock data to wiremock")
    public void initializeWiremock(String pathToWiremockData) {
        WiremockMappingsPublisher publisher = new WiremockMappingsPublisher();
        File path = new File(getClass().getResource(pathToWiremockData).getFile());
        try {
            publisher.publish(path);
        } catch (IOException e) {
            throw new RuntimeException("Wiremock stubs not found. They cannot be uploaded!");
        }
        WiremockHelper.requestsReset();
    }
}
