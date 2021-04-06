package com.tsystems.tm.acc.ta.team.upiter.deprovisioning;

import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;

@ServiceLog({
        WG_ACCESS_PROVISIONING_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        ACCESS_LINE_MANAGEMENT_MS,
        NETWORK_LINE_PROFILE_MANAGEMENT_MS,
        DECOUPLING_MS,
        GATEWAY_ROUTE_MS
})
public class DeprovisioningTest extends GigabitTest {

    private AccessLineRiRobot accessLineRiRobot;
    private WgAccessProvisioningRobot wgAccessProvisioningRobot;
    private PortProvisioning portDepr;
    private PortProvisioning cardDepr;
    private PortProvisioning deviceDepr;
    private UpiterTestContext context = UpiterTestContext.get();

    @BeforeClass
    public void init() {
        accessLineRiRobot = new AccessLineRiRobot();
        wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
        portDepr = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portDeprovisioning);
        cardDepr = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.cardDeprovisioning);
        deviceDepr = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.deviceDepovisioning);
    }

    @BeforeMethod
    public void prepareData() {
        accessLineRiRobot.clearDatabase();
        accessLineRiRobot.fillDatabaseForOltCommissioning();
    }

    @AfterMethod
    public void clearData() {
        accessLineRiRobot.clearDatabase();
    }

    @Test
    @TmsLink("DIGIHUB-36495")
    @Description("Port deprovisioning case")
    public void portDeprovisioningTest() {
        checkPreconditions(portDepr);
        wgAccessProvisioningRobot.startPortDeprovisioning(portDepr);

        checkPostConditions(portDepr);
    }

    @Test
    @TmsLink("DIGIHUB-36495")
    @Description("Card deprovisioning case")
    public void cardDeprovisioningTest() {
        checkPreconditions(cardDepr);
        wgAccessProvisioningRobot.startCardDeprovisioning(cardDepr);

        checkPostConditions(cardDepr);
    }

    @Test
    @TmsLink("DIGIHUB-36495")
    @Description("Device deprovisioning case")
    public void deviceDeprovisioningTest() {
        checkPreconditions(deviceDepr);
        wgAccessProvisioningRobot.startDeviceDeprovisioning(deviceDepr);

        checkPostConditions(deviceDepr);
    }

    private void checkPreconditions(PortProvisioning port) {
        accessLineRiRobot.prepareTestDataToDeprovisioning(port);
        accessLineRiRobot.checkDecommissioningPreconditions(port);
    }

    private void checkPostConditions(PortProvisioning port) {
        accessLineRiRobot.checkPortParametersForLines(port);
        accessLineRiRobot.checkPhysicalResourceRefAndBackhaulIdAbsence(port);
    }
}
