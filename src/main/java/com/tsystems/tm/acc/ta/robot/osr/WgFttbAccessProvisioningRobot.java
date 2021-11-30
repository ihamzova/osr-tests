package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.UnleashClient;
import com.tsystems.tm.acc.ta.api.osr.WgFttbAccessProvisioningClient;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.FEATURE_TOGGLE_DPU_LIFECYCLE_USES_DPU_DEMANDS_NAME;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_ACCEPTED_202;
import static com.tsystems.tm.acc.ta.wiremock.ExtendedWireMock.CONSUMER_ENDPOINT;

@Slf4j
public class WgFttbAccessProvisioningRobot {

    private static final UUID uuid = UUID.randomUUID();
    private static final AuthTokenProvider authTokenProvider = new RhssoClientFlowAuthTokenProvider("wiremock-acc", RhssoHelper.getSecretOfGigabitHub("wiremock-acc"));
    private WgFttbAccessProvisioningClient wgFttbAccessProvisioningClient = new WgFttbAccessProvisioningClient(authTokenProvider);
    private UnleashClient unleashClient = new UnleashClient();

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

    @Step("Change feature toggle state")
    public void changeFeatureToogleDpuDemandState(boolean toggleState) {
        if (toggleState) {
            unleashClient.enableToggle(FEATURE_TOGGLE_DPU_LIFECYCLE_USES_DPU_DEMANDS_NAME);
        } else {
            unleashClient.disableToggle(FEATURE_TOGGLE_DPU_LIFECYCLE_USES_DPU_DEMANDS_NAME);
        }
        log.info("toggleState for {} = {}", FEATURE_TOGGLE_DPU_LIFECYCLE_USES_DPU_DEMANDS_NAME , toggleState);
    }

    @Step("Get feature toggle state")
    public boolean getFeatureToggleDpuDemandState() {
        return unleashClient.isToggleEnabled(FEATURE_TOGGLE_DPU_LIFECYCLE_USES_DPU_DEMANDS_NAME);
    }


}
