package com.tsystems.tm.acc.ta.team.mercury.commissioning.manual;

import com.codeborne.selenide.WebDriverRunner;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.dpudevice.DpuDeviceCase;
import com.tsystems.tm.acc.ta.api.osr.DeviceResourceInventoryManagementClient;
import com.tsystems.tm.acc.ta.api.osr.DeviceTestDataManagementClient;
import com.tsystems.tm.acc.ta.data.mercury.wiremock.MercuryWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.data.osr.enums.DevicePortLifeCycleStateUI;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.pages.osr.dpucommissioning.DpuCreatePage;
import com.tsystems.tm.acc.ta.pages.osr.dpucommissioning.DpuEditPage;
import com.tsystems.tm.acc.ta.pages.osr.dpucommissioning.DpuInfoPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.ta.robot.osr.DpuCommissioningUiRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.model.Device;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.model.DeviceType;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.model.JsonPatchOperation;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_NO_CONTENT_204;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.COMPOSITE_PARTY_ID_DTAG;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.EMS_NBI_NAME_MA5600;
import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;

@Slf4j
public class DpuDeviceCommissioningProcess extends GigabitTest {

    private final DeviceResourceInventoryManagementClient deviceResourceInventoryManagementClient = new DeviceResourceInventoryManagementClient();
    private final DeviceTestDataManagementClient deviceTestDataManagementClient = new DeviceTestDataManagementClient();
    private DpuDevice dpuDevice;
    private WireMockMappingsContext mappingsContext;
    private final DpuCommissioningUiRobot dpuCommissioningUiRobot = new DpuCommissioningUiRobot();

    @BeforeClass
    public void init() {
        OsrTestContext context = OsrTestContext.get();
        dpuDevice = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.EndSz_49_8571_0_71G4_SDX2221);

        WireMockFactory.get().resetToDefaultMappings();
        mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "dpuCommissioningPositiveDomain");
        new MercuryWireMockMappingsContextBuilder(mappingsContext)
                .addGigaAreasLocationMock(dpuDevice)
                .build()
                .publish();

        clearResourceInventoryDataBase(dpuDevice);
        prepareResourceInventoryDataBase(dpuDevice);
    }

    @AfterClass
    public void cleanUp() {

        WireMockFactory.get().resetToDefaultMappings();
        //clearResourceInventoryDataBase(dpuDevice);
    }

    @Test(description = "DIGIHUB-53694 Manual commissioning for MA5800 with DTAG user on team environment")
    @TmsLink("DIGIHUB-53694") // Jira Id for this test in Xray
    @Description("Perform manual commissioning for not discovered MA5800 device as DTAG user")
    public void createDpu() throws InterruptedException {

        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiDTAG);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        String endSz = dpuDevice.getEndsz();
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();

        oltSearchPage.searchNotDiscoveredByEndSz(endSz);
        Thread.sleep(100);
        DpuCreatePage dpuCreatePage = oltSearchPage.pressCreateDpuButton();

        dpuCreatePage.validateUrl();
        //dpuCreatePage.startDpuCreation(dpuDevice);
        dpuCreatePage.startDpuCreationWithDpuDemand(dpuDevice);
        Thread.sleep(100);

        dpuCreatePage.openDpuInfoPage();

        Thread.sleep(100);

        // internal test
        List<Device> deviceList = deviceResourceInventoryManagementClient.getClient().device().listDevice()
                .endSzQuery(endSz).depthQuery(3).executeAs(checkStatus(HTTP_CODE_OK_200));
        Assert.assertEquals(deviceList.size(), 1L, "OLT deviceList.size mismatch");
        Device patchDevice = deviceList.get(0);
        log.info("FiberOnLocationId = {}", patchDevice.getFiberOnLocationId());  // 71520003000100
        Assert.assertEquals(dpuDevice.getFiberOnLocationId(), patchDevice.getFiberOnLocationId(), "FiberOnLocationId missmatch");

        //page tests
        DpuInfoPage dpuInfoPage = new DpuInfoPage();
        dpuInfoPage.validateUrl();
        Assert.assertEquals(DpuInfoPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());
        Assert.assertEquals(DpuInfoPage.getPortLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

        // for team level test only
        log.info("+++ set lifeCycleState");
        deviceResourceInventoryManagementClient.getClient().device().patchDevice()
                .idPath(patchDevice.getId())
                .body(Collections.singletonList(new JsonPatchOperation().op(JsonPatchOperation.OpEnum.REPLACE)
                        .from("string")
                        .path("/lifeCycleState")
                        .value("INSTALLING")))
                .executeAs(checkStatus(HTTP_CODE_OK_200));
        // ----

        dpuInfoPage.startDpuCommissioning();

        dpuInfoPage.openDpuConfiguraionTab();
        Assert.assertEquals(DpuInfoPage.getDpuKlsId(), dpuDevice.getKlsId(), "UI KlsId missmatch");

        dpuInfoPage.openDpuAccessLinesTab();
        dpuInfoPage.openDpuPortsTab();

        //DIGIHUB-79622
        dpuInfoPage.openDpuEditPage();
        DpuEditPage dpuEditPage = new DpuEditPage();
        dpuEditPage.validateUrl();
        dpuEditPage.SetDpuState();
        Assert.assertEquals(DpuInfoPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.OPERATING.toString());
        Assert.assertEquals(DpuInfoPage.getPortLifeCycleState(), DevicePortLifeCycleStateUI.OPERATING.toString());

        deviceList = deviceResourceInventoryManagementClient.getClient().device().listDevice()
                .endSzQuery(endSz).depthQuery(3).executeAs(checkStatus(HTTP_CODE_OK_200));
        Assert.assertEquals(deviceList.size(), 1L, "DPU deviceList.size mismatch");
        Assert.assertEquals(deviceList.get(0).getDeviceType(), DeviceType.DPU, "DPU DeviceType mismatch");
        Assert.assertEquals(deviceList.get(0).getEndSz(), dpuDevice.getEndsz(), "DPU endSz mismatch");
        Device deviceAfterCommissioning = deviceList.get(0);

        Assert.assertEquals(deviceAfterCommissioning.getKlsId(), dpuDevice.getKlsId(), "DPU KlsId missmatch");
        Assert.assertEquals(deviceAfterCommissioning.getFiberOnLocationId(), dpuDevice.getFiberOnLocationId(), "DPU FiberOnLocationId missmatch");
    }


    @Test(dependsOnMethods = "createDpu", description = "Decommissioning for DPU on team environment")
    //@Test(description = "Decommissioning for DPU on team environment")
    public void deleteDpu() throws InterruptedException {

        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiDTAG);
        setCredentials(loginData.getLogin(), loginData.getPassword());
        //dpuDevice = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.EndSz_49_8571_0_71G4_SDX2221);


        // for team level test only
        List<Device> deviceList = deviceResourceInventoryManagementClient.getClient().device().listDevice()
                .endSzQuery(dpuDevice.getEndsz()).depthQuery(1).executeAs(checkStatus(HTTP_CODE_OK_200));
        Assert.assertEquals(deviceList.size(), 1L, "OLT deviceList.size mismatch");
        Device patchDevice = deviceList.get(0);

        log.info("+++ set lifeCycleState");
        deviceResourceInventoryManagementClient.getClient().device().patchDevice()
                .idPath(patchDevice.getId())
                .body(Collections.singletonList(new JsonPatchOperation().op(JsonPatchOperation.OpEnum.REPLACE)
                        .from("string")
                        .path("/lifeCycleState")
                        .value("NOT_OPERATING")))
                .executeAs(checkStatus(HTTP_CODE_OK_200));
        // ----

        //dpuCommissioningUiRobot.startDpuDecommissioning(dpuDevice);
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        oltSearchPage.searchDiscoveredByEndSz(dpuDevice.getEndsz());
        DpuInfoPage dpuInfoPage = new DpuInfoPage();
        dpuInfoPage.validateUrl();
        dpuInfoPage.startDpuDecommissioningV2();
        Thread.sleep(1000);

        dpuCommissioningUiRobot.checkDpuDecommissioningResult(dpuDevice);

        //dpuCommissioningUiRobot.checkDpuDeviceDeletionResult(dpuDevice);
    }

    public void clearResourceInventoryDataBase(DpuDevice dpuDevice) {
        deviceTestDataManagementClient.getClient().deviceTestDataManagement().deleteTestData().deviceEndSzQuery(dpuDevice.getOltEndsz())
                .execute(checkStatus(HTTP_CODE_NO_CONTENT_204));
        deviceTestDataManagementClient.getClient().deviceTestDataManagement().deleteTestData().deviceEndSzQuery(dpuDevice.getEndsz())
                .execute(checkStatus(HTTP_CODE_NO_CONTENT_204));
    }

    public void prepareResourceInventoryDataBase(DpuDevice dpuDevice) {
        deviceTestDataManagementClient.getClient().deviceTestDataManagement().createTestData()
                .deviceEmsNbiNameQuery(EMS_NBI_NAME_MA5600)
                .deviceEndSzQuery(dpuDevice.getOltEndsz())
                .deviceSlotNumbersQuery("3,4,5,19")
                .deviceKlsIdQuery("12377812")
                .deviceCompositePartyIdQuery(COMPOSITE_PARTY_ID_DTAG.toString())
                .uplinkEndSzQuery(dpuDevice.getBngEndsz())
                .uplinkTargetPortQuery(dpuDevice.getBngDownlinkPort())
                .uplinkAncpConfigurationQuery("1")
                .executeSqlQuery("1")
                .execute(checkStatus(HTTP_CODE_OK_200));
    }
}
