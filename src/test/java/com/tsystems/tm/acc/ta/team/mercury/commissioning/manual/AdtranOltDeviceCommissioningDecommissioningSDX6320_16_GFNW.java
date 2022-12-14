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
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.ta.robot.osr.OltCommissioningRobot;
import com.tsystems.tm.acc.ta.robot.osr.OltDeCommissioningRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.COMPOSITE_PARTY_ID_GFNW;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;

@Slf4j
@ServiceLog({ANCP_CONFIGURATION_MS, OLT_DISCOVERY_MS, OLT_RESOURCE_INVENTORY_MS, OLT_UPLINK_MANAGEMENT_MS, PSL_ADAPTER_MS, PSL_TRANSFORMER_MS})
public class AdtranOltDeviceCommissioningDecommissioningSDX6320_16_GFNW extends GigabitTest {

    private static final int WAIT_TIME_FOR_RENDERING = 5_000;
    private static final Integer WAIT_TIME_FOR_DEVICE_DELETION = 2_000;

    private final OltCommissioningRobot oltCommissioningRobot = new OltCommissioningRobot();
    private final OltDeCommissioningRobot oltDeCommissioningRobot = new OltDeCommissioningRobot();

    OsrTestContext context = OsrTestContext.get();
    private final OltDevice oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76Z8_SDX_6320);

    private WireMockMappingsContext mappingsContext;
    private WireMockMappingsContext mappingsContext2;

    @BeforeClass
    public void init() {

        mappingsContext = new OsrWireMockMappingsContextBuilder(WireMockFactory.get())
                .addSealMock(oltDevice)
                .addPslMock(oltDevice)
                .addPslMockXML(oltDevice)
                .build();

        mappingsContext.publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        mappingsContext2 = new MercuryWireMockMappingsContextBuilder(WireMockFactory.get()) //create mocks
                .addPonInventoryMock(oltDevice)
                .addAccessLineInventoryMock()
                .addRebellUewegeMock(oltDevice)
                .build();

        mappingsContext2.publish()                                              //inject in WM
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        oltCommissioningRobot.clearResourceInventoryDataBase(oltDevice);
    }

    @AfterClass
    public void cleanUp() {
        mappingsContext.close();
        mappingsContext
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());

        mappingsContext2.close();
        mappingsContext2
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());
    }

    @Test(description = "DIGIHUB-104219 Manual commissioning for not discovered SDX 6320-16 device as GFNW user")
    @TmsLink("DIGIHUB-104219") // Jira Id for this test in Xray
    @Description("Perform manual commissioning for not discovered SDX 6320-16 device as GFNW user on team environment")
    public void manuallyAdtranOltCommissioningGFNW() {

        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiGFNW);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        OltDevice oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76Z8_SDX_6320);
        oltCommissioningRobot.startManualOltCommissioningWithoutAccessLines(oltDevice);

        oltCommissioningRobot.checkOltCommissioningResultWithoutAccessLines(oltDevice, COMPOSITE_PARTY_ID_GFNW);
        oltCommissioningRobot.checkUplink(oltDevice);
    }

    @Test(dependsOnMethods = "manuallyAdtranOltCommissioningGFNW", description = "Manual decommissioning for SDX 6320-16 device as GFNW user")
    @TmsLink("DIGIHUB-104221")
    @Description("Manual decommissioning for SDX 6320-16 device as GFNW user on team environment")
    public void manuallyAdtranOltDeCommissioningGFNW() throws InterruptedException {
        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiGFNW);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        OltDevice oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76Z8_SDX_6320);
        String endSz = oltDevice.getEndsz();
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();

        Thread.sleep(WAIT_TIME_FOR_RENDERING); // During the pipeline test no EndSz Search can be selected for the user GFNW if the page is not yet finished.
        OltDetailsPage oltDetailsPage = oltSearchPage.searchDiscoveredOltByParameters(oltDevice);
        Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.OPERATING.toString());
        oltDetailsPage.openPortView(null);
        Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(null, oltDevice.getOltPort()), DevicePortLifeCycleStateUI.OPERATING.toString());

        oltDetailsPage.deconfigureAncpSession();
        oltDetailsPage.deleteUplinkConfiguration();
        Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

        // check uplink port life cycle state
        oltDetailsPage.openPortView(null);
        Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(oltDevice.getOltSlot(), oltDevice.getOltPort()), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

        Thread.sleep(WAIT_TIME_FOR_DEVICE_DELETION); // ensure that the resource inventory database is updated
        oltDeCommissioningRobot.checkUplinkIsDeleted(endSz);
        oltDeCommissioningRobot.startDeviceDeletion(oltDevice, oltDetailsPage);
    }
}