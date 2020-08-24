package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.data.osr.models.BusinessInformation;
import com.tsystems.tm.acc.ta.helpers.log.ContainsExpecter;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLogExpectSince;
import com.tsystems.tm.acc.ta.helpers.osr.logs.LogConverter;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.tsystems.tm.acc.ta.helpers.log.ServiceLogExpectSince.given;

@Slf4j
public class WgAccessProvisioningRobot {
    private static final String WG_ACCESS_PROVISIONING_MS_NAME = "wg-access-provisioning";
    private ServiceLogExpectSince logExpect;

    @Step("Collect wg-access-provisioning logs")
    public void startWgAccessProvisioningLog() throws InterruptedException {
        // Set a start time from which logs will be fetched

        logExpect =
                given().service(WG_ACCESS_PROVISIONING_MS_NAME)
                        .expect(WG_ACCESS_PROVISIONING_MS_NAME,
                        new ContainsExpecter("business_information"))
                        .buildAndStart();
        Thread.sleep(10000);
    }

    @Step("Get businessInformation from log")
    public List<BusinessInformation> getBusinessInformation() {
        logExpect.fetch();

        List<BusinessInformation> businessInformations = LogConverter.logsToBusinessInformationMessages(
                ((ContainsExpecter) logExpect
                        .getExpecterMap()
                        .get(WG_ACCESS_PROVISIONING_MS_NAME))
                        .getCatched());
        return businessInformations;
    }
}
