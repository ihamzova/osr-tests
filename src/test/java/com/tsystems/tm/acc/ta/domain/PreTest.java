package com.tsystems.tm.acc.ta.domain;

import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import io.qameta.allure.Description;
import org.testng.annotations.Test;

public class PreTest extends BaseTest {
    @Test(description = "Reset Wiremock")
    @Description("Reset Wiremock")
    public void resetWiremock() {
        WireMockFactory.get().resetRequests();
        WireMockFactory.get().resetToDefaultMappings();
    }
}
