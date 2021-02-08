package com.tsystems.tm.acc.ta.team.upiter.provisioning;

import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.WgAccessProvisioningClient;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.internal.v5_1_0.client.model.AccessLineDto;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_2_0.client.model.Card;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_2_0.client.model.Device;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.internal.v1_5_0.client.model.DeviceDto;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.internal.v1_5_0.client.model.CardRequestDto;
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
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_ACCEPTED_202;
import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static io.restassured.RestAssured.given;

@ServiceLog(WG_ACCESS_PROVISIONING_MS)
@ServiceLog(ACCESS_LINE_RESOURCE_INVENTORY_MS)
@ServiceLog(ACCESS_LINE_MANAGEMENT_MS)
@ServiceLog(NETWORK_LINE_PROFILE_MANAGEMENT_MS)
@ServiceLog(DECOUPLING_MS)
@ServiceLog(GATEWAY_ROUTE_MS)
public class OltProvisioning5800 extends BaseTest {

    private static final Integer LATENCY = 2 * 80_000;

    private AccessLineRiRobot accessLineRiRobot;
    private WgAccessProvisioningRobot wgAccessProvisioningRobot;
    private WgAccessProvisioningClient wgAccessProvisioningClient;
    private AccessLineResourceInventoryClient accessLineResourceInventoryClient;
    private PortProvisioning portEmpty;
    private UpiterTestContext context = UpiterTestContext.get();

    @BeforeClass
    public void init() {
        accessLineRiRobot = new AccessLineRiRobot();
        wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
        accessLineResourceInventoryClient = new AccessLineResourceInventoryClient();
        wgAccessProvisioningClient = new WgAccessProvisioningClient();
        portEmpty = context.getData()
                .getPortProvisioningDataProvider()
                .get(PortProvisioningCase.portEmpty5800);
    }

    @BeforeMethod
    public void prepareData() {
        accessLineRiRobot.clearDatabase();
    }

    @AfterMethod
    public void clearData() {
        accessLineRiRobot.clearDatabase();
    }

    @Test
    @TmsLink("DIGIHUB-30877")
    @Description("Port Provisioning with 32 WG Lines")
    public void portProvisioning() {
        List<AccessLineDto> accessLinesBeforeProvisioning = accessLineRiRobot.getAccessLinesByPort(portEmpty);
        Assert.assertEquals(accessLinesBeforeProvisioning.size(), 0);

        wgAccessProvisioningRobot.startPortProvisioning(portEmpty);
        accessLineRiRobot.checkProvisioningResults(portEmpty);
    }

    @Test
    @TmsLink("DIGIHUB-30870")
    @Description("Card Provisioning with 1 port")
    public void cardProvisioning() throws InterruptedException {

        Card cardBeforeProvisioning = getCard();
        PortProvisioning port = getPortProvisioning(portEmpty.getEndSz(),
                portEmpty.getSlotNumber(),
                cardBeforeProvisioning.getPorts().get(0).getPortNumber());

        Assert.assertNotNull(cardBeforeProvisioning);
        Assert.assertEquals(cardBeforeProvisioning.getPorts().size(), 16);
        Assert.assertEquals(accessLineRiRobot.getAccessLinesByPort(port).size(), 0);

        wgAccessProvisioningClient.getClient().provisioningProcess().startCardsProvisioning()
                .body(Stream.of(new CardRequestDto().endSz(portEmpty.getEndSz()).slotNumber(portEmpty.getSlotNumber())).collect(Collectors.toList()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));

        Thread.sleep(LATENCY);

        accessLineRiRobot.checkProvisioningResults(port);
    }

    @Test
    @TmsLink("DIGIHUB-30824")
    @Description("Device Provisioning with 1 card and 1 port")
    public void deviceProvisioning() throws InterruptedException {

        Device deviceBeforeProvisioning = getDevice();

        PortProvisioning port = getPortProvisioning(portEmpty.getEndSz(),
                deviceBeforeProvisioning.getEquipmentHolders().get(0).getSlotNumber(),
                deviceBeforeProvisioning.getEquipmentHolders().get(0).getCard().getPorts().get(0).getPortNumber());

        Assert.assertNotNull(deviceBeforeProvisioning);
        Assert.assertEquals(deviceBeforeProvisioning.getEmsNbiName(), "MA5800-X7");
        Assert.assertEquals(deviceBeforeProvisioning.getEquipmentHolders().get(0).getCard().getPorts().size(), 16);
        Assert.assertEquals(accessLineRiRobot.getAccessLinesByPort(port).size(), 0);

        wgAccessProvisioningClient.getClient().provisioningProcess().startDeviceProvisioning()
                .body(new DeviceDto().endSz(portEmpty.getEndSz())).executeAs(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));

        Thread.sleep(LATENCY);

        accessLineRiRobot.checkProvisioningResults(port);
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
}
