package com.tsystems.tm.acc.ta.team.mercury.commissioning.manual;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.dpudevice.DpuDeviceCase;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.mercury.wiremock.MercuryWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.data.osr.enums.DevicePortLifeCycleStateUI;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.pages.osr.dpucommissioning.DpuCreatePage;
import com.tsystems.tm.acc.ta.pages.osr.dpucommissioning.DpuInfoPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.Device;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

@Slf4j
public class DpuDeviceCommissioningProcess extends BaseTest {

    private static final Integer HTTP_CODE_OK_200 = 200;
    private static final String DPU_ANCP_CONFIGURATION_STATE = "aktiv";
    private static final String OLT_EMS_CONFIGURATION_STATE_LOCATOR = "active";
    private static final String DPU_EMS_CONFIGURATION_STATE_LOCATOR = "active";
    private OltResourceInventoryClient oltResourceInventoryClient;
    private DpuDevice dpuDevice;
    private String businessKey;
    private WireMockMappingsContext mappingsContext;

    @BeforeClass
    public void init() {

        oltResourceInventoryClient = new OltResourceInventoryClient();

        OsrTestContext context = OsrTestContext.get();
        dpuDevice = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.EndSz_49_30_179_71G0_SDX2221);

        WireMockFactory.get().resetToDefaultMappings();
        mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningPositiveDomain");
        new MercuryWireMockMappingsContextBuilder(mappingsContext)
                .addGigaAreasLocationMock(dpuDevice)
                .build()
                .publish();

        oltResourceInventoryClient.getClient().testDataManagementController().deleteDevice().endszQuery(dpuDevice.getOltEndsz())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        oltResourceInventoryClient.getClient().testDataManagementController().createDevice()
                ._01EmsNbiNameQuery("MA5600T")
                ._02EndszQuery(dpuDevice.getOltEndsz())
                ._03SlotNumbersQuery("3,4,5,19")
                ._06KLSIdQuery("12377812")
                ._07CompositePartyIDQuery("10001")
                ._08UplinkEndszQuery(dpuDevice.getBngEndsz())
                ._10ANCPConfQuery("1")
                ._11RunSQLQuery("1")
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @AfterClass
    public void cleanUp() {

        WireMockFactory.get().resetToDefaultMappings();

        oltResourceInventoryClient.getClient().testDataManagementController().deleteDevice().endszQuery(dpuDevice.getOltEndsz())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        oltResourceInventoryClient.getClient().testDataManagementController().deleteDevice().endszQuery(dpuDevice.getEndsz())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Test(description = "DIGIHUB-53694 Manual commissioning for MA5800 with DTAG user on team environment")
    @TmsLink("DIGIHUB-53694") // Jira Id for this test in Xray
    @Description("Perform manual commissioning for not discovered MA5800 device as DTAG user")
    public void createDpu() throws InterruptedException {

        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiDTAG);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());
        dpuDevice = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.EndSz_49_30_179_71G0_SDX2221);

        String endSz = dpuDevice.getEndsz();
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();

        oltSearchPage.searchNotDiscoveredByEndSz(endSz);
        Thread.sleep(1000);
        DpuCreatePage dpuCreatePage = oltSearchPage.pressCreateDpuButton();

        dpuCreatePage.validateUrl();
        dpuCreatePage.startDpuCreation(dpuDevice);
        Thread.sleep(1000);

        dpuCreatePage.openDpuInfoPage();

        Thread.sleep(100);
        // internal test
        List<Device>  deviceList = oltResourceInventoryClient.getClient().deviceInternalController().findDeviceByCriteria()
                .endszQuery(endSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertEquals(deviceList.size(), 1L, "deviceList.size is wrong");
        Device patchDevice = deviceList.get(0);
        log.info("FiberOnLocationId = {}", patchDevice.getFiberOnLocationId());  // 71520003000100
        Assert.assertEquals(dpuDevice.getFiberOnLocationId(), patchDevice.getFiberOnLocationId(), "FiberOnLocationId missmatch" );

        DpuInfoPage dpuInfoPage = new DpuInfoPage();
        dpuInfoPage.validateUrl();
        Assert.assertEquals(DpuInfoPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());
        Assert.assertEquals(DpuInfoPage.getPortLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());
        dpuInfoPage.startDpuCommissioning();
        businessKey = dpuInfoPage.getBusinessKey();
        Assert.assertNotNull(businessKey);
        Assert.assertFalse(businessKey.isEmpty());

        Thread.sleep(1000);
        /*testable only on domain level
         * Assert.assertEquals(DpuInfoPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.OPERATING.toString());
         Assert.assertEquals(DpuInfoPage.getPortLifeCycleState(dpuDevice.getOltGponPort()), DevicePortLifeCycleStateUI.OPERATING.toString());*/
        dpuInfoPage.openDpuConfiguraionTab();
        //Assert.assertEquals(DpuInfoPage.getDpuAncpConfigState(), DPU_ANCP_CONFIGURATION_STATE);
        //Assert.assertEquals(DpuInfoPage.getOltEmsConfigState(), OLT_EMS_CONFIGURATION_STATE_LOCATOR);
        //Assert.assertEquals(DpuInfoPage.getDpuEmsConfigState(), DPU_EMS_CONFIGURATION_STATE_LOCATOR);
        Thread.sleep(1000);
        dpuInfoPage.openDpuAccessLinesTab();
        dpuInfoPage.openDpuPortsTab();

    }
}
