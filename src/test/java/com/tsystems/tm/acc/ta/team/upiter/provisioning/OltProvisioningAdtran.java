package com.tsystems.tm.acc.ta.team.upiter.provisioning;

import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.WgAccessProvisioningClient;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_12_0.client.model.AccessLineDto;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.Device;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.Port;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.Port.PortTypeEnum.PON;

@ServiceLog({
        WG_ACCESS_PROVISIONING_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        ACCESS_LINE_MANAGEMENT_MS,
        NETWORK_LINE_PROFILE_MANAGEMENT_MS,
        DECOUPLING_MS,
        GATEWAY_ROUTE_MS
})
public class OltProvisioningAdtran extends GigabitTest {

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
        List<AccessLineDto> accessLinesBeforeProvisioning = accessLineRiRobot.getAccessLinesByPort(portEmpty);
        Assert.assertEquals(accessLinesBeforeProvisioning.size(), 0);

        wgAccessProvisioningRobot.startPortProvisioning(portEmpty);
        accessLineRiRobot.checkProvisioningResults(portEmpty);
    }

    @Test
    @TmsLink("DIGIHUB-30824")
    @Description("Device Provisioning SDX 6320")
    public void deviceProvisioning() {
        Device deviceBeforeProvisioning = wgAccessProvisioningRobot.getDevice(portEmpty);
        Assert.assertNotNull(deviceBeforeProvisioning);
        Assert.assertEquals(deviceBeforeProvisioning.getEmsNbiName(), "SDX 6320 16-port Combo OLT");
        Assert.assertEquals(getPonPorts().size(), 1);
        wgAccessProvisioningRobot.startDeviceProvisioning(portEmpty);
        accessLineRiRobot.checkProvisioningResults(portEmpty);
        //checkDevicePostConditions(portEmpty);
    }

    @Test
    @TmsLink("DIGIHUB-36495")
    @Description("Port deprovisioning SDX 6320")
    public void portDeprovisioningTest() {
        checkPreconditions(portDeprovisioningForDpu);
        wgAccessProvisioningRobot.startPortDeprovisioningForDpuAdtran(portDeprovisioningForDpu);

        checkPostConditions(portDeprovisioningForDpu);
    }

    private PortProvisioning getPortProvisioning(PortProvisioning portProvisioning, String portNumber) {
        PortProvisioning port = new PortProvisioning();
        port.setEndSz(portProvisioning.getEndSz());
        port.setPortNumber(portNumber);
        port.setLineIdPool(portProvisioning.getLineIdPool());
        port.setHomeIdPool(portProvisioning.getHomeIdPool());
        port.setDefaultNEProfilesActive(portProvisioning.getDefaultNEProfilesActive());
        port.setDefaultNetworkLineProfilesActive(portProvisioning.getDefaultNetworkLineProfilesActive());
        port.setAccessLinesWG(portProvisioning.getAccessLinesWG());
        return port;
    }

    private List<Port> getPonPorts() {
        return wgAccessProvisioningRobot.getDevice(portEmpty).getPorts().stream()
                .filter(ponPort -> ponPort.getPortType().getValue().equals(PON.toString()))
                .collect(Collectors.toList());
    }

    private void checkPreconditions(PortProvisioning port) {
        accessLineRiRobot.prepareTestDataToDeprovisioning(port);
        accessLineRiRobot.checkDecommissioningPreconditions(port);
    }

    private void checkPostConditions(PortProvisioning port) {
        accessLineRiRobot.checkPortParametersForLines(port);
        accessLineRiRobot.getPhysicalResourceRef(port);
        accessLineRiRobot.getBackHaulId(port);
    }

    private void checkDevicePostConditions(PortProvisioning port) {
        List<String> portNumbers = getPonPorts()
                .stream()
                .map(Port::getPortNumber)
                .collect(Collectors.toList()); //list of ponPort numbers

        List<PortProvisioning> portProvisioningList = portNumbers
                .stream()
                .map(portNumber -> getPortProvisioning(port, portNumber))
                .collect(Collectors.toList()); //list of portProvisioning numbers

        portProvisioningList.forEach(portAfterProvisioning -> accessLineRiRobot.checkProvisioningResults(portAfterProvisioning));
    }
}
