package com.tsystems.tm.acc.ta.domain.commissioning;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.defaultnetworklineprofile.DefaultNetworkLineProfileCase;
import com.tsystems.tm.acc.data.osr.models.dpudemand.DpuDemandCase;
import com.tsystems.tm.acc.data.osr.models.dpudevice.DpuDeviceCase;
import com.tsystems.tm.acc.data.osr.models.fttbneprofile.FttbNeProfileCase;
import com.tsystems.tm.acc.data.osr.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.mercury.wiremock.MercuryWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.data.morpheus.wiremock.MorpeusWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.pages.osr.mobiledpu.MobileDpuPage;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.DpuCommissioningUiRobot;
import com.tsystems.tm.acc.ta.robot.osr.DpuPlanningRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemandCreate;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Owner;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;

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
        DPU_COMMISSIONING_MS,
        DPU_PLANNING_MS
})
public class DpuCommissioningAtBngPlatformFromMobileDpu extends GigabitTest {
    private final OsrTestContext context = OsrTestContext.get();
    private final DpuCommissioningUiRobot dpuCommissioningUiRobot = new DpuCommissioningUiRobot();
    private final AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
    private final DpuPlanningRobot dpuPlanningRobot = new DpuPlanningRobot();
    private DpuDevice dpuDevice;
    private DpuDemand dpuDemand;
    private PortProvisioning oltDevice;
    private FttbNeProfile expectedFttbNeProfile;
    private DefaultNetworkLineProfile expectedDefaultNlProfile;
    private int numberOfAccessLines;
    private static final String CREATE_DPU_DEMAND = "/domain/osr/commissioning/createDpuDemandForMobileDpuDomain.json";
    private MobileDpuPage mobileDpuPage;

    private WireMockMappingsContext mappingsContext;

    @BeforeClass
    public void init() {
        dpuCommissioningUiRobot.restoreOsrDbState();
        dpuPlanningRobot.changeFeatureToggleDpuConfigurationWithA4Support(true);

        dpuDevice = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.EndSz_49_30_306_71G1_SDX2221);
        dpuDemand = context.getData().getDpuDemandDataProvider().get(DpuDemandCase.DpuDemand_49_30_306_71G1);
        oltDevice = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.EndSz_49_30_179_76H8);
        expectedFttbNeProfile = context.getData().getFttbNeProfileDataProvider().get(FttbNeProfileCase.fttbNeProfileTwistedPair);
        expectedDefaultNlProfile = context.getData().getDefaultNetworkLineProfileDataProvider()
                .get(DefaultNetworkLineProfileCase.defaultNLProfileFttbTP);

        numberOfAccessLines = Integer.parseInt(dpuDemand.getNumberOfNeededDpuPorts());
        DpuDemandCreate createDpuDemandRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND);
        dpuPlanningRobot.createDpuDemand(createDpuDemandRequestData);
        dpuPlanningRobot.fulfillDpuDemandDomain(dpuPlanningRobot.findDpuDemandByFolIdDomain(dpuDemand));

        dpuCommissioningUiRobot.clearResourceInventoryDataBase(dpuDevice);
        dpuCommissioningUiRobot.prepareResourceInventoryDataBase(dpuDevice);
        accessLineRiRobot.fillDatabaseForOltCommissioningV2WithOlt(1, 1, oltDevice.getEndSz(), oltDevice.getSlotNumber());
    }

    @AfterClass (alwaysRun = true)
    public void teardown() {
        dpuPlanningRobot.deleteDpuDemand(dpuPlanningRobot.findDpuDemandByFolIdDomain(dpuDemand));
        mappingsContext.close();
        mappingsContext
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());
        dpuCommissioningUiRobot.clearResourceInventoryDataBase(dpuDevice);
        dpuCommissioningUiRobot.restoreOsrDbState();
        dpuPlanningRobot.changeFeatureToggleDpuConfigurationWithA4Support(false);
    }

    @Test(description = "DPU Commissioning V2 from Mobile DPU UI: BNG Platform")
    @Owner("DL-T-Magic.Mercury@telekom.de, DL-Morpheus@telekom.de, DL_T-Magic.U-Piter@t-systems.com")
    public void dpuCommissioningV2BngPlatform() throws MalformedURLException {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOMobileDpu);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningBngPlatformDomain");
        new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                .addMocksForDomainFromMobileDpu(dpuDevice)
                .build()
                .publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningPositiveDomain");
        new MercuryWireMockMappingsContextBuilder(mappingsContext)
                .addGigaAreasLocationMock(dpuDevice)
                .build()
                .publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        mobileDpuPage = MobileDpuPage.openPage();
        mobileDpuPage.selectDpuDemand();
        mobileDpuPage.goToNextPage();
        mobileDpuPage.inputSerialNumber();
        mobileDpuPage.goToNextPage();
        mobileDpuPage.startCommissioning();
        mobileDpuPage.goToNextPage();
        mobileDpuPage.finishCommissioning();

        dpuCommissioningUiRobot.checkDpuCommissioningResult(dpuDevice);
        accessLineRiRobot.checkAccessLinesAfterFttbProvisioning(oltDevice, dpuDevice, expectedFttbNeProfile, expectedDefaultNlProfile, numberOfAccessLines);
    }

    @Test(dependsOnMethods = "dpuCommissioningV2BngPlatform", description = "DPU Decommissioning V2 from OS&R UI: BNG Platform")
    @Owner("DL-T-Magic.Mercury@telekom.de, DL-Morpheus@telekom.de, DL_T-Magic.U-Piter@t-systems.com")
    public void dpuDecommissioningV2BngPlatform() throws InterruptedException {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        dpuCommissioningUiRobot.startDpuDecommissioningV2(dpuDevice);

        // todo check if process is finished  and remove sleep
        for (int i = 0; i < 12; ++i) {
            Thread.sleep(30_000);
            if (dpuCommissioningUiRobot.countOfDevices(dpuDevice.getEndsz()) == 0) {
                break;
            }
        }

        dpuCommissioningUiRobot.checkDpuDeviceDeletionResult(dpuDevice);
        accessLineRiRobot.checkPhysicalResourceRefCountFttb(dpuDevice,
                oltDevice,
                0,
                1, 1);
        accessLineRiRobot.checkFtthPortParameters(oltDevice);
    }

}
