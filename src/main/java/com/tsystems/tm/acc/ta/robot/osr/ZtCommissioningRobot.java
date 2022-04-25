package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.ResponseSpecBuilders;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.DeviceResourceInventoryManagementClient;
import com.tsystems.tm.acc.ta.api.osr.DeviceTestDataManagementClient;
import com.tsystems.tm.acc.ta.api.osr.OltCommissioningClient;
import com.tsystems.tm.acc.ta.api.osr.OltCommissioningEventListenerClient;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.pages.osr.ztcommissioning.OltInstallationPage;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.model.Device;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.model.DeviceType;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.model.LifeCycleState;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.model.OltZtcConfiguration;
import com.tsystems.tm.api.client.olt.commissioning.event.listener.model.Event;
import com.tsystems.tm.api.client.olt.commissioning.event.listener.model.EventData;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_NO_CONTENT_204;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.COMPOSITE_PARTY_ID_DTAG;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.EMS_NBI_NAME_SDX6320_16;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.OLT_BFF_PROXY_MS;
import static org.testng.Assert.assertEquals;

@Slf4j
public class ZtCommissioningRobot {

    private static final Integer TIMEOUT_FOR_ZTC_COMMISSIONING = 2 * 60_000;

    private static final AuthTokenProvider authTokenProvider = new RhssoClientFlowAuthTokenProvider(OLT_BFF_PROXY_MS, RhssoHelper.getSecretOfGigabitHub(OLT_BFF_PROXY_MS));
    private final DeviceResourceInventoryManagementClient deviceResourceInventoryManagementClient = new DeviceResourceInventoryManagementClient(authTokenProvider);
    private final DeviceTestDataManagementClient deviceTestDataManagementClient = new DeviceTestDataManagementClient();
    private final OltCommissioningClient oltCommissioningClient = new OltCommissioningClient(authTokenProvider);
    private final OltCommissioningEventListenerClient oltCommissioningEventListenerClient = new OltCommissioningEventListenerClient();

    @Step("Start the zero touch commissioning process")
    public void startZtCommissioning(OltDevice oltDevice, String acid) {
        OltInstallationPage.openInstallationPage(acid)
                .validateUrl()
                .startZtCommissioningProcess(oltDevice)
                .checkForceProceedLinkExist(TIMEOUT_FOR_ZTC_COMMISSIONING);
    }

    @Step("Start the zero touch commissioning process and wait for error message.")
    public void startZtCommissioningWithError(OltDevice oltDevice, String acid) {
        OltInstallationPage.openInstallationPage(acid)
                .validateUrl()
                .startZtCommissioningProcess(oltDevice)
                .waitZtCommissioningProcessErrorMessage(TIMEOUT_FOR_ZTC_COMMISSIONING);
    }

    @Step("Restart the zero touch commissioning process")
    public void restartZtCommissioning(OltDevice oltDevice) {
        new OltInstallationPage()
                .startZtCommissioningProcess(oltDevice)
                .checkForceProceedLinkExist(TIMEOUT_FOR_ZTC_COMMISSIONING);
    }

    public void checkForceProceedLinkExist() {
        new OltInstallationPage().checkForceProceedLinkExist();
    }

    public void continueZtCommissioningWithErrorCallback() {
        new OltInstallationPage()
                .continueZtCommissioningProcessCallbackError(TIMEOUT_FOR_ZTC_COMMISSIONING);
    }

    public void continueZtCommissioningWithError() {
        new OltInstallationPage()
                .continueZtCommissioningProcess()
                .waitZtCommissioningProcessErrorMessage(TIMEOUT_FOR_ZTC_COMMISSIONING);
    }

    public void continueZtCommissioning() {
        new OltInstallationPage()
                .continueZtCommissioningProcess();
    }

    public void waitZtCommissioningProcessIsFinished() {
        new OltInstallationPage()
                .waitZtCommissioningProcessFinishedSuccess(TIMEOUT_FOR_ZTC_COMMISSIONING);
    }

    @Step("Clear old zero touch commissioning process data")
    public void clearZtCommissioningData(String endSz) {
        List<OltZtcConfiguration> oltZtcConfigurations = deviceResourceInventoryManagementClient.getClient().oltZtcConfiguration().listOltZtcConfiguration()
                .oltEndSzQuery(endSz).executeAs(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));

        if (oltZtcConfigurations.size() > 0) {
            log.info("delete oltZtcConfigurations = {}", oltZtcConfigurations.get(0));
            deviceResourceInventoryManagementClient.getClient().oltZtcConfiguration().deleteOltZtcConfiguration()
                    .idPath(oltZtcConfigurations.get(0).getId()).execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
            oltCommissioningClient.getClient().oltZtCommissioning().deleteZtCommissioning()
                    .processIdPath(oltZtcConfigurations.get(0).getProcessId()).execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
        }
    }

    @Step("Send seal event.")
    public void sendZtCommissioningSealEvent(String endSz, String objectState) {
        oltCommissioningEventListenerClient.getClient().eventListener()
                .deviceEventCallback().body(
                        new Event().data(
                                        new EventData()
                                                .eventTime(OffsetDateTime.now())
                                                .meName(endSz.replace("/", "_"))
                                                .message("netconf Session established")
                                                .objectState(objectState))
                                .datacontenttype("application/json")
                                .id(UUID.randomUUID())
                                .source("'http://seal.telekom.de/device/" + endSz.replace("/", "_"))
                                .specversion("1")
                                .time(OffsetDateTime.now())
                                .type("de.telekom.seal.device.olt.statechanged.v1"))
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
    }

    public Integer getZtCommissioningState(String endSz) {
        List<OltZtcConfiguration> oltZtcConfigurations = deviceResourceInventoryManagementClient.getClient().oltZtcConfiguration().listOltZtcConfiguration()
                .oltEndSzQuery(endSz).executeAs(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
        if (oltZtcConfigurations.size() > 0) {
            log.info("oltZtcConfigurations state = {}", oltZtcConfigurations.get(0).getState());
            return oltZtcConfigurations.get(0).getState();
        }
        return -1;
    }

    @Step("Verify the oltZtcConfiguration state.")
    public void verifyZtCommissioningState(String endSz, Integer expectedState, Integer bitmask) {
        Integer state =  getZtCommissioningState(endSz) & bitmask;
        Assert.assertEquals(state, expectedState, "oltZtcConfiguration.state mismatch");
    }

    @Step("Verify the OLT device in olt-resource-inventory.")
    public void verifyDeviceSDX3620(OltDevice oltDevice) {
        List<Device> deviceList = deviceResourceInventoryManagementClient.getClient().device().listDevice()
                .endSzQuery(oltDevice.getEndsz()).depthQuery(3).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        Assert.assertEquals(deviceList.size(), 1L, "OLT deviceList.size mismatch");
        Device device = deviceList.get(0);
        Assert.assertEquals(device.getEndSz(), oltDevice.getEndsz(), "OLT EndSz mismatch");

        Assert.assertEquals(device.getEmsNbiName(), EMS_NBI_NAME_SDX6320_16, "EMS NBI name mismatch");
        Assert.assertEquals(device.getDeviceType(), DeviceType.OLT, "DeviceType mismatch");
        Assert.assertEquals(device.getRelatedParty().get(0).getId(), COMPOSITE_PARTY_ID_DTAG.toString(), "composite partyId DTAG mismatch");

        assertEquals(device.getLifeCycleState(), LifeCycleState.OPERATING, "Device LifeCycleState is not in operating state");
        assertEquals(device.getSerialNumber(), oltDevice.getSeriennummer(), "Serial number mismatch");
    }

    @Step("Clear device in olt-resource-inventory databases")
    public void clearResourceInventoryDataBase(String endSz) {
        deviceTestDataManagementClient.getClient().deviceTestDataManagement().deleteTestData().deviceEndSzQuery(endSz)
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }
}
