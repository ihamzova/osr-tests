package com.tsystems.tm.acc.ta.team.morpheus.mobiledpubff;

import com.tsystems.tm.acc.ta.data.morpheus.wiremock.MorpeusWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.robot.osr.MobileDpuBffRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class WorkorderDpuInstallation extends BaseTest  {
    private MobileDpuBffRobot mobileDpuBffRobot;

@BeforeClass
public void init(){
    mobileDpuBffRobot = new MobileDpuBffRobot();
    WireMockFactory.get().resetToDefaultMappings();
}

    @BeforeMethod
    public void reset() {
        WireMockFactory.get().resetRequests();
    }

@Test
public void workorderDpuInstallation () {
try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "workorderDpuInstallationPositive"))
    {
        new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                .addWorkorderDpuInstallationStub()
                .build()
                .publish();

        mobileDpuBffRobot.getWorkorder(2);
    }
    }
}
