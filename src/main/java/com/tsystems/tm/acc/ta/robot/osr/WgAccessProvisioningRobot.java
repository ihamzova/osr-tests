package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.WgAccessProvisioningClient;
import com.tsystems.tm.acc.ta.data.osr.models.BusinessInformation;
import com.tsystems.tm.acc.ta.data.osr.models.Process;
import com.tsystems.tm.acc.ta.helpers.log.ContainsExpecter;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLogExpectSince;
import com.tsystems.tm.acc.ta.helpers.osr.logs.LogConverter;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.internal.client.model.PortDto;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_CREATED_201;
import static com.tsystems.tm.acc.ta.helpers.log.ServiceLogExpectSince.given;
import static com.tsystems.tm.acc.wiremock.oauth.client.invoker.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.wiremock.oauth.client.invoker.ResponseSpecBuilders.validatedWith;

@Slf4j
public class WgAccessProvisioningRobot {
    private static final String WG_ACCESS_PROVISIONING_MS_NAME = "wg-access-provisioning";
    private ServiceLogExpectSince logExpect;
    private WgAccessProvisioningClient wgAccessProvisioningClient = new WgAccessProvisioningClient();

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

    public UUID startPortProvisioningAndGetProcessId(Process process){
        return wgAccessProvisioningClient.getClient().provisioningProcess().startPortProvisioning()
                .body(new PortDto()
                        .endSz(process.getEndSz())
                        .slotNumber(process.getSlotNumber())
                        .portNumber(process.getPortNumber()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201))).getId();
    }

}
