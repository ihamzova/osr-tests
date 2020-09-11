package com.tsystems.tm.acc.ta.domain.commissioning;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.dpudevice.DpuDeviceCase;
import com.tsystems.tm.acc.ta.data.morpheus.wiremock.MorpeusWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.DpuCommissioningRobot;
import com.tsystems.tm.acc.ta.robot.osr.DpuCommissioningUiRobot;
import com.tsystems.tm.acc.ta.robot.osr.ETCDRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;

@ServiceLog(NETWORK_LINE_PROFILE_MANAGEMENT_MS)
@ServiceLog(ACCESS_LINE_RESOURCE_INVENTORY_MS)
@ServiceLog(WG_ACCESS_PROVISIONING_MS)
@ServiceLog(OLT_RESOURCE_INVENTORY_MS)
@ServiceLog(EA_EXT_ROUTE_MS)
@ServiceLog(LINE_ID_GENERATOR_MS)
@ServiceLog(ACCESS_LINE_MANAGEMENT)
@ServiceLog("ancp-configuration")
@ServiceLog("dpu-commissioning")
public class DpuCommissioningSDX2221 extends BaseTest {
    private OsrTestContext context = OsrTestContext.get();
    private DpuCommissioningUiRobot dpuCommissioningUiRobot = new DpuCommissioningUiRobot();
    private AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
    private ETCDRobot etcdRobot = new ETCDRobot();
    private DpuDevice dpuDevice;

    private WireMockMappingsContext mappingsContext;

    @BeforeClass
    public void init() {
        dpuCommissioningUiRobot.restoreOsrDbState();

        dpuDevice = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.EndSz_49_30_179_71G0_SDX2221);
        dpuCommissioningUiRobot.clearResourceInventoryDataBase(dpuDevice);
        dpuCommissioningUiRobot.prepareResourceInventoryDataBase(dpuDevice);
        accessLineRiRobot.fillDatabaseForOltCommissioning();

    }

    @AfterClass
    public void teardown() {
        mappingsContext.close();
        dpuCommissioningUiRobot.clearResourceInventoryDataBase(dpuDevice);
        dpuCommissioningUiRobot.restoreOsrDbState();
        accessLineRiRobot.clearDatabase();
    }

    @Test(description = "DPU creation and DPU-Commissioning (device : SDX2221-16 TP-AC-MELT) case")
    @TmsLink("DIGIHUB-75965")
    @Description("DPU creation and DPU-Commissioning (device : SDX2221-16 TP-AC-MELT) case")
    @Owner("@t-systems.com")
    public void dpuCommissioning() throws InterruptedException {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());

        mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningPositiveDomain");
        new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                .addSEALMocksForDomain(dpuDevice)
                .build()
                .publish();

        dpuCommissioningUiRobot.startDpuCommissioning(dpuDevice);
        dpuCommissioningUiRobot.checkDpuCommissioningResult(dpuDevice);

        etcdRobot.checkEtcdValues(dpuCommissioningUiRobot.getBusinessKey(),
                Arrays.asList(
                        "EXECUTED Successfuly [Read DPU device data]",
                        "EXECUTED Successfuly [update LifecycleStatus of DPU to INSTALLING]",
                        "EXECUTED Successfuly [update LifecycleStatus of DPU.uplinkPort to INSTALLING]",
                        "EXECUTED Successfuly [Read OltPonPort Data]",
                        "EXECUTED Successfuly [Read OltUpLinkPortData]",
                        "EXECUTED Successfuly [Get Unique OnuId for DPU]",
                        "EXECUTED Successfuly [Read BackhaulId]",
                        "EXECUTED Successfuly [Read BackhaulId]",
                        "EXECUTED Successfuly [Deprovision FTTH on PonPort][call]",
                        "EXECUTED Successfuly [Deprovision FTTH on PonPort][callback]",
                        "EXECUTED Successfuly [Configure ANCP on BNG][call]",
                        "EXECUTED Successfuly [Configure ANCP on BNG][callback]",
                        "EXECUTED Successfuly [Read ANCP Info]",
                        "EXECUTED Successfuly [Create DpuAtOltConfiguration If Missing]",
                        "EXECUTED Successfuly [Configure DPU at OLT][call]",
                        "EXECUTED Successfuly [Configure DPU at OLT][callback]",
                        "EXECUTED Successfuly [Set DpuAtOltConfiguration.configurationState to active]",
                        "EXECUTED Successfuly [Create DpuEmsConfiguration If Missing]",
                        "EXECUTED Successfuly [Configure DPU Ems][call]",
                        "EXECUTED Successfuly [Configure DPU Ems][callback]",
                        "EXECUTED Successfuly [Set DpuEmsConfiguration.configurationState to active]",
                        "EXECUTED Successfuly [Provision FTTB access provisioning on DPU][call]",
                        "EXECUTED Successfuly [Provision FTTB access provisioning on DPU][callback]",
                        "EXECUTED Successfuly [update LifecycleStatus of DPU to OPERATING]",
                        "EXECUTED Successfuly [update LifecycleStatus of DPU.uplinkPort to OPERATING]"));

    }
}
