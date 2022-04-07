package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.ResponseSpecBuilders;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.DeviceResourceInventoryManagementClient;
import com.tsystems.tm.acc.ta.api.osr.DeviceTestDataManagementClient;
import com.tsystems.tm.acc.ta.api.osr.OltCommissioningClient;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.pages.osr.ztcommissioning.OltInstallationPage;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.model.OltZtcConfiguration;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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

    @Step("Starts zero touch commissioning process")
    public void startZtCommissioning(OltDevice oltDevice, String acid) {
        OltInstallationPage.openInstallationPage(acid)
                .validateUrl()
                .startZtCommisioningProcess(oltDevice, TIMEOUT_FOR_ZTC_COMMISSIONING);
    }

    @Step("Clear zero touch commisioning process data")
    public void clearZtCommisioningData(String endSz) {
        List<OltZtcConfiguration> oltZtcConfigurations = deviceResourceInventoryManagementClient.getClient().oltZtcConfiguration().listOltZtcConfiguration()
                .oltEndSzQuery(endSz).executeAs(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));

        if(oltZtcConfigurations.size() > 0) {
            log.info("delete oltZtcConfigurations = {}", oltZtcConfigurations.get(0));
            deviceResourceInventoryManagementClient.getClient().oltZtcConfiguration().deleteOltZtcConfiguration()
                            .idPath(oltZtcConfigurations.get(0).getId()).execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
            oltCommissioningClient.getClient().oltZtCommissioning().deleteZtCommissioning()
                            .processIdPath(oltZtcConfigurations.get(0).getProcessId()).execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
        }
    }

    @Step("Clear device in inventory databases")
    public void clearResourceInventoryDataBase(String endSz) {
        deviceTestDataManagementClient.getClient().deviceTestDataManagement().deleteTestData().deviceEndSzQuery(endSz)
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }
}
