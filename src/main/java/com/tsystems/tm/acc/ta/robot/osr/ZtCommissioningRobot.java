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
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.model.OltZtcConfiguration;
import com.tsystems.tm.api.client.olt.commissioning.event.listener.model.Event;
import com.tsystems.tm.api.client.olt.commissioning.event.listener.model.EventData;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_NO_CONTENT_204;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.OLT_BFF_PROXY_MS;

@Slf4j
public class ZtCommissioningRobot {

    private static final Integer TIMEOUT_FOR_ZTC_COMMISSIONING = 2 * 60_000;

    private static final AuthTokenProvider authTokenProvider = new RhssoClientFlowAuthTokenProvider(OLT_BFF_PROXY_MS, RhssoHelper.getSecretOfGigabitHub(OLT_BFF_PROXY_MS));
    //private UplinkResourceInventoryManagementClient uplinkResourceInventoryManagementClient = new UplinkResourceInventoryManagementClient(authTokenProvider);
    //private AncpResourceInventoryManagementClient ancpResourceInventoryManagementClient = new AncpResourceInventoryManagementClient(authTokenProvider);
    private final DeviceResourceInventoryManagementClient deviceResourceInventoryManagementClient = new DeviceResourceInventoryManagementClient(authTokenProvider);
    private final DeviceTestDataManagementClient deviceTestDataManagementClient = new DeviceTestDataManagementClient();
    private final OltCommissioningClient oltCommissioningClient = new OltCommissioningClient(authTokenProvider);
    private final OltCommissioningEventListenerClient oltCommissioningEventListenerClient = new OltCommissioningEventListenerClient();

    @Step("Starts zero touch commissioning process")
    public void startZtCommissioning(OltDevice oltDevice, String acid) {
        OltInstallationPage.openInstallationPage(acid)
                .validateUrl()
                .startZtCommisioningProcess(oltDevice)
                .chekcForceProceedLinkExist(TIMEOUT_FOR_ZTC_COMMISSIONING);
    }

    @Step("Waiting on the process until the force proceed link exist")
    public void startZtCommissioningWithError(OltDevice oltDevice, String acid) {
        OltInstallationPage.openInstallationPage(acid)
                .validateUrl()
                .startZtCommisioningProcess(oltDevice)
                .waitZtCommisioningProcessErrorMessage();
    }

    @Step("Restart zero touch commissioning process")
    public void restartZtCommissioning(OltDevice oltDevice) {
        new OltInstallationPage()
                .startZtCommisioningProcess(oltDevice)
                .chekcForceProceedLinkExist(TIMEOUT_FOR_ZTC_COMMISSIONING);
    }

    @Step("Check force proceed link exist")
    public void chekcForceProceedLinkExist() {
        new OltInstallationPage().chekcForceProceedLinkExist();
    }

    @Step("Manually continue the zero touch commissioning process. Wait until an error is displayed in the UI")
    public void continueZtCommissioningWaitForError() {
        new OltInstallationPage()
                .continueZtCommisioningProcessCallbackError(TIMEOUT_FOR_ZTC_COMMISSIONING);
    }

    @Step("Manually continue the zero touch commissioning process")
    public void continueZtCommissioning() {
        new OltInstallationPage()
                .continueZtCommisioningProcess();
    }

    @Step("Wait until the process is finished")
    public void waitZtCommissioningProcessIsFinished() {
        new OltInstallationPage()
                .waitZtCommisioningProcessFinishedSuccess(TIMEOUT_FOR_ZTC_COMMISSIONING);
    }

    @Step("Clear zero touch commisioning process data")
    public void clearZtCommisioningData(String endSz) {
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

    public void sendZtCommisioningSealEvent(String endSz, String objectState) {
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
                                .type("de.telekom.seal.device.olt.stateChanged.v1"))
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Get the oltZtcConfiguration.state from olt-ri")
    public Integer getZtCommisioningState(String endSz) {
        List<OltZtcConfiguration> oltZtcConfigurations = deviceResourceInventoryManagementClient.getClient().oltZtcConfiguration().listOltZtcConfiguration()
                .oltEndSzQuery(endSz).executeAs(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));

        log.info("oltZtcConfigurations state size = {}", oltZtcConfigurations.size());
        if (oltZtcConfigurations.size() > 0) {
            log.info("oltZtcConfigurations state = {}", oltZtcConfigurations.get(0).getState());
            return oltZtcConfigurations.get(0).getState();
        }
        return -1;
    }

    @Step("Verify the oltZtcConfiguration.state.")
    public void verifyZtCommisioningState(String endSz, Integer expectedState, Integer bitmask) {
        Integer state =  getZtCommisioningState(endSz) & bitmask;
        Assert.assertEquals(state, expectedState, "oltZtcConfiguration.state missmatch");
    }

    @Step("Clear device in inventory databases")
    public void clearResourceInventoryDataBase(String endSz) {
        deviceTestDataManagementClient.getClient().deviceTestDataManagement().deleteTestData().deviceEndSzQuery(endSz)
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }
}
