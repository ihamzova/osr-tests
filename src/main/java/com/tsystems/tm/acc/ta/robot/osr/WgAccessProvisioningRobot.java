package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.WgAccessProvisioningClient;
import com.tsystems.tm.acc.ta.data.osr.models.BusinessInformation;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.data.osr.models.Process;
import com.tsystems.tm.acc.ta.data.upiter.UpiterConstants;
import com.tsystems.tm.acc.ta.helpers.log.ContainsExpecter;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLogExpectSince;
import com.tsystems.tm.acc.ta.helpers.osr.logs.LogConverter;
import com.tsystems.tm.acc.ta.helpers.osr.logs.TimeoutBlock;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.internal.client.model.AccessLineDto;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.internal.client.model.HomeIdDto;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.internal.client.model.CardDto;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.internal.client.model.DeviceDto;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.internal.client.model.PortDto;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_CREATED_201;
import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.WG_ACCESS_PROVISIONING_MS;
import static com.tsystems.tm.acc.ta.helpers.WiremockHelper.CONSUMER_ENDPOINT;
import static com.tsystems.tm.acc.ta.helpers.log.ServiceLogExpectSince.given;

@Slf4j
public class WgAccessProvisioningRobot {
    private static final Integer LATENCY_FOR_PORT_PROVISIONING = 300_000;
    private static String CORRELATION_ID = UUID.randomUUID().toString();
    private ServiceLogExpectSince logExpect;
    private WgAccessProvisioningClient wgAccessProvisioningClient = new WgAccessProvisioningClient();
    private OntOltOrchestratorRobot ontOltOrchestratorRobot = new OntOltOrchestratorRobot();
    AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();

    @Step("Start port provisioning")
    public void startPortProvisioning(PortProvisioning port) {
        wgAccessProvisioningClient.getClient().provisioningProcess().startPortProvisioning()
                .body(new PortDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber())
                        .portNumber(port.getPortNumber()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    @Step("Start card provisioning")
    public void startCardProvisioning(PortProvisioning port) {
        wgAccessProvisioningClient.getClient().provisioningProcess().startCardsProvisioning()
                .body(Stream.of(new CardDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber()))
                        .collect(Collectors.toList()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    @Step("Start device provisioning")
    public void startDeviceProvisioning(PortProvisioning port) {
        wgAccessProvisioningClient.getClient().provisioningProcess().startDeviceProvisioning()
                .body(new DeviceDto()
                        .endSz(port.getEndSz()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    @Step("Collect wg-access-provisioning logs")
    public void startWgAccessProvisioningLog() throws InterruptedException {
        // Set a start time from which logs will be fetched

        logExpect =
                given().service(WG_ACCESS_PROVISIONING_MS)
                        .expect(WG_ACCESS_PROVISIONING_MS,
                                new ContainsExpecter("business_information"))
                        .buildAndStart();
        Thread.sleep(10000);
    }

    @Step("Get businessInformation from log")
    public List<BusinessInformation> getBusinessInformation() throws InterruptedException {
        Thread.sleep(20000);
        logExpect.fetch();

        List<BusinessInformation> businessInformations = LogConverter.logsToBusinessInformationMessages(
                ((ContainsExpecter) logExpect
                        .getExpecterMap()
                        .get(WG_ACCESS_PROVISIONING_MS))
                        .getCatched());
        Assert.assertNotNull(businessInformations, "Business Info is not collected.");
        return businessInformations;
    }

    public UUID startPortProvisioningAndGetProcessId(Process process) {
        return wgAccessProvisioningClient.getClient().provisioningProcess().startPortProvisioning()
                .body(new PortDto()
                        .endSz(process.getEndSz())
                        .slotNumber(process.getSlotNumber())
                        .portNumber(process.getPortNumber()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201))).getId();
    }

    @Step("Start postprovisioning")
    public void startPostprovisioning(PortProvisioning port) {
        wgAccessProvisioningClient
                .getClient()
                .postProvisioningProcessController()
                .postProvisioning()
                .xCallbackCorrelationIdHeader(CORRELATION_ID)
                .xCallbackUrlHeader(new OCUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .xCallbackErrorUrlHeader(new OCUrlBuilder(UpiterConstants.WIREMOCK_MS_NAME)
                        .withEndpoint(CONSUMER_ENDPOINT)
                        .build()
                        .toString())
                .body(new PortDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber())
                        .portNumber(port.getPortNumber()))
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    public void prepareForPostprovisioning(int linesCount, PortProvisioning port, HomeIdDto homeIdDto) {
        for (int i = 0; i < linesCount; i++) {
            ontOltOrchestratorRobot.reserveAccessLineTask(homeIdDto); //assigned linesCount
        }
        try {
            TimeoutBlock timeoutBlock = new TimeoutBlock(LATENCY_FOR_PORT_PROVISIONING); //set timeout in milliseconds
            Supplier<Boolean> precondition = () -> {
                List<AccessLineDto> accessLines = accessLineRiRobot.getAccessLines(port);
                return accessLines.size() == port.getAccessLinesCount();
            };

            timeoutBlock.addBlock(precondition); // execute the runnable precondition
        } catch (Throwable e) {
            //catch the exception here . Which is block didn't execute within the time limit
        }
    }
}
