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
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.DpuCommissioningUiRobot;
import com.tsystems.tm.acc.ta.robot.osr.DpuPlanningRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemandCreate;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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
public class DpuCommissioningSDX2221_inclusive_dpuDemand extends GigabitTest {
    private OsrTestContext context = OsrTestContext.get();
    private DpuCommissioningUiRobot dpuCommissioningUiRobot = new DpuCommissioningUiRobot();
    private AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
    private DpuPlanningRobot dpuPlanningRobot = new DpuPlanningRobot();
    private DpuDevice dpuDevice;
    private DpuDemand dpuDemand;
    private PortProvisioning oltDevice;
    private FttbNeProfile expectedFttbNeProfile;
    private DefaultNetworkLineProfile expectedDefaultNlProfile;
    private int numberOfAcсessLines;
    private static final String CREATE_DPU_DEMAND = "/domain/osr/commissioning/createDpuDemandDomain.json";

    private WireMockMappingsContext mappingsContext;

    @BeforeClass
    public void init() {
        dpuCommissioningUiRobot.restoreOsrDbState();

        dpuDevice = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.EndSz_49_30_179_71G1_SDX2221);
        dpuDemand = context.getData().getDpuDemandDataProvider().get(DpuDemandCase.DpuDemand_49_30_179_71G1);
        oltDevice = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.EndSz_49_30_179_76H7);
        expectedFttbNeProfile = context.getData().getFttbNeProfileDataProvider().get(FttbNeProfileCase.fttbNeProfileCoax);
        expectedDefaultNlProfile = context.getData().getDefaultNetworkLineProfileDataProvider()
                .get(DefaultNetworkLineProfileCase.defaultNLProfileFttbCoax);

        numberOfAcсessLines = Integer.parseInt(dpuDemand.getNumberOfNeededDpuPorts());
        DpuDemandCreate createDpuDemandRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND);
        dpuPlanningRobot.createDpuDemand(createDpuDemandRequestData);
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
    }

    @Test(description = "DPU creation and DPU-Commissioning incl DPU demand (device : SDX2221-04-CX) case")
    @TmsLink("DIGIHUB-127585")
    @Description("DPU creation and DPU-Commissioning (device : SDX2221-04-CX) case")
    @Owner("DL-T-Magic.Mercury@telekom.de, DL-Morpheus@telekom.de, DL_T-Magic.U-Piter@t-systems.com")
    public void dpuCommissioningDpuDemand() {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningPositiveDomain");
        new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                .addMocksForDomainWithDpuDemands(dpuDevice)
                .build()
                .publish();

        mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningPositiveDomain");
        new MercuryWireMockMappingsContextBuilder(mappingsContext)
                .addGigaAreasLocationMock(dpuDevice)
                .build()
                .publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        dpuCommissioningUiRobot.startDpuCommissioning(dpuDevice, true);
        dpuCommissioningUiRobot.checkDpuCommissioningResult(dpuDevice);
        accessLineRiRobot.checkAccessLinesAfterFttbProvisioning(oltDevice, dpuDevice, expectedFttbNeProfile, expectedDefaultNlProfile, numberOfAcсessLines);
        dpuPlanningRobot.checkDpuDemandDomain(dpuPlanningRobot.findDpuDemandByFolIdDomain(dpuDemand));

    }

    @Test(dependsOnMethods = "dpuCommissioningDpuDemand", description = "DPU Decommissioning and DPU deletion incl DPU demand (device : SDX2221-04-CX) case")
    @TmsLink("DIGIHUB-142189")
    @Description("(DPU Decommissioning incl DPU demand (device : SDX2221-04-CX) case")
    @Owner("DL-T-Magic.Mercury@telekom.de, DL-Morpheus@telekom.de, DL_T-Magic.U-Piter@t-systems.com")
    public void dpuDeommissioningDpuDemand() {
        dpuCommissioningUiRobot.startDpuDecommissioning(dpuDevice);
        dpuCommissioningUiRobot.checkDpuDecommissioningResult(dpuDevice);

        dpuCommissioningUiRobot.deleteDpuDevice(dpuDevice);
        dpuCommissioningUiRobot.checkDpuDeviceDelationResult(dpuDevice);
        dpuPlanningRobot.checkDpuDemandAfterDeletionDomain(dpuPlanningRobot.findDpuDemandByFolIdDomain(dpuDemand));
        accessLineRiRobot.checkPhysicalResourceRefCountFttb(dpuDevice,
                oltDevice,
                0,
                1, 1);
        accessLineRiRobot.checkFtthPortParameters(oltDevice);
    }
}
