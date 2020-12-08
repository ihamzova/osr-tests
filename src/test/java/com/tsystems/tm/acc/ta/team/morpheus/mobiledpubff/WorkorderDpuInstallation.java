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
public void testBffToWorkorder() {

    OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DefaultOltForCommissioningPositive);
    Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DefaultPositive);

    long woid = 2;

try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "workorderPositive"))
    {
        new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                .addWorkorderStub()
                .build()
                .publish();

        mobileDpuBffRobot.getWorkorder(woid);
        mobileDpuBffRobot.startWorkorder(woid);
        mobileDpuBffRobot.completeWorkorder(woid);
    }
    }

    @Test
    public void testBffToOLRI(){

        OltDevice olt = osrTestContext.getData().getOltDeviceDataProvider().get(OltDeviceCase.DefaultOltForCommissioningPositive);
        Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DefaultPositive);

        String folId = "1111222233334444555";
        String serialNumber = "08120000";
        String dpuEndsz = dpu.getEndSz();

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "OLRIPositive"))
        {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addOlRiStub(dpu)
                    .build()
                    .publish();
            mobileDpuBffRobot.getDpuByFolId(folId, dpuEndsz, serialNumber);
            mobileDpuBffRobot.updateDpuSerialNumber(folId, dpuEndsz, serialNumber);
           // mobileDpuBffRobot.setDpuAsOperating(folId, dpuEndsz, serialNumber);
        }

    }
}
