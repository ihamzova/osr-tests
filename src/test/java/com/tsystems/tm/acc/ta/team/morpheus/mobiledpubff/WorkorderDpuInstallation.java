package com.tsystems.tm.acc.ta.team.morpheus.mobiledpubff;

import com.tsystems.tm.acc.data.osr.models.dpu.DpuCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.data.morpheus.wiremock.MorpeusWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.MobileDpuBffRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class WorkorderDpuInstallation extends BaseTest  {
    private final OsrTestContext osrTestContext = OsrTestContext.get();
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

    OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DefaultOltForCommissioningPositive);
    Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DefaultPositive);

try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "workorderDpuInstallationPositive"))
    {
        new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                .addWorkorderDpuInstallationStub(dpu)
                .build()
                .publish();

        mobileDpuBffRobot.getWorkorder(2);
        mobileDpuBffRobot.startWorkorder(2);
        mobileDpuBffRobot.completeWorkorder(2);
    }
    }
}
