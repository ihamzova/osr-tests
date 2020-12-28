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
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.Device;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.Port;
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

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.Port.PortTypeEnum.PON;
import static io.restassured.RestAssured.given;

@ServiceLog(WG_ACCESS_PROVISIONING_MS)
@ServiceLog(ACCESS_LINE_RESOURCE_INVENTORY_MS)
@ServiceLog(NETWORK_LINE_PROFILE_MANAGEMENT_MS)
@ServiceLog(DECOUPLING_MS)
@ServiceLog(GATEWAY_ROUTE_MS)
public class OltProvisioningAdtran extends BaseTest {

    private AccessLineRiRobot accessLineRiRobot;
    private WgAccessProvisioningRobot wgAccessProvisioningRobot;
    private WgAccessProvisioningClient wgAccessProvisioningClient;
    private AccessLineResourceInventoryClient accessLineResourceInventoryClient;
    private PortProvisioning portEmpty;
    private PortProvisioning portDeprovisioningForDpu;
    private UpiterTestContext context = UpiterTestContext.get();

    @BeforeClass
    public void init() {
        accessLineRiRobot = new AccessLineRiRobot();
        wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
        accessLineResourceInventoryClient = new AccessLineResourceInventoryClient();
        wgAccessProvisioningClient = new WgAccessProvisioningClient();
        portEmpty = context.getData()
                .getPortProvisioningDataProvider()
                .get(PortProvisioningCase.portEmptyAdtran);
        portDeprovisioningForDpu = context.getData()
                .getPortProvisioningDataProvider()
                .get(PortProvisioningCase.portDeprovisioningForDpuAdtran);
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
    @Description("Port Provisioning with 32 WG Lines on SDX 6320")
    public void portProvisioning() {
        List<AccessLineDto> accessLinesBeforeProvisioning = accessLineRiRobot.getAccessLines(portEmpty);
        Assert.assertEquals(accessLinesBeforeProvisioning.size(), 0);

        wgAccessProvisioningRobot.startPortProvisioning(portEmpty);
        accessLineRiRobot.checkProvisioningResults(portEmpty);
    }

    @Test
    @TmsLink("DIGIHUB-30824")
    @Description("Device Provisioning SDX 6320")
    public void deviceProvisioning() {

        Device deviceBeforeProvisioning = getDevice();
        List<Port> ponPorts = deviceBeforeProvisioning.getPorts().stream()
                .filter(ponPort -> ponPort.getPortType().equals(PON))
                .collect(Collectors.toList()); // list of ponPorts

        Assert.assertNotNull(deviceBeforeProvisioning);
        Assert.assertEquals(deviceBeforeProvisioning.getEmsNbiName(), "SDX 6320 16-port Combo OLT");
        Assert.assertEquals(ponPorts.size(), 16);

        wgAccessProvisioningRobot.startDeviceProvisioning(portEmpty);

        checkDevicePostConditions(portEmpty);
    }

    @Test
    @TmsLink("DIGIHUB-36495")
    @Description("Port deprovisioning SDX 6320")
    public void portDeprovisioningTest() {
        checkPreconditions(portDeprovisioningForDpu);
        wgAccessProvisioningRobot.startPortDeprovisioningForDpuAdtran(portDeprovisioningForDpu);

        checkPostConditions(portDeprovisioningForDpu);
    }

    private PortProvisioning getPortProvisioning(String endSz, String portNumber) {
        PortProvisioning port = new PortProvisioning();
        port.setEndSz(endSz);
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
        String response =
                given()
                        .when()
                        .get(deviceUrl.toString().replace("%2F", "/"))
                        .then()
                        .extract()
                        .body()
                        .asString()
                        .replaceFirst("\"lastDiscovery\": \".+\",\n", "");
        return OltResourceInventoryClient.json().deserialize(response, Device.class);
    }

    private void checkPreconditions(PortProvisioning port) {
        accessLineRiRobot.prepareTestDataToDeprovisioning(port);
        accessLineRiRobot.checkDecommissioningPreconditions(port);
    }

    private void checkPostConditions(PortProvisioning port) {
        accessLineRiRobot.checkPortParametersForLines(port);
        accessLineRiRobot.checkPhysicalResourceRefAbsence(port);
        accessLineRiRobot.checkBackHaulIdAbsence(port);
    }

    private void checkDevicePostConditions(PortProvisioning port) {
        List<Port> ponPorts = getDevice()
                .getPorts()
                .stream()
                .filter(ponPort -> ponPort.getPortType().equals(PON))
                .collect(Collectors.toList()); // list of ponPorts

        List<String> portNumbers = ponPorts
                .stream()
                .map(Port::getPortNumber)
                .collect(Collectors.toList()); //list of ponPort numbers

        List<PortProvisioning> portProvisioningList = portNumbers
                .stream()
                .map(portNumber -> getPortProvisioning(port.getEndSz(), portNumber))
                .collect(Collectors.toList()); //list of portProvisioning numbers

        portProvisioningList.forEach(portAfterProvisioning -> accessLineRiRobot.checkProvisioningResults(portAfterProvisioning));
    }
}
