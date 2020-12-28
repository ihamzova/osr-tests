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
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.internal.client.model.AccessLineDto;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.Card;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.Device;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.internal.v1_5_0.client.model.DeviceDto;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_ACCEPTED_202;
import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static io.restassured.RestAssured.given;

@ServiceLog(WG_ACCESS_PROVISIONING_MS)
@ServiceLog(ACCESS_LINE_RESOURCE_INVENTORY_MS)
@ServiceLog(NETWORK_LINE_PROFILE_MANAGEMENT_MS)
@ServiceLog(DECOUPLING_MS)
@ServiceLog(GATEWAY_ROUTE_MS)
public class OltProvisioning5600 extends BaseTest {

    private static final Integer LATENCY_FOR_PORT_PROVISIONING = 100_000;
    private static final Integer LATENCY_FOR_DEVICE_PROVISIONING = 15 * LATENCY_FOR_PORT_PROVISIONING;

    private AccessLineRiRobot accessLineRiRobot;
    private WgAccessProvisioningRobot wgAccessProvisioningRobot;
    private AccessLineResourceInventoryClient accessLineResourceInventoryClient;
    private WgAccessProvisioningClient wgAccessProvisioningClient;
    private PortProvisioning portEmpty;
    private PortProvisioning portProvisioningPartly;
    private PortProvisioning portProvisioningFully;
    private PortProvisioning portWithInActiveLines;
    private UpiterTestContext context = UpiterTestContext.get();
    private int overallLatency;

    @BeforeMethod
    public void prepareData() throws InterruptedException {
        overallLatency = 0;
        accessLineRiRobot.clearDatabase();
        Thread.sleep(1000);
        accessLineRiRobot.fillDatabaseForOltCommissioning();
    }

    @AfterMethod
    public void clearData() {
        accessLineRiRobot.clearDatabase();
    }

    @BeforeClass
    public void init() {
        accessLineRiRobot = new AccessLineRiRobot();
        wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
        accessLineResourceInventoryClient = new AccessLineResourceInventoryClient();
        wgAccessProvisioningClient = new WgAccessProvisioningClient();
        portEmpty = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portEmpty5600);
        portProvisioningPartly = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portPartlyOccupied);
        portProvisioningFully = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portFullyOccupied);
        portWithInActiveLines = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portWithInActiveLines);
    }

    @Test
    @TmsLink("DIGIHUB-29664")
    @Description("Port provisioning case when port completely free")
    public void portProvisioningEmpty() {
        List<AccessLineDto> accessLinesBeforeProvisioning = accessLineRiRobot.getAccessLines(portEmpty);
        Assert.assertEquals(accessLinesBeforeProvisioning.size(), 0);

        wgAccessProvisioningRobot.startPortProvisioning(portEmpty);
        accessLineRiRobot.checkProvisioningResults(portEmpty);
    }

    @Test
    @TmsLink("DIGIHUB-32288")
    @Description("Port provisioning case when port partly occupied")
    public void portProvisioningPartly() {
        List<AccessLineDto> accessLinesBeforeProvisioning = accessLineRiRobot.getAccessLines(portProvisioningPartly);
        Assert.assertEquals(accessLinesBeforeProvisioning.size(), 8);

        wgAccessProvisioningRobot.startPortProvisioning(portProvisioningPartly);
        accessLineRiRobot.checkProvisioningResults(portProvisioningPartly);
    }

    @Test
    @TmsLink("DIGIHUB-40631")
    @Description("Port provisioning case when port completely occupied")
    public void portProvisioningFully() {
        List<AccessLineDto> accessLinesBeforeProvisioning = accessLineRiRobot.getAccessLines(portProvisioningFully);
        Assert.assertEquals(accessLinesBeforeProvisioning.size(), portProvisioningFully.getAccessLinesCount().intValue());

        wgAccessProvisioningRobot.startPortProvisioning(portProvisioningFully);
        accessLineRiRobot.checkProvisioningResults(portProvisioningFully);
    }

    @Test
    @TmsLink("DIGIHUB-32026")
    @Description("Port provisioning case when port has InActive Lines")
    public void portProvisioningWithInactiveLines() {
        wgAccessProvisioningRobot.startPortProvisioning(portWithInActiveLines);

        accessLineRiRobot.checkProvisioningResults(portWithInActiveLines);
    }

    @Test
    @TmsLink("DIGIHUB-29666")
    @Description("Card provisioning case with 1 empty port")
    public void cardProvisioning() {
        Card cardBeforeProvisioning = getCard();

        Assert.assertNotNull(cardBeforeProvisioning);
        Assert.assertEquals(cardBeforeProvisioning.getPorts().size(), 1);
        Assert.assertEquals(accessLineRiRobot.getAccessLines(portEmpty).size(), 0);

        wgAccessProvisioningRobot.startCardProvisioning(portEmpty);

        accessLineRiRobot.checkProvisioningResults(portEmpty);
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
                .body(new DeviceDto().endSz(portEmpty.getEndSz())).executeAs(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));

        Thread.sleep(LATENCY_FOR_DEVICE_PROVISIONING);

        Device deviceAfterProvisioning = getDevice();

        PortProvisioning port = getPortProvisioning(portEmpty.getEndSz(),
                deviceAfterProvisioning.getEquipmentHolders().get(2).getSlotNumber(),
                deviceAfterProvisioning.getEquipmentHolders().get(2).getCard().getPorts().get(0).getPortNumber());
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
