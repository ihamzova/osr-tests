package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.WgFttbAccessProvisioningClient;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_ACCEPTED_202;
import static com.tsystems.tm.acc.ta.wiremock.ExtendedWireMock.CONSUMER_ENDPOINT;
import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;

@Slf4j
public class WgFttbAccessProvisioningRobot {

    private static final UUID uuid = UUID.randomUUID();
    private final WgFttbAccessProvisioningClient wgFttbAccessProvisioningClient = new WgFttbAccessProvisioningClient();

    @Step("Start FTTB preprovisioning process for a device")
    public void startWgFttbAccessProvisioningForDevice(String dpuEndSz) {
        wgFttbAccessProvisioningClient
                .getClient()
                .fttbProvisioningController()
                .startDeviceProvisioning()
                .xCallbackCorrelationIdHeader(String.valueOf(uuid))
                .xCallbackUrlHeader(new GigabitUrlBuilder("wiremock-acc")
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .buildContainer()
                        .toString())
                .xCallbackErrorUrlHeader(new GigabitUrlBuilder("wiremock-acc")
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .buildContainer()
                        .toString())
                .endSZQuery(dpuEndSz)
                .execute(checkStatus(HTTP_CODE_ACCEPTED_202));
        log.info("Received xCallbackCorrelationId: " + uuid.toString());
    }

    @Step("Start FTTB preprovisioning process for a port")
    public void startWgFttbAccessProvisioningForPort(PortProvisioning port) {
        wgFttbAccessProvisioningClient
                .getClient()
                .fttbProvisioningController()
                .startPortProvisioning()
                .xCallbackCorrelationIdHeader(String.valueOf(uuid))
                .xCallbackUrlHeader(new GigabitUrlBuilder("wiremock-acc")
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .buildContainer()
                        .toString())
                .xCallbackErrorUrlHeader(new GigabitUrlBuilder("wiremock-acc")
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .buildContainer()
                        .toString())
                .endSZQuery(port.getEndSz())
                .portQuery(port.getPortNumber())
                .execute(checkStatus(HTTP_CODE_ACCEPTED_202));
        log.info("Received xCallbackCorrelationId: " + uuid.toString());
    }

    @Step("Start FTTB deprovisioning process for a device")
    public void startWgFttbAccessDeprovisioningForDevice(String dpuEndSz) {
        wgFttbAccessProvisioningClient
                .getClient()
                .fttbDeprovisioningController()
                .startDeviceDeprovisioning()
                .xCallbackCorrelationIdHeader(String.valueOf(uuid))
                .xCallbackUrlHeader(new GigabitUrlBuilder("wiremock-acc")
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .buildContainer()
                        .toString())
                .xCallbackErrorUrlHeader(new GigabitUrlBuilder("wiremock-acc")
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .buildContainer()
                        .toString())
                .dpuEndSZQuery(dpuEndSz)
                .execute(checkStatus(HTTP_CODE_ACCEPTED_202));
        log.info("Received xCallbackCorrelationId: " + uuid.toString());
    }
}
