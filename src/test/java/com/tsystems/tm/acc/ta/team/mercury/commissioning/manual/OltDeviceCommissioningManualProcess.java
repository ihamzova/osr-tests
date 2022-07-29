package com.tsystems.tm.acc.ta.team.mercury.commissioning.manual;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.data.mercury.wiremock.MercuryWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.data.osr.enums.DevicePortLifeCycleStateUI;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDetailsPage;
import com.tsystems.tm.acc.ta.robot.osr.OltCommissioningRobot;
import com.tsystems.tm.acc.ta.robot.osr.OltDeCommissioningRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.*;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;

@Slf4j
@ServiceLog({ANCP_CONFIGURATION_MS, OLT_DISCOVERY_MS, OLT_RESOURCE_INVENTORY_MS, OLT_UPLINK_MANAGEMENT_MS, PSL_ADAPTER_MS, PSL_TRANSFORMER_MS})
public class OltDeviceCommissioningManualProcess extends GigabitTest {

    final String STUB_GROUP_ID = "OltDeviceCommissioningManualProcess";

    private final OltCommissioningRobot oltCommissioningRobot = new OltCommissioningRobot();
    private final OltDeCommissioningRobot oltDeCommissioningRobot = new OltDeCommissioningRobot();

    OsrTestContext context = OsrTestContext.get();
    private final OltDevice oltDeviceDTAG = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76HE_SDX_6320_16);
    private final OltDevice oltDeviceGFNW = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76Z7_MA5600);
    private final OltDevice oltDeviceGFMM_MA5600 = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_911_1100_76G2_MA5600);
    private final OltDevice oltDeviceGFMM_SDX_6320_16 = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_911_1100_76G4_SDX_6320_16);

    private final WireMockMappingsContext mappingsContextOsr = new WireMockMappingsContext(WireMockFactory.get(), STUB_GROUP_ID);
    private final WireMockMappingsContext mappingsContextTeam = new WireMockMappingsContext(WireMockFactory.get(), STUB_GROUP_ID);

    @BeforeClass
    public void init() {
        //WireMockFactory.get().resetToDefaultMappings();
        List<OltDevice> olts = Arrays.asList(oltDeviceDTAG, oltDeviceGFNW, oltDeviceGFMM_MA5600, oltDeviceGFMM_SDX_6320_16);

        olts.forEach(oltCommissioningRobot::clearResourceInventoryDataBase);

        olts.forEach(olt ->
                new OsrWireMockMappingsContextBuilder(mappingsContextOsr)
                        .addSealMock(olt)
                        //.addPslMock(olt)
                        .addPslMockXML(olt)
                        .build()
                        .publish()
                        .publishedHook(savePublishedToDefaultDir())
                        .publishedHook(attachStubsToAllureReport())
        );
        olts.forEach(olt ->
                new MercuryWireMockMappingsContextBuilder(mappingsContextTeam)
                        .addPonInventoryMock(olt)
                        .addAccessLineInventoryMock()
                        .addRebellUewegeMock(olt)
                        .build()
                        .publish()
                        .publishedHook(savePublishedToDefaultDir())
                        .publishedHook(attachStubsToAllureReport())
        );

    }

    @AfterClass
    public void cleanUp() {
        mappingsContextOsr.close();
        mappingsContextOsr
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());

        mappingsContextTeam.close();
        mappingsContextTeam
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());
    }

    @Test(description = "DIGIHUB-53694 Manual commissioning for Adtran SDX 6320-16 as DTAG user")
    public void SearchAndDiscoverOltDTAGTest() {

        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiDTAG);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        OltDetailsPage oltDetailsPage = oltCommissioningRobot.startManualOltCommissioningWithoutAccessLines(oltDeviceDTAG);

        oltCommissioningRobot.checkOltCommissioningResultWithoutAccessLines(oltDeviceDTAG, COMPOSITE_PARTY_ID_DTAG);
        oltCommissioningRobot.checkUplink(oltDeviceDTAG);

        //Thread.sleep(1000); // prevent Init Deconfiguration of ANCP session runs in error
        oltDetailsPage.deconfigureAncpSession();
        oltDetailsPage.deleteUplinkConfiguration();
        Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

        // check uplink port life cycle state
        oltDetailsPage.openPortView(oltDeviceDTAG.getOltSlot());
        Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(oltDeviceDTAG.getOltSlot(), oltDeviceDTAG.getOltPort()), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

        oltDeCommissioningRobot.checkUplinkIsDeleted(oltDeviceDTAG.getEndsz());
    }


    @Test(description = "DIGIHUB-53713 Manual commissioning for MA5600 as GFNW user")
    public void SearchAndDiscoverOltGFNWTest() {

        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiGFNW);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        OltDetailsPage oltDetailsPage = oltCommissioningRobot.startManualOltCommissioningWithoutAccessLines(oltDeviceGFNW);

        oltCommissioningRobot.checkOltCommissioningResultWithoutAccessLines(oltDeviceGFNW, COMPOSITE_PARTY_ID_GFNW);
        oltCommissioningRobot.checkUplink(oltDeviceGFNW);

        //Thread.sleep(1000); // prevent Init Deconfiguration of ANCP session runs in error
        oltDetailsPage.deconfigureAncpSession();
        oltDetailsPage.deleteUplinkConfiguration();
        Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

        // check uplink port life cycle state
        oltDetailsPage.openPortView(oltDeviceGFNW.getOltSlot());
        Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(oltDeviceGFNW.getOltSlot(), oltDeviceGFNW.getOltPort()), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

        oltDeCommissioningRobot.checkUplinkIsDeleted(oltDeviceGFNW.getEndsz());
    }

    @Test(description = "DIGIHUB-160199 Manual commissioning for MA5600 as GFMM user")
    public void SearchAndDiscoverOltGFMM_MA5600Test() throws InterruptedException {

        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiGFMM);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        OltDetailsPage oltDetailsPage = oltCommissioningRobot.startManualOltCommissioningWithoutAccessLines(oltDeviceGFMM_MA5600);

        oltCommissioningRobot.checkOltCommissioningResultWithoutAccessLines(oltDeviceGFMM_MA5600, COMPOSITE_PARTY_ID_GFMM);
        oltCommissioningRobot.checkUplink(oltDeviceGFMM_MA5600);

        //Thread.sleep(1000); // prevent Init Deconfiguration of ANCP session runs in error
        oltDetailsPage.deconfigureAncpSession();
        oltDetailsPage.deleteUplinkConfiguration();
        Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

        // check uplink port life cycle state
        oltDetailsPage.openPortView(oltDeviceGFMM_MA5600.getOltSlot());
        Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(oltDeviceGFMM_MA5600.getOltSlot(), oltDeviceGFMM_MA5600.getOltPort()), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

        oltDeCommissioningRobot.checkUplinkIsDeleted(oltDeviceGFMM_MA5600.getEndsz());
        oltDeCommissioningRobot.startDeviceDeletion(oltDeviceGFMM_MA5600, oltDetailsPage);
    }

    @Test(description = "DIGIHUB-160234 Manual commissioning for Adtran SDX 6320-16 as GFMM user")
    public void SearchAndDiscoverOltGFMM_SDX_6320Test() throws InterruptedException {

        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiGFMM);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        OltDetailsPage oltDetailsPage = oltCommissioningRobot.startManualOltCommissioningWithoutAccessLines(oltDeviceGFMM_SDX_6320_16);

        oltCommissioningRobot.checkOltCommissioningResultWithoutAccessLines(oltDeviceGFMM_SDX_6320_16, COMPOSITE_PARTY_ID_GFMM);
        oltCommissioningRobot.checkUplink(oltDeviceGFMM_SDX_6320_16);

        //Thread.sleep(1000); // prevent Init Deconfiguration of ANCP session runs in error
        oltDetailsPage.deconfigureAncpSession();
        oltDetailsPage.deleteUplinkConfiguration();
        Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

        // check uplink port life cycle state
        oltDetailsPage.openPortView(oltDeviceGFMM_SDX_6320_16.getOltSlot());
        Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(oltDeviceGFMM_SDX_6320_16.getOltSlot(), oltDeviceGFMM_SDX_6320_16.getOltPort()), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

        oltDeCommissioningRobot.checkUplinkIsDeleted(oltDeviceGFMM_SDX_6320_16.getEndsz());
        oltDeCommissioningRobot.startDeviceDeletion(oltDeviceGFMM_MA5600, oltDetailsPage);
    }
}


