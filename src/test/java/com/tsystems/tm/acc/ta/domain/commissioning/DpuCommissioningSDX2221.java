package com.tsystems.tm.acc.ta.domain.commissioning;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.defaultnetworklineprofile.DefaultNetworkLineProfileCase;
import com.tsystems.tm.acc.data.osr.models.dpudevice.DpuDeviceCase;
import com.tsystems.tm.acc.data.osr.models.fttbneprofile.FttbNeProfileCase;
import com.tsystems.tm.acc.data.osr.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.mercury.wiremock.MercuryWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.data.morpheus.wiremock.MorpeusWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.DpuCommissioningUiRobot;
import com.tsystems.tm.acc.ta.robot.osr.ETCDRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;

@ServiceLog({
        NETWORK_LINE_PROFILE_MANAGEMENT_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        WG_ACCESS_PROVISIONING_MS,
        WG_FTTB_ACCESS_PROVISIONING_MS,
        OLT_RESOURCE_INVENTORY_MS,
        EA_EXT_ROUTE_MS,
        LINE_ID_GENERATOR_MS,
        ACCESS_LINE_MANAGEMENT,
        ANCP_CONFIGURATION_MS,
        DPU_COMMISSIONING_MS
})
public class DpuCommissioningSDX2221 extends GigabitTest {
    private OsrTestContext context = OsrTestContext.get();
    private DpuCommissioningUiRobot dpuCommissioningUiRobot = new DpuCommissioningUiRobot();
    private AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
    private ETCDRobot etcdRobot = new ETCDRobot();
    private DpuDevice dpuDevice;
    private PortProvisioning oltDevice;
    private FttbNeProfile expectedFttbNeProfile;
    private DefaultNetworkLineProfile expectedDefaultNlProfile;
    private int numberOfAcсessLines;

    private WireMockMappingsContext mappingsContext;

    @BeforeClass
    public void init() {
        dpuCommissioningUiRobot.disableFeatureToogleDpuDemand();

        dpuCommissioningUiRobot.restoreOsrDbState();

        dpuDevice = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.EndSz_49_30_179_71G0_SDX2221);
        oltDevice = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.EndSz_49_30_179_76H1);
        expectedFttbNeProfile = context.getData().getFttbNeProfileDataProvider().get(FttbNeProfileCase.fttbNeProfileTwistedPair);
        expectedDefaultNlProfile = context.getData().getDefaultNetworkLineProfileDataProvider()
                .get(DefaultNetworkLineProfileCase.defaultNLProfileFttbTP);
        numberOfAcсessLines = dpuDevice.getNumberOfAccessLines();
        dpuCommissioningUiRobot.clearResourceInventoryDataBase(dpuDevice);
        dpuCommissioningUiRobot.prepareResourceInventoryDataBase(dpuDevice);
        accessLineRiRobot.fillDatabaseForOltCommissioningV2WithOlt(1, 1, oltDevice.getEndSz(), oltDevice.getSlotNumber());
    }

    @AfterClass
    public void teardown() {
        mappingsContext.close();
        mappingsContext
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());
        dpuCommissioningUiRobot.clearResourceInventoryDataBase(dpuDevice);
        dpuCommissioningUiRobot.restoreOsrDbState();

    }

    @Test(description = "DPU creation and DPU-Commissioning (device : SDX2221-16 TP-AC-MELT) case")
    @TmsLink("DIGIHUB-75965")
    @Description("DPU creation and DPU-Commissioning (device : SDX2221-16 TP-AC-MELT) case")
    @Owner("DL-T-Magic.Mercury@telekom.de")
    public void dpuCommissioning() {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningPositiveDomain");
        new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                .addMocksForDomain(dpuDevice)
                .build()
                .publish();

        mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningPositiveDomain");
        new MercuryWireMockMappingsContextBuilder(mappingsContext)
                .addGigaAreasLocationMock(dpuDevice)
                .build()
                .publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        dpuCommissioningUiRobot.startDpuCommissioning(dpuDevice, false);
        accessLineRiRobot.checkAccessLinesAfterFttbProvisioning(oltDevice, dpuDevice, expectedFttbNeProfile, expectedDefaultNlProfile, numberOfAcсessLines);
        dpuCommissioningUiRobot.checkDpuCommissioningResult(dpuDevice);

    }
}
