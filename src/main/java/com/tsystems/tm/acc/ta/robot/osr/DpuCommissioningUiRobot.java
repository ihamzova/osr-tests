package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.ResponseSpecBuilders;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.*;
import com.tsystems.tm.acc.ta.data.osr.enums.DevicePortLifeCycleStateUI;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.pages.osr.dpucommissioning.DpuCreatePage;
import com.tsystems.tm.acc.ta.pages.osr.dpucommissioning.DpuEditPage;
import com.tsystems.tm.acc.ta.pages.osr.dpucommissioning.DpuInfoPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_19_0.client.model.*;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.model.*;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_NO_CONTENT_204;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.*;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.OLT_BFF_PROXY_MS;

@Slf4j
public class DpuCommissioningUiRobot {

    private static final Integer HTTP_CODE_OK_200 = 200;
    private static final String DPU_ANCP_CONFIGURATION_STATE = "aktiv";
    private static final String OLT_EMS_CONFIGURATION_STATE = "ACTIVE";
    private static final String DPU_EMS_CONFIGURATION_STATE = "ACTIVE";
    private static final Integer LINE_ID_POOL_PER_PORT = 32;
    private static final Integer HOME_ID_POOL_PER_PORT = 32;

    private static final AuthTokenProvider authTokenProviderOltBffProxy = new RhssoClientFlowAuthTokenProvider(OLT_BFF_PROXY_MS, RhssoHelper.getSecretOfGigabitHub(OLT_BFF_PROXY_MS));

    private OltResourceInventoryClient oltResourceInventoryClient = new OltResourceInventoryClient(authTokenProviderOltBffProxy);
    private DeviceResourceInventoryManagementClient deviceResourceInventoryManagementClient = new DeviceResourceInventoryManagementClient(authTokenProviderOltBffProxy);
    private DeviceTestDataManagementClient deviceTestDataManagementClient = new DeviceTestDataManagementClient();
    private AccessLineResourceInventoryClient accessLineResourceInventoryClient = new AccessLineResourceInventoryClient(authTokenProviderOltBffProxy);
    private AccessLineResourceInventoryFillDbClient accessLineResourceInventoryFillDbClient = new AccessLineResourceInventoryFillDbClient(authTokenProviderOltBffProxy);
    private String businessKey;

    @Step("Start automatic dpu creation and commissioning process")
    public void startDpuCommissioning(DpuDevice dpuDevice) {
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        oltSearchPage = oltSearchPage.searchNotDiscoveredByEndSz(dpuDevice.getEndsz());

        oltSearchPage.pressCreateDpuButton();

        DpuCreatePage dpuCreatePage = new DpuCreatePage();
        dpuCreatePage.validateUrl();
        dpuCreatePage.startDpuCreation(dpuDevice);

        dpuCreatePage.openDpuInfoPage();

        DpuInfoPage dpuInfoPage = new DpuInfoPage();
        dpuInfoPage.validateUrl();
        Assert.assertEquals(DpuInfoPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString(), "Initial Device LifeCycleState mismatch");
        Assert.assertEquals(DpuInfoPage.getPortLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString(), "Initial Port LifeCycleState mismatch");
        dpuInfoPage.startDpuCommissioning();
        businessKey = dpuInfoPage.getBusinessKey();
        Assert.assertNotNull(businessKey);
        Assert.assertFalse(businessKey.isEmpty());

        Assert.assertEquals(DpuInfoPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.INSTALLING.toString(), "Device LifeCycleState after com. mismatch");


        dpuInfoPage.openDpuConfiguraionTab();
        Assert.assertEquals(DpuInfoPage.getDpuKlsId(), dpuDevice.getKlsId(), "UI KlsId missmatch");
        /*
        Assert.assertTrue(DpuInfoPage.getDpuAncpConfigState().contains(DPU_ANCP_CONFIGURATION_STATE), "DPU ANCP configuration state mismatch");
        Assert.assertTrue(DpuInfoPage.getOltEmsConfigState().contains(OLT_EMS_CONFIGURATION_STATE), "OLT EMS configuration state mismatch");
        Assert.assertTrue(DpuInfoPage.getDpuEmsConfigState().contains(DPU_EMS_CONFIGURATION_STATE), "DPU EMS configuration state mismatch");
        Assert.assertTrue(DpuInfoPage.getOltEmsDpuEndsz().contains(dpuDevice.getEndsz()), "OLT EMS DPU EndSz mismatch");
        Assert.assertTrue(DpuInfoPage.getOltEmsOltEndsz().contains(dpuDevice.getOltEndsz()), "OLT EMS OLT EndSz mismatch");
        Assert.assertTrue(DpuInfoPage.getDpuEmsDpuEndsz().contains(dpuDevice.getEndsz()), "DPU EMS DPU EndSz mismatch");
         */
        dpuInfoPage.openDpuAccessLinesTab();
        dpuInfoPage.openDpuPortsTab();
        //DIGIHUB-79622
        dpuInfoPage.openDpuEditPage();
        DpuEditPage dpuEditPage = new DpuEditPage();
        dpuEditPage.validateUrl();
        dpuEditPage.SetDpuState();

        Assert.assertEquals(DpuInfoPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.OPERATING.toString(), "Device LifeCycleState after com. mismatch");
        Assert.assertEquals(DpuInfoPage.getPortLifeCycleState(), DevicePortLifeCycleStateUI.OPERATING.toString(), "Port LifeCycleState after com. mismatch");
    }

    @Step("Checks data in ri after commissioning process")
    public void checkDpuCommissioningResult(DpuDevice dpuDevice) {

        List<Device> deviceList = deviceResourceInventoryManagementClient.getClient().device().listDevice()
                .endSzQuery(dpuDevice.getEndsz()).depthQuery(3).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertEquals(deviceList.size(), 1L, "DPU deviceList.size mismatch");
        Assert.assertEquals(deviceList.get(0).getDeviceType(), DeviceType.DPU, "DPU DeviceType mismatch");
        Assert.assertEquals(deviceList.get(0).getEndSz(), dpuDevice.getEndsz(), "DPU endSz mismatch");
        Device deviceAfterCommissioning = deviceList.get(0);

        Assert.assertEquals(deviceAfterCommissioning.getKlsId().toString(), dpuDevice.getKlsId(), "DPU KlsId missmatch");
        Assert.assertEquals(deviceAfterCommissioning.getFiberOnLocationId(), dpuDevice.getFiberOnLocationId(), "DPU FiberOnLocationId missmatch");

        // DIGIHUB-79622 check port and device lifecycle state
        Assert.assertEquals(deviceAfterCommissioning.getLifeCycleState(), LifeCycleState.OPERATING, "DPU LifeCycleState mismatch");
        Assert.assertEquals(DpuInfoPage.getPortLifeCycleState(), DevicePortLifeCycleStateUI.OPERATING.toString(), "Port LifeCycleState mismatch");

        // check AccessLines, corresponding profiles and pools
        int numberOfAccessLinesForProvisioning = Integer.parseInt(dpuDevice.getPonConnectionGe()) + Integer.parseInt(dpuDevice.getPonConnectionWe());

        List<AccessLineDto> wgFttbAccessLines = accessLineResourceInventoryClient.getClient().accessLineController().searchAccessLines()
                .body(new SearchAccessLineDto().endSz(dpuDevice.getEndsz())
                        .referenceType(ReferenceType.DPU))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)))
                .stream().filter(accessLineDto -> accessLineDto.getStatus().equals(AccessLineStatus.WALLED_GARDEN)).collect(Collectors.toList());

        List<AccessLineDto> ftthAccessLines = accessLineResourceInventoryClient.getClient().accessLineController().searchAccessLines()
                .body(new SearchAccessLineDto().endSz(dpuDevice.getOltEndsz())
                        .slotNumber(dpuDevice.getOltGponSlot())
                        .portNumber(dpuDevice.getOltGponPort())
                        .referenceType(ReferenceType.OLT))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)))
                .stream().filter(accessLineDto -> accessLineDto.getTechnology().equals(AccessLineTechnology.GPON)).collect(Collectors.toList());

        long wgFttbAccessLinesCount = wgFttbAccessLines.size();
        long ftthAccessLinesCount = ftthAccessLines.size();

        long countFttbNeOltStateActive = wgFttbAccessLines.stream().map(AccessLineDto::getFttbNeProfile)
                .filter(fttbNeProfile -> fttbNeProfile != null && ProfileState.ACTIVE.equals(fttbNeProfile.getStateOlt())).count();

        long countFttbNeMosaicActive = wgFttbAccessLines.stream().map(AccessLineDto::getFttbNeProfile)
                .filter(fttbNeProfile -> fttbNeProfile != null && ProfileState.ACTIVE.equals(fttbNeProfile.getStateMosaic())).count();

        long countDefaultNetworkLineProfilesActive = wgFttbAccessLines.stream().map(AccessLineDto::getDefaultNetworkLineProfile)
                .filter(defaultNetworkLineProfile -> defaultNetworkLineProfile != null && ProfileState.ACTIVE.equals(defaultNetworkLineProfile.getState())).count();

        List<Integer> expectedOnuAccessIdsList = IntStream.rangeClosed(1, numberOfAccessLinesForProvisioning)
                .boxed().collect(Collectors.toList());

        List<Integer> onuAccessIds = wgFttbAccessLines.stream().map(AccessLineDto::getFttbNeProfile).map(FttbNeProfileDto::getOnuAccessId).
                map(OnuAccessIdDto::getOnuAccessId).sorted().collect(Collectors.toList());

        List<LineIdDto> lineIdDtos = accessLineResourceInventoryClient.getClient().lineIdController().searchLineIds()
                .body(new SearchLineIdDto().endSz(dpuDevice.getOltEndsz())
                        .slotNumber(dpuDevice.getOltGponSlot())
                        .portNumber(dpuDevice.getOltGponPort()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        long freeLineIdCount = lineIdDtos.stream().filter(lineIdDto -> LineIdStatus.FREE.equals(lineIdDto.getStatus())).count();
        long usedLineIdCount = lineIdDtos.stream().filter(lineIdDto -> LineIdStatus.USED.equals(lineIdDto.getStatus())).count();

        List<HomeIdDto> homeIdDtos = accessLineResourceInventoryClient.getClient().homeIdController().searchHomeIds()
                .body(new SearchHomeIdDto().endSz(dpuDevice.getOltEndsz())
                        .slotNumber(dpuDevice.getOltGponSlot())
                        .portNumber(dpuDevice.getOltGponPort()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        long homeIdsCount = homeIdDtos.stream().filter(homeIdDto -> homeIdDto.getStatus().equals(HomeIdStatus.FREE)).count();

        Assert.assertEquals(wgFttbAccessLinesCount, numberOfAccessLinesForProvisioning, "FTTB AccessLines count is incorrect");
        Assert.assertEquals(ftthAccessLinesCount, 0, "There are FTTH AccessLines on the OLT port");
        Assert.assertEquals(countFttbNeOltStateActive, numberOfAccessLinesForProvisioning, "FTTB NE Profiles (Olt State) count is incorrect");
        Assert.assertEquals(countFttbNeMosaicActive, numberOfAccessLinesForProvisioning, "FTTB NE Profiles (Mosaic State) count is incorrect");
        Assert.assertEquals(countDefaultNetworkLineProfilesActive, numberOfAccessLinesForProvisioning, "Default NetworkLine Profile count is incorrect");
        Assert.assertEquals(onuAccessIds, expectedOnuAccessIdsList, "OnuAccessIds are incorrect");
        Assert.assertEquals(freeLineIdCount, LINE_ID_POOL_PER_PORT - numberOfAccessLinesForProvisioning, "Free LineIDs count is incorrect");
        Assert.assertEquals(usedLineIdCount, numberOfAccessLinesForProvisioning, "Used LineIDs count is incorrect");
        Assert.assertEquals(homeIdsCount, HOME_ID_POOL_PER_PORT.intValue(), "HomeIDs count is incorrect");
    }

    @Step("Restore accessline-resource-inventory Database state")
    public void restoreOsrDbState() {
        accessLineResourceInventoryFillDbClient.getClient().fillDatabase().deleteDatabase()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Clear devices (DPU and OLT) in olt-resource-inventory database")
    public void clearResourceInventoryDataBase(DpuDevice dpuDevice) {

        if (FEATURE_ANCP_MIGRATION_ACTIVE) {
            deviceTestDataManagementClient.getClient().deviceTestDataManagement().deleteTestData().deviceEndSzQuery(dpuDevice.getOltEndsz())
                    .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
            deviceTestDataManagementClient.getClient().deviceTestDataManagement().deleteTestData().deviceEndSzQuery(dpuDevice.getEndsz())
                    .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
        } else {
            oltResourceInventoryClient.getClient().testDataManagementController().deleteDevice().endszQuery(dpuDevice.getEndsz())
                    .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
            oltResourceInventoryClient.getClient().testDataManagementController().deleteDevice().endszQuery(dpuDevice.getOltEndsz())
                    .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        }
    }

    @Step("Create the precondition olt-resource-inventory and access-line-resource-inventory database")
    public void prepareResourceInventoryDataBase(DpuDevice dpuDevice) {
        if (FEATURE_ANCP_MIGRATION_ACTIVE) {
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
                    .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));

        } else {
            oltResourceInventoryClient.getClient().testDataManagementController().createDevice()
                    ._01EmsNbiNameQuery(EMS_NBI_NAME_MA5600)
                    ._02EndszQuery(dpuDevice.getOltEndsz())
                    ._03SlotNumbersQuery("3,4,5,19")
                    ._06KLSIdQuery("12377812")
                    ._07CompositePartyIDQuery(COMPOSITE_PARTY_ID_DTAG.toString())
                    ._08UplinkEndszQuery(dpuDevice.getBngEndsz())
                    ._10ANCPConfQuery("1")
                    ._11RunSQLQuery("1")
                    .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        }

        accessLineResourceInventoryFillDbClient.getClient().fillDatabase().fillDatabaseForOltCommissioning()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("get businessKey")
    public String getBusinessKey() {
        return businessKey;
    }
}
