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
    private Dpu dpu = osrTestContext.getData().getDpuDataProvider().get(DpuCase.DefaultPositive);
    private String folId = "1111222233334444555";
    private String serialNumber = "08120000";
    private String dpuEndsz = dpu.getEndSz();
    private long woid = 2;

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
    public void getWorkorder() {

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "workorderPositive"))
        {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addWorkorderStub()
                    .build()
                    .publish();

            mobileDpuBffRobot.getWorkorder(woid);

        }
    }

    @Test
    public void startWorkorder() {

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "workorderPositive"))
        {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addWorkorderStub()
                    .build()
                    .publish();

            mobileDpuBffRobot.startWorkorder(woid);
        }
    }

    @Test
    public void completeWorkorder() {

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "workorderPositive"))
        {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addWorkorderStub()
                    .build()
                    .publish();

            mobileDpuBffRobot.completeWorkorder(woid);
        }
    }

    @Test
public void testBffToWorkorder() {

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

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "OLRIPositive"))
        {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addOlRiStub(dpu)
                    .build()
                    .publish();
            mobileDpuBffRobot.getDpuByFolId(folId, dpuEndsz, serialNumber);
            mobileDpuBffRobot.updateDpuSerialNumber(folId, dpuEndsz, serialNumber);
            mobileDpuBffRobot.setDpuAsOperating(folId, dpuEndsz, serialNumber);
        }

    }

    @Test
    public void getDpuByFolId() {

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "OLRIPositive")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addGetDpuDeviceFolIdStub(dpu, true)
                    .build()
                    .publish();
            mobileDpuBffRobot.getDpuByFolId(folId, dpuEndsz, serialNumber);
        }
    }

    @Test
    public void getDpuByFolId400(){

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "OLRINegative"))
        {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addGetDpuDeviceFolIdStub(dpu, false)
                    .build()
                    .publish();
            mobileDpuBffRobot.getDpuByFolIdNegative(folId);
        }

    }

    @Test
    public void updateDpuSerialNumber() {

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "OLRIPositive")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addGetDpuDeviceMobileDpuBffEndsz(dpu, true)
                    .addPatchDpuDeviceMobileDpuBff(dpu, true)
                    .build()
                    .publish();
            mobileDpuBffRobot.updateDpuSerialNumber(folId, dpuEndsz, serialNumber);
        }
    }

    @Test
    public void setDpuAsOperating() {

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "OLRIPositive"))
        {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addGetDpuDeviceMobileDpuBffEndsz(dpu, true)
                    .addPatchDpuDeviceMobileDpuBff(dpu, true)
                    .addPatchDpuPortMobileDpuBff(dpu, true)
                    .build()
                    .publish();
            mobileDpuBffRobot.setDpuAsOperating(folId, dpuEndsz, serialNumber);

        }
    }

}
