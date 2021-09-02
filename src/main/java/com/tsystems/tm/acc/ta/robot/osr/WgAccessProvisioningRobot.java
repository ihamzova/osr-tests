package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.WgAccessProvisioningClient;
import com.tsystems.tm.acc.ta.data.osr.models.BusinessInformation;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.data.osr.models.Process;
import com.tsystems.tm.acc.ta.data.upiter.UpiterConstants;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.helpers.osr.logs.LogConverter;
import com.tsystems.tm.acc.ta.helpers.osr.logs.TimeoutBlock;
import com.tsystems.tm.acc.ta.log.ContainsExpecter;
import com.tsystems.tm.acc.ta.log.ServiceLogExpectSince;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_19_0.client.model.AccessLineDto;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.Card;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.Device;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.Port;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.HomeIdDto;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.v2_0_0.client.model.CardRequestDto;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.v2_0_0.client.model.DeviceDto;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.v2_0_0.client.model.PortDto;
import de.telekom.it.t3a.kotlin.log.ServiceDiscoveryStrategy;
import de.telekom.it.t3a.kotlin.log.query.ServiceDescriptor;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_ACCEPTED_202;
import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.WG_ACCESS_PROVISIONING_MS;
import static com.tsystems.tm.acc.ta.wiremock.ExtendedWireMock.CONSUMER_ENDPOINT;
import static com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.Port.PortTypeEnum.PON;

@Slf4j
public class WgAccessProvisioningRobot {
  private static final Integer LATENCY_FOR_PORT_PROVISIONING = 320_000;
  private static String CORRELATION_ID = UUID.randomUUID().toString();
  private ServiceLogExpectSince logExpect;
  private WgAccessProvisioningClient wgAccessProvisioningClient = new WgAccessProvisioningClient(authTokenProvider);
  private OntOltOrchestratorRobot ontOltOrchestratorRobot = new OntOltOrchestratorRobot();
  AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();

  private static final AuthTokenProvider authTokenProvider = new RhssoClientFlowAuthTokenProvider("wiremock-acc", RhssoHelper.getSecretOfGigabitHub("wiremock-acc"));

  @Step("Start port provisioning")
  public void startPortProvisioning(PortProvisioning port) {
    wgAccessProvisioningClient.getClient().provisioningProcess().startPortProvisioning()
            .body(new PortDto()
                    .endSz(port.getEndSz())
                    .slotNumber(port.getSlotNumber())
                    .portNumber(port.getPortNumber()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
  }

  @Step("Start card provisioning for 1 card")
  public void startCardProvisioningV2(PortProvisioning port) {
    wgAccessProvisioningClient.getClient().provisioningProcess().startCardProvisioning()
            .body(new CardRequestDto()
                    .endSz(port.getEndSz())
                    .slotNumber(port.getSlotNumber()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
  }

  @Step("Start card provisioning")
  public void startCardProvisioning(PortProvisioning port) {
    wgAccessProvisioningClient.getClient().provisioningProcess().startCardsProvisioning()
            .body(Stream.of(new CardRequestDto()
                    .endSz(port.getEndSz())
                    .slotNumber(port.getSlotNumber()))
                    .collect(Collectors.toList()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
  }

  @Step("Start device provisioning")
  public void startDeviceProvisioning(PortProvisioning port) {
    wgAccessProvisioningClient.getClient().provisioningProcess().startDeviceProvisioning()
            .body(new DeviceDto()
                    .endSz(port.getEndSz()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
  }

  @Step("Start port deprovisioning")
  public void startPortDeprovisioning(PortProvisioning port) {
    wgAccessProvisioningClient.getClient().deprovisioningProcess().startPortDeprovisioning()
            .body(new PortDto()
                    .endSz(port.getEndSz())
                    .slotNumber(port.getSlotNumber())
                    .portNumber(port.getPortNumber()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
  }

  @Step("Start port deprovisioning with deprovisioningForDpu flag")
  public void startPortDeprovisioningForDpu(PortProvisioning port, boolean deprovisioningForDpu) {
    wgAccessProvisioningClient.getClient().deprovisioningProcess().startPortDeprovisioning()
            .body(new PortDto()
                    .endSz(port.getEndSz())
                    .slotNumber(port.getSlotNumber())
                    .portNumber(port.getPortNumber()))
            .deprovisioningForDpuQuery(deprovisioningForDpu)
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
  }

  @Step("Start card deprovisioning")
  public void startCardDeprovisioning(PortProvisioning port) {
    wgAccessProvisioningClient.getClient().deprovisioningProcess().startCardsDeprovisioning()
            .body(Stream.of(new CardRequestDto()
                    .endSz(port.getEndSz())
                    .slotNumber(port.getSlotNumber()))
                    .collect(Collectors.toList()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
  }
  @Step("Start card deprovisioning for 1 card")
  public void startCardDeprovisioningV2(PortProvisioning port) {
    wgAccessProvisioningClient.getClient().deprovisioningProcess().startCardDeprovisioning()
            .body(new CardRequestDto()
                    .endSz(port.getEndSz())
                    .slotNumber(port.getSlotNumber()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
  }

  @Step("Start device deprovisioning")
  public void startDeviceDeprovisioning(PortProvisioning port) {
    wgAccessProvisioningClient.getClient().deprovisioningProcess().startDeviceDeprovisioning()
            .body(new DeviceDto()
                    .endSz(port.getEndSz()))
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
  }

  @Step("Collect wg-access-provisioning logs")
  public void startWgAccessProvisioningLog() throws InterruptedException {
    // Set a start time from which logs will be fetched

    logExpect = new ServiceLogExpectSince(new ServiceDescriptor(WG_ACCESS_PROVISIONING_MS, ServiceDiscoveryStrategy.APP));
    logExpect.attach(WG_ACCESS_PROVISIONING_MS, new ContainsExpecter("business_information"));
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
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202))).getId();
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
            .execute(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
  }

  @Step("Prepare data for postprovisioning")
  public void prepareForPostprovisioning(int linesCount, PortProvisioning port, HomeIdDto homeIdDto) {
    for (int i = 0; i < linesCount; i++) {
      ontOltOrchestratorRobot.reserveAccessLineTask(homeIdDto); //assigned linesCount
    }
    try {
      TimeoutBlock timeoutBlock = new TimeoutBlock(LATENCY_FOR_PORT_PROVISIONING); //set timeout in milliseconds
      Supplier<Boolean> precondition = () -> {
        List<AccessLineDto> accessLines = accessLineRiRobot.getAccessLinesByPort(port);
        return accessLines.size() == port.getAccessLinesCount();
      };

      timeoutBlock.addBlock(precondition); // execute the runnable precondition
    } catch (Throwable e) {
      //catch the exception here . Which is block didn't execute within the time limit
    }
  }

  @Step("Get PON Ports")
  public List<Port> getPonPorts(PortProvisioning port) {
    return getDevice(port).getPorts().stream()
            .filter(ponPort -> ponPort.getPortType().getValue().equals(PON.toString()))
            .collect(Collectors.toList());
  }

  @Step("Check card before provisioning")
  public Card getCard(PortProvisioning port) {
    URL cardUrl = new OCUrlBuilder("wiremock-acc")
            .withEndpoint("/api/oltResourceInventory/v1/card")
            .withParameter("endSz", port.getEndSz())
            .withParameter("slotNumber", port.getSlotNumber()).build();
    String response = RestAssured.given().when().get(cardUrl.toString().replace("%2F", "/"))
            .then().extract().body().asString().replaceFirst("\"lastDiscovery\": \".+\",\n", "");
    return OltResourceInventoryClient.json().deserialize(response, Card.class);
  }

  @Step("Check device before/after provisioning")
  public Device getDevice(PortProvisioning port) {
    URL deviceUrl = new OCUrlBuilder("wiremock-acc")
            .withEndpoint("/api/oltResourceInventory/v1/olt")
            .withParameter("endSZ", port.getEndSz()).build();
    String response = RestAssured.given().when().get(deviceUrl.toString().replace("%2F", "/"))
            .then().extract().body().asString().replaceFirst("\"lastDiscovery\": \".+\",\n", "");
    return OltResourceInventoryClient.json().deserialize(response, Device.class);
  }

  @Step("Check port after provisioning")
  public PortProvisioning getPortProvisioning(String endSz, String slotNumber, String portNumber, PortProvisioning port) {
    PortProvisioning portBeforeProvisioning = new PortProvisioning();
    portBeforeProvisioning.setEndSz(endSz);
    portBeforeProvisioning.setSlotNumber(slotNumber);
    portBeforeProvisioning.setPortNumber(portNumber);
    portBeforeProvisioning.setLineIdPool(port.getLineIdPool());
    portBeforeProvisioning.setHomeIdPool(port.getHomeIdPool());
    portBeforeProvisioning.setBackhaulId(port.getBackhaulId());
    portBeforeProvisioning.setDefaultNEProfilesActive(port.getDefaultNEProfilesActive());
    portBeforeProvisioning.setDefaultNetworkLineProfilesActive(port.getDefaultNetworkLineProfilesActive());
    portBeforeProvisioning.setAccessLinesWG(port.getAccessLinesWG());
    return portBeforeProvisioning;
  }
}
