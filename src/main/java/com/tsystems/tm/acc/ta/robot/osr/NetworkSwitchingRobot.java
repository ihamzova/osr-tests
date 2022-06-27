package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.NetworkSwitchingConfigMgtFillDbClient;
import io.qameta.allure.Step;

import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_OK_200;

public class NetworkSwitchingRobot {

    private NetworkSwitchingConfigMgtFillDbClient networkSwitchingConfigMgtFillDbClient = new NetworkSwitchingConfigMgtFillDbClient();

    @Step("Clear network-switching db")
    public void clearDatabase() {
        networkSwitchingConfigMgtFillDbClient.getClient()
                .fillDatabase()
                .clearDatabase()
                .execute(checkStatus(HTTP_CODE_OK_200));
    }

}
