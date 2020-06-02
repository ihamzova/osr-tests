package com.tsystems.tm.acc.ta.team.upiter.provisioning;

import com.tsystems.tm.acc.data.models.stable.PortProvisioning;
import com.tsystems.tm.acc.data.osr.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.WgAccessProvisioningClient;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.internal.client.model.*;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.Card;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.Device;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.internal.client.model.CardDto;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.internal.client.model.DeviceDto;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.internal.client.model.PortDto;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.team.upiter.common.CommonTestData.*;
import static io.restassured.RestAssured.given;

public class OltProvisioning5600 extends ApiTest {

    private static final Integer LATENCY_FOR_PORT_PROVISIONING = 80_000;
    private static final Integer LATENCY_FOR_DEVICE_PROVISIONING = 15 * 75_000;

    private AccessLineResourceInventoryClient accessLineResourceInventoryClient;
    private WgAccessProvisioningClient wgAccessProvisioningClient;
    private PortProvisioning portEmpty;
    private PortProvisioning portProvisioningPartly;
    private PortProvisioning portProvisioningFully;
    private PortProvisioning portWithInActiveLines;

    @BeforeMethod
    public void prepareData() throws InterruptedException {
        clearDataBase();
        Thread.sleep(1000);
        fillDataBase();
    }

    @AfterMethod
    public void clearData() {
        clearDataBase();
    }

    @BeforeClass
    public void init() {
        accessLineResourceInventoryClient = new AccessLineResourceInventoryClient();
        wgAccessProvisioningClient = new WgAccessProvisioningClient();
        OsrTestContext context = OsrTestContext.get();
        portEmpty = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portEmpty5600);
        portProvisioningPartly = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portPartlyOccupied);
        portProvisioningFully = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portFullyOccupied);
        portWithInActiveLines = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portWithInActiveLines);
    }

    @Test
    @TmsLink("DIGIHUB-29664")
    @Description("Port provisioning case when port completely free")
    public void portProvisioningEmpty() throws InterruptedException {
        testPortProvisioning(portEmpty, LATENCY_FOR_PORT_PROVISIONING, false);
    }

    @Test
    @TmsLink("DIGIHUB-32288")
    @Description("Port provisioning case when port partly occupied")
    public void portProvisioningPartly() throws InterruptedException {
        testPortProvisioning(portProvisioningPartly, LATENCY_FOR_PORT_PROVISIONING / 2, false);
    }

    @Test
    @TmsLink("DIGIHUB-40631")
    @Description("Port provisioning case when port completely occupied")
    public void portProvisioningFully() throws InterruptedException {
        testPortProvisioning(portProvisioningFully, 1_000, false);
    }

    @Test
    @TmsLink("DIGIHUB-32026")
    @Description("Port provisioning case when port has InActive Lines")
    public void portProvisioningWithInactiveLines() throws InterruptedException {
        testPortProvisioning(portWithInActiveLines, LATENCY_FOR_PORT_PROVISIONING / 2, true);
    }

    @Test
    @TmsLink("DIGIHUB-29666")
    @Description("Card provisioning case with 1 empty port")
    public void cardProvisioning() throws InterruptedException {

        Card cardBeforeProvisioning = getCard();

        Assert.assertNotNull(cardBeforeProvisioning);
        Assert.assertEquals(cardBeforeProvisioning.getPorts().size(), 1);

        wgAccessProvisioningClient.getClient().provisioningProcess().startCardsProvisioning()
                .body(Stream.of(new CardDto().endSz(portEmpty.getEndSz()).slotNumber(portEmpty.getSlotNumber())).collect(Collectors.toList()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        Thread.sleep(LATENCY_FOR_PORT_PROVISIONING);

        Card cardAfterProvisioning = getCard();

        PortProvisioning port = getPortProvisioning(portEmpty.getEndSz(), portEmpty.getSlotNumber(), cardAfterProvisioning.getPorts().get(0).getPortNumber());

        checkResults(port);
    }

    @Test
    @TmsLink("DIGIHUB-29667")
    @Description("Device provisioning case")
    public void deviceProvisioning() throws InterruptedException {

        Device deviceBeforeProvisioning = getDevice();

        Assert.assertNotNull(deviceBeforeProvisioning);
        Assert.assertEquals(deviceBeforeProvisioning.getEmsNbiName(), "MA5600T");
        Assert.assertEquals(deviceBeforeProvisioning.getEquipmentHolders().get(0).getCard().getPorts().size(), 8);

        wgAccessProvisioningClient.getClient().provisioningProcess().startDeviceProvisioning()
                .body(new DeviceDto().endSz(portEmpty.getEndSz())).executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        Thread.sleep(LATENCY_FOR_DEVICE_PROVISIONING);

        Device deviceAfterProvisioning = getDevice();

        PortProvisioning port = getPortProvisioning(portEmpty.getEndSz(),
                deviceAfterProvisioning.getEquipmentHolders().get(2).getSlotNumber(),
                deviceAfterProvisioning.getEquipmentHolders().get(2).getCard().getPorts().get(0).getPortNumber());

        checkResults(port);
    }

    private PortProvisioning getPortProvisioning(String endSz, String slotNumber, String portNumber) {
        PortProvisioning port = new PortProvisioning();
        port.setEndSz(endSz);
        port.setSlotNumber(slotNumber);
        port.setPortNumber(portNumber);
        port.setLineIdPool(portEmpty.getLineIdPool());
        port.setHomeIdPool(portEmpty.getHomeIdPool());
        port.setDefaultNEProfilesActive(portEmpty.getDefaultNEProfilesActive());
        port.setDefaultNetworkLineProfilesActive(portEmpty.getDefaultNetworkLineProfilesActive());
        port.setAccessLinesWG(portEmpty.getAccessLinesWG());
        return port;
    }

    private void testPortProvisioning(PortProvisioning port, int waitTime, boolean isInactiveLines) throws InterruptedException {
        List<AccessLineDto> accessLinesBeforeProvisioning = getAccessLines(port);

        long linesCount = isInactiveLines
                ? accessLinesBeforeProvisioning.stream()
                .filter(AccessLine -> AccessLine.getStatus().getValue().equals(STATUS_INACTIVE))
                .count()
                : accessLinesBeforeProvisioning.size();

        Assert.assertEquals(linesCount, port.getAccessLinesCount().intValue());

        startPortProvisioning(port);

        Thread.sleep(waitTime);

        checkResults(port);
    }

    private void checkResults(PortProvisioning port) {
        List<AccessLineDto> accessLinesAfterProvisioning = getAccessLines(port);

        long countDefaultNEProfileActive = accessLinesAfterProvisioning.stream().map(AccessLineDto::getDefaultNeProfile)
                .filter(DefaultNeProfile -> DefaultNeProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countDefaultNetworkLineProfileActive = accessLinesAfterProvisioning.stream().map(AccessLineDto::getDefaultNetworkLineProfile)
                .filter(DefaultNetworkLineProfile -> DefaultNetworkLineProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countAccessLinesWG = accessLinesAfterProvisioning.stream()
                .filter(AccessLine -> AccessLine.getStatus().getValue().equals(STATUS_WALLED_GARDEN)).count();

        Assert.assertEquals(getLineIdPools(port).size(), port.getLineIdPool().intValue());
        Assert.assertEquals(getHomeIdPools(port).size(), port.getHomeIdPool().intValue());
        Assert.assertEquals(countDefaultNetworkLineProfileActive, port.getDefaultNetworkLineProfilesActive().intValue());
        Assert.assertEquals(countDefaultNEProfileActive, port.getDefaultNEProfilesActive().intValue());
        Assert.assertEquals(countAccessLinesWG, port.getAccessLinesWG().intValue());
    }

    private List<AccessLineDto> getAccessLines(PortProvisioning port) {
        return accessLineResourceInventoryClient.getClient().accessLineInternalController().searchAccessLines().body(
                new SearchAccessLineDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber())
                        .portNumber(port.getPortNumber()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    private List<LineIdDto> getLineIdPools(PortProvisioning port) {
        return accessLineResourceInventoryClient.getClient().lineIdController().searchLineIds().body(
                new SearchLineIdDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber())
                        .portNumber(port.getPortNumber()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    private List<HomeIdDto> getHomeIdPools(PortProvisioning port) {
        return accessLineResourceInventoryClient.getClient().homeIdInternalController().searchHomeIds().body(
                new SearchHomeIdDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber())
                        .portNumber(port.getPortNumber()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    private Device getDevice() {
        URL deviceUrl = new OCUrlBuilder("wiremock-acc")
                .withEndpoint("/api/oltResourceInventory/v1/olt")
                .withParameter("endSZ", portEmpty.getEndSz()).build();
        String response = given().when().get(deviceUrl.toString().replace("%2F", "/"))
                .then().extract().body().asString().replaceFirst("\"lastDiscovery\": \".+\",\n","");
        return OltResourceInventoryClient.json().deserialize(response, Device.class);
    }

    private Card getCard() {
        URL cardUrl = new OCUrlBuilder("wiremock-acc")
                .withEndpoint("/api/oltResourceInventory/v1/card")
                .withParameter("endSz", portEmpty.getEndSz())
                .withParameter("slotNumber", portEmpty.getSlotNumber()).build();
        String response = given().when().get(cardUrl.toString().replace("%2F", "/"))
                .then().extract().body().asString().replaceFirst("\"lastDiscovery\": \".+\",\n","");
        return OltResourceInventoryClient.json().deserialize(response, Card.class);
    }

    private void startPortProvisioning(PortProvisioning port) {
        wgAccessProvisioningClient.getClient().provisioningProcess().startPortProvisioning()
                .body(new PortDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber())
                        .portNumber(port.getPortNumber()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    private void fillDataBase() {
        accessLineResourceInventoryClient.getClient().fillDatabase().fillDatabaseForOltCommissioning()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    private void clearDataBase() {
        accessLineResourceInventoryClient.getClient().fillDatabase().deleteDatabase()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }
}
