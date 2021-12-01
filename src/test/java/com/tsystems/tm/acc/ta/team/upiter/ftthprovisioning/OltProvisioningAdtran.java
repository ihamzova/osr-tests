package com.tsystems.tm.acc.ta.team.upiter.ftthprovisioning;

import com.tsystems.tm.acc.data.upiter.models.defaultneprofile.DefaultNeProfileCase;
import com.tsystems.tm.acc.data.upiter.models.defaultnetworklineprofile.DefaultNetworkLineProfileCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.WgAccessProvisioningClient;
import com.tsystems.tm.acc.ta.data.osr.models.DefaultNeProfile;
import com.tsystems.tm.acc.ta.data.osr.models.DefaultNetworkLineProfile;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.*;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.Device;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

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
    private PortProvisioning portEmptyAdtran;
    private PortProvisioning portDeprovisioningAdtran;
    private PortProvisioning portDeprovisioningForDpu;
    private DefaultNeProfile defaultNeProfile;
    private DefaultNetworkLineProfile defaultNetworkLineProfile;
    private UpiterTestContext context = UpiterTestContext.get();

    @BeforeClass
    public void init() {
        accessLineRiRobot = new AccessLineRiRobot();
        wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
        accessLineResourceInventoryClient = new AccessLineResourceInventoryClient();
        wgAccessProvisioningClient = new WgAccessProvisioningClient();
        portEmptyAdtran = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.deviceAdtran);
        portDeprovisioningAdtran = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portDeprovisioningAdtran);
        portDeprovisioningForDpu = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portDeprovisioningForDpuAdtran);
        defaultNeProfile = context.getData().getDefaultNeProfileDataProvider().get(DefaultNeProfileCase.defaultNeProfile);
        defaultNetworkLineProfile = context.getData().getDefaultNetworkLineProfileDataProvider().get(DefaultNetworkLineProfileCase.defaultNLProfileFtth);
        accessLineRiRobot.clearDatabase();
    }

    @AfterClass
    public void clearData() {
        accessLineRiRobot.clearDatabase();
    }

    @Test
    @TmsLink("DIGIHUB-95613")
    @Description("Port Provisioning with 32 WG Lines on SDX 6320")
    public void portProvisioning() {
        List<AccessLineDto> accessLinesBeforeProvisioning = accessLineRiRobot.getAccessLinesByPort(portEmptyAdtran);
        Assert.assertEquals(accessLinesBeforeProvisioning.size(), 0);

        wgAccessProvisioningRobot.startPortProvisioning(portEmptyAdtran);
        accessLineRiRobot.checkFtthPortParameters(portEmptyAdtran);
        accessLineRiRobot.checkDefaultNeProfiles(portEmptyAdtran, defaultNeProfile, portEmptyAdtran.getAccessLinesCount());
        accessLineRiRobot.checkDefaultNetworkLineProfiles(portEmptyAdtran, defaultNetworkLineProfile, portEmptyAdtran.getAccessLinesCount());
        accessLineRiRobot.checkPhysicalResourceRefCountFtth(portEmptyAdtran, 1, 1);
    }

    @Test (priority = 2)
    @TmsLink("DIGIHUB-116582")
    @Description("Device Provisioning SDX 6320")
    public void deviceProvisioning() {
        Device deviceBeforeProvisioning = wgAccessProvisioningRobot.getDevice(portEmptyAdtran);
        assertNotNull(deviceBeforeProvisioning);
        assertEquals(deviceBeforeProvisioning.getEmsNbiName(), "SDX 6320 16-port Combo OLT");
        assertEquals(wgAccessProvisioningRobot.getPonPorts(portEmptyAdtran).size(), 16);
        wgAccessProvisioningRobot.startDeviceProvisioning(portEmptyAdtran);
        accessLineRiRobot.checkFtthPortParameters(portEmptyAdtran);
        accessLineRiRobot.checkDefaultNeProfiles(portEmptyAdtran, defaultNeProfile, portEmptyAdtran.getAccessLinesCount());
        accessLineRiRobot.checkDefaultNetworkLineProfiles(portEmptyAdtran, defaultNetworkLineProfile, portEmptyAdtran.getAccessLinesCount());
        accessLineRiRobot.checkPhysicalResourceRefCountFtth(portEmptyAdtran, 1, 1);
    }

    @Test(dependsOnMethods = "portProvisioning", priority = 1)
    @TmsLink("DIGIHUB-116583")
    @Description("Port deprovisioning SDX 6320, deprovisionigForDpu = na (= false)")
    public void portDeprovisioningTest() {
        accessLineRiRobot.checkDecommissioningPreconditions(portDeprovisioningAdtran);
        wgAccessProvisioningRobot.startPortDeprovisioning(portDeprovisioningAdtran,true);
        accessLineRiRobot.checkFtthPortParameters(portDeprovisioningAdtran);
        accessLineRiRobot.checkPhysicalResourceRefCountFtth(portDeprovisioningAdtran, 0, 1);
        accessLineRiRobot.clearDatabase();
    }

    @Test(dependsOnMethods = "deviceProvisioning", priority = 3)
    @TmsLink("DIGIHUB-116584")
    @Description("Port deprovisioning SDX 6320, deprovisionigForDpu = true")
    public void portDeprovisioningForDpuTrueTest() {
        accessLineRiRobot.checkDecommissioningPreconditions(portDeprovisioningForDpu);
        wgAccessProvisioningRobot.startPortDeprovisioningForDpu(portDeprovisioningForDpu, true);
        accessLineRiRobot.checkFtthPortParameters(portDeprovisioningForDpu);
        accessLineRiRobot.checkPhysicalResourceRefCountFtth(portDeprovisioningForDpu, 1, 1);

        List<HomeIdDto> homeIds = accessLineRiRobot.getHomeIdPool(portDeprovisioningForDpu);
        List<LineIdDto> lineIds = accessLineRiRobot.getLineIdPool(portDeprovisioningForDpu);
        long countHomeIDsFree = homeIds.stream().filter(HomeId -> HomeId.getStatus().getValue().equals(HomeIdLogicalStatus.FREE.getValue())).count();
        long countLineIDsFree = lineIds.stream().filter(LineId -> LineId.getStatus().getValue().equals(LineIdStatus.FREE.getValue())).count();
        assertEquals(accessLineRiRobot.getBackHaulId(portDeprovisioningForDpu).get(0).getStatus(), BackhaulStatus.CONFIGURED);
        assertEquals(countHomeIDsFree, portDeprovisioningForDpu.getHomeIdPool().intValue());
        assertEquals(countLineIDsFree, portDeprovisioningForDpu.getLineIdPool().intValue());
    }
}
