package com.tsystems.tm.acc.ta.team.upiter.ftthprovisioning;

import com.tsystems.tm.acc.data.upiter.models.defaultneprofile.DefaultNeProfileCase;
import com.tsystems.tm.acc.data.upiter.models.defaultnetworklineprofile.DefaultNetworkLineProfileCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.DefaultNeProfile;
import com.tsystems.tm.acc.ta.data.osr.models.DefaultNetworkLineProfile;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.AccessLineDto;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static org.testng.Assert.assertEquals;

@ServiceLog({
        WG_ACCESS_PROVISIONING_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        ACCESS_LINE_MANAGEMENT_MS,
        NETWORK_LINE_PROFILE_MANAGEMENT_MS,
        DECOUPLING_MS,
        GATEWAY_ROUTE_MS
})

@Epic("WG Access Provisioning")
public class OltProvisioning5600SpecialCases extends GigabitTest {

    private AccessLineRiRobot accessLineRiRobot;
    private WgAccessProvisioningRobot wgAccessProvisioningRobot;
    private PortProvisioning portProvisioningPartly;
    private PortProvisioning portProvisioningFully;
    private PortProvisioning portWithInActiveLines;
    private DefaultNeProfile defaultNeProfile;
    private DefaultNetworkLineProfile defaultNetworkLineProfile;
    private UpiterTestContext context = UpiterTestContext.get();

    @BeforeClass
    public void prepareData() throws InterruptedException {
        accessLineRiRobot.clearDatabase();
        Thread.sleep(3000);
        accessLineRiRobot.fillDatabaseForOltCommissioningV2(1, 1);
    }

    @AfterClass
    public void clearData() {
        accessLineRiRobot.clearDatabase();
    }

    @BeforeClass
    public void init() {
        accessLineRiRobot = new AccessLineRiRobot();
        wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
        portProvisioningPartly = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portPartlyOccupied);
        portProvisioningFully = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portFullyOccupied);
        portWithInActiveLines = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portWithInActiveLines);
        defaultNeProfile = context.getData().getDefaultNeProfileDataProvider().get(DefaultNeProfileCase.defaultNeProfile);
        defaultNetworkLineProfile = context.getData().getDefaultNetworkLineProfileDataProvider().get(DefaultNetworkLineProfileCase.defaultNLProfileFtth);
    }

    @Test
    @TmsLink("DIGIHUB-40631")
    @Description("Port provisioning case when port is completely occupied")
    public void portProvisioningFully() throws InterruptedException {
        prepareData();
        List<AccessLineDto> accessLinesBeforeProvisioning = accessLineRiRobot.getAccessLinesByPort(portProvisioningFully);
        assertEquals(accessLinesBeforeProvisioning.size(), portProvisioningFully.getAccessLinesCount().intValue());
        wgAccessProvisioningRobot.startPortProvisioning(portProvisioningFully);
        accessLineRiRobot.checkFtthPortParameters(portProvisioningFully);
        accessLineRiRobot.checkDefaultNeProfiles(portProvisioningFully, defaultNeProfile, portProvisioningFully.getAccessLinesCount());
        accessLineRiRobot.checkDefaultNetworkLineProfiles(portProvisioningFully, defaultNetworkLineProfile, portProvisioningFully.getAccessLinesCount());
        accessLineRiRobot.checkPhysicalResourceRefCountFtth(portProvisioningFully, 1, 1);
    }

    @Test
    @TmsLink("DIGIHUB-32288")
    @Description("Port provisioning case when port is partly occupied")
    public void portProvisioningPartly() {
        List<AccessLineDto> accessLinesBeforeProvisioning = accessLineRiRobot.getAccessLinesByPort(portProvisioningPartly);
        assertEquals(accessLinesBeforeProvisioning.size(), 8);
        wgAccessProvisioningRobot.startPortProvisioning(portProvisioningPartly);
        accessLineRiRobot.checkFtthPortParameters(portProvisioningPartly);
        accessLineRiRobot.checkDefaultNeProfiles(portProvisioningPartly, defaultNeProfile, portProvisioningPartly.getAccessLinesCount());
        accessLineRiRobot.checkDefaultNetworkLineProfiles(portProvisioningPartly, defaultNetworkLineProfile, portProvisioningPartly.getAccessLinesCount());
        accessLineRiRobot.checkPhysicalResourceRefCountFtth(portProvisioningFully, 1, 1);
    }

    @Test
    @TmsLink("DIGIHUB-32026")
    @Description("Port provisioning case when port has Inactive Lines")
    public void portProvisioningWithInactiveLines() {
        wgAccessProvisioningRobot.startPortProvisioning(portWithInActiveLines);
        accessLineRiRobot.checkFtthPortParameters(portWithInActiveLines);
        accessLineRiRobot.checkDefaultNeProfiles(portWithInActiveLines, defaultNeProfile, portWithInActiveLines.getAccessLinesCount());
        accessLineRiRobot.checkDefaultNetworkLineProfiles(portWithInActiveLines, defaultNetworkLineProfile, portWithInActiveLines.getAccessLinesCount());
        accessLineRiRobot.checkPhysicalResourceRefCountFtth(portProvisioningFully, 1, 1);
    }
}