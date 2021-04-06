package com.tsystems.tm.acc.ta.domain;

import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import io.qameta.allure.Description;
import org.testng.annotations.Test;

public class PreTest extends GigabitTest {
    @Test(description = "Reset Wiremock")
    @Description("Reset Wiremock")
    public void resetWiremock() {
        WireMockFactory.get().resetRequests();
        WireMockFactory.get().resetToDefaultMappings();
    }
}
