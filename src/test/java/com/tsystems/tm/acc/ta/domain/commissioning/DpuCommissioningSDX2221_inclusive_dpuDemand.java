package com.tsystems.tm.acc.ta.domain.commissioning;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.dpudevice.DpuDeviceCase;
import com.tsystems.tm.acc.ta.data.mercury.wiremock.MercuryWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.data.morpheus.wiremock.MorpeusWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
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
public class DpuCommissioningSDX2221_inclusive_dpuDemand extends GigabitTest {
    private OsrTestContext context = OsrTestContext.get();
    private DpuCommissioningUiRobot dpuCommissioningUiRobot = new DpuCommissioningUiRobot();
    private ETCDRobot etcdRobot = new ETCDRobot();
    private DpuDevice dpuDevice;

    private WireMockMappingsContext mappingsContext;

    @BeforeClass
    public void init() {
        dpuCommissioningUiRobot.restoreOsrDbState();

        dpuDevice = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.EndSz_49_30_179_71G0_SDX2221);
        dpuCommissioningUiRobot.clearResourceInventoryDataBase(dpuDevice);
        dpuCommissioningUiRobot.prepareResourceInventoryDataBase(dpuDevice);
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

        dpuCommissioningUiRobot.startDpuCommissioning(dpuDevice);
        dpuCommissioningUiRobot.checkDpuCommissioningResult(dpuDevice);
        List<String> values = Arrays.asList(
                "EXECUTED successfully [Read DPU device data]",
                "EXECUTED successfully [update LifecycleStatus of DPU to INSTALLING]",
                "EXECUTED successfully [update LifecycleStatus of DPU.uplinkPort to INSTALLING]",
                "EXECUTED successfully [Read OltPonPort Data]",
                "EXECUTED successfully [Read OltUpLinkPortData]",
                "EXECUTED successfully [Get Unique OnuId for DPU]",
                "EXECUTED successfully [Read BackhaulId]",
                "EXECUTED successfully [Read BackhaulId]",
                "EXECUTED successfully [Deprovision FTTH on PonPort][call]",
                "EXECUTED successfully [Deprovision FTTH on PonPort][callback]",
                "EXECUTED successfully [Configure ANCP on BNG][call]",
                "EXECUTED successfully [Configure ANCP on BNG][callback]",
                "EXECUTED successfully [Read ANCP Info]",
                "EXECUTED successfully [Create DpuAtOltConfiguration If Missing]",
                "EXECUTED successfully [Configure DPU at OLT][call]",
                "EXECUTED successfully [Configure DPU at OLT][callback]",
                "EXECUTED successfully [Set DpuAtOltConfiguration.configurationState to active]",
                "EXECUTED successfully [Create DpuEmsConfiguration If Missing]",
                "EXECUTED successfully [Configure DPU Ems][call]",
                "EXECUTED successfully [Configure DPU Ems][callback]",
                "EXECUTED successfully [Set DpuEmsConfiguration.configurationState to active]",
                "EXECUTED successfully [Provision FTTB access provisioning on DPU][call]",
                "EXECUTED successfully [Provision FTTB access provisioning on DPU][callback]");
        etcdRobot.checkEtcdValues(dpuCommissioningUiRobot.getBusinessKey(), Collections.emptyList());

    }
}
