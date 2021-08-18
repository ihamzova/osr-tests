package com.tsystems.tm.acc.ta.team.morpheus.mobiledpubff;

import com.tsystems.tm.acc.data.osr.models.dpu.DpuCase;
import com.tsystems.tm.acc.ta.data.morpheus.wiremock.MorpeusWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.MobileDpuBffRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.attachStubsToAllureReport;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.savePublishedToDefaultDir;

public class WorkorderDpuInstallation extends GigabitTest  {
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
                    .addGetWorkorderStub(true, "DPU_INSTALLATION")
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            mobileDpuBffRobot.getWorkorder(woid);
        }
    }

    @Test
    public void getWorkorder404() {

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "workorderNegative"))
        {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addGetWorkorderStub(false, "DPU_INSTALLATION")
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            mobileDpuBffRobot.getWorkorderNegative(woid);
        }
    }

    @Test
    public void getWorkorderWrongWorkorderType() {

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "workorderNegative"))
        {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addGetWorkorderStub(true, "GF_AP_INSTALLATION")
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            mobileDpuBffRobot.getWorkorder400(woid);
        }
    }

    @Test
    public void startWorkorder() {

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "workorderPositive"))
        {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addPatchInProgressWorkorderStub(true)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            mobileDpuBffRobot.startWorkorder(woid);
        }
    }

    @Test
    public void startWorkorder404() {

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "workorderNegative"))
        {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addPatchInProgressWorkorderStub(false)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            mobileDpuBffRobot.startWorkorderNegative(woid);
        }
    }

    @Test
    public void completeWorkorder() {

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "workorderPositive"))
        {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addPatchCreatedWorkorderStub(true)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            mobileDpuBffRobot.completeWorkorder(woid);
        }
    }

    @Test
    public void completeWorkorder404() {

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "workorderNegative"))
        {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addPatchCreatedWorkorderStub(false)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            mobileDpuBffRobot.completeWorkorderNegative(woid);
        }
    }


    @Test
    public void getDpuByFolId() {

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "OLRIPositive")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addGetDpuDeviceFolIdStub(dpu, true)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            String shortName = "SDX2221-04 TP-AC-M-FTTB ETSI";
            String deviceName = "4 PORT G.FAST FTTB ONU, TWISTED PAIR, AC POWER, WITH MELT";
            mobileDpuBffRobot.getDpuByFolId(folId, dpuEndsz, serialNumber, deviceName, shortName);
        }
    }

    @Test
    public void getDpuByFolId404(){

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "OLRINegative"))
        {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addGetDpuDeviceFolIdStub(dpu, false)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

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
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            mobileDpuBffRobot.updateDpuSerialNumber(folId, dpuEndsz, serialNumber);
        }
    }

    @Test
    public void updateDpuSerialNumber404() {

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "OLRINegative")) {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addGetDpuDeviceMobileDpuBffEndsz(dpu, true)
                    .addPatchDpuDeviceMobileDpuBff(dpu, false)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            mobileDpuBffRobot.updateDpuSerialNumberNegative(dpuEndsz, serialNumber);
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
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            mobileDpuBffRobot.setDpuAsOperating(folId, dpuEndsz, serialNumber);
        }
    }

    @Test
    public void setDpuAsOperatingPatchDpuDevice404() {

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "OLRINegative"))
        {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addGetDpuDeviceMobileDpuBffEndsz(dpu, true)
                    .addPatchDpuDeviceMobileDpuBff(dpu, false)
                    .addPatchDpuPortMobileDpuBff(dpu, true)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            mobileDpuBffRobot.setDpuAsOperatingNegative(dpuEndsz, serialNumber);
        }
    }

    @Test
    public void setDpuAsOperatingPatchDpuPort404() {

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "OLRINegative"))
        {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addGetDpuDeviceMobileDpuBffEndsz(dpu, true)
                    .addPatchDpuDeviceMobileDpuBff(dpu, true)
                    .addPatchDpuPortMobileDpuBff(dpu, false)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            mobileDpuBffRobot.setDpuAsOperatingNegative(dpuEndsz, serialNumber);
        }
    }

    @Test
    public void setDpuAsOperatingGetDpuDevice404() {

        try (WireMockMappingsContext mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "OLRINegative"))
        {
            new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                    .addGetDpuDeviceMobileDpuBffEndsz(dpu, false)
                    .addPatchDpuDeviceMobileDpuBff(dpu, true)
                    .addPatchDpuPortMobileDpuBff(dpu, true)
                    .build()
                    .publish()
                    .publishedHook(savePublishedToDefaultDir())
                    .publishedHook(attachStubsToAllureReport());

            mobileDpuBffRobot.setDpuAsOperatingNegative(dpuEndsz, serialNumber);
        }
    }

    @Test
    public void startDpuComissioningPositive() {

            mobileDpuBffRobot.triggerStartDpuCommissioning(dpuEndsz);
    }


}
