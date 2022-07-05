package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.UnleashClient;
import com.tsystems.tm.acc.ta.api.osr.NetworkSwitchingConfigMgtFillDbClient;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.FEATURE_TOGGLE_ENABLE_FTTB_NE2_NETWORK_SWITCHING;

@Slf4j
public class NetworkSwitchingRobot {

    private NetworkSwitchingConfigMgtFillDbClient networkSwitchingConfigMgtFillDbClient = new NetworkSwitchingConfigMgtFillDbClient();
    private UnleashClient unleashClient = new UnleashClient();

    @Step("Clear network-switching db")
    public void clearDatabase() {
        networkSwitchingConfigMgtFillDbClient.getClient()
                .fillDatabase()
                .clearDatabase()
                .execute(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("eenable-fttb-ne2-network-switching - сhange feature toggle state")
    public void changeFeatureToogleEnableFttbNe2NetworkSwitchingState(boolean toggleState) {
        if (toggleState) {
            unleashClient.enableToggle(FEATURE_TOGGLE_ENABLE_FTTB_NE2_NETWORK_SWITCHING);
        } else {
            unleashClient.disableToggle(FEATURE_TOGGLE_ENABLE_FTTB_NE2_NETWORK_SWITCHING);
        }
        log.info("toggleState for {} = {}", FEATURE_TOGGLE_ENABLE_FTTB_NE2_NETWORK_SWITCHING, toggleState);
    }

    @Step("enable-fttb-ne2-network-switching - get feature toggle state")
    public boolean getFeatureToogleEnableFttbNe2NetworkSwitchingState() {
        return unleashClient.isToggleEnabled(FEATURE_TOGGLE_ENABLE_FTTB_NE2_NETWORK_SWITCHING);
    }

}
