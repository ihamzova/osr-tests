package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.WgFttbAccessProvisioningClient;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_ACCEPTED_202;
import static com.tsystems.tm.acc.ta.helpers.WiremockHelper.CONSUMER_ENDPOINT;

@Slf4j
public class WgFttbAccessProvisioningRobot {

    private static final UUID uuid = UUID.randomUUID();
    private WgFttbAccessProvisioningClient wgFttbAccessProvisioningClient = new WgFttbAccessProvisioningClient();

    @Step("Start FTTB preprovisioning process for a device")
    public void startWgFttbAccessProvisioningForDevice(String dpuEndSz) {
        wgFttbAccessProvisioningClient
                .getClient()
                .fttbProvisioningController()
                .startDeviceProvisioning()
                .xCallbackCorrelationIdHeader(String.valueOf(uuid))
                .xCallbackUrlHeader(new OCUrlBuilder("wiremock-acc")
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .xCallbackErrorUrlHeader(new OCUrlBuilder("wiremock-acc")
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .endSZQuery(dpuEndSz)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
        log.info("Received xCallbackCorrelationId: " + uuid.toString());
    }

    @Step("Start FTTB preprovisioning process for a port")
    public void startWgFttbAccessProvisioningForPort(PortProvisioning port) {
        wgFttbAccessProvisioningClient
                .getClient()
                .fttbProvisioningController()
                .startPortProvisioning()
                .xCallbackCorrelationIdHeader(String.valueOf(uuid))
                .xCallbackUrlHeader(new OCUrlBuilder("wiremock-acc")
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .xCallbackErrorUrlHeader(new OCUrlBuilder("wiremock-acc")
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .endSZQuery(port.getEndSz())
                .portQuery(port.getPortNumber())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
        log.info("Received xCallbackCorrelationId: " + uuid.toString());
    }

    @Step("Start FTTB deprovisioning process for a device")
    public void startWgFttbAccessDeprovisioningForDevice(String dpuEndSz) {
        wgFttbAccessProvisioningClient
                .getClient()
                .fttbDeprovisioningController()
                .startDeviceDeprovisioning()
                .xCallbackCorrelationIdHeader(String.valueOf(uuid))
                .xCallbackUrlHeader(new OCUrlBuilder("wiremock-acc")
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .xCallbackErrorUrlHeader(new OCUrlBuilder("wiremock-acc")
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .dpuEndSZQuery(dpuEndSz)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
        log.info("Received xCallbackCorrelationId: " + uuid.toString());
    }


}
